package org.backend.task.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class Account {
    private final long id;
    private final BigDecimal balance;
    private final AccountState state;

    @JsonCreator
    public Account(@JsonProperty("id") long id, @JsonProperty("balance") BigDecimal balance, @JsonProperty("state") AccountState state) {
        this.id = id;
        this.balance = balance;
        this.state = state;
    }
}
