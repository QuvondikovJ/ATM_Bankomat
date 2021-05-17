package uz.pdp.program_49.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.program_49.entity.Employee;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByUsername(String username);
    Optional<Employee> findByPhoneNumber(String phoneNumber);
    Optional<Employee> findByUsernameAndIdNot(String username, UUID id);
    Optional<Employee> findByPhoneNumberAndIdNot(String phoneNumber, UUID id);
}
