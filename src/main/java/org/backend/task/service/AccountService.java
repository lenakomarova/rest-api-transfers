package org.backend.task.service;

import lombok.extern.slf4j.Slf4j;
import org.backend.task.dto.Account;
import org.backend.task.dto.AccountState;
import org.backend.task.dto.Transfer;
import org.backend.task.dto.TransferError;
import org.backend.task.events.AccountStateEvent;

import java.util.*;

import static org.backend.task.dto.TransferError.ACCOUNT_NOT_EXISTS;

@Slf4j
public class AccountService {
    private final MoneyTransfersService moneyTransfers = MoneyTransfersService.getInstance();
    private final Map<String, LinkedList<AccountStateEvent>> accountEvents = new HashMap<>();

    public Optional<Account> process(AccountStateEvent event) {
        log.info("Received event, {}", event);
        accountEvents.computeIfAbsent(event.getAccountId(), id -> new LinkedList<>()).add(event);
        return findById(event.getAccountId());
    }

    public Optional<Account> findById(String accountId) {
        LinkedList<AccountStateEvent> events = accountEvents.get(accountId);
        if (events == null || events.size() == 0) {
            log.warn("account not found by id: {}", accountId);
            return Optional.empty();
        }
        return Optional.of(Account.builder()
                .id(accountId)
                .state(events.getLast().getState())
                .balance(moneyTransfers.getBalance(accountId))
                .build());
    }

    public Optional<TransferError> transfer(Transfer transfer) {
        log.info("Received {}", transfer);
        Optional<Account> creditAccount = findById(transfer.getCreditAccountId());
        Optional<Account> debitAccount = findById(transfer.getDebitAccountId());
        if (!creditAccount.isPresent() || !debitAccount.isPresent() ||
                creditAccount.get().getState() == AccountState.CLOSED || debitAccount.get().getState() == AccountState.CLOSED) {
            log.warn("Account not found or closed, {}", transfer);
            return Optional.of(ACCOUNT_NOT_EXISTS);
        }
        return moneyTransfers.transfer(transfer);
    }

    private AccountService() {
    }

    private static final AccountService INSTANCE = new AccountService();

    public static AccountService getInstance() {
        return INSTANCE;
    }

}