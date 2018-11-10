package org.backend.test.service;

import org.backend.test.dto.Account;
import org.backend.test.dto.AccountState;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

public class AccountTest extends AbstractTest {

    @Test
    public void create() {
        Account account = createAccount();

        checkStateAndBalance(AccountState.OPEN, BigDecimal.ZERO, Optional.of(account));
    }

    @Test
    public void findByIdStored() {
        Account createdAccount = createAccount();

        Optional<Account> account = accountService.findById(createdAccount.getId());

        checkStateAndBalance(AccountState.OPEN, BigDecimal.ZERO, account);
    }


    @Test
    public void close() {
        Account createdAcount = createAccount();

        closeAccount(createdAcount.getId());

        Optional<Account> account = accountService.findById(createdAcount.getId());
        checkStateAndBalance(AccountState.CLOSED, BigDecimal.ZERO, account);
    }

}
