package org.backend.task.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class Transfer {
    private final BigDecimal amount;
    private final String description;
    private final long involvedAccount;

    @JsonCreator
    public Transfer(@JsonProperty("amount") BigDecimal amount, @JsonProperty("description") String description, @JsonProperty("involvedAccount") long involvedAccount) {
        this.amount = amount;
        this.description = description;
        this.involvedAccount = involvedAccount;
    }
}
