package dev.codescreen.core.api.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionsException {
    private final String message;
    private final String code;
}
