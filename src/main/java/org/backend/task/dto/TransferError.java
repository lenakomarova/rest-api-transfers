package org.backend.task.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransferError {
    INSUFFICIENT_FUNDS("Insufficient funds"),
    ACCOUNT_NOT_EXISTS("Account doesn't exist"),
    AMOUNT_MUST_BE_POSITIVE("Amount must be positive"),
    SYSTEM_ERROR("System error. Try later");

    private final String text;

}
