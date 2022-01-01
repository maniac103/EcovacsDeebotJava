package dev.pott.sucks.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.pott.sucks.api.dto.LoginResponse;
import dev.pott.sucks.api.dto.ResponseWrapper;
import dev.pott.sucks.util.MD5Util;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpStatus;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * @return true when login was successful
     */
    public LoginResponse login() {
        try {
            httpClient.start();

            // Generate login Params
            HashMap<String, String> params = new HashMap<>();
            params.put("account", configuration.getUsername());
            params.put("password", MD5Util.getMD5Hash(configuration.getPassword()));
            HashMap<String, String> signedParams = getSignedParams(params);

            // SIGNING

            // Request
            String loginUrl = EcovacsApiUrlFactory.getLoginUrl(
                    configuration.getCountry(),
                    "EN",
                    configuration.getDeviceId(),
                    "global_e",
                    "1.6.3",
                    "google_play",
                    "1"
            );

            Request loginRequest = httpClient.newRequest(loginUrl);
            signedParams.forEach(loginRequest::param);
            ContentResponse loginResponse = loginRequest.send();

            httpClient.stop();

            if (loginResponse.getStatus() == HttpStatus.OK_200) {
                return getLoginResponse(loginResponse);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private LoginResponse getLoginResponse(ContentResponse loginResponse) {
        String content = loginResponse.getContentAsString();
        Type loginResponseWrapperType = new TypeToken<ResponseWrapper<LoginResponse>>() {
        }.getType();
        ResponseWrapper<LoginResponse> response = gson.fromJson(content, loginResponseWrapperType);
        if (response.isSuccess()) {
            return response.getData();
        } else {
            return null;
        }
    }

    private HashMap<String, String> getSignedParams(Map<String, String> params) {
        HashMap<String, String> signedParams = new HashMap<>();
        signedParams.put("requestId", MD5Util.getMD5Hash(String.valueOf(System.currentTimeMillis())));
        signedParams.put("authTimespan", String.valueOf(System.currentTimeMillis()));
        signedParams.put("authTimeZone", "GMT-8");
        signedParams.putAll(params);

        HashMap<String, String> signOn = new HashMap<>(meta);
        signOn.putAll(signedParams);
        StringBuilder signOnText = new StringBuilder(CLIENT_KEY);

        List<String> keys = signOn.keySet().stream().sorted().collect(Collectors.toList());
        for (String key : keys) {
            signOnText.append(key).append("=").append(signOn.get(key));
        }
        signOnText.append(SECRET);

        signedParams.put("authAppkey", CLIENT_KEY);
        signedParams.put("authSign", MD5Util.getMD5Hash(signOnText.toString()));
        return signedParams;
    }

}
