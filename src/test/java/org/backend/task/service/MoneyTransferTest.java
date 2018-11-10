package org.backend.task.service;

import org.backend.task.dto.Account;
import org.backend.task.dto.AccountState;
import org.backend.task.dto.Transfer;
import org.backend.task.dto.TransferError;
import org.backend.task.events.AccountStateEvent;
import org.backend.task.events.TransferDirection;
import org.backend.task.events.TransferEvent;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MoneyTransferTest extends AbstractTest {

    private static Account unlimitedAccount;

    @BeforeClass
    public static void addMoneyToAccount() {
        unlimitedAccount = AccountService.getInstance().process(new AccountStateEvent("UNLIMITED", AccountState.OPEN)).get();

        TransferEvent transferEvent = TransferEvent.builder()
                .amount(BigDecimal.valueOf(Long.MAX_VALUE))
                .currentBalance(BigDecimal.valueOf(Long.MAX_VALUE))
                .description("Unlimited Money Account")
                .direction(TransferDirection.CREDIT)
                .involvedAccountId("Unknown")
                .build();
        MoneyTransfersService.getInstance().submit(unlimitedAccount.getId(), transferEvent);
    }

    @Test
    public void successfulCredit() {
        Account myAccount = createAccount();
        Transfer transfer = Transfer.builder()
                .amount(BigDecimal.TEN)
                .creditAccountId(myAccount.getId())
                .debitAccountId(unlimitedAccount.getId())
                .description("task credit transfer")
                .build();

        accountService.transfer(transfer);

        Optional<Account> account = accountService.findById(myAccount.getId());
        checkStateAndBalance(AccountState.OPEN, BigDecimal.TEN, account);
    }

    @Test
    public void successfulDebit() {
        Account myAccount = createAccount();
        Transfer creditTransfer = Transfer.builder()
                .amount(BigDecimal.TEN)
                .creditAccountId(myAccount.getId())
                .debitAccountId(unlimitedAccount.getId())
                .description("task credit transfer")
                .build();
        Transfer debitTransfer = Transfer.builder()
                .amount(BigDecimal.valueOf(3))
                .creditAccountId(unlimitedAccount.getId())
                .debitAccountId(myAccount.getId())
                .description("task debit transfer")
                .build();

        accountService.transfer(creditTransfer);
        accountService.transfer(debitTransfer);

        Optional<Account> account = accountService.findById(myAccount.getId());
        checkStateAndBalance(AccountState.OPEN, BigDecimal.valueOf(7), account);
    }

    @Test
    public void insufficientFunds() {
        Account myAccount = createAccount();
        Transfer creditTransfer = Transfer.builder()
                .amount(BigDecimal.TEN)
                .creditAccountId(myAccount.getId())
                .debitAccountId(unlimitedAccount.getId())
                .description("task credit transfer")
                .build();
        Transfer debitTransfer = Transfer.builder()
                .amount(BigDecimal.valueOf(11))
                .creditAccountId(unlimitedAccount.getId())
                .debitAccountId(myAccount.getId())
                .description("task debit transfer")
                .build();

        accountService.transfer(creditTransfer);
        Optional<TransferError> error = accountService.transfer(debitTransfer);

        Optional<Account> account = accountService.findById(myAccount.getId());
        checkStateAndBalance(AccountState.OPEN, BigDecimal.TEN, account);
        assertTrue(error.isPresent());
        assertEquals(TransferError.INSUFFICIENT_FUNDS, error.get());
    }

    @Test
    public void accountNotFound() {
        String fakeAccId = "Fake Account Id";
        Transfer transfer = Transfer.builder()
                .amount(BigDecimal.TEN)
                .creditAccountId(fakeAccId)
                .debitAccountId(unlimitedAccount.getId())
                .description("task credit transfer")
                .build();

        Optional<TransferError> error = accountService.transfer(transfer);

        Optional<Account> account = accountService.findById(fakeAccId);
        assertFalse(account.isPresent());
        assertTrue(error.isPresent());
        assertEquals(TransferError.ACCOUNT_NOT_EXISTS, error.get());
    }



}
