package dev.codescreen.command.api.events;

import dev.codescreen.command.api.dto.Status;

public class DebitMoneyEvent extends BaseEvent {
    public final String messageId;
    public final String debitAmount;
    public final String currency;
    public final Status status;

    public DebitMoneyEvent(String userId, String messageId, String debitAmount, String currency, Status status) {
        super(userId);
        this.messageId = messageId;
        this.debitAmount = debitAmount;
        this.currency = currency;
        this.status = status;
    }
}
