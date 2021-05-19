package uz.pdp.program_49.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uz.pdp.program_49.entity.KupyuraName;
import uz.pdp.program_49.entity.KupyuraValue;
import uz.pdp.program_49.projection.KupyuraNameProjection;
import uz.pdp.program_49.projection.KupyuraValueProjection;

@RepositoryRestResource(path = "kupyuraValue", excerptProjection = KupyuraValueProjection.class)
public interface KupyuraValueRepository extends JpaRepository<KupyuraValue, Integer> {
}
