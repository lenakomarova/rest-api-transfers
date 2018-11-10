package org.backend.task.events;

import lombok.Value;
import org.backend.task.dto.AccountState;

import java.time.LocalDateTime;

@Value
public class AccountStateEvent {
    final long accountId;
    final AccountState state;
    final LocalDateTime time = LocalDateTime.now();

}
