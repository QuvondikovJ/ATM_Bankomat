package uz.pdp.program_49.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.pdp.program_49.entity.KupyuraValue;

@Projection(types = KupyuraValue.class)
public interface KupyuraValueProjection {
Integer getId();
Integer getKupyuraValue();


}
