package com.stackroute.transactionsmanagement.service;

import com.stackroute.transactionsmanagement.model.BankAccount;
import com.stackroute.transactionsmanagement.model.IsolationLevel;
import com.stackroute.transactionsmanagement.repository.BankAccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BankAccountService {

	
    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Transactional
    public void transferMoney(Long fromAccountId, Long toAccountId, double amount, IsolationLevel isolationLevel) {
        //write business logic for transferring money from one account to another
    	BankAccount fromAccount = bankAccountRepository.findById(fromAccountId).orElse(null);
        BankAccount toAccount = bankAccountRepository.findById(toAccountId).orElse(null);
        
        if (fromAccount == null || toAccount == null) {
            throw new RuntimeException("Invalid account numbers");
        }

        if (fromAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }
        
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);
        
        bankAccountRepository.save(fromAccount);
        bankAccountRepository.save(toAccount);
    }
}

