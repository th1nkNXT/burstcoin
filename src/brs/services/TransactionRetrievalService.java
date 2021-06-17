package brs.services;

import brs.Transaction;

public interface TransactionRetrievalService {

  public Transaction getTransaction(long transactionId);
}
