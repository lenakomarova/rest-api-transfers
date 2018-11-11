package org.backend.task.service.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.backend.task.service.AccountService;
import org.backend.task.service.DatabaseService;
import org.backend.task.service.MoneyTransfersService;
import org.backend.task.service.ServiceFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceFactoryImpl implements ServiceFactory {

    public static ServiceFactory INSTANCE = new ServiceFactoryImpl();

    final DatabaseService DATABASE_SERVICE = new DatabaseServiceImpl();
    final MoneyTransfersService MONEY_TRANSFERS_SERVICE = new MoneyTransfersServiceImpl(DATABASE_SERVICE);
    final AccountService ACCOUNT_SERVICE = new AccountServiceImpl(MONEY_TRANSFERS_SERVICE, DATABASE_SERVICE);

    @Override
    public AccountService accountService() {
        return ACCOUNT_SERVICE;
    }

}
