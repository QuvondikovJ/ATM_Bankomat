package uz.pdp.program_49.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uz.pdp.program_49.entity.Client;
import uz.pdp.program_49.projection.ClientProjection;

import java.util.UUID;
@RepositoryRestResource(path = "client", excerptProjection = ClientProjection.class)
public interface ClientRepository extends JpaRepository<Client, UUID> {
}
