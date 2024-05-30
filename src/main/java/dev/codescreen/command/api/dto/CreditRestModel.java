package dev.codescreen.command.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreditRestModel {
    @NotNull(message = "messageId missing in the request header")
    private String messageId;
    @NotNull(message = "User ID missing in the request header")
    private String userId;
    @Valid
    @NotNull(message = "transactionAmount missing in the request header")
    private TransactionAmount transactionAmount;
}
