package org.backend.task.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.backend.task.dto.Transfer;
import org.backend.task.dto.TransferError;
import org.backend.task.events.TransferDirection;
import org.backend.task.events.TransferEvent;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.backend.task.dto.TransferError.AMOUNT_MUST_BE_POSITIVE;
import static org.backend.task.dto.TransferError.INSUFFICIENT_FUNDS;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MoneyTransfersService {
    private static final MoneyTransfersService INSTANCE = new MoneyTransfersService();

    public static MoneyTransfersService getInstance() {
        return INSTANCE;
    }

    private final Map<Long, LinkedList<TransferEvent>> transferEvents = new HashMap<>();

    public BigDecimal getBalance(long accountId) {
        LinkedList<TransferEvent> transfers = transferEvents.get(accountId);
        if (transfers == null) {
            return BigDecimal.ZERO;
        }
        return transfers.getLast().getCurrentBalance();
    }

    Optional<TransferError> transfer(Long debitAccountId, Transfer transfer) {
        if (transfer.getAmount().signum() <= 0) {
            return Optional.of(AMOUNT_MUST_BE_POSITIVE);
        }
        TransferEvent debitEvent = TransferEvent.builder()
                .amount(transfer.getAmount())
                .currentBalance(getBalance(debitAccountId).subtract(transfer.getAmount()))
                .involvedAccountId(transfer.getInvolvedAccount())
                .description(transfer.getDescription())
                .direction(TransferDirection.DEBIT)
                .build();
        TransferEvent creditEvent = TransferEvent.builder()
                .amount(transfer.getAmount())
                .currentBalance(getBalance(transfer.getInvolvedAccount()).add(transfer.getAmount()))
                .involvedAccountId(debitAccountId)
                .description(transfer.getDescription())
                .direction(TransferDirection.CREDIT)
                .build();

        return Optional.ofNullable(submit(debitAccountId, debitEvent)
                .orElse(submit(transfer.getInvolvedAccount(), creditEvent).orElse(null)));
    }

    Optional<TransferError> submit(long accountId, TransferEvent event) {
        if (event.getDirection() == TransferDirection.DEBIT && event.getCurrentBalance().signum() == -1) {
            return Optional.of(INSUFFICIENT_FUNDS);
        }

        LinkedList<TransferEvent> transfers = transferEvents.computeIfAbsent(accountId, id -> new LinkedList<>());
        transfers.add(event);
        return Optional.empty();
    }

    public List<Transfer> getTransfers(Long accountId) {
        return transferEvents.get(accountId).stream().map(e -> Transfer
                .builder()
                .amount(e.getDirection() == TransferDirection.CREDIT ? e.getAmount() : e.getAmount().negate())
                .involvedAccount(e.getInvolvedAccountId())
                .description(e.getDescription())
                .build()).collect(Collectors.toList());
    }
}
