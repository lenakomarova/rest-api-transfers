package org.backend.task.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Getter
@RequiredArgsConstructor
public enum TransferError {
    INSUFFICIENT_FUNDS("Insufficient funds"),
    ACCOUNT_NOT_EXISTS("Account doesn't exist"),
    SYSTEM_ERROR("System error. Try later");

    private final String text;

}
