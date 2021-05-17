package uz.pdp.program_49.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    long expireTime = 1000 * 60 * 60 * 24;
    String secretKey = "BuSuzJudaMaxfiyBuniXechKimBilmasin123";

    public String generateToken(String username) {
        Date expireDate = new Date(System.currentTimeMillis() + expireTime);

        String token = Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
        return token;
    }


    public String getUsernameFromToken(String token) {
        try {
            String username = Jwts
                    .parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return username;
        } catch (Exception e) {
            return null;
        }
    }
}
