package dev.pott.sucks.api;

import java.util.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jetty.client.HttpClient;

import dev.pott.sucks.api.internal.EcovacsApiImpl;

@NonNullByDefault
public interface EcovacsApi {
    public static EcovacsApi create(HttpClient httpClient, EcovacsApiConfiguration configuration) {
        return new EcovacsApiImpl(httpClient, configuration);
    }

    public void loginAndGetAccessToken() throws EcovacsApiException;

    public List<EcovacsDevice> getDevices() throws EcovacsApiException;
}
