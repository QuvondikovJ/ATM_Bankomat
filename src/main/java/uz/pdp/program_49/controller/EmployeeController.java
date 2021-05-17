package uz.pdp.program_49.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import uz.pdp.program_49.payload.EmployeeDto;
import uz.pdp.program_49.payload.LoginDto;
import uz.pdp.program_49.payload.Result;
import uz.pdp.program_49.service.EmployeeService;

import javax.annotation.security.PermitAll;
import java.util.UUID;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

@PostMapping
    public ResponseEntity<Result> add(@RequestBody EmployeeDto employeeDto){
    Result result = employeeService.add(employeeDto);
    return ResponseEntity.status(result.isActive() ? 201 : 409).body(result);
}

@GetMapping("/login")
public ResponseEntity<Result> logIn(@RequestBody LoginDto loginDto){
    Result result = employeeService.login(loginDto);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}


@GetMapping("/all")
    public ResponseEntity<Result> get(@RequestParam int page){
    Result result = employeeService.get(page);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}

@GetMapping("/{id}")
public ResponseEntity<Result> getById(@PathVariable UUID id){
    Result result = employeeService.getById(id);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}

@PutMapping("/{id}")
    public ResponseEntity<Result> edit(@PathVariable UUID id, @RequestBody EmployeeDto employeeDto){
    Result result = employeeService.edit(id, employeeDto);
    return ResponseEntity.status(result.isActive() ? HttpStatus.ACCEPTED : HttpStatus.CONFLICT).body(result);
}

@DeleteMapping("/{id}")
    public ResponseEntity<Result> delete(@PathVariable UUID id){
    Result result = employeeService.delete(id);
    return ResponseEntity.status(result.isActive() ? 200 : 409).body(result);
}
}
