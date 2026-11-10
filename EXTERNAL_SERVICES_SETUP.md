# FINPATH AI — External Services & Third-Party Integration Setup
**Version:** 2.0.0 (Post-Upgrade Phase 08)  
**Edition:** Paytm-Hyderabad Edition  
**Repository:** [https://github.com/aviii1229/FinPath-AI](https://github.com/aviii1229/FinPath-AI)  
**Document Type:** Third-Party Architecture & Integration Guide  

---

## 1. Architectural Integration Overview

FinPath AI is designed with **Resilient Self-Contained Fallbacks**:
- In **Development & Demo Modes**, the system requires **zero external paid subscriptions or API keys**. The AI orchestrator, policy comparison, EMI engine, financial health scoring, document parsing, and notification channels operate completely offline using embedded models, local disk storage, and in-memory mock dispatchers.
- In **Commercial Production**, each mock or local subsystem can be upgraded to an enterprise-grade cloud provider simply by supplying the corresponding environment variables and accounts detailed below.

---

## 2. Service-by-Service Integration Breakdown

### 2.1. Managed PostgreSQL & Vector Search (`pgvector`)

| Attribute | Details |
|---|---|
| **Current Implementation** | Local/Docker container using official image `pgvector/pgvector:pg16` with volume persistence. Flyway auto-applies migrations `V1`–`V6`. |
| **Production Target** | Managed Cloud PostgreSQL (AWS RDS PostgreSQL 16, Supabase, Neon.tech, or Azure Database for PostgreSQL). |
| **Extension Required** | `vector` (used for 1536-dimensional semantic policy and document embeddings). |
| **Free Tier Options** | **Neon.tech**: Free 0.5 GB storage, serverless compute, `pgvector` pre-installed. **Supabase**: Free 500 MB DB, `pgvector` pre-installed. |
| **Fallback Behavior** | If connecting without Docker, backend uses embedded H2 database (in-memory mode with PostgreSQL syntax emulation). Vector operations fall back to SQL text queries. |

#### Setup Instructions (Neon / Supabase):
1. Sign up at [Neon.tech](https://neon.tech/) or [Supabase.com](https://supabase.com/).
2. Create a new PostgreSQL 16 project named `finpath-prod`.
3. Open the SQL Editor and execute:
   ```sql
   CREATE EXTENSION IF NOT EXISTS vector;
   ```
4. Copy the connection string (pooled or direct) into `SPRING_DATASOURCE_URL`:
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://ep-xyz.us-east-2.aws.neon.tech/finpath_db?sslmode=require
   SPRING_DATASOURCE_USERNAME=finpath_user
   SPRING_DATASOURCE_PASSWORD=your_neon_password
   ```

---

### 2.2. Large Language Models & AI Gateways

| Attribute | Details |
|---|---|
| **Current Implementation** | **Built-in Deterministic Financial Intelligence & RAG**: FinPath AI runs an internal rule-based, pattern-matching, and tool-invocation orchestrator (`FinancialToolRegistry`, `PolicyRagService`, `DocumentUnderstandingService`, `AiJourneyOrchestratorService`). It generates instant, 100% predictable, hallucination-free financial recommendations without calling any third-party billable LLM. |
| **Optional Cloud LLMs** | Google Gemini (Gemini 1.5 Pro / Flash), OpenAI (GPT-4o), Anthropic (Claude 3.5 Sonnet), or Local Ollama (`llama3:8b`). |
| **Environment Keys** | `GEMINI_API_KEY`, `OPENAI_API_KEY`, `ANTHROPIC_API_KEY`, `OLLAMA_BASE_URL` |
| **Free Tier Options** | **Google AI Studio**: Free rate-limited API keys for Gemini 1.5 Flash (15 RPM free). **Ollama**: 100% free locally hosted inference. |
| **Fallback Behavior** | When API keys are blank or omitted, the backend automatically uses its built-in deterministic financial orchestrator. **No system failure occurs.** |

#### Setup Instructions (Google Gemini):
1. Navigate to [Google AI Studio](https://aistudio.google.com/).
2. Click **Get API key** -> **Create API key in new project**.
3. Copy the key (`AIzaSy...`).
4. Set in `.env`:
   ```env
   GEMINI_API_KEY=AIzaSyD...your_key_here
   ```

---

### 2.3. Transactional Email Notification Service

| Attribute | Details |
|---|---|
| **Current Implementation** | `NotificationChannelDispatcher` detects channel `EMAIL` and logs the structured email payload (`to`, `subject`, `body`) to the server audit log with simulation markers. In-app notifications are saved to the PostgreSQL database. |
| **Production Target** | Transactional SMTP Provider: **Resend**, **SendGrid**, **AWS SES**, or **Brevo**. |
| **Environment Keys** | `SPRING_MAIL_HOST`, `SPRING_MAIL_PORT`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD` |
| **Free Tier Options** | **Resend**: 3,000 emails/month free, modern developer API & SMTP. **Brevo**: 300 emails/day free. |
| **Fallback Behavior** | If SMTP credentials are not configured, emails are logged to the console and marked as simulated in the database audit log. |

#### Setup Instructions (Resend SMTP):
1. Sign up at [Resend.com](https://resend.com/).
2. Add your sending domain (e.g., `notifications.yourdomain.com`).
3. Add the generated DNS records (**SPF**, **DKIM**, **MX**) to your DNS registrar (Cloudflare / GoDaddy).
4. Generate an API Key under **API Keys**.
5. Configure environment variables in backend:
   ```env
   SPRING_MAIL_HOST=smtp.resend.com
   SPRING_MAIL_PORT=587
   SPRING_MAIL_USERNAME=resend
   SPRING_MAIL_PASSWORD=re_123456789...
   ```

---

### 2.4. Web Push Notifications (Browser Push)

| Attribute | Details |
|---|---|
| **Current Implementation** | WebPush subscription endpoints are registered via `NotificationChannelDispatcher`. Browser notifications can be delivered in-tab via standard HTML5 Notification API. |
| **Production Target** | W3C Push API / WebPush with VAPID authentication for background mobile/desktop delivery. |
| **Environment Keys** | `VAPID_PUBLIC_KEY`, `VAPID_PRIVATE_KEY` |
| **Cost** | 100% Free (WebPush protocol is supported natively by Google Chrome, Mozilla Firefox, Apple Safari, and Edge without third-party fees). |
| **Fallback Behavior** | In-app notification center displays all notifications natively with unread badges. |

#### Setup Instructions:
1. Run Node.js VAPID generator:
   ```powershell
   npx web-push generate-vapid-keys
   ```
2. Copy the resulting Public Key and Private Key into your configuration:
   ```env
   VAPID_PUBLIC_KEY=BD8v...
   VAPID_PRIVATE_KEY=y3K...
   ```

---

### 2.5. Document Storage Vault & OCR Processing

| Attribute | Details |
|---|---|
| **Current Implementation** | Files uploaded to the Document Vault are stored on persistent disk storage (`/app/storage/documents` in Docker, mapped to named volume `finpath_document_storage`). Scanned and textual PDFs/images are processed via embedded **Apache Tika** (`tika-parsers-standard-package`). |
| **Production Target** | S3-Compatible Cloud Object Store: **AWS S3**, **Cloudflare R2**, or **Google Cloud Storage**. |
| **Environment Keys** | `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_REGION`, `AWS_S3_BUCKET_NAME` |
| **Free Tier Options** | **Cloudflare R2**: 10 GB free storage/month, zero egress fees. **AWS S3**: 5 GB free storage for 12 months. |
| **Fallback Behavior** | If cloud credentials are unset, the system uses the local volume mount with zero external cloud dependencies. |

#### Setup Instructions (AWS S3):
1. Create a private S3 bucket named `finpath-documents-prod`.
2. Block all public access (files should be served through backend authenticated proxy streams).
3. Create an IAM User `finpath-doc-vault` with policy `AmazonS3FullAccess` scoped to `arn:aws:s3:::finpath-documents-prod/*`.
4. Generate and save IAM Access Key ID and Secret Access Key.

---

### 2.6. Identity & KYC Integration (Indian Fintech Ecosystem)

| Service | Target Partner / Authority | Purpose | Current Implementation Status |
|---|---|---|---|
| **DigiLocker / Aadhaar e-KYC** | National e-Governance Division (NeGD) | Instant verified document retrieval (Aadhaar, PAN, driving license) | **Simulated**: Document Vault supports direct manual upload of PDF/JPG KYC documents with Tika OCR extraction. Live API requires Indian entity registration & MeitY approval. |
| **Credit Information Reports** | CIBIL / Experian / CRIF High Mark | Pulling verified credit score and outstanding credit lines | **Simulated**: Lending AI calculates comprehensive creditworthiness and debt-to-income ratios locally with configurable assumptions. |
| **Account Aggregator (AA)** | RBI-approved NBFC-AA (e.g. Setu, Finvu, OneMoney) | Consented financial data sharing for real-time bank statement fetching | **Simulated**: Fintech module parses uploaded bank statement PDFs and categorizes transactions deterministically. |

---

## 3. Integration Matrix Summary

| Service Category | Local Development | Demo / Sandbox | Commercial Production | Setup Action Required |
|---|---|---|---|---|
| **Relational Database** | In-memory H2 | Docker PostgreSQL 16 | AWS RDS / Neon Postgres 16 | Provision DB & run Flyway |
| **Vector Search** | Text / Exact match | `pgvector` container | Managed `pgvector` | Execute `CREATE EXTENSION vector;` |
| **AI / Orchestrator** | Built-in Rule Engine | Built-in Rule Engine | Built-in or Gemini 1.5 Flash | Optional API Key generation |
| **Email Delivery** | Server Console Log | Server Console Log | Resend / SendGrid SMTP | Create account & verify DNS |
| **Push Notifications** | In-app Badge | In-app Badge | WebPush VAPID Keypair | Run `npx web-push generate-vapid-keys` |
| **Document Storage** | Local Disk Directory | Docker Volume | Cloudflare R2 / AWS S3 | Create bucket & IAM role |
| **OCR Processing** | Apache Tika (Java) | Apache Tika (Java) | Apache Tika + Tesseract | Bundled in Spring Boot image |

