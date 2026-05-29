package vn.aeoc.rbac.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import vn.aeoc.base.constant.ErrorCode;
import vn.aeoc.base.util.HttpResponseUtils;
import vn.aeoc.rbac.annotation.IsAdmin;
import vn.aeoc.rbac.annotation.IsSeller;
import vn.aeoc.rbac.annotation.IsUser;
import vn.aeoc.rbac.config.principal.impl.AppAuthentication;
import vn.aeoc.rbac.config.principal.impl.AppPrincipal;

import java.io.IOException;

@Slf4j
@Component
public class RoleInterceptor implements HandlerInterceptor {

    private static final String INTERNAL_API_KEY_ATTRIBUTE = "IS_INTERNAL_API_CALL";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {

        // ==================== BYPASS NẾU LÀ INTERNAL API KEY ====================
        if (Boolean.TRUE.equals(request.getAttribute(INTERNAL_API_KEY_ATTRIBUTE))) {
            return true;        // Cho phép đi qua tất cả, không kiểm tra role
        }

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        boolean needAdmin  = handlerMethod.hasMethodAnnotation(IsAdmin.class);
        boolean needUser   = handlerMethod.hasMethodAnnotation(IsUser.class);
        boolean needSeller = handlerMethod.hasMethodAnnotation(IsSeller.class);

        // Nếu endpoint không yêu cầu quyền gì thì cho qua
        if (!needAdmin && !needUser && !needSeller) {
            return true;
        }

        // ==================== KIỂM TRA PRINCIPAL ====================
        java.security.Principal p = request.getUserPrincipal();

        if (p == null) {
            log.warn("No principal found for protected endpoint: {}", request.getRequestURI());
            HttpResponseUtils.writeError(response, request, ErrorCode.UNAUTHORIZED);
            return false;
        }

        if (!(p instanceof AppPrincipal appPrincipal)) {
            log.warn("Invalid principal type: {}", p.getClass().getName());
            HttpResponseUtils.writeError(response, request, ErrorCode.FORBIDDEN);
            return false;
        }

        AppAuthentication auth = (AppAuthentication) appPrincipal.getAuthentication();
        if (auth == null) {
            HttpResponseUtils.writeError(response, request, ErrorCode.UNAUTHORIZED);
            return false;
        }

        // ==================== KIỂM TRA QUYỀN ====================
        if (auth.isAdmin()) {
            return true;
        }
        if (needSeller && (auth.isSeller() || auth.isAdmin())) {
            return true;
        }
        if (needUser && (auth.isUser() || auth.isSeller())) {
            return true;
        }

        // Không đủ quyền
        log.warn("Access denied for user '{}' on endpoint '{}'", appPrincipal.getName(), request.getRequestURI());
        HttpResponseUtils.writeError(response, request, ErrorCode.FORBIDDEN);
        return false;
    }
}
