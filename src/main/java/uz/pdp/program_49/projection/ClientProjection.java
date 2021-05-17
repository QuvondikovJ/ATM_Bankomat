package uz.pdp.program_49.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.pdp.program_49.entity.Client;

import java.util.UUID;

@Projection(types = Client.class)
public interface ClientProjection {
UUID getId();
String getFirstName();
String getLastName();
String getPhoneNumber();

}
