package vn.aeoc.rbac.config;

import io.jsonwebtoken.Claims;
import vn.aeoc.rbac.model.SecurityUser;

import java.util.function.Function;

public interface TokenService {

    String generate(SecurityUser<?> user);

    boolean validate(String token);

    boolean isExpired(String token);

    <T> T getClaim(String token, Function<Claims, T> resolver);

    SecurityUser<?> extractUser(String token);
}
