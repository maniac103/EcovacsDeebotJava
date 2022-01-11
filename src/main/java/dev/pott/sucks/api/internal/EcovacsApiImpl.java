package dev.pott.sucks.api.internal;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import dev.pott.sucks.api.EcovacsApi;
import dev.pott.sucks.api.EcovacsApiConfiguration;
import dev.pott.sucks.api.EcovacsApiException;
import dev.pott.sucks.api.EcovacsDevice;
import dev.pott.sucks.api.commands.IotDeviceCommand;
import dev.pott.sucks.api.internal.dto.request.portal.*;
import dev.pott.sucks.api.internal.dto.response.main.AccessData;
import dev.pott.sucks.api.internal.dto.response.main.AuthCode;
import dev.pott.sucks.api.internal.dto.response.main.ResponseWrapper;
import dev.pott.sucks.api.internal.dto.response.portal.*;
import dev.pott.sucks.api.util.MD5Util;

@NonNullByDefault
public final class EcovacsApiImpl implements EcovacsApi {
    private final Logger logger = LoggerFactory.getLogger(EcovacsApi.class);

    private final HttpClient httpClient;
    private final Gson gson = new Gson();

    private final EcovacsApiConfiguration configuration;
    private final Map<String, String> meta = new HashMap<>();
    private @Nullable PortalLoginResponse loginData;

    public EcovacsApiImpl(HttpClient httpClient, EcovacsApiConfiguration configuration) {
        this.httpClient = httpClient;
        this.configuration = configuration;

        meta.put(RequestQueryParameter.META_COUNTRY, configuration.getCountry());
        meta.put(RequestQueryParameter.META_LANG, configuration.getLanguage());
        meta.put(RequestQueryParameter.META_DEVICE_ID, configuration.getDeviceId());
        meta.put(RequestQueryParameter.META_APP_CODE, configuration.getAppCode());
        meta.put(RequestQueryParameter.META_APP_VERSION, configuration.getAppVersion());
        meta.put(RequestQueryParameter.META_CHANNEL, configuration.getChannel());
        meta.put(RequestQueryParameter.META_DEVICE_TYPE, configuration.getDeviceType());
    }

    @Override
    public void loginAndGetAccessToken() throws EcovacsApiException {
        loginData = null;

        AccessData accessData = login();
        AuthCode authCode = getAuthCode(accessData);
        loginData = portalLogin(authCode, accessData);
    }

    EcovacsApiConfiguration getConfig() {
        return configuration;
    }

    @Nullable
    PortalLoginResponse getLoginData() {
        return loginData;
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
                configuration.getAuthClientKey(), configuration.getAuthClientSecret());
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
        PortalLoginResponse response = handleResponse(portalLoginResponse, PortalLoginResponse.class);
        if (!response.wasSuccessful()) {
            throw new EcovacsApiException("Login failed");
        }
        return response;
    }

    @Override
    public List<EcovacsDevice> getDevices() throws EcovacsApiException {
        List<DeviceDescription> descriptions = getSupportedDeviceList();
        List<IotProduct> products = null;
        List<EcovacsDevice> devices = new ArrayList<>();
        for (Device dev : getDeviceList()) {
            Optional<DeviceDescription> descOpt = descriptions.stream()
                    .filter(d -> dev.getDeviceClass().equals(d.deviceClass)).findFirst();
            if (!descOpt.isPresent()) {
                if (products == null) {
                    products = getIotProductMap();
                }
                Optional<IotProduct> product = products.stream()
                        .filter(prod -> dev.getDeviceClass().equals(prod.getClassId())).findFirst();
                logger.info("Found unsupported device {} (class {}), ignoring.",
                        product.isPresent() ? product.get().getDefinition().name : "UNKNOWN", dev.getDeviceClass());
                continue;
            }
            DeviceDescription desc = descOpt.get();
            if (desc.usesMqtt) {
                devices.add(new EcovacsIotMqDevice(dev, desc, this, gson));
            } else {
                // TODO: XMPP device
            }
        }
        return devices;
    }

    private List<DeviceDescription> getSupportedDeviceList() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("devices/supported_device_list.json");
        JsonReader reader = new JsonReader(new InputStreamReader(is));
        Type type = new TypeToken<List<DeviceDescription>>() {
        }.getType();
        List<DeviceDescription> descs = gson.fromJson(reader, type);
        return descs.stream().map(desc -> {
            final DeviceDescription result;
            if (desc.deviceClassLink != null) {
                Optional<DeviceDescription> linkedDescOpt = descs.stream()
                        .filter(d -> d.deviceClass.equals(desc.deviceClassLink)).findFirst();
                if (!linkedDescOpt.isPresent()) {
                    throw new IllegalStateException(
                            "Desc " + desc.deviceClass + " links unknown desc " + desc.deviceClassLink);
                }
                result = desc.resolveLinkWith(linkedDescOpt.get());
            } else {
                result = desc;
            }
            return result;
        }).collect(Collectors.toList());
    }

    private List<Device> getDeviceList() throws EcovacsApiException {
        PortalAuthRequest data = new PortalAuthRequest(PortalTodo.GET_DEVICE_LIST, createAuthData());
        String json = gson.toJson(data);
        String userUrl = EcovacsApiUrlFactory.getPortalUsersUrl(configuration.getContinent());
        Request deviceRequest = httpClient.newRequest(userUrl).method(HttpMethod.POST)
                .header(HttpHeader.CONTENT_TYPE, "application/json").content(new StringContentProvider(json));
        ContentResponse deviceResponse = executeRequest(deviceRequest);
        return handleResponse(deviceResponse, PortalDeviceResponse.class).getDevices();
    }

    private List<IotProduct> getIotProductMap() throws EcovacsApiException {
        PortalIotProductRequest data = new PortalIotProductRequest(createAuthData());
        String json = gson.toJson(data);
        String url = EcovacsApiUrlFactory.getPortalProductIotMapUrl(configuration.getContinent());
        Request deviceRequest = httpClient.newRequest(url).method(HttpMethod.POST)
                .header(HttpHeader.CONTENT_TYPE, "application/json").content(new StringContentProvider(json));
        ContentResponse deviceResponse = executeRequest(deviceRequest);
        return handleResponse(deviceResponse, PortalIotProductResponse.class).getProducts();
    }

    public <T> T sendIotCommand(Device device, DeviceDescription desc, IotDeviceCommand<T> command)
            throws EcovacsApiException {
        boolean useJson = desc.usesJsonApi && !command.forceXmlFormat();
        final Object payload;
        try {
            payload = useJson ? command.getJsonPayload(gson) : command.getXmlPayload();
        } catch (Exception e) {
            logger.debug("Could not convert payload for command " + command, e);
            throw new EcovacsApiException(e);
        }

        PortalIotCommandRequest data = new PortalIotCommandRequest(createAuthData(), command.getName(!useJson), payload,
                device.getDid(), device.getResource(), device.getDeviceClass(), useJson);
        String json = gson.toJson(data);
        String url = EcovacsApiUrlFactory.getPortalIotDeviceManagerUrl(configuration.getContinent());
        Request request = httpClient.newRequest(url).method(HttpMethod.POST)
                .header(HttpHeader.CONTENT_TYPE, "application/json").content(new StringContentProvider(json));
        ContentResponse response = executeRequest(request);

        logger.trace("Sent IOT command " + json);
        logger.trace("Got response " + response.getContentAsString());

        final AbstractPortalIotCommandResponse commandResponse;
        if (useJson) {
            commandResponse = handleResponse(response, PortalIotCommandJsonResponse.class);
        } else {
            commandResponse = handleResponse(response, PortalIotCommandXmlResponse.class);
        }
        if (!commandResponse.wasSuccessful()) {
            throw new EcovacsApiException("Sending IOT command " + command.getName(!useJson) + " failed: "
                    + commandResponse.getFailureMessage());
        }
        try {
            return command.convertResponse(commandResponse, gson);
        } catch (Exception e) {
            logger.debug("Converting response for command " + command + " failed", e);
            throw new EcovacsApiException(e);
        }
    }

    private PortalAuthRequestParameter createAuthData() {
        PortalLoginResponse loginData = this.loginData;
        if (loginData == null) {
            throw new IllegalStateException("Not logged in");
        }
        return new PortalAuthRequestParameter(configuration.getPortalAUthRequestWith(), loginData.getUserId(),
                configuration.getRealm(), loginData.getToken(), configuration.getDeviceId().substring(0, 8));
    }

    private <T> T handleResponseWrapper(@Nullable ResponseWrapper<T> response) throws EcovacsApiException {
        if (response == null) {
            // should not happen in practice
            throw new EcovacsApiException("No response received");
        }
        if (!response.isSuccess()) {
            throw new EcovacsApiException("API call failed: " + response.getMessage() + ", code " + response.getCode());
        }
        return response.getData();
    }

    private <T> T handleResponse(ContentResponse response, Class<T> clazz) throws EcovacsApiException {
        @Nullable
        T respObject = gson.fromJson(response.getContentAsString(), clazz);
        if (respObject == null) {
            // should not happen in practice
            throw new EcovacsApiException("No response received");
        }
        return respObject;
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
        return getSignedRequestParameters(requestSpecificParameters, configuration.getClientKey(),
                configuration.getClientSecret());
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
