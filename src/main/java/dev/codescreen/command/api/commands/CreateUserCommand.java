package dev.codescreen.command.api.commands;

public class CreateUserCommand extends BaseCommand {
    public String messageId;
    public String creditAmount;
    public String currency;

    public CreateUserCommand(String userId, String messageId, String creditAmount, String currency) {
        super(userId);
        this.messageId = messageId;
        this.creditAmount = creditAmount;
        this.currency = currency;
    }
}
