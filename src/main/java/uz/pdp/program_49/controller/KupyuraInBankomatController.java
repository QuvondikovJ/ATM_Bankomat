package uz.pdp.program_49.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import uz.pdp.program_49.payload.KupyuraInBankomatDto;
import uz.pdp.program_49.payload.Result;
import uz.pdp.program_49.service.KupyuraInBankomatService;

@RestController
@RequestMapping("/api/kupyuraInBankomat")
public class KupyuraInBankomatController {

    @Autowired
    KupyuraInBankomatService kupyuraInBankomatService;

    @PostMapping
    public ResponseEntity<Result> add(@RequestBody KupyuraInBankomatDto kupyuraInBankomatDto) {
        Result result = kupyuraInBankomatService.add(kupyuraInBankomatDto);
        return ResponseEntity.status(result.isActive() ? 201 : 409).body(result);
    }

    @GetMapping("/all")
    public ResponseEntity<Result> getAll(@RequestParam int page) {
        Result result = kupyuraInBankomatService.get(page);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }


    @GetMapping("/byBankomatId/{id}")
    public ResponseEntity<Result> getByBankomatId(@PathVariable Integer id) {
        Result result = kupyuraInBankomatService.getByBankomatId(id);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Result> edit(@PathVariable Integer id, @RequestBody KupyuraInBankomatDto kupyuraInBankomatDto) {
        Result result = kupyuraInBankomatService.edit(id, kupyuraInBankomatDto);
        return ResponseEntity.status(result.isActive() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteById(@PathVariable Integer id) {
        Result result = kupyuraInBankomatService.delete(id);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @DeleteMapping("/byBankomatId/{id}")
    public ResponseEntity<Result> deleteByBankomatId(@PathVariable Integer id) {
        Result result = kupyuraInBankomatService.deleteByBankomatId(id);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }


}
