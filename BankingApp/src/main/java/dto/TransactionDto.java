package dto;

import com.Aithani.BankingApp.Entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private double amount;
    private TransactionType type;
    private LocalDateTime transactionTime;
}
