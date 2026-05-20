package vn.aeoc.base.config.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {

    private final int code;
    private final String message;
    private final T result;

    private final Object errors;

    public static <T> ResponseEntity<ApiResponse<T>> okEntity(T body) {
        return ResponseEntity.ok(ok(body));
    }

    public static <T> ApiResponse<T> ok(T result) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.OK.value())
                .message("OK")
                .result(result)
                .build();
    }

    public static <T> ApiResponse<T> created(T result) {
        return ApiResponse.<T>builder()
                .code(HttpStatus.CREATED.value())
                .message("Created")
                .result(result)
                .build();
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return ApiResponse.<T>builder()
                .code(status.value())
                .message(message)
                .build();
    }

    /**
     * Trả về lỗi kèm theo đối tượng errors chi tiết.
     */
    public static <T> ApiResponse<T> error(HttpStatus status, String message, Object errors) {
        return ApiResponse.<T>builder()
                .code(status.value())
                .message(message)
                .errors(errors)
                .build();
    }

    // ---  Phương thức tiện ích để chuyển đổi sang ResponseEntity ---

    public ResponseEntity<ApiResponse<T>> toResponseEntity() {
        return new ResponseEntity<>(this, HttpStatus.valueOf(this.code));
    }
}