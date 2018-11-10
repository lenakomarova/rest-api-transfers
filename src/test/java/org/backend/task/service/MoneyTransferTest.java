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
        unlimitedAccount = AccountService.getInstance().process(new AccountStateEvent(-1, AccountState.OPEN)).get();

        TransferEvent transferEvent = TransferEvent.builder()
                .amount(BigDecimal.valueOf(Long.MAX_VALUE))
                .currentBalance(BigDecimal.valueOf(Long.MAX_VALUE))
                .description("Unlimited Money Account")
                .direction(TransferDirection.CREDIT)
                .involvedAccountId(-1)
                .build();
        MoneyTransfersService.getInstance().submit(unlimitedAccount.getId(), transferEvent);
    }

    @Test
    public void successfulCredit() {
        Account myAccount = createAccount();
        Transfer transfer = Transfer.builder()
                .amount(BigDecimal.TEN)
                .involvedAccount(myAccount.getId())
                .description("test credit transfer")
                .build();

        accountService.transfer(unlimitedAccount.getId(), transfer);

        Optional<Account> account = accountService.findById(myAccount.getId());
        checkStateAndBalance(AccountState.OPEN, BigDecimal.TEN, account);
    }

    @Test
    public void successfulDebit() {
        Account myAccount = createAccount();
        Transfer creditTransfer = Transfer.builder()
                .amount(BigDecimal.TEN)
                .involvedAccount(myAccount.getId())
                .description("test credit transfer")
                .build();
        Transfer debitTransfer = Transfer.builder()
                .amount(BigDecimal.valueOf(3))
                .involvedAccount(unlimitedAccount.getId())
                .description("test debit transfer")
                .build();

        accountService.transfer(unlimitedAccount.getId(), creditTransfer);
        accountService.transfer(myAccount.getId(), debitTransfer);

        Optional<Account> account = accountService.findById(myAccount.getId());
        checkStateAndBalance(AccountState.OPEN, BigDecimal.valueOf(7), account);
    }

    @Test
    public void insufficientFunds() {
        Account myAccount = createAccount();
        Transfer creditTransfer = Transfer.builder()
                .amount(BigDecimal.TEN)
                .involvedAccount(myAccount.getId())
                .description("test credit transfer")
                .build();
        Transfer debitTransfer = Transfer.builder()
                .amount(BigDecimal.valueOf(11))
                .involvedAccount(unlimitedAccount.getId())
                .description("test debit transfer")
                .build();

        accountService.transfer(unlimitedAccount.getId(), creditTransfer);
        Optional<TransferError> error = accountService.transfer(myAccount.getId(), debitTransfer);

        Optional<Account> account = accountService.findById(myAccount.getId());
        checkStateAndBalance(AccountState.OPEN, BigDecimal.TEN, account);
        assertTrue(error.isPresent());
        assertEquals(TransferError.INSUFFICIENT_FUNDS, error.get());
    }

    @Test
    public void accountNotFound() {
        long fakeAccId = 100500;
        Transfer transfer = Transfer.builder()
                .amount(BigDecimal.TEN)
                .involvedAccount(fakeAccId)
                .description("test credit transfer")
                .build();

        Optional<TransferError> error = accountService.transfer(unlimitedAccount.getId(), transfer);

        Optional<Account> account = accountService.findById(fakeAccId);
        assertFalse(account.isPresent());
        assertTrue(error.isPresent());
        assertEquals(TransferError.ACCOUNT_NOT_EXISTS, error.get());
    }

    @Test
    public void negativeAmount() {
        Account myAccount = createAccount();
        Transfer transfer = Transfer.builder()
                .amount(BigDecimal.valueOf(-10))
                .involvedAccount(myAccount.getId())
                .description("test negative amount transfer")
                .build();

        Optional<TransferError> error = accountService.transfer(unlimitedAccount.getId(), transfer);

        assertTrue(error.isPresent());
        assertEquals(TransferError.AMOUNT_MUST_BE_POSITIVE, error.get());
    }



}
