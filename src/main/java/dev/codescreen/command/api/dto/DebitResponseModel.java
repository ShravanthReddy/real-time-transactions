package dev.codescreen.command.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
@Setter
public class DebitResponseModel {
    private String messageId;
    private String userId;
    private Status responseCode;
    private Balance balance;
}
