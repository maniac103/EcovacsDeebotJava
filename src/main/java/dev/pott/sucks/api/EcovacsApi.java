package dev.pott.sucks.api;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import dev.pott.sucks.api.dto.request.commands.IotDeviceCommand;
import dev.pott.sucks.api.dto.request.portal.PortalAuthRequest;
import dev.pott.sucks.api.dto.request.portal.PortalAuthRequestParameter;
import dev.pott.sucks.api.dto.request.portal.PortalIotCommandRequest;
import dev.pott.sucks.api.dto.request.portal.PortalIotProductRequest;
import dev.pott.sucks.api.dto.request.portal.PortalLoginRequest;
import dev.pott.sucks.api.dto.response.main.AccessData;
import dev.pott.sucks.api.dto.response.main.AuthCode;
import dev.pott.sucks.api.dto.response.main.ResponseWrapper;
import dev.pott.sucks.api.dto.response.portal.Device;
import dev.pott.sucks.api.dto.response.portal.IotProduct;
import dev.pott.sucks.api.dto.response.portal.PortalDeviceResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotProductResponse;
import dev.pott.sucks.api.dto.response.portal.PortalLoginResponse;
import dev.pott.sucks.util.MD5Util;

public final class EcovacsApi {

    private final HttpClient httpClient;
    private final Gson gson;
    private final EcovacsApiConfiguration configuration;
    private final Map<String, String> meta = new HashMap<>();
    private PortalLoginResponse loginData;

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

    public void loginAndGetAccessToken() throws EcovacsApiException {
        loginData = null;

        AccessData accessData = login();
        if (accessData != null) {
            AuthCode authCode = getAuthCode(accessData);
            if (authCode != null) {
                loginData = portalLogin(authCode, accessData);
            }
        }
    }

    public boolean isLoggedIn() {
        return loginData != null;
    }

    private AccessData login() throws EcovacsApiException {
        // Generate login Params
        HashMap<String, String> loginParameters = new HashMap<>();
        loginParameters.put(RequestQueryParameter.AUTH_ACCOUNT, configuration.getUsername());
        loginParameters.put(RequestQueryParameter.AUTH_PASSWORD, MD5Util.getMD5Hash(configuration.getPassword()));
        loginParameters.put(RequestQueryParameter.AUTH_REQUEST_ID,
                MD5Util.getMD5Hash(String.valueOf(System.currentTimeMillis())));
        loginParameters.put(RequestQueryParameter.AUTH_TIME_ZONE, configuration.getTimeZone());
        loginParameters.putAll(meta);

        HashMap<String, String> signedRequestParameters = getSignedRequestParameters(loginParameters);
        String loginUrl = EcovacsApiUrlFactory.getLoginUrl(configuration.getCountry(), configuration.getLanguage(),
                configuration.getDeviceId(), configuration.getAppCode(), configuration.getAppVersion(),
                configuration.getChannel(), configuration.getDeviceType());
        Request loginRequest = httpClient.newRequest(loginUrl).method(HttpMethod.GET);
        signedRequestParameters.forEach(loginRequest::param);

        ContentResponse loginResponse = executeRequest(loginRequest);
        Type responseType = new TypeToken<ResponseWrapper<AccessData>>() {
        }.getType();
        return handleResponseWrapper(gson.fromJson(loginResponse.getContentAsString(), responseType));
    }

    private AuthCode getAuthCode(AccessData accessData) throws EcovacsApiException {
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

        ContentResponse authCodeResponse = executeRequest(authCodeRequest);
        Type responseType = new TypeToken<ResponseWrapper<AuthCode>>() {
        }.getType();
        return handleResponseWrapper(gson.fromJson(authCodeResponse.getContentAsString(), responseType));
    }

    private PortalLoginResponse portalLogin(AuthCode authCode, AccessData accessData) throws EcovacsApiException {
        PortalLoginRequest loginRequestData = new PortalLoginRequest(PortalTodo.LOGIN_BY_TOKEN,
                configuration.getCountry(), "", configuration.getOrg(), configuration.getResource(),
                configuration.getRealm(), authCode.getAuthCode(), accessData.getUid(), configuration.getEdition());
        String json = gson.toJson(loginRequestData);
        String userUrl = EcovacsApiUrlFactory.getPortalUsersUrl(configuration.getContinent());
        Request loginRequest = httpClient.newRequest(userUrl).method(HttpMethod.POST)
                .header(HttpHeader.CONTENT_TYPE, "application/json").content(new StringContentProvider(json));
        ContentResponse portalLoginResponse = executeRequest(loginRequest);
        PortalLoginResponse response = gson.fromJson(portalLoginResponse.getContentAsString(),
                PortalLoginResponse.class);
        if (!response.wasSuccessful()) {
            throw new EcovacsApiException("Login failed");
        }
        return response;
    }

    public List<Device> getDevices() throws EcovacsApiException {
        PortalAuthRequest data = new PortalAuthRequest(PortalTodo.GET_DEVICE_LIST, loginData.getUserId(), createAuthData());
        String json = gson.toJson(data);
        String userUrl = EcovacsApiUrlFactory.getPortalUsersUrl(configuration.getContinent());
        Request deviceRequest = httpClient.newRequest(userUrl).method(HttpMethod.POST)
                .header(HttpHeader.CONTENT_TYPE, "application/json").content(new StringContentProvider(json));
        ContentResponse deviceResponse = executeRequest(deviceRequest);
        return gson.fromJson(deviceResponse.getContentAsString(), PortalDeviceResponse.class).getDevices();
    }

    public List<IotProduct> getIotProductMap() throws EcovacsApiException {
        PortalIotProductRequest data = new PortalIotProductRequest(createAuthData());
        String json = gson.toJson(data);
        String url = EcovacsApiUrlFactory.getPortalProductIotMapUrl(configuration.getContinent());
        Request deviceRequest = httpClient.newRequest(url).method(HttpMethod.POST)
                .header(HttpHeader.CONTENT_TYPE, "application/json").content(new StringContentProvider(json));
        ContentResponse deviceResponse = executeRequest(deviceRequest);
        return gson.fromJson(deviceResponse.getContentAsString(), PortalIotProductResponse.class).getProducts();
    }

    public <T> T sendIotCommand(Device device, IotDeviceCommand<T> command) throws EcovacsApiException {
        PortalIotCommandRequest data = new PortalIotCommandRequest(createAuthData(),
                command.getName(), command.getPayloadXml(),
                device.getDid(), device.getResource(), device.getDeviceClass());
        String json = gson.toJson(data);
        String url = EcovacsApiUrlFactory.getPortalIotDeviceManagerUrl(configuration.getContinent());
        Request request = httpClient.newRequest(url).method(HttpMethod.POST)
                .header(HttpHeader.CONTENT_TYPE, "application/json").content(new StringContentProvider(json));
        ContentResponse response = executeRequest(request);
        PortalIotCommandResponse commandResponse = gson.fromJson(response.getContentAsString(), PortalIotCommandResponse.class);
        if (!commandResponse.wasSuccessful()) {
            throw new EcovacsApiException("Sending IOT command " + command.getName() + " failed: " + commandResponse.getFailureMessage());
        }
        try {
            return command.convertResponse(commandResponse.getResponsePayload(), gson);
        } catch (Exception e) {
            throw new EcovacsApiException(e);
        }
    }

    private PortalAuthRequestParameter createAuthData() {
        PortalLoginResponse loginData = this.loginData;
        if (loginData == null) {
            throw new IllegalStateException("Not logged in");
        }
        return new PortalAuthRequestParameter(
                configuration.getPortalAUthRequestWith(), loginData.getUserId(), configuration.getRealm(),
                loginData.getToken(), configuration.getDeviceId().substring(0, 8));
    }

    private <T> T handleResponseWrapper(ResponseWrapper<T> response) throws EcovacsApiException {
        if (!response.isSuccess()) {
            throw new EcovacsApiException("API call failed: " + response.getMessage() + ", code " + response.getCode());
        }
        return response.getData();
    }

    private ContentResponse executeRequest(Request request) throws EcovacsApiException {
        try {
            ContentResponse response = request.send();
            if (response.getStatus() != HttpStatus.OK_200) {
                throw new EcovacsApiException(response);
            }
            return response;
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new EcovacsApiException(e);
        }
    }

    private HashMap<String, String> getSignedRequestParameters(Map<String, String> requestSpecificParameters) {
        return getSignedRequestParameters(requestSpecificParameters, ClientKeys.CLIENT_KEY, ClientKeys.SECRET);
    }

    private HashMap<String, String> getSignedRequestParameters(Map<String, String> requestSpecificParameters,
            String clientKey, String clientSecret) {
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
