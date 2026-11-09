import { apiClient } from './apiClient';
import { FinancialAccount, Transaction, Budget } from '@/types';
import {
  mockAccounts,
  mockTransactions,
  mockBudgets,
} from '@/mock/mockData';

export interface CreateTransactionPayload {
  accountId: string;
  amount: number;
  transactionDate: string;
  merchantName: string;
  category: string;
  isRecurring?: boolean;
}

export interface RecurringBillItem {
  billName: string;
  category: string;
  amount: number;
  dueDayOfMonth: number;
  status: string;
}

export interface CashFlowForecast {
  currentLiquidCash: number;
  projectedCashIn30Days: number;
  expectedInflows30Days: number;
  expectedOutflows30Days: number;
  burnRateDailyAverage: number;
  liquidityHealth: string;
  forecastPoints?: {
    date: string;
    projectedBalance: number;
    confidenceLow: number;
    confidenceHigh: number;
  }[];
  recurringBills?: RecurringBillItem[];
  recurringBillsTotal?: number;
  potentialShortageDetected?: boolean;
  shortageRiskLevel?: string;
  lowestProjectedBalance?: number;
  forecastAssumptions?: string[];
  uncertaintyNotes?: string;
  disclaimer?: string;
}

export interface AutoCategorizeResult {
  rawMerchantName: string;
  cleanMerchantName: string;
  category: string;
  confidenceScore: number;
  reasoning: string;
}

export interface CsvImportResult {
  importedCount: number;
  skippedCount: number;
  parsingNotes: string[];
  importedTransactions: Transaction[];
}

export interface CategorySpending {
  category: string;
  amount: number;
  percentage: number;
  transactionCount: number;
}

export interface MonthlyTrendPoint {
  month: string;
  income: number;
  expenses: number;
  savings: number;
  savingsRate: number;
}

export interface FinancialInsight {
  type: 'ANOMALY' | 'OPPORTUNITY' | 'ACHIEVEMENT' | 'WARNING';
  title: string;
  description: string;
  potentialMonthlySavings: number;
  impactLevel: 'HIGH' | 'MEDIUM' | 'LOW';
  actionRecommendation: string;
}

export interface MonthlySummary {
  totalIncome: number;
  totalExpenses: number;
  netSavings: number;
  savingsRate: number;
  previousMonthExpenses: number;
  expenseChangePercentage: number;
  categoryDistribution: CategorySpending[];
}

export interface FinancialInsightsResult {
  monthlySummary: MonthlySummary;
  insights: FinancialInsight[];
  unusualSpikes: CategorySpending[];
  historicalMonthlyTrends: MonthlyTrendPoint[];
}

export interface SavingsGoal {
  id: string;
  title: string;
  category: string;
  targetAmount: number;
  currentSavedAmount: number;
  targetDate: string;
  monthlyRequiredSavings: number;
  progressPercentage: number;
  status: string;
  monthsRemaining: number;
  createdAt: string;
  affordabilityStatus?: string; // AFFORDABLE, STRETCH, DEFICIT
}

export interface CreateSavingsGoalPayload {
  title: string;
  category: string;
  targetAmount: number;
  currentSavedAmount?: number;
  targetDate: string;
}

// ----------------------------------------------------
// UPGRADE PHASE 04: ADVANCED FINANCIAL INTELLIGENCE INTERFACES
// ----------------------------------------------------

export interface ScoringFactor {
  factorName: string;
  scoreAwarded: number;
  maxScore: number;
  currentMetricValue: string;
  benchmark: string;
  status: 'OPTIMAL' | 'GOOD' | 'FAIR' | 'VULNERABLE';
  explanation: string;
}

export interface FinancialHealthScore {
  overallScore: number;
  healthTier: 'EXCELLENT' | 'GOOD' | 'FAIR' | 'NEEDS_ATTENTION';
  summaryText: string;
  scoringFactors: ScoringFactor[];
  improvementAreas: string[];
  disclaimer: string;
}

export interface RecurringSubscription {
  merchantName: string;
  category: string;
  monthlyAmount: number;
  lastChargedDate: string;
  frequency: string;
  utilityLevel: 'ESSENTIAL' | 'DISCRETIONARY' | 'UNDERUTILIZED';
}

export interface UnusualExpense {
  transactionId: string;
  merchantName: string;
  category: string;
  amount: number;
  date: string;
  categoryAverage: number;
  reason: string;
}

export interface SpendingIncrease {
  category: string;
  currentMonthSpent: number;
  previousMonthSpent: number;
  percentageIncrease: number;
  insightText: string;
}

export interface DuplicateSuspect {
  suspectId: string;
  primaryTransactionId: string;
  duplicateTransactionId: string;
  merchantName: string;
  amount: number;
  primaryDate: string;
  duplicateDate: string;
  status: 'PENDING_CONFIRMATION' | 'CONFIRMED_DUPLICATE' | 'DISMISSED';
  explanation: string;
}

export interface SmartExpenseAnalysis {
  subscriptions: RecurringSubscription[];
  monthlySubscriptionsTotal: number;
  unusualExpenses: UnusualExpense[];
  categoryIncreases: SpendingIncrease[];
  duplicateSuspects: DuplicateSuspect[];
}

export interface WhatIfSimulationRequest {
  additionalMonthlySavings?: number;
  categoryReductionsPercent?: Record<string, number>;
  simulatedLoanAmount?: number;
  simulatedLoanTenureMonths?: number;
  simulatedLoanApr?: number;
  simulatedInsuranceMonthlyPremium?: number;
  changedGoalCategory?: string;
  changedGoalTargetAmount?: number;
}

export interface WhatIfSimulationResponse {
  baselineIncome: number;
  baselineExpenses: number;
  baselineMonthlySurplus: number;
  baselineSavingsRate: number;
  baselineRunwayMonths: number;

  simulatedIncome: number;
  simulatedExpenses: number;
  simulatedMonthlySurplus: number;
  simulatedSavingsRate: number;
  simulatedRunwayMonths: number;

  netMonthlyCashflowDelta: number;
  simulatedLoanEmi: number;
  categorySavingsTotal: number;
  categoryBreakdownSimulated: Record<string, number>;
  impactObservations: string[];
  feasibilityVerdict: 'HIGHLY_FEASIBLE' | 'BALANCED' | 'CASHFLOW_STRAIN';
}

export const fintechApi = {
  getAccounts: async (): Promise<FinancialAccount[]> => {
    try {
      const res = await apiClient.get<FinancialAccount[]>('/fintech/accounts');
      return res.data;
    } catch (err) {
      return mockAccounts;
    }
  },

  getTransactions: async (): Promise<Transaction[]> => {
    try {
      const res = await apiClient.get<any>('/fintech/transactions');
      if (res.data && Array.isArray(res.data.content)) {
        return res.data.content;
      }
      return Array.isArray(res.data) ? res.data : mockTransactions;
    } catch (err) {
      return mockTransactions;
    }
  },

  getBudgets: async (): Promise<Budget[]> => {
    try {
      const res = await apiClient.get<Budget[]>('/fintech/budgets');
      return res.data;
    } catch (err) {
      return mockBudgets;
    }
  },

  createOrUpdateBudget: async (budget: { category: string; monthlyLimit: number; alertThresholdPercent?: number }): Promise<Budget> => {
    const res = await apiClient.post<Budget>('/fintech/budgets', budget);
    return res.data;
  },

  getCashFlowForecast: async (): Promise<CashFlowForecast> => {
    try {
      const res = await apiClient.get<CashFlowForecast>('/fintech/cashflow/forecast');
      return res.data;
    } catch (err) {
      return {
        currentLiquidCash: 345250.00,
        projectedCashIn30Days: 386510.00,
        expectedInflows30Days: 75000.00,
        expectedOutflows30Days: 33740.00,
        burnRateDailyAverage: 1124.67,
        liquidityHealth: 'EXCELLENT',
        recurringBills: [
          { billName: 'Residential Rent & Lease', category: 'HOUSING', amount: 18500.00, dueDayOfMonth: 1, status: 'UPCOMING' },
          { billName: 'Adani Electricity & Municipal Utilities', category: 'UTILITIES', amount: 2800.00, dueDayOfMonth: 5, status: 'UPCOMING' },
          { billName: 'Airtel Xstream Broadband', category: 'UTILITIES', amount: 1499.00, dueDayOfMonth: 10, status: 'UPCOMING' },
          { billName: 'Aegis Family Health Shield', category: 'HEALTHCARE', amount: 3308.00, dueDayOfMonth: 15, status: 'UPCOMING' },
          { billName: 'Apex Premier Personal Loan EMI', category: 'LOAN_PAYMENT', amount: 8540.00, dueDayOfMonth: 20, status: 'UPCOMING' },
        ],
        recurringBillsTotal: 34647.00,
        potentialShortageDetected: false,
        shortageRiskLevel: 'LOW',
        lowestProjectedBalance: 341200.00,
        forecastAssumptions: [
          'Assumes baseline salaried payroll credit arrives reliably on the 1st of the month.',
          'Fixed recurring obligations (Rent, Loan EMIs, Health Insurance) debit automatically on scheduled dates.',
          'Excludes irregular emergency hospitalizations or discretionary capital investments.',
        ],
        uncertaintyNotes: 'Uncertainty bands reflect a ±5.8% variance range modeled from 60-day historical standard deviations.',
        disclaimer: 'Cash flow projections are simulated approximations for liquidity planning and do not guarantee future account balances.',
      };
    }
  },

  createTransaction: async (payload: CreateTransactionPayload): Promise<Transaction> => {
    const res = await apiClient.post<Transaction>('/fintech/transactions', payload);
    return res.data;
  },

  updateTransactionCategory: async (id: string, category: string): Promise<Transaction> => {
    const res = await apiClient.patch<Transaction>(`/fintech/transactions/${id}/category`, { category });
    return res.data;
  },

  autoCategorize: async (merchantName: string, amount?: number): Promise<AutoCategorizeResult> => {
    const res = await apiClient.post<AutoCategorizeResult>('/fintech/transactions/auto-categorize', {
      merchantName,
      amount,
    });
    return res.data;
  },

  importCsv: async (csvContent: string, defaultAccountId?: string): Promise<CsvImportResult> => {
    const res = await apiClient.post<CsvImportResult>('/fintech/transactions/import-csv', {
      csvContent,
      defaultAccountId,
    });
    return res.data;
  },

  getFinancialInsights: async (): Promise<FinancialInsightsResult> => {
    try {
      const res = await apiClient.get<FinancialInsightsResult>('/fintech/insights');
      return res.data;
    } catch (err) {
      return {
        monthlySummary: {
          totalIncome: 75000.00,
          totalExpenses: 33740.00,
          netSavings: 41260.00,
          savingsRate: 55.0,
          previousMonthExpenses: 36500.00,
          expenseChangePercentage: -7.4,
          categoryDistribution: [
            { category: 'HOUSING', amount: 18500.00, percentage: 54.8, transactionCount: 1 },
            { category: 'LOAN_PAYMENT', amount: 8540.00, percentage: 25.3, transactionCount: 1 },
            { category: 'GROCERIES', amount: 4200.00, percentage: 12.4, transactionCount: 4 },
            { category: 'DINING', amount: 2500.00, percentage: 7.5, transactionCount: 5 },
          ],
        },
        insights: [
          {
            type: 'ACHIEVEMENT',
            title: 'Superior Savings Velocity (55.0%)',
            description: 'Your net monthly savings rate of 55.0% is more than double the national recommended 20% benchmark.',
            potentialMonthlySavings: 0,
            impactLevel: 'HIGH',
            actionRecommendation: 'Channel surplus into tax-free ELSS mutual funds and liquid flexi-FD reserves.',
          },
          {
            type: 'OPPORTUNITY',
            title: 'Dining Takeout Optimization',
            description: 'Your Food & Dining expenses increased compared with last month. Trimming takeout frees up ~₹3,500/month.',
            potentialMonthlySavings: 3500.00,
            impactLevel: 'MEDIUM',
            actionRecommendation: 'Set a dining category budget guardrail in the Smart Budgeting tab.',
          },
        ],
        unusualSpikes: [
          { category: 'DINING', amount: 2500.00, percentage: 120.0, transactionCount: 5 },
        ],
        historicalMonthlyTrends: [
          { month: 'Sep 2026', income: 72000, expenses: 38000, savings: 34000, savingsRate: 47.2 },
          { month: 'Oct 2026', income: 75000, expenses: 36500, savings: 38500, savingsRate: 51.3 },
          { month: 'Nov 2026', income: 75000, expenses: 33740, savings: 41260, savingsRate: 55.0 },
        ],
      };
    }
  },

  getSavingsGoals: async (): Promise<SavingsGoal[]> => {
    try {
      const res = await apiClient.get<SavingsGoal[]>('/fintech/savings-goals');
      return res.data;
    } catch (err) {
      return [
        {
          id: 'goal-1',
          title: '3-Month Emergency Reserve Cushion',
          category: 'EMERGENCY_FUND',
          targetAmount: 200000.00,
          currentSavedAmount: 145000.00,
          targetDate: '2027-06-30',
          monthlyRequiredSavings: 6875.00,
          progressPercentage: 72.5,
          status: 'IN_PROGRESS',
          monthsRemaining: 8,
          createdAt: new Date().toISOString(),
          affordabilityStatus: 'AFFORDABLE',
        },
        {
          id: 'goal-2',
          title: 'MacBook Pro M4 Developer Laptop',
          category: 'NEW_LAPTOP',
          targetAmount: 180000.00,
          currentSavedAmount: 90000.00,
          targetDate: '2027-03-31',
          monthlyRequiredSavings: 18000.00,
          progressPercentage: 50.0,
          status: 'IN_PROGRESS',
          monthsRemaining: 5,
          createdAt: new Date().toISOString(),
          affordabilityStatus: 'STRETCH',
        },
      ];
    }
  },

  createSavingsGoal: async (payload: CreateSavingsGoalPayload): Promise<SavingsGoal> => {
    const res = await apiClient.post<SavingsGoal>('/fintech/savings-goals', payload);
    return res.data;
  },

  updateSavingsGoal: async (id: string, currentSavedAmount: number): Promise<SavingsGoal> => {
    const res = await apiClient.patch<SavingsGoal>(`/fintech/savings-goals/${id}`, { currentSavedAmount });
    return res.data;
  },

  updateSavingsGoalProgress: async (id: string, currentSavedAmount: number): Promise<SavingsGoal> => {
    const res = await apiClient.patch<SavingsGoal>(`/fintech/savings-goals/${id}`, { currentSavedAmount });
    return res.data;
  },

  deleteSavingsGoal: async (id: string): Promise<void> => {
    await apiClient.delete(`/fintech/savings-goals/${id}`);
  },

  // Phase 04 Endpoints
  getHealthScore: async (): Promise<FinancialHealthScore> => {
    try {
      const res = await apiClient.get<FinancialHealthScore>('/fintech/health-score');
      return res.data;
    } catch (err) {
      return {
        overallScore: 84,
        healthTier: 'EXCELLENT',
        summaryText: 'Your Financial Health Score is 84/100 (EXCELLENT). You maintain a robust 55.0% savings rate and 10.2 months of liquid emergency runway.',
        scoringFactors: [
          { factorName: 'Savings Rate', scoreAwarded: 25, maxScore: 25, currentMetricValue: '55.0% of take-home', benchmark: 'Target: >= 20.0%', status: 'OPTIMAL', explanation: 'Measures net monthly liquidity retained for long-term wealth acceleration.' },
          { factorName: 'Expense-to-Income Ratio', scoreAwarded: 25, maxScore: 25, currentMetricValue: '45.0% of income', benchmark: 'Target: <= 50.0%', status: 'OPTIMAL', explanation: 'Evaluates proportion of gross inflow consumed by living necessities.' },
          { factorName: 'Debt Obligations (FOIR)', scoreAwarded: 15, maxScore: 20, currentMetricValue: '11.4% of income', benchmark: 'Target: <= 35.0%', status: 'GOOD', explanation: 'Tracks loan EMI debt servicing burden against gross monthly cashflow.' },
          { factorName: 'Budget Adherence', scoreAwarded: 11, maxScore: 15, currentMetricValue: '3 of 4 budgets on track (75%)', benchmark: 'Target: 100% adherence', status: 'GOOD', explanation: 'Measures consistency with designated monthly category spending limits.' },
          { factorName: 'Emergency Runway', scoreAwarded: 15, maxScore: 15, currentMetricValue: '10.2 months buffer', benchmark: 'Target: 3.0 - 6.0 months', status: 'OPTIMAL', explanation: 'Liquid capital cushion available to sustain living expenses without new income.' },
        ],
        improvementAreas: [
          'Cap dining delivery spend by ₹3,500 to expand your monthly surplus even further.',
          'Automate an extra ₹5,000 monthly transfer to long-term mutual fund SIPs on salary credit day.',
          'Set alert thresholds at 80% on Dining to prevent monthly category overspending.',
        ],
        disclaimer: 'This financial health score is an illustrative personal finance evaluation for informational and planning purposes only. It DOES NOT constitute a credit score (e.g., CIBIL/Experian), official underwriting rating, or formal creditworthiness assessment.',
      };
    }
  },

  getSmartExpenses: async (): Promise<SmartExpenseAnalysis> => {
    try {
      const res = await apiClient.get<SmartExpenseAnalysis>('/fintech/smart-expenses');
      return res.data;
    } catch (err) {
      return {
        subscriptions: [
          { merchantName: 'Netflix Premium India', category: 'ENTERTAINMENT', monthlyAmount: 649.00, lastChargedDate: '2026-10-28', frequency: 'MONTHLY', utilityLevel: 'DISCRETIONARY' },
          { merchantName: 'Spotify Duo Subscription', category: 'ENTERTAINMENT', monthlyAmount: 149.00, lastChargedDate: '2026-10-22', frequency: 'MONTHLY', utilityLevel: 'DISCRETIONARY' },
          { merchantName: 'Cult.fit Elite Pass', category: 'HEALTHCARE', monthlyAmount: 1750.00, lastChargedDate: '2026-11-04', frequency: 'MONTHLY', utilityLevel: 'UNDERUTILIZED' },
          { merchantName: 'JioFiber Home Broadband', category: 'UTILITIES', monthlyAmount: 1179.00, lastChargedDate: '2026-10-18', frequency: 'MONTHLY', utilityLevel: 'ESSENTIAL' },
        ],
        monthlySubscriptionsTotal: 3727.00,
        unusualExpenses: [
          { transactionId: 'tx-unusual-1', merchantName: 'Croma Electronics & Appliances', category: 'SHOPPING', amount: 14999.00, date: '2026-11-01', categoryAverage: 3200.00, reason: 'Large consumer electronics purchase deviates from monthly baseline.' },
        ],
        categoryIncreases: [
          { category: 'DINING', currentMonthSpent: 9840.00, previousMonthSpent: 6200.00, percentageIncrease: 58.7, insightText: 'Dining out and delivery increased by 58.7% compared with last month.' },
          { category: 'SHOPPING', currentMonthSpent: 8450.00, previousMonthSpent: 5100.00, percentageIncrease: 65.7, insightText: 'Apparel and lifestyle retail purchases grew significantly this cycle.' },
        ],
        duplicateSuspects: [
          {
            suspectId: 'suspect-1',
            primaryTransactionId: 'tx-dup-1',
            duplicateTransactionId: 'tx-dup-2',
            merchantName: 'Swiggy Dineout Gourmet',
            amount: 1420.00,
            primaryDate: '2026-11-06',
            duplicateDate: '2026-11-06',
            status: 'PENDING_CONFIRMATION',
            explanation: 'Suspected duplicate charge: Two identical charges of ₹1,420.00 debited on the same day. Please confirm or dismiss.',
          },
        ],
      };
    }
  },

  confirmDuplicate: async (duplicateTransactionId: string, confirmed: boolean): Promise<void> => {
    await apiClient.post('/fintech/smart-expenses/confirm-duplicate', {
      duplicateTransactionId,
      confirmed,
    });
  },

  simulateWhatIf: async (request: WhatIfSimulationRequest): Promise<WhatIfSimulationResponse> => {
    try {
      const res = await apiClient.post<WhatIfSimulationResponse>('/fintech/what-if/simulate', request);
      return res.data;
    } catch (err) {
      const baselineIncome = 75000.00;
      const baselineExpenses = 33740.00;
      const baselineSurplus = baselineIncome - baselineExpenses;

      let catSavings = 0;
      if (request.categoryReductionsPercent) {
        if (request.categoryReductionsPercent['DINING']) {
          catSavings += 9840.00 * (request.categoryReductionsPercent['DINING'] / 100);
        }
        if (request.categoryReductionsPercent['SHOPPING']) {
          catSavings += 6500.00 * (request.categoryReductionsPercent['SHOPPING'] / 100);
        }
      }

      let simulatedLoanEmi = 0;
      if (request.simulatedLoanAmount && request.simulatedLoanAmount > 0) {
        const p = request.simulatedLoanAmount;
        const n = request.simulatedLoanTenureMonths || 36;
        const rMonthly = (request.simulatedLoanApr || 8.99) / 12 / 100;
        simulatedLoanEmi = (p * rMonthly * Math.pow(1 + rMonthly, n)) / (Math.pow(1 + rMonthly, n) - 1);
      }

      const insPremium = request.simulatedInsuranceMonthlyPremium || 0;
      const extraSavings = request.additionalMonthlySavings || 0;

      const simExpenses = baselineExpenses - catSavings + simulatedLoanEmi + insPremium;
      const simSurplus = baselineIncome - simExpenses - extraSavings;
      const simSavingsRate = ((simSurplus + extraSavings) / baselineIncome) * 100;
      const simRunway = 345250.00 / simExpenses;

      return {
        baselineIncome,
        baselineExpenses,
        baselineMonthlySurplus: baselineSurplus,
        baselineSavingsRate: 55.0,
        baselineRunwayMonths: 10.2,

        simulatedIncome: baselineIncome,
        simulatedExpenses: Math.round(simExpenses),
        simulatedMonthlySurplus: Math.round(simSurplus),
        simulatedSavingsRate: Math.round(simSavingsRate * 10) / 10,
        simulatedRunwayMonths: Math.round(simRunway * 10) / 10,

        netMonthlyCashflowDelta: Math.round(simSurplus - baselineSurplus),
        simulatedLoanEmi: Math.round(simulatedLoanEmi),
        categorySavingsTotal: Math.round(catSavings),
        categoryBreakdownSimulated: {
          DINING: Math.round(9840 - (request.categoryReductionsPercent?.['DINING'] ? (9840 * request.categoryReductionsPercent['DINING']) / 100 : 0)),
          SHOPPING: Math.round(6500 - (request.categoryReductionsPercent?.['SHOPPING'] ? (6500 * request.categoryReductionsPercent['SHOPPING']) / 100 : 0)),
        },
        impactObservations: [
          `Category trimming unlocks ₹${Math.round(catSavings)}/month in liquid margin.`,
          simulatedLoanEmi > 0 ? `Simulated loan adds ₹${Math.round(simulatedLoanEmi)}/month in fixed EMI.` : 'No loan commitments added.',
          insPremium > 0 ? `Adding ₹${Math.round(insPremium)}/month family health protection preserves long-term security.` : 'No insurance policy adjustment modeled.',
          extraSavings > 0 ? `Automating ₹${Math.round(extraSavings)}/month extra savings accelerates emergency buffer.` : 'No additional voluntary savings added.',
        ],
        feasibilityVerdict: simSurplus >= 20000 ? 'HIGHLY_FEASIBLE' : simSurplus >= 5000 ? 'BALANCED' : 'CASHFLOW_STRAIN',
      };
    }
  },
};
