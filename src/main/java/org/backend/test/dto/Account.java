package org.backend.test.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class Account {
    private final String id;
    private final BigDecimal balance;
    private final AccountState state;
}
