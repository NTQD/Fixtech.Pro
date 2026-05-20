package vn.aeoc.rbac.config.principal.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import vn.aeoc.rbac.config.principal.Authentication;

@AllArgsConstructor
@Getter
public class AppAuthentication implements Authentication {

    private final String role;

    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    public boolean isUser() {
        return "user".equalsIgnoreCase(role);
    }

    public boolean isSeller() {
        return "seller".equalsIgnoreCase(role);
    }
}
