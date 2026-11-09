-- ====================================================================
-- FinPath AI: V5 Proactive Notifications & Reminder Engine Schema Migration
-- ====================================================================

-- 1. Extend notifications table with proactive intelligence attributes
ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS category VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    ADD COLUMN IF NOT EXISTS priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    ADD COLUMN IF NOT EXISTS action_url VARCHAR(255),
    ADD COLUMN IF NOT EXISTS action_label VARCHAR(100),
    ADD COLUMN IF NOT EXISTS read_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN IF NOT EXISTS deduplication_key VARCHAR(150),
    ADD COLUMN IF NOT EXISTS metadata_json TEXT,
    ADD COLUMN IF NOT EXISTS channel_delivery_json TEXT,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_notifications_user_read ON notifications(user_id, is_read, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_notifications_user_cat ON notifications(user_id, category);
CREATE INDEX IF NOT EXISTS idx_notifications_dedup ON notifications(user_id, deduplication_key);

-- 2. Create user_notification_preferences table
CREATE TABLE IF NOT EXISTS user_notification_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    in_app_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    email_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    push_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    loan_emi_alerts BOOLEAN NOT NULL DEFAULT TRUE,
    insurance_renewal_alerts BOOLEAN NOT NULL DEFAULT TRUE,
    budget_threshold_alerts BOOLEAN NOT NULL DEFAULT TRUE,
    savings_goal_alerts BOOLEAN NOT NULL DEFAULT TRUE,
    journey_incomplete_alerts BOOLEAN NOT NULL DEFAULT TRUE,
    document_missing_alerts BOOLEAN NOT NULL DEFAULT TRUE,
    reminder_days_before_emi INT NOT NULL DEFAULT 3,
    reminder_days_before_renewal INT NOT NULL DEFAULT 15,
    quiet_hours_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    quiet_hours_start VARCHAR(10) DEFAULT '22:00',
    quiet_hours_end VARCHAR(10) DEFAULT '08:00',
    email_consent_granted BOOLEAN NOT NULL DEFAULT TRUE,
    push_consent_granted BOOLEAN NOT NULL DEFAULT FALSE,
    time_zone VARCHAR(50) NOT NULL DEFAULT 'Asia/Kolkata',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_notif_prefs_user ON user_notification_preferences(user_id);

-- 3. Seed default preferences for demo user Sarah Connor (9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d)
INSERT INTO user_notification_preferences (
    id, user_id, in_app_enabled, email_enabled, push_enabled,
    loan_emi_alerts, insurance_renewal_alerts, budget_threshold_alerts,
    savings_goal_alerts, journey_incomplete_alerts, document_missing_alerts,
    reminder_days_before_emi, reminder_days_before_renewal,
    quiet_hours_enabled, quiet_hours_start, quiet_hours_end,
    email_consent_granted, push_consent_granted, time_zone
) VALUES (
    '8c2d1e0f-5a4b-4c3d-9e8f-7a6b5c4d3e2f',
    '9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d',
    TRUE, TRUE, FALSE,
    TRUE, TRUE, TRUE,
    TRUE, TRUE, TRUE,
    3, 15,
    FALSE, '22:00', '08:00',
    TRUE, FALSE, 'Asia/Kolkata'
) ON CONFLICT (user_id) DO NOTHING;

