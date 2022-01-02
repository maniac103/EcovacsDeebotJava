package dev.pott.sucks.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.pott.sucks.api.dto.*;
import dev.pott.sucks.api.dto.request.PortalAuthRequestParameter;
import dev.pott.sucks.api.dto.request.PortalRequest;
import dev.pott.sucks.util.MD5Util;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringRequestContent;
import org.eclipse.jetty.http.*;

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

    /**
     * Login at Ecovacs API
     *
     * @return LoginResponse or null if something went wrong
     */
    public LoginResponse login() {
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
                Type responseType = new TypeToken<ResponseWrapper<LoginResponse>>() {}.getType();
                return getContent(loginResponse, responseType);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private <T> T getContent(ContentResponse loginResponse, Type dataType) {
        String content = loginResponse.getContentAsString();
        ResponseWrapper<T> response = gson.fromJson(content, dataType);
        if (response.isSuccess()) {
            return response.getData();
        } else {
            return null;
        }
    }

    public AuthCodeResponse getAuthCode(LoginResponse loginResponse) {
        try {
            httpClient.start();
            HashMap<String, String> authCodeParameters = new HashMap<>();
            authCodeParameters.put("uid", loginResponse.getUid());
            authCodeParameters.put("accessToken", loginResponse.getAccessToken());
            authCodeParameters.put("bizType", "ECOVACS_IOT");
            authCodeParameters.put("deviceId", configuration.getDeviceId());
            HashMap<String, String> signedRequestParameters = getSignedAuthCodeRequestParameters(authCodeParameters);

            String authCodeUrl = EcovacsApiUrlFactory.getAuthCodeUrl(configuration.getCountry());

            Request authCodeRequest = httpClient.newRequest(authCodeUrl).method(HttpMethod.GET);
            signedRequestParameters.forEach(authCodeRequest::param);
            ContentResponse authCodeResponse = authCodeRequest.send();

            httpClient.stop();

            if (authCodeResponse.getStatus() == HttpStatus.OK_200) {
                Type responseType = new TypeToken<ResponseWrapper<AuthCodeResponse>>() {}.getType();
                return getContent(authCodeResponse, responseType);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public LoginAcknowledgementResponse acknowledgeLogin(AuthCodeResponse authCodeResponse, LoginResponse loginResponse) {
        try {
            httpClient.start();
            HashMap<String, String> acknowledgementParameters = new HashMap<>();
            acknowledgementParameters.put("edition","ECOGLOBLE");
            acknowledgementParameters.put("userId", loginResponse.getUid());
            acknowledgementParameters.put("token", authCodeResponse.getAuthCode());
            acknowledgementParameters.put("realm","ecouser.net");
            acknowledgementParameters.put("resource", configuration.getDeviceId().substring(0,8));
            acknowledgementParameters.put("org","ECOWW");
            acknowledgementParameters.put("last","");
            acknowledgementParameters.put("country",configuration.getCountry());
            acknowledgementParameters.put("todo", "loginByItToken");

            String userUrl = EcovacsApiUrlFactory.getUsersUrl(configuration.getContinent());

            Request authCodeRequest = httpClient.newRequest(userUrl).method(HttpMethod.GET);
            acknowledgementParameters.forEach(authCodeRequest::param);
            ContentResponse acknowledgementResponse = authCodeRequest.send();

            httpClient.stop();

            if (acknowledgementResponse.getStatus() == HttpStatus.OK_200) {
                return getLoginAcknowledgementResponse(acknowledgementResponse);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private LoginAcknowledgementResponse getLoginAcknowledgementResponse(ContentResponse acknowledgementResponse) {
        String content = acknowledgementResponse.getContentAsString();
        LoginAcknowledgementResponse response = gson.fromJson(content, LoginAcknowledgementResponse.class);
        if(Objects.equals(response.getResult(), "ok")) {
            return response;
        } else {
            return null;
        }
    }

    public String getDevices(LoginAcknowledgementResponse loginAcknowledgementResponse) {
        try {
            httpClient.start();
            PortalAuthRequestParameter authRequestParameter = new PortalAuthRequestParameter(
                    "users",
                    loginAcknowledgementResponse.getUserId(),
                    "ecouser.net",
                    loginAcknowledgementResponse.getToken(),
                    configuration.getDeviceId().substring(0,8)

            );
            PortalRequest data = new PortalRequest(
                    "GetDeviceList",
                    loginAcknowledgementResponse.getUserId(),
                    authRequestParameter
            );
            String json = gson.toJson(data);
            String userUrl = EcovacsApiUrlFactory.getUsersUrl(configuration.getContinent());

            Request authCodeRequest = httpClient.newRequest(userUrl)
                    .method(HttpMethod.POST)
                    .headers(httpFields -> httpFields.add(HttpHeader.CONTENT_TYPE, "application/json"))
                    .body(new StringRequestContent(json));
            ContentResponse deviceResponse = authCodeRequest.send();

            httpClient.stop();

            if (deviceResponse.getStatus() == HttpStatus.OK_200) {
                return deviceResponse.getContentAsString();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
