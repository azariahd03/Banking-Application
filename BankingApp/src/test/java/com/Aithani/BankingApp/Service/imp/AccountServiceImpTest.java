package com.Aithani.BankingApp.Service.imp;

import com.Aithani.BankingApp.Entity.Account;
import com.Aithani.BankingApp.Entity.Loan;
import com.Aithani.BankingApp.Entity.LoanStatus;
import com.Aithani.BankingApp.Exception.ActiveLoanExistsException;
import com.Aithani.BankingApp.Exception.InsufficientBalanceException;
import com.Aithani.BankingApp.Exception.InvalidLoanAmountException;
import com.Aithani.BankingApp.Exception.NoActiveLoanException;
import com.Aithani.BankingApp.Repository.AccountRepository;
import com.Aithani.BankingApp.Repository.LoanRepository;
import dto.AccountDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

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

    private AccountServiceImp accountService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

        accountService = new AccountServiceImp(accountRepository,loanRepository);
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
}
