package com.stackroute.transactionsmanagement.repository;

import com.stackroute.transactionsmanagement.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,Long> {
}
