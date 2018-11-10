package org.backend.test.service;


import org.backend.test.dto.Account;
import org.backend.test.dto.AccountState;
import org.backend.test.events.AccountEvent;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class AbstractTest {
    AccountService accountService = AccountService.getInstance();
    MoneyTransfersService moneyTransfersService = MoneyTransfersService.getInstance();

    Account createAccount() {
        return accountService.process(new AccountEvent(UUID.randomUUID().toString(), AccountState.OPEN))
                .orElseThrow(() -> new RuntimeException("Couldn't create account"));
    }

    Account closeAccount(String accountId) {
        return accountService.process(new AccountEvent(accountId, AccountState.CLOSED))
                .orElseThrow(() -> new RuntimeException("Couldn't close account"));
    }

    void checkStateAndBalance(AccountState state, BigDecimal balance, Optional<Account> account) {
        assertTrue(account.isPresent());
        assertEquals(state, account.get().getState());
        assertEquals(balance, account.get().getBalance());
    }
}
