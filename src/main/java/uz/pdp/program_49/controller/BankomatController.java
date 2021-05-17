package uz.pdp.program_49.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.program_49.payload.BankomatDto;
import uz.pdp.program_49.payload.Result;
import uz.pdp.program_49.service.BankomatService;

import javax.annotation.security.PermitAll;
import java.util.UUID;

@RestController
@RequestMapping("/api/bankomat")
public class BankomatController {

    @Autowired
    BankomatService bankService;

    @PostMapping
    public ResponseEntity<Result> add(@RequestBody BankomatDto bankomatDto) {
        Result result = bankService.add(bankomatDto);
        return ResponseEntity.status(result.isActive() ? 201 : 409).body(result);
    }

    @GetMapping("/all")
    public ResponseEntity<Result> getAll(@RequestParam int page) {
        Result result = bankService.get(page);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result> getById(@PathVariable Integer id) {
        Result result = bankService.getById(id);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @GetMapping("/byBankId/{id}")
    public ResponseEntity<Result> getByBankId(@PathVariable Integer id, @RequestParam int page) {
        Result result = bankService.getByBankId(id, page);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @GetMapping("/byCardTypeId/{id}")
    public ResponseEntity<Result> getByCardTypeId(@PathVariable Integer id, @RequestParam int page) {
        Result result = bankService.getByCardTypeId(id, page);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @GetMapping("/byCardId/{id}")
    ResponseEntity<Result> getByCardId(@PathVariable UUID id, @RequestParam int page) {
        Result result = bankService.getByCardId(id, page);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result> edit(@PathVariable Integer id, @RequestBody BankomatDto bankomatDto) {
        Result result = bankService.edit(id, bankomatDto);
        return ResponseEntity.status(result.isActive() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(result);
    }

    @PutMapping("/withdrawMoney/{id}")
    public ResponseEntity<Result> editWithdrawMoneyByCardTypeId(@PathVariable Integer id, @RequestParam Double maxWithdrawMoney) {
        Result result = bankService.editMaxWithdrawMoneyByCardTypeId(id, maxWithdrawMoney);
        return ResponseEntity.status(result.isActive() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> delete(@PathVariable Integer id) {
        Result result = bankService.delete(id);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

}
