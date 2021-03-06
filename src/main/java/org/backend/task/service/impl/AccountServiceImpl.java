package org.backend.task.service.impl;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.backend.task.dto.Account;
import org.backend.task.dto.AccountState;
import org.backend.task.dto.Transfer;
import org.backend.task.dto.TransferError;
import org.backend.task.events.AccountStateEvent;
import org.backend.task.events.TransferDirection;
import org.backend.task.events.TransferEvent;
import org.backend.task.service.AccountService;
import org.backend.task.service.DatabaseService;
import org.backend.task.service.MoneyTransfersService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.backend.task.dto.TransferError.ACCOUNT_NOT_EXISTS;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @_(@Inject))
class AccountServiceImpl implements AccountService {
    private final AtomicLong idGenerator = new AtomicLong();

    private final MoneyTransfersService moneyTransfers;
    private final DatabaseService databaseService;

    @Override
    public Optional<Account> findById(Long accountId) {
        Optional<AccountStateEvent> lastEvent = databaseService.getLastAccountStateEvent(accountId);
        if (!lastEvent.isPresent()) {
            log.warn("account not found by id: {}", accountId);
            return Optional.empty();
        }
        return Optional.of(Account.builder()
                .id(accountId)
                .state(lastEvent.get().getState())
                .balance(moneyTransfers.getBalance(accountId))
                .build());
    }

    @Override
    public Optional<TransferError> transfer(Long debitAccountId, Transfer transfer) {
        log.info("Received {}", transfer);
        Optional<Account> creditAccount = findById(transfer.getInvolvedAccount());
        Optional<Account> debitAccount = findById(debitAccountId);
        if (!creditAccount.isPresent() || !debitAccount.isPresent() ||
                creditAccount.get().getState() == AccountState.CLOSED || debitAccount.get().getState() == AccountState.CLOSED) {
            log.warn("Account not found or closed, {}", transfer);
            return Optional.of(ACCOUNT_NOT_EXISTS);
        }
        return moneyTransfers.transfer(debitAccountId, transfer);
    }

    @Override
    public Optional<Account> create() {
        AccountStateEvent event = new AccountStateEvent(idGenerator.incrementAndGet(), AccountState.OPEN);
        databaseService.addAccountStateEvent(event.getAccountId(), event);
        return findById(event.getAccountId());
    }

    @Override
    public Optional<Account> close(Long id) {
        if (!findById(id).isPresent()) {
            log.warn("Account requested to close doesn't exist");
            return Optional.empty();
        }
        AccountStateEvent event = new AccountStateEvent(id, AccountState.CLOSED);
        databaseService.addAccountStateEvent(event.getAccountId(), event);
        return findById(id);
    }

    @Override
    public List<Account> findAll() {
        return databaseService.getAccounts().stream().map(this::findById).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    @Override
    public List<Transfer> getTransfers(Long accountId) {
        return moneyTransfers.getTransfers(accountId);
    }

    @Override
    public Optional<Account> createAccountWithMoney(@NonNull BigDecimal amount) {
        Optional<Account> account = create();
        if (account.isPresent()) {
            databaseService.addTransferEvent(account.get().getId(), TransferEvent.builder()
                    .amount(amount)
                    .currentBalance(amount)
                    .description("Initial money deposit")
                    .direction(TransferDirection.CREDIT)
                    .involvedAccountId(-1)
                    .build());
            return findById(account.get().getId());
        }
        return Optional.empty();
    }
}
