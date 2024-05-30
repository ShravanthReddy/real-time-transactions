package dev.codescreen.command.api.events;

public class CreditMoneyEvent extends BaseEvent {
    public final String messageId;
    public final String creditAmount;
    public final String currency;

    public CreditMoneyEvent(String userId, String messageId, String creditAmount, String currency) {
        super(userId);
        this.messageId = messageId;
        this.creditAmount = creditAmount;
        this.currency = currency;
    }
}
