package dev.codescreen.command.api.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class BaseCommand {
    @TargetAggregateIdentifier
    public final String userId;

    public BaseCommand(String userId) {
        this.userId = userId;
    }
}
