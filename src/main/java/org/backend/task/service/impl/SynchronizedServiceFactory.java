package org.backend.task.service.impl;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.backend.task.service.AccountService;
import org.backend.task.service.LockingService;
import org.backend.task.service.ServiceFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SynchronizedServiceFactory implements ServiceFactory {
    public static final ServiceFactory INSTANCE = new SynchronizedServiceFactory();

    final LockingService LOCKING_SERVICE = new LockingServiceImpl();
    final AccountService ACCOUNT_SERVICE = new SynchronizedAccountService(ServiceFactoryImpl.INSTANCE.accountService(), LOCKING_SERVICE);

    @Override
    public AccountService accountService() {
        return ACCOUNT_SERVICE;
    }
}
