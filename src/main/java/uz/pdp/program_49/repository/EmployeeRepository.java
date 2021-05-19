package uz.pdp.program_49.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.program_49.entity.Employee;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByUsernameAndActive(String username, boolean active);

    Optional<Employee> findByPhoneNumberAndActive(String phoneNumber, boolean active);
    Optional<Employee> findByUsernameAndIdNotAndActive(String username, UUID id, boolean active);
    Optional<Employee> findByPhoneNumberAndIdNotAndActive(String phoneNumber, UUID id, boolean active);
    Optional<Employee> findByIdAndActive(UUID id, boolean active);
    Page<Employee> getByActive(boolean active, Pageable pageable);
}
