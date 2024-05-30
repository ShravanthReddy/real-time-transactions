package dev.codescreen.command.api.events;

import dev.codescreen.command.api.data.UserRepository;
import dev.codescreen.command.api.data.Users;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

class CreateUserEventHandlerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CreateUserEventHandler createUserEventHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void on_UserCreationEvent_SavesUserInRepository() {
        CreateUserEvent createUserEvent = new CreateUserEvent(
                "8786e2f9-d472-46a8-958f-d659880e723d",
                "ABC123",
                "100",
                "USD"
        );

        createUserEventHandler.on(createUserEvent);
        verify(userRepository).save(new Users("8786e2f9-d472-46a8-958f-d659880e723d", "100", "USD"));
    }
}