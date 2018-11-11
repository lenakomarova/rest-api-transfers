package org.backend.task.service.impl;

import org.backend.task.dto.Account;
import org.backend.task.dto.AccountState;
import org.backend.task.service.AccountService;
import org.backend.task.service.DatabaseService;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
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

    @Test
    public void getAll() {
        DatabaseService databaseService = new DatabaseServiceImpl();
        AccountService accountService = new AccountServiceImpl(new MoneyTransfersServiceImpl(databaseService), databaseService);
        accountService.create(); accountService.create(); accountService.create();

        List<Account> accounts = accountService.findAll();

        Assert.assertEquals(3, accounts.size());
    }

}
