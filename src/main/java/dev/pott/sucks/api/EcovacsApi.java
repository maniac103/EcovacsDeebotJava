package dev.pott.sucks.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.pott.sucks.api.dto.request.portal.PortalAuthRequest;
import dev.pott.sucks.api.dto.request.portal.PortalAuthRequestParameter;
import dev.pott.sucks.api.dto.request.portal.PortalIotProductRequest;
import dev.pott.sucks.api.dto.request.portal.PortalLoginRequest;
import dev.pott.sucks.api.dto.response.main.AccessData;
import dev.pott.sucks.api.dto.response.main.AuthCode;
import dev.pott.sucks.api.dto.response.main.ResponseWrapper;
import dev.pott.sucks.api.dto.response.portal.PortalDeviceResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotProductResponse;
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
import java.util.stream.Collectors;

public final class EcovacsApi {

    private final HttpClient httpClient;
    private final Gson gson;
    private final EcovacsApiConfiguration configuration;
    private final Map<String, String> meta = new HashMap<>();

    public EcovacsApi(HttpClient httpClient, Gson gson, EcovacsApiConfiguration configuration) {
        this.httpClient = httpClient;
        this.gson = gson;
        this.configuration = configuration;

        meta.put(RequestQueryParameter.META_COUNTRY, configuration.getCountry());
        meta.put(RequestQueryParameter.META_LANG, configuration.getLanguage());
        meta.put(RequestQueryParameter.META_DEVICE_ID, configuration.getDeviceId());
        meta.put(RequestQueryParameter.META_APP_CODE, configuration.getAppCode());
        meta.put(RequestQueryParameter.META_APP_VERSION, configuration.getAppVersion());
        meta.put(RequestQueryParameter.META_CHANNEL, configuration.getChannel());
        meta.put(RequestQueryParameter.META_DEVICE_TYPE, configuration.getDeviceType());
    }

    public AccessData login() {
        try {
            httpClient.start();

            // Generate login Params
            HashMap<String, String> loginParameters = new HashMap<>();
            loginParameters.put(RequestQueryParameter.AUTH_ACCOUNT, configuration.getUsername());
            loginParameters.put(RequestQueryParameter.AUTH_PASSWORD, MD5Util.getMD5Hash(configuration.getPassword()));
            loginParameters.put(
                    RequestQueryParameter.AUTH_REQUEST_ID,
                    MD5Util.getMD5Hash(String.valueOf(System.currentTimeMillis()))
            );
            loginParameters.put(RequestQueryParameter.AUTH_TIME_ZONE, configuration.getTimeZone());
            loginParameters.putAll(meta);
            HashMap<String, String> signedRequestParameters = getSignedRequestParameters(loginParameters);

            String loginUrl = EcovacsApiUrlFactory.getLoginUrl(
                    configuration.getCountry(),
                    configuration.getLanguage(),
                    configuration.getDeviceId(),
                    configuration.getAppCode(),
                    configuration.getAppVersion(),
                    configuration.getChannel(),
                    configuration.getDeviceType()
            );

            Request loginRequest = httpClient.newRequest(loginUrl).method(HttpMethod.GET);
            signedRequestParameters.forEach(loginRequest::param);
            ContentResponse loginResponse = loginRequest.send();

            httpClient.stop();

            if (loginResponse.getStatus() == HttpStatus.OK_200) {
                Type responseType = new TypeToken<ResponseWrapper<AccessData>>() {
                }.getType();
                ResponseWrapper<AccessData> response = gson.fromJson(loginResponse.getContentAsString(), responseType);
                if (response.isSuccess()) {
                    return response.getData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public AuthCode getAuthCode(AccessData accessData) {
        try {
            httpClient.start();
            HashMap<String, String> authCodeParameters = new HashMap<>();
            authCodeParameters.put(RequestQueryParameter.AUTH_CODE_UID, accessData.getUid());
            authCodeParameters.put(RequestQueryParameter.AUTH_CODE_ACCESS_TOKEN, accessData.getAccessToken());
            authCodeParameters.put(RequestQueryParameter.AUTH_CODE_BIZ_TYPE, configuration.getBizType());
            authCodeParameters.put(RequestQueryParameter.AUTH_CODE_DEVICE_ID, configuration.getDeviceId());
            authCodeParameters.put(RequestQueryParameter.AUTH_OPEN_ID, configuration.getAuthOpenId());

            HashMap<String, String> signedRequestParameters = getSignedRequestParameters(authCodeParameters,
                    ClientKeys.AUTH_CLIENT_KEY, ClientKeys.AUTH_CLIENT_SECRET);

            String authCodeUrl = EcovacsApiUrlFactory.getAuthUrl(configuration.getCountry());

            Request authCodeRequest = httpClient.newRequest(authCodeUrl).method(HttpMethod.GET);
            signedRequestParameters.forEach(authCodeRequest::param);
            ContentResponse authCodeResponse = authCodeRequest.send();

            httpClient.stop();

            if (authCodeResponse.getStatus() == HttpStatus.OK_200) {
                Type responseType = new TypeToken<ResponseWrapper<AuthCode>>() {
                }.getType();
                ResponseWrapper<AuthCode> response = gson.fromJson(authCodeResponse.getContentAsString(), responseType);
                if (response.isSuccess()) {
                    return response.getData();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PortalLoginResponse portalLogin(AuthCode authCode, AccessData accessData) {
        try {
            httpClient.start();
            PortalLoginRequest loginRequestData = new PortalLoginRequest(
                    PortalTodo.LOGIN_BY_TOKEN,
                    configuration.getCountry(),
                    "",
                    configuration.getOrg(),
                    configuration.getResource(),
                    configuration.getRealm(),
                    authCode.getAuthCode(),
                    accessData.getUid(),
                    configuration.getEdition()
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
                PortalLoginResponse response = gson.fromJson(portalLoginResponse.getContentAsString(), PortalLoginResponse.class);
                if (response.wasSuccessful()) {
                    return response;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PortalDeviceResponse getDevices(PortalLoginResponse portalLoginResponse) {
        try {
            httpClient.start();
            PortalAuthRequestParameter deviceRequestData = new PortalAuthRequestParameter(
                    configuration.getPortalAUthRequestWith(),
                    portalLoginResponse.getUserId(),
                    configuration.getRealm(),
                    portalLoginResponse.getToken(),
                    configuration.getDeviceId().substring(0, 8)

            );
            PortalAuthRequest data = new PortalAuthRequest(
                    PortalTodo.GET_DEVICE_LIST,
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

    public PortalIotProductResponse getIotProductMap(PortalLoginResponse portalLoginResponse) {
        try {
            httpClient.start();
            PortalAuthRequestParameter deviceRequestData = new PortalAuthRequestParameter(
                    configuration.getPortalAUthRequestWith(),
                    portalLoginResponse.getUserId(),
                    configuration.getRealm(),
                    portalLoginResponse.getToken(),
                    configuration.getDeviceId().substring(0, 8)
            );
            PortalIotProductRequest data = new PortalIotProductRequest(deviceRequestData);
            String json = gson.toJson(data);
            String url = EcovacsApiUrlFactory.getPortalProductIotMapUrl(configuration.getContinent());
            Request deviceRequest = httpClient.newRequest(url)
                    .method(HttpMethod.POST)
                    .headers(httpFields -> httpFields.add(HttpHeader.CONTENT_TYPE, "application/json"))
                    .body(new StringRequestContent(json));
            ContentResponse deviceResponse = deviceRequest.send();
            httpClient.stop();
            if (deviceResponse.getStatus() == HttpStatus.OK_200) {
                return gson.fromJson(
                        deviceResponse.getContentAsString(),
                        PortalIotProductResponse.class
                );
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private HashMap<String, String> getSignedRequestParameters(Map<String, String> requestSpecificParameters) {
        return getSignedRequestParameters(requestSpecificParameters, ClientKeys.CLIENT_KEY, ClientKeys.SECRET);
    }

    private HashMap<String, String> getSignedRequestParameters(Map<String, String> requestSpecificParameters,
                                                               String clientKey,
                                                               String clientSecret) {
        HashMap<String, String> signedRequestParameters = new HashMap<>(requestSpecificParameters);
        signedRequestParameters.put(RequestQueryParameter.AUTH_TIMESPAN, String.valueOf(System.currentTimeMillis()));

        StringBuilder signOnText = new StringBuilder(clientKey);
        signedRequestParameters.keySet().stream().sorted().forEach(key -> {
            signOnText.append(key).append("=").append(signedRequestParameters.get(key));
        });
        signOnText.append(clientSecret);

        signedRequestParameters.put(RequestQueryParameter.AUTH_APPKEY, clientKey);
        signedRequestParameters.put(RequestQueryParameter.AUTH_SIGN, MD5Util.getMD5Hash(signOnText.toString()));
        return signedRequestParameters;
    }

}
