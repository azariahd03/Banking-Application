package com.Aithani.BankingApp.Service.imp;

import com.Aithani.BankingApp.Entity.Account;
import com.Aithani.BankingApp.Entity.Loan;
import com.Aithani.BankingApp.Entity.LoanStatus;
import com.Aithani.BankingApp.Mapper.AccountMapper;
import com.Aithani.BankingApp.Repository.AccountRepository;
import com.Aithani.BankingApp.Service.AccountService;
import dto.AccountDto;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.Aithani.BankingApp.Repository.LoanRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountServiceImp implements AccountService
{
    private AccountRepository accountRepository;
    private LoanRepository loanRepository;

    public AccountServiceImp(AccountRepository accountRepository,
                             LoanRepository loanRepository) {
        this.accountRepository = accountRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = AccountMapper.mapToAccount(accountDto);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(long id)
    {
        Account account = accountRepository.findById(id).orElseThrow(()->new RuntimeException("Account doesn't Exist"));

        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, double amount)
    {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(()->new RuntimeException("Account doesn't Exist"));

        double total = account.getBalance()+amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save((account));
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdraw(Long id, double amount)
    {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(()->new RuntimeException("Account doesn't Exist"));

        if(account.getBalance()<amount)
        {
            throw new RuntimeException("Insufficient Amount");
        }
        double total = account.getBalance()-amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map((account) -> AccountMapper.mapToAccountDto(account))
                .collect((Collectors.toList()));
    }

    @Override
    public void deleteAccount(long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(()->new RuntimeException("Account doesn't Exist"));

        accountRepository.deleteById(id);
    }

    @Override
    public AccountDto transfer(Long idTarget, Long idSource, double amount) {
        AccountDto sourceAccountDto = getAccountById(idSource);
        double sourceBalance = sourceAccountDto.getBalance();
        if (sourceBalance < amount) {
            throw new RuntimeException("Insufficient funds in the source account");
        }
        AccountDto sourceUpdatedAccount = withdraw(idSource, amount);
        AccountDto targetUpdatedAccount = deposit(idTarget, amount);
        return targetUpdatedAccount;
    }

    @Override
    @Transactional
    public AccountDto quickLoan(Long id, double amount) {

        if(amount<=0){
            throw new RuntimeException("Loan Amount should be greater than 0");
        }
boolean activeLoanExists =
        loanRepository.existsByAccountIdAndStatus(
                id,
                LoanStatus.ACTIVE
        );

if (activeLoanExists) {
    throw new RuntimeException("Active loan already exists");
}

Account account = accountRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Account doesn't Exist"));

double balance = account.getBalance();

        if (balance < amount * (2.0 / 3.0)) {
            throw new RuntimeException("Insufficient Amount");
        }
        Loan loan = new Loan();
        loan.setAmount(amount);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setAccount(account);

        System.out.println("Amount: " + loan.getAmount());
        System.out.println("Status: " + loan.getStatus());
        System.out.println("Account ID: " + loan.getAccount().getId());

        loanRepository.save(loan);

        account.setBalance(balance + amount);
        Account savedAccount = accountRepository.save(account);

        return AccountMapper.mapToAccountDto(savedAccount);
    }
    @Override
    @Transactional
    public AccountDto repayLoan(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Account doesn't Exist"));

        Loan loan = loanRepository
                .findByAccountIdAndStatus(id, LoanStatus.ACTIVE)
                .orElseThrow(() ->
                        new RuntimeException("No active loan found"));

        double loanAmount = loan.getAmount();

        if (account.getBalance() < loanAmount) {
            throw new RuntimeException("Insufficient balance to repay loan");
        }

        account.setBalance(account.getBalance() - loanAmount);

        loan.setStatus(LoanStatus.CLOSED);

        accountRepository.save(account);
        loanRepository.save(loan);

        return AccountMapper.mapToAccountDto(account);
    }

}
