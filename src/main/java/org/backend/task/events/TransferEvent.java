package org.backend.task.events;


import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Value
public class TransferEvent {
    private final TransferDirection direction;
    private final BigDecimal amount;
    private final String description;
    private final long involvedAccountId;
    private final BigDecimal currentBalance;
    private final LocalDateTime date;
}
