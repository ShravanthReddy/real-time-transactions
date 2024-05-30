package dev.codescreen.command.api.commands;

public class DebitMoneyCommand extends BaseCommand {
    public String messageId;
    public String debitAmount;
    public String currency;

    public DebitMoneyCommand(String userId, String messageId, String debitAmount, String currency) {
        super(userId);
        this.messageId = messageId;
        this.debitAmount = debitAmount;
        this.currency = currency;
    }
}
