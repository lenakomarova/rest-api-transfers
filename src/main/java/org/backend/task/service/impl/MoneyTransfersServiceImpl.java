package org.backend.task.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.backend.task.dto.Transfer;
import org.backend.task.dto.TransferError;
import org.backend.task.events.TransferDirection;
import org.backend.task.events.TransferEvent;
import org.backend.task.service.DatabaseService;
import org.backend.task.service.MoneyTransfersService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.backend.task.dto.TransferError.AMOUNT_MUST_BE_POSITIVE;
import static org.backend.task.dto.TransferError.INSUFFICIENT_FUNDS;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor = @_(@Inject))
class MoneyTransfersServiceImpl implements MoneyTransfersService {

    private final DatabaseService databaseService;

    @Override
    public BigDecimal getBalance(Long accountId) {
        return databaseService.getLastTransferEvent(accountId)
                .map(TransferEvent::getCurrentBalance)
                .orElse(BigDecimal.ZERO);
    }

    public Optional<TransferError> transfer(Long debitAccountId, Transfer transfer) {
        if (transfer.getAmount().signum() <= 0) {
            return Optional.of(AMOUNT_MUST_BE_POSITIVE);
        }
        BigDecimal currentDebitBalance = getBalance(debitAccountId);
        if (currentDebitBalance.compareTo(transfer.getAmount()) < 0) {
            return Optional.of(INSUFFICIENT_FUNDS);
        }
        TransferEvent debitEvent = TransferEvent.builder()
                .amount(transfer.getAmount())
                .currentBalance(currentDebitBalance.subtract(transfer.getAmount()))
                .involvedAccountId(transfer.getInvolvedAccount())
                .description(transfer.getDescription())
                .direction(TransferDirection.DEBIT)
                .build();

        databaseService.addTransferEvent(debitAccountId, debitEvent);

        TransferEvent creditEvent = TransferEvent.builder()
                .amount(transfer.getAmount())
                .currentBalance(getBalance(transfer.getInvolvedAccount()).add(transfer.getAmount()))
                .involvedAccountId(debitAccountId)
                .description(transfer.getDescription())
                .direction(TransferDirection.CREDIT)
                .build();

        //TODO: in real db rollback of debit part will be provided automatically by transaction management
        databaseService.addTransferEvent(transfer.getInvolvedAccount(), creditEvent);

        return Optional.empty();
    }

    @Override
    public List<Transfer> getTransfers(Long accountId) {
        return databaseService.getTransferEventsHistory(accountId)
                .stream()
                .map(e -> Transfer.builder()
                        .amount(e.getDirection() == TransferDirection.CREDIT ? e.getAmount() : e.getAmount().negate())
                        .involvedAccount(e.getInvolvedAccountId())
                        .description(e.getDescription())
                        .build())
                .collect(Collectors.toList());
    }
}
