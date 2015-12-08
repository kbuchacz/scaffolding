package io.realskill.tasks;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;

public class ATMTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void withdraw_notConnected_throwsException() throws Exception
    {
//        Given
        final Connection connectionMock = mock(Connection.class);
        final ATM atm = new ATM(connectionMock);
        expectedException.expect(IllegalStateException.class);
//        When
        atm.withdraw(1, 1, 1);
    }

    @Test
    public void withdraw_allOk_passesWithoutException() throws Exception
    {
//        Given
        final Connection connectionMock = mock(Connection.class);
        Mockito.when(connectionMock.isConnected()).thenReturn(true);
        Mockito.when(connectionMock.isCredentialsValid(1, 2)).thenReturn(true);
        Mockito.when(connectionMock.withdraw(1, 2, 3)).thenReturn(true);
        final ATM atm = new ATM(connectionMock);
//        When
        final boolean result = atm.withdraw(1, 2, 3);
//        Then
        Assert.assertTrue(result);
    }
}
