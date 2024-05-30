package dev.codescreen.command.api.dto;

public enum SupportedCurrencies {
    USD("USD"), CAD("CAD"), EUR("EUR"), GBP("GBP");

    private final String value;

    SupportedCurrencies(String value) {
        this.value = value;
    }
}
