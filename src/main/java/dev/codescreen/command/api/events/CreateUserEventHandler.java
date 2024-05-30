package dev.codescreen.command.api.events;

import dev.codescreen.command.api.data.UserRepository;
import dev.codescreen.command.api.data.Users;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

@Component
public class CreateUserEventHandler {

    private UserRepository userRepository;

    public CreateUserEventHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventHandler
    public void on(CreateUserEvent createUserEvent) {
        Users user = new Users(createUserEvent.userId, createUserEvent.creditAmount, createUserEvent.currency);
        userRepository.save(user);
    }
}
