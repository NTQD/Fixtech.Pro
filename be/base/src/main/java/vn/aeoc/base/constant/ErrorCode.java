package vn.aeoc.base.constant;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNAUTHORIZED(401, "Token không hợp lệ hoặc sai chữ ký"),
    INVALID_APP_API_KEY(401, "Invalid API Key."),

    TOKEN_EXPIRED(401,  "Token đã hết hạn"),
    FORBIDDEN(403, "Không có quyền truy cập"),
    LOCK_ACCOUNT(403, "Tài khoản bị khóa"),
    INTERNAL_ERROR(500, "Lỗi hệ thống"),

    DUPLICATED_USER(400, "Số điện thoại tạo tài khoản đã tồn tại trong hệ thống"),
    INVALID_AUTHENTICATION_INFO(400, "Tài khoản hoặc mật khẩu không đúng."),
    DUPLICATED_EMAIL(400, "Email tạo tài khoản đã tồn tại"),
    UPLOAD_FILE_FAILED(400, "Upload file thất bại"),

    WRONG_LOGIN_INFO(400, "Sai thông tin đăng nhập. Kiểm tra lại tài khoản hoặc mật khẩu!"),
    MISSING_PASSWORD(400, "Chưa nhập mật khẩu!"),
    WRONG_PASSWORD(400, "Mật khẩu không hợp lệ"),
    SAME_PASSWORD(400, "Mật khẩu mới và cũ giống nhau"),
    MISSING_EMAIL_OR_PHONE(400, "Chưa nhập email hoặc số điện thoại!"),
    REGISTRATION_DISABLED(403, "Hệ thống không mở đăng ký"),
    DATA_NOT_FOUND(404, "Dữ liệu không tồn tại"),
    DEFAULT_ROLE_NOT_FOUND(404, "Role mặc định không tồn tại"),
    LOCK_STATUS(401, "Trạng thái không hoạt động"),
    BAD_REQUEST(400, "Yêu cầu không hợp lệ")
    ;

    public final int code;
    public final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
