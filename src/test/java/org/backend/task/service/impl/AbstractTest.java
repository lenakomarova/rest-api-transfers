package org.backend.task.service.impl;


import org.backend.task.dto.Account;
import org.backend.task.dto.AccountState;
import org.backend.task.events.TransferDirection;
import org.backend.task.events.TransferEvent;
import org.backend.task.service.AccountService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class AbstractTest {
    static AccountService accountService = SynchronizedServiceFactory.INSTANCE.accountService();

    protected Account createAccount() {
        return accountService.create()
                .orElseThrow(() -> new RuntimeException("Couldn't create account"));
    }

    protected Account closeAccount(long accountId) {
        return accountService.close(accountId)
                .orElseThrow(() -> new RuntimeException("Couldn't close account"));
    }

    protected void checkStateAndBalance(AccountState state, BigDecimal balance, Optional<Account> account) {
        assertTrue(account.isPresent());
        assertEquals(state, account.get().getState());
        assertEquals(balance, account.get().getBalance());
    }

    protected static Account fillAccount(BigDecimal balance){
        Account unlimitedAccount = SynchronizedServiceFactory.INSTANCE.accountService().create().get();

        TransferEvent transferEvent = TransferEvent.builder()
                .amount(balance)
                .currentBalance(balance)
                .description("Unlimited Money Account")
                .direction(TransferDirection.CREDIT)
                .involvedAccountId(-1)
                .build();

        ((ServiceFactoryImpl)ServiceFactoryImpl.INSTANCE).DATABASE_SERVICE.addTransferEvent(unlimitedAccount.getId(), transferEvent);
        return unlimitedAccount;
    }

}
