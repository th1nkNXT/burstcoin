package brs.http.api;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class SubscriptionDto {
	public String id;

	// TODO sender and senderRS for backward compatibility.
	// With the type AccountDto there is a new way to provide the data using more reusable code. 
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
