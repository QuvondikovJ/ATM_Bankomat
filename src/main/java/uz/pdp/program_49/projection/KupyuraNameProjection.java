package uz.pdp.program_49.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.pdp.program_49.entity.KupyuraName;

@Projection(types = KupyuraName.class)
public interface KupyuraNameProjection  {
Integer getId();
String getCurrency();


}
