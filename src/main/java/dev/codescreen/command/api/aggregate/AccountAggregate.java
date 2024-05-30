package dev.codescreen.command.api.aggregate;

import dev.codescreen.command.api.commands.CreateUserCommand;
import dev.codescreen.command.api.commands.CreditMoneyCommand;
import dev.codescreen.command.api.commands.DebitMoneyCommand;
import dev.codescreen.command.api.dto.*;
import dev.codescreen.command.api.events.CreateUserEvent;
import dev.codescreen.command.api.events.CreditMoneyEvent;
import dev.codescreen.command.api.events.DebitMoneyEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;

@Aggregate
public class AccountAggregate {
    @AggregateIdentifier
    private String userId;
    private BigDecimal balance;
    private String currency;

    public AccountAggregate() {
    }

    @CommandHandler
    public AccountAggregate(CreateUserCommand createUserCommand) {
        AggregateLifecycle.apply(
                new CreateUserEvent(
                        createUserCommand.userId,
                        createUserCommand.messageId,
                        createUserCommand.creditAmount,
                        createUserCommand.currency
                )
        );
    }

    @EventSourcingHandler
    public void userCreatedEvent(CreateUserEvent createUserEvent) {
        this.userId = createUserEvent.userId;
        this.balance = new BigDecimal(createUserEvent.creditAmount);
        this.currency = createUserEvent.currency;
    }

    @CommandHandler
    public CreditResponseModel moneyCreditCommand(CreditMoneyCommand creditMoneyCommand) {
        AggregateLifecycle.apply(
                new CreditMoneyEvent(
                        creditMoneyCommand.userId,
                        creditMoneyCommand.messageId,
                        creditMoneyCommand.creditAmount,
                        creditMoneyCommand.currency
                )
        );
        return new CreditResponseModel(
                creditMoneyCommand.messageId,
                creditMoneyCommand.userId,
                new Balance(
                        this.balance.toString(),
                        this.currency,
                        DebitOrCredit.CREDIT
                )
        );
    }

    @EventSourcingHandler
    public void moneyCreditedEvent(CreditMoneyEvent creditMoneyEvent) {
        this.balance = this.balance.add(new BigDecimal(creditMoneyEvent.creditAmount));
    }

    @CommandHandler
    public DebitResponseModel moneyDebitCommand(DebitMoneyCommand debitMoneyCommand) {
        BigDecimal updatedBalance = this.balance.subtract(new BigDecimal(debitMoneyCommand.debitAmount));
        BigDecimal zero = new BigDecimal("0");
        boolean debitCondition = this.balance.compareTo(zero) >= 0 & updatedBalance.compareTo(zero) < 0;

        AggregateLifecycle.apply(new DebitMoneyEvent(
                debitMoneyCommand.userId,
                debitMoneyCommand.messageId,
                debitMoneyCommand.debitAmount,
                debitMoneyCommand.currency,
                debitCondition ? Status.DECLINED : Status.APPROVED
        ));

        return new DebitResponseModel(
                debitMoneyCommand.messageId,
                debitMoneyCommand.userId,
                debitCondition ? Status.DECLINED : Status.APPROVED,
                new Balance(
                        this.balance.toString(),
                        this.currency,
                        DebitOrCredit.DEBIT
                )
        );
    }

    @EventSourcingHandler
    public void moneyDebitedEvent(DebitMoneyEvent debitMoneyEvent) {
        if (debitMoneyEvent.status == Status.APPROVED) {
            this.balance = this.balance.subtract(new BigDecimal(debitMoneyEvent.debitAmount));
        }
    }
}
