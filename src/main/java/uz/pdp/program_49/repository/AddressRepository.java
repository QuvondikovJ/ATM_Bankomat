package uz.pdp.program_49.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.program_49.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}
