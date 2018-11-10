package org.backend.task.service;


import org.backend.task.dto.Account;
import org.backend.task.dto.AccountState;
import org.backend.task.events.AccountStateEvent;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class AbstractTest {
    AccountService accountService = AccountService.getInstance();
    MoneyTransfersService moneyTransfersService = MoneyTransfersService.getInstance();

    Account createAccount() {
        return accountService.create()
                .orElseThrow(() -> new RuntimeException("Couldn't create account"));
    }

    Account closeAccount(long accountId) {
        return accountService.close(accountId)
                .orElseThrow(() -> new RuntimeException("Couldn't close account"));
    }

    void checkStateAndBalance(AccountState state, BigDecimal balance, Optional<Account> account) {
        assertTrue(account.isPresent());
        assertEquals(state, account.get().getState());
        assertEquals(balance, account.get().getBalance());
    }
}
