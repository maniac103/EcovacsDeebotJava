package dev.pott.sucks;

import com.google.gson.GsonBuilder;
import dev.pott.sucks.api.EcovacsApi;
import dev.pott.sucks.api.EcovacsApiConfiguration;
import dev.pott.sucks.api.dto.response.main.AccessData;
import dev.pott.sucks.api.dto.response.main.AuthCode;
import dev.pott.sucks.api.dto.response.main.ResponseWrapper;
import dev.pott.sucks.api.dto.response.portal.PortalDeviceResponse;
import dev.pott.sucks.api.dto.response.portal.PortalLoginResponse;
import dev.pott.sucks.util.MD5Util;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.dynamic.HttpClientTransportDynamic;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class Main {

    public static void main(String[] args) {
        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
        sslContextFactory.setTrustAll(true);
        ClientConnector clientConnector = new ClientConnector();
        clientConnector.setSslContextFactory(sslContextFactory);
        HttpClient httpClient = new HttpClient(new HttpClientTransportDynamic(clientConnector));
        httpClient.setConnectTimeout(60);
        EcovacsApi api = new EcovacsApi(
                httpClient,
                new GsonBuilder().create(),
                new EcovacsApiConfiguration(
                        MD5Util.getMD5Hash(String.valueOf(System.currentTimeMillis())),
                        "user",
                        "password",
                        "EU",
                        "DE",
                        "EN"
                )
        );
        ResponseWrapper<AccessData> accessDataResponse = api.login();
        if (accessDataResponse != null && accessDataResponse.isSuccess()) {
            authenticate(api, accessDataResponse.getData());
        }
    }

    private static void authenticate(EcovacsApi api, AccessData accessDataResponse) {
        ResponseWrapper<AuthCode> authCodeResponse = api.getAuthCode(accessDataResponse);
        if (authCodeResponse != null && authCodeResponse.isSuccess()) {
            requestDevices(api, accessDataResponse, authCodeResponse.getData());
        }
    }

    private static void requestDevices(
            EcovacsApi api,
            AccessData accessDataResponse,
            AuthCode authCodeResponse
    ) {
        PortalLoginResponse acknowledgementResponse = api.portalLogin(authCodeResponse, accessDataResponse);
        if (acknowledgementResponse != null) {
            PortalDeviceResponse devices = api.getDevices(acknowledgementResponse);
            System.out.println(devices);
        }
    }
}
