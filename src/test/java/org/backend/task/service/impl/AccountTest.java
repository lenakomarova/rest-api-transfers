package org.backend.task.service.impl;

import org.backend.task.dto.Account;
import org.backend.task.dto.AccountState;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

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

    @Test
    public void getAll() {
        final int numAccounts = 42;
        IntStream.range(0, numAccounts).forEach(i -> accountService.create());

        List<Account> accounts = accountService.findAll();

        Assert.assertEquals(numAccounts, accounts.size());
    }

}
