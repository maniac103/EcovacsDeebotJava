package dev.pott.sucks.api.commands;

public class StopCommand extends AbstractCleaningCommand {
    @Override
    protected String getAction() {
        return "stop";
    }
}
