package dev.codescreen.command.api.events;

import dev.codescreen.command.api.data.UserRepository;
import dev.codescreen.command.api.data.Users;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CreditMoneyEventHandler {

    private UserRepository userRepository;

    public CreditMoneyEventHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventHandler
    public void on(CreditMoneyEvent creditMoneyEvent) {
        Users user = userRepository.findById(creditMoneyEvent.userId).get();
        user.setBalance((new BigDecimal(user.getBalance()))
                .add(new BigDecimal(creditMoneyEvent.creditAmount))
                .toString()
        );

        userRepository.save(user);
    }
}
