package uz.pdp.program_49.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uz.pdp.program_49.entity.Bank;
import uz.pdp.program_49.projection.BankProjection;

@RepositoryRestResource(path = "banks", excerptProjection = BankProjection.class)
public interface BankRepository extends JpaRepository<Bank, Integer> {
}
