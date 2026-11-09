package com.finpath.modules.fintech.controller;

import com.finpath.core.dto.ApiResponse;
import com.finpath.modules.fintech.dto.*;
import com.finpath.modules.fintech.service.FintechService;
import com.finpath.security.model.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fintech")
@Tag(name = "Fintech & PFM", description = "Personal Finance Management, Transactions, AI Categorization, Budgets & Savings")
public class FintechController {

    private final FintechService fintechService;

    public FintechController(FintechService fintechService) {
        this.fintechService = fintechService;
    }

    @GetMapping("/accounts")
    @Operation(summary = "Get all aggregated financial accounts for the user")
    public ResponseEntity<ApiResponse<List<FinancialAccountDto>>> getAccounts(
            @AuthenticationPrincipal UserPrincipal user) {
        List<FinancialAccountDto> accounts = fintechService.getAccounts(user.getId());
        return ResponseEntity.ok(ApiResponse.success(accounts));
    }

    @PostMapping("/accounts")
    @Operation(summary = "Connect a new financial institution account")
    public ResponseEntity<ApiResponse<FinancialAccountDto>> connectAccount(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody FinancialAccountDto dto) {
        FinancialAccountDto created = fintechService.connectAccount(user.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Account connected", created));
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get paginated transaction ledger")
    public ResponseEntity<ApiResponse<Page<TransactionDto>>> getTransactions(
            @AuthenticationPrincipal UserPrincipal user,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<TransactionDto> page = fintechService.getTransactions(user.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    @GetMapping("/transactions/all")
    @Operation(summary = "Get all transactions for the user")
    public ResponseEntity<ApiResponse<List<TransactionDto>>> getAllTransactions(
            @AuthenticationPrincipal UserPrincipal user) {
        List<TransactionDto> list = fintechService.getAllTransactions(user.getId());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/transactions")
    @Operation(summary = "Record a manual transaction into ledger")
    public ResponseEntity<ApiResponse<TransactionDto>> createTransaction(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateTransactionRequest request) {
        TransactionDto tx = fintechService.createTransaction(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Transaction recorded", tx));
    }

    @PatchMapping("/transactions/{id}/category")
    @Operation(summary = "Manually correct or update a transaction category")
    public ResponseEntity<ApiResponse<TransactionDto>> updateTransactionCategory(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTransactionCategoryRequest request) {
        TransactionDto updated = fintechService.updateTransactionCategory(user.getId(), id, request.getCategory());
        return ResponseEntity.ok(ApiResponse.success("Category updated", updated));
    }

    @PostMapping("/transactions/auto-categorize")
    @Operation(summary = "Auto categorize merchant name and detect clean merchant")
    public ResponseEntity<ApiResponse<AutoCategorizeResponse>> autoCategorize(
            @Valid @RequestBody AutoCategorizeRequest request) {
        AutoCategorizeResponse response = fintechService.autoCategorizeMerchant(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/transactions/import-csv")
    @Operation(summary = "Import and validate batch transactions from CSV")
    public ResponseEntity<ApiResponse<CsvImportResultDto>> importCsv(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody CsvImportRequest request) {
        CsvImportResultDto result = fintechService.importCsvTransactions(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("CSV transactions imported", result));
    }

    @GetMapping("/budgets")
    @Operation(summary = "Get monthly budget category limits and current consumption")
    public ResponseEntity<ApiResponse<List<BudgetDto>>> getBudgets(
            @AuthenticationPrincipal UserPrincipal user) {
        List<BudgetDto> budgets = fintechService.getBudgets(user.getId());
        return ResponseEntity.ok(ApiResponse.success(budgets));
    }

    @PostMapping("/budgets")
    @Operation(summary = "Create or update category budget limit")
    public ResponseEntity<ApiResponse<BudgetDto>> createOrUpdateBudget(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody BudgetDto dto) {
        BudgetDto updated = fintechService.createOrUpdateBudget(user.getId(), dto);
        return ResponseEntity.ok(ApiResponse.success("Budget updated", updated));
    }

    @GetMapping("/insights")
    @Operation(summary = "Get AI monthly expenditure analysis, anomalies & savings opportunities")
    public ResponseEntity<ApiResponse<FinancialInsightsResponse>> getFinancialInsights(
            @AuthenticationPrincipal UserPrincipal user) {
        FinancialInsightsResponse insights = fintechService.getFinancialInsights(user.getId());
        return ResponseEntity.ok(ApiResponse.success(insights));
    }

    @GetMapping("/savings-goals")
    @Operation(summary = "Get user savings goals with required monthly contributions")
    public ResponseEntity<ApiResponse<List<SavingsGoalDto>>> getSavingsGoals(
            @AuthenticationPrincipal UserPrincipal user) {
        List<SavingsGoalDto> goals = fintechService.getSavingsGoals(user.getId());
        return ResponseEntity.ok(ApiResponse.success(goals));
    }

    @PostMapping("/savings-goals")
    @Operation(summary = "Create a new savings goal with target date and amount")
    public ResponseEntity<ApiResponse<SavingsGoalDto>> createSavingsGoal(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateSavingsGoalRequest request) {
        SavingsGoalDto created = fintechService.createSavingsGoal(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Savings goal created", created));
    }

    @PatchMapping("/savings-goals/{id}")
    @Operation(summary = "Update current saved amount for a savings goal")
    public ResponseEntity<ApiResponse<SavingsGoalDto>> updateSavingsGoal(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @RequestBody Map<String, BigDecimal> body) {
        BigDecimal newAmount = body.getOrDefault("currentSavedAmount", BigDecimal.ZERO);
        SavingsGoalDto updated = fintechService.updateSavingsGoal(user.getId(), id, newAmount);
        return ResponseEntity.ok(ApiResponse.success("Savings goal progress updated", updated));
    }

    @DeleteMapping("/savings-goals/{id}")
    @Operation(summary = "Delete a savings goal")
    public ResponseEntity<ApiResponse<Void>> deleteSavingsGoal(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id) {
        fintechService.deleteSavingsGoal(user.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Savings goal deleted", null));
    }

    @GetMapping("/cashflow/forecast")
    @Operation(summary = "Get 30-day forward liquidity & burn rate forecast")
    public ResponseEntity<ApiResponse<CashflowForecastDto>> getCashflowForecast(
            @AuthenticationPrincipal UserPrincipal user) {
        CashflowForecastDto forecast = fintechService.getCashflowForecast(user.getId());
        return ResponseEntity.ok(ApiResponse.success(forecast));
    }

    @GetMapping("/health-score")
    @Operation(summary = "Calculate transparent financial health score with individual factor breakdowns")
    public ResponseEntity<ApiResponse<FinancialHealthScoreDto>> getFinancialHealthScore(
            @AuthenticationPrincipal UserPrincipal user) {
        FinancialHealthScoreDto healthScore = fintechService.calculateFinancialHealthScore(user.getId());
        return ResponseEntity.ok(ApiResponse.success(healthScore));
    }

    @GetMapping("/smart-expenses")
    @Operation(summary = "Analyze subscriptions, high expenses, MoM category increases and suspected duplicates")
    public ResponseEntity<ApiResponse<SmartExpenseAnalysisDto>> getSmartExpenses(
            @AuthenticationPrincipal UserPrincipal user) {
        SmartExpenseAnalysisDto smartExpenses = fintechService.analyzeSmartExpenses(user.getId());
        return ResponseEntity.ok(ApiResponse.success(smartExpenses));
    }

    @PostMapping("/smart-expenses/confirm-duplicate")
    @Operation(summary = "Confirm or dismiss suspected duplicate transaction with user consent")
    public ResponseEntity<ApiResponse<Void>> confirmDuplicate(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody DuplicateConfirmationRequest request) {
        fintechService.confirmDuplicateTransaction(user.getId(), request.getDuplicateTransactionId(), request.isConfirmed());
        String msg = request.isConfirmed() ? "Transaction confirmed as duplicate" : "Transaction dismissed as distinct charge";
        return ResponseEntity.ok(ApiResponse.success(msg, null));
    }

    @PostMapping("/what-if/simulate")
    @Operation(summary = "Run interactive what-if cashflow simulation with category cuts, loan EMI and savings boosts")
    public ResponseEntity<ApiResponse<WhatIfSimulationResponseDto>> simulateWhatIf(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody WhatIfSimulationRequestDto request) {
        WhatIfSimulationResponseDto response = fintechService.simulateWhatIf(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
