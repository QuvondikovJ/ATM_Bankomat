package uz.pdp.program_49.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.pdp.program_49.entity.Card;
import uz.pdp.program_49.repository.CardRepository;
import uz.pdp.program_49.service.CardService;
import uz.pdp.program_49.service.EmployeeService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    CardService cardService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CardRepository cardRepository;

    Map<String, LocalDateTime> map = new LinkedHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer")) {
            token = token.substring(7);
            String username = jwtProvider.getUsernameFromToken(token);
            if (username != null) {
                UserDetails userDetails = employeeService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } else if (token != null && token.startsWith("Basic")) {
            token = token.substring(6);
            byte[] bytes = Base64
                    .getDecoder().decode(token);
            String cardToken = new String(bytes, StandardCharsets.UTF_8);
            String[] words = cardToken.split(":");
            String cardUsername = words[0];
            String cardPassword = words[1];
            UserDetails userDetails =
                    cardService.loadUserByUsername(cardUsername);

            if (passwordEncoder.matches(cardPassword, userDetails.getPassword()) && userDetails.isEnabled()) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                LocalDateTime localDateTime = LocalDateTime.now();
                boolean exists = map.containsKey(cardUsername);
                if (exists) {
                    Optional<Card> optionalCard = cardRepository.findByUsernameAndActive(cardUsername, true);
                    if (optionalCard.isPresent()) {
                        Card card = optionalCard.get();
                        card.setEnabled(false);
                        cardRepository.save(card);
                    }
                } else {
                    map.put(cardUsername, localDateTime);
                   Collection<LocalDateTime> collection = map.values();
                    for (LocalDateTime localDateTime1 : collection) {
                        if (localDateTime1.plusDays(1).isBefore(localDateTime)) {
                            Set<String> keySet = map.keySet();
                            for (String key : keySet) {
                                if (map.get(key).equals(localDateTime1)) {
                                    map.remove(key);
                                }
                            }
                        }
                    }

                }
            }
            // bu yerda agar card orqali tizimga kirilganda  1 marta not'g'ri kiritganidan
            // keyin bir kun davomida yana bir marta noto'g'ri password kiritsa
            // card bloklanadi
        }
            filterChain.doFilter(request, response);
    }

}
