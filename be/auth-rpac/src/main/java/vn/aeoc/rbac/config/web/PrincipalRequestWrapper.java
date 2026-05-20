package vn.aeoc.rbac.config.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.security.Principal;

public class PrincipalRequestWrapper extends HttpServletRequestWrapper {

    private final Principal principal;

    public PrincipalRequestWrapper(HttpServletRequest request, Principal principal) {
        super(request);
        this.principal = principal;
    }

    @Override
    public Principal getUserPrincipal() {
        return this.principal;
    }
}
