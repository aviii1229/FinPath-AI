# FINPATH AI — Complete Environment Setup & Configuration Guide
**Version:** 2.0.0 (Post-Upgrade Phase 08)  
**Edition:** Paytm-Hyderabad Edition  
**Repository:** [https://github.com/aviii1229/FinPath-AI](https://github.com/aviii1229/FinPath-AI)  
**Document Type:** Technical Operations & DevOps Specification  

---

## 1. Environment Variable Master Catalog

The table below catalogs every environment variable recognized by the FinPath AI platform across Backend (Spring Boot 3.3.5 / Java 21), Frontend (React 18 / Vite / TypeScript), and Orchestration (Docker Compose).

| Variable Name | Tier | Status | Default / Fallback | Description & Requirements | Where to Obtain |
|---|---|---|---|---|---|
| `POSTGRES_DB` | Database / Docker | **Mandatory** (Docker) | `finpath_db` | Name of the PostgreSQL database instance. | User defined in `.env` |
| `POSTGRES_USER` | Database / Docker | **Mandatory** (Docker) | `finpath_user` | Superuser / application user for PostgreSQL. | User defined in `.env` |
| `POSTGRES_PASSWORD` | Database / Docker | **Mandatory** | `finpath_secret` | High-entropy password for database authentication. | Generated locally or by cloud DB provider |
| `POSTGRES_PORT` | Database / Docker | Optional | `5432` | Host port mapped to PostgreSQL container. | Standard database port |
| `SPRING_PROFILES_ACTIVE` | Backend | **Mandatory** | `default` (H2) | Active Spring profile (`default`, `dev`, or `docker`). | Set to `dev` for local Postgres, `docker` for container |
| `SPRING_DATASOURCE_URL` | Backend | **Mandatory** (Prod) | `jdbc:h2:mem:finpath_db...` | JDBC Connection URL to PostgreSQL instance. | Cloud DB console or Docker bridge network |
| `SPRING_DATASOURCE_USERNAME` | Backend | **Mandatory** (Prod) | `sa` | JDBC connection username. | Database user credentials |
| `SPRING_DATASOURCE_PASSWORD` | Backend | **Mandatory** (Prod) | *(empty)* | JDBC connection password. | Database user credentials |
| `SPRING_PORT` | Backend | Optional | `8080` | Port for Spring Boot HTTP server. | Host OS configuration |
| `JWT_SECRET` | Backend / Security | **Mandatory** (Prod) | Built-in test 64-hex string | Secret HMAC key used to sign and verify JWT tokens. Must be >= 256 bits. | Generated via PowerShell or OpenSSL |
| `JWT_EXPIRATION_MS` | Backend / Security | Optional | `86400000` (24h) | Time in milliseconds before JWT access token expires. | System configuration |
| `OPENAI_API_KEY` | Backend / AI | Optional | *(empty)* | Optional API key for OpenAI GPT models. System defaults to local pattern orchestrator if empty. | [OpenAI API Keys](https://platform.openai.com/api-keys) |
| `GEMINI_API_KEY` | Backend / AI | Optional | *(empty)* | Optional API key for Google Gemini models. System defaults to local pattern orchestrator if empty. | [Google AI Studio](https://aistudio.google.com/) |
| `ANTHROPIC_API_KEY` | Backend / AI | Optional | *(empty)* | Optional API key for Anthropic Claude models. | [Anthropic Console](https://console.anthropic.com/) |
| `OLLAMA_BASE_URL` | Backend / AI | Optional | `http://host.docker.internal:11434` | HTTP endpoint for locally hosted Ollama LLM models. | Local Ollama instance |
| `SPRING_MAIL_HOST` | Backend / Notify | Optional | `localhost` | SMTP relay server host (e.g. `smtp.resend.com` or `smtp.sendgrid.net`). | Email service provider |
| `SPRING_MAIL_PORT` | Backend / Notify | Optional | `587` | SMTP port (587 for STARTTLS, 465 for SSL). | Email service provider |
| `SPRING_MAIL_USERNAME` | Backend / Notify | Optional | *(empty)* | SMTP login username / API key name. | Email service provider |
| `SPRING_MAIL_PASSWORD` | Backend / Notify | Optional | *(empty)* | SMTP API key secret. | Email service provider |
| `VAPID_PUBLIC_KEY` | Frontend / Push | Optional | *(empty)* | Public key for WebPush browser push subscriptions. | Generated via `web-push` CLI |
| `VAPID_PRIVATE_KEY` | Backend / Push | Optional | *(empty)* | Private key for WebPush notification payload signing. | Generated via `web-push` CLI |
| `FRONTEND_PORT` | Frontend / Docker | Optional | `80` | Host port mapped to Nginx web server. | Docker compose config |
| `VITE_API_BASE_URL` | Frontend / Vite | **Mandatory** | `http://localhost:8080/api/v1` | URL endpoint consumed by Axios/Fetch client. In Docker Nginx, set to `/api/v1`. | Frontend build environment |

---

## 2. Windows PowerShell Secret Generation Playbook

Run these commands in Windows PowerShell to generate production-grade cryptographically secure secrets:

### 2.1. Generate 512-bit Base64 JWT Signing Secret
```powershell
$bytes = New-Object byte[] 64
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
$jwtSecret = [Convert]::ToBase64String($bytes)
Write-Host "Generated JWT_SECRET:" -ForegroundColor Green
Write-Host $jwtSecret
```

### 2.2. Generate High-Entropy Database Password (32 chars)
```powershell
$chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*"
$dbPassword = -join ((1..32) | ForEach-Object { $chars[(Get-Random -Maximum $chars.Length)] })
Write-Host "Generated POSTGRES_PASSWORD:" -ForegroundColor Green
Write-Host $dbPassword
```

### 2.3. Generate WebPush VAPID Keypair (Requires Node.js)
```powershell
npx web-push generate-vapid-keys
```

---

## 3. Four Deployment Execution Profiles

### Profile A: Zero-Docker Local Development (H2 In-Memory)
*Ideal for rapid UI/feature development and automated test runs.*

1. **Backend:**
   ```powershell
   cd c:\Users\avira\.gemini\antigravity-ide\scratch\FinPath\backend
   # Uses default profile with in-memory H2 database (PostgreSQL mode)
   .\gradlew.bat bootRun
   ```
   *Backend starts at `http://localhost:8080` with Swagger UI at `http://localhost:8080/swagger-ui.html`.*

2. **Frontend:**
   ```powershell
   cd c:\Users\avira\.gemini\antigravity-ide\scratch\FinPath\frontend
   npm install
   npm run dev
   ```
   *Frontend dev server starts at `http://localhost:5173` pointing to `http://localhost:8080/api/v1`.*

---

### Profile B: Local Development with Docker PostgreSQL (Hybrid Mode)
*Runs real PostgreSQL 16 + pgvector in Docker while running Spring Boot and Vite natively for rapid code hot-reloading.*

1. **Start PostgreSQL Container Only:**
   ```powershell
   cd c:\Users\avira\.gemini\antigravity-ide\scratch\FinPath
   docker-compose up -d postgres
   ```

2. **Run Backend with `dev` Profile:**
   ```powershell
   cd backend
   $env:SPRING_PROFILES_ACTIVE = "dev"
   $env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/finpath_db"
   $env:SPRING_DATASOURCE_USERNAME = "finpath_user"
   $env:SPRING_DATASOURCE_PASSWORD = "finpath_secret"
   .\gradlew.bat bootRun
   ```
   *Flyway automatically runs all migrations `V1` through `V6`.*

3. **Run Frontend:**
   ```powershell
   cd ..\frontend
   npm run dev
   ```

---

### Profile C: Fully Containerized Stack (Docker Compose)
*Recommended for demos, integration testing, and local production parity.*

1. **Prepare Environment File:**
   ```powershell
   cd c:\Users\avira\.gemini\antigravity-ide\scratch\FinPath
   Copy-Item .env.example .env
   ```

2. **Build and Launch Containerized Services:**
   ```powershell
   docker-compose up --build -d
   ```

3. **Verify Container Health:**
   ```powershell
   docker-compose ps
   ```
   Expected status:
   - `finpath-postgres`: Up (healthy) on port `5432`
   - `finpath-backend`: Up on port `8080`
   - `finpath-frontend`: Up on port `80`

4. **Access Applications:**
   - Web Application: `http://localhost`
   - Backend Direct API: `http://localhost:8080/api/v1`
   - API Documentation: `http://localhost:8080/swagger-ui.html`

---

### Profile D: Staging & Production Cloud Deployment
*For cloud VMs, Kubernetes, AWS ECS, or Render/Railway.*

1. **Managed Database:**
   - Provision a PostgreSQL 16+ instance.
   - Run query: `CREATE EXTENSION IF NOT EXISTS vector;`
   - Set `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`.
   - Set `SPRING_PROFILES_ACTIVE=docker` (enables Flyway migrations `V1`–`V6`).

2. **Backend Service Container:**
   - Deploy image built from `backend/Dockerfile`.
   - Inject `JWT_SECRET`, `SPRING_DATASOURCE_*`, and optional SMTP variables as container environment variables.
   - Mount persistent volume to `/app/storage/documents` or configure cloud S3 bucket.

3. **Frontend SPA Hosting:**
   - Build static assets with production API URL:
     ```powershell
     cd frontend
     $env:VITE_API_BASE_URL = "https://api.yourdomain.com/api/v1"
     npm run build
     ```
   - Deploy `dist/` directory to Cloudflare Pages, AWS CloudFront + S3, Vercel, or Nginx.

---

## 4. Environment Verification & Health Check Scripts

Execute these PowerShell verification scripts to confirm the runtime environment is operating correctly:

### 4.1. Test Backend API Health
```powershell
try {
    $res = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method Get -TimeoutSec 5
    Write-Host "Backend Status: $($res.status)" -ForegroundColor Green
} catch {
    # If actuator is secured or fallback to base API check:
    try {
        $res = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/insurance/policies" -Method Get -TimeoutSec 5
        Write-Host "Backend API is LIVE and responding (Status 200 OK)" -ForegroundColor Green
    } catch {
        Write-Host "Backend API is NOT reachable: $_" -ForegroundColor Red
    }
}
```

### 4.2. Test PostgreSQL Connection & Vector Extension
```powershell
docker exec -it finpath-postgres psql -U finpath_user -d finpath_db -c "SELECT extname, extversion FROM pg_extension WHERE extname = 'vector';"
```
*Expected output: Row displaying `vector` and installed version (e.g. `0.7.0` or `0.8.0`).*

### 4.3. Test Document Storage Permissions
```powershell
docker exec -it finpath-backend ls -ld /app/storage/documents
```
*Expected output: Directory exists with write permissions.*

