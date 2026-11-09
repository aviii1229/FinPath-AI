-- ====================================================================
-- FinPath AI: V3 Advanced Financial Document Intelligence Migration
-- ====================================================================

-- 1. Extend document_uploads with extraction entities, confidence, manual override flags, and source journey
ALTER TABLE document_uploads
    ADD COLUMN IF NOT EXISTS extracted_entities_json TEXT,
    ADD COLUMN IF NOT EXISTS classification_confidence NUMERIC(4, 3) DEFAULT 0.950,
    ADD COLUMN IF NOT EXISTS is_user_corrected BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS source_journey VARCHAR(50) DEFAULT 'GENERAL',
    ADD COLUMN IF NOT EXISTS document_date DATE,
    ADD COLUMN IF NOT EXISTS expiry_date DATE;

-- 2. Add index on user_id and document_category for fast search and filtering
CREATE INDEX IF NOT EXISTS idx_document_uploads_user_cat ON document_uploads(user_id, document_category);
CREATE INDEX IF NOT EXISTS idx_document_uploads_status ON document_uploads(processing_status);
