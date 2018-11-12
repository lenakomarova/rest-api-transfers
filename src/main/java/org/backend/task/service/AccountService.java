package org.backend.task.service;


import org.backend.task.dto.Account;
import org.backend.task.dto.Transfer;
import org.backend.task.dto.TransferError;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountService {
    Optional<Account> findById(Long accountId);

    Optional<TransferError> transfer(Long debitAccountId, Transfer transfer);

    Optional<Account> create();

    Optional<Account> close(Long id);

    List<Account> findAll();

    List<Transfer> getTransfers(Long accountId);

    Optional<Account> createAccountWithMoney(BigDecimal amount);
}
