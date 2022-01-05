package dev.pott.sucks.api;

import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientSslConfig;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.auth.Mqtt3SimpleAuth;

import dev.pott.sucks.api.dto.request.commands.GetBatteryInfoCommand;
import dev.pott.sucks.api.dto.request.commands.GetChargeStateCommand;
import dev.pott.sucks.api.dto.request.commands.GetCleanStateCommand;
import dev.pott.sucks.api.dto.request.commands.GetFirmwareVersionCommand;
import dev.pott.sucks.api.dto.request.commands.IotDeviceCommand;
import dev.pott.sucks.api.dto.response.portal.Device;
import dev.pott.sucks.api.dto.response.portal.IotProduct.ProductDefinition;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandJsonResponse.JsonResponsePayloadWrapper;
import dev.pott.sucks.api.dto.response.portal.PortalLoginResponse;
import dev.pott.sucks.cleaner.ChargeMode;
import dev.pott.sucks.cleaner.CleanMode;
import io.netty.handler.ssl.util.SimpleTrustManagerFactory;

public class EcovacsIotMqDevice implements EcovacsDevice {
    private final Device device;
    private final ProductDefinition product;
    private final String firmwareVersion;
    private final EcovacsApi api;
    private final Gson gson;
    private final MessageHandler messageHandler;
    private Mqtt3AsyncClient mqttClient;
    private StateChangeListener listener;

    private int lastBatteryLevel;
    private boolean wasCharging;
    private CleanMode lastCleanMode;

    EcovacsIotMqDevice(Device device, ProductDefinition product, EcovacsApi api, Gson gson) throws EcovacsApiException {
        this.device = device;
        this.product = product;
        this.firmwareVersion = api.sendIotCommand(device, new GetFirmwareVersionCommand());
        this.api = api;
        this.gson = gson;
        this.messageHandler = device.usesJsonApi() ? new JsonMessageHandler() : new XmlMessageHandler();
    }

    @Override
    public String getId() {
        return device.getDid();
    }

    @Override
    public String getSerialNumber() {
        return device.getName();
    }

    @Override
    public String getModelName() {
        return product.name;
    }

    @Override
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    @Override
    public <T> T sendCommand(IotDeviceCommand<T> command) throws EcovacsApiException {
        return api.sendIotCommand(device, command);
    }

    @Override
    public void connect(final StateChangeListener listener) throws EcovacsApiException {
        EcovacsApiConfiguration config = api.getConfig();
        PortalLoginResponse loginData = api.getLoginData();
        if (loginData == null) {
            throw new EcovacsApiException("Can not connect when not logged in");
        }

        // TOOD: use realm from config
        String userName = loginData.getUserId() + "@ecouser";
        String host = String.format("mq-%s.ecouser.net", config.getContinent());

        Mqtt3SimpleAuth auth = Mqtt3SimpleAuth.builder().username(userName).password(loginData.getToken().getBytes())
                .build();

        MqttClientSslConfig sslConfig = MqttClientSslConfig.builder().trustManagerFactory(createTrustManagerFactory())
                .build();

        lastBatteryLevel = api.sendIotCommand(device, new GetBatteryInfoCommand());
        wasCharging = api.sendIotCommand(device, new GetChargeStateCommand()) == ChargeMode.CHARGING;
        lastCleanMode = api.sendIotCommand(device, new GetCleanStateCommand());

        listener.onBatteryLevelChanged(this, lastBatteryLevel);
        listener.onChargingStateChanged(this, wasCharging);
        listener.onCleaningModeChanged(this, lastCleanMode);

        mqttClient = MqttClient.builder().useMqttVersion3().identifier(userName + "/" + loginData.getResource())
                .simpleAuth(auth).serverHost(host).serverPort(8883).sslConfig(sslConfig).buildAsync();

        mqttClient.connect().whenComplete((connAck, connError) -> {
            if (connError != null) {
                handleMqttError(connError);
                return;
            }

            String topic = String.format("iot/atr/+/%s/%s/%s/+", device.getDid(), device.getDeviceClass(),
                    device.getResource());
            mqttClient.subscribeWith().topicFilter(topic).callback(publish -> {
                String payload = new String(publish.getPayloadAsBytes());
                messageHandler.handleMessage(publish.getTopic().toString(), payload);
            }).send().whenComplete((subAck, subError) -> {
                if (subError != null) {
                    handleMqttError(subError);
                } else {
                    this.listener = listener;
                }
            });
        });
    }

    public void disconnect() {
        if (mqttClient != null) {
            mqttClient.disconnect();
        }
    }

    private void handleMqttError(Throwable t) {
        // TODO: retry?
    }

    private TrustManagerFactory createTrustManagerFactory() {
        final TrustManager noOpTrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        return new SimpleTrustManagerFactory() {
            @Override
            protected void engineInit(KeyStore keyStore) throws Exception {
            }

            @Override
            protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {
            }

            @Override
            protected TrustManager[] engineGetTrustManagers() {
                return new TrustManager[] { noOpTrustManager };
            }
        };
    }

    private void handleBatteryLevelUpdate(int percent) {
        if (listener != null && percent != lastBatteryLevel) {
            lastBatteryLevel = percent;
            listener.onBatteryLevelChanged(this, percent);
        }
    }

    private void handleChargingStateUpdate(boolean charging) {
        if (listener != null && charging != wasCharging) {
            wasCharging = charging;
            listener.onChargingStateChanged(this, charging);
        }
    }

    private void handleCleanModeUpdate(CleanMode mode) {
        if (listener != null && mode != lastCleanMode) {
            lastCleanMode = mode;
            listener.onCleaningModeChanged(this, mode);
        }
    }

    private interface MessageHandler {
        void handleMessage(String topic, String payload);
    }

    private class XmlMessageHandler implements MessageHandler {
        @Override
        public void handleMessage(String topic, String payload) {
        }
    }

    private class JsonMessageHandler implements MessageHandler {
        @Override
        public void handleMessage(String topic, String payload) {
            String eventName = topic.split("/")[2].toLowerCase();
            JsonResponsePayloadWrapper response = gson.fromJson(payload, JsonResponsePayloadWrapper.class);
            if (response == null) {
                return;
            }
            // TODO: update FW version?

            if (eventName.startsWith("on")) {
                eventName = eventName.substring(2);
            } else if (eventName.startsWith("report")) {
                eventName = eventName.substring(6);
            }
            if (eventName.endsWith("_v2")) {
                eventName = eventName.substring(0, eventName.length() - 3);
            }

            switch (eventName) {
                case "battery": {
                    BatteryReport report = payloadAs(response, BatteryReport.class);
                    handleBatteryLevelUpdate(report.percent);
                    break;
                }
                case "chargestate": {
                    ChargeReport report = payloadAs(response, ChargeReport.class);
                    handleChargingStateUpdate(report.isCharging != 0);
                    break;
                }
                case "cleaninfo": {
                    CleanReport report = payloadAs(response, CleanReport.class);
                    handleCleanModeUpdate(report.mode);
                    break;
                }
                case "speed": {
                    // SpeedReport report = payloadAs(response, SpeedReport.class);
                    // SuctionPower power = SuctionPower.values()[report.speedLevel];
                    // TODO: report change
                }
            }
        }

        private <T> @NonNull T payloadAs(JsonResponsePayloadWrapper response, Class<T> clazz) {
            @Nullable
            T payload = gson.fromJson(response.body.payload, clazz);
            if (payload == null) {
                throw new NullPointerException();
            }
            return payload;
        }
    }

    private static class BatteryReport {
        @SerializedName("value")
        public int percent;
        @SerializedName("isLow")
        public int batteryIsLow;
    }

    private static class ChargeReport {
        @SerializedName("isCharging")
        public int isCharging;
    }

    private static class CleanReport {
        @SerializedName("trigger")
        public String trigger;
        @SerializedName("state")
        public CleanMode mode;
    }

    private static class SpeedReport {
        @SerializedName("speed")
        public int speedLevel;
    }
}
