package io.realskill.tasks;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ATMCentral implements Serializable {

    final static double MAX_CARD_NUMBER = 9999999999999999d;
    final static double MIN_CARD_NUMBER = 1000000000000000d;
    final static int MIN_CARD_PIN = 1000;
    final static int MAX_CARD_PIN = 9999;
    static final long serialVersionUID = 21L;

    boolean isConnected = false;

    static Map<Double, Integer> cardsDb = new HashMap<Double, Integer>();
    static Map<Double, Double> bankDb = new HashMap<Double, Double>();

    public ATMCentral() {
    }

    public CreditCard addCard() {
        Random random = new Random();
        Double cardNumber = MIN_CARD_NUMBER + (MAX_CARD_NUMBER - MIN_CARD_NUMBER) * random.nextDouble();
        Integer cardPin = MIN_CARD_PIN + random.nextInt((MAX_CARD_PIN - MIN_CARD_PIN));
        cardsDb.put(cardNumber, cardPin);
        return new CreditCard(cardNumber, cardPin);
    }

    public Double currentStatus(double cardNo, int pin) {
        if (this.isCredentialsValid(cardNo, pin)) {
            Double status = bankDb.get(cardNo);
            if (null != status) {
                return status;
            } else {
                return 0d;
            }
        } else {
            throw new RuntimeException("Not authorized!!!");
        }
    }

    public Double deposit(double cardNo, double amount) {
        if (bankDb.containsKey(cardNo)) {
            Double status = bankDb.get(cardNo) + amount;
            bankDb.put(cardNo, status);
            return status;
        } else {
            if (cardsDb.containsKey(cardNo)) {
                bankDb.put(cardNo, amount);
                return amount;
            } else {
                throw new RuntimeException("Incorrect or inactive card!!!");
            }
        }
    }

    public Double withdraw(double cardNo, int pin, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("Incorrect amount!");
        }
        if (cardsDb.containsKey(cardNo)) {
            if (this.isCredentialsValid(cardNo, pin)) {
                Double currentAmount = bankDb.get(cardNo);
                if (null != currentAmount && currentAmount >= amount) {
                    double currentValue = bankDb.get(cardNo) - amount;
                    bankDb.put(cardNo, currentValue);
                    return currentValue;
                } else {
                    throw new RuntimeException("Not sufficient funds!");
                }
            } else {
                throw new RuntimeException("Wrong pin!!!");
            }
        } else {
            throw new RuntimeException("Incorrect or inactive card!!!");
        }
    }

    public boolean connect() {
        isConnected = true;
        return isConnected;
    }

    public void disconnect() {
        isConnected = false;
    }

    public boolean isCredentialsValid(double cardNo, int pin) {
        Integer dbPin = cardsDb.get(cardNo);
        return null != dbPin && dbPin.equals(pin);
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
