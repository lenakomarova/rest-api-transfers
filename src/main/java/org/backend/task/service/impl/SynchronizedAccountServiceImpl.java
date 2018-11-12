package org.backend.task.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.backend.task.dto.Account;
import org.backend.task.dto.Transfer;
import org.backend.task.dto.TransferError;
import org.backend.task.service.AccountService;
import org.backend.task.service.LockingService;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @_(@Inject))
class SynchronizedAccountServiceImpl implements AccountService {
    private final AccountService delegate;
    private final LockingService lockingService;

    @Override
    public Optional<Account> findById(Long accountId) {
        return delegate.findById(accountId);
    }

    @Override
    public Optional<TransferError> transfer(Long debitAccountId, Transfer transfer) {
        return lockingService.invokeConcurrently(() -> delegate.transfer(debitAccountId, transfer), debitAccountId, transfer.getInvolvedAccount());
    }

    @Override
    public Optional<Account> create() {
        return delegate.create();
    }

    @Override
    public Optional<Account> close(Long id) {
        return delegate.close(id);
    }

    @Override
    public List<Account> findAll() {
        return delegate.findAll();
    }

    @Override
    public List<Transfer> getTransfers(Long accountId) {
        return delegate.getTransfers(accountId);
    }
}
