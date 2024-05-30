package dev.codescreen.command.api.service;

import dev.codescreen.command.api.commands.CreateUserCommand;
import dev.codescreen.command.api.commands.CreditMoneyCommand;
import dev.codescreen.command.api.commands.DebitMoneyCommand;
import dev.codescreen.command.api.dto.*;
import dev.codescreen.command.api.events.CreateUserEvent;
import dev.codescreen.command.api.events.CreditMoneyEvent;
import dev.codescreen.command.api.events.DebitMoneyEvent;
import dev.codescreen.core.api.exception.*;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.UUID;

@Service
public class AccountCommandServiceImpl implements AccountCommandService {

    private CommandGateway commandGateway;
    private EventStore eventStore;

    public AccountCommandServiceImpl(CommandGateway commandGateway, EventStore eventStore) {
        this.commandGateway = commandGateway;
        this.eventStore = eventStore;
    }

    @Override
    public CreateUserResponseModel createUser(String messageId, UserRestModel userRestModel) {
        checkIfMessageIdMatchesAndUnique("", messageId, userRestModel.getMessageId());
        checkAmount(userRestModel.getCreditAmount());
        checkCurrency("", userRestModel.getCurrency());

        userRestModel.setCreditAmount(
                new BigDecimal(userRestModel.getCreditAmount())
                        .setScale(2, RoundingMode.HALF_EVEN)
                        .toString()
        );

        String userId = commandGateway.sendAndWait(new CreateUserCommand(
                UUID.randomUUID().toString(),
                userRestModel.getMessageId(),
                userRestModel.getCreditAmount(),
                userRestModel.getCurrency()
        ));

        return new CreateUserResponseModel(
                userRestModel.getMessageId(),
                userId,
                new Balance(
                        userRestModel.getCreditAmount(),
                        userRestModel.getCurrency(),
                        DebitOrCredit.CREDIT
                )
        );
    }

    @Override
    public CreditResponseModel credit(String messageId, CreditRestModel creditRestModel) {
        checkIfUserIdExists(creditRestModel.getUserId());
        checkIfMessageIdMatchesAndUnique(creditRestModel.getUserId(), messageId, creditRestModel.getMessageId());
        checkAmount(creditRestModel.getTransactionAmount().getAmount());
        checkDebitOrCredit(creditRestModel.getTransactionAmount().getDebitOrCredit(), DebitOrCredit.CREDIT);
        checkCurrency(creditRestModel.getUserId(), creditRestModel.getTransactionAmount().getCurrency());

        creditRestModel.getTransactionAmount().setAmount(
                new BigDecimal(creditRestModel.getTransactionAmount().getAmount())
                        .setScale(2, RoundingMode.HALF_EVEN)
                        .toString()
        );

        return commandGateway.sendAndWait(new CreditMoneyCommand(
                creditRestModel.getUserId(),
                creditRestModel.getMessageId(),
                creditRestModel.getTransactionAmount().getAmount(),
                creditRestModel.getTransactionAmount().getCurrency()
        ));
    }

    @Override
    public DebitResponseModel debit(String messageId, DebitRestModel debitRestModel) {
        checkIfUserIdExists(debitRestModel.getUserId());
        checkIfMessageIdMatchesAndUnique(debitRestModel.getUserId(), messageId, debitRestModel.getMessageId());
        checkAmount(debitRestModel.getTransactionAmount().getAmount());
        checkDebitOrCredit(debitRestModel.getTransactionAmount().getDebitOrCredit(), DebitOrCredit.DEBIT);
        checkCurrency(debitRestModel.getUserId(), debitRestModel.getTransactionAmount().getCurrency());

        debitRestModel.getTransactionAmount().setAmount(
                new BigDecimal(debitRestModel.getTransactionAmount().getAmount())
                        .setScale(2, RoundingMode.HALF_EVEN)
                        .toString()
        );

        return commandGateway.sendAndWait(new DebitMoneyCommand(
                debitRestModel.getUserId(),
                debitRestModel.getMessageId(),
                debitRestModel.getTransactionAmount().getAmount(),
                debitRestModel.getTransactionAmount().getCurrency()
        ));
    }

    public void checkIfUserIdExists(String userId) {
        DomainEventStream eventStream = eventStore.readEvents(userId);
        if (!eventStream.hasNext()) {
            throw new UserIdNotFoundException("User not registered, Register the user and try again", "404");
        }
    }

    public void checkAmount(String amount) {
        String decimalPattern = "^\\d+(\\.\\d+)?$";
        if (amount.isEmpty() || !amount.matches(decimalPattern) || (new BigDecimal(amount).compareTo(new BigDecimal("0")) <= 0)) {
            throw new NotValidAmountException("Amount should be greater than 0 and should be in valid format", "400");
        }
    }

    public void checkDebitOrCredit(DebitOrCredit debitOrCredit, DebitOrCredit validator) {
        if (debitOrCredit != validator) {
            throw new WrongTransactionTypeException(
                    "Transaction type '" + debitOrCredit + "' is invalid for a " + validator + " transaction. Please provide a valid transaction type",
                    "400"
            );
        }
    }

    public void checkCurrency(String userId, String currency) {

        boolean supportedCurrency = false;
        if (userId.isEmpty()) { // Create User Id - check Currency logic
            if (currency.isEmpty()) {
                throw new InvalidCurrencyException("No currency mentioned, specify a currency", "400");
            }
        } else { // Other endpoints
            DomainEventStream eventStream = eventStore.readEvents(userId);
            String defaultCurrency = "";
            if (eventStream.hasNext()) {
                CreateUserEvent createUserEvent;
                DomainEventMessage<?> eventMessage = eventStream.next();
                createUserEvent = (CreateUserEvent) eventMessage.getPayload();
                defaultCurrency = createUserEvent.currency;
            }
            if (!defaultCurrency.equals(currency)) {
                throw new InvalidCurrencyException("Invalid currency in the request header, use default currency: " + defaultCurrency, "400");
            }
        }

        // checking if the currency is in the supported currencies enum
        for (SupportedCurrencies supportedCurrencies: SupportedCurrencies.values()) {
            if (supportedCurrencies.toString().equals(currency)) {
                supportedCurrency = true;
                break;
            }
        }

        if (!supportedCurrency) {
            throw new InvalidCurrencyException("Not supported currency in the request header", "400");
        }

    }

    public void checkIfMessageIdMatchesAndUnique(String userId, String pathMessageId, String messageId) {
        if (!pathMessageId.equals(messageId)) {
            throw new InvalidMessageIdException("messageId in the path should match with the messageId in the request header", "400");
        }

        if (messageId.isEmpty()) {
            throw new InvalidMessageIdException("Please provide a messageId, should not be empty", "400");
        }

        if (!userId.isEmpty()) {
            CreditMoneyEvent creditMoneyEvent;
            DebitMoneyEvent debitMoneyEvent;
            CreateUserEvent createUserEvent;
            boolean messageIdAlreadyExists = false;
            DomainEventStream eventStream = eventStore.readEvents(userId);

            while (eventStream.hasNext()) {
                DomainEventMessage<?> domainEventMessage = eventStream.next();
                if (domainEventMessage.getPayload().getClass() == CreditMoneyEvent.class) {
                    creditMoneyEvent = (CreditMoneyEvent) domainEventMessage.getPayload();
                    if(messageId.equals(creditMoneyEvent.messageId)) {
                        messageIdAlreadyExists = true;
                    };

                } else if (domainEventMessage.getPayload().getClass() == DebitMoneyEvent.class) {
                    debitMoneyEvent = (DebitMoneyEvent) domainEventMessage.getPayload();
                    if(messageId.equals(debitMoneyEvent.messageId)) {
                        messageIdAlreadyExists = true;
                    };

                } else if (domainEventMessage.getPayload().getClass() == CreateUserEvent.class) {
                    createUserEvent = (CreateUserEvent) domainEventMessage.getPayload();
                    if(messageId.equals(createUserEvent.messageId)) {
                        messageIdAlreadyExists = true;
                    };
                }

                if (messageIdAlreadyExists) {
                    throw new InvalidMessageIdException("messageId should be unique, please try again", "400");
                }
            }
        }
    }
}
