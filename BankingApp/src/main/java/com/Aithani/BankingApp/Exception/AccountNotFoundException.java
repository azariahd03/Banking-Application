package com.Aithani.BankingApp.Exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message){
        super(message);
    }
}
