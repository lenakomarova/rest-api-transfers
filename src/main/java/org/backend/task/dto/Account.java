package org.backend.task.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class Account {
    private final long id;
    private final BigDecimal balance;
    private final AccountState state;
}
