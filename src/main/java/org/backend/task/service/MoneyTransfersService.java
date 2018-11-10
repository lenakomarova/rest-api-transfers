package org.backend.task.service;

import lombok.extern.slf4j.Slf4j;
import org.backend.task.dto.Transfer;
import org.backend.task.dto.TransferError;
import org.backend.task.events.TransferDirection;
import org.backend.task.events.TransferEvent;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import static org.backend.task.dto.TransferError.INSUFFICIENT_FUNDS;

@Slf4j
public class MoneyTransfersService {

    private final Map<String, LinkedList<TransferEvent>> transferEvents = new HashMap<>();

    public BigDecimal getBalance(String accountId) {
        LinkedList<TransferEvent> transfers = transferEvents.get(accountId);
        if (transfers == null) {
            return BigDecimal.ZERO;
        }
        return transfers.getLast().getCurrentBalance();
    }

    Optional<TransferError> transfer(Transfer transfer) {
        TransferEvent debitEvent = TransferEvent.builder()
                .amount(transfer.getAmount())
                .currentBalance(getBalance(transfer.getDebitAccountId()).subtract(transfer.getAmount()))
                .involvedAccountId(transfer.getCreditAccountId())
                .description(transfer.getDescription())
                .direction(TransferDirection.DEBIT)
                .build();
        TransferEvent creditEvent = TransferEvent.builder()
                .amount(transfer.getAmount())
                .currentBalance(getBalance(transfer.getCreditAccountId()).add(transfer.getAmount()))
                .involvedAccountId(transfer.getDebitAccountId())
                .description(transfer.getDescription())
                .direction(TransferDirection.CREDIT)
                .build();

        return Optional.ofNullable(submit(transfer.getDebitAccountId(), debitEvent)
                .orElse(submit(transfer.getCreditAccountId(), creditEvent).orElse(null)));
    }

    Optional<TransferError> submit(String accountId, TransferEvent event) {
        if (event.getDirection() == TransferDirection.DEBIT && event.getCurrentBalance().signum() == -1) {
            return Optional.of(INSUFFICIENT_FUNDS);
        }

        LinkedList<TransferEvent> transfers = transferEvents.computeIfAbsent(accountId, id -> new LinkedList<>());
        transfers.add(event);
        return Optional.empty();
    }

    private static final MoneyTransfersService INSTANCE = new MoneyTransfersService();

    private MoneyTransfersService() {
    }

    public static MoneyTransfersService getInstance() {
        return INSTANCE;
    }


}
