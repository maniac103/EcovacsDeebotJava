package dev.pott.sucks;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.GsonBuilder;
import dev.pott.sucks.api.EcovacsApi;
import dev.pott.sucks.api.EcovacsApiConfiguration;
import dev.pott.sucks.api.dto.response.main.AccessData;
import dev.pott.sucks.api.dto.response.main.AuthCode;
import dev.pott.sucks.api.dto.response.portal.Device;
import dev.pott.sucks.api.dto.response.portal.IotProduct;
import dev.pott.sucks.api.dto.response.portal.PortalDeviceResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotProductResponse;
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
        AccessData accessDataResponse = api.login();
        if (accessDataResponse != null) {
            AuthCode authCodeResponse = api.getAuthCode(accessDataResponse);
            if (authCodeResponse != null) {
                PortalLoginResponse acknowledgementResponse = api.portalLogin(authCodeResponse, accessDataResponse);
                if (acknowledgementResponse != null) {
                    PortalDeviceResponse devices = api.getDevices(acknowledgementResponse);
                    System.out.println(devices);
                    PortalIotProductResponse products = api.getIotProductMap(acknowledgementResponse);
                    System.out.println(products);

                    for (Device dev : devices.getDevices()) {
                        List<IotProduct> matchingProducts = products.getProducts()
                                .stream()
                                .filter(prod -> dev.getDeviceClass().equals(prod.getClassId()))
                                .collect(Collectors.toList());
                        if (matchingProducts.isEmpty()) {
                            System.out.println("Did not find device class for " + dev.getName());
                        } else {
                            System.out.println("Device " + dev.getName() + " is a " + matchingProducts.get(0).getDefinition().name);
                        }
                    }
                }
            }
        }
    }
}
