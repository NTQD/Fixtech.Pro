package vn.aeoc.rbac.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.aeoc.base.constant.ErrorCode;
import vn.aeoc.base.exception.AppException;
import vn.aeoc.base.util.HttpResponseUtils;
import vn.aeoc.rbac.config.TokenService;
import vn.aeoc.rbac.config.principal.impl.AppPrincipal;
import vn.aeoc.rbac.config.property.SystemProperties;
import vn.aeoc.rbac.config.resolver.PrincipalResolver;
import vn.aeoc.rbac.config.web.PrincipalRequestWrapper;

import java.io.IOException;
import java.security.Principal;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService jwtService;
    private final PrincipalResolver principalResolver;
    private final SystemProperties systemProperties;

    private static final String INTERNAL_API_KEY_ATTRIBUTE = "IS_INTERNAL_API_CALL";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // ==================== 1. KIỂM TRA APP API KEY ====================
        String apiKey = request.getHeader("X-Api-Key");

        if (apiKey != null && !apiKey.isBlank()) {
            if (apiKey.equals(systemProperties.getAppApiKey())) {
                // Đánh dấu đây là internal call → RoleInterceptor sẽ bypass
                request.setAttribute(INTERNAL_API_KEY_ATTRIBUTE, true);
                filterChain.doFilter(request, response);
                return;
            } else {
                // API Key sai
                HttpResponseUtils.writeError(response, request, ErrorCode.INVALID_APP_API_KEY);
                return;
            }
        }

        // ==================== 2. XỬ LÝ JWT BÌNH THƯỜNG ====================
        String token = extractTokenFromRequest(request);

        try {
            if (token != null) {
                if (jwtService.isExpired(token)) {
                    HttpResponseUtils.writeError(response, request, ErrorCode.TOKEN_EXPIRED);
                    return;
                }

                Principal principal = principalResolver.resolve(token, request);

                if (principal != null) {
                    request = new PrincipalRequestWrapper(request, principal);
                }
            }

            filterChain.doFilter(request, response);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            HttpResponseUtils.writeError(response, request, ErrorCode.TOKEN_EXPIRED);
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            HttpResponseUtils.writeError(response, request, ErrorCode.UNAUTHORIZED);
        } catch (AppException e) {
            HttpResponseUtils.writeError(response, request, e.getStatusCode(), e.getMessage());
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}