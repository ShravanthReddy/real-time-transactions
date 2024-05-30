package dev.codescreen.command.api.events;

public class CreateUserEvent extends BaseEvent {
    public String messageId;
    public String creditAmount;
    public String currency;

    public CreateUserEvent(String userId, String messageId, String creditAmount, String currency) {
        super(userId);
        this.messageId = messageId;
        this.creditAmount = creditAmount;
        this.currency = currency;
    }
}
