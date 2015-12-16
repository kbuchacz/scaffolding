package io.realskill.tasks;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.jboss.logging.Logger;

import javax.inject.Inject;

import static org.junit.Assert.*;


@RunWith(Arquillian.class)
public class ATMTest {

    @Inject
    private ATMCentral connection;

    @Inject
    private ATM atm;

    Logger logger = Logger.getLogger(this.getClass());

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(ATMCentral.class)
                .addClass(ATM.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

//    @Before
//    public void prepare() {
//        atm = new ATM();
//    }

    @Test
    public void dependency_injection_works() throws Exception
    {
        assertNotEquals(connection, null);
    }

    @Test
    public void deposit_work() throws Exception
    {
        connection.connect();
        ATMCentral.CreditCard creditCard = connection.addCard();
        Assert.assertNotNull(creditCard);
        atm.deposit(creditCard.getNumber(), 1000d);
        Assert.assertEquals(connection.currentStatus(creditCard.getNumber(), creditCard.getPin()), 1000d, 0d);
    }

    @Test
    public void withdraw_allOk_passesWithoutException() throws Exception
    {
//        Given
//        Then
        Assert.assertTrue(true);
    }
}
