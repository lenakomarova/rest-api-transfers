package org.backend.task.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.backend.task.events.AccountStateEvent;
import org.backend.task.events.TransferEvent;
import org.backend.task.service.DatabaseService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

@Slf4j
public class DatabaseServiceImpl implements DatabaseService {
    private final Map<Long, ConcurrentLinkedDeque<AccountStateEvent>> accountEvents = new ConcurrentHashMap<>();
    private final Map<Long, ConcurrentLinkedDeque<TransferEvent>> transferEvents = new ConcurrentHashMap<>();

    @Override
    public void addAccountStateEvent(Long accountId, AccountStateEvent event) {
        log.info("Received event, {}", event);
        accountEvents.computeIfAbsent(event.getAccountId(), id -> new ConcurrentLinkedDeque<>()).addLast(event);
    }

    @Override
    public Optional<AccountStateEvent> getLastAccountStateEvent(Long accountId) {
        log.debug("Get account {} state events, {}", accountId);
        return Optional.ofNullable(accountEvents.get(accountId)).map(Deque::getLast);
    }

    @Override
    public List<Long> getAccounts() {
        log.debug("Get accounts");
        return accountEvents.keySet().stream().collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    @Override
    public void addTransferEvent(Long accountId, TransferEvent transferEvent) {
        log.info("Received account {} transfer event, {}", accountId, transferEvent);
        transferEvents.computeIfAbsent(accountId, notFoundId -> new ConcurrentLinkedDeque<>()).addLast(transferEvent);
    }

    @Override
    public Optional<TransferEvent> getLastTransferEvent(Long accountId) {
        log.debug("Get account {} last transfer", accountId);
        return Optional.ofNullable(transferEvents.get(accountId)).map(Deque::getLast);
    }

    @Override
    public List<TransferEvent> getTransferEventsHistory(Long accountId) {
        log.debug("Get account {} transfers", accountId);
        return Optional.ofNullable(transferEvents.get(accountId))
                .<List<TransferEvent>>map(ArrayList::new).orElseGet(Collections::emptyList);
    }
}
