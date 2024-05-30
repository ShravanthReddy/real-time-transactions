package dev.codescreen.command.api.controller;

import dev.codescreen.command.api.dto.CreditRestModel;
import dev.codescreen.command.api.dto.DebitRestModel;
import dev.codescreen.command.api.dto.UserRestModel;
import dev.codescreen.command.api.service.AccountCommandService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionsCommandController {

    private final AccountCommandService accountCommandService;

    public TransactionsCommandController(AccountCommandService accountCommandService) {
        this.accountCommandService = accountCommandService;
    }

    @PostMapping("/create/{messageId}")
    public ResponseEntity<Object> createUser(@PathVariable String messageId, @RequestBody @Valid UserRestModel userRestModel) {
        return new ResponseEntity<>(accountCommandService.createUser(messageId, userRestModel), HttpStatus.CREATED);
    }

    @PostMapping("/load/{messageId}")
    public ResponseEntity<Object> creditAmount(@PathVariable String messageId, @RequestBody @Valid CreditRestModel creditRestModel) {
        return new ResponseEntity<>(accountCommandService.credit(messageId, creditRestModel), HttpStatus.CREATED);
    }

    @PostMapping("/authorization/{messageId}")
    public ResponseEntity<Object> debitAmount(@PathVariable String messageId, @RequestBody @Valid DebitRestModel debitRestModel) {
        return new ResponseEntity<>(accountCommandService.debit(messageId, debitRestModel), HttpStatus.CREATED);
    }
}
