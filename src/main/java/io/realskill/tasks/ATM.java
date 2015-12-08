package io.realskill.tasks;

public class ATM {

    private Connection connection;

    public ATM(Connection connection)
    {
        this.connection = connection;
    }

    public boolean withdraw(int cardNo, int pin, int amount)
    {
        if (!connection.isConnected()) {
            throw new IllegalStateException();
        }
        if (!connection.isCredentialsValid(cardNo, pin)) {
            throw new SecurityException();
        }
        return connection.withdraw(cardNo, pin, amount);
    }
}
