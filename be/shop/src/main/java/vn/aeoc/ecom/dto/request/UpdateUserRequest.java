package vn.aeoc.ecom.dto.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;
    private String phone;
    private String email;
    private String role;
    private Boolean active;
    private String password;
    private String plainPassword;
}

