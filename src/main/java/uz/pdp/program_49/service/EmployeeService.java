package uz.pdp.program_49.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.program_49.entity.Employee;
import uz.pdp.program_49.entity.Role;
import uz.pdp.program_49.entity.enums.RoleName;
import uz.pdp.program_49.payload.EmployeeDto;
import uz.pdp.program_49.payload.LoginDto;
import uz.pdp.program_49.payload.Result;
import uz.pdp.program_49.repository.EmployeeRepository;
import uz.pdp.program_49.repository.RoleRepository;
import uz.pdp.program_49.security.JwtProvider;

import java.util.Optional;
import java.util.UUID;

@Service
public class EmployeeService implements UserDetailsService {

    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    PasswordEncoder passwordEncoder;

    public Result add(EmployeeDto employeeDto) {
        Employee employeeWhichEnteredSystem = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employeeWhichEnteredSystem.getRole().getRoleName();

        if ((roleName.equals(RoleName.ADMIN) && employeeDto.getRoleId().equals(employeeWhichEnteredSystem.getRole().getId()) ||
                roleName.equals(RoleName.DIRECTOR))) {
            Optional<Employee> optionalEmployee = employeeRepository.findByUsernameAndActive(employeeDto.getUsername(),true);
            if (optionalEmployee.isPresent()) {
                return new Result("This username belongs to another employee!", false);
            }
            Optional<Employee> optionalEmployee1 = employeeRepository.findByPhoneNumberAndActive(employeeDto.getPhoneNumber(),true);
            if (optionalEmployee1.isPresent()) {
                return new Result("This phone number belongs to another employee!", false);
            }
            Role role = roleRepository.getOne(employeeDto.getRoleId());
            Employee employee = new Employee();
            employee.setId(UUID.randomUUID());
            employee.setRole(role);
            employee.setFirstName(employeeDto.getFirstName());
            employee.setLastName(employeeDto.getLastName());
            employee.setPhoneNumber(employeeDto.getPhoneNumber());
            employee.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
            employee.setUsername(employeeDto.getUsername());
            employeeRepository.save(employee);
            return new Result("New Employee successfully saved.", true);
        }
        return new Result("You do not have the right to add new employee!", false);
    }

    public Result login(LoginDto loginDto) {
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    ));
            String token = jwtProvider.generateToken(loginDto.getUsername());
            return new Result(token, true);
        } catch (Exception e) {
            return new Result("Lorin or password is wrong!", false);
        }
    }


    public Result get(int page) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Pageable pageable = PageRequest.of(page, 20);
            Page<Employee> page1 = employeeRepository.getByActive(true, pageable);
            return new Result(page1, true);
        }
        return new Result("You do not have the right to see list of employee!", false);
    }

    public Result getById(UUID id) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR) || employee.getId().equals(id)) {
            Optional<Employee> optionalEmployee = employeeRepository.findByIdAndActive(id,true);
            return optionalEmployee.map(value -> new Result(value, true)).orElseGet(() -> new Result("Such employee id not exist!", false));
        }
        return new Result("You do not have the right to see information of another employee!", false);
    }


    public Result edit(UUID id, EmployeeDto employeeDto) {
        Employee employeeWhichEnteredSystem = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employeeWhichEnteredSystem.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR) ||
                (employeeWhichEnteredSystem.getId().equals(id) && employeeWhichEnteredSystem.getRole().getId().equals(employeeDto.getRoleId()))) {
            Optional<Employee> optionalEmployee = employeeRepository.findByIdAndActive(id,true);
            if (!optionalEmployee.isPresent()) {
                return new Result("Such employee id not exist!", false);
            }
            Optional<Employee> optionalEmployee1 = employeeRepository.findByUsernameAndIdNotAndActive(employeeDto.getUsername(), id,true);
            if (optionalEmployee1.isPresent()) {
                return new Result("This username belongs to another employee!", false);
            }
            Optional<Employee> optionalEmployee2 = employeeRepository.findByPhoneNumberAndIdNotAndActive(employeeDto.getPhoneNumber(), id,true);
            if (optionalEmployee2.isPresent()) {
                return new Result("This phone number belongs to another employee!", false);
            }
            Role role = roleRepository.getOne(employeeDto.getRoleId());
            Employee employee = optionalEmployee.get();
            employee.setFirstName(employeeDto.getFirstName());
            employee.setLastName(employeeDto.getLastName());
            employee.setUsername(employeeDto.getUsername());
            employee.setPassword(passwordEncoder.encode(employeeDto.getPassword()));
            employee.setPhoneNumber(employeeDto.getPhoneNumber());
            employee.setRole(role);
            employeeRepository.save(employee);
            return new Result("Given employee successfully edited!", false);
        }
        return new Result("You do not have the right to edit information of another employee!", false);
    }

    public Result delete(UUID id) {
        Employee employee = (Employee) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        RoleName roleName = employee.getRole().getRoleName();

        if (roleName.equals(RoleName.DIRECTOR)) {
            Optional<Employee> optionalEmployee = employeeRepository.findByIdAndActive(id,true);
            if (!optionalEmployee.isPresent()) {
                return new Result("Such employee id not exist!", false);
            }
           Employee employee1 = optionalEmployee.get();
            employee1.setActive(false);
            employeeRepository.save(employee1);
            return new Result("Given employee successfully deleted.", true);
        }
        return new Result("You do not have the right to delete information of employees!", false);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Employee> optionalEmployee = employeeRepository.findByUsernameAndActive(username,true);
        if (optionalEmployee.isPresent()){
            return optionalEmployee.get();
        }
        throw new UsernameNotFoundException(username+" such username not found!");
    }

}
