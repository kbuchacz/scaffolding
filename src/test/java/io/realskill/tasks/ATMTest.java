package io.realskill.tasks;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class ATMTest {

    private ATMCentral connection;

    private ATM atm;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void prepare() {
        connection = new ATMCentral();
        atm = new ATM(connection);
    }

    @Test
    public void connection_problem_withdraw() throws Exception
    {
        ATMCentral spyCentral = Mockito.spy(connection);
        Mockito.when(spyCentral.connect()).thenReturn(false);
        atm = new ATM(spyCentral);
        expectedException.expect(IllegalStateException.class);
        atm.withdraw(0d, 0, 0d);
    }

    @Test
    public void connection_problem_deposit() throws Exception
    {
        ATMCentral spyCentral = Mockito.spy(connection);
        Mockito.when(spyCentral.connect()).thenReturn(false);
        atm = new ATM(spyCentral);
        expectedException.expect(IllegalStateException.class);
        atm.deposit(0d, 0d);
    }

    @Test
    public void disconnection_current_status() throws Exception
    {
        ATMCentral spyCentral = Mockito.spy(connection);
        atm = new ATM(spyCentral);
        ATMCentral.CreditCard creditCard = spyCentral.addCard();
        Assert.assertNotNull(creditCard);
        atm.currentStatus(creditCard.getNumber(), creditCard.getPin());
        verify(spyCentral, times(1)).disconnect();
    }

    @Test
    public void disconnection_deposit_withdraw() throws Exception
    {
        ATMCentral spyCentral = Mockito.spy(connection);
        atm = new ATM(spyCentral);
        ATMCentral.CreditCard creditCard = spyCentral.addCard();
        atm.deposit(creditCard.getNumber(), 1000d);
        Assert.assertNotNull(creditCard);
        atm.withdraw(creditCard.getNumber(), creditCard.getPin(), 1000d);
        verify(spyCentral, times(2)).disconnect();
    }

    @Test
    public void disconnection_deposit() throws Exception
    {
        ATMCentral spyCentral = Mockito.spy(connection);
        atm = new ATM(spyCentral);
        ATMCentral.CreditCard creditCard = spyCentral.addCard();
        Assert.assertNotNull(creditCard);
        atm.deposit(creditCard.getNumber(), 0d);
        verify(spyCentral, times(1)).disconnect();
    }

    @Test
    public void connection_problem_current_status() throws Exception
    {
        ATMCentral spyCentral = Mockito.spy(connection);
        Mockito.when(spyCentral.connect()).thenReturn(false);
        atm = new ATM(spyCentral);
        expectedException.expect(IllegalStateException.class);
        atm.currentStatus(0d, 0);
    }

    @Test
    public void check_status_unauthorized() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Not authorized!!!");
        atm.currentStatus(creditCard.getNumber(), creditCard.getPin() + 1);
    }

    @Test
    public void check_status_incorrect_card() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Not authorized!!!");
        atm.currentStatus(1d, creditCard.getPin());
    }

    @Test
    public void check_status_empty() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        Assert.assertEquals(atm.currentStatus(creditCard.getNumber(), creditCard.getPin()), 0d, 0d);
    }

    @Test
    public void check_current_status() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        Assert.assertEquals(atm.currentStatus(creditCard.getNumber(), creditCard.getPin()), 0d, 0d);
    }

    @Test
    public void deposit_money_on_empty_account() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        atm.deposit(creditCard.getNumber(), 1000d);
        Assert.assertEquals(connection.currentStatus(creditCard.getNumber(), creditCard.getPin()), 1000d, 0d);
    }

    @Test
    public void deposit_money_add_funds() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        atm.deposit(creditCard.getNumber(), 1000d);
        Assert.assertEquals(atm.deposit(creditCard.getNumber(), 1000d), 2000d, 0d);
    }

    @Test
    public void deposit_incorrect_card() throws Exception
    {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Incorrect or inactive card!!!");
        atm.deposit(0d, 1000d);
    }

    @Test
    public void withdraw_incorrect_card() throws Exception {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Incorrect or inactive card!!!");
        atm.withdraw(1d, 1111, 2000d);
    }

    @Test
    public void withdraw_incorrect_pin() throws Exception {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Wrong pin!!!");
        atm.withdraw(creditCard.getNumber(), creditCard.getPin() + 1, 2000d);
    }

    @Test
    public void withdraw_too_high_amount() throws Exception {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        Assert.assertEquals(atm.deposit(creditCard.getNumber(), 2000d), 2000d, 0d);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Not sufficient funds!");
        atm.withdraw(creditCard.getNumber(), creditCard.getPin(), 5000d);
    }

    @Test
    public void withdraw_all_funds() throws Exception {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        Assert.assertEquals(atm.deposit(creditCard.getNumber(), 2000d), 2000d, 0d);
        Assert.assertEquals(atm.withdraw(creditCard.getNumber(), creditCard.getPin(), 2000d), 0d, 0d);
    }

    @Test
    public void withdraw_funds() throws Exception {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        Assert.assertEquals(atm.deposit(creditCard.getNumber(), 2000d), 2000d, 0d);
        Assert.assertEquals(atm.withdraw(creditCard.getNumber(), creditCard.getPin(), 1000d), 1000d, 0d);
    }

    @Test
    public void withdraw_insufficient_funds() throws Exception {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Not sufficient funds!");
        Assert.assertEquals(atm.withdraw(creditCard.getNumber(), creditCard.getPin(), 1000d), 1000d, 0d);
    }

    @Test
    public void withdraw_incorrect_funds() throws Exception {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Incorrect amount!");
        atm.withdraw(creditCard.getNumber(), creditCard.getPin(), 0d);
    }

}
