package com.stackroute.transactionsmanagement.test;
import com.stackroute.transactionsmanagement.controller.TransactionController;
import com.stackroute.transactionsmanagement.model.IsolationLevel;
import com.stackroute.transactionsmanagement.service.BankAccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionControllerTest {

    @Mock
    private BankAccountService bankAccountService;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testTransferMoney_SuccessfulTransfer() {
        // Arrange
        Long fromAccountId = 1L;
        Long toAccountId = 2L;
        double amount = 100.0;
        IsolationLevel isolationLevel = IsolationLevel.READ_COMMITTED;

        when(transactionTemplate.execute(any(TransactionCallbackWithoutResult.class))).thenAnswer(invocation -> {
            TransactionCallbackWithoutResult callback = invocation.getArgument(0);
            callback.doInTransaction(Mockito.mock(TransactionStatus.class));
            return null;
        });

        // Act
        ResponseEntity<String> response = transactionController.transferMoney(fromAccountId, toAccountId, amount, isolationLevel);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Money transferred successfully", response.getBody());
        verify(bankAccountService).transferMoney(fromAccountId, toAccountId, amount, isolationLevel);
    }

    @Test
    public void testMapIsolationLevel() {
        // Arrange & Act
        int readUncommitted = transactionController.mapIsolationLevel(IsolationLevel.READ_UNCOMMITTED);
        int readCommitted = transactionController.mapIsolationLevel(IsolationLevel.READ_COMMITTED);
        int repeatableRead = transactionController.mapIsolationLevel(IsolationLevel.REPEATABLE_READ);
        int serializable = transactionController.mapIsolationLevel(IsolationLevel.SERIALIZABLE);

        // Assert
        assertEquals(readUncommitted, org.springframework.transaction.annotation.Isolation.READ_UNCOMMITTED.value());
        assertEquals(readCommitted, org.springframework.transaction.annotation.Isolation.READ_COMMITTED.value());
        assertEquals(repeatableRead, org.springframework.transaction.annotation.Isolation.REPEATABLE_READ.value());
        assertEquals(serializable, org.springframework.transaction.annotation.Isolation.SERIALIZABLE.value());
    }

}

