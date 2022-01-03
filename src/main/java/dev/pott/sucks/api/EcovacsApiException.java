package dev.pott.sucks.api;

import java.io.IOException;

import org.eclipse.jetty.client.api.Response;

public class EcovacsApiException extends IOException {
    public EcovacsApiException(String reason) {
        super(reason);
    }

    public EcovacsApiException(Response response) {
        super("HTTP status " + response.getStatus());
    }

    public EcovacsApiException(Throwable cause) {
        super(cause);
    }
}
