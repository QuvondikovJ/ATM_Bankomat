package uz.pdp.program_49.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.program_49.entity.*;
import uz.pdp.program_49.entity.enums.CardName;
import uz.pdp.program_49.entity.enums.RoleName;
import uz.pdp.program_49.payload.BankomatDto;
import uz.pdp.program_49.payload.Result;
import uz.pdp.program_49.repository.BankRepository;
import uz.pdp.program_49.repository.BankomatRepository;
import uz.pdp.program_49.repository.CardRepository;
import uz.pdp.program_49.repository.CardTypeRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class BankomatService {

    @Autowired
    BankRepository bankRepository;
    @Autowired
    BankomatRepository bankomatRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    CardTypeRepository cardTypeRepository;

    public Result add(BankomatDto bankomatDto) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR) || roleName.equals(RoleName.ACCOUNTING_MANAGER)) {
            Bank bank = bankRepository.getOne(bankomatDto.getBankId());
            CardType cardType = cardTypeRepository.getOne(bankomatDto.getCardTypeId());
            UUID cardId = UUID.fromString(bankomatDto.getCardId());
            Card card = cardRepository.getOne(cardId);

            boolean existsByStreetAndCardTypeAndBankomat = bankomatRepository.existsByStreetAndCardTypeAndBank(bankomatDto.getStreet(), cardType, bank);
            if (existsByStreetAndCardTypeAndBankomat) {
                return new Result("This street already has such bankomat of this bank!", false);
            }
            Bankomat bankomat = new Bankomat();
            if (cardType.getCardName().equals(CardName.VISA)) {
                bankomat.setMaxWithdrawMoney(100.0);
            } else {
                bankomat.setMaxWithdrawMoney(1000000.0);
            }
            bankomat.setBank(bank);
            bankomat.setCard(card);
            bankomat.setCardType(cardType);
            bankomat.setCity(bankomatDto.getCity());
            bankomat.setDistrict(bankomatDto.getDistrict());
            bankomat.setStreet(bankomatDto.getStreet());
            bankomatRepository.save(bankomat);
            return new Result("New bankomat successfully saved.", true);
        }
        return new Result("You do not have the right to add new bankomat!", false);
    }

    public Result get(int page) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Pageable pageable = PageRequest.of(page, 20);
            Page<Bankomat> page1 = bankomatRepository.findAll(pageable);
            return new Result(page1, true);
        }
        return new Result("You do not have the right to see list of bankomats!", false);
    }

    public Result getById(Integer id) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR) || roleName.equals(RoleName.ACCOUNTING_MANAGER)) {
            Optional<Bankomat> optionalBankomat = bankomatRepository.findById(id);
            if (!optionalBankomat.isPresent()) {
                return new Result("Such bankomat id not exist!", false);
            }
            return new Result(optionalBankomat.get(), true);
        }
        return new Result("You do not have the right to see information of bankomat!", false);
    }

    public Result getByBankId(Integer id, int page) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Pageable pageable = PageRequest.of(page, 20);
            Page<Bankomat> page1 = bankomatRepository.getByBankId(id, pageable);
            return new Result(page1, true);
        }
        return new Result("You do not have the right too see list of bankomats!", false);
    }

    public Result getByCardId(UUID id, int page) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Pageable pageable = PageRequest.of(page, 20);
            Page<Bankomat> page1 = bankomatRepository.getByCardId(id, pageable);
            return new Result(page1, true);
        }
        return new Result("You do not have the right to see list of bankomats!", false);
    }

    public Result getByCardTypeId(Integer id, int page) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Pageable pageable = PageRequest.of(page, 20);
            Page<Bankomat> page1 = bankomatRepository.getByCardTypeId(id, pageable);
            return new Result(page1, true);
        }
        return new Result("You do not have the right to see list of bankomats!", false);
    }

    public Result edit(Integer id, BankomatDto bankomatDto) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR) || roleName.equals(RoleName.ACCOUNTING_MANAGER)) {
            Optional<Bankomat> optionalBankomat = bankomatRepository.findById(id);
            if (!optionalBankomat.isPresent()) {
                return new Result("Such bankomat id not exist!", false);
            }
            Bankomat bankomat = optionalBankomat.get();

            boolean existsByStreetAndCardTypeAndBank = bankomatRepository.
                    existsByStreetAndCardTypeIdAndBankIdAndIdNot(bankomatDto.getStreet(), bankomatDto.getCardTypeId(), bankomatDto.getBankId(), id);
            if (existsByStreetAndCardTypeAndBank) {
                return new Result("This street already has such bankomat of this bank!", false);
            }
            Bank bank = bankRepository.getOne(bankomatDto.getBankId());
            CardType cardType = cardTypeRepository.getOne(bankomatDto.getCardTypeId());
            UUID cardId = UUID.fromString(bankomatDto.getCardId());
            Card card = cardRepository.getOne(cardId);

            bankomat.setBank(bank);
            bankomat.setCardType(cardType);
            bankomat.setCard(card);
            bankomat.setCity(bankomatDto.getCity());
            bankomat.setDistrict(bankomatDto.getDistrict());
            bankomat.setStreet(bankomatDto.getStreet());
            bankomatRepository.save(bankomat);
            return new Result("Given bankomat successfully edited.", true);
        }
        return new Result("You do not have the right to edit information of bankomat!", false);
    }

    public Result editMaxWithdrawMoneyByCardTypeId(Integer cardTypeId, Double maxWithdrawMoney) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR) || roleName.equals(RoleName.ACCOUNTING_MANAGER)) {

            bankomatRepository.editMaxWithdrawMoneyByCardTypeId(maxWithdrawMoney, cardTypeId);
            return new Result("Given bankomats successfully edited.", true);
        }
        return new Result("You do not have the right to edit information of bankomats!", false);
    }

    public Result delete(Integer id) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Optional<Bankomat> optionalBankomat = bankomatRepository.findById(id);
            if (!optionalBankomat.isPresent()) {
                return new Result("Such bankomat id not exist!", false);
            }
            bankomatRepository.deleteById(id);
            return new Result("Given bankomat successfully deleted.", true);
        }
        return new Result("You do not have the right to delete information of bankomats!", false);
    }

}

