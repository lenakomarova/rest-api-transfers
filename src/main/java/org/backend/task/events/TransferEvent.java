package org.backend.task.events;


import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Builder
@Value
public class TransferEvent {
    private final TransferDirection direction;
    private final BigDecimal amount;
    private final String description;
    private final String involvedAccountId;
    private final BigDecimal currentBalance;
}
