package org.backend.test.events;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class TransferEvent {
    private final TransferDirection direction;
    private final BigDecimal amount;
    private final String description;
    private final String involvedAccountId;
    private final BigDecimal currentBalance;
}
