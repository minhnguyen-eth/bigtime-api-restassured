package base;

import java.util.HashMap;
import java.util.Map;

public class CommonHeaders {

    public static Map<String, String> getCommonHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("x-client-request", "hero");
        headers.put("x-client-language", "vi");
        return headers;
    }

    public static Map<String, String> getAuthHeaders(String token) {
        Map<String, String> headers = getCommonHeaders();
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }
}