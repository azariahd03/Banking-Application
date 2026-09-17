package com.Aithani.BankingApp.Controller;

import com.Aithani.BankingApp.Service.AccountService;
import dto.AccountDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
    void setup(){
        MockitoAnnotations.openMocks(this);

        AccountController accountController = new AccountController(accountService);

        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
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
}
