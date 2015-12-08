package io.realskill.tasks;

public interface Connection {

    boolean isConnected();

    boolean isCredentialsValid(int cardNo, int pin);

    boolean withdraw(int cardNo, int pin, int amount);
}
