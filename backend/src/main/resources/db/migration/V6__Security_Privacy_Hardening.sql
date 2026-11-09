-- ====================================================================
-- FinPath AI: V6 Security, Privacy Hardening & DPDP Compliance Migration
-- ====================================================================

-- 1. Extend users table with Account Lockout and Rate Limiting attributes
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS failed_login_attempts INT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS locked_until TIMESTAMP WITH TIME ZONE;

-- 2. Create refresh_tokens table for secure JWT rotation and revocation
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user ON refresh_tokens(user_id);

-- 3. Create user_privacy_consents table (DPDP Act 2023 Compliance)
CREATE TABLE IF NOT EXISTS user_privacy_consents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    data_collection_consent BOOLEAN NOT NULL DEFAULT TRUE,
    ai_processing_consent BOOLEAN NOT NULL DEFAULT TRUE,
    marketing_communication_consent BOOLEAN NOT NULL DEFAULT FALSE,
    third_party_sharing_consent BOOLEAN NOT NULL DEFAULT FALSE,
    document_ocr_storage_consent BOOLEAN NOT NULL DEFAULT TRUE,
    store_chat_history BOOLEAN NOT NULL DEFAULT TRUE,
    consent_version VARCHAR(20) NOT NULL DEFAULT 'v2026.1',
    consent_timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_privacy_consents_user ON user_privacy_consents(user_id);

-- 4. Seed default consent for demo user Sarah Connor (9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d)
INSERT INTO user_privacy_consents (
    id, user_id, data_collection_consent, ai_processing_consent,
    marketing_communication_consent, third_party_sharing_consent,
    document_ocr_storage_consent, store_chat_history, consent_version
) VALUES (
    '7a1b2c3d-4e5f-6a7b-8c9d-0e1f2a3b4c5d',
    '9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d',
    TRUE, TRUE, FALSE, FALSE, TRUE, TRUE, 'v2026.1'
) ON CONFLICT (user_id) DO NOTHING;
