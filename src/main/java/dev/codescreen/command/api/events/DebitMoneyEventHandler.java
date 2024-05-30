package dev.codescreen.command.api.events;

import dev.codescreen.command.api.data.UserRepository;
import dev.codescreen.command.api.data.Users;
import dev.codescreen.command.api.dto.Status;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DebitMoneyEventHandler {

    private UserRepository userRepository;

    public DebitMoneyEventHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventHandler
    public void on(DebitMoneyEvent debitMoneyEvent) {
        if (debitMoneyEvent.status == Status.APPROVED) {
            Users user = userRepository.findById(debitMoneyEvent.userId).get();
            user.setBalance((new BigDecimal(user.getBalance()))
                    .subtract(new BigDecimal(debitMoneyEvent.debitAmount))
                    .toString()
            );
            userRepository.save(user);
        }
    }
}
