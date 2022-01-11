package dev.pott.sucks.api.commands;

public interface MultiCommand<T> {
    public IotDeviceCommand<?> getFirstCommand(boolean useXml);

    public IotDeviceCommand<?> processResultAndGetNextCommand(Object lastCommandResult);

    public T getResult();
}
