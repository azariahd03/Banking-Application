package com.Aithani.BankingApp.Exception;

public class InsufficientBalanceException extends BusinessException{
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
