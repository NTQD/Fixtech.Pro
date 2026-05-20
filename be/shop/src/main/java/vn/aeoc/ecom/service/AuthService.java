package vn.aeoc.ecom.service;


import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.aeoc.base.constant.ErrorCode;
import vn.aeoc.base.exception.AppException;
import vn.aeoc.ecom.dto.request.ChangePasswordRequest;
import vn.aeoc.entity.data.mysql.User;
import vn.aeoc.entity.data.response.TokenResponse;
import vn.aeoc.rbac.config.AppUserService;
import vn.aeoc.rbac.config.TokenService;

import static vn.aeoc.rbac.util.SecurityUtils.toSecurityUser;

@Service
@AllArgsConstructor
public class AuthService {

    private final AppUserService<User> userService;
    private final TokenService tokenService;

    public TokenResponse authenticate(User loginRequest, boolean refresh) {

        User userDb =
                userService.findForAuthentication(loginRequest.getEmail(), loginRequest.getPhone());

        if (userDb == null || !userDb.isActive()) {
            throw new AppException(ErrorCode.INVALID_AUTHENTICATION_INFO);
        }

        if (!refresh && !BCrypt.checkpw(loginRequest.getPassword(), userDb.getPassword())) {
            throw new AppException(ErrorCode.INVALID_AUTHENTICATION_INFO);
        }

        return new TokenResponse(tokenService.generate(toSecurityUser(userDb)));
    }

    @Transactional
    public void changePassword(Integer userId, ChangePasswordRequest request) {

        User userDb = userService.getAllFieldById(userId);
        if (userDb == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        // 1. Check old password
        if (!BCrypt.checkpw(request.getOldPassword(), userDb.getPassword())) {
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }

        // 3. New password must be different
        if (BCrypt.checkpw(request.getNewPassword(), userDb.getPassword())) {
            throw new AppException(ErrorCode.SAME_PASSWORD);
        }


        userService.updatePassword(userId, request.getNewPassword());
    }
}
