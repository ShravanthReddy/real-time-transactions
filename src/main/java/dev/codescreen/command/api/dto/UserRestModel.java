package dev.codescreen.command.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserRestModel {
    @NotNull(message = "messageId missing in the request header")
    private String messageId;
    @NotNull(message = "creditAmount missing in the request header")
    private String creditAmount;
    @NotNull(message = "currency missing in the request header")
    private String currency;
}
