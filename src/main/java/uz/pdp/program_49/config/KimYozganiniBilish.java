package uz.pdp.program_49.config;

import org.apache.catalina.User;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.pdp.program_49.entity.Card;
import uz.pdp.program_49.entity.Employee;

import java.util.Optional;
import java.util.UUID;

public class KimYozganiniBilish implements AuditorAware<UUID> {


    @Override
    public Optional<UUID> getCurrentAuditor() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
        !authentication.getPrincipal().equals("anonymousUser")){
            try {
                Employee employee = (Employee) authentication.getPrincipal();
                return Optional.of(employee.getId());
            }catch (Exception e){
                Card  card = (Card) authentication.getPrincipal();
                return Optional.of(card.getId());
            }
        }
        return Optional.empty();
    }
}
