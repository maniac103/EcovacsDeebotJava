package dev.pott.sucks.api.internal;

import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.MqttClientSslConfig;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.auth.Mqtt3SimpleAuth;

import dev.pott.sucks.api.EcovacsApiConfiguration;
import dev.pott.sucks.api.EcovacsApiException;
import dev.pott.sucks.api.EcovacsDevice;
import dev.pott.sucks.api.commands.GetBatteryInfoCommand;
import dev.pott.sucks.api.commands.GetChargeStateCommand;
import dev.pott.sucks.api.commands.GetCleanStateCommand;
import dev.pott.sucks.api.commands.GetFirmwareVersionCommand;
import dev.pott.sucks.api.commands.GetMoppingWaterAmountCommand;
import dev.pott.sucks.api.commands.GetWaterSystemPresentCommand;
import dev.pott.sucks.api.commands.IotDeviceCommand;
import dev.pott.sucks.api.internal.dto.response.deviceapi.BatteryReport;
import dev.pott.sucks.api.internal.dto.response.deviceapi.ChargeReport;
import dev.pott.sucks.api.internal.dto.response.deviceapi.CleanReport;
import dev.pott.sucks.api.internal.dto.response.deviceapi.StatsReport;
import dev.pott.sucks.api.internal.dto.response.deviceapi.WaterInfoReport;
import dev.pott.sucks.api.internal.dto.response.portal.Device;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandJsonResponse.JsonResponsePayloadWrapper;
import dev.pott.sucks.api.internal.dto.response.portal.PortalLoginResponse;
import dev.pott.sucks.cleaner.ChargeMode;
import dev.pott.sucks.cleaner.CleanMode;
import dev.pott.sucks.cleaner.DeviceCapability;
import dev.pott.sucks.cleaner.MoppingWaterAmount;
import io.netty.handler.ssl.util.SimpleTrustManagerFactory;

public class EcovacsIotMqDevice implements EcovacsDevice {
    private final Logger logger = LoggerFactory.getLogger(EcovacsIotMqDevice.class);

    private final Device device;
    private final DeviceDescription desc;
    private final String firmwareVersion;
    private final EcovacsApiImpl api;
    private final Gson gson;
    private final MessageHandler messageHandler;
    private Mqtt3AsyncClient mqttClient;
    private StateChangeListener listener;

    private int lastBatteryLevel;
    private boolean wasCharging;
    private CleanMode lastCleanMode;
    private boolean wasWaterSystemPresent;
    private MoppingWaterAmount lastWaterAmount;

    EcovacsIotMqDevice(Device device, DeviceDescription desc, EcovacsApiImpl api, Gson gson)
            throws EcovacsApiException {
        this.device = device;
        this.desc = desc;
        this.firmwareVersion = api.sendIotCommand(device, desc, new GetFirmwareVersionCommand());
        this.api = api;
        this.gson = gson;
        this.messageHandler = desc.usesJsonApi ? new JsonMessageHandler() : new XmlMessageHandler();
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
        return desc.modelName;
    }

    @Override
    public boolean hasCapability(DeviceCapability cap) {
        return desc.capabilities.contains(cap);
    }

    @Override
    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    @Override
    public <T> T sendCommand(IotDeviceCommand<T> command) throws EcovacsApiException {
        return api.sendIotCommand(device, desc, command);
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

        lastBatteryLevel = api.sendIotCommand(device, desc, new GetBatteryInfoCommand());
        wasCharging = api.sendIotCommand(device, desc, new GetChargeStateCommand()) == ChargeMode.CHARGING;
        lastCleanMode = api.sendIotCommand(device, desc, new GetCleanStateCommand());
        if (hasCapability(DeviceCapability.MOPPING_SYSTEM)) {
            wasWaterSystemPresent = api.sendIotCommand(device, desc, new GetWaterSystemPresentCommand());
            lastWaterAmount = api.sendIotCommand(device, desc, new GetMoppingWaterAmountCommand());
        }

        listener.onBatteryLevelChanged(this, lastBatteryLevel);
        listener.onChargingStateChanged(this, wasCharging);
        listener.onCleaningModeChanged(this, lastCleanMode);
        if (hasCapability(DeviceCapability.MOPPING_SYSTEM)) {
            listener.onWaterSystemChanged(this, wasWaterSystemPresent, lastWaterAmount);
        }

        mqttClient = MqttClient.builder().useMqttVersion3().identifier(userName + "/" + loginData.getResource())
                .simpleAuth(auth).serverHost(host).serverPort(8883).sslConfig(sslConfig).buildAsync();

        mqttClient.connect().whenComplete((connAck, connError) -> {
            if (connError != null) {
                handleMqttError(connError);
                return;
            }

            logger.debug("Established MQTT connection to device {}", getSerialNumber());
            String topic = String.format("iot/atr/+/%s/%s/%s/+", device.getDid(), device.getDeviceClass(),
                    device.getResource());
            mqttClient.subscribeWith().topicFilter(topic).callback(publish -> {
                String payload = new String(publish.getPayloadAsBytes());
                try {
                    messageHandler.handleMessage(publish.getTopic().toString(), payload);
                } catch (Exception e) {
                    handleMqttError(e);
                }
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
        if (listener != null) {
            listener.onDeviceConnectionFailed(this, t);
        }
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

    private void handleStatsUpdate(int area, int cleaningTimeInSeconds) {
        if (listener != null) {
            listener.onCleaningStatsChanged(this, area, cleaningTimeInSeconds);
        }
    }

    private void handleWaterInfoUpdate(boolean present, int level) {
        MoppingWaterAmount amount = MoppingWaterAmount.fromApiValue(level);
        if (hasCapability(DeviceCapability.MOPPING_SYSTEM) && listener != null
                && (wasWaterSystemPresent != present || lastWaterAmount != amount)) {
            wasWaterSystemPresent = present;
            lastWaterAmount = amount;
            listener.onWaterSystemChanged(this, present, amount);
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

            logger.trace("{}: Got MQTT message on topic {}: {}", getSerialNumber(), topic, payload);

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
                    handleCleanModeUpdate(report.determineCleanMode(gson));
                    break;
                }
                case "evt": {
                    // EventReport report = payloadAs(reponse, EventReport.class);
                    break;
                }
                case "lifespan": {
                    // ComponentLifeSpanReport report = payloadAs(response, ComponentLifeSpanReport.class);
                    break;
                }
                case "speed": {
                    // SpeedReport report = payloadAs(response, SpeedReport.class);
                    // SuctionPower power = SuctionPower.fromJsonValue(report.speedLevel);
                    // TODO: report change
                    break;
                }
                case "stats": {
                    StatsReport report = payloadAs(response, StatsReport.class);
                    handleStatsUpdate(report.area, report.timeInSeconds);
                    break;
                }
                case "waterinfo": {
                    WaterInfoReport report = payloadAs(response, WaterInfoReport.class);
                    handleWaterInfoUpdate(report.waterPlatePresent != 0, report.waterAmount);
                    break;
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
}
