package vn.aeoc.entity.data.response;

import lombok.Data;
import vn.aeoc.entity.data.mysql.User;

/**
 * Trả về thông tin đầy đủ của một người dùng (Phục vụ cho /auth/api/me khi login.
 * Đồng thời gia hạn token
 */
@Data
public class UserInfoResponse {
    private String token;

    //Thông tin cơ bản về user
    private User user;
}
