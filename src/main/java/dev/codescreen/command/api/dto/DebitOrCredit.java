package dev.codescreen.command.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DebitOrCredit {
    DEBIT, CREDIT;

    @JsonCreator
    public static DebitOrCredit forCheck(String transactionType) {
        for(DebitOrCredit dc: values()) {
            if(dc.name().equals(transactionType)) { //change accordingly
                return dc;
            }
        }
        return null;
    }
}
