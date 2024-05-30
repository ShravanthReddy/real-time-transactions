package dev.codescreen.command.api.aggregate;

import dev.codescreen.command.api.commands.CreateUserCommand;
import dev.codescreen.command.api.commands.CreditMoneyCommand;
import dev.codescreen.command.api.commands.DebitMoneyCommand;
import dev.codescreen.command.api.dto.Status;
import dev.codescreen.command.api.events.CreateUserEvent;
import dev.codescreen.command.api.events.CreditMoneyEvent;
import dev.codescreen.command.api.events.DebitMoneyEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountAggregateTest {
    private FixtureConfiguration<AccountAggregate> fixture;

    @BeforeEach
    public void setUp() {
        fixture = new AggregateTestFixture<>(AccountAggregate.class);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreateUserCommand_SucceedsAndEmitsCreateUserEvent() {
        fixture.givenNoPriorActivity()
                .when(new CreateUserCommand(
                        "8786e2f9-d472-46a8-958f-d659880e723d",
                        "ABC122",
                        "100", // creditAmount as String
                        "USD"
                )).expectSuccessfulHandlerExecution()
                .expectEvents(new CreateUserEvent(
                        "8786e2f9-d472-46a8-958f-d659880e723d",
                        "ABC122",
                        "100", // creditAmount as String
                        "USD"
                ));
    }

    @Test
    void testMoneyCreditCommand_SucceedsAndEmitsCreditMoneyEvent() {
        fixture.given(new CreateUserEvent(
                        "8786e2f9-d472-46a8-958f-d659880e723d",
                        "ABC123",
                        "1000",
                        "USD")
                ).when(new CreditMoneyCommand(
                        "8786e2f9-d472-46a8-958f-d659880e723d",
                        "ABC124",
                        "220",
                        "USD")
                ).expectSuccessfulHandlerExecution()
                .expectEvents(new CreditMoneyEvent(
                        "8786e2f9-d472-46a8-958f-d659880e723d",
                        "ABC124",
                        "220",
                        "USD")
                );
    }

    @Test
    void testMoneyDebitCommand_SucceedsAndEmitsDebitMoneyEvent() {
        fixture.given(new CreateUserEvent(
                        "8786e2f9-d472-46a8-958f-d659880e723d",
                        "ABC123",
                        "1000",
                        "USD")
                ).when(new DebitMoneyCommand(
                        "8786e2f9-d472-46a8-958f-d659880e723d",
                        "ABC124",
                        "220",
                        "USD")
                ).expectSuccessfulHandlerExecution()
                .expectEvents(new DebitMoneyEvent(
                        "8786e2f9-d472-46a8-958f-d659880e723d",
                        "ABC124",
                        "220",
                        "USD",
                        Status.APPROVED)
                );
    }
}