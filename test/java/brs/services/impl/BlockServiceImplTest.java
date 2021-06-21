package brs.services.impl;
import static brs.schema.Tables.ACCOUNT;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import brs.Account;
import brs.Block;
import brs.Blockchain;
import brs.BlockchainProcessor.BlockOutOfOrderException;
import brs.Burst;
import brs.BurstException.ValidationException;
import brs.Constants;
import brs.Generator;
import brs.db.BurstKey;
import brs.db.sql.DbKey;
import brs.db.sql.DbKey.LongKey;
import brs.db.store.AccountStore;
import brs.db.store.Stores;
import brs.fluxcapacitor.FluxCapacitor;
import brs.fluxcapacitor.FluxValues;
import brs.services.AccountService;
import brs.services.TransactionService;
import brs.util.Convert;
import brs.util.DownloadCacheImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Burst.class)
public class BlockServiceImplTest {

  final int currentHeight = 2;

  @Mock
  private AccountService accountServiceMock;
  
  @Mock
  private TransactionService transactionServiceMock;
  
  @Mock
  private Blockchain blockchainMock;
  
  @Mock
  private DownloadCacheImpl downloadCacheMock;
  
  @Mock
  Stores storesMock;
  
  @Mock
  AccountStore accountStoreMock;
  
  @Mock
  DbKey.LongKeyFactory<Account> accountKeyFactoryMock;
  
  @Mock
  FluxCapacitor fluxCapacitorMock;
  
  @Mock
  private Generator generatorMock;
  
  @InjectMocks
  private BlockServiceImpl t;

  @Before
  public void setUp() {
    mockStatic(Burst.class);
    when(Burst.getBlockchain()).thenReturn(blockchainMock); 
    when(blockchainMock.getHeight()).thenReturn(currentHeight);

    when(fluxCapacitorMock.getValue(eq(FluxValues.MAX_PAYLOAD_LENGTH), eq(currentHeight))).thenReturn(FluxValues.MAX_PAYLOAD_LENGTH.getDefaultValue());
    when(fluxCapacitorMock.getValue(eq(FluxValues.MAX_PAYLOAD_LENGTH), eq(currentHeight+1))).thenReturn(FluxValues.MAX_PAYLOAD_LENGTH.getDefaultValue());
    when(fluxCapacitorMock.getValue(eq(FluxValues.MAX_NUMBER_TRANSACTIONS), eq(currentHeight))).thenReturn(FluxValues.MAX_NUMBER_TRANSACTIONS.getDefaultValue());
    when(fluxCapacitorMock.getValue(eq(FluxValues.MAX_NUMBER_TRANSACTIONS), eq(currentHeight+1))).thenReturn(FluxValues.MAX_NUMBER_TRANSACTIONS.getDefaultValue());
    when(Burst.getFluxCapacitor()).thenReturn(fluxCapacitorMock);
  }

  @DisplayName("Just verify the valid signature of a new block")
  @Test
  public void verifyBlockSignature() throws ValidationException, BlockOutOfOrderException {
    Block block = createValidBlock(currentHeight+1);
    Block prevBlock = createPrevBlock(block);
    when(blockchainMock.getBlock(block.getPreviousBlockId())).thenReturn(prevBlock);

    assertTrue(t.verifyBlockSignature(block));
  }
  
  @DisplayName("Verify that the reward fallback is working as expected.")
  @Test
  public void verifyBlockSignatureWithRewardFallback() throws ValidationException, BlockOutOfOrderException {
    BurstKey burstKey = new LongKey(111L, ACCOUNT.ID.getName());
    when(Burst.getStores()).thenReturn(storesMock);
    when(storesMock.getAccountStore()).thenReturn(accountStoreMock);
    when(accountStoreMock.getAccountKeyFactory()).thenReturn(accountKeyFactoryMock);
    when(accountKeyFactoryMock.newKey(eq(111L))).thenReturn(burstKey);

    Block block = createValidBlock(currentHeight+1);
    Block prevBlock = createPrevBlock(block);
    when(blockchainMock.getBlock(block.getPreviousBlockId())).thenReturn(prevBlock);

    // TODO: test reward account
    Account rewardAccount = new Account(111L);
/*    RewardRecipientAssignment rewardRecipientAssignment = new RewardRecipientAssignment(rewardAccount.id, );
    public RewardRecipientAssignment(Long accountId, Long prevRecipientId, Long recipientId, int fromHeight, BurstKey burstKey) {
      this.accountId = accountId;
      this.prevRecipientId = prevRecipientId;
      this.recipientId = recipientId;
      this.fromHeight = fromHeight;
      this.burstKey = burstKey;
    }
*/
    when(accountServiceMock.getAccount(eq(block.getGeneratorPublicKey()))).thenReturn(rewardAccount);
    assertTrue(t.verifyBlockSignature(block));
  }

  private Block createValidBlock(int height) throws ValidationException {
    return Block.builder()
      .version(3)
      .height(height)
      .generationSignature(Convert.parseHexString("305a98571a8b96f699449dd71eff051fc10a3475bce18c7dac81b3d9316a9780"))
      .generatorPublicKey(Convert.parseHexString("a44e4299354f59919329a0bfbac7d6858873ef06c8db3a6a90158f581478bd38"))
      .payloadHash(Convert.parseHexString("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"))
      .blockSignature(Convert.parseHexString("0271d3d9adae0636d8d7c4a3612848f694690157693847734441f3a499410a0cadfc65aac43802afe39e22725465b9301eae89ae59fdd554835607a40c3b370b"))
      .timestamp(683)
      .transactions(Collections.emptyList())
      .previousBlockId(3444294670862540038L)
      .previousBlockHash(Convert.parseHexString("065d8826c197cc2fc7059b15fedc7d700bc56320095eafb4c1ab115ba0a3979e"))
      .nonce(31687)
      .baseTarget(Constants.INITIAL_BASE_TARGET)
    .build();
  }

  private Block createPrevBlock(Block block) throws ValidationException {
    return Block.builder()
      .version(block.getVersion())
      .height(block.getHeight()-1)
      .blockSignature(Convert.parseHexString("0271d3d9adae0636d8d7c4a3612848f694690157693847734441f3a499410a0cadfc65aac43802afe39e22725465b9301eae89ae59fdd554835607a40c3b370b"))
      .timestamp(block.getTimestamp()-1)
      .transactions(Collections.emptyList())
    .build();
    }
}
