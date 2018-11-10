package org.backend.test.events;

import lombok.Data;
import org.backend.test.dto.AccountState;

import java.time.LocalDateTime;

@Data
public class AccountEvent {
    final String accountId;
    final AccountState state;
    final LocalDateTime time;

    public AccountEvent(String accountId, AccountState state) {
        this.accountId = accountId;
        this.state = state;
        this.time = LocalDateTime.now();
    }
}
