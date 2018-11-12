package org.backend.task.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@EqualsAndHashCode(of = "id")
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
