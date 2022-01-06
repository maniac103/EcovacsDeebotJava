package dev.pott.sucks.api.commands;

public class StartCleaningCommand extends AbstractCleaningCommand {
    @Override
    protected String getAction() {
        return "auto";
    }
}
