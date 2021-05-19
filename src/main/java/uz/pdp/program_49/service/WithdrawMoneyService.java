package uz.pdp.program_49.service;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.program_49.entity.*;
import uz.pdp.program_49.entity.enums.CardName;
import uz.pdp.program_49.entity.enums.RoleName;
import uz.pdp.program_49.payload.*;
import uz.pdp.program_49.repository.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WithdrawMoneyService {

    @Autowired
    WithdrawMoneyRepository withdrawMoneyRepository;
    @Autowired
    KupyuraInBankomatRepository kupyuraInBankomatRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    KupyuraInBankomatService kupyuraInBankomatService;
    @Autowired
    KupyuraRepository kupyuraRepository;
    @Autowired
    CardService cardService;
    @Autowired
    BankomatRepository bankomatRepository;
    @Autowired
    JavaMailSender javaMailSender;


    public Result withdrawMoneyFromBankomat(WithdrawMoneyDto withdrawMoneyDto) {
        Card card = (Card) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Bankomat bankomat = bankomatRepository.getOne(withdrawMoneyDto.getBankomatId());

        if (!card.getCardType().equals(bankomat.getCardType())) {
            return new Result("You must enter " + bankomat.getCardType().getCardName() + " card type!", false);
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        Timestamp now = Timestamp.valueOf(localDateTime);
        boolean checkExpiryDate = now.before(card.getExpiryDate());
        if (!checkExpiryDate) {
            return new Result("Your card has expired!", false);
        }

        List<KupyuraInBankomat> kupyuraInBankomatList = kupyuraInBankomatRepository.getByBankomatIdAndActive(bankomat.getId(),true);

        if (withdrawMoneyDto.getCountMoney() > bankomat.getMaxWithdrawMoney()) {
            return new Result("It is not possible to withdraw more than $ 100 or 1 mln in one attempt from an ATM!", false);
        }

        double commissionBalance;
        if (card.getBank().equals(bankomat.getBank())) {
            commissionBalance = withdrawMoneyDto.getCountMoney() * 0.005;
        } else {
            commissionBalance = withdrawMoneyDto.getCountMoney() * 0.01;
        }

        if ((withdrawMoneyDto.getCountMoney() + commissionBalance) > card.getBalance()) {
            return new Result("There is not enough money on your card!", false);
        }
        if (withdrawMoneyDto.getCountMoney() > bankomat.getBalance()) {
            return new Result("The amount of money you deposit is not enough at the ATM, enter the amount of money less!", false);
        }

        List<Integer> kupyuraList = new ArrayList<>();
        for (KupyuraInBankomat kupyuraInBankomat : kupyuraInBankomatList) {
            kupyuraList.add(kupyuraInBankomat.getKupyura().getKupyuraValue().getKupyura());
        }
        kupyuraList.sort((o1, o2) -> o2 - o1);
        List<Integer> countOfMoneyList = new ArrayList<>();

        for (int i = 0; i < kupyuraList.size(); i++) {
            for (KupyuraInBankomat kupyuraInBankomat : kupyuraInBankomatList) {
                if (kupyuraList.get(i).equals(kupyuraInBankomat.getKupyura().getKupyuraValue().getKupyura())) {
                    countOfMoneyList.add(kupyuraInBankomat.getCount());
                }
            }
        }

        int money = withdrawMoneyDto.getCountMoney();
        List<Integer> whichKupyura = new ArrayList<>();
        List<Integer> howMuchKupyura = new ArrayList<>();
        List<Integer> newWhichKupyura = new ArrayList<>();
        List<Integer> newHowMuchKupyura = new ArrayList<>();
        int j = 0;
        for (int i = 0; i < kupyuraList.size(); i++) {
            if (money / kupyuraList.get(i) >= 1) {
                howMuchKupyura.add((money - money % kupyuraList.get(i)) / kupyuraList.get(i));
                whichKupyura.add(kupyuraList.get(i));
                money = money - howMuchKupyura.get(j) * whichKupyura.get(j);
                j++;
            }
        }
        if (money != 0) {
            return new Result("The amount of money you deposit cannot be expressed " +
                    " in terms of available banknotes", false);
        }

        Result result = checkThatAmountOfMoneyIsSufficient(whichKupyura, howMuchKupyura, countOfMoneyList, kupyuraList, money, newWhichKupyura, newHowMuchKupyura);
        try {
            List<List<Integer>> newList = (List<List<Integer>>) result.getObject();
            newWhichKupyura = newList.get(0);
            newHowMuchKupyura = newList.get(1);

            card.setBalance(card.getBalance() - (withdrawMoneyDto.getCountMoney() + commissionBalance));
            cardRepository.save(card);

            for (int l = 0; l < newWhichKupyura.size(); l++) {
                for (KupyuraInBankomat kupyuraInBankomat : kupyuraInBankomatList) {
                    if (newWhichKupyura.get(l).equals(kupyuraInBankomat.getKupyura().getKupyuraValue().getKupyura())) {
                        kupyuraInBankomat.setCount(kupyuraInBankomat.getCount() - newHowMuchKupyura.get(l));
                        kupyuraInBankomatRepository.save(kupyuraInBankomat);
                    }
                }
            }
            kupyuraInBankomatService.calculationBalance(bankomat.getId());

            Card cardWhichBelongsToBankomat = bankomat.getCard();
            cardWhichBelongsToBankomat.setBalance(cardWhichBelongsToBankomat.getBalance() + withdrawMoneyDto.getCountMoney() + commissionBalance);
            cardRepository.save(cardWhichBelongsToBankomat);

            if (bankomat.getCardType().getCardName().equals(CardName.VISA) && bankomat.getBalance() < 2000.0) {
                // emailga xabar boradi
//                sendEmail("quvondikovj6@gmail.com", bankomat.getBalance(), bankomat.getId(), "sum");

            }
            if ((bankomat.getCardType().getCardName().equals(CardName.HUMO) || bankomat.getCardType().getCardName().equals(CardName.UZCARD)) &&
                    bankomat.getBalance() < 20000000.0) {
                // emailga xabar boradi
//                sendEmail("quvondikovj6@gmail.com", bankomat.getBalance(), bankomat.getId(), "dollar");

            }
            WithdrawMoney withdrawMoney = new WithdrawMoney(false);
            withdrawMoney.setWithdrawMoney(withdrawMoneyDto.getCountMoney());
            withdrawMoney.setBankomat(bankomat);
withdrawMoney.setCard(card);
withdrawMoneyRepository.save(withdrawMoney);

            return new Result("The exchange process was successful.", true);

        } catch (Exception e) {
            String text = (String) result.getObject();
            return result;
        }

    }


    public Result checkThatAmountOfMoneyIsSufficient(List<Integer> whichKupyura, List<Integer> howMuchKupyura,
                                                     List<Integer> countOfMoneyList, List<Integer> kupyuraList, Integer money,
                                                     List<Integer> newWhichKupyura, List<Integer> newHowMuchKupyura) {
        for (int i = 0; i < whichKupyura.size(); i++) {
            for (int k = 0; k < kupyuraList.size(); k++) {
                if (whichKupyura.get(i).equals(kupyuraList.get(k)) && countOfMoneyList.get(k) < howMuchKupyura.get(i)) {
                    if (k == kupyuraList.size() - 1) {
                        return new Result("Banknotes are not enough for the amount of money you " +
                                " have deposited, so enter a larger amount of money!", false);
                    }
                    if (countOfMoneyList.get(k) > 0) {
                        money = money - countOfMoneyList.get(k) * kupyuraList.get(k);
                        newWhichKupyura.add(kupyuraList.get(k));
                        newHowMuchKupyura.add(countOfMoneyList.get(k));
                    }
                    whichKupyura = new ArrayList<>();
                    howMuchKupyura = new ArrayList<>();

                    int s = 0;
                    for (Integer kupyura : kupyuraList) {
                        if (kupyura < kupyuraList.get(k) && money / kupyura > 1) {
                            howMuchKupyura.add((money - money % kupyura) / kupyura);
                            whichKupyura.add(kupyura);
                            money = money - howMuchKupyura.get(s) * whichKupyura.get(s);
                            s++;
                        }
                    }
                    checkThatAmountOfMoneyIsSufficient(whichKupyura, howMuchKupyura, countOfMoneyList,
                            kupyuraList, money, newWhichKupyura, newHowMuchKupyura);

                } else if (whichKupyura.get(i).equals(kupyuraList.get(k)) && countOfMoneyList.get(k) >= howMuchKupyura.get(i)) {
                    newWhichKupyura.add(whichKupyura.get(i));
                    newHowMuchKupyura.add(howMuchKupyura.get(i));
                    money = money - kupyuraList.get(k) * countOfMoneyList.get(k);
                }
            }
        }
        List<List<Integer>> newList = new ArrayList<>();
        newList.add(newWhichKupyura);
        newList.add(newHowMuchKupyura);
        return new Result(newList, true);
    }

    public void sendEmail(String email, Double bankomatBalance, Integer bankomatId, String currencyType){
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("noreply@gmail.com");
            mailMessage.setTo(email);
            mailMessage.setSubject("Bankomatni to'ldirish kerak!");
            String query = "Id si "+bankomatId+" ga teng bo'lgan bankomatda "+bankomatBalance+" "+
                    currencyType+" qoldi. Bankomatni to'ldirishingiz lozim!";
        mailMessage.setText(query);
        javaMailSender.send(mailMessage);
        }catch (Exception e){
          e.printStackTrace();
        }
    }




    public Result depositMoneyAtAnATM(DepositMoneyDto depositMoneyDto) {
        Card card = (Card) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Bankomat bankomat = bankomatRepository.getOne(depositMoneyDto.getBankomatId());

        if (!card.getCardType().equals(bankomat.getCardType())) {
            return new Result("You must enter " + bankomat.getCardType().getCardName() + " card type!", false);
        }

        LocalDateTime localDateTime = LocalDateTime.now();
        Timestamp now = Timestamp.valueOf(localDateTime);
        boolean checkExpiryDate = now.before(card.getExpiryDate());
        if (!checkExpiryDate) {
            return new Result("Your card has expired!", false);
        }
        List<KupyuraInBankomat> kupyuraInBankomatList = kupyuraInBankomatRepository.getByBankomatIdAndActive(bankomat.getId(),true);

        List<Kupyura> kupyuraList = new ArrayList<>();
List<Integer> kupyuraId = depositMoneyDto.getKupyuraId();
for (Integer kupyura  : kupyuraId) {
    Optional<Kupyura> optionalKupyura = kupyuraRepository.findById(kupyura);
    if (optionalKupyura.isPresent()){
        kupyuraList.add(optionalKupyura.get());
    }
}

        List<Integer> howMuchKupyura = depositMoneyDto.getHowMuchKupyura();



        int countMoney = 0;
        for (int i = 0; i < kupyuraId.size(); i++) {
            countMoney = countMoney + kupyuraList.get(i).getKupyuraValue().getKupyura() * howMuchKupyura.get(i);
        }

        if (countMoney > bankomat.getMaxWithdrawMoney()) {
            return new Result("It is not possible to deposit more than $ 100 or 1 mln in one attempt to an ATM!", false);
        }

        double commissionBalance;
        if (card.getBank().equals(bankomat.getBank())) {
            commissionBalance = countMoney * 0.005;
        } else {
            commissionBalance = countMoney * 0.01;
        }

        Card cardWhichBelongsToBankomat = bankomat.getCard();
        if ((countMoney - commissionBalance) > cardWhichBelongsToBankomat.getBalance()) {
            return new Result("The ATM does not have the amount of money you have entered, enter a smaller amount!", false);
        }

        for (int t = 0; t < kupyuraId.size(); t++) {
            for (KupyuraInBankomat kupyuraInBankomat : kupyuraInBankomatList) {
                if (kupyuraList.get(t).getKupyuraValue().getKupyura().equals(kupyuraInBankomat.getKupyura().getKupyuraValue().getKupyura())) {
                    kupyuraInBankomat.setCount(kupyuraInBankomat.getCount() + howMuchKupyura.get(t));
                    kupyuraInBankomatRepository.save(kupyuraInBankomat);
                }
            }
        }

        kupyuraInBankomatService.calculationBalance(bankomat.getId());

        cardWhichBelongsToBankomat.setBalance(cardWhichBelongsToBankomat.getBalance() - (countMoney - commissionBalance));
        cardRepository.save(cardWhichBelongsToBankomat);

        card.setBalance(card.getBalance() + (countMoney - commissionBalance));
        cardRepository.save(card);

        WithdrawMoney withdrawMoney = new WithdrawMoney(true);
        withdrawMoney.setWithdrawMoney(countMoney);
        withdrawMoney.setBankomat(bankomat);
        withdrawMoney.setCard(card);
        withdrawMoneyRepository.save(withdrawMoney);

        return new Result("The exchange process was successful.",true);
    }

public Result getWithdrawMoneyByBankomat(Integer bankomatId, SeeWithdrawMoneyDto seeWithdrawMoneyDto){
Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
RoleName roleName = employee.getRole().getRoleName();

if (roleName.equals(RoleName.DIRECTOR)){
    LocalDate localDate = LocalDate.parse(seeWithdrawMoneyDto.getDate());
    LocalTime localTime1 = LocalTime.of(0,0);
    LocalTime localTime2 = LocalTime.of(23,59);
    LocalDateTime localDateTime1 = LocalDateTime.of(localDate, localTime1);
    LocalDateTime localDateTime2 = LocalDateTime.of(localDate, localTime2);
    Timestamp timestamp1 = Timestamp.valueOf(localDateTime1);
    Timestamp timestamp2 = Timestamp.valueOf(localDateTime2);

    boolean existsByDate = withdrawMoneyRepository.existsByDateAndBankomatId(timestamp1,timestamp2, false, bankomatId);
    if (!existsByDate){
        return new Result(seeWithdrawMoneyDto.getDate()+" in this day does not any withdraw money from bankomat!",false);
    }
    List<WithdrawMoney> withdrawMoneyList = withdrawMoneyRepository.getByWithdrawMoney(timestamp1,timestamp2,false,bankomatId);
    return new Result(withdrawMoneyList,true);
}
return new Result("You do not have the right to see list of withdrawing money!",false);
}


    public Result getDepositMoneyByBankomat(Integer bankomatId, SeeWithdrawMoneyDto seeWithdrawMoneyDto){
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)){
            LocalDate localDate = LocalDate.parse(seeWithdrawMoneyDto.getDate());
            LocalTime localTime1 = LocalTime.of(0,0);
            LocalTime localTime2 = LocalTime.of(23,59);
            LocalDateTime localDateTime1 = LocalDateTime.of(localDate, localTime1);
            LocalDateTime localDateTime2 = LocalDateTime.of(localDate, localTime2);
            Timestamp timestamp1 = Timestamp.valueOf(localDateTime1);
            Timestamp timestamp2 = Timestamp.valueOf(localDateTime2);

            boolean existsByDate = withdrawMoneyRepository.existsByDateAndBankomatId(timestamp1,timestamp2, true, bankomatId);
            if (!existsByDate){
                return new Result(seeWithdrawMoneyDto.getDate()+" in this day does not any withdraw money from bankomat!",false);
            }
            List<WithdrawMoney> withdrawMoneyList = withdrawMoneyRepository.getByDepositMoney(timestamp1,timestamp2,true,bankomatId);
            return new Result(withdrawMoneyList,true);
        }
        return new Result("You do not have the right to see list of withdrawing money!",false);
    }

}
