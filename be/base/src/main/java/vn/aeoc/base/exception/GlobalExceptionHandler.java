package vn.aeoc.base.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Map<String, Object>> handleAppException(AppException ex, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("path", request.getRequestURI());
        body.put("message", ex.getMessage());
        body.put("code", ex.getStatusCode());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex, HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("path", request.getRequestURI());
        body.put("message", "Lỗi: " + ex.getMessage());
        body.put("code", 500);
        return ResponseEntity.status(500).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, Object> body = new HashMap<>();

        Map<String, String> errors = new HashMap<>();

        body.put("path", request.getRequestURI());
        body.put("code", 400);
        body.put("message", "Validation failed");
        body.put("error", ex.getBindingResult().getFieldError().getDefaultMessage());

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointerException(
            NullPointerException ex, HttpServletRequest request) {

        Map<String, Object> body = new HashMap<>();
        body.put("path", request.getRequestURI());
        body.put("code", 500);
        body.put("dev", (ex.getMessage() != null ? ex.getMessage() : "Null pointer exception"));

        // Optional: You can make it more user-friendly
         body.put("message", "Lỗi hệ thống. Vui lòng thử lại sau.");

        return ResponseEntity.status(500).body(body);
    }
}
