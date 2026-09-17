package dto;  // used to transfer Data between Client and Server

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto
{
    private long id;

    @NotBlank(message = "Account holder name is required")
    @Size(min = 3, max = 50, message = "Account holder name must be between 3 and 50 characters")
    private String accountHolderName;

    @PositiveOrZero(message = "Balance cannot be negative")
    private double balance;

}
