package org.backend.test.service;

import lombok.extern.slf4j.Slf4j;
import org.backend.test.dto.Account;
import org.backend.test.events.AccountEvent;

import java.util.*;

@Slf4j
public class AccountService {
    private final MoneyTransfersService moneyTransfers = MoneyTransfersService.getInstance();
    private final Map<String, LinkedList<AccountEvent>> accountEvents = new HashMap<>();

    public Account process(AccountEvent event) {
        log.info("Account event, {}", event);
        accountEvents.computeIfAbsent(event.getAccountId(), id -> new LinkedList<>()).add(event);
        return findById(event.getAccountId());
    }

    public Account findById(String accountId) {
        LinkedList<AccountEvent> events = accountEvents.get(accountId);
        if (events == null || events.size() == 0) {
            log.warn("account not found by id: {}", accountId);
            return null;
        }
        return Account.builder()
                .id(accountId)
                .state(events.getLast().getState())
                .balance(moneyTransfers.getBalance(accountId))
                .build();
    }

    private AccountService() {}

    private static class AccountServiceHolder {
        private static final AccountService INSTANCE = new AccountService();
    }

    public static AccountService getInstance() {
        return AccountServiceHolder.INSTANCE;
    }

}
