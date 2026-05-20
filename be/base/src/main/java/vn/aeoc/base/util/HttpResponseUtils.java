package vn.aeoc.base.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.aeoc.base.constant.ErrorCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//Sử dụng khi cần trả lỗi nhất quán
public final class HttpResponseUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private HttpResponseUtils() {
    }

    public static void writeError(HttpServletResponse response, HttpServletRequest request, ErrorCode errorCode) throws IOException {
        writeError(response, request, errorCode.getCode(), errorCode.getMessage());
    }

    public static void writeError(HttpServletResponse response, HttpServletRequest request, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> body = new HashMap<>();
        body.put("path", request.getRequestURI());
        body.put("message", message);
        body.put("code", statusCode);

        response.getWriter().write(OBJECT_MAPPER.writeValueAsString(body));
    }
}


