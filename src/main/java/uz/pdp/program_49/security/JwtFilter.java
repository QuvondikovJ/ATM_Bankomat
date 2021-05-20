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

    List<String> list = new ArrayList<>();

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

              for (int i = 0; i < list.size(); i++) {
                  if (list.get(i).equals(cardUsername)) list.remove(i);
              }
            } else {
                boolean check = false;
                for (String checkCardUsername : list) {
                    if (checkCardUsername.equals(cardUsername)) {
                        Optional<Card> optionalCard = cardRepository.findByUsernameAndActive(cardUsername, true);
                        if (optionalCard.isPresent()) {
                            Card card = optionalCard.get();
                            card.setEnabled(false);
                            cardRepository.save(card);
                            list.remove(cardUsername);
                            check = true;
                        }
                    }
                }
                if (!check) {
                    list.add(cardUsername);
                }
            }

        }
        filterChain.doFilter(request, response);
    }

}
