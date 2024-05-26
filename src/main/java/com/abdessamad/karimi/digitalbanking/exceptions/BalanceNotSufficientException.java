package com.abdessamad.karimi.digitalbanking.exceptions;

public class BalanceNotSufficientException extends Exception {
    public BalanceNotSufficientException(String balanceNotSufficien) {
        super(balanceNotSufficien);
    }
}
