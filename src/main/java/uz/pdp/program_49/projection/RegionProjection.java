package uz.pdp.program_49.projection;

import org.springframework.data.rest.core.config.Projection;
import uz.pdp.program_49.entity.Region;

@Projection(types = Region.class)
public interface RegionProjection {
Integer getId();
String getName();



}
