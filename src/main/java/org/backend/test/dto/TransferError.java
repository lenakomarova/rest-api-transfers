package org.backend.test.dto;

public enum TransferError {
    INSUFFICIENT_FUNDS("Insufficient funds"),
    ACCOUNT_NOT_EXISTS("Account not exists"),
    SYSTEM_ERROR("System error. Try later");

    private final String text;

    public String getText() {
        return text;
    }

    TransferError(String text) {
        this.text = text;
    }
}
