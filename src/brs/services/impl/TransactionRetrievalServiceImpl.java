package brs.services.impl;

import brs.Blockchain;
import brs.Transaction;
import brs.TransactionProcessor;
import brs.services.TransactionRetrievalService;


/**
 * This class provides methods to find and retrieve a transaction regardless 
 * of the current state (including unconfirmed transactions)
 *
 */
public class TransactionRetrievalServiceImpl implements TransactionRetrievalService {
  private final Blockchain blockchain;
  private final TransactionProcessor transactionProcessor;

  public TransactionRetrievalServiceImpl(Blockchain blockchain, TransactionProcessor transactionProcessor) {
    this.blockchain = blockchain;
    this.transactionProcessor = transactionProcessor;
  }

  @Override
  public Transaction getTransaction(long transactionId) {
    Transaction transaction;
    transaction = blockchain.getTransaction(transactionId);

    if (transaction == null) {
      transaction = transactionProcessor.getUnconfirmedTransaction(transactionId);
    }
    
    return transaction;
  }
}
