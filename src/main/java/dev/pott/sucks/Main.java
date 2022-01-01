package dev.pott.sucks;

import com.google.gson.GsonBuilder;
import dev.pott.sucks.api.EcovacsApi;
import dev.pott.sucks.api.EcovacsApiConfiguration;
import dev.pott.sucks.api.dto.AuthCodeResponse;
import dev.pott.sucks.api.dto.LoginAcknowledgementResponse;
import dev.pott.sucks.api.dto.LoginResponse;
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
                new HttpClient(),
                new GsonBuilder().create(),
                new EcovacsApiConfiguration(
                        MD5Util.getMD5Hash(String.valueOf(System.currentTimeMillis())),
                        "user",
                        "password",
                        "EU",
                        "DE"
                )
        );
        LoginResponse loginResponse = api.login();
        AuthCodeResponse authCodeResponse = api.getAuthCode(loginResponse);
        LoginAcknowledgementResponse acknowledgementResponse = api.acknowledgeLogin(authCodeResponse, loginResponse);
        System.out.println(authCodeResponse);
    }
}
