package com.Aithani.BankingApp.Exception;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException(String message){
        super(message);
    }
}
