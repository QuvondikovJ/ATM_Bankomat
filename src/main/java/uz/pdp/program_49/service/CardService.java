package uz.pdp.program_49.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.program_49.entity.*;
import uz.pdp.program_49.entity.enums.CardName;
import uz.pdp.program_49.entity.enums.RoleName;
import uz.pdp.program_49.payload.Result;
import uz.pdp.program_49.payload.CardDto;
import uz.pdp.program_49.repository.BankRepository;
import uz.pdp.program_49.repository.CardRepository;
import uz.pdp.program_49.repository.CardTypeRepository;
import uz.pdp.program_49.repository.ClientRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class CardService implements UserDetailsService {

    @Autowired
    CardRepository cardRepository;
    @Autowired
    BankRepository bankRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    CardTypeRepository cardTypeRepository;
    @Autowired
    PasswordEncoder passwordEncoder;


    public Result add(CardDto cardDto) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR) || roleName.equals(RoleName.ACCOUNTING_MANAGER)) {
            Bank bank = bankRepository.getOne(cardDto.getBankId());
            UUID uuid = UUID.fromString(cardDto.getClientId());
            Client client = clientRepository.getOne(uuid);
            CardType cardType = cardTypeRepository.getOne(cardDto.getCardTypeId());

            Card card = new Card();
            if (cardType.getCardName().equals(CardName.VISA)) {
                int cvvCode = (int) (Math.random() * 1000);
                while (cvvCode < 100) {
                    cvvCode = (int) (Math.random() * 1000);
                }
                card.setCvvCode(cvvCode);
            }

            LocalDateTime localDateTime = LocalDateTime.now();
            LocalDateTime localDateTime1 = localDateTime.plusYears(4);
            Timestamp expiryDate = Timestamp.valueOf(localDateTime1);

            String username = generateUsername(cardType.getId());
            card.setUsername(username);
            card.setBank(bank);
            card.setClient(client);
            card.setExpiryDate(expiryDate);
            card.setPassword(passwordEncoder.encode(cardDto.getPassword()));
            card.setCardType(cardType);
            cardRepository.save(card);
            return new Result("New card successfully saved.", true);
        }
        return new Result("You do not have the right to add new card!", false);
    }

    public String generateUsername(Integer cardTypeId) {
        long usernameCount = (long) (Math.random() * Math.pow(10.0, 16.0));
        if (usernameCount < (long) (Math.pow(10.0, 15.0))) {
            usernameCount = (long) (Math.random() * Math.pow(10.0, 16.0));
        }
        String username = Long.toString(usernameCount);
        boolean existsCardByUsername = cardRepository.existsByUsernameAndCardTypeId(username, cardTypeId);
        if (existsCardByUsername) {
            generateUsername(cardTypeId);
        }
        return username;
    }

    public void replenishBalance(UUID cardId, double countMoney){
        Card card = cardRepository.getOne(cardId);
        card.setBalance(card.getBalance() + countMoney);
        cardRepository.save(card);
    }



    public Result get(int page) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Pageable pageable = PageRequest.of(page, 20);
            Page<Card> page1 = cardRepository.findAll(pageable);
            return new Result(page1, true);
        }
        return new Result("You do not have the right to see list of cards!", false);
    }


    public Result getById(UUID id) {
        RoleName roleName = null;
        Card card = null;
        try {
            Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            roleName = employee.getRole().getRoleName();
            if (roleName.equals(RoleName.DIRECTOR)) {
                Optional<Card> optionalCard = cardRepository.findById(id);
                if (!optionalCard.isPresent()) {
                    return new Result("Such card id not exist1", false);
                }
                return new Result(optionalCard.get(), true);
            }
            return new Result("You do not have the right to see information about card of another person!", false);
        } catch (Exception e) {
            card = (Card) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (card.getId().equals(id)) {
                Optional<Card> optionalCard = cardRepository.findById(id);
                if (!optionalCard.isPresent()) {
                    return new Result("Such card id not exist1", false);
                }
                return new Result(optionalCard.get(), true);
            }
        }

        return new Result("You do not have the right to see information about card of another person!", false);
    }

    public Result getByBankId(Integer id, int page) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Pageable pageable = PageRequest.of(page, 20);
            Page<Card> page1 = cardRepository.getByBankId(id, pageable);
            return new Result(page1, true);
        }
        return new Result("You do not have the right to see list of cards!", false);
    }

    public Result getByClientId(UUID id) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            List<Card> cards = cardRepository.getByClientId(id);
            return new Result(cards, true);
        }
        return new Result("You do not have the right to see cards of another clients!", false);
    }

    public Result getByCardTypeId(Integer id, int page) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Pageable pageable = PageRequest.of(page, 20);
            Page<Card> page1 = cardRepository.getByCardTypeId(id, pageable);
            return new Result(page1, true);
        }
        return new Result("You do not have the right to see cards of another clients!", false);
    }

    public Result edit(UUID id, CardDto cardDto) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR) || roleName.equals(RoleName.ACCOUNTING_MANAGER)) {
            Optional<Card> optionalCard = cardRepository.findById(id);
            if (!optionalCard.isPresent()) {
                return new Result("Such card id not exist1", false);
            }
            Card card = optionalCard.get();
            Bank bank = bankRepository.getOne(cardDto.getBankId());
            UUID uuid = UUID.fromString(cardDto.getClientId());
            Client client = clientRepository.getOne(uuid);
            CardType cardType = cardTypeRepository.getOne(cardDto.getCardTypeId());

            if (!card.getCardType().equals(cardType) && card.getCardType().getCardName().equals(CardName.VISA)) {
                card.setCvvCode(null);
            }
            if (!card.getCardType().equals(cardType) && cardType.getCardName().equals(CardName.VISA)) {
                int cvvCode = (int) (Math.random() * 1000);
                if (cvvCode < 100) {
                    cvvCode = (int) (Math.random() * 1000);
                }
                card.setCvvCode(cvvCode);
            }
            if (!card.getCardType().equals(cardType)) {
                String username = generateUsername(cardType.getId()); // agar card type o'zgarsa uni username i xam o'zgaradi
           card.setUsername(username);
            }

            card.setBank(bank);
            card.setClient(client);
            card.setPassword(passwordEncoder.encode(cardDto.getPassword()));
            card.setCardType(cardType);
            cardRepository.save(card);
            return new Result("Given card successfully edited.", true);
        }
        return new Result("You do not have the right to edit information of cards!", false);
    }


    public Result activationExpiryDate(UUID id) {
        // bu metodga agar murojaat bo'lsa card ni expiryDate iga 4 yil qo'shib beradi
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR) || roleName.equals(RoleName.ACCOUNTING_MANAGER)) {

            Optional<Card> optionalCard = cardRepository.findById(id);
            if (!optionalCard.isPresent()) {
                return new Result("Such card id not exist!", false);
            }

            LocalDateTime localDateTime = LocalDateTime.now();
            localDateTime = localDateTime.plusYears(4);
            Timestamp expiryDate = Timestamp.valueOf(localDateTime);

            Card card = optionalCard.get();
            card.setExpiryDate(expiryDate);
            cardRepository.save(card);
            return new Result("Given card activated.", true);
        }
        return new Result("You do not have the right to edit information of cards!", false);
    }


    public Result delete(UUID id) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Optional<Card> optionalCard = cardRepository.findById(id);
            if (!optionalCard.isPresent()) {
                return new Result("Such card id not exist", false);
            }
            cardRepository.deleteById(id);
            return new Result("Given card successfully deleted!", false);
        }
        return new Result("You do not have the right to delete information of cards!", false);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Card> optionalCard = cardRepository.findByUsername(username);
        if (optionalCard.isPresent()){
            return optionalCard.get();
        }
        throw new UsernameNotFoundException(username+ " such username not found!");
    }
}




