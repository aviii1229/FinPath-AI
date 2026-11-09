-- ====================================================================
-- FinPath AI: V1 Initial Schema Migration (Flyway Baseline)
-- Supports PostgreSQL 16 + pgvector
-- ====================================================================

-- 1. Enable Required Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "vector";

-- ====================================================================
-- 2. Identity, Access Management & Auditing
-- ====================================================================

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(30),
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    is_mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    mfa_secret VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS roles (
    id VARCHAR(50) PRIMARY KEY,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id VARCHAR(50) NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS user_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    date_of_birth DATE NOT NULL,
    encrypted_national_id TEXT NOT NULL,
    national_id_hash VARCHAR(64) NOT NULL,
    employment_status VARCHAR(50) NOT NULL,
    annual_income NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    risk_tolerance VARCHAR(30) NOT NULL DEFAULT 'MODERATE',
    address_line1 VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state_province VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country_code VARCHAR(3) NOT NULL DEFAULT 'USA',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_profiles_national_id_hash ON user_profiles(national_id_hash);

CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id VARCHAR(100) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    user_agent VARCHAR(255),
    changes_json JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ====================================================================
-- 3. Document Processing & Vector RAG Storage
-- ====================================================================

CREATE TABLE IF NOT EXISTS document_uploads (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    file_name VARCHAR(255) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    document_category VARCHAR(50) NOT NULL,
    processing_status VARCHAR(30) NOT NULL DEFAULT 'QUEUED',
    extracted_summary TEXT,
    raw_text TEXT,
    error_message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS document_chunks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id UUID NOT NULL REFERENCES document_uploads(id) ON DELETE CASCADE,
    chunk_index INT NOT NULL,
    page_number INT,
    content TEXT NOT NULL,
    embedding VECTOR(1536) NOT NULL,
    metadata_json JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Cosine similarity HNSW index for high-speed sub-millisecond search
CREATE INDEX IF NOT EXISTS idx_document_chunks_embedding 
ON document_chunks 
USING hnsw (embedding vector_cosine_ops)
WITH (m = 16, ef_construction = 64);

CREATE TABLE IF NOT EXISTS ai_chat_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL DEFAULT 'New Conversation',
    context_intent VARCHAR(50),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ai_chat_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES ai_chat_sessions(id) ON DELETE CASCADE,
    sender_role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    tool_calls_json JSONB,
    sources_cited_json JSONB,
    tokens_used INT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ai_agent_executions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    message_id UUID REFERENCES ai_chat_messages(id) ON DELETE SET NULL,
    provider_name VARCHAR(50) NOT NULL,
    model_name VARCHAR(100) NOT NULL,
    prompt_tokens INT NOT NULL,
    completion_tokens INT NOT NULL,
    latency_ms BIGINT NOT NULL,
    cost_estimate_usd NUMERIC(10, 6) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ====================================================================
-- 4. Insurance Domain
-- ====================================================================

CREATE TABLE IF NOT EXISTS insurance_products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    carrier_name VARCHAR(100) NOT NULL,
    product_name VARCHAR(150) NOT NULL,
    category VARCHAR(50) NOT NULL,
    base_premium_monthly NUMERIC(15, 2) NOT NULL,
    coverage_amount_min NUMERIC(15, 2) NOT NULL,
    coverage_amount_max NUMERIC(15, 2) NOT NULL,
    terms_summary TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS insurance_quotes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES insurance_products(id),
    quoted_premium_monthly NUMERIC(15, 2) NOT NULL,
    coverage_selected NUMERIC(15, 2) NOT NULL,
    deductible NUMERIC(15, 2) NOT NULL,
    risk_score NUMERIC(5, 2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS insurance_policies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    policy_number VARCHAR(64) NOT NULL UNIQUE,
    quote_id UUID REFERENCES insurance_quotes(id) ON DELETE SET NULL,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    premium_amount NUMERIC(15, 2) NOT NULL,
    payment_frequency VARCHAR(20) NOT NULL DEFAULT 'MONTHLY',
    policy_document_id UUID REFERENCES document_uploads(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS insurance_claims (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    claim_number VARCHAR(64) NOT NULL UNIQUE,
    policy_id UUID NOT NULL REFERENCES insurance_policies(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    claim_amount NUMERIC(15, 2) NOT NULL,
    approved_amount NUMERIC(15, 2),
    incident_date DATE NOT NULL,
    incident_description TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED',
    ai_fraud_risk_score NUMERIC(5, 2),
    adjudication_notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS claim_documents (
    claim_id UUID NOT NULL REFERENCES insurance_claims(id) ON DELETE CASCADE,
    document_id UUID NOT NULL REFERENCES document_uploads(id) ON DELETE CASCADE,
    PRIMARY KEY (claim_id, document_id)
);

-- ====================================================================
-- 5. Lending Domain
-- ====================================================================

CREATE TABLE IF NOT EXISTS loan_products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_name VARCHAR(150) NOT NULL,
    loan_type VARCHAR(50) NOT NULL,
    min_principal NUMERIC(15, 2) NOT NULL,
    max_principal NUMERIC(15, 2) NOT NULL,
    min_interest_rate NUMERIC(6, 4) NOT NULL,
    max_interest_rate NUMERIC(6, 4) NOT NULL,
    min_term_months INT NOT NULL,
    max_term_months INT NOT NULL,
    min_credit_score INT NOT NULL DEFAULT 600,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS credit_profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    credit_score INT NOT NULL,
    total_existing_debt NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    monthly_debt_obligations NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    delinquencies_count INT NOT NULL DEFAULT 0,
    bureau_report_date DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS loan_applications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_number VARCHAR(64) NOT NULL UNIQUE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    loan_product_id UUID NOT NULL REFERENCES loan_products(id),
    requested_amount NUMERIC(15, 2) NOT NULL,
    requested_term_months INT NOT NULL,
    loan_purpose VARCHAR(255) NOT NULL,
    calculated_dti NUMERIC(5, 2),
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    assigned_officer_id UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS loan_underwriting_decisions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID NOT NULL UNIQUE REFERENCES loan_applications(id) ON DELETE CASCADE,
    approved_amount NUMERIC(15, 2),
    approved_term_months INT,
    approved_apr NUMERIC(6, 4),
    decision VARCHAR(30) NOT NULL,
    decision_rationale TEXT NOT NULL,
    underwriter_id UUID REFERENCES users(id) ON DELETE SET NULL,
    decided_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS repayment_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    application_id UUID NOT NULL REFERENCES loan_applications(id) ON DELETE CASCADE,
    installment_number INT NOT NULL,
    due_date DATE NOT NULL,
    principal_due NUMERIC(15, 2) NOT NULL,
    interest_due NUMERIC(15, 2) NOT NULL,
    total_payment_due NUMERIC(15, 2) NOT NULL,
    remaining_balance NUMERIC(15, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING'
);

-- ====================================================================
-- 6. Fintech Domain (PFM & Wealth)
-- ====================================================================

CREATE TABLE IF NOT EXISTS financial_accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    institution_name VARCHAR(100) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    account_mask VARCHAR(20) NOT NULL,
    encrypted_account_number TEXT NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    current_balance NUMERIC(15, 2) NOT NULL,
    available_balance NUMERIC(15, 2) NOT NULL,
    is_connected BOOLEAN NOT NULL DEFAULT TRUE,
    last_synced_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL REFERENCES financial_accounts(id) ON DELETE CASCADE,
    amount NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    transaction_date DATE NOT NULL,
    merchant_name VARCHAR(150) NOT NULL,
    clean_merchant_name VARCHAR(150),
    category VARCHAR(50) NOT NULL DEFAULT 'UNCATEGORIZED',
    is_recurring BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(20) NOT NULL DEFAULT 'POSTED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_transactions_account_date ON transactions(account_id, transaction_date DESC);
CREATE INDEX IF NOT EXISTS idx_transactions_category ON transactions(category);

CREATE TABLE IF NOT EXISTS budgets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category VARCHAR(50) NOT NULL,
    monthly_limit NUMERIC(15, 2) NOT NULL,
    alert_threshold_percent INT NOT NULL DEFAULT 85,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, category)
);

CREATE TABLE IF NOT EXISTS cashflow_forecasts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    forecast_date DATE NOT NULL,
    predicted_inflow NUMERIC(15, 2) NOT NULL,
    predicted_outflow NUMERIC(15, 2) NOT NULL,
    projected_balance NUMERIC(15, 2) NOT NULL,
    confidence_interval_low NUMERIC(15, 2) NOT NULL,
    confidence_interval_high NUMERIC(15, 2) NOT NULL,
    generated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ====================================================================
-- 7. Seed Initial Baseline Data
-- ====================================================================

-- Default Roles
INSERT INTO roles (id, description) VALUES
('ROLE_CUSTOMER', 'Retail Customer with access to personal finance, lending, and insurance journeys'),
('ROLE_LOAN_OFFICER', 'Lending officer with underwriting and loan application approval authority'),
('ROLE_UNDERWRITER', 'Insurance underwriting officer assessing quotes, risks, and claims'),
('ROLE_ADMIN', 'Platform administrator managing system configuration and AI gateways')
ON CONFLICT (id) DO NOTHING;

-- Initial Demo User (Sarah Connor - password: SecurePassword123!)
-- BCrypt: $2a$10$wB1yB2KqT9o8x7y6z5w4v.e3d2c1b0a9f8e7d6c5b4a3Z2Y1X0W9V
INSERT INTO users (id, email, password_hash, first_name, last_name, phone_number, status) VALUES
('9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d', 'sarah.connor@finpath.ai', '$2a$10$wB1yB2KqT9o8x7y6z5w4v.e3d2c1b0a9f8e7d6c5b4a3Z2Y1X0W9V', 'Sarah', 'Connor', '+14155552671', 'ACTIVE')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id) VALUES
('9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d', 'ROLE_CUSTOMER')
ON CONFLICT (user_id, role_id) DO NOTHING;

-- Initial Loan Programs
INSERT INTO loan_products (id, product_name, loan_type, min_principal, max_principal, min_interest_rate, max_interest_rate, min_term_months, max_term_months, min_credit_score) VALUES
('3d5f7b9a-1c2e-4a6b-8d0f-2a4b6c8e0f1a', 'Prime Personal Flexi-Borrowing', 'PERSONAL', 2000.00, 50000.00, 0.0549, 0.1299, 12, 60, 640),
('4e6f8a0b-2d3f-5b7c-9e1f-3b5c7d9f1a2b', 'Conforming 30-Year Fixed Mortgage', 'MORTGAGE', 100000.00, 1500000.00, 0.0595, 0.0750, 180, 360, 620),
('5f7a9b1c-3e4a-6c8d-0f2a-4c6e8e0a2b3c', 'Tier-1 Clean Energy & Auto Loan', 'AUTO', 10000.00, 85000.00, 0.0475, 0.0899, 24, 72, 660)
ON CONFLICT (id) DO NOTHING;

-- Initial Insurance Products
INSERT INTO insurance_products (id, carrier_name, product_name, category, base_premium_monthly, coverage_amount_min, coverage_amount_max, terms_summary) VALUES
('e4f8d2a1-6a23-4b61-9c32-1a482b54d190', 'Aegis National Life', 'Term Life 20-Year Level Shield', 'TERM_LIFE', 35.50, 100000.00, 2000000.00, 'Guaranteed level premium for 20 years with accelerated terminal illness benefit.'),
('f5a9e3b2-7b34-5c72-0d43-2b593c65e201', 'Aegis Mutual Health', 'Platinum Comprehensive Care HMO', 'HEALTH', 195.00, 500000.00, 1500000.00, 'Comprehensive hospitalization, prescription drug coverage, and $1,200 deductible.'),
('a6b0f4c3-8c45-6d83-1e54-3c604d76f312', 'Geovera Comprehensive', 'Prime Auto Collision & Comprehensive', 'AUTO', 105.00, 50000.00, 250000.00, 'Zero-depreciation replacement for new vehicles with $500 deductible.')
ON CONFLICT (id) DO NOTHING;

