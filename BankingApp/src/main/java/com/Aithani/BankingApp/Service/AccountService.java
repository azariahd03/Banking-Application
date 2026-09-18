package com.Aithani.BankingApp.Service;

import dto.AccountDto;
import dto.TransactionDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AccountService
{
    AccountDto createAccount (AccountDto accountDto);

    AccountDto getAccountById(long id);

    AccountDto deposit(Long id, double amount);

    AccountDto withdraw(Long id, double amount);

    List<AccountDto> getAllAccounts();

    void deleteAccount(long id);

    AccountDto transfer(Long idTarget,Long idSource, double amount);

    AccountDto quickLoan(Long id, double amount);

    AccountDto repayLoan(Long id);

    Page<TransactionDto> getTransactionHistory(
            Long accountId,
            int page,
            int size
    );
}
