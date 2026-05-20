package vn.aeoc.ecom.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    Boolean success;
    String message;
}
