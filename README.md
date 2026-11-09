# FinPath AI — Autonomous Financial Journey Platform
### Paytm-Hyderabad Edition

[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java 21](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://adoptium.net)
[![React 19](https://img.shields.io/badge/React-19.0-blue.svg)](https://react.dev)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.6-blue.svg)](https://www.typescriptlang.org)
[![PostgreSQL 16](https://img.shields.io/badge/PostgreSQL-16%20%2B%20pgvector-blue.svg)](https://github.com/pgvector/pgvector)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-3.4-38B2AC.svg)](https://tailwindcss.com)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED.svg)](https://www.docker.com)
[![Tests](https://img.shields.io/badge/Tests-Passed%20(100%25)-success.svg)](https://github.com)

**FinPath AI (Paytm-Hyderabad Edition)** is an enterprise-grade autonomous financial journey platform uniting **Insurance**, **Lending**, and **Fintech (Wealth & Personal Finance Management)** under a central, context-aware AI orchestrator with grounded RAG document processing, deterministic calculation engines, and localized Indian Rupee (INR - ₹) financial frameworks.

---

## 🏛️ System Architecture

FinPath AI is built as a **Modular Monolith** designed for high cohesion, low coupling, and zero unnecessary microservice network latency:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                      React 19 + TypeScript + Tailwind SPA                   │
│   (Electric Cyan & Navy · Dark Glassmorphism · Recharts · Lucide Icons)     │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │ REST / JSON (JWT Protected)
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                       FinPath AI Spring Boot 3.3.4 Backend                  │
│                        (Java 21 Virtual Threads / Loom)                     │
│                                                                             │
│  ┌───────────────────────┐  ┌──────────────────────┐  ┌──────────────────┐  │
│  │   Auth & Security     │  │ Central Orchestrator │  │  RAG & Document  │  │
│  │ (JWT, BCrypt, RBAC)   │  │   (Cognitive Brain)  │  │  (Apache Tika)   │  │
│  └──────────┬────────────┘  └──────────┬───────────┘  └─────────┬────────┘  │
│             │                          │                        │           │
│  ┌──────────▼────────────┐  ┌──────────▼───────────┐  ┌─────────▼────────┐  │
│  │    Insurance Domain   │  │    Lending Domain    │  │  Fintech Domain  │  │
│  │ • Family Floater Recs │  │ • Reducing Bal EMI   │  │ • PFM Ledger     │  │
│  │ • Side-by-side Matrix │  │ • Side-by-side Rates │  │ • 50-30-20 Rule  │  │
│  │ • Claims Adjudication │  │ • Underwriting State │  │ • Cashflow Intel │  │
│  └───────────────────────┘  └──────────────────────┘  └──────────────────┘  │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │ JDBC / Connection Pool
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                      PostgreSQL 16 Database + pgvector                      │
│        (UUID PKs · pgvector Semantic Indexing · Flyway Migrations)          │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 🚀 Key Functional Modules

### 1. Central AI Journey Orchestrator
- **Intent Recognition**: Classifies goals across Insurance, Lending, Fintech, or Cross-Domain Optimization.
- **Authoritative Service Invocations**: Directly executes Spring Boot business logic (`InsuranceService`, `LendingService`, `FintechService`), returning verified calculation metrics and document citations.
- **Multi-Step Workflows**: Tracks progress with visual journey steppers (`currentStep`, `totalSteps`, `completedSteps`, `pendingSteps`).
- **Interactive Action Pills**: Context-aware prompts and deep-links (`/insurance`, `/lending`, `/fintech`).

### 2. Insurance AI
- **Insurance Discovery**: Interactive requirements intake recommending tailored policies (Family Floater, Term Life, Critical Illness).
- **Comparison Matrix**: Side-by-side analytical evaluation of coverage, deductibles, waiting periods, sub-limits, and cashless hospital networks.
- **RAG Policy Assistant**: PDF policy upload with Apache Tika text extraction and semantic question answering with section citations.
- **Claims Workflow**: Automated document checklists (Form 80D, TPA pre-authorization) and stage tracking.

### 3. Lending AI
- **Loan Discovery & Eligibility**: Dynamic Debt-to-Income (DTI) and Fixed Obligation to Income Ratio (FOIR) evaluation.
- **Deterministic Java EMI Calculator**: Reducing-balance formula ($E = P \cdot r \cdot \frac{(1+r)^n}{(1+r)^n - 1}$) computing exact monthly installments, amortization schedules, and total borrowing costs.
- **Lender Comparison**: Side-by-side comparison across banks and NBFCs showing APRs, processing fees, and net borrowing costs.
- **Application Tracking & Underwriting Engine**: Full lifecycle from `DRAFT` $\to$ `SUBMITTED` $\to$ `UNDER_REVIEW` $\to$ simulated `APPROVED` / `REFERRED`.

### 4. Fintech AI (Wealth & PFM)
- **Financial Ledger**: Multi-account transaction tracking with instant search, filtering, and manual entry.
- **AI Categorization**: Merchant intelligence and automated category labeling with user override controls.
- **CSV Import**: Bulk transaction ingestion with automated schema parsing.
- **Smart Budgeting**: Category limits with real-time utilization progress and overspending alerts.
- **AI Expenditure Insights**: Anomaly detection and discretionary leakage identification.
- **Savings Planner**: 50-30-20 rule allocations and automated emergency reserve tracking.

---

## 🔑 Demo Credentials & Pre-Seeded Personas

When running locally or via Docker, the database is pre-seeded with realistic financial portfolios:

| Persona | Email | Password | Role | Financial Health |
| :--- | :--- | :--- | :--- | :--- |
| **Sarah Connor** (Primary Customer) | `sarah.connor@finpath.ai` | `SecurePass123!` | `ROLE_CUSTOMER` | **84 / 100** (Excellent) |
| **Alex Murphy** (New Onboarding) | `alex.murphy@finpath.ai` | `SecurePass123!` | `ROLE_CUSTOMER` | **72 / 100** (Good) |

---

## ⚡ Quick Start & Local Development

### Prerequisites
- **Java 21 LTS** (Eclipse Adoptium OpenJDK recommended)
- **Node.js 20+** and **npm**
- **Docker & Docker Compose** (optional for containerized deployment)

### 1. Backend Setup (Spring Boot)

```bash
cd backend

# Run automated tests (41 tests)
./gradlew test

# Start the Spring Boot application (Default in-memory H2 with PostgreSQL syntax)
./gradlew bootRun
```
The backend starts at `http://localhost:8080`.
- **Swagger / OpenAPI Documentation**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### 2. Frontend Setup (React 19 SPA)

```bash
cd frontend

# Install dependencies
npm install

# Run automated test suite (26 tests)
npm test -- --run

# Start the Vite development server
npm run dev
```
Open [http://localhost:5173](http://localhost:5173) in your browser.

---

## 🐳 Docker Compose Deployment

To deploy the entire production stack (PostgreSQL 16 + pgvector, Spring Boot Backend, and Nginx SPA Frontend):

```bash
# 1. Clone or navigate to the repository
cd FinPath

# 2. Copy the environment configuration template
cp .env.example .env

# 3. Start all services in detached mode
docker compose up -d --build

# 4. Check service status
docker compose ps
```

### Deployed Services:
- **Frontend SPA (Nginx)**: [http://localhost](http://localhost) (Port 80)
- **Backend API**: [http://localhost:8080/api/v1](http://localhost:8080/api/v1)
- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **PostgreSQL Database**: `localhost:5432` (Database: `finpath_db`, User: `finpath_user`)

To view live logs:
```bash
docker compose logs -f backend
```

To stop and remove containers:
```bash
docker compose down
```

---

## 📡 REST API Reference

| Domain | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Auth** | `POST` | `/api/v1/auth/register` | Register a new user account |
| **Auth** | `POST` | `/api/v1/auth/login` | Authenticate user & return signed JWT |
| **Auth** | `GET` | `/api/v1/users/me` | Fetch authenticated user profile & health score |
| **AI Orchestrator** | `POST` | `/api/v1/ai/chat/orchestrate` | Unified cross-domain AI journey orchestration |
| **AI Orchestrator** | `POST` | `/api/v1/ai/chat/sessions` | Create a new conversational journey session |
| **AI Orchestrator** | `GET` | `/api/v1/ai/chat/sessions` | List user conversational sessions |
| **Insurance** | `GET` | `/api/v1/insurance/products` | Retrieve active insurance products catalog |
| **Insurance** | `POST` | `/api/v1/insurance/discovery/recommendations` | AI requirements discovery & product suggestions |
| **Insurance** | `POST` | `/api/v1/insurance/compare` | Side-by-side policy feature comparison matrix |
| **Insurance** | `POST` | `/api/v1/insurance/assistant/query` | Grounded RAG query answering from uploaded policies |
| **Insurance** | `POST` | `/api/v1/insurance/claims/checklist` | Generate incident-specific claim document checklists |
| **Lending** | `POST` | `/api/v1/lending/discovery/eligibility` | Illustrative eligibility, DTI & FOIR calculations |
| **Lending** | `POST` | `/api/v1/lending/calculator/emi` | Authoritative Java EMI calculation & amortization |
| **Lending** | `POST` | `/api/v1/lending/compare` | Competing lender loan terms comparison matrix |
| **Lending** | `POST` | `/api/v1/lending/applications` | Create and submit a loan application |
| **Fintech** | `GET` | `/api/v1/fintech/accounts` | Retrieve linked checking, savings & credit accounts |
| **Fintech** | `GET` | `/api/v1/fintech/transactions` | Paginated financial ledger with search & category filter |
| **Fintech** | `POST` | `/api/v1/fintech/transactions/import-csv` | Bulk CSV transaction ledger import |
| **Fintech** | `GET` | `/api/v1/fintech/budgets` | Fetch monthly category budget limits & utilization |
| **Fintech** | `POST` | `/api/v1/fintech/budgets` | Create or update category budget ceiling |
| **Fintech** | `GET` | `/api/v1/fintech/insights` | AI expenditure insights, anomalies & spending spikes |
| **Fintech** | `GET` | `/api/v1/fintech/savings-goals` | Algorithmic savings goals planner & progress tracking |

---

## 🔒 Security & Data Protection

- **Stateless JWT Authentication**: HMAC-SHA256 tokens validated on every request via `JwtAuthenticationFilter`.
- **Role-Based Access Control (RBAC)**: Fine-grained method security (`ROLE_CUSTOMER`, `ROLE_ADMIN`, `ROLE_LOAN_OFFICER`, `ROLE_UNDERWRITER`).
- **Cryptographic Protection**: Passwords hashed with BCrypt ($2^{10}$ work factor); national identifiers encrypted via AES-256.
- **Document Sandboxing**: Apache Tika executes in restricted I/O boundaries with strict MIME-type validation.
- **Provider-Independent AI Resilience**: Resilience4j circuit breakers prevent cascade failures during LLM latency spikes.
- **Grounded AI Guardrails**: Strict policy citation matching prevents hallucination of financial terms or rates.

---

## 🧪 Testing Verification Report

FinPath AI maintains a 100% test pass rate across both backend and frontend layers:

```
=============================================================================
                          FINPATH AI TEST VERIFICATION
=============================================================================
 Backend Integration Tests (Spring Boot + JUnit 5 + MockMvc):
   • AiChatIntegrationTest           : 6/6 PASSED
   • AuthIntegrationTest             : 4/4 PASSED
   • InsuranceIntegrationTest        : 10/10 PASSED
   • LendingIntegrationTest          : 10/10 PASSED
   • FintechIntegrationTest          : 10/10 PASSED
   • FinPathApplicationTests         : 1/1 PASSED
   ─────────────────────────────────────────────────
   Backend Total                     : 41/41 PASSED (100% Green)

 Frontend Integration Tests (Vitest + React Testing Library):
   • navigation.test.tsx             : 26/26 PASSED
   ─────────────────────────────────────────────────
   Frontend Total                    : 26/26 PASSED (100% Green)

 Total Platform Test Suite           : 67/67 PASSED (100% Green)
 Production Build (npm run build)    : Clean compilation (0 TS/Vite errors)
=============================================================================
```

---

## 📄 License
This project is proprietary software developed for FinPath AI. All rights reserved.
