package uz.pdp.program_49.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.pdp.program_49.entity.Kupyura;

import java.sql.Timestamp;
import java.util.UUID;

@Projection(types = Kupyura.class)
public interface KupyuraProjection {
Integer getId();
Integer getKupyura();
String getCurrency();
    Timestamp getCreatedAt();
    Timestamp getUpdatedAt();
    UUID getCreatedBy();
    UUID getUpdatedBy();




}
