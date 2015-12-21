package io.realskill.tasks;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@SessionScoped
@Named
public class ATM implements Serializable {

    static final long serialVersionUID = 20L;

    @Inject
    private ATMCentral connection;

    public ATM()
    {
    }

    public ATMCentral getConnection()
    {
        return connection;
    }

    public void setConnection(ATMCentral conn)
    {
        connection = conn;
    }

    public Double currentStatus(double cardNo, int pin)
    {
        if (!connection.connect()) {
            throw new IllegalStateException();
        }

        Double status = connection.currentStatus(cardNo, pin);
        connection.disconnect();
        return status;
    }

    public Double deposit(double cardNo, double amount)
    {
        if (!connection.connect()) {
            throw new IllegalStateException();
        }

        Double deposit = connection.deposit(cardNo, amount);
        connection.disconnect();
        return deposit;
    }

    public Double withdraw(double cardNo, int pin, double amount)
    {
        if (!connection.connect()) {
            throw new IllegalStateException();
        }

        Double withdrawal = connection.withdraw(cardNo, pin, amount);
        connection.disconnect();
        return withdrawal;
    }
}