package org.backend.test.dto;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Transfer {
    private final BigDecimal amount;
    private final String description;

    private final String debitAccountId;
    private final String creditAccountId;
}
