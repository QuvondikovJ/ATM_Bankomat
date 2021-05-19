package uz.pdp.program_49.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uz.pdp.program_49.entity.KupyuraName;
import uz.pdp.program_49.projection.KupyuraNameProjection;

@RepositoryRestResource(path = "kupyuraName", excerptProjection = KupyuraNameProjection.class)
public interface KupyuraNameRepository extends JpaRepository<KupyuraName, Integer> {
}
