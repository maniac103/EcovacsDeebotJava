package dev.pott.sucks.api;

import com.google.gson.Gson;
import dev.pott.sucks.api.dto.LoginRequest;
import dev.pott.sucks.util.MD5Util;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class EcovacsApi {

    private static final String CLIENT_KEY = "1520391301804";
    private static final String SECRET = "6c319b2a5cd3e66e39159c2e28f2fce9";

    private static final String AUTH_CLIENT_KEY = "1520391491841";
    private static final String AUTH_CLIENT_SECRET = "77ef58ce3afbe337da74aa8c5ab963a9";

    private final HttpClient httpClient;
    private final Gson gson;
    private final EcovacsApiConfiguration configuration;

    public EcovacsApi(HttpClient httpClient, Gson gson, EcovacsApiConfiguration configuration) {
        this.httpClient = httpClient;
        this.gson = gson;
        this.configuration = configuration;
    }

    /**
     * Login at Ecovacs API
     *
     * @return true when login was successful
     */
    public boolean login() {
        try {
            httpClient.start();

            //Generate meta data
            HashMap<String, String> meta = new HashMap<>();
            meta.put("country", configuration.getCountry());
            meta.put("lang", "EN");
            meta.put("deviceId", configuration.getDeviceId());
            meta.put("appCode", "global_e");
            meta.put("appVersion", "1.6.3");
            meta.put("channel", "google_play");
            meta.put("deviceType", "1");

            // Generate login Params
            HashMap<String, String> params = new HashMap<>();
            params.put("requestId", MD5Util.getMD5Hash(String.valueOf(System.currentTimeMillis())));
            params.put("authTimespan", String.valueOf(System.currentTimeMillis()));
            params.put("authTimeZone", "GMT-8");
            params.put("account", configuration.getUsername());
            params.put("password", configuration.getPasswordHash());

            // SIGNING
            HashMap<String, String> signOn = new HashMap<>(meta);
            signOn.putAll(params);
            StringBuilder signOnText = new StringBuilder(CLIENT_KEY);

            List<String> keys = signOn.keySet().stream().sorted().collect(Collectors.toList());
            for (String key : keys) {
                signOnText.append(key).append("=").append(signOn.get(key));
            }
            signOnText.append(SECRET);

            params.put("authAppkey", CLIENT_KEY);
            params.put("authSign", MD5Util.getMD5Hash(signOnText.toString()));

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
            params.forEach(loginRequest::param);
            ContentResponse loginResponse = loginRequest.send();

            httpClient.stop();

            // Read result
            String content = loginResponse.getContentAsString();
            System.out.println("Login response: " + content);
            return loginResponse.getStatus() == HttpStatus.OK_200;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
