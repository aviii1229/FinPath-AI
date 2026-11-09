-- ====================================================================
-- FinPath AI: V2 Smart Onboarding and Personalization Schema Migration
-- ====================================================================

-- 1. Extend user_profiles with Onboarding and Personalization attributes
ALTER TABLE user_profiles
    ADD COLUMN IF NOT EXISTS age INT,
    ADD COLUMN IF NOT EXISTS employment_category VARCHAR(50),
    ADD COLUMN IF NOT EXISTS occupation VARCHAR(100),
    ADD COLUMN IF NOT EXISTS monthly_income NUMERIC(15, 2) DEFAULT 75000.00,
    ADD COLUMN IF NOT EXISTS monthly_expenses NUMERIC(15, 2) DEFAULT 33740.00,
    ADD COLUMN IF NOT EXISTS existing_obligations NUMERIC(15, 2) DEFAULT 8540.00,
    ADD COLUMN IF NOT EXISTS approximate_savings NUMERIC(15, 2) DEFAULT 345250.00,
    ADD COLUMN IF NOT EXISTS preferred_assistance VARCHAR(50) DEFAULT 'AI_CONVERSATIONAL',
    ADD COLUMN IF NOT EXISTS financial_knowledge VARCHAR(50) DEFAULT 'INTERMEDIATE',
    ADD COLUMN IF NOT EXISTS communication_style VARCHAR(50) DEFAULT 'ANALYTICAL',
    ADD COLUMN IF NOT EXISTS short_term_goal VARCHAR(255),
    ADD COLUMN IF NOT EXISTS long_term_goal VARCHAR(255),
    ADD COLUMN IF NOT EXISTS onboarding_completed BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS onboarding_step INT NOT NULL DEFAULT 0;

-- 2. Create user_financial_goals table for multi-select goal mappings
CREATE TABLE IF NOT EXISTS user_financial_goals (
    profile_id UUID NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    goal_name VARCHAR(100) NOT NULL,
    PRIMARY KEY (profile_id, goal_name)
);

CREATE INDEX IF NOT EXISTS idx_user_financial_goals_profile ON user_financial_goals(profile_id);

