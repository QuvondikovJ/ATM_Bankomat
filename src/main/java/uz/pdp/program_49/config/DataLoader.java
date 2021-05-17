package uz.pdp.program_49.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.pdp.program_49.entity.CardType;
import uz.pdp.program_49.entity.Employee;
import uz.pdp.program_49.entity.Role;
import uz.pdp.program_49.entity.enums.CardName;
import uz.pdp.program_49.entity.enums.RoleName;
import uz.pdp.program_49.repository.CardTypeRepository;
import uz.pdp.program_49.repository.EmployeeRepository;
import uz.pdp.program_49.repository.RoleRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {
@Autowired
    RoleRepository roleRepository;
@Autowired
    PasswordEncoder passwordEncoder;
@Autowired
CardTypeRepository cardTypeRepository;
@Autowired
    EmployeeRepository employeeRepository;

@Value(value = "${spring.datasource.initialization-mode}")
private String initializationMode;


    LocalDateTime localDateTime = LocalDateTime.now();
    Timestamp createdAt = Timestamp.valueOf(localDateTime);
    UUID directorId = UUID.randomUUID();

    @Override
    public void run(String... args) throws Exception {
        if (initializationMode.equals("always")){
            Role  role = new Role(1, RoleName.DIRECTOR);
            Role  role1 = new Role(2, RoleName.ACCOUNTING_MANAGER);
            CardType cardType = new CardType(1, CardName.HUMO);
            CardType cardType1 = new CardType(2, CardName.UZCARD);
            CardType cardType2 = new CardType(3, CardName.VISA);
            Employee employee = new Employee(directorId,"director","directorov",
                    "+998971112233", passwordEncoder.encode("1111"),
                    "d@gmail.com",role,true,true,true,true);

            List<Role> roles = new ArrayList<>();
            roles.add(role);
            roles.add(role1);
roleRepository.saveAll(roles);

List<CardType> cardTypes = new ArrayList<>();
cardTypes.add(cardType);
cardTypes.add(cardType1);
cardTypes.add(cardType2);
cardTypeRepository.saveAll(cardTypes);

employeeRepository.save(employee);
        }
    }


}
