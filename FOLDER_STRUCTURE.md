# FinPath AI — Repository & Folder Structure Documentation

**Project:** FinPath AI (AI-Powered Financial Journey Platform)  
**Architecture:** Modular Monolith (Java 21 / Spring Boot 3) + Single Page Application (React 18 / TypeScript / Tailwind CSS)  
**Containerization:** Docker & Docker Compose  

---

## 1. Root Repository Structure

```text
finpath-ai/
├── .github/                        # CI/CD Workflows (GitHub Actions)
│   ├── workflows/
│   │   ├── backend-ci.yml          # Java 21 build, unit & integration tests
│   │   ├── frontend-ci.yml         # TypeScript check, ESLint, Vitest, build
│   │   └── e2e-tests.yml           # Playwright end-to-end testing
│   └── pull_request_template.md
├── docker/                         # Docker & Container Orchestration configurations
│   ├── backend/
│   │   ├── Dockerfile              # Multi-stage Java 21 Alpine JRE build
│   │   └── entrypoint.sh           # Container startup & profile hook
│   ├── frontend/
│   │   ├── Dockerfile              # Multi-stage Node 20 build + Nginx Alpine
│   │   └── nginx.conf              # SPA routing & reverse proxy config
│   ├── postgres/
│   │   └── init-pgvector.sh        # Postgres extension bootstrap script
│   └── nginx/
│       └── gateway.conf            # Edge reverse proxy, SSL & rate limiting
├── docs/                           # Architectural Specifications & Blueprints
│   ├── ARCHITECTURE.md             # Core System Architecture & Modularity
│   ├── DATABASE_SCHEMA.md          # PostgreSQL + pgvector Data Dictionary & DDL
│   ├── API_DESIGN.md               # REST API Endpoints & OpenAPI Contracts
│   ├── DEVELOPMENT_ROADMAP.md      # Milestones, WBS, Risk Management
│   └── FOLDER_STRUCTURE.md         # This repository structure document
├── backend/                        # Spring Boot 3 + Java 21 Modular Monolith
├── frontend/                       # React 18 + TypeScript + Tailwind CSS SPA
├── docker-compose.yml              # Local development multi-container orchestration
├── docker-compose.prod.yml         # Production container orchestration
├── .gitignore                      # Git ignore rules for Java, Node, IDEs
├── .env.example                    # Template environment variables
└── README.md                       # Quick-start instructions & developer guide
```

---

## 2. Backend Folder Structure (`backend/`)

The backend follows a **Domain-Driven Modular Monolith** package structure. Each business domain (`insurance`, `lending`, `fintech`, `ai`, `security`) is encapsulated with clear boundaries, exposing public interfaces while keeping internal entities and logic encapsulated.

```text
backend/
├── pom.xml                                 # Maven configuration (Java 21, Spring Boot 3.3+)
├── mvnw / mvnw.cmd                         # Maven wrapper scripts
├── src/
│   ├── main/
│   │   ├── java/com/finpath/
│   │   │   ├── FinPathApplication.java     # Main Spring Boot Application Entrypoint
│   │   │   │
│   │   │   ├── core/                       # Core Infrastructure & Shared Kernel
│   │   │   │   ├── config/                 # Application configuration classes
│   │   │   │   │   ├── AsyncConfig.java    # Project Loom Virtual Threads & Executor
│   │   │   │   │   ├── CacheConfig.java    # Caffeine cache configuration
│   │   │   │   │   ├── CorsConfig.java     # Global CORS policies
│   │   │   │   │   └── JacksonConfig.java  # Custom serializers (BigDecimal, Instant)
│   │   │   │   ├── exception/              # Global Error Handling & RFC 7807 Models
│   │   │   │   │   ├── ApiException.java
│   │   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   │   ├── BusinessRuleViolationException.java
│   │   │   │   │   └── GlobalExceptionHandler.java # @RestControllerAdvice
│   │   │   │   ├── auditing/               # Audit trail interceptors & models
│   │   │   │   │   ├── AuditAwareImpl.java
│   │   │   │   │   └── AuditLoggingAspect.java
│   │   │   │   └── util/                   # Shared utility helpers (crypto, formatting)
│   │   │   │       ├── EncryptionUtils.java# AES-256 PII column encryption converter
│   │   │   │       └── FinancialMath.java  # Exact currency rounding & EMI math
│   │   │   │
│   │   │   ├── security/                   # Authentication & Authorization (IAM)
│   │   │   │   ├── config/
│   │   │   │   │   └── SecurityFilterChainConfig.java # Spring Security 6 stateless filter
│   │   │   │   ├── jwt/
│   │   │   │   │   ├── JwtTokenProvider.java          # JWT creation, signing & verification
│   │   │   │   │   └── JwtAuthenticationFilter.java   # OncePerRequestFilter for Bearer token
│   │   │   │   ├── model/
│   │   │   │   │   ├── UserPrincipal.java             # Spring UserDetails implementation
│   │   │   │   │   └── RoleName.java                  # Enum: ROLE_CUSTOMER, ROLE_LOAN_OFFICER, etc.
│   │   │   │   ├── service/
│   │   │   │   │   └── CustomUserDetailsService.java
│   │   │   │   ├── controller/
│   │   │   │   │   └── AuthController.java            # /api/v1/auth/login, register, refresh
│   │   │   │   └── dto/
│   │   │   │       ├── LoginRequest.java
│   │   │   │       ├── RegisterRequest.java
│   │   │   │       └── AuthResponse.java
│   │   │   │
│   │   │   ├── ai/                         # Central AI Orchestrator & RAG Engine
│   │   │   │   ├── gateway/                # Provider-Independent LLM Gateway
│   │   │   │   │   ├── LlmProviderService.java        # Core interface
│   │   │   │   │   ├── LlmGatewayRouter.java          # Dynamic fallback router
│   │   │   │   │   ├── model/                         # Standardized prompt & response DTOs
│   │   │   │   │   │   ├── LlmPrompt.java
│   │   │   │   │   │   └── LlmResponse.java
│   │   │   │   │   └── adapter/                       # Concrete LLM provider adapters
│   │   │   │   │       ├── OpenAiLlmProvider.java
│   │   │   │   │       ├── GeminiLlmProvider.java
│   │   │   │   │       ├── AnthropicLlmProvider.java
│   │   │   │   │       └── OllamaLlmProvider.java
│   │   │   │   ├── rag/                    # Document Ingestion & Vector Retrieval
│   │   │   │   │   ├── DocumentParserService.java     # Apache Tika / PDFBox extraction
│   │   │   │   │   ├── TextChunkingService.java       # Markdown & semantic chunking
│   │   │   │   │   ├── VectorStoreService.java        # pgvector HNSW similarity queries
│   │   │   │   │   └── HybridRetrievalService.java    # Vector + Full-text fusion (RRF)
│   │   │   │   ├── agent/                  # Financial Journey Co-Pilot & Tools
│   │   │   │   │   ├── FinancialJourneyOrchestrator.java # Multi-domain coordination
│   │   │   │   │   ├── IntentClassifier.java             # Classifies Insurance vs Loan vs PFM
│   │   │   │   │   ├── PromptTemplateRegistry.java       # System prompts with guardrails
│   │   │   │   │   └── tools/                            # Function calling implementations
│   │   │   │   │       ├── LendingEligibilityTool.java
│   │   │   │   │       ├── CashFlowAnalysisTool.java
│   │   │   │   │       └── InsuranceRecommenderTool.java
│   │   │   │   ├── controller/
│   │   │   │   │   ├── AiChatController.java          # Chat sessions & SSE message streams
│   │   │   │   │   └── DocumentController.java        # PDF upload & processing status
│   │   │   │   └── entity/
│   │   │   │       ├── DocumentUploadEntity.java
│   │   │   │       ├── DocumentChunkEntity.java       # pgvector mapped entity
│   │   │   │       ├── ChatSessionEntity.java
│   │   │   │       └── ChatMessageEntity.java
│   │   │   │
│   │   │   ├── insurance/                  # Insurance Bounded Context
│   │   │   │   ├── controller/             # /api/v1/insurance/**
│   │   │   │   │   ├── InsuranceProductController.java
│   │   │   │   │   ├── QuoteController.java
│   │   │   │   │   ├── PolicyController.java
│   │   │   │   │   └── ClaimController.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── InsuranceQuoteCalculator.java  # Premium & risk scoring
│   │   │   │   │   ├── PolicyLifecycleService.java    # Binding & certificate generation
│   │   │   │   │   └── ClaimAdjudicationService.java  # Triage & exclusion RAG checking
│   │   │   │   ├── repository/
│   │   │   │   │   ├── InsuranceProductRepository.java
│   │   │   │   │   ├── InsuranceQuoteRepository.java
│   │   │   │   │   ├── InsurancePolicyRepository.java
│   │   │   │   │   └── InsuranceClaimRepository.java
│   │   │   │   ├── entity/
│   │   │   │   │   ├── InsuranceProductEntity.java
│   │   │   │   │   ├── InsuranceQuoteEntity.java
│   │   │   │   │   ├── InsurancePolicyEntity.java
│   │   │   │   │   └── InsuranceClaimEntity.java
│   │   │   │   └── event/                  # Domain Events emitted by Insurance
│   │   │   │       ├── PolicyIssuedEvent.java
│   │   │   │       └── ClaimFiledEvent.java
│   │   │   │
│   │   │   ├── lending/                    # Lending Bounded Context
│   │   │   │   ├── controller/             # /api/v1/lending/**
│   │   │   │   │   ├── LoanProductController.java
│   │   │   │   │   ├── LoanApplicationController.java
│   │   │   │   │   └── AmortizationController.java
│   │   │   │   ├── service/
│   │   │   │   │   ├── LoanEligibilityEngine.java     # DTI & LTV automated verification
│   │   │   │   │   ├── AmortizationScheduleGenerator.java # EMI calculation algorithms
│   │   │   │   │   └── UnderwritingAssistantService.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── LoanProductRepository.java
│   │   │   │   │   ├── LoanApplicationRepository.java
│   │   │   │   │   └── RepaymentScheduleRepository.java
│   │   │   │   ├── entity/
│   │   │   │   │   ├── LoanProductEntity.java
│   │   │   │   │   ├── LoanApplicationEntity.java
│   │   │   │   │   └── RepaymentScheduleEntity.java
│   │   │   │   └── event/
│   │   │   │       └── LoanApplicationSubmittedEvent.java
│   │   │   │
│   │   │   └── fintech/                    # Fintech Bounded Context (PFM & Wealth)
│   │   │       ├── controller/             # /api/v1/fintech/**
│   │   │       │   ├── AccountController.java
│   │   │       │   ├── TransactionController.java
│   │   │       │   ├── BudgetController.java
│   │   │       │   └── CashFlowController.java
│   │   │       ├── service/
│   │   │       │   ├── AccountAggregationService.java
│   │   │       │   ├── TransactionCategorizerService.java # Rule + LLM classifier
│   │   │       │   ├── BudgetMonitoringService.java       # Threshold alerting
│   │   │       │   └── CashFlowForecastingService.java    # 30-day projection
│   │   │       ├── repository/
│   │   │       │   ├── FinancialAccountRepository.java
│   │   │       │   ├── TransactionRepository.java
│   │   │       │   └── BudgetRepository.java
│   │   │       ├── entity/
│   │   │       │   ├── FinancialAccountEntity.java
│   │   │       │   ├── TransactionEntity.java
│   │   │       │   └── BudgetEntity.java
│   │   │       └── event/
│   │   │           └── TransactionCategorizedEvent.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml             # Base configuration
│   │       ├── application-dev.yml         # Local Docker development profile
│   │       ├── application-prod.yml        # Production hardened profile
│   │       ├── db/migration/               # Flyway SQL Migration scripts
│   │       │   ├── V1__Initial_Schema.sql  # Users, Roles, Vector extensions
│   │       │   ├── V2__Fintech_Schema.sql  # Accounts, Transactions, Budgets
│   │       │   ├── V3__Lending_Schema.sql  # Loan products, Applications, Schedules
│   │       │   ├── V4__Insurance_Schema.sql# Policies, Quotes, Claims
│   │       │   └── V5__RAG_Vector_Schema.sql# Document chunks & HNSW vector indexes
│   │       └── prompts/                    # Versioned System Prompt templates
│   │           ├── journey-orchestrator.st
│   │           ├── claim-triage.st
│   │           └── underwriting-assistant.st
│   │
│   └── test/
│       ├── java/com/finpath/
│       │   ├── architecture/               # ArchUnit Architecture Boundary Tests
│       │   │   └── DomainModularityTest.java# Verifies zero illegal cross-domain dependencies
│       │   ├── unit/                       # High-speed unit tests
│       │   │   ├── lending/AmortizationScheduleTest.java
│       │   │   ├── insurance/QuoteCalculationTest.java
│       │   │   └── ai/RecursiveTextChunkerTest.java
│       │   └── integration/                # Integration tests with Testcontainers
│       │       ├── TestcontainersConfiguration.java # PostgreSQL + pgvector container
│       │       ├── RagVectorStoreIntegrationTest.java
│       │       └── LoanApplicationFlowIntegrationTest.java
│       └── resources/
│           └── application-test.yml
```

---

## 3. Frontend Folder Structure (`frontend/`)

The React application adopts a **Feature-Driven Architecture** (Domain-Driven UI). Shared UI primitives and global core services are decoupled from feature-specific views, state, and API endpoints.

```text
frontend/
├── package.json                            # Scripts, dependencies, devDependencies
├── tsconfig.json                           # Strict TypeScript compiler options
├── tsconfig.node.json
├── vite.config.ts                          # Vite build tooling with path aliases (@/*)
├── tailwind.config.js                      # Custom Tailwind theme, tokens, animations
├── postcss.config.js
├── index.html                              # Root HTML template
├── public/                                 # Static assets (favicons, logos, manifests)
│   ├── favicon.ico
│   └── logo-finpath.svg
│
└── src/
    ├── main.tsx                            # React DOM entrypoint & provider wrapping
    ├── App.tsx                             # Top-level Router & QueryClientProvider
    ├── index.css                           # Global Tailwind directives & CSS variables
    │
    ├── core/                               # Singletons, Providers, Global Config
    │   ├── api/
    │   │   ├── apiClient.ts                # Axios instance with auth & correlation interceptors
    │   │   └── sseClient.ts                # Server-Sent Events client for streaming AI chat
    │   ├── auth/
    │   │   ├── AuthContext.tsx             # Auth provider context
    │   │   ├── useAuthStore.ts             # Zustand store for user token, roles, profile
    │   │   └── ProtectedRoute.tsx          # Route guard based on authentication & RBAC
    │   ├── layout/
    │   │   ├── AppHeader.tsx               # Top navigation bar with notifications
    │   │   ├── AppSidebar.tsx              # Dynamic sidebar navigation per role
    │   │   ├── MainLayout.tsx              # Standard dashboard shell layout
    │   │   └── AuthLayout.tsx              # Clean centered layout for login/register
    │   └── routes/
    │       └── router.tsx                  # React Router 6 configuration & lazy-loaded routes
    │
    ├── shared/                             # Reusable components, hooks & types
    │   ├── components/                     # Shadcn / Tailwind Atomic Components
    │   │   ├── ui/
    │   │   │   ├── Button.tsx
    │   │   │   ├── Card.tsx
    │   │   │   ├── Input.tsx
    │   │   │   ├── Modal.tsx
    │   │   │   ├── Badge.tsx
    │   │   │   ├── Progress.tsx
    │   │   │   ├── Table.tsx
    │   │   │   ├── Tabs.tsx
    │   │   │   └── Toast.tsx
    │   │   ├── feedback/
    │   │   │   ├── LoadingSpinner.tsx
    │   │   │   ├── SkeletonCard.tsx
    │   │   │   └── ErrorBoundary.tsx
    │   │   └── data-display/
    │   │       ├── CurrencyDisplay.tsx     # Formats currency with consistent precision
    │   │       ├── StatusBadge.tsx         # Color-coded badges for loan/claim status
    │   │       └── StatMetric.tsx          # Key metric display with trend indicator
    │   ├── hooks/
    │   │   ├── useDebounce.ts
    │   │   ├── useLocalStorage.ts
    │   │   └── useNotification.ts
    │   └── types/
    │       ├── common.ts                   # ApiResponse<T>, PaginatedResponse<T>, ErrorResponse
    │       └── user.ts                     # UserProfile, Role
    │
    └── features/                           # Independent Domain Modules (Domain-Driven UI)
        │
        ├── dashboard/                      # Unified Journey Overview (Phase 7 Cockpit)
        │   ├── components/
        │   │   ├── FinancialHealthScore.tsx
        │   │   ├── ProactiveInsightsFeed.tsx
        │   │   └── QuickActionCards.tsx
        │   ├── pages/
        │   │   └── DashboardPage.tsx
        │   └── api/
        │       └── dashboardApi.ts
        │
        ├── ai-assistant/                   # Central AI Chat Co-Pilot & Journey Planner
        │   ├── components/
        │   │   ├── ChatDrawer.tsx          # Collapsible or standalone advisor view
        │   │   ├── MessageList.tsx         # Virtualized chat history
        │   │   ├── StreamingMessageItem.tsx# Real-time token streaming & markdown
        │   │   ├── CitationBadge.tsx       # Links to referenced policy/document excerpts
        │   │   └── ToolActionCard.tsx      # Interactive action buttons proposed by AI
        │   ├── hooks/
        │   │   └── useAiStreamChat.ts      # Custom hook managing SSE streaming state
        │   ├── pages/
        │   │   └── AiAdvisorPage.tsx
        │   └── api/
        │       └── aiChatApi.ts
        │
        ├── documents/                      # Document Ingestion & RAG Management
        │   ├── components/
        │   │   ├── DocumentDropzone.tsx    # Drag-and-drop file uploader
        │   │   ├── DocumentStatusList.tsx  # Indexing status monitor
        │   │   └── DocumentViewerModal.tsx # PDF preview with highlighted citations
        │   └── api/
        │       └── documentApi.ts
        │
        ├── fintech/                        # Fintech Module (Accounts, Transactions, Budgets)
        │   ├── components/
        │   │   ├── AccountSummaryCards.tsx
        │   │   ├── TransactionTable.tsx    # Paginated, searchable, category-filtered
        │   │   ├── BudgetProgressBar.tsx
        │   │   └── CashFlowChart.tsx       # Recharts 30-day projection
        │   ├── pages/
        │   │   ├── AccountsPage.tsx
        │   │   ├── TransactionsPage.tsx
        │   │   └── BudgetAnalyticsPage.tsx
        │   └── api/
        │       └── fintechApi.ts
        │
        ├── lending/                        # Lending Module (Loans, DTI, Amortization)
        │   ├── components/
        │   │   ├── LoanProductCard.tsx
        │   │   ├── EmiCalculatorSlider.tsx # Principal, tenure, APR slider simulator
        │   │   ├── AmortizationTable.tsx   # Month-by-month principal/interest breakdown
        │   │   └── LoanApplicationWizard.tsx# Multi-step origination flow
        │   ├── pages/
        │   │   ├── LoanMarketplacePage.tsx
        │   │   ├── LoanApplicationPage.tsx
        │   │   └── LoanDetailPage.tsx
        │   └── api/
        │       └── lendingApi.ts
        │
        └── insurance/                      # Insurance Module (Quotes, Policies, Claims)
            ├── components/
            │   ├── PolicyCatalogGrid.tsx
            │   ├── QuoteComparisonCard.tsx
            │   ├── ActivePolicyCard.tsx
            │   └── ClaimSubmissionForm.tsx # Digital claim filing with receipt upload
            ├── pages/
            │   ├── InsuranceStorePage.tsx
            │   ├── MyPoliciesPage.tsx
            │   └── ClaimsPortalPage.tsx
            └── api/
                └── insuranceApi.ts
```

