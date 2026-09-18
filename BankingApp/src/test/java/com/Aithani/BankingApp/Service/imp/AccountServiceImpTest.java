package com.Aithani.BankingApp.Service.imp;

import com.Aithani.BankingApp.Entity.*;
import com.Aithani.BankingApp.Exception.ActiveLoanExistsException;
import com.Aithani.BankingApp.Exception.InsufficientBalanceException;
import com.Aithani.BankingApp.Exception.InvalidLoanAmountException;
import com.Aithani.BankingApp.Exception.NoActiveLoanException;
import com.Aithani.BankingApp.Repository.AccountRepository;
import com.Aithani.BankingApp.Repository.LoanRepository;
import com.Aithani.BankingApp.Repository.TransactionRepository;
import dto.AccountDto;
import dto.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountServiceImpTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private LoanRepository loanRepository;
    @Mock
    private TransactionRepository transactionRepository;

    private AccountServiceImp accountService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

        accountService = new AccountServiceImp(accountRepository,loanRepository,transactionRepository);
    }
    @Test
    void quickLoan_shouldApproveLoan_whenRequestIsValid() {

        Account account = new Account();
        account.setId(1L);
        account.setAccountHolderName("Azariah");
        account.setBalance(5000);

        when(loanRepository.existsByAccountIdAndStatus(
                1L,
                LoanStatus.ACTIVE
        )).thenReturn(false);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AccountDto result = accountService.quickLoan(1L, 1000);

        assertEquals(6000, result.getBalance());

        verify(loanRepository).save(any(Loan.class));
        verify(accountRepository).save(account);
        verify(transactionRepository).save(any(Transaction.class));
    }
    @Test
    void quickLoan_shouldThrowException_whenAmountIsInvalid() {

        assertThrows(
                InvalidLoanAmountException.class,
                () -> accountService.quickLoan(1L, 0)
        );
    }
    @Test
    void quickLoan_shouldThrowException_whenActiveLoanAlreadyExists() {

        when(loanRepository.existsByAccountIdAndStatus(
                1L,
                LoanStatus.ACTIVE
        )).thenReturn(true);

        assertThrows(
                ActiveLoanExistsException.class,
                () -> accountService.quickLoan(1L, 1000)
        );
    }
    @Test
    void repayLoan_shouldCloseLoan_whenRepaymentIsSuccessful() {

        Account account = new Account();
        account.setId(1L);
        account.setAccountHolderName("Azariah");
        account.setBalance(5000);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setAmount(1000.0);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setAccount(account);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(loanRepository.findByAccountIdAndStatus(
                1L,
                LoanStatus.ACTIVE
        )).thenReturn(Optional.of(loan));

        AccountDto result = accountService.repayLoan(1L);

        assertEquals(4000, result.getBalance());
        assertEquals(LoanStatus.CLOSED, loan.getStatus());

        verify(accountRepository).save(account);
        verify(loanRepository).save(loan);
    }
    @Test
    void repayLoan_shouldThrowException_whenNoActiveLoanExists() {

        Account account = new Account();
        account.setId(1L);
        account.setAccountHolderName("Azariah");
        account.setBalance(5000);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(loanRepository.findByAccountIdAndStatus(
                1L,
                LoanStatus.ACTIVE
        )).thenReturn(Optional.empty());

        assertThrows(
                NoActiveLoanException.class,
                () -> accountService.repayLoan(1L)
        );
    }
    @Test
    void repayLoan_shouldThrowException_whenBalanceIsInsufficient() {

        Account account = new Account();
        account.setId(1L);
        account.setAccountHolderName("Azariah");
        account.setBalance(500);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setAmount(1000.0);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setAccount(account);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(loanRepository.findByAccountIdAndStatus(
                1L,
                LoanStatus.ACTIVE
        )).thenReturn(Optional.of(loan));

        assertThrows(
                InsufficientBalanceException.class,
                () -> accountService.repayLoan(1L)
        );
    }
    @Test
    void deposit_shouldSaveTransaction_whenDepositIsSuccessful() {

        Account account = new Account();
        account.setId(1L);
        account.setAccountHolderName("Test User");
        account.setBalance(5000);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AccountDto result = accountService.deposit(1L, 1000);

        assertEquals(6000, result.getBalance());

        verify(transactionRepository)
                .save(any(Transaction.class));
    }
    @Test
    void withdraw_shouldSaveTransaction_whenWithdrawalIsSuccessful() {

        Account account = new Account();
        account.setId(1L);
        account.setAccountHolderName("Test User");
        account.setBalance(5000);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AccountDto result = accountService.withdraw(1L, 1000);

        assertEquals(4000, result.getBalance());

        verify(transactionRepository)
                .save(any(Transaction.class));
    }
    @Test
    void getTransactionHistory_shouldReturnTransactions_whenAccountExists() {

        Account account = new Account();
        account.setId(1L);
        account.setAccountHolderName("Test User");
        account.setBalance(5000);

        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setAmount(1000);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setAccount(account);

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        when(transactionRepository
                .findByAccountIdOrderByTransactionTimeDesc(1L))
                .thenReturn(List.of(transaction));

        List<TransactionDto> result =
                accountService.getTransactionHistory(1L);

        assertEquals(1, result.size());
        assertEquals(1000, result.get(0).getAmount());
        assertEquals(TransactionType.DEPOSIT, result.get(0).getType());

        verify(transactionRepository)
                .findByAccountIdOrderByTransactionTimeDesc(1L);
    }
}
