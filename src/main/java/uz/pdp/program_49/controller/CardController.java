package uz.pdp.program_49.controller;

import org.aspectj.lang.annotation.DeclareError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.program_49.payload.CardDto;
import uz.pdp.program_49.payload.Result;
import uz.pdp.program_49.service.CardService;

import java.util.UUID;

@RestController
@RequestMapping("/api/card")
public class CardController {

    @Autowired
    CardService cardService;

    @PostMapping
    public ResponseEntity<Result> add(@RequestBody CardDto cardDto) {
        Result result = cardService.add(cardDto);
        return ResponseEntity.status(result.isActive() ? 201 : 409).body(result);
    }

    @GetMapping("/all")
    public ResponseEntity<Result> getAll(@RequestParam int page) {
        Result result = cardService.get(page);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result> getById(@PathVariable UUID id) {
        Result result = cardService.getById(id);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @GetMapping("/byBankId/{id}")
    private ResponseEntity<Result> getByBankId(@PathVariable Integer id, @RequestParam int page) {
        Result result = cardService.getByBankId(id, page);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @GetMapping("/byClientId/{id}")
    public ResponseEntity<Result> getByClientId(@PathVariable UUID id) {
        Result result = cardService.getByClientId(id);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @GetMapping("/byCardTypeId/{id}")
    public ResponseEntity<Result> getByCArdTypeId(@PathVariable Integer id, @RequestParam int page) {
        Result result = cardService.getByCardTypeId(id, page);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result> edit(@PathVariable UUID id, @RequestBody CardDto cardDto) {
        Result result = cardService.edit(id, cardDto);
        return ResponseEntity.status(result.isActive() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(result);
    }

    @PutMapping("/expiryDate/{id}")
    public ResponseEntity<Result> editExpiryDate(@PathVariable UUID id) {
        Result result = cardService.activationExpiryDate(id);
        return ResponseEntity.status(result.isActive() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> delete(@PathVariable UUID id) {
        Result result = cardService.delete(id);
        return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
    }
}
