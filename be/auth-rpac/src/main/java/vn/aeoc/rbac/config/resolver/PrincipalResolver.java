package vn.aeoc.rbac.config.resolver;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.aeoc.rbac.config.AppUserService;
import vn.aeoc.rbac.config.TokenService;
import vn.aeoc.rbac.config.principal.impl.AppAuthentication;
import vn.aeoc.rbac.config.principal.impl.AppPrincipal;
import vn.aeoc.rbac.model.AppUser;
import vn.aeoc.rbac.model.SecurityUser;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class PrincipalResolver {

    private final TokenService tokenService;
    private final AppUserService<? extends AppUser> appUserService;

    public Principal resolve(String token, HttpServletRequest request) {

        SecurityUser<?> securityUser = tokenService.extractUser(token);

        AppUser appUser = appUserService.findById(securityUser.getId());

        if (appUser == null) {
            throw new IllegalStateException("User not found: " + securityUser.getId());
        }

        AppAuthentication authentication = new AppAuthentication(
                appUser.getRole()
        );

        Map<String, String> clientInfo = new HashMap<>();
        clientInfo.put("User-Agent", request.getHeader("User-Agent"));
        clientInfo.put("X-Forwarded-For", request.getHeader("X-Forwarded-For"));
        clientInfo.put("X-Real-IP", request.getHeader("X-Real-IP"));

        return AppPrincipal.builder().id(appUser.getId()).authentication(authentication).otherInfo(appUser).clientInfo(clientInfo).build();
    }
}
