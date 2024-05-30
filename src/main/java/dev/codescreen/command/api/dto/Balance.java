package dev.codescreen.command.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class Balance {
    private String amount;
    private String currency;
    private DebitOrCredit debitOrCredit;
}
