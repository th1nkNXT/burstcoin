package brs.http;

import static brs.http.common.Parameters.SUBSCRIPTION_PARAMETER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.JsonObject;

import brs.BurstException;
import brs.Subscription;
import brs.Transaction;
import brs.common.AbstractUnitTest;
import brs.common.QuickMocker;
import brs.common.QuickMocker.MockParam;
import brs.services.SubscriptionService;
import brs.services.TransactionRetrievalService;
import brs.util.Convert;
import brs.util.JSON;

@RunWith(MockitoJUnitRunner.class)
public class GetSubscriptionsTest extends AbstractUnitTest {

  @Mock
  private SubscriptionService subscriptionServiceMock;

  @Mock
  private TransactionRetrievalService transactionRetrievalServiceMock;

  @InjectMocks
  private GetSubscription t;

  @Test
  public void processRequest() throws BurstException {
    final Long subscriptionId = Long.MAX_VALUE;

    final HttpServletRequest req = QuickMocker.httpServletRequest(
      new MockParam(SUBSCRIPTION_PARAMETER, subscriptionId)
    );

    final Subscription subscription = mock(Subscription.class);
    when(subscription.getId()).thenReturn(subscriptionId);
    when(subscription.getSenderId()).thenReturn(123456789L);
    when(subscription.getRecipientId()).thenReturn(23456789L);
    when(subscription.getAmountNQT()).thenReturn(321L);
    when(subscription.getFrequency()).thenReturn(4);
    when(subscription.getTimeNext()).thenReturn(5);
    when(subscriptionServiceMock.getSubscription(eq(subscriptionId))).thenReturn(subscription);

    
    final Transaction transaction = mock(Transaction.class);
    when(transaction.getTimestamp()).thenReturn(1623878907);
    when(transaction.getBlockTimestamp()).thenReturn(1623878900);
    when(transactionRetrievalServiceMock.getTransaction(eq(subscriptionId))).thenReturn(transaction);

    final JsonObject result = (JsonObject) t.processRequest(req);
    assertNotNull(result);

    assertEquals("" + subscription.getId(), JSON.getAsString(result.get("id")));
    assertEquals("" + subscription.getSenderId(), JSON.getAsString(result.get("sender")));
    assertEquals("" + Convert.rsAccount(subscription.getSenderId()), JSON.getAsString(result.get("senderRS")));
    assertEquals("" + subscription.getRecipientId(), JSON.getAsString(result.get("recipient")));
    assertEquals("" + Convert.rsAccount(subscription.getRecipientId()), JSON.getAsString(result.get("recipientRS")));
    assertEquals("" + subscription.getAmountNQT(), JSON.getAsString(result.get("amountNQT")));
    assertEquals(subscription.getFrequency(), JSON.getAsInt(result.get("frequency")));
    assertEquals(subscription.getTimeNext(), JSON.getAsInt(result.get("timeNext")));
    assertEquals(transaction.getTimestamp(), JSON.getAsInt(result.get("timestamp")));
    assertEquals(transaction.getBlockTimestamp(), JSON.getAsInt(result.get("blockTimestamp")));
    
    // check for account objects
    JsonObject sender = JSON.getAsJsonObject(result.get("senderAccount"));
    assertEquals("" + subscription.getSenderId(), JSON.getAsString(sender.get("id")));
    assertEquals("" + Convert.rsAccount(subscription.getSenderId()), JSON.getAsString(sender.get("idRS")));
    JsonObject recipient = JSON.getAsJsonObject(result.get("recipientAccount"));
    assertEquals("" + subscription.getRecipientId(), JSON.getAsString(recipient.get("id")));
    assertEquals("" + Convert.rsAccount(subscription.getRecipientId()), JSON.getAsString(recipient.get("idRS")));
  }

}
