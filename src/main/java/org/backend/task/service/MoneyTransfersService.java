package org.backend.task.service;


import org.backend.task.dto.Transfer;
import org.backend.task.dto.TransferError;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MoneyTransfersService {
    BigDecimal getBalance(Long accountId);

    List<Transfer> getTransfers(Long accountId);

    Optional<TransferError> transfer(Long debitAccountId, Transfer transfer);
}
