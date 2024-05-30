package dev.codescreen.command.api.events;

import dev.codescreen.command.api.data.UserRepository;
import dev.codescreen.command.api.data.Users;
import dev.codescreen.command.api.dto.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DebitMoneyEventHandlerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DebitMoneyEventHandler debitMoneyEventHandler;
    private Users user;

    @BeforeEach
    void setUp() {
        user = new Users("8786e2f9-d472-46a8-958f-d659880e723d", "100", "USD");
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void on_DebitMoneyEvent_DebitsBalance() {
        DebitMoneyEvent debitMoneyEvent = new DebitMoneyEvent(
                user.getUserId(),
                "ABC123",
                "100",
                "USD",
                Status.APPROVED
        );

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        BigDecimal expectedBalance = new BigDecimal(user.getBalance()).subtract(new BigDecimal("100"));

        debitMoneyEventHandler.on(debitMoneyEvent);

        verify(userRepository, times(1)).findById(user.getUserId());
        verify(userRepository, times(1)).save(user);

        assertEquals(expectedBalance.toString(), user.getBalance());
    }
}