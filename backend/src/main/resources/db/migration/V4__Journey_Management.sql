-- ====================================================================
-- FinPath AI: V4 Intelligent Financial Journey Management & State Machine
-- ====================================================================

-- 1. Table: user_journeys
CREATE TABLE IF NOT EXISTS user_journeys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    journey_type VARCHAR(60) NOT NULL,
    journey_domain VARCHAR(30) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS',
    current_step_key VARCHAR(60) NOT NULL,
    current_step_index INT NOT NULL DEFAULT 0,
    total_steps INT NOT NULL,
    progress_percentage INT NOT NULL DEFAULT 0,
    journey_data_json TEXT,
    cancellation_reason VARCHAR(500),
    is_simulated BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    completed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_user_journeys_user_status ON user_journeys(user_id, status);
CREATE INDEX IF NOT EXISTS idx_user_journeys_user_domain ON user_journeys(user_id, journey_domain);
CREATE INDEX IF NOT EXISTS idx_user_journeys_updated ON user_journeys(updated_at DESC);

-- 2. Table: journey_step_records
CREATE TABLE IF NOT EXISTS journey_step_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    journey_id UUID NOT NULL REFERENCES user_journeys(id) ON DELETE CASCADE,
    step_key VARCHAR(60) NOT NULL,
    step_order INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    step_data_json TEXT,
    required_documents_json TEXT,
    ai_guidance TEXT,
    completed_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_step_records_journey ON journey_step_records(journey_id, step_order);
