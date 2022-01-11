package dev.pott.sucks.api.util;

import java.util.Map;

public class StringPlaceholderUtils {

    public static String replacePlaceHolders(String format, Map<String, String> placeholder) {
        String result = format;
        for (Map.Entry<String, String> entry : placeholder.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            result = result.replace(key, value);
        }
        return result;
    }
}
