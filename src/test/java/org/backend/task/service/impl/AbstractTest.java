package org.backend.task.service.impl;


import org.backend.task.dto.Account;
import org.backend.task.dto.AccountState;
import org.backend.task.events.TransferDirection;
import org.backend.task.events.TransferEvent;
import org.backend.task.service.AccountService;
import org.backend.task.service.DatabaseService;
import org.backend.task.service.LockingService;
import org.junit.Before;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class AbstractTest {

    protected ServiceContext serviceContext;
    protected AccountService accountService;
    protected DatabaseService databaseService;
    protected LockingService lockingService;

    @Before
    public void setUp() {
        serviceContext = new ServiceContext();
        accountService = serviceContext.getAccountService();
        databaseService = serviceContext.getDatabaseService();
        lockingService = serviceContext.getLockingService();
    }

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

    public static Account fillAccount(BigDecimal balance, ServiceContext serviceContext){
        Account unlimitedAccount = serviceContext.getAccountService().create().get();

        TransferEvent transferEvent = TransferEvent.builder()
                .amount(balance)
                .currentBalance(balance)
                .description("Unlimited Money Account")
                .direction(TransferDirection.CREDIT)
                .involvedAccountId(-1)
                .build();

        serviceContext.getDatabaseService().addTransferEvent(unlimitedAccount.getId(), transferEvent);
        return unlimitedAccount;
    }

}
