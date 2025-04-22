-- ユーザーテーブル
CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       company_name VARCHAR(255) NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                       updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_users_name ON users(name);

-- 請求書テーブル
CREATE TABLE invoices (
                          id SERIAL PRIMARY KEY,
                          user_id INT NOT NULL,
                          issue_date DATE NOT NULL,
                          payment_amount DECIMAL(15, 2) NOT NULL,
                          fee DECIMAL(15, 2) NOT NULL,
                          fee_rate DECIMAL(5, 2) NOT NULL,
                          tax_amount DECIMAL(15, 2) NOT NULL,
                          tax_rate DECIMAL(5, 2) NOT NULL,
                          total_amount DECIMAL(15, 2) NOT NULL,
                          payment_due_date DATE NOT NULL,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          CONSTRAINT fk_invoices_users
                              FOREIGN KEY (user_id)
                                  REFERENCES users(id)
                                  ON DELETE CASCADE
);
CREATE INDEX idx_invoices_user_id ON invoices(user_id);
CREATE INDEX idx_invoices_payment_due_date ON invoices(payment_due_date);
