package dev.pott.sucks.api;

import dev.pott.sucks.util.StringPlaceholderUtils;

import java.util.HashMap;

public final class EcovacsApiUrlFactory {

    private EcovacsApiUrlFactory() {
        // Prevent instantiation
    }

    private static final String MAIN_URL_FORMAT = "https://gl-{country}-api.ecovacs.com/v1/private/{country}/{lang}/{deviceId}/{appCode}/{appVersion}/{channel}/{deviceType}";
    private static final String MAIN_URL_LOGIN_PATH = "/user/login";

    private static final String USER_URL_FORMAT = "https://users-{continent}.ecouser.net:8000/user.do";

    private static final String PORTAL_URL_FORMAT = "https://portal-{continent}.ecouser.net/api";
    private static final String PORTAL_URL_FORMAT_CN = "https://portal.ecouser.net/api/";
    private static final String PORTAL_GLOBAL_AUTHCODE = "https://gl-{country}-openapi.ecovacs.com/v1/global/auth/getAuthCode";

    private static final String COUNTRY_PLACEHOLDER = "{country}";
    private static final String LANGUAGE_PLACEHOLDER = "{lang}";
    private static final String DEVICE_ID_PLACEHOLDER = "{deviceId}";
    private static final String APP_CODE_PLACEHOLDER = "{appCode}";
    private static final String APP_VERSION_PLACEHOLDER = "{appVersion}";
    private static final String CHANNEL_PLACEHOLDER = "{channel}";
    private static final String DEVICE_TYPE_PLACEHOLDER = "{deviceType}";
    private static final String CONTINENT_PLACEHOLDER = "{continent}";

    public static String getLoginUrl(
            String country,
            String language,
            String deviceId,
            String appCode,
            String appVersion,
            String channel,
            String deviceType
    ) {
        return getMainUrl(country, language, deviceId, appCode, appVersion, channel, deviceType) + MAIN_URL_LOGIN_PATH;
    }

    private static String getMainUrl(
            String country,
            String language,
            String deviceId,
            String appCode,
            String appVersion,
            String channel,
            String deviceType
    ) {
        HashMap<String, String> placeholder = new HashMap<>();
        placeholder.put(COUNTRY_PLACEHOLDER, country);
        placeholder.put(LANGUAGE_PLACEHOLDER, language);
        placeholder.put(DEVICE_ID_PLACEHOLDER, deviceId);
        placeholder.put(APP_CODE_PLACEHOLDER, appCode);
        placeholder.put(APP_VERSION_PLACEHOLDER, appVersion);
        placeholder.put(CHANNEL_PLACEHOLDER, channel);
        placeholder.put(DEVICE_TYPE_PLACEHOLDER, deviceType);
        return StringPlaceholderUtils.replacePlaceHolders(MAIN_URL_FORMAT, placeholder);
    }

}
