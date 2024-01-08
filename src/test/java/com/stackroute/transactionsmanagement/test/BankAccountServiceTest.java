package com.stackroute.transactionsmanagement.test;

import com.stackroute.transactionsmanagement.model.BankAccount;
import com.stackroute.transactionsmanagement.model.IsolationLevel;
import com.stackroute.transactionsmanagement.repository.BankAccountRepository;
import com.stackroute.transactionsmanagement.service.BankAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testTransferMoney_SuccessfulTransfer() {
        // Arrange
        long fromAccountId = 1L;
        long toAccountId = 2L;
        double amount = 100.0;
        IsolationLevel isolationLevel = IsolationLevel.READ_COMMITTED;

        BankAccount fromAccount = new BankAccount(fromAccountId, 200.0);
        BankAccount toAccount = new BankAccount(toAccountId, 100.0);

        Mockito.when(bankAccountRepository.findById(fromAccountId)).thenReturn(java.util.Optional.of(fromAccount));
        Mockito.when(bankAccountRepository.findById(toAccountId)).thenReturn(java.util.Optional.of(toAccount));

        // Act
        bankAccountService.transferMoney(fromAccountId, toAccountId, amount, isolationLevel);

        // Assert
        assertEquals(100.0, fromAccount.getBalance());
        assertEquals(200.0, toAccount.getBalance());
    }

    @Test
    public void testTransferMoney_InsufficientBalance() {
        // Arrange
        long fromAccountId = 3L;
        long toAccountId = 4L;
        double amount = 1000000.0;
        IsolationLevel isolationLevel = IsolationLevel.SERIALIZABLE;

        BankAccount fromAccount = new BankAccount(fromAccountId, 50.0);
        BankAccount toAccount = new BankAccount(toAccountId, 75.0);

        Mockito.when(bankAccountRepository.findById(fromAccountId)).thenReturn(java.util.Optional.of(fromAccount));
        Mockito.when(bankAccountRepository.findById(toAccountId)).thenReturn(java.util.Optional.of(toAccount));

        // Act and Assert
        assertThrows(RuntimeException.class, () ->
                bankAccountService.transferMoney(fromAccountId, toAccountId, amount, isolationLevel));
    }

    @Test
    public void testTransferMoney_FromAccountNotFound() {
        // Arrange
        long fromAccountId = 100L;
        long toAccountId = 5L;
        double amount = 50.0;
        IsolationLevel isolationLevel = IsolationLevel.READ_UNCOMMITTED;

        Mockito.when(bankAccountRepository.findById(fromAccountId)).thenReturn(java.util.Optional.empty());

        // Act and Assert
        assertThrows(RuntimeException.class, () ->
                bankAccountService.transferMoney(fromAccountId, toAccountId, amount, isolationLevel));
    }

    @Test
    public void testTransferMoney_ToAccountNotFound() {
        // Arrange
        long fromAccountId = 6L;
        long toAccountId = 200L;
        double amount = 75.0;
        IsolationLevel isolationLevel = IsolationLevel.REPEATABLE_READ;

        BankAccount fromAccount = new BankAccount(fromAccountId, 100.0);

        Mockito.when(bankAccountRepository.findById(fromAccountId)).thenReturn(java.util.Optional.of(fromAccount));
        Mockito.when(bankAccountRepository.findById(toAccountId)).thenReturn(java.util.Optional.empty());

        // Act and Assert
        assertThrows(RuntimeException.class, () ->
                bankAccountService.transferMoney(fromAccountId, toAccountId, amount, isolationLevel));
    }

    @Test
    public void testTransferMoney_ZeroAmountTransfer() {
        // Arrange
        long fromAccountId = 7L;
        long toAccountId = 8L;
        double amount = 0.0;
        IsolationLevel isolationLevel = IsolationLevel.READ_COMMITTED;

        BankAccount fromAccount = new BankAccount(fromAccountId, 100.0);
        BankAccount toAccount = new BankAccount(toAccountId, 50.0);

        Mockito.when(bankAccountRepository.findById(fromAccountId)).thenReturn(java.util.Optional.of(fromAccount));
        Mockito.when(bankAccountRepository.findById(toAccountId)).thenReturn(java.util.Optional.of(toAccount));

        // Act
        bankAccountService.transferMoney(fromAccountId, toAccountId, amount, isolationLevel);

        // Assert
        assertEquals(100.0, fromAccount.getBalance());
        assertEquals(50.0, toAccount.getBalance());
    }

    @Test
    public void testTransferMoney_ValidTransferWithMinimumAmount() {
        // Arrange
        long fromAccountId = 9L;
        long toAccountId = 10L;
        double amount = 0.01;
        IsolationLevel isolationLevel = IsolationLevel.SERIALIZABLE;

        BankAccount fromAccount = new BankAccount(fromAccountId, 100.0);
        BankAccount toAccount = new BankAccount(toAccountId, 50.0);

        Mockito.when(bankAccountRepository.findById(fromAccountId)).thenReturn(java.util.Optional.of(fromAccount));
        Mockito.when(bankAccountRepository.findById(toAccountId)).thenReturn(java.util.Optional.of(toAccount));

        // Act
        bankAccountService.transferMoney(fromAccountId, toAccountId, amount, isolationLevel);

        // Assert
        assertEquals(99.99, fromAccount.getBalance(), 0.01);
        assertEquals(50.01, toAccount.getBalance(), 0.01);
    }

    @Test
    public void testTransferMoney_ValidTransferWithMaximumAmount() {
        // Arrange
        long fromAccountId = 11L;
        long toAccountId = 12L;
        double amount = Double.MAX_VALUE;
        IsolationLevel isolationLevel = IsolationLevel.READ_UNCOMMITTED;

        BankAccount fromAccount = new BankAccount(fromAccountId, 200.0);
        BankAccount toAccount = new BankAccount(toAccountId, 100.0);

        Mockito.when(bankAccountRepository.findById(fromAccountId)).thenReturn(java.util.Optional.of(fromAccount));
        Mockito.when(bankAccountRepository.findById(toAccountId)).thenReturn(java.util.Optional.of(toAccount));

        // Assert
        assertThrows(RuntimeException.class,() ->  bankAccountService.transferMoney(fromAccountId, toAccountId, amount, isolationLevel));
    }
}
