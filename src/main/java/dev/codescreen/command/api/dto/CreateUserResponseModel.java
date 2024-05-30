package dev.codescreen.command.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserResponseModel {
    private String messageId;
    private String userId;
    private Balance balance;
}
