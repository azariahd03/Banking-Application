package com.Aithani.BankingApp.Exception;

public class ActiveLoanExistsException extends BusinessException{
    public ActiveLoanExistsException(String message) {
        super(message);
    }
}
