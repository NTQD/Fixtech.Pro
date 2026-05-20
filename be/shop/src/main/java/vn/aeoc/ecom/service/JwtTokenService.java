package vn.aeoc.ecom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import vn.aeoc.rbac.config.TokenService;
import vn.aeoc.rbac.model.SecurityUser;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtTokenService implements TokenService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expired-in}")
    private Long expiresIn;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String generate(SecurityUser<?> user) {
        try {
            return Jwts.builder()
                    .subject(user.getId().toString())
                    .claim("user", objectMapper.writeValueAsString(user))
                    .expiration(new Date(System.currentTimeMillis() + expiresIn))
                    .signWith(getKey())
                    .compact();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot serialize SecurityUser", e);
        }
    }

    @Override
    public boolean validate(String token) {
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false; // token expired
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isExpired(String token) {
        try {
            Claims claims =
                    Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();

            Date exp = claims.getExpiration();
            return exp.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            return true;
        }
    }

    @Override
    public <T> T getClaim(String token, Function<Claims, T> resolver) {
        Claims claims =
                Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
        return resolver.apply(claims);
    }

    @Override
    public SecurityUser<?> extractUser(String token) {
        try {
            Claims claims =
                    Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();

            String userJson = claims.get("user", String.class);
            return objectMapper.readValue(userJson, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }
}
