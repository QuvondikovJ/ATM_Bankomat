package uz.pdp.program_49.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.pdp.program_49.entity.Bank;

import java.sql.Timestamp;
import java.util.UUID;

@Projection(types = Bank.class)
public interface BankProjection {
Integer getId();
String getName();
Timestamp getCreatedAt();
Timestamp getUpdatedAt();
UUID getCreatedBy();
UUID getUpdatedBy();




}
