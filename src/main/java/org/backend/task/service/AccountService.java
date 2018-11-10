package org.backend.task.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.backend.task.dto.Account;
import org.backend.task.dto.AccountState;
import org.backend.task.dto.Transfer;
import org.backend.task.dto.TransferError;
import org.backend.task.events.AccountStateEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.backend.task.dto.TransferError.ACCOUNT_NOT_EXISTS;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountService {
    private static final AccountService INSTANCE = new AccountService();
    public static AccountService getInstance() {
        return INSTANCE;
    }
    static final AtomicLong ID_GENERATOR = new AtomicLong();

    private final MoneyTransfersService moneyTransfers = MoneyTransfersService.getInstance();
    private final Map<Long, LinkedList<AccountStateEvent>> accountEvents = new HashMap<>();

    public Optional<Account> findById(Long accountId) {
        LinkedList<AccountStateEvent> events = accountEvents.get(accountId);
        if (events == null || events.isEmpty()) {
            log.warn("account not found by id: {}", accountId);
            return Optional.empty();
        }
        return Optional.of(Account.builder()
                .id(accountId)
                .state(events.getLast().getState())
                .balance(moneyTransfers.getBalance(accountId))
                .build());
    }

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

    public Optional<Account> create() {
        AccountStateEvent event = new AccountStateEvent(ID_GENERATOR.incrementAndGet(), AccountState.OPEN);
        return process(event);
    }

    public Optional<Account> close(long id) {
        AccountStateEvent event = new AccountStateEvent(id, AccountState.CLOSED);
        return process(event);
    }

    Optional<Account> process(AccountStateEvent event) {
        log.info("Received event, {}", event);
        accountEvents.computeIfAbsent(event.getAccountId(), id -> new LinkedList<>()).add(event);
        return findById(event.getAccountId());
    }

    public List<Account> findAll() {
        return accountEvents.keySet().stream().map(this::findById).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    public List<Transfer> getTransfers(Long accountId) {
        return moneyTransfers.getTransfers(accountId);
    }
}
