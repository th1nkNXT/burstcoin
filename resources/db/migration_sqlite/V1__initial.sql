create table block
(
    db_id                 INTEGER   primary key autoincrement,
    id                    BIGINT    not null,
    version               INT       not null,
    timestamp             INT       not null,
    previous_block_id     BIGINT    references block (id) on delete cascade deferrable initially deferred,
    total_amount          BIGINT    not null,
    total_fee             BIGINT    not null,
    payload_length        INT       not null,
    generator_public_key  BLOB(32)  not null,
    previous_block_hash   BLOB(32),
    cumulative_difficulty BLOB      not null,
    base_target           BIGINT    not null,
    next_block_id         BIGINT    references block (id) on delete set null deferrable initially deferred,
    height                INT       not null,
    generation_signature  BLOB(64)  not null,
    block_signature       BLOB(64)  not null,
    payload_hash          BLOB(32)  not null,
    generator_id          BIGINT    not null,
    nonce                 BIGINT    not null,
    ats                   BLOB
);

create index block_generator_id_idx
    on block (generator_id);

create unique index block_height_idx
    on block (height);

create unique index block_id_idx
    on block (id);

create unique index block_timestamp_idx
    on block (timestamp desc);

-- account

create table account
(
    db_id               INTEGER     primary key autoincrement,
    id                  BIGINT      not null,
    creation_height     INT         not null,
    public_key          BLOB(32),
    key_height          INT,
    balance             BIGINT      not null,
    unconfirmed_balance BIGINT      not null,
    forged_balance      BIGINT      not null,
    name                VARCHAR(100),
    description         TEXT,
    height              INT         not null,
    latest              BOOLEAN     default true not null
);

create index account_id_balance_height_idx
    on account (id asc, balance asc, height desc);

create unique index account_id_height_idx
    on account (id asc, height desc);

create index account_id_latest_idx
    on account (id, latest);

-- transaction

create table "transaction"
(
    db_id                           INTEGER     primary key autoincrement,
    id                              BIGINT      not null,
    deadline                        SMALLINT    not null,
    sender_public_key               BLOB(32)    not null,
    recipient_id                    BIGINT,
    amount                          BIGINT      not null,
    fee                             BIGINT      not null,
    height                          INT         not null,
    block_id                        BIGINT      not null references block (id) on delete cascade deferrable initially deferred,
    signature                       BLOB(64),
    timestamp                       INT         not null,
    type                            TINYINT     not null,
    subtype                         TINYINT     not null,
    sender_id                       BIGINT      not null,
    attachment_bytes                BLOB,
    block_timestamp                 INT         not null,
    full_hash                       BLOB(32)    not null,
    referenced_transaction_fullhash BLOB(32),
    version                         TINYINT     not null,
    has_message                     BOOLEAN     default false not null,
    has_encrypted_message           BOOLEAN     default false not null,
    has_public_key_announcement     BOOLEAN     default false not null,
    ec_block_height                 INT         default null,
    ec_block_id                     BIGINT      default null,
    has_encrypttoself_message       BOOLEAN     default false not null
);

create index transaction_block_timestamp_idx
    on "transaction" (block_timestamp desc);

create unique index transaction_full_hash_idx
    on "transaction" (full_hash);

create unique index transaction_id_idx
    on "transaction" (id);

create index transaction_recipient_id_amount_height_idx
    on "transaction" (recipient_id, amount, height);

create index transaction_recipient_id_idx
    on "transaction" (recipient_id);

create index transaction_sender_id_idx
    on "transaction" (sender_id);

-- asset

create table asset
(
    db_id       INTEGER     primary key autoincrement,
    id          BIGINT      not null,
    account_id  BIGINT      not null,
    name        VARCHAR(10),
    description TEXT,
    quantity    BIGINT      not null,
    decimals    TINYINT     not null,
    height      INT         not null
);

create index asset_account_id_idx
    on asset (account_id);

create unique index asset_id_idx
    on asset (id);


-- account_asset

create table account_asset
(
    db_id                INTEGER    primary key autoincrement,
    account_id           BIGINT     not null,
    asset_id             BIGINT     not null,
    quantity             BIGINT     not null,
    unconfirmed_quantity BIGINT     not null,
    height               INT        not null,
    latest               BOOLEAN    default true not null
);

create unique index account_asset_id_height_idx
    on account_asset (account_id asc, asset_id asc, height desc);

create index account_asset_quantity_idx
    on account_asset (quantity desc);

-- alias

create table alias
(
    db_id            INTEGER        primary key autoincrement,
    id               BIGINT         not null,
    account_id       BIGINT         not null,
    alias_name       VARCHAR(100)   not null,
    alias_name_lower VARCHAR(100)   not null,
    alias_uri        TEXT           not null,
    timestamp        INT            not null,
    height           INT            not null,
    latest           BOOLEAN        default true not null
);

create index alias_account_id_idx
    on alias (account_id asc, height desc);

create unique index alias_id_height_idx
    on alias (id asc, height desc);

create index alias_name_lower_idx
    on alias (alias_name_lower);

-- alias_offer

create table alias_offer
(
    db_id    INTEGER    primary key autoincrement,
    id       BIGINT     not null,
    price    BIGINT     not null,
    buyer_id BIGINT,
    height   INT        not null,
    latest   BOOLEAN    default true not null
);

create unique index alias_offer_id_height_idx
    on alias_offer (id asc, height desc);

-- ask_order

create table ask_order
(
    db_id           INTEGER     primary key autoincrement,
    id              BIGINT      not null,
    account_id      BIGINT      not null,
    asset_id        BIGINT      not null,
    price           BIGINT      not null,
    quantity        BIGINT      not null,
    creation_height INT         not null,
    height          INT         not null,
    latest          BOOLEAN     default true not null
);

create index ask_order_account_id_idx
    on ask_order (account_id asc, height desc);

create index ask_order_asset_id_price_idx
    on ask_order (asset_id, price);

create index ask_order_creation_idx
    on ask_order (creation_height desc);

create unique index ask_order_id_height_idx
    on ask_order (id asc, height desc);

-- asset_transfer

create table asset_transfer
(
    db_id        INTEGER    primary key autoincrement,
    id           BIGINT     not null,
    asset_id     BIGINT     not null,
    sender_id    BIGINT     not null,
    recipient_id BIGINT     not null,
    quantity     BIGINT     not null,
    timestamp    INT        not null,
    height       INT        not null
);

create index asset_transfer_asset_id_idx
    on asset_transfer (asset_id asc, height desc);

create unique index asset_transfer_id_idx
    on asset_transfer (id);

create index asset_transfer_recipient_id_idx
    on asset_transfer (recipient_id asc, height desc);

create index asset_transfer_sender_id_idx
    on asset_transfer (sender_id asc, height desc);

-- at

create table at
(
    db_id              INTEGER      primary key autoincrement,
    id                 BIGINT       not null,
    creator_id         BIGINT       not null,
    name               VARCHAR(30),
    description        TEXT,
    version            SMALLINT     not null,
    csize              INT          not null,
    dsize              INT          not null,
    c_user_stack_bytes INT          not null,
    c_call_stack_bytes INT          not null,
    creation_height    INT          not null,
    ap_code            BLOB         not null,
    height             INT          not null,
    latest             BOOLEAN      default true not null
);

create index at_creator_id_height_idx
    on at (creator_id asc, height desc);

create unique index at_id_height_idx
    on at (id asc, height desc);

-- at_state

create table at_state
(
    db_id                    INTEGER    primary key autoincrement,
    at_id                    BIGINT     not null,
    state                    BLOB       not null,
    prev_height              INT        not null,
    next_height              INT        not null,
    sleep_between            INT        not null,
    prev_balance             BIGINT     not null,
    freeze_when_same_balance BOOLEAN    not null,
    min_activate_amount      BIGINT     not null,
    height                   INT        not null,
    latest                   BOOLEAN    default true not null
);

create unique index at_state_at_id_height_idx
    on at_state (at_id asc, height desc);

create index at_state_id_next_height_height_idx
    on at_state (at_id asc, next_height asc, height desc);

-- bid_order

create table bid_order
(
    db_id           INTEGER     primary key autoincrement,
    id              BIGINT      not null,
    account_id      BIGINT      not null,
    asset_id        BIGINT      not null,
    price           BIGINT      not null,
    quantity        BIGINT      not null,
    creation_height INT         not null,
    height          INT         not null,
    latest          BOOLEAN     default true not null
);

create index bid_order_account_id_idx
    on bid_order (account_id asc, height desc);

create index bid_order_asset_id_price_idx
    on bid_order (asset_id asc, price desc);

create index bid_order_creation_idx
    on bid_order (creation_height desc);

create unique index bid_order_id_height_idx
    on bid_order (id asc, height desc);

-- escrow

create table escrow
(
    db_id            INTEGER    primary key autoincrement,
    id               BIGINT     not null,
    sender_id        BIGINT     not null,
    recipient_id     BIGINT     not null,
    amount           BIGINT     not null,
    required_signers INT,
    deadline         INT        not null,
    deadline_action  INT        not null,
    height           INT        not null,
    latest           BOOLEAN    default true not null
);

create index escrow_deadline_height_idx
    on escrow (deadline asc, height desc);

create unique index escrow_id_height_idx
    on escrow (id asc, height desc);

create index escrow_recipient_id_height_idx
    on escrow (recipient_id asc, height desc);

create index escrow_sender_id_height_idx
    on escrow (sender_id asc, height desc);

-- escrow_decision

create table escrow_decision
(
    db_id      INTEGER  primary key autoincrement,
    escrow_id  BIGINT   not null,
    account_id BIGINT   not null,
    decision   INT      not null,
    height     INT      not null,
    latest     BOOLEAN  default true not null
);

create index escrow_decision_account_id_height_idx
    on escrow_decision (account_id asc, height desc);

create unique index escrow_decision_escrow_id_account_id_height_idx
    on escrow_decision (escrow_id asc, account_id asc, height desc);

create index escrow_decision_escrow_id_height_idx
    on escrow_decision (escrow_id asc, height desc);

-- goods

create table goods
(
    db_id       INTEGER         primary key autoincrement,
    id          BIGINT          not null,
    seller_id   BIGINT          not null,
    name        VARCHAR(100)    not null,
    description TEXT,
    tags        VARCHAR(100),
    timestamp   INT             not null,
    quantity    INT             not null,
    price       BIGINT          not null,
    delisted    BOOLEAN         not null,
    height      INT             not null,
    latest      BOOLEAN         default true not null
);

create unique index goods_id_height_idx
    on goods (id asc, height desc);

create index goods_seller_id_name_idx
    on goods (seller_id, name);

create index goods_timestamp_idx
    on goods (timestamp desc, height desc);

-- indirect_incoming

create table indirect_incoming
(
    db_id          INTEGER  primary key autoincrement,
    account_id     bigint   not null,
    transaction_id bigint   not null,
    height         INT      not null
);

create unique index indirect_incoming_db_id_uindex
    on indirect_incoming (account_id, transaction_id);

-- purchase

create table purchase
(
    db_id                INTEGER    primary key autoincrement,
    id                   BIGINT     not null,
    buyer_id             BIGINT     not null,
    goods_id             BIGINT     not null,
    seller_id            BIGINT     not null,
    quantity             INT        not null,
    price                BIGINT     not null,
    deadline             INT        not null,
    note                 BLOB,
    nonce                BLOB(32),
    timestamp            INT        not null,
    pending              BOOLEAN    not null,
    goods                BLOB,
    goods_nonce          BLOB(32),
    refund_note          BLOB,
    refund_nonce         BLOB(32),
    has_feedback_notes   BOOLEAN    default false not null,
    has_public_feedbacks BOOLEAN    default false not null,
    discount             BIGINT     not null,
    refund               BIGINT     not null,
    height               INT        not null,
    latest               BOOLEAN    default true not null
);

create index purchase_buyer_id_height_idx
    on purchase (buyer_id asc, height desc);

create index purchase_deadline_idx
    on purchase (deadline desc, height desc);

create unique index purchase_id_height_idx
    on purchase (id asc, height desc);

create index purchase_seller_id_height_idx
    on purchase (seller_id asc, height desc);

create index purchase_timestamp_idx
    on purchase (timestamp desc, id asc);

-- purchase_feedback

create table purchase_feedback
(
    db_id          INTEGER      primary key autoincrement,
    id             BIGINT       not null,
    feedback_data  BLOB         not null,
    feedback_nonce BLOB(32)     not null,
    height         INT          not null,
    latest         BOOLEAN      default true not null
);

create index purchase_feedback_id_height_idx
    on purchase_feedback (id asc, height desc);

-- purchase_public_feedback

create table purchase_public_feedback
(
    db_id           INTEGER     primary key autoincrement,
    id              BIGINT      not null,
    public_feedback TEXT        not null,
    height          INT         not null,
    latest          BOOLEAN     default true not null
);

create unique index purchase_public_feedback_id_height_idx
    on purchase_public_feedback (id asc, height desc);

-- reward_recip_assign

create table reward_recip_assign
(
    db_id         INTEGER   primary key autoincrement,
    account_id    BIGINT    not null,
    prev_recip_id BIGINT    not null,
    recip_id      BIGINT    not null,
    from_height   INT       not null,
    height        INT       not null,
    latest        BOOLEAN   default true not null
);

create unique index reward_recip_assign_account_id_height_idx
    on reward_recip_assign (account_id asc, height desc);

create index reward_recip_assign_recip_id_height_idx
    on reward_recip_assign (recip_id asc, height desc);

-- subscription

create table subscription
(
    db_id        INTEGER    primary key autoincrement,
    id           BIGINT     not null,
    sender_id    BIGINT     not null,
    recipient_id BIGINT     not null,
    amount       BIGINT     not null,
    frequency    INT        not null,
    time_next    INT        not null,
    height       INT        not null,
    latest       BOOLEAN    default true not null
);

create unique index subscription_id_height_idx
    on subscription (id asc, height desc);

create unique index subscription_idx
    on subscription (id, sender_id, recipient_id, amount, frequency, time_next, height, latest);

create index subscription_recipient_id_height_idx
    on subscription (recipient_id asc, height desc);

create index subscription_sender_id_height_idx
    on subscription (sender_id asc, height desc);

-- trade

create table trade
(
    db_id            INTEGER    primary key autoincrement,
    asset_id         BIGINT     not null,
    block_id         BIGINT     not null,
    ask_order_id     BIGINT     not null,
    bid_order_id     BIGINT     not null,
    ask_order_height INT        not null,
    bid_order_height INT        not null,
    seller_id        BIGINT     not null,
    buyer_id         BIGINT     not null,
    quantity         BIGINT     not null,
    price            BIGINT     not null,
    timestamp        INT        not null,
    height           INT        not null
);

create unique index trade_ask_bid_idx
    on trade (ask_order_id, bid_order_id);

create index trade_asset_id_idx
    on trade (asset_id asc, height desc);

create index trade_buyer_id_idx
    on trade (buyer_id asc, height desc);

create index trade_seller_id_idx
    on trade (seller_id asc, height desc);

-- peer

create table peer (
    address VARCHAR(100) primary key
);

-- unconfirmed_transaction

create table unconfirmed_transaction
(
    db_id               INTEGER     primary key autoincrement,
    id                  BIGINT      not null,
    expiration          INT         not null,
    transaction_height  INT         not null,
    fee_per_byte        BIGINT      not null,
    timestamp           INT         not null,
    transaction_bytes   BLOB,
    height              INT         not null
);

create index unconfirmed_transaction_height_fee_timestamp_idx
    on unconfirmed_transaction (transaction_height asc, fee_per_byte desc, timestamp asc);
