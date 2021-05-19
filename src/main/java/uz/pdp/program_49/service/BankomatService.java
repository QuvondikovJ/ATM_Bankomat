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
import uz.pdp.program_49.repository.*;

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
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    DistrictRepository districtRepository;

    public Result add(BankomatDto bankomatDto) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR) || roleName.equals(RoleName.ACCOUNTING_MANAGER)) {
            Bank bank = bankRepository.getOne(bankomatDto.getBankId());
            CardType cardType = cardTypeRepository.getOne(bankomatDto.getCardTypeId());
            UUID cardId = UUID.fromString(bankomatDto.getCardId());
            Card card = cardRepository.getOne(cardId);

            boolean existsByStreetAndCardTypeAndBankomat = bankomatRepository.existsByCardTypeAndBankAndAddress_HomeNumber(cardType, bank, bankomatDto.getHomeNumber());
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

            Address address = new Address();
            District district = districtRepository.getOne(bankomatDto.getDistrictId());
            address.setStreet(bankomatDto.getStreet());
            address.setHomeNumber(bankomatDto.getHomeNumber());
            address.setDistrict(district);
            address = addressRepository.save(address);

            bankomat.setAddress(address);
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
            Page<Bankomat> page1 = bankomatRepository.getByActive(true, pageable);
            return new Result(page1, true);
        }
        return new Result("You do not have the right to see list of bankomats!", false);
    }

    public Result getById(Integer id) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR) || roleName.equals(RoleName.ACCOUNTING_MANAGER)) {
           boolean existsByIdAndActive = bankomatRepository.existsByIdAndActive(id, true);
            if (!existsByIdAndActive) {
                return new Result("Such bankomat id not exist!", false);
            }
            Bankomat bankomat = bankomatRepository.getByIdAndActive(id, true);
            return new Result(bankomat, true);
        }
        return new Result("You do not have the right to see information of bankomat!", false);
    }

    public Result getByBankId(Integer id, int page) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Pageable pageable = PageRequest.of(page, 20);
            Page<Bankomat> page1 = bankomatRepository.getByBankIdAndActive(id, true, pageable);
            return new Result(page1, true);
        }
        return new Result("You do not have the right too see list of bankomats!", false);
    }

    public Result getByCardId(UUID id, int page) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Pageable pageable = PageRequest.of(page, 20);
            Page<Bankomat> page1 = bankomatRepository.getByCardIdAndActive(id,true, pageable);
            return new Result(page1, true);
        }
        return new Result("You do not have the right to see list of bankomats!", false);
    }

    public Result getByCardTypeId(Integer id, int page) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Pageable pageable = PageRequest.of(page, 20);
            Page<Bankomat> page1 = bankomatRepository.getByCardTypeIdAndActive(id,true, pageable);
            return new Result(page1, true);
        }
        return new Result("You do not have the right to see list of bankomats!", false);
    }

    public Result edit(Integer id, BankomatDto bankomatDto) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR) || roleName.equals(RoleName.ACCOUNTING_MANAGER)) {
           boolean existsByIdAndActive = bankomatRepository.existsByIdAndActive(id, true);
            if (!existsByIdAndActive) {
                return new Result("Such bankomat id not exist!", false);
            }
            Bankomat bankomat =bankomatRepository.getByIdAndActive(id,true);

            boolean existsByStreetAndCardTypeAndBank = bankomatRepository.
                    existsByCardTypeIdAndBankIdAndIdNotAndAddress_HomeNumber(bankomatDto.getCardTypeId(), bankomatDto.getBankId(), id, bankomatDto.getHomeNumber());
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

            Address address = bankomat.getAddress();
            District district = districtRepository.getOne(bankomatDto.getDistrictId());
            address.setStreet(bankomatDto.getStreet());
            address.setHomeNumber(bankomatDto.getHomeNumber());
            address.setDistrict(district);
            addressRepository.save(address);

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
            boolean existByIdAndActive = bankomatRepository.existsByIdAndActive(id,true);
            if (!existByIdAndActive) {
                return new Result("Such bankomat id not exist!", false);
            }
          Bankomat bankomat = bankomatRepository.getByIdAndActive(id, true);
            bankomat.setActive(false);
            bankomatRepository.save(bankomat);
            return new Result("Given bankomat successfully deleted.", true);
        }
        return new Result("You do not have the right to delete information of bankomats!", false);
    }

}

