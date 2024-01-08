package com.stackroute.transactionsmanagement.controller;

import com.stackroute.transactionsmanagement.model.IsolationLevel;
import com.stackroute.transactionsmanagement.service.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

	private final BankAccountService bankAccountService;
	private final TransactionTemplate transactionTemplate;

	@Autowired
	public TransactionController(BankAccountService bankAccountService, TransactionTemplate transactionTemplate) {
		this.bankAccountService = bankAccountService;
		this.transactionTemplate = transactionTemplate;
	}

	@PostMapping("/transfer")
	public ResponseEntity<String> transferMoney(@RequestParam Long fromAccountId, @RequestParam Long toAccountId,
			@RequestParam double amount, @RequestParam IsolationLevel isolationLevel) {
		// use try catch and return status code 200 and "Money transferred successful"
		// message if success
		// return Internal Server Error with "Error during money transfer" error message
		try {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					// TODO Auto-generated method stub
					bankAccountService.transferMoney(fromAccountId, toAccountId, amount, isolationLevel);
				}
			});

			return ResponseEntity.ok("Money transferred successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during money transfer");
		}
	}

	// Map the IsolationLevel enum to Spring's Isolation enum
	public int mapIsolationLevel(IsolationLevel isolationLevel) {
    	return switch (isolationLevel) {
		case READ_COMMITTED-> {
			
			yield TransactionDefinition.ISOLATION_READ_COMMITTED;
		}
		case READ_UNCOMMITTED-> {
			
			yield TransactionDefinition.ISOLATION_READ_UNCOMMITTED;
		}
		case REPEATABLE_READ-> {
			
			yield TransactionDefinition.ISOLATION_REPEATABLE_READ;
		}
		case SERIALIZABLE-> {
			
			yield TransactionDefinition.ISOLATION_SERIALIZABLE;
		}
		default-> {yield TransactionDefinition.ISOLATION_DEFAULT;}
		};
    }
}
