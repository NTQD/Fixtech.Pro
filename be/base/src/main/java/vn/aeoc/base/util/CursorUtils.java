package vn.aeoc.base.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class CursorUtils {

    private CursorUtils() {}

    /** Encode timestamp (epoch millis) + id into an opaque cursor string. */
    public static String encode(long epochMilli, long id) {
        String raw = epochMilli + "_" + id;
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    /** Returns [epochMilli, id]. Throws if cursor is malformed. */
    public static long[] decode(String cursor) {
        try {
            String raw = new String(
                    Base64.getUrlDecoder().decode(cursor),
                    StandardCharsets.UTF_8
            );
            String[] parts = raw.split("_", 2);
            return new long[]{ Long.parseLong(parts[0]), Long.parseLong(parts[1]) };
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cursor: " + cursor);
        }
    }
}