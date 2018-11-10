package org.backend.task.events;

import lombok.Value;
import org.backend.task.dto.AccountState;

import java.time.LocalDateTime;

@Value
public class AccountStateEvent {
    final String accountId;
    final AccountState state;
    final LocalDateTime time;

    public AccountStateEvent(String accountId, AccountState state) {
        this.accountId = accountId;
        this.state = state;
        this.time = LocalDateTime.now();
    }
}
