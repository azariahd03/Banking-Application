package com.Aithani.BankingApp.Service.imp;

import com.Aithani.BankingApp.Entity.Account;
import com.Aithani.BankingApp.Entity.Loan;
import com.Aithani.BankingApp.Entity.LoanStatus;
import com.Aithani.BankingApp.Exception.AccountNotFoundException;
import com.Aithani.BankingApp.Exception.ActiveLoanExistsException;
import com.Aithani.BankingApp.Exception.InvalidLoanAmountException;
import com.Aithani.BankingApp.Exception.InsufficientBalanceException;
import com.Aithani.BankingApp.Exception.NoActiveLoanException;
import com.Aithani.BankingApp.Mapper.AccountMapper;
import com.Aithani.BankingApp.Repository.AccountRepository;
import com.Aithani.BankingApp.Repository.TransactionRepository;
import com.Aithani.BankingApp.Service.AccountService;
import com.Aithani.BankingApp.Entity.Transaction;
import com.Aithani.BankingApp.Entity.TransactionType;

import java.time.LocalDateTime;
import dto.AccountDto;
import dto.TransactionDto;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private TransactionRepository transactionRepository;

    public AccountServiceImp(AccountRepository accountRepository,
                             LoanRepository loanRepository,
                             TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.loanRepository = loanRepository;
        this.transactionRepository = transactionRepository;
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
        Account account = accountRepository.findById(id).orElseThrow(()->new AccountNotFoundException("Account doesn't Exist"));

        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, double amount)
    {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account doesn't exist"));

        double total = account.getBalance()+amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save((account));
        Transaction transaction = new Transaction();

        transaction.setAmount(amount);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setAccount(savedAccount);

        transactionRepository.save(transaction);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto withdraw(Long id, double amount)
    {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account doesn't exist"));
        if(account.getBalance()<amount)
        {
            throw new RuntimeException("Insufficient Amount");
        }
        double total = account.getBalance()-amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();

        transaction.setAmount(amount);
        transaction.setType(TransactionType.WITHDRAW);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setAccount(savedAccount);

        transactionRepository.save(transaction);

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
                .orElseThrow(() ->
                        new AccountNotFoundException("Account doesn't exist"));
        accountRepository.deleteById(id);
    }

    @Override
    public AccountDto transfer(Long idTarget, Long idSource, double amount) {

        // Get source account
        Account sourceAccount = accountRepository.findById(idSource)
                .orElseThrow(() ->
                        new AccountNotFoundException("Source account doesn't exist"));

        // Get target account
        Account targetAccount = accountRepository.findById(idTarget)
                .orElseThrow(() ->
                        new AccountNotFoundException("Target account doesn't exist"));

        if (sourceAccount.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient funds in the source account");
        }
        // Save both accounts
        Account savedSourceAccount =
                accountRepository.save(sourceAccount);

        Account savedTargetAccount =
                accountRepository.save(targetAccount);

        Transaction sourceTransaction = new Transaction();
        sourceTransaction.setAmount(amount);
        sourceTransaction.setType(TransactionType.TRANSFER);
        sourceTransaction.setTransactionTime(LocalDateTime.now());
        sourceTransaction.setAccount(sourceAccount);

        transactionRepository.save(sourceTransaction);

        Transaction targetTransaction = new Transaction();
        targetTransaction.setAmount(amount);
        targetTransaction.setType(TransactionType.TRANSFER);
        targetTransaction.setTransactionTime(LocalDateTime.now());
        targetTransaction.setAccount(targetAccount);

        transactionRepository.save(targetTransaction);
        return AccountMapper.mapToAccountDto(savedTargetAccount);
    }

    @Override
    @Transactional
    public AccountDto quickLoan(Long id, double amount) {

        if(amount<=0){
            throw new InvalidLoanAmountException("Loan Amount should be greater than 0");
        }
boolean activeLoanExists =
        loanRepository.existsByAccountIdAndStatus(
                id,
                LoanStatus.ACTIVE
        );

if (activeLoanExists) {
    throw new ActiveLoanExistsException("Active loan already exists");
}

Account account = accountRepository.findById(id)
        .orElseThrow(() ->
                new AccountNotFoundException("Account doesn't exist"));
double balance = account.getBalance();

        if (balance < amount * (2.0 / 3.0)) {
            throw new InsufficientBalanceException("Insufficient Amount");
        }
        Loan loan = new Loan();
        loan.setAmount(amount);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setAccount(account);

        loanRepository.save(loan);

        account.setBalance(balance + amount);
        Account savedAccount = accountRepository.save(account);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(TransactionType.LOAN_CREDIT);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setAccount(savedAccount);

        transactionRepository.save(transaction);

        return AccountMapper.mapToAccountDto(savedAccount);
    }
    @Override
    @Transactional
    public AccountDto repayLoan(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() ->
                                new AccountNotFoundException("Account doesn't exist"));
        Loan loan = loanRepository
                .findByAccountIdAndStatus(id, LoanStatus.ACTIVE)
                .orElseThrow(() ->
                        new NoActiveLoanException("No active loan found"));

        double loanAmount = loan.getAmount();

        if (account.getBalance() < loanAmount) {
            throw new InsufficientBalanceException("Insufficient balance to repay loan");
        }

        account.setBalance(account.getBalance() - loanAmount);

        loan.setStatus(LoanStatus.CLOSED);

        accountRepository.save(account);
        loanRepository.save(loan);
        Transaction transaction = new Transaction();
        transaction.setAmount(loanAmount);
        transaction.setType(TransactionType.LOAN_REPAYMENT);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setAccount(account);

        transactionRepository.save(transaction);

        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public Page<TransactionDto> getTransactionHistory(
            Long accountId,
            int page,
            int size) {

        accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account doesn't exist"));

        Pageable pageable = PageRequest.of(page, size);

        Page<Transaction> transactions =
                transactionRepository
                        .findByAccountIdOrderByTransactionTimeDesc(
                                accountId,
                                pageable
                        );

        return transactions.map(transaction ->
                new TransactionDto(
                        transaction.getId(),
                        transaction.getAmount(),
                        transaction.getType(),
                        transaction.getTransactionTime()
                )
        );
    }
}
