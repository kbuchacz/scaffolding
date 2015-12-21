package io.realskill.tasks;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolverSystem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import javax.inject.Inject;
import java.io.File;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Arquillian.class)
public class ATMTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Inject
    private ATM atm;

    private ATMCentral connection;

    @InjectMocks
    private ATMCentral spyMock;

    @Test
    public void check_current_status() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        Assert.assertEquals(atm.currentStatus(creditCard.getNumber(), creditCard.getPin()), 0d, 0d);
    }

    @Test
    public void check_status_empty() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        Assert.assertEquals(atm.currentStatus(creditCard.getNumber(), creditCard.getPin()), 0d, 0d);
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
    public void check_status_unauthorized() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Not authorized!!!");
        atm.currentStatus(creditCard.getNumber(), creditCard.getPin() + 1);
    }

    @Test
    public void connection_problem_current_status() throws Exception
    {
        ATMCentral mockConn = Mockito.spy(connection);
        atm.setConnection(mockConn);
        Mockito.when(mockConn.connect()).thenReturn(false);
        expectedException.expect(IllegalStateException.class);
        atm.currentStatus(0d, 0);
    }

    @Test
    public void connection_problem_deposit() throws Exception
    {
        ATMCentral mockConn = Mockito.spy(connection);
        atm.setConnection(mockConn);
        Mockito.when(mockConn.connect()).thenReturn(false);
        expectedException.expect(IllegalStateException.class);
        atm.deposit(0d, 0d);
    }

    @Test
    public void connection_problem_withdraw() throws Exception
    {
        ATMCentral mockConn = Mockito.spy(connection);
        atm.setConnection(mockConn);
        Mockito.when(mockConn.connect()).thenReturn(false);
        expectedException.expect(IllegalStateException.class);
        atm.withdraw(0d, 0, 0d);
    }

    @Deployment
    public static WebArchive createDeployment()
    {
        WebArchive war = ShrinkWrap.create(WebArchive.class)
            .addClass(ATMCentral.class)
            .addClass(ATM.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        MavenResolverSystem resolver = Maven.resolver();
        File[] libraries = resolver.loadPomFromFile("pom.xml")
            .resolve("org.mockito:mockito-core", "org.hamcrest:hamcrest-core:1.3", "org.apache.ant:ant:1.9.6")
            .withTransitivity()
            .as(File.class);

        war.addAsLibraries(libraries);
        return war;
    }

    @Test
    public void deposit_incorrect_card() throws Exception
    {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Incorrect or inactive card!!!");
        atm.deposit(0d, 1000d);
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
    public void deposit_money_on_empty_account() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        atm.deposit(creditCard.getNumber(), 1000d);
        Assert.assertEquals(connection.currentStatus(creditCard.getNumber(), creditCard.getPin()), 1000d, 0d);
    }

    @Test
    public void disconnection_current_status() throws Exception
    {
        ATMCentral mockConn = Mockito.spy(connection);
        atm.setConnection(mockConn);
        ATMCentral.CreditCard creditCard = mockConn.addCard();
        Assert.assertNotNull(creditCard);
        atm.currentStatus(creditCard.getNumber(), creditCard.getPin());
        verify(mockConn, times(1)).disconnect();
    }

    @Test
    public void disconnection_deposit() throws Exception
    {
        ATMCentral mockConn = Mockito.spy(connection);
        atm.setConnection(mockConn);
        ATMCentral.CreditCard creditCard = mockConn.addCard();
        Assert.assertNotNull(creditCard);
        atm.deposit(creditCard.getNumber(), 0d);
        verify(mockConn, times(1)).disconnect();
    }

    @Test
    public void disconnection_deposit_withdraw() throws Exception
    {
        ATMCentral mockConn = Mockito.spy(connection);
        atm.setConnection(mockConn);
        ATMCentral.CreditCard creditCard = mockConn.addCard();
        atm.deposit(creditCard.getNumber(), 1000d);
        Assert.assertNotNull(creditCard);
        atm.withdraw(creditCard.getNumber(), creditCard.getPin(), 1000d);
        verify(mockConn, times(2)).disconnect();
    }

    @Before
    public void prepare()
    {
        connection = atm.getConnection();
    }

    @Test
    public void withdraw_all_funds() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        Assert.assertEquals(atm.deposit(creditCard.getNumber(), 2000d), 2000d, 0d);
        Assert.assertEquals(atm.withdraw(creditCard.getNumber(), creditCard.getPin(), 2000d), 0d, 0d);
    }

    @Test
    public void withdraw_funds() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        Assert.assertEquals(atm.deposit(creditCard.getNumber(), 2000d), 2000d, 0d);
        Assert.assertEquals(atm.withdraw(creditCard.getNumber(), creditCard.getPin(), 1000d), 1000d, 0d);
    }

    @Test
    public void withdraw_incorrect_card() throws Exception
    {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Incorrect or inactive card!!!");
        atm.withdraw(1d, 1111, 2000d);
    }

    @Test
    public void withdraw_incorrect_funds() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Incorrect amount!");
        atm.withdraw(creditCard.getNumber(), creditCard.getPin(), 0d);
    }

    @Test
    public void withdraw_incorrect_pin() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Wrong pin!!!");
        atm.withdraw(creditCard.getNumber(), creditCard.getPin() + 1, 2000d);
    }

    @Test
    public void withdraw_insufficient_funds() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Not sufficient funds!");
        Assert.assertEquals(atm.withdraw(creditCard.getNumber(), creditCard.getPin(), 1000d), 1000d, 0d);
    }

    @Test
    public void withdraw_too_high_amount() throws Exception
    {
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        Assert.assertEquals(atm.deposit(creditCard.getNumber(), 2000d), 2000d, 0d);
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Not sufficient funds!");
        atm.withdraw(creditCard.getNumber(), creditCard.getPin(), 5000d);
    }
}
