package com.Aithani.BankingApp.Repository;

import com.Aithani.BankingApp.Entity.Account;
import com.Aithani.BankingApp.Entity.Loan;
import com.Aithani.BankingApp.Entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan,Long > {

    boolean existsByAccountIdAndStatus(Long accountId, LoanStatus status);


    Optional<Loan> findByAccountIdAndStatus(Long accountId, LoanStatus status);
}
