package uz.pdp.program_49.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uz.pdp.program_49.entity.District;
import uz.pdp.program_49.entity.Region;
import uz.pdp.program_49.projection.DistrictProjection;
import uz.pdp.program_49.projection.RegionProjection;

@RepositoryRestResource(path = "district", excerptProjection = DistrictProjection.class)
public interface DistrictRepository extends JpaRepository<District, Integer> {
}
