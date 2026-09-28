package com.Aithani.BankingApp.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="transactions",
        indexes = {
                @Index(name = "idx_transactions_account_time",
                        columnList = "account_id, transaction_time")
        }
        )

public class Transaction {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;


}
