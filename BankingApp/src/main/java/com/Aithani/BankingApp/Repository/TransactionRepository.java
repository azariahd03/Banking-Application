package com.Aithani.BankingApp.Repository;

import com.Aithani.BankingApp.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    List<Transaction> findByAccountIdOrderByTransactionTimeDesc(Long accountId);
}
