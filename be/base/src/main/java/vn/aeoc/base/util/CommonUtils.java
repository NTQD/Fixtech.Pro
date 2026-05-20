package vn.aeoc.base.util;

import org.apache.commons.lang3.StringUtils;
import org.jooq.JSON;
import org.jooq.exception.DataException;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommonUtils {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static Float NVL(Float l) {
        return NVL(l, 0f);
    }

    public static Long NVL(Long l) {
        return NVL(l, 0l);
    }

    public static Double NVL(Double l) {
        return NVL(l, 0.0);
    }

    public static Long NVL(Long l, Long defaultVal) {
        return (l == null ? defaultVal : l);
    }

    public static Integer NVL(Integer t) {
        return NVL(t, 0);
    }

    public static Integer NVL(Integer t, Integer defaultVal) {
        return (t == null ? defaultVal : t);
    }

    public static Double NVL(Double t, Double defaultVal) {
        return (t == null ? defaultVal : t);
    }

    public static <T> T NVL(T t, T defaultVal) {
        return (t == null ? defaultVal : t);
    }

    private static final Map<String, Integer> runningCodes = new ConcurrentHashMap<>();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Sinh mã code dạng PREFIX + yyyyMMdd + - + 6 số tăng dần
     * Ví dụ: INV20250203-000001
     */
    public static synchronized String nextCode(String prefix) {
        String today = LocalDate.now().format(DATE_FORMAT);

        // Key theo ngày + prefix riêng
        String key = prefix + today;

        // Lấy số tăng dần
        Integer current = runningCodes.getOrDefault(key, 0);
        current++;
        runningCodes.put(key, current);

        return prefix + today + "-" + String.format("%06d", current);
    }

    public static String NVL(String l) {
        if (StringUtils.isBlank(l)) {
            return "";
        }

        return l.trim();
    }

    public static String NVL(String l, String defaultVal) {
        return l == null ? defaultVal : l.trim();
    }

    /**
     * Lấy về kiểu JSONB trong postgresql
     *
     * @param jsonString
     * @return
     */
    public static JSON getJSON(String jsonString) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }

        try {
            JSON jsonData = JSON.valueOf(jsonString);
            return jsonData;
        } catch (DataException e) {
            System.err.println("Chuỗi JSON không hợp lệ: " + e.getMessage());
            return null;
        }
    }

    /**
     * Sinh chuỗi số ngẫu nhiên có độ dài bất kỳ (OTP)
     * Ví dụ: randomNumber(6) -> "482931"
     */
    public static String randomNumber(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be > 0");
        }

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(RANDOM.nextInt(10)); // 0 -> 9
        }

        return sb.toString();
    }
}
