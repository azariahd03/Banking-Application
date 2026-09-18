package com.Aithani.BankingApp.Repository;

import com.Aithani.BankingApp.Entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    Page<Transaction> findByAccountIdOrderByTransactionTimeDesc(
            Long accountId,
            Pageable pageable
    );
}
