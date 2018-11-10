package org.backend.test;

import org.backend.test.dto.Account;
import org.backend.test.dto.AccountState;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.*;

public class AccountTest extends AbstractTest {

    @Test
    public void create() {
        Account account = createAccount();

        checkStateAndBalance(AccountState.OPEN, BigDecimal.ZERO, account);
    }

    @Test
    public void findByIdStored() {
        Account accountCreated = createAccount();

        Account account = accountService.findById(accountCreated.getId());

        checkStateAndBalance(AccountState.OPEN, BigDecimal.ZERO, account);
    }


    @Test
    public void close() {
        Account accountCreated = createAccount();

        closeAccount(accountCreated.getId());

        Account account = accountService.findById(accountCreated.getId());
        checkStateAndBalance(AccountState.CLOSED, BigDecimal.ZERO, account);
    }

    private void checkStateAndBalance(AccountState state, BigDecimal balance, Account account) {
        assertNotNull(account);
        assertEquals(state, account.getState());
        assertEquals(balance, account.getBalance());
    }
}
