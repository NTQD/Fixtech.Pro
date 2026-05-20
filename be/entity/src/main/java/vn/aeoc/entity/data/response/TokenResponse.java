package vn.aeoc.entity.data.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponse {
    private String token;
    private Integer userId;

    public TokenResponse() {
    }

    public TokenResponse(String token) {
        this.token = token;
    }

    public TokenResponse(String token, Integer userId) {
        this.token = token;
        this.userId = userId;
    }
}