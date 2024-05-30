package dev.codescreen.command.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TransactionAmount {
    @NotNull(message = "amount missing in the request header")
    private String amount;
    @NotNull(message = "currency missing in the request header")
    private String currency;
    private DebitOrCredit debitOrCredit;
}
