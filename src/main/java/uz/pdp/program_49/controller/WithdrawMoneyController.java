package uz.pdp.program_49.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.program_49.payload.*;
import uz.pdp.program_49.service.WithdrawMoneyService;

@RestController
@RequestMapping("/api/withdrawMoney")
public class WithdrawMoneyController {

    @Autowired
    WithdrawMoneyService withdrawMoneyService;

    @PostMapping("/withdraw")
    public ResponseEntity<Result> withdraw(@RequestBody WithdrawMoneyDto withdrawMoneyDto) {
        Result result = withdrawMoneyService.withdrawMoneyFromBankomat(withdrawMoneyDto);
        return ResponseEntity.status(result.isActive() ? 201 : 409).body(result);
    }

    @PostMapping("/deposit")
    public ResponseEntity<Result> refund(@RequestBody DepositMoneyDto depositMoneyDto) {
        Result result = withdrawMoneyService.depositMoneyAtAnATM(depositMoneyDto);
        return ResponseEntity.status(result.isActive() ? 201 : 409).body(result);
    }

    @GetMapping("/withdraw/{id}")
public ResponseEntity<Result> getWithdrawMoneyByBankomatId(@PathVariable Integer id, @RequestBody SeeWithdrawMoneyDto seeWithdrawMoneyDto){
        Result result = withdrawMoneyService.getWithdrawMoneyByBankomat(id, seeWithdrawMoneyDto);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @GetMapping("/deposit/{id}")
    public ResponseEntity<Result> getByDepositMoneyByBankomatId(@PathVariable Integer id, @RequestBody SeeWithdrawMoneyDto seeWithdrawMoneyDto){
        Result result = withdrawMoneyService.getDepositMoneyByBankomat(id, seeWithdrawMoneyDto);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

}
