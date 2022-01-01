package dev.pott.sucks;

import com.google.gson.GsonBuilder;
import dev.pott.sucks.api.EcovacsApi;
import dev.pott.sucks.api.EcovacsApiConfiguration;
import org.eclipse.jetty.client.HttpClient;

public class Main {

    public static void main(String[] args) {
        EcovacsApi api = new EcovacsApi(
                new HttpClient(),
                new GsonBuilder().create(),
                new EcovacsApiConfiguration(
                        "",
                        "",
                        "",
                        "eu",
                        "de"
                )
        );
        api.login();
    }
}
