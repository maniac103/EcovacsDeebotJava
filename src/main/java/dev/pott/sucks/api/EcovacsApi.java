package dev.pott.sucks.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.pott.sucks.api.dto.request.portal.PortalAuthRequest;
import dev.pott.sucks.api.dto.request.portal.PortalAuthRequestParameter;
import dev.pott.sucks.api.dto.request.portal.PortalLoginRequest;
import dev.pott.sucks.api.dto.response.main.AccessData;
import dev.pott.sucks.api.dto.response.main.AuthCode;
import dev.pott.sucks.api.dto.response.main.ResponseWrapper;
import dev.pott.sucks.api.dto.response.portal.PortalDeviceResponse;
import dev.pott.sucks.api.dto.response.portal.PortalLoginResponse;
import dev.pott.sucks.util.MD5Util;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringRequestContent;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class EcovacsApi {

    private static final String CLIENT_KEY = "1520391301804";
    private static final String SECRET = "6c319b2a5cd3e66e39159c2e28f2fce9";

    private static final String AUTH_CLIENT_KEY = "1520391491841";
    private static final String AUTH_CLIENT_SECRET = "77ef58ce3afbe337da74aa8c5ab963a9";

    private final HttpClient httpClient;
    private final Gson gson;
    private final EcovacsApiConfiguration configuration;
    private final Map<String, String> meta = new HashMap<>();

    public EcovacsApi(HttpClient httpClient, Gson gson, EcovacsApiConfiguration configuration) {
        this.httpClient = httpClient;
        this.gson = gson;
        this.configuration = configuration;

        meta.put("country", configuration.getCountry());
        meta.put("lang", "EN");
        meta.put("deviceId", configuration.getDeviceId());
        meta.put("appCode", "global_e");
        meta.put("appVersion", "1.6.3");
        meta.put("channel", "google_play");
        meta.put("deviceType", "1");
    }

    public ResponseWrapper<AccessData> login() {
        try {
            httpClient.start();

            // Generate login Params
            HashMap<String, String> loginParameters = new HashMap<>();
            loginParameters.put("account", configuration.getUsername());
            loginParameters.put("password", MD5Util.getMD5Hash(configuration.getPassword()));
            loginParameters.put("requestId", MD5Util.getMD5Hash(String.valueOf(System.currentTimeMillis())));
            HashMap<String, String> signedRequestParameters = getSignedRequestParameters(loginParameters);

            String loginUrl = EcovacsApiUrlFactory.getLoginUrl(
                    configuration.getCountry(),
                    "EN",
                    configuration.getDeviceId(),
                    "global_e",
                    "1.6.3",
                    "google_play",
                    "1"
            );

            Request loginRequest = httpClient.newRequest(loginUrl).method(HttpMethod.GET);
            signedRequestParameters.forEach(loginRequest::param);
            ContentResponse loginResponse = loginRequest.send();

            httpClient.stop();

            if (loginResponse.getStatus() == HttpStatus.OK_200) {
                Type responseType = new TypeToken<ResponseWrapper<AccessData>>() {
                }.getType();
                return getMainResponse(loginResponse, responseType);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResponseWrapper<AuthCode> getAuthCode(AccessData accessData) {
        try {
            httpClient.start();
            HashMap<String, String> authCodeParameters = new HashMap<>();
            authCodeParameters.put("uid", accessData.getUid());
            authCodeParameters.put("accessToken", accessData.getAccessToken());
            authCodeParameters.put("bizType", "ECOVACS_IOT");
            authCodeParameters.put("deviceId", configuration.getDeviceId());
            HashMap<String, String> signedRequestParameters = getSignedAuthCodeRequestParameters(authCodeParameters);

            String authCodeUrl = EcovacsApiUrlFactory.getAuthUrl(configuration.getCountry());

            Request authCodeRequest = httpClient.newRequest(authCodeUrl).method(HttpMethod.GET);
            signedRequestParameters.forEach(authCodeRequest::param);
            ContentResponse authCodeResponse = authCodeRequest.send();

            httpClient.stop();

            if (authCodeResponse.getStatus() == HttpStatus.OK_200) {
                Type responseType = new TypeToken<ResponseWrapper<AuthCode>>() {
                }.getType();
                return getMainResponse(authCodeResponse, responseType);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public PortalLoginResponse portalLogin(AuthCode authCode, AccessData accessData) {
        try {
            httpClient.start();
            PortalLoginRequest loginRequestData = new PortalLoginRequest(
                    "loginByItToken",
                    configuration.getCountry(),
                    "",
                    "ECOWW",
                    configuration.getResource(),
                    "ecouser.net",
                    authCode.getAuthCode(),
                    accessData.getUid(),
                    "ECOGLOBLE"
            );
            String json = gson.toJson(loginRequestData);

            String userUrl = EcovacsApiUrlFactory.getPortalUsersUrl(configuration.getContinent());
            Request loginRequest = httpClient.newRequest(userUrl)
                    .method(HttpMethod.POST)
                    .headers(httpFields -> httpFields.add(HttpHeader.CONTENT_TYPE, "application/json"))
                    .body(new StringRequestContent(json));
            ContentResponse portalLoginResponse = loginRequest.send();

            httpClient.stop();

            if (portalLoginResponse.getStatus() == HttpStatus.OK_200) {
                return getPortalLoginResponse(portalLoginResponse);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public PortalDeviceResponse getDevices(PortalLoginResponse portalLoginResponse) {
        try {
            httpClient.start();
            PortalAuthRequestParameter deviceRequestData = new PortalAuthRequestParameter(
                    "users",
                    portalLoginResponse.getUserId(),
                    "ecouser.net",
                    portalLoginResponse.getToken(),
                    configuration.getDeviceId().substring(0, 8)

            );
            PortalAuthRequest data = new PortalAuthRequest(
                    "GetDeviceList",
                    portalLoginResponse.getUserId(),
                    deviceRequestData
            );
            String json = gson.toJson(data);
            String userUrl = EcovacsApiUrlFactory.getPortalUsersUrl(configuration.getContinent());
            Request deviceRequest = httpClient.newRequest(userUrl)
                    .method(HttpMethod.POST)
                    .headers(httpFields -> httpFields.add(HttpHeader.CONTENT_TYPE, "application/json"))
                    .body(new StringRequestContent(json));
            ContentResponse deviceResponse = deviceRequest.send();
            httpClient.stop();
            if (deviceResponse.getStatus() == HttpStatus.OK_200) {
                return gson.fromJson(
                        deviceResponse.getContentAsString(),
                        PortalDeviceResponse.class
                );
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private <T> T getMainResponse(ContentResponse loginResponse, Type dataType) {
        String content = loginResponse.getContentAsString();
        return gson.fromJson(content, dataType);
    }

    private PortalLoginResponse getPortalLoginResponse(ContentResponse contentResponse) {
        String content = contentResponse.getContentAsString();
        PortalLoginResponse response = gson.fromJson(content, PortalLoginResponse.class);
        if (Objects.equals(response.getResult(), "ok")) {
            return response;
        } else {
            return null;
        }
    }

    private HashMap<String, String> getSignedRequestParameters(Map<String, String> requestSpecificParameters) {
        HashMap<String, String> signedRequestParameters = new HashMap<>();
        signedRequestParameters.put("authTimespan", String.valueOf(System.currentTimeMillis()));
        signedRequestParameters.put("authTimeZone", "GMT-8");
        signedRequestParameters.putAll(requestSpecificParameters);

        HashMap<String, String> signOn = new HashMap<>(meta);
        signOn.putAll(signedRequestParameters);
        StringBuilder signOnText = new StringBuilder(CLIENT_KEY);

        List<String> keys = signOn.keySet().stream().sorted().collect(Collectors.toList());
        for (String key : keys) {
            signOnText.append(key).append("=").append(signOn.get(key));
        }
        signOnText.append(SECRET);

        signedRequestParameters.put("authAppkey", CLIENT_KEY);
        signedRequestParameters.put("authSign", MD5Util.getMD5Hash(signOnText.toString()));
        return signedRequestParameters;
    }

    private HashMap<String, String> getSignedAuthCodeRequestParameters(Map<String, String> requestSpecificParameters) {
        HashMap<String, String> signedRequestParameters = new HashMap<>(requestSpecificParameters);
        signedRequestParameters.put("authTimespan", String.valueOf(System.currentTimeMillis()));

        HashMap<String, String> signOn = new HashMap<>(signedRequestParameters);
        signOn.put("openId", "global");
        StringBuilder signOnText = new StringBuilder(AUTH_CLIENT_KEY);

        List<String> keys = signOn.keySet().stream().sorted().collect(Collectors.toList());
        for (String key : keys) {
            signOnText.append(key).append("=").append(signOn.get(key));
        }
        signOnText.append(AUTH_CLIENT_SECRET);

        signedRequestParameters.put("authAppkey", AUTH_CLIENT_KEY);
        signedRequestParameters.put("authSign", MD5Util.getMD5Hash(signOnText.toString()));
        return signedRequestParameters;
    }

}
