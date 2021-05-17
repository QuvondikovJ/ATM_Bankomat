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
import uz.pdp.program_49.payload.KupyuraInBankomatDto;
import uz.pdp.program_49.payload.Result;
import uz.pdp.program_49.repository.BankomatRepository;
import uz.pdp.program_49.repository.KupyuraInBankomatRepository;
import uz.pdp.program_49.repository.KupyuraRepository;
import uz.pdp.program_49.repository.WithdrawMoneyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class KupyuraInBankomatService {

    @Autowired
    KupyuraInBankomatRepository kupyuraInBankomatRepository;
    @Autowired
    KupyuraRepository kupyuraRepository;
    @Autowired
    BankomatRepository bankomatRepository;
    @Autowired
    WithdrawMoneyRepository withdrawMoneyRepository;

    public Result add(KupyuraInBankomatDto kupyuraInBankomatDto) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR) || roleName.equals(RoleName.ACCOUNTING_MANAGER)) {
            Optional<KupyuraInBankomat> optionalKupyuraInBankomat = kupyuraInBankomatRepository.
                    findByKupyuraIdAndBankomatId(kupyuraInBankomatDto.getKupyuraId(), kupyuraInBankomatDto.getBankomatId());
            if (optionalKupyuraInBankomat.isPresent()) {
                KupyuraInBankomat kupyuraInBankomat = optionalKupyuraInBankomat.get();
                kupyuraInBankomat.setCount(kupyuraInBankomat.getCount() + kupyuraInBankomatDto.getCount());
                kupyuraInBankomatRepository.save(kupyuraInBankomat);

                calculationBalance(kupyuraInBankomatDto.getBankomatId());
                return new Result("Given money count successfully added.", true);
            } else {
                Bankomat bankomat = bankomatRepository.getOne(kupyuraInBankomatDto.getBankomatId());
                Kupyura kupyura = kupyuraRepository.getOne(kupyuraInBankomatDto.getKupyuraId());
                KupyuraInBankomat kupyuraInBankomat = new KupyuraInBankomat();
                kupyuraInBankomat.setBankomat(bankomat);
                kupyuraInBankomat.setKupyura(kupyura);
                kupyuraInBankomat.setCount(kupyuraInBankomatDto.getCount());
                kupyuraInBankomatRepository.save(kupyuraInBankomat);

                calculationBalance(kupyuraInBankomatDto.getBankomatId());
                return new Result("Given money successfully added!", false);
            }
        }
        return new Result("You do not have the right to add kupyura to bankomat!", false);
    }


    void calculationBalance(Integer bankomatId) {
        List<KupyuraInBankomat> kupyuraInBankomats = kupyuraInBankomatRepository.getByBankomatId(bankomatId);
        double balance = 0.0;
        for (KupyuraInBankomat kupyuraInBankomat1 : kupyuraInBankomats) {
            Kupyura kupyura = kupyuraInBankomat1.getKupyura();
            double summa = kupyura.getKupyura() * kupyuraInBankomat1.getCount();
            balance = balance + summa;
        }
        Bankomat bankomat = bankomatRepository.getOne(bankomatId);
        bankomat.setBalance(balance);
        bankomatRepository.save(bankomat);
    }


    public Result get(int page) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Pageable pageable = PageRequest.of(page, 20);
            Page<KupyuraInBankomat> page1 = kupyuraInBankomatRepository.findAll(pageable);
            return new Result(page1, true);
        }
        return new Result("You do not have the right to see list kupyura in bankomats!", false);
    }

    public Result getByBankomatId(Integer bankomatId) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR) || roleName.equals(RoleName.ACCOUNTING_MANAGER)) {
            List<KupyuraInBankomat> kupyuraInBankomatList = kupyuraInBankomatRepository.getByBankomatId(bankomatId);
            return new Result(kupyuraInBankomatList, true);
        }
        return new Result("You do not have the right to see kupyuras in bankomat!", false);
    }

    public Result edit(Integer id, KupyuraInBankomatDto kupyuraInBankomatDto) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR) || roleName.equals(RoleName.ACCOUNTING_MANAGER)) {
            Optional<KupyuraInBankomat> optionalKupyuraInBankomat = kupyuraInBankomatRepository.findById(id);
            if (!optionalKupyuraInBankomat.isPresent()) {
                return new Result("Such kupyura in bankomat id not exist!", false);
            }
            KupyuraInBankomat kupyuraInBankomat = optionalKupyuraInBankomat.get();

            if (kupyuraInBankomat.getBankomat().getId().equals(kupyuraInBankomatDto.getBankomatId()) &&
                    kupyuraInBankomat.getKupyura().getId().equals(kupyuraInBankomatDto.getKupyuraId())) {

                kupyuraInBankomat.setCount(kupyuraInBankomatDto.getCount());
                kupyuraInBankomatRepository.save(kupyuraInBankomat);

                calculationBalance(kupyuraInBankomatDto.getBankomatId());
                return new Result("Given kupyura count successfully edited.", true);
            }
            return new Result("You can only count of kupyura in this bankomat!", false);
        }
        return new Result("You do not have the right to edit count of kupyura in this bank!", false);
    }

    public Result delete(Integer id) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Optional<KupyuraInBankomat> optionalKupyuraInBankomat = kupyuraInBankomatRepository.findById(id);
            if (!optionalKupyuraInBankomat.isPresent()) {
                return new Result("Such kupyura in bankomat id not exist!", false);
            }
            kupyuraInBankomatRepository.deleteById(id);
            return new Result("Given kupyura successfully deleted in this bankomat!", false);
        }
        return new Result("You do not have the right to delete kupyuras in bankomat!", false);
    }

    public Result deleteByBankomatId(Integer bankomatId) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            boolean existsByBankomatId = kupyuraInBankomatRepository.existsByBankomatId(bankomatId);
            if (!existsByBankomatId) {
                return new Result("This bankomat does not any kupyuras!", false);
            }
            kupyuraInBankomatRepository.deleteByBankomatId(bankomatId);
            return new Result("Kupyuras successfully deleted in this bankomat!", false);
        }
        return new Result("You do not have the right to delete kupyuras in bankomat!", false);
    }
}


