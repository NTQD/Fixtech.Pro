package vn.aeoc.rbac.util;

import vn.aeoc.rbac.config.principal.impl.AppPrincipal;
import vn.aeoc.rbac.model.AppUser;
import vn.aeoc.rbac.model.SecurityUser;

import java.security.Principal;

public class SecurityUtils {

    public static Integer userId(Principal principal) {
        if (principal == null) return 0;
        Object id = ((AppPrincipal) principal).getId();
        return id instanceof Integer ? (Integer) id : 0;
    }

    public static <T extends AppUser> SecurityUser<T> toSecurityUser(T user) {
        return new SecurityUser<>(user);
    }
}
