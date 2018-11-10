package org.backend.test;


import org.backend.test.dto.Account;
import org.backend.test.dto.AccountState;
import org.backend.test.events.AccountEvent;
import org.backend.test.service.AccountService;
import org.backend.test.service.MoneyTransfersService;

import java.util.UUID;

public abstract class AbstractTest {
    AccountService accountService = AccountService.getInstance();
    MoneyTransfersService moneyTransfersService = MoneyTransfersService.getInstance();

    Account createAccount() {
        AccountEvent accountCreated = new AccountEvent(UUID.randomUUID().toString(), AccountState.OPEN);
        return accountService.process(accountCreated);
    }

    Account closeAccount(String accountId) {
        return accountService.process(new AccountEvent(accountId, AccountState.CLOSED));
    }
}
