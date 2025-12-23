CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              transaction_id VARCHAR(64) UNIQUE NOT NULL,
                              account_id VARCHAR(64) NOT NULL,
                              txn_type VARCHAR(20) NOT NULL,
                              amount NUMERIC(18,2) NOT NULL,
                              currency VARCHAR(10),
                              location VARCHAR(100),
                              merchant VARCHAR(100),
                              timestamp TIMESTAMPTZ NOT NULL,
                              created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_transactions_account_ts
    ON transactions (account_id, timestamp DESC);

CREATE INDEX idx_transactions_txn_id
    ON transactions (transaction_id);

--------------------------------------------------

CREATE TABLE fraud_alerts (
                              id BIGSERIAL PRIMARY KEY,
                              account_id VARCHAR(64) NOT NULL,
                              alert_type VARCHAR(50) NOT NULL,
                              alert_score NUMERIC(5,2) NOT NULL,
                              related_txn_id VARCHAR(64),
                              details JSONB,
                              detected_at TIMESTAMPTZ NOT NULL,
                              acknowledged BOOLEAN DEFAULT false
);

CREATE INDEX idx_fraud_alerts_account_detected
    ON fraud_alerts (account_id, detected_at);

--------------------------------------------------

CREATE TABLE account_profiles (
                                  account_id VARCHAR(64) PRIMARY KEY,
                                  avg_daily_spend NUMERIC(18,2),
                                  avg_txn_amount NUMERIC(18,2),
                                  home_country VARCHAR(50),
                                  risk_tier VARCHAR(10),
                                  updated_at TIMESTAMPTZ
);
