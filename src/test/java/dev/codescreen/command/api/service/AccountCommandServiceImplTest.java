package dev.codescreen.command.api.service;

import dev.codescreen.command.api.data.Users;
import dev.codescreen.command.api.dto.*;
import dev.codescreen.command.api.events.CreateUserEvent;
import dev.codescreen.core.api.exception.*;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.eventsourcing.eventstore.DomainEventStream;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountCommandServiceImplTest {
    @Mock
    private EventStore eventStore;
    @Mock
    private CommandGateway commandGateway;
    @InjectMocks
    private AccountCommandServiceImpl accountCommandService;
    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users("8786e2f9-d472-46a8-958f-d659880e723d", "1000", "USD");
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreateUser_SuccessfulCreation_ReturnsUserId() {

        when(commandGateway.sendAndWait(any())).thenReturn(user.getUserId());
        CreateUserResponseModel createUserResponseModel = accountCommandService.createUser("ABC123", new UserRestModel(
                "ABC123",
                "100.00",
                "USD"
        ));

        verify(commandGateway, times(1)).sendAndWait(any());
        assertEquals(new CreateUserResponseModel(
                "ABC123",
                user.getUserId(),
                new Balance(
                        "100.00",
                        "USD",
                        DebitOrCredit.CREDIT
                )), createUserResponseModel);
    }

    @Test
    void testCredit_ValidCredit_ReturnsCreditResponseModel() {
        CreditRestModel creditRestModel = new CreditRestModel(
                "ABC123",
                user.getUserId(),
                new TransactionAmount("200", "USD", DebitOrCredit.CREDIT)
        );

        CreditResponseModel creditResponseModel = new CreditResponseModel(
                "ABC123",
                user.getUserId(),
                new Balance(user.getBalance(), user.getCurrency(), DebitOrCredit.CREDIT)
        );

        // for userId checks
        DomainEventStream eventStream = Mockito.mock(DomainEventStream.class);
        when(eventStore.readEvents(Mockito.anyString())).thenReturn(eventStream);

        // for userId, messageId and currency checks
        when(eventStream.hasNext()).thenReturn(true).thenReturn(false).thenReturn(true);

        // for currency checks
        DomainEventMessage domainEventMessage = Mockito.mock(DomainEventMessage.class);
        when(eventStream.next()).thenReturn(domainEventMessage);
        when((CreateUserEvent) domainEventMessage.getPayload()).thenReturn(new CreateUserEvent("1234", "ABC12", "100", "USD"));

        when(commandGateway.sendAndWait(any())).thenReturn(creditResponseModel);

        CreditResponseModel response = accountCommandService.credit("ABC123", creditRestModel);
        verify(commandGateway, times(1)).sendAndWait(any());
        assertEquals(creditResponseModel, response);
    }

    @Test
    void testDebit_ValidDebit_ReturnsDebitResponseModel() {
        DebitRestModel debitRestModel = new DebitRestModel(
                "ABC123",
                user.getUserId(),
                new TransactionAmount("200", "USD", DebitOrCredit.DEBIT)
        );

        DebitResponseModel debitResponseModel = new DebitResponseModel(
                "ABC123",
                user.getUserId(),
                Status.APPROVED,
                new Balance(user.getBalance(), user.getCurrency(), DebitOrCredit.CREDIT)
        );

        // for userId checks
        DomainEventStream eventStream = Mockito.mock(DomainEventStream.class);
        when(eventStore.readEvents(Mockito.anyString())).thenReturn(eventStream);

        // for userId, messageId and currency checks
        when(eventStream.hasNext()).thenReturn(true).thenReturn(false).thenReturn(true);

        // for currency checks
        DomainEventMessage domainEventMessage = Mockito.mock(DomainEventMessage.class);
        when(eventStream.next()).thenReturn(domainEventMessage);
        when((CreateUserEvent) domainEventMessage.getPayload()).thenReturn(new CreateUserEvent(
                user.getUserId(),
                "ABC122",
                "400",
                "USD"
        ));
        when(commandGateway.sendAndWait(any())).thenReturn(debitResponseModel);

        DebitResponseModel response = accountCommandService.debit("ABC123", debitRestModel);

        verify(commandGateway, times(1)).sendAndWait(any());
        assertEquals(debitResponseModel, response);
    }

    @Test
    void testWithInvalidAmount_NegativeAmount_ThrowsNotValidAmountException() {
        UserRestModel userRestModel = new UserRestModel(
                "ABC123",
                "-100",
                "USD"
        );

        CreditRestModel creditRestModel = new CreditRestModel(
                "ABC123",
                user.getUserId(),
                new TransactionAmount("-200", "USD", DebitOrCredit.CREDIT)
        );

        DebitRestModel debitRestModel = new DebitRestModel(
                "ABC123",
                user.getUserId(),
                new TransactionAmount("-200", "USD", DebitOrCredit.DEBIT)
        );

        // for userId checks
        DomainEventStream eventStream = Mockito.mock(DomainEventStream.class);
        when(eventStore.readEvents(Mockito.anyString())).thenReturn(eventStream);

        // for userId, messageId and currency checks
        when(eventStream.hasNext()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);

        assertThrows(NotValidAmountException.class, () -> accountCommandService.createUser("ABC123", userRestModel));
        assertThrows(NotValidAmountException.class, () -> accountCommandService.credit("ABC123", creditRestModel));
        assertThrows(NotValidAmountException.class, () -> accountCommandService.debit("ABC123", debitRestModel));
    }

    @Test
    void testWithWrongEndpoint_DebitCreditEndpoint_ThrowsWrongTransactionTypeException() {
        CreditRestModel creditRestModel = new CreditRestModel(
                "ABC123",
                user.getUserId(),
                new TransactionAmount("200", "USD", DebitOrCredit.DEBIT)
        );

        DebitRestModel debitRestModel = new DebitRestModel(
                "ABC123",
                user.getUserId(),
                new TransactionAmount("200", "USD", DebitOrCredit.CREDIT)
        );

        // for userId checks
        DomainEventStream eventStream = Mockito.mock(DomainEventStream.class);
        when(eventStore.readEvents(Mockito.anyString())).thenReturn(eventStream);

        // for userId, messageId and currency checks
        when(eventStream.hasNext()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);

        assertThrows(WrongTransactionTypeException.class, () -> accountCommandService.credit("ABC123", creditRestModel));
        assertThrows(WrongTransactionTypeException.class, () -> accountCommandService.debit("ABC123", debitRestModel));
    }

    @Test
    void testWithWrongUserId_CreditDebitWithNonExistentUserId_ThrowsUserIdNotFoundException() {
        CreditRestModel creditRestModel = new CreditRestModel(
                "ABC123",
                user.getUserId() + 1,
                new TransactionAmount("200", "USD", DebitOrCredit.CREDIT)
        );

        DebitRestModel debitRestModel = new DebitRestModel(
                "ABC123",
                user.getUserId() + 1,
                new TransactionAmount("200", "USD", DebitOrCredit.DEBIT)
        );

        // for userId checks
        DomainEventStream eventStream = Mockito.mock(DomainEventStream.class);
        when(eventStore.readEvents(Mockito.anyString())).thenReturn(eventStream);

        // for userId, messageId and currency checks
        when(eventStream.hasNext()).thenReturn(false);

        assertThrows(UserIdNotFoundException.class, () -> accountCommandService.credit("ABC123", creditRestModel));
        assertThrows(UserIdNotFoundException.class, () -> accountCommandService.debit("ABC123", debitRestModel));
    }

    @Test
    void testWithWrongMessageId_CreditDebitMismatchedMessageId_ThrowsMessageIdNotMatchingException() {
        CreditRestModel creditRestModel = new CreditRestModel(
                "ABC123",
                user.getUserId(),
                new TransactionAmount("200", "USD", DebitOrCredit.CREDIT)
        );

        DebitRestModel debitRestModel = new DebitRestModel(
                "ABC123",
                user.getUserId(),
                new TransactionAmount("200", "USD", DebitOrCredit.DEBIT)
        );

        // for userId checks
        DomainEventStream eventStream = Mockito.mock(DomainEventStream.class);
        when(eventStore.readEvents(Mockito.anyString())).thenReturn(eventStream);

        // for userId, messageId and currency checks
        when(eventStream.hasNext()).thenReturn(true).thenReturn(true);

        assertThrows(InvalidMessageIdException.class, () -> accountCommandService.credit("ABC122", creditRestModel));
        assertThrows(InvalidMessageIdException.class, () -> accountCommandService.debit("ABC122", debitRestModel));
    }

    @Test
    void testWithWrongCurrency_DebitCreditInvalidCurrency_ThrowsInvalidCurrencyException() {
        CreditRestModel creditRestModel = new CreditRestModel(
                "ABC123",
                user.getUserId(),
                new TransactionAmount("200", "INR", DebitOrCredit.CREDIT)
        );

        DebitRestModel debitRestModel = new DebitRestModel(
                "ABC123",
                user.getUserId(),
                new TransactionAmount("200", "INR", DebitOrCredit.DEBIT)
        );

        // for userId checks
        DomainEventStream eventStream = Mockito.mock(DomainEventStream.class);
        when(eventStore.readEvents(Mockito.anyString())).thenReturn(eventStream);

        // for userId, messageId and currency checks
        when(eventStream.hasNext()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(true).thenReturn(false).thenReturn(true);

        // for currency checks
        DomainEventMessage domainEventMessage = Mockito.mock(DomainEventMessage.class);
        when(eventStream.next()).thenReturn(domainEventMessage);
        when((CreateUserEvent) domainEventMessage.getPayload()).thenReturn(new CreateUserEvent(
                user.getUserId(),
                "ABC122",
                "400",
                "USD"
        ));

        assertThrows(InvalidCurrencyException.class, () -> accountCommandService.credit("ABC123", creditRestModel));
        assertThrows(InvalidCurrencyException.class, () -> accountCommandService.debit("ABC123", debitRestModel));
    }
}
