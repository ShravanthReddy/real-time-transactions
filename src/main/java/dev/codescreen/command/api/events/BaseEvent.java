package dev.codescreen.command.api.events;

public class BaseEvent {
    public final String userId;

    public BaseEvent(String userId) {
        this.userId = userId;
    }
}
