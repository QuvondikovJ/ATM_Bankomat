package uz.pdp.program_49.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.pdp.program_49.entity.District;
import uz.pdp.program_49.entity.Region;

@Projection(types = District.class)
public interface DistrictProjection {
Integer getId();
String getName();
Region getRegion();


}
