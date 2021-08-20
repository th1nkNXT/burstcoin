/*
 * This file is generated by jOOQ.
 */
package brs.schema;

import brs.schema.tables.*;

import javax.annotation.Generated;

/** Convenience access to all tables in DB */
@Generated(
    value = {"http://www.jooq.org", "jOOQ version:3.11.11"},
    comments = "This class is generated by jOOQ")
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Tables {

  /** The table <code>DB.account</code>. */
  public static final Account ACCOUNT = brs.schema.tables.Account.ACCOUNT;

  /** The table <code>DB.account_asset</code>. */
  public static final AccountAsset ACCOUNT_ASSET = brs.schema.tables.AccountAsset.ACCOUNT_ASSET;

  /** The table <code>DB.alias</code>. */
  public static final Alias ALIAS = brs.schema.tables.Alias.ALIAS;

  /** The table <code>DB.alias_offer</code>. */
  public static final AliasOffer ALIAS_OFFER = brs.schema.tables.AliasOffer.ALIAS_OFFER;

  /** The table <code>DB.ask_order</code>. */
  public static final AskOrder ASK_ORDER = brs.schema.tables.AskOrder.ASK_ORDER;

  /** The table <code>DB.asset</code>. */
  public static final Asset ASSET = brs.schema.tables.Asset.ASSET;

  /** The table <code>DB.asset_transfer</code>. */
  public static final AssetTransfer ASSET_TRANSFER = brs.schema.tables.AssetTransfer.ASSET_TRANSFER;

  /** The table <code>DB.at</code>. */
  public static final At AT = brs.schema.tables.At.AT;

  /** The table <code>DB.at_state</code>. */
  public static final AtState AT_STATE = brs.schema.tables.AtState.AT_STATE;

  /** The table <code>DB.bid_order</code>. */
  public static final BidOrder BID_ORDER = brs.schema.tables.BidOrder.BID_ORDER;

  /** The table <code>DB.block</code>. */
  public static final Block BLOCK = brs.schema.tables.Block.BLOCK;

  /** The table <code>DB.escrow</code>. */
  public static final Escrow ESCROW = brs.schema.tables.Escrow.ESCROW;

  /** The table <code>DB.escrow_decision</code>. */
  public static final EscrowDecision ESCROW_DECISION =
      brs.schema.tables.EscrowDecision.ESCROW_DECISION;

  /** The table <code>DB.flyway_schema_history</code>. */
  public static final FlywaySchemaHistory FLYWAY_SCHEMA_HISTORY =
      brs.schema.tables.FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY;

  /** The table <code>DB.goods</code>. */
  public static final Goods GOODS = brs.schema.tables.Goods.GOODS;

  /** The table <code>DB.indirect_incoming</code>. */
  public static final IndirectIncoming INDIRECT_INCOMING =
      brs.schema.tables.IndirectIncoming.INDIRECT_INCOMING;

  /** The table <code>DB.peer</code>. */
  public static final Peer PEER = brs.schema.tables.Peer.PEER;

  /** The table <code>DB.purchase</code>. */
  public static final Purchase PURCHASE = brs.schema.tables.Purchase.PURCHASE;

  /** The table <code>DB.purchase_feedback</code>. */
  public static final PurchaseFeedback PURCHASE_FEEDBACK =
      brs.schema.tables.PurchaseFeedback.PURCHASE_FEEDBACK;

  /** The table <code>DB.purchase_public_feedback</code>. */
  public static final PurchasePublicFeedback PURCHASE_PUBLIC_FEEDBACK =
      brs.schema.tables.PurchasePublicFeedback.PURCHASE_PUBLIC_FEEDBACK;

  /** The table <code>DB.reward_recip_assign</code>. */
  public static final RewardRecipAssign REWARD_RECIP_ASSIGN =
      brs.schema.tables.RewardRecipAssign.REWARD_RECIP_ASSIGN;

  /** The table <code>DB.subscription</code>. */
  public static final Subscription SUBSCRIPTION = brs.schema.tables.Subscription.SUBSCRIPTION;

  /** The table <code>DB.trade</code>. */
  public static final Trade TRADE = brs.schema.tables.Trade.TRADE;

  /** The table <code>DB.transaction</code>. */
  public static final Transaction TRANSACTION = brs.schema.tables.Transaction.TRANSACTION;

  /** The table <code>DB.unconfirmed_transaction</code>. */
  public static final UnconfirmedTransaction UNCONFIRMED_TRANSACTION =
      brs.schema.tables.UnconfirmedTransaction.UNCONFIRMED_TRANSACTION;
}
