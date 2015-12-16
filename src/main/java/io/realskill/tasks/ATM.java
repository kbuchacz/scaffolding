package io.realskill.tasks;

import javax.inject.Inject;

public class ATM {

    @Inject
    private ATMCentral connection;

    public ATM() {
    }

    public Double withdraw(double cardNo, int pin, double amount) {
        if (!connection.isConnected()) {
            throw new IllegalStateException();
        }
        return connection.withdraw(cardNo, pin, amount);
    }

    public Double deposit(double cardNo, double amount) {
        if (!connection.isConnected()) {
            throw new IllegalStateException();
        }
        return connection.deposit(cardNo, amount);
    }

}
