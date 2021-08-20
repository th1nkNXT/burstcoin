package brs.http.api;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class SubscriptionDto {
  public String id;

  public String sender;
  public String senderRS;
  public AccountDto senderAccount;

  public String recipient;
  public String recipientRS;
  public AccountDto recipientAccount;

  public String amountNQT;
  public int frequency;
  public int timeNext;

  // Optional: tx information. Currently only used via GetSubscription.  
  public Integer timestamp;
  public Integer blockTimestamp;
}
