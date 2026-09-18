package com.Aithani.BankingApp.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="transactions")
public class Transaction {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    private LocalDateTime transactionTime;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;


}
