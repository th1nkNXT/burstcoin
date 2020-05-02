package brs.db.sql;

import brs.Burst;
import brs.DigitalGoodsStore;
import brs.crypto.EncryptedData;
import brs.db.BurstKey;
import brs.db.VersionedEntityTable;
import brs.db.VersionedValuesTable;
import brs.db.store.DerivedTableManager;
import brs.db.store.DigitalGoodsStoreStore;
import brs.schema.tables.records.GoodsRecord;
import brs.schema.tables.records.PurchasePublicFeedbackRecord;
import brs.schema.tables.records.PurchaseRecord;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SortField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static brs.schema.Tables.*;

public class SqlDigitalGoodsStoreStore implements DigitalGoodsStoreStore {

  private static final DbKey.LongKeyFactory<DigitalGoodsStore.Purchase> feedbackDbKeyFactory
    = new DbKey.LongKeyFactory<DigitalGoodsStore.Purchase>(PURCHASE.ID) {
        @Override
        public BurstKey newKey(DigitalGoodsStore.Purchase purchase) {
          return purchase.dbKey;
        }
      };

  private final BurstKey.LongKeyFactory<DigitalGoodsStore.Purchase> purchaseDbKeyFactory
    = new DbKey.LongKeyFactory<DigitalGoodsStore.Purchase>(PURCHASE.ID) {
        @Override
        public BurstKey newKey(DigitalGoodsStore.Purchase purchase) {
          return purchase.dbKey;
        }
      };

  private final VersionedEntityTable<DigitalGoodsStore.Purchase> purchaseTable;

  @Deprecated
  private final VersionedValuesTable<DigitalGoodsStore.Purchase, EncryptedData> feedbackTable;

  private final DbKey.LongKeyFactory<DigitalGoodsStore.Purchase> publicFeedbackDbKeyFactory
    = new DbKey.LongKeyFactory<DigitalGoodsStore.Purchase>(PURCHASE.ID) {
        @Override
        public BurstKey newKey(DigitalGoodsStore.Purchase purchase) {
          return purchase.dbKey;
        }
      };

  private final VersionedValuesTable<DigitalGoodsStore.Purchase, String> publicFeedbackTable;

  private final BurstKey.LongKeyFactory<DigitalGoodsStore.Goods> goodsDbKeyFactory = new DbKey.LongKeyFactory<DigitalGoodsStore.Goods>(GOODS.ID) {
      @Override
      public BurstKey newKey(DigitalGoodsStore.Goods goods) {
        return goods.dbKey;
      }
    };

  private final VersionedEntityTable<DigitalGoodsStore.Goods> goodsTable;

  public SqlDigitalGoodsStoreStore(DerivedTableManager derivedTableManager) {
    purchaseTable = new VersionedEntitySqlTable<DigitalGoodsStore.Purchase>("purchase", brs.schema.Tables.PURCHASE, purchaseDbKeyFactory, derivedTableManager) {
      @Override
      protected DigitalGoodsStore.Purchase load(DSLContext ctx, Record rs) {
        return new SQLPurchase(rs);
      }

      @Override
      protected void save(DSLContext ctx, DigitalGoodsStore.Purchase purchase) {
        savePurchase(ctx, purchase);
      }

      @Override
      protected List<SortField<?>> defaultSort() {
        List<SortField<?>> sort = new ArrayList<>();
        sort.add(tableClass.field("timestamp", Integer.class).desc());
        sort.add(tableClass.field("id", Long.class).asc());
        return sort;
      }
    };

    feedbackTable = new VersionedValuesSqlTable<DigitalGoodsStore.Purchase, EncryptedData>("purchase_feedback", brs.schema.Tables.PURCHASE_FEEDBACK, feedbackDbKeyFactory, derivedTableManager) {

      @Override
      protected EncryptedData load(DSLContext ctx, Record record) {
        byte[] data = record.get(PURCHASE_FEEDBACK.FEEDBACK_DATA);
        byte[] nonce = record.get(PURCHASE_FEEDBACK.FEEDBACK_NONCE);
        return new EncryptedData(data, nonce);
      }

      @Override
      protected void save(DSLContext ctx, DigitalGoodsStore.Purchase purchase, EncryptedData encryptedData) {
        byte[] data  = null;
        byte[] nonce = null;
        if ( encryptedData.getData() != null ) {
          data  = encryptedData.getData();
          nonce = encryptedData.getNonce();
        }
        ctx.insertInto(
            PURCHASE_FEEDBACK,
            PURCHASE_FEEDBACK.ID,
            PURCHASE_FEEDBACK.FEEDBACK_DATA, PURCHASE_FEEDBACK.FEEDBACK_NONCE,
            PURCHASE_FEEDBACK.HEIGHT, PURCHASE_FEEDBACK.LATEST
        ).values(
            purchase.getId(),
            data, nonce,
            brs.Burst.getBlockchain().getHeight(), true
        ).execute();
      }
    };

    publicFeedbackTable
        = new VersionedValuesSqlTable<DigitalGoodsStore.Purchase, String>("purchase_public_feedback", brs.schema.Tables.PURCHASE_PUBLIC_FEEDBACK, publicFeedbackDbKeyFactory, derivedTableManager) {

      @Override
      protected String load(DSLContext ctx, Record record) {
        return record.get(PURCHASE_PUBLIC_FEEDBACK.PUBLIC_FEEDBACK);
      }

      @Override
      protected void save(DSLContext ctx, DigitalGoodsStore.Purchase purchase, String publicFeedback) {
    	PurchasePublicFeedbackRecord record = new PurchasePublicFeedbackRecord();
    	record.setId(purchase.getId());
    	record.setPublicFeedback(publicFeedback);
    	record.setHeight(Burst.getBlockchain().getHeight());
    	record.setLatest(true);
    	DbUtils.upsert(ctx, record, PURCHASE_PUBLIC_FEEDBACK.ID, PURCHASE_PUBLIC_FEEDBACK.HEIGHT).execute();
      }
    };

    goodsTable = new VersionedEntitySqlTable<DigitalGoodsStore.Goods>("goods", brs.schema.Tables.GOODS, goodsDbKeyFactory, derivedTableManager) {

      @Override
      protected DigitalGoodsStore.Goods load(DSLContext ctx, Record rs) {
        return new SQLGoods(rs);
      }

      @Override
      protected void save(DSLContext ctx, DigitalGoodsStore.Goods goods) {
        saveGoods(ctx, goods);
      }

      @Override
      protected List<SortField<?>> defaultSort() {
        List<SortField<?>> sort = new ArrayList<>();
        sort.add(brs.schema.Tables.GOODS.field("timestamp", Integer.class).desc());
        sort.add(brs.schema.Tables.GOODS.field("id", Long.class).asc());
        return sort;
      }
    };
  }

  @Override
  public Collection<DigitalGoodsStore.Purchase> getExpiredPendingPurchases(final int timestamp) {
    return getPurchaseTable().getManyBy(PURCHASE.DEADLINE.lt(timestamp).and(PURCHASE.PENDING.isTrue()), 0, -1);
  }

  private EncryptedData loadEncryptedData(Record record, Field<byte[]> dataField, Field<byte[]> nonceField) {
    byte[] data = record.get(dataField);
    if (data == null) {
      return null;
    }
    return new EncryptedData(data, record.get(nonceField));
  }

  @Override
  public BurstKey.LongKeyFactory<DigitalGoodsStore.Purchase> getFeedbackDbKeyFactory() {
    return feedbackDbKeyFactory;
  }

  @Override
  public BurstKey.LongKeyFactory<DigitalGoodsStore.Purchase> getPurchaseDbKeyFactory() {
    return purchaseDbKeyFactory;
  }

  @Override
  public VersionedEntityTable<DigitalGoodsStore.Purchase> getPurchaseTable() {
    return purchaseTable;
  }

  @Override
  public VersionedValuesTable<DigitalGoodsStore.Purchase, EncryptedData> getFeedbackTable() {
    return feedbackTable;
  }

  @Override
  public DbKey.LongKeyFactory<DigitalGoodsStore.Purchase> getPublicFeedbackDbKeyFactory() {
    return publicFeedbackDbKeyFactory;
  }

  public VersionedValuesTable<DigitalGoodsStore.Purchase, String> getPublicFeedbackTable() {
    return publicFeedbackTable;
  }

  @Override
  public BurstKey.LongKeyFactory<DigitalGoodsStore.Goods> getGoodsDbKeyFactory() {
    return goodsDbKeyFactory;
  }

  @Override
  public VersionedEntityTable<DigitalGoodsStore.Goods> getGoodsTable() {
    return goodsTable;
  }

  private void saveGoods(DSLContext ctx, DigitalGoodsStore.Goods goods) {
	GoodsRecord record = new GoodsRecord();
	record.setId(goods.getId());
	record.setSellerId(goods.getSellerId());
	record.setName(goods.getName());
	record.setDescription(goods.getDescription());
	record.setTags(goods.getTags());
	record.setTimestamp(goods.getTimestamp());
	record.setQuantity(goods.getQuantity());
	record.setPrice(goods.getPriceNQT());
	record.setDelisted(goods.isDelisted());
	record.setHeight(Burst.getBlockchain().getHeight());
	record.setLatest(true);
	DbUtils.upsert(ctx, record, GOODS.ID, GOODS.HEIGHT).execute();
  }

  private void savePurchase(DSLContext ctx, DigitalGoodsStore.Purchase purchase) {
    byte[] note        = null;
    byte[] nonce       = null;
    byte[] goods       = null;
    byte[] goodsNonce  = null;
    byte[] refundNote  = null;
    byte[] refundNonce = null;
    if ( purchase.getNote() != null ) {
      note  = purchase.getNote().getData();
      nonce = purchase.getNote().getNonce();
    }
    if ( purchase.getEncryptedGoods() != null ) {
      goods      = purchase.getEncryptedGoods().getData();
      goodsNonce = purchase.getEncryptedGoods().getNonce();
    }
    if ( purchase.getRefundNote() != null ) {
      refundNote  = purchase.getRefundNote().getData();
      refundNonce = purchase.getRefundNote().getNonce();
    }
    PurchaseRecord record = new PurchaseRecord();
    record.setId(purchase.getId());
    record.setBuyerId(purchase.getBuyerId());
    record.setGoodsId(purchase.getGoodsId());
    record.setSellerId(purchase.getSellerId());
    record.setQuantity(purchase.getQuantity());
    record.setPrice(purchase.getPriceNQT());
    record.setDeadline(purchase.getDeliveryDeadlineTimestamp());
    record.setNote(note);
    record.setNonce(nonce);
    record.setTimestamp(purchase.getTimestamp());
    record.setPending(purchase.isPending());
    record.setGoods(goods);
    record.setGoodsNonce(goodsNonce);
    record.setRefundNote(refundNote);
    record.setRefundNonce(refundNonce);
    record.setHasFeedbackNotes(purchase.getFeedbackNotes() != null && !purchase.getFeedbackNotes().isEmpty());
    record.setHasPublicFeedbacks(!purchase.getPublicFeedback().isEmpty());
    record.setDiscount(purchase.getDiscountNQT());
    record.setRefund(purchase.getRefundNQT());
    record.setHeight(Burst.getBlockchain().getHeight());
    record.setLatest(true);
    DbUtils.upsert(ctx, record, PURCHASE.ID, PURCHASE.HEIGHT).execute();
  }

  @Override
  public Collection<DigitalGoodsStore.Goods> getGoodsInStock(int from, int to) {
    return goodsTable.getManyBy(GOODS.DELISTED.isFalse().and(GOODS.QUANTITY.gt(0)), from, to);
  }

  @Override
  public Collection<DigitalGoodsStore.Goods> getSellerGoods(final long sellerId, final boolean inStockOnly, int from, int to) {
    List<SortField<?>> sort = new ArrayList<>();
    sort.add(GOODS.field("name", String.class).asc());
    sort.add(GOODS.field("timestamp", Integer.class).desc());
    sort.add(GOODS.field("id", Long.class).asc());
    return getGoodsTable().getManyBy(
      (
        inStockOnly
          ? GOODS.SELLER_ID.eq(sellerId).and(GOODS.DELISTED.isFalse()).and(GOODS.QUANTITY.gt(0))
          : GOODS.SELLER_ID.eq(sellerId)
      ),
      from, to, sort
    );
  }

  @Override
  public Collection<DigitalGoodsStore.Purchase> getAllPurchases(int from, int to) {
    return purchaseTable.getAll(from, to);
  }

  @Override
  public Collection<DigitalGoodsStore.Purchase> getSellerPurchases(long sellerId, int from, int to) {
    return purchaseTable.getManyBy(PURCHASE.SELLER_ID.eq(sellerId), from, to);
  }

  @Override
  public Collection<DigitalGoodsStore.Purchase> getBuyerPurchases(long buyerId, int from, int to) {
    return purchaseTable.getManyBy(PURCHASE.BUYER_ID.eq(buyerId), from, to);
  }

  @Override
  public Collection<DigitalGoodsStore.Purchase> getSellerBuyerPurchases(final long sellerId, final long buyerId, int from, int to) {
    return purchaseTable.getManyBy(PURCHASE.SELLER_ID.eq(sellerId).and(PURCHASE.BUYER_ID.eq(buyerId)), from, to);
  }

  @Override
  public Collection<DigitalGoodsStore.Purchase> getPendingSellerPurchases(final long sellerId, int from, int to) {
    return purchaseTable.getManyBy(PURCHASE.SELLER_ID.eq(sellerId).and(PURCHASE.PENDING.isTrue()), from, to);
  }

  public DigitalGoodsStore.Purchase getPendingPurchase(long purchaseId) {
    DigitalGoodsStore.Purchase purchase =
        purchaseTable.get(purchaseDbKeyFactory.newKey(purchaseId));
    return purchase == null || !purchase.isPending() ? null : purchase;
  }



  private class SQLGoods extends DigitalGoodsStore.Goods {
    private SQLGoods(Record record) {
      super(
            record.get(GOODS.ID),
            goodsDbKeyFactory.newKey(record.get(GOODS.ID)),
            record.get(GOODS.SELLER_ID),
            record.get(GOODS.NAME),
            record.get(GOODS.DESCRIPTION),
            record.get(GOODS.TAGS),
            record.get(GOODS.TIMESTAMP),
            record.get(GOODS.QUANTITY),
            record.get(GOODS.PRICE),
            record.get(GOODS.DELISTED)
            );
    }
  }

  class SQLPurchase extends DigitalGoodsStore.Purchase {

    SQLPurchase(Record record) {
      super(
            record.get(PURCHASE.ID),
            purchaseDbKeyFactory.newKey(record.get(PURCHASE.ID)),
            record.get(PURCHASE.BUYER_ID),
            record.get(PURCHASE.GOODS_ID),
            record.get(PURCHASE.SELLER_ID),
            record.get(PURCHASE.QUANTITY),
            record.get(PURCHASE.PRICE),
            record.get(PURCHASE.DEADLINE),
            loadEncryptedData(record, PURCHASE.NOTE, PURCHASE.NONCE),
            record.get(PURCHASE.TIMESTAMP),
            record.get(PURCHASE.PENDING),
            loadEncryptedData(record, PURCHASE.GOODS, PURCHASE.GOODS_NONCE),
            loadEncryptedData(record, PURCHASE.REFUND_NOTE, PURCHASE.REFUND_NONCE),
            record.get(PURCHASE.HAS_FEEDBACK_NOTES),
            record.get(PURCHASE.HAS_PUBLIC_FEEDBACKS),
            record.get(PURCHASE.DISCOUNT),
            record.get(PURCHASE.REFUND)
            );
    }
  }

}
