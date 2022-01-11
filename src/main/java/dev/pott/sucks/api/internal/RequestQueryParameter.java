package dev.pott.sucks.api.internal;

public class RequestQueryParameter {

    private RequestQueryParameter() {
        // Prevent instantiation
    }

    // Authentication
    public static final String AUTH_TIMESPAN = "authTimespan";
    public static final String AUTH_TIME_ZONE = "authTimeZone";
    public static final String AUTH_APPKEY = "authAppkey";
    public static final String AUTH_SIGN = "authSign";
    public static final String AUTH_OPEN_ID = "openId";
    public static final String AUTH_ACCOUNT = "account";
    public static final String AUTH_PASSWORD = "password";
    public static final String AUTH_REQUEST_ID = "requestId";
    public static final String AUTH_CODE_UID = "uid";
    public static final String AUTH_CODE_ACCESS_TOKEN = "accessToken";
    public static final String AUTH_CODE_BIZ_TYPE = "bizType";
    public static final String AUTH_CODE_DEVICE_ID = "deviceId";

    // Metadata
    public static final String META_COUNTRY = "country";
    public static final String META_LANG = "lang";
    public static final String META_DEVICE_ID = "deviceId";
    public static final String META_APP_CODE = "appCode";
    public static final String META_APP_VERSION = "appVersion";
    public static final String META_CHANNEL = "channel";
    public static final String META_DEVICE_TYPE = "deviceType";
}
