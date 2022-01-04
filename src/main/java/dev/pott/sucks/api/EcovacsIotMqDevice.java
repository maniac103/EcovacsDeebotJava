package dev.pott.sucks.api;

import dev.pott.sucks.api.dto.request.commands.GetFirmwareVersionCommand;
import dev.pott.sucks.api.dto.response.portal.Device;
import dev.pott.sucks.api.dto.response.portal.IotProduct.ProductDefinition;

public class EcovacsIotMqDevice implements EcovacsDevice {
    private final Device device;
    private final ProductDefinition product;
    private final String firmwareVersion;
    private final EcovacsApi api;

    EcovacsIotMqDevice(Device device, ProductDefinition product, EcovacsApi api) throws EcovacsApiException {
        this.device = device;
        this.product = product;
        this.firmwareVersion = api.sendIotCommand(device, new GetFirmwareVersionCommand());
        this.api = api;
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

}
