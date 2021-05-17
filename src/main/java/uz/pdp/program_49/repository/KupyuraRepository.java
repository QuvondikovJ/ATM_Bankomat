package uz.pdp.program_49.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uz.pdp.program_49.entity.Kupyura;
import uz.pdp.program_49.projection.KupyuraProjection;

@RepositoryRestResource(path = "kupyura", excerptProjection = KupyuraProjection.class)
public interface KupyuraRepository  extends JpaRepository<Kupyura, Integer > {
    Kupyura getByKupyura(Integer kupyura);
}
