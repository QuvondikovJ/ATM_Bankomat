package uz.pdp.program_49.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.pdp.program_49.entity.Kupyura;
import uz.pdp.program_49.entity.KupyuraName;
import uz.pdp.program_49.entity.KupyuraValue;

import java.sql.Timestamp;
import java.util.UUID;

@Projection(types = Kupyura.class)
public interface KupyuraProjection {
Integer getId();
KupyuraValue getKupyuraValue();
KupyuraName getKupyuraName();
    Timestamp getCreatedAt();
    Timestamp getUpdatedAt();
    UUID getCreatedBy();
    UUID getUpdatedBy();




}
