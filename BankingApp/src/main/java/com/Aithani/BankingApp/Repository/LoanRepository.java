package com.Aithani.BankingApp.Repository;

import com.Aithani.BankingApp.Entity.Account;
import com.Aithani.BankingApp.Entity.Loan;
import com.Aithani.BankingApp.Entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan,Long > {

    boolean existsByAccountIdAndStatus(Long accountId, LoanStatus status);
}
