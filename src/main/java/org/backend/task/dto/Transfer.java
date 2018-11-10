package org.backend.task.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class Transfer {
    private final BigDecimal amount;
    private final String description;
    private final long involvedAccount;
}
