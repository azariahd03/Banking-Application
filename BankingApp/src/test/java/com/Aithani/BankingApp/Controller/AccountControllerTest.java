package com.Aithani.BankingApp.Controller;

import com.Aithani.BankingApp.Entity.TransactionType;
import com.Aithani.BankingApp.Exception.AccountNotFoundException;
import com.Aithani.BankingApp.Exception.GlobalExceptionHandler;
import com.Aithani.BankingApp.Exception.InvalidLoanAmountException;
import com.Aithani.BankingApp.Service.AccountService;
import dto.AccountDto;
import dto.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AccountControllerTest {

    @Mock
    private AccountService accountService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        AccountController accountController =
                new AccountController(accountService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(accountController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAccount_shouldReturnAccount_whenAccountExists() throws Exception {

        AccountDto accountDto =
                new AccountDto(1L, "Azariah", 5000);

        when(accountService.getAccountById(1L))
                .thenReturn(accountDto);

        mockMvc.perform(get("/api/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountHolderName").value("Azariah"))
                .andExpect(jsonPath("$.balance").value(5000));
    }
    @Test
    void addAccount_shouldCreateAccount_whenRequestIsValid() throws Exception {

        AccountDto createdAccount =
                new AccountDto(1L, "Azariah", 5000);

        when(accountService.createAccount(any(AccountDto.class)))
                .thenReturn(createdAccount);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "accountHolderName": "Azariah",
                              "balance": 5000
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.accountHolderName").value("Azariah"))
                .andExpect(jsonPath("$.balance").value(5000));
    }
    @Test
    void addAccount_shouldReturnBadRequest_whenAccountHolderNameIsEmpty() throws Exception {

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "accountHolderName": "",
                              "balance": 5000
                            }
                            """))
                .andExpect(status().isBadRequest());
    }
    @Test
    void addAccount_shouldReturnBadRequest_whenBalanceIsNegative() throws Exception {

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "accountHolderName": "Azariah",
                              "balance": -100
                            }
                            """))
                .andExpect(status().isBadRequest());
    }
    @Test
    void getAccount_shouldReturnNotFound_whenAccountDoesNotExist() throws Exception {

        when(accountService.getAccountById(999L))
                .thenThrow(new AccountNotFoundException("Account doesn't exist"));

        mockMvc.perform(get("/api/accounts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Account doesn't exist"));
    }
    @Test
    void quickLoan_shouldReturnBadRequest_whenLoanAmountIsInvalid() throws Exception {

        when(accountService.quickLoan(1L, 0))
                .thenThrow(new InvalidLoanAmountException("Loan amount must be greater than zero"));

        mockMvc.perform(post("/api/accounts/1/quick-loan")
                        .param("amount", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Loan amount must be greater than zero"));
    }
    @Test
    void getTransactionHistory_shouldReturnTransactions_whenAccountExists() throws Exception {

        TransactionDto transactionDto =
                new TransactionDto(
                        1L,
                        1000,
                        TransactionType.DEPOSIT,
                        LocalDateTime.now()
                );

        Page<TransactionDto> page =
                new PageImpl<>(List.of(transactionDto));

        when(accountService.getTransactionHistory(1L, 0, 10))
                .thenReturn(page);

        mockMvc.perform(get("/api/accounts/1/transactions")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].amount").value(1000))
                .andExpect(jsonPath("$.content[0].type").value("DEPOSIT"));
    }
}
