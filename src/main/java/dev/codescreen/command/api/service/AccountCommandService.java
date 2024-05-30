package dev.codescreen.command.api.service;

import dev.codescreen.command.api.dto.*;
import org.springframework.http.ResponseEntity;

public interface AccountCommandService {
    public CreateUserResponseModel createUser(String messageId, UserRestModel userRestModel);
    public CreditResponseModel credit(String messageId, CreditRestModel creditRestModel);
    public DebitResponseModel debit(String messageId, DebitRestModel debitRestModel);
}
