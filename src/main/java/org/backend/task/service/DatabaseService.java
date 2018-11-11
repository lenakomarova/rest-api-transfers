package org.backend.task.service;


import org.backend.task.events.AccountStateEvent;
import org.backend.task.events.TransferEvent;

import java.util.List;
import java.util.Optional;

public interface DatabaseService {
    List<Long> getAccounts();

    void addAccountStateEvent(Long accountId, AccountStateEvent accountStateEvent);

    Optional<AccountStateEvent> getLastAccountStateEvent(Long accountId);

    void addTransferEvent(Long accountId, TransferEvent transferEvent);

    Optional<TransferEvent> getLastTransferEvent(Long accountId);

    List<TransferEvent> getTransferEventsHistory(Long accountId);

}
