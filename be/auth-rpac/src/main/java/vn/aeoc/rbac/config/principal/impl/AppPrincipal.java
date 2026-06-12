package vn.aeoc.rbac.config.principal.impl;

import lombok.Builder;
import lombok.Data;
import vn.aeoc.rbac.config.principal.Authentication;

import java.security.Principal;
import java.util.Map;

@Data
@Builder
public class AppPrincipal<T> implements Principal {

    private Authentication authentication;
    private T otherInfo;
    private Integer id;
    private Map<String, String> clientInfo;

    @Override
    public String getName() {
        return id != null ? id.toString() : null;
    }

    public boolean isAdmin() {
        return authentication != null && "admin".equalsIgnoreCase(authentication.getRole());
    }

    public boolean isUser() {
        if (authentication == null) return false;
        String r = authentication.getRole();
        return "user".equalsIgnoreCase(r) || "customer".equalsIgnoreCase(r);
    }
}
