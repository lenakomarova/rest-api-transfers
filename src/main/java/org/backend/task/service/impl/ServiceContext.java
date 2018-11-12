package org.backend.task.service.impl;


import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.AccessLevel;
import lombok.Getter;
import org.backend.task.service.AccountService;
import org.backend.task.service.DatabaseService;
import org.backend.task.service.LockingService;
import org.backend.task.service.MoneyTransfersService;

public class ServiceContext {
    @Getter
    private final AccountService accountService;
    @Getter(AccessLevel.PACKAGE)
    private final DatabaseService databaseService;
    @Getter(AccessLevel.PACKAGE)
    private final LockingService lockingService;

    public static final ServiceContext INSTANCE = new ServiceContext();

    ServiceContext() {
        final Injector injector = Guice.createInjector(new ServiceModule());
        accountService = injector.getInstance(SynchronizedAccountServiceImpl.class);
        databaseService = injector.getInstance(DatabaseService.class);
        lockingService = injector.getInstance(LockingService.class);
    }

    private static class ServiceModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(AccountService.class).to(AccountServiceImpl.class).asEagerSingleton();
            bind(DatabaseService.class).to(DatabaseServiceImpl.class).asEagerSingleton();
            bind(LockingService.class).to(LockingServiceImpl.class).asEagerSingleton();
            bind(MoneyTransfersService.class).to(MoneyTransfersServiceImpl.class).asEagerSingleton();

            bind(SynchronizedAccountServiceImpl.class).asEagerSingleton();
        }
    }
}
