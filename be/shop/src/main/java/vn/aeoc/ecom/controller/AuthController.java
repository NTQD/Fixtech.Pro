package vn.aeoc.ecom.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import vn.aeoc.base.config.model.ApiResponse;
import vn.aeoc.base.util.Mapper;
import vn.aeoc.ecom.dto.response.AuthResponse;
import vn.aeoc.ecom.service.AuthService;
import vn.aeoc.entity.data.mysql.User;
import vn.aeoc.entity.data.response.TokenResponse;
import vn.aeoc.rbac.annotation.IsUser;
import vn.aeoc.rbac.config.AppUserService;
import vn.aeoc.rbac.config.principal.impl.AppPrincipal;

@RestController
@AllArgsConstructor
public class AuthController {
    private final vn.aeoc.ecom.service.UserService userService;
    private final AuthService authService;

    @PostMapping("/authenticate/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody User user) {

        userService.register(user);

        return Mapper.map(new AuthResponse(true, "Đăng ký thành công"), ApiResponse::okEntity);
    }

    @PostMapping("/authenticate/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@RequestBody User loginRequest) {

        TokenResponse token = authService.authenticate(loginRequest, false);

        return Mapper.map(token, ApiResponse::okEntity);
    }

    @GetMapping("/authenticate/me")
    @IsUser
    public ResponseEntity<ApiResponse<User>> me(
            AppPrincipal<User> principal) {

        Integer userId = Integer.valueOf(principal.getName());
        User user = userService.findById(userId);

        return Mapper.map(user, ApiResponse::okEntity);
    }

    @GetMapping("/authenticate/test")
    @IsUser
    public ResponseEntity<ApiResponse<String>> test(
            AppPrincipal<User> principal
    )
    {
        String test = "ok";
        return Mapper.map(test, ApiResponse::okEntity);
    }

    @org.springframework.web.bind.annotation.PutMapping("/authenticate/me")
    @IsUser
    public ResponseEntity<ApiResponse<User>> updateMe(
            @RequestBody vn.aeoc.ecom.dto.request.UpdateUserRequest request,
            AppPrincipal<User> principal) {

        Integer userId = Integer.valueOf(principal.getName());
        User user = new User();
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        
        userService.update(userId, user);

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            userService.updatePassword(userId, request.getPassword());
        }

        return Mapper.map(userService.findById(userId), ApiResponse::okEntity);
    }
}
