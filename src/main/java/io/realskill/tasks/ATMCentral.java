package io.realskill.tasks;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@SessionScoped
public class ATMCentral implements Serializable {

    final static double MAX_CARD_NUMBER = 9999999999999999d;
    final static double MIN_CARD_NUMBER = 1000000000000000d;
    final static int MIN_CARD_PIN = 0000;
    final static int MAX_CARD_PIN = 9999;
    static final long serialVersionUID = 21L;

    boolean isConnected = false;

    static Map<Double, Integer> cardsDb = new HashMap<Double, Integer>();
    static Map<Double, Double> bankDb = new HashMap<Double, Double>();

    @PostConstruct
    public void initialize() {
    }

    public ATMCentral() {
    }

    public CreditCard addCard() {
        Random random = new Random();
        Double cardNumber = MIN_CARD_NUMBER + (MAX_CARD_NUMBER - MIN_CARD_NUMBER) * random.nextDouble();
        Integer cardPin = MIN_CARD_PIN + (MAX_CARD_PIN - MIN_CARD_PIN) * random.nextInt();
        cardsDb.put(cardNumber, cardPin);
        return new CreditCard(cardNumber, cardPin);
    }

    public Double currentStatus(double cardNo, int pin) {
        if (this.isCredentialsValid(cardNo, pin)) {
            return bankDb.get(cardNo);
        } else {
            throw new RuntimeException("Not authorized!!!");
        }
    }

    public Double deposit(double cardNo, double amount) {
        if (bankDb.containsKey(cardNo)) {
            return bankDb.put(cardNo, bankDb.get(cardNo) + amount);
        } else {
            return bankDb.put(cardNo, amount);
        }
    }

    public Double withdraw(double cardNo, int pin, double amount) {
        if (cardsDb.containsKey(cardNo)) {
            if (this.isCredentialsValid(cardNo, pin)) {
                if (bankDb.get(cardNo) < amount) {
                    return bankDb.put(cardNo, bankDb.get(cardNo) - amount);
                } else {
                    throw new RuntimeException("Too large amount of money!!!");
                }
            } else {
                throw new RuntimeException("Wrong pin!!!");
            }
        } else {
            throw new RuntimeException("Wrong card!!!");
        }
    }

    public void connect() {
        isConnected = true;
    }

    public void disconnect() {
        isConnected = false;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public boolean isCredentialsValid(double cardNo, int pin) {
        return cardsDb.get(cardNo).equals(pin);
    }

    public static class CreditCard {
        private double number;
        private int pin;

        public CreditCard(double number, int pin) {
            this.number = number;
            this.pin = pin;
        }

        public double getNumber() {
            return number;
        }

        public int getPin() {
            return pin;
        }
    }
}
