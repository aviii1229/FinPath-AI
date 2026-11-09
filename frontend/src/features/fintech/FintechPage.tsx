import React, { useState, useEffect } from 'react';
import {
  Coins,
  PlusCircle,
  TrendingUp,
  RefreshCw,
  Wallet,
  ArrowUpRight,
  ArrowDownLeft,
  CheckCircle2,
  Calendar,
  Sparkles,
  Receipt,
  PieChart as PieChartIcon,
  PiggyBank,
  AlertTriangle,
  Upload,
  Search,
  Filter,
  Check,
  Edit2,
  Trash2,
  DollarSign,
  Scale,
  ArrowRight,
  Activity,
  Sliders,
} from 'lucide-react';
import { HealthScoreView } from './components/HealthScoreView';
import { CashflowForecastView } from './components/CashflowForecastView';
import { SmartExpensesView } from './components/SmartExpensesView';
import { WhatIfSimulatorView } from './components/WhatIfSimulatorView';
import {
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  AreaChart,
  Area,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
} from 'recharts';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Button } from '@/shared/components/ui/Button';
import { Badge } from '@/shared/components/ui/Badge';
import { Modal } from '@/shared/components/ui/Modal';
import { Input } from '@/shared/components/ui/Input';
import { mockAccounts, mockTransactions, mockBudgets } from '@/mock/mockData';
import {
  fintechApi,
  CashFlowForecast,
  FinancialInsightsResult,
  SavingsGoal,
  AutoCategorizeResult,
  FinancialHealthScore,
  SmartExpenseAnalysis,
} from '@/core/api/fintechApi';
import { FinancialAccount, Transaction, Budget, TransactionCategory } from '@/types';
import { formatINR, formatCompactINR } from '@/core/utils/currency';

type FintechTab = 'dashboard' | 'health-score' | 'cashflow' | 'smart-expenses' | 'what-if' | 'transactions' | 'budgets' | 'savings' | 'insights';

const CATEGORY_COLORS: Record<string, string> = {
  HOUSING: '#3b82f6',
  GROCERIES: '#10b981',
  LOAN_PAYMENT: '#f59e0b',
  HEALTHCARE: '#ef4444',
  ENTERTAINMENT: '#8b5cf6',
  DINING: '#ec4899',
  TRANSPORTATION: '#06b6d4',
  UTILITIES: '#6366f1',
  SHOPPING: '#14b8a6',
  SALARY: '#22c55e',
  INVESTMENTS: '#a855f7',
  MISCELLANEOUS: '#64748b',
};

export const FintechPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<FintechTab>('dashboard');
  const [accounts, setAccounts] = useState<FinancialAccount[]>(mockAccounts);
  const [transactions, setTransactions] = useState<Transaction[]>(mockTransactions);
  const [budgets, setBudgets] = useState<Budget[]>(mockBudgets);
  const [savingsGoals, setSavingsGoals] = useState<SavingsGoal[]>([]);
  const [insightsResult, setInsightsResult] = useState<FinancialInsightsResult | null>(null);
  const [forecast, setForecast] = useState<CashFlowForecast | null>(null);
  const [healthScore, setHealthScore] = useState<FinancialHealthScore | null>(null);
  const [smartExpenses, setSmartExpenses] = useState<SmartExpenseAnalysis | null>(null);
  const [isLoadingHealthScore, setIsLoadingHealthScore] = useState(false);
  const [isLoadingSmartExpenses, setIsLoadingSmartExpenses] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isSyncing, setIsSyncing] = useState(false);

  // ----------------------------------------------------
  // TRANSACTION MANAGEMENT & AI AUTO-CATEGORIZATION STATE
  // ----------------------------------------------------
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');
  const [showAddTxModal, setShowAddTxModal] = useState(false);
  const [txMerchant, setTxMerchant] = useState('');
  const [txAmount, setTxAmount] = useState('');
  const [txCategory, setTxCategory] = useState<TransactionCategory>('GROCERIES');
  const [txIsIncome, setTxIsIncome] = useState(false);
  const [txAccountId, setTxAccountId] = useState(mockAccounts[0]?.id || '');
  const [autoCatSuggestion, setAutoCatSuggestion] = useState<AutoCategorizeResult | null>(null);
  const [isSubmittingTx, setIsSubmittingTx] = useState(false);

  // Edit category modal
  const [editTx, setEditTx] = useState<Transaction | null>(null);
  const [newCategoryVal, setNewCategoryVal] = useState<TransactionCategory>('GROCERIES');

  // CSV Import State
  const [showCsvModal, setShowCsvModal] = useState(false);
  const [csvContent, setCsvContent] = useState('');
  const [isImportingCsv, setIsImportingCsv] = useState(false);
  const [csvFeedback, setCsvFeedback] = useState<string | null>(null);

  // ----------------------------------------------------
  // SMART BUDGETING STATE
  // ----------------------------------------------------
  const [showBudgetModal, setShowBudgetModal] = useState(false);
  const [budgetCategory, setBudgetCategory] = useState<TransactionCategory>('DINING');
  const [budgetLimit, setBudgetLimit] = useState('500');
  const [budgetAlertThreshold, setBudgetAlertThreshold] = useState('85');
  const [isSavingBudget, setIsSavingBudget] = useState(false);

  // ----------------------------------------------------
  // SAVINGS PLANNER STATE
  // ----------------------------------------------------
  const [showSavingsModal, setShowSavingsModal] = useState(false);
  const [goalTitle, setGoalTitle] = useState('');
  const [goalCategory, setGoalCategory] = useState('EMERGENCY_FUND');
  const [goalTargetAmount, setGoalTargetAmount] = useState('10000');
  const [goalSavedAmount, setGoalSavedAmount] = useState('2500');
  const [goalTargetDate, setGoalTargetDate] = useState('2027-12-31');
  const [isSavingGoal, setIsSavingGoal] = useState(false);

  // Deposit progress modal
  const [depositGoal, setDepositGoal] = useState<SavingsGoal | null>(null);
  const [depositAmount, setDepositAmount] = useState('');

  // Initial Data Load
  useEffect(() => {
    loadFintechData();
  }, []);

  const loadFintechData = async () => {
    setIsLoading(true);
    try {
      const [accs, txs, bdgs, ins, goals, fc, hs, se] = await Promise.all([
        fintechApi.getAccounts(),
        fintechApi.getTransactions(),
        fintechApi.getBudgets(),
        fintechApi.getFinancialInsights(),
        fintechApi.getSavingsGoals(),
        fintechApi.getCashFlowForecast(),
        fintechApi.getHealthScore(),
        fintechApi.getSmartExpenses(),
      ]);
      if (accs && accs.length > 0) setAccounts(accs);
      if (txs && txs.length > 0) setTransactions(txs);
      if (bdgs && bdgs.length > 0) setBudgets(bdgs);
      if (ins) setInsightsResult(ins);
      if (goals && goals.length > 0) setSavingsGoals(goals);
      if (fc) setForecast(fc);
      if (hs) setHealthScore(hs);
      if (se) setSmartExpenses(se);
    } catch (err) {
      console.warn('Using mock data for Fintech initial load', err);
    } finally {
      setIsLoading(false);
    }
  };

  const loadHealthScore = async () => {
    setIsLoadingHealthScore(true);
    try {
      const hs = await fintechApi.getHealthScore();
      if (hs) setHealthScore(hs);
    } catch (e) {
      console.warn('Could not reload health score', e);
    } finally {
      setIsLoadingHealthScore(false);
    }
  };

  const loadSmartExpenses = async () => {
    setIsLoadingSmartExpenses(true);
    try {
      const se = await fintechApi.getSmartExpenses();
      if (se) setSmartExpenses(se);
    } catch (e) {
      console.warn('Could not reload smart expenses', e);
    } finally {
      setIsLoadingSmartExpenses(false);
    }
  };

  // Real-time AI categorization check when merchant name changes
  const handleMerchantChange = async (name: string) => {
    setTxMerchant(name);
    if (name.trim().length >= 3) {
      try {
        const catRes = await fintechApi.autoCategorize(name, parseFloat(txAmount) || undefined);
        setAutoCatSuggestion(catRes);
        if (catRes.category && catRes.category !== 'MISCELLANEOUS') {
          setTxCategory(catRes.category as TransactionCategory);
        }
      } catch (err) {
        // Fallback local heuristic
        const lower = name.toLowerCase();
        if (lower.includes('food')) {
          setTxCategory('GROCERIES');
        }
      }
    } else {
      setAutoCatSuggestion(null);
    }
  };

  // Add Manual Transaction
  const handleCreateTransaction = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!txMerchant || !txAmount) return;
    setIsSubmittingTx(true);
    try {
      const numAmount = parseFloat(txAmount);
      const finalAmount = txIsIncome ? Math.abs(numAmount) : -Math.abs(numAmount);
      const accId = txAccountId || (accounts[0] ? accounts[0].id : 'acc-1');

      const created = await fintechApi.createTransaction({
        accountId: accId,
        amount: finalAmount,
        transactionDate: new Date().toISOString().split('T')[0],
        merchantName: txMerchant,
        category: txCategory,
        isRecurring: false,
      });

      setTransactions([created, ...transactions]);
      setShowAddTxModal(false);
      setTxMerchant('');
      setTxAmount('');
      setAutoCatSuggestion(null);
      // Reload insights to refresh calculations
      loadFintechData();
    } catch (err) {
      console.error('Failed to create transaction', err);
    } finally {
      setIsSubmittingTx(false);
    }
  };

  // Update Category
  const handleUpdateCategory = async () => {
    if (!editTx) return;
    try {
      const updated = await fintechApi.updateTransactionCategory(editTx.id, newCategoryVal);
      setTransactions(transactions.map((t) => (t.id === editTx.id ? { ...t, category: updated.category } : t)));
      setEditTx(null);
      loadFintechData();
    } catch (err) {
      // Local fallback
      setTransactions(transactions.map((t) => (t.id === editTx.id ? { ...t, category: newCategoryVal } : t)));
      setEditTx(null);
    }
  };

  // Handle CSV Import
  const handleImportCsv = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!csvContent.trim()) return;
    setIsImportingCsv(true);
    setCsvFeedback(null);
    try {
      const result = await fintechApi.importCsv(csvContent);
      setCsvFeedback(`Successfully imported ${result.importedCount} transactions. Skipped: ${result.skippedCount}.`);
      if (result.importedTransactions && result.importedTransactions.length > 0) {
        setTransactions([...result.importedTransactions, ...transactions]);
      }
      setTimeout(() => {
        setShowCsvModal(false);
        setCsvContent('');
        setCsvFeedback(null);
        loadFintechData();
      }, 1500);
    } catch (err) {
      setCsvFeedback('Failed to parse CSV. Please check formatting.');
    } finally {
      setIsImportingCsv(false);
    }
  };

  // Create / Update Budget
  const handleSaveBudget = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSavingBudget(true);
    try {
      const updated = await fintechApi.createOrUpdateBudget({
        category: budgetCategory,
        monthlyLimit: parseFloat(budgetLimit) || 500,
        alertThresholdPercent: parseInt(budgetAlertThreshold) || 85,
      });

      const existingIndex = budgets.findIndex((b) => b.category === budgetCategory);
      if (existingIndex >= 0) {
        const next = [...budgets];
        next[existingIndex] = updated;
        setBudgets(next);
      } else {
        setBudgets([...budgets, updated]);
      }
      setShowBudgetModal(false);
      loadFintechData();
    } catch (err) {
      console.error('Failed to save budget', err);
    } finally {
      setIsSavingBudget(false);
    }
  };

  // Create Savings Goal
  const handleCreateSavingsGoal = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSavingGoal(true);
    try {
      const created = await fintechApi.createSavingsGoal({
        title: goalTitle,
        category: goalCategory,
        targetAmount: parseFloat(goalTargetAmount) || 10000,
        currentSavedAmount: parseFloat(goalSavedAmount) || 0,
        targetDate: goalTargetDate,
      });
      setSavingsGoals([created, ...savingsGoals]);
      setShowSavingsModal(false);
      setGoalTitle('');
    } catch (err) {
      console.error('Failed to create savings goal', err);
    } finally {
      setIsSavingGoal(false);
    }
  };

  // Quick Deposit to Savings Goal
  const handleDepositToGoal = async () => {
    if (!depositGoal || !depositAmount) return;
    try {
      const additional = parseFloat(depositAmount) || 0;
      const newTotal = depositGoal.currentSavedAmount + additional;
      const updated = await fintechApi.updateSavingsGoal(depositGoal.id, newTotal);
      setSavingsGoals(savingsGoals.map((g) => (g.id === depositGoal.id ? updated : g)));
      setDepositGoal(null);
      setDepositAmount('');
    } catch (err) {
      console.error('Deposit error', err);
    }
  };

  // Delete Savings Goal
  const handleDeleteGoal = async (id: string) => {
    try {
      await fintechApi.deleteSavingsGoal(id);
      setSavingsGoals(savingsGoals.filter((g) => g.id !== id));
    } catch (err) {
      console.error('Failed to delete goal', err);
    }
  };

  // Filtered Transactions
  const filteredTransactions = transactions.filter((tx) => {
    const matchesSearch =
      tx.merchantName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      tx.accountName.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = selectedCategory === 'ALL' || tx.category === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <Coins className="w-6 h-6 text-cyan-400" />
            <h1 className="text-2xl font-bold text-white tracking-tight">Fintech & Wealth AI (PFM)</h1>
            <Badge variant="cyan" size="sm">
              PHASE 6 ENGINE
            </Badge>
          </div>
          <p className="text-xs text-slate-400">
            Intelligent transaction management, AI expense categorization, smart budget utilization, savings planning, and visual trends.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => setShowCsvModal(true)}
            leftIcon={<Upload className="w-3.5 h-3.5" />}
          >
            Import CSV
          </Button>
          <Button
            variant="electric"
            size="sm"
            onClick={() => setShowAddTxModal(true)}
            leftIcon={<PlusCircle className="w-4 h-4" />}
          >
            Record Transaction
          </Button>
        </div>
      </div>

      {/* Tabs Navigation */}
      <div className="flex border-b border-navy-800 space-x-1 sm:space-x-4 overflow-x-auto pb-1 text-xs sm:text-sm">
        <button
          onClick={() => setActiveTab('dashboard')}
          className={`flex items-center gap-2 px-3 py-2 font-medium border-b-2 transition-colors whitespace-nowrap ${
            activeTab === 'dashboard'
              ? 'border-cyan-400 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <PieChartIcon className="w-4 h-4" />
          Financial Dashboard & Trends
        </button>

        <button
          onClick={() => setActiveTab('health-score')}
          className={`flex items-center gap-2 px-3 py-2 font-medium border-b-2 transition-colors whitespace-nowrap ${
            activeTab === 'health-score'
              ? 'border-cyan-400 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <Activity className="w-4 h-4" />
          Financial Health Score
        </button>

        <button
          onClick={() => setActiveTab('cashflow')}
          className={`flex items-center gap-2 px-3 py-2 font-medium border-b-2 transition-colors whitespace-nowrap ${
            activeTab === 'cashflow'
              ? 'border-cyan-400 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <TrendingUp className="w-4 h-4" />
          Cash Flow Forecast
        </button>

        <button
          onClick={() => setActiveTab('smart-expenses')}
          className={`flex items-center gap-2 px-3 py-2 font-medium border-b-2 transition-colors whitespace-nowrap ${
            activeTab === 'smart-expenses'
              ? 'border-cyan-400 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <Sparkles className="w-4 h-4" />
          Smart Expense Detection
        </button>

        <button
          onClick={() => setActiveTab('what-if')}
          className={`flex items-center gap-2 px-3 py-2 font-medium border-b-2 transition-colors whitespace-nowrap ${
            activeTab === 'what-if'
              ? 'border-cyan-400 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <Sliders className="w-4 h-4" />
          What-If Simulator
        </button>

        <button
          onClick={() => setActiveTab('transactions')}
          className={`flex items-center gap-2 px-3 py-2 font-medium border-b-2 transition-colors whitespace-nowrap ${
            activeTab === 'transactions'
              ? 'border-cyan-400 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <Receipt className="w-4 h-4" />
          Transactions & AI Categorization
        </button>

        <button
          onClick={() => setActiveTab('budgets')}
          className={`flex items-center gap-2 px-3 py-2 font-medium border-b-2 transition-colors whitespace-nowrap ${
            activeTab === 'budgets'
              ? 'border-cyan-400 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <Scale className="w-4 h-4" />
          Smart Budgeting
        </button>

        <button
          onClick={() => setActiveTab('savings')}
          className={`flex items-center gap-2 px-3 py-2 font-medium border-b-2 transition-colors whitespace-nowrap ${
            activeTab === 'savings'
              ? 'border-cyan-400 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <PiggyBank className="w-4 h-4" />
          Savings Goals Planner
        </button>

        <button
          onClick={() => setActiveTab('insights')}
          className={`flex items-center gap-2 px-3 py-2 font-medium border-b-2 transition-colors whitespace-nowrap ${
            activeTab === 'insights'
              ? 'border-cyan-400 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <Sparkles className="w-4 h-4" />
          AI Financial Insights & Anomalies
        </button>
      </div>

      {/* ========================================================================= */}
      {/* TAB 1: FINANCIAL DASHBOARD & RECHARTS VISUALIZATIONS */}
      {/* ========================================================================= */}
      {activeTab === 'dashboard' && (
        <div className="space-y-6">
          {/* Key Financial KPIs */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            <Card className="p-4 bg-navy-900 border border-navy-800">
              <div className="flex justify-between items-start">
                <span className="text-[10px] uppercase font-bold text-slate-400">Total Monthly Income</span>
                <ArrowDownLeft className="w-4 h-4 text-emerald-400" />
              </div>
              <p className="text-2xl font-bold text-emerald-400 font-mono mt-1">
                {formatINR(insightsResult?.monthlySummary.totalIncome || 75000)}
              </p>
              <span className="text-[10px] text-slate-400">Payroll Direct Deposits</span>
            </Card>

            <Card className="p-4 bg-navy-900 border border-navy-800">
              <div className="flex justify-between items-start">
                <span className="text-[10px] uppercase font-bold text-slate-400">Total Monthly Expenses</span>
                <ArrowUpRight className="w-4 h-4 text-rose-400" />
              </div>
              <p className="text-2xl font-bold text-slate-100 font-mono mt-1">
                {formatINR(insightsResult?.monthlySummary.totalExpenses || 33740)}
              </p>
              <span className="text-[10px] text-emerald-400 font-mono">
                {insightsResult?.monthlySummary.expenseChangePercentage || -7.4}% vs last month
              </span>
            </Card>

            <Card className="p-4 bg-navy-900 border border-navy-800">
              <div className="flex justify-between items-start">
                <span className="text-[10px] uppercase font-bold text-slate-400">Net Discretionary Savings</span>
                <PiggyBank className="w-4 h-4 text-cyan-400" />
              </div>
              <p className="text-2xl font-bold text-cyan-300 font-mono mt-1">
                {formatINR(insightsResult?.monthlySummary.netSavings || 41260)}
              </p>
              <span className="text-[10px] text-slate-400">Discretionary wealth velocity</span>
            </Card>

            <Card className="p-4 bg-navy-900 border border-navy-800">
              <div className="flex justify-between items-start">
                <span className="text-[10px] uppercase font-bold text-slate-400">Savings Rate</span>
                <Badge variant="cyan" size="sm">
                  TIER 1
                </Badge>
              </div>
              <p className="text-2xl font-bold text-electric-400 font-mono mt-1">
                {insightsResult?.monthlySummary.savingsRate || 50.1}%
              </p>
              <span className="text-[10px] text-slate-400">Exceeds 20% national benchmark</span>
            </Card>
          </div>

          {/* Recharts Visualizations Grid */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Spending Distribution Pie Chart */}
            <Card className="p-6 bg-navy-900 border border-navy-700/80">
              <CardHeader className="p-0 pb-4">
                <CardTitle className="text-sm flex items-center justify-between">
                  <span className="flex items-center gap-2">
                    <PieChartIcon className="w-4 h-4 text-cyan-400" />
                    Category Spending Distribution
                  </span>
                  <span className="text-[10px] text-slate-400 font-mono">CURRENT BILLING CYCLE</span>
                </CardTitle>
              </CardHeader>

              <div className="h-64 w-full">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={insightsResult?.monthlySummary.categoryDistribution || []}
                      dataKey="amount"
                      nameKey="category"
                      cx="50%"
                      cy="50%"
                      innerRadius={55}
                      outerRadius={85}
                      paddingAngle={4}
                    >
                      {(insightsResult?.monthlySummary.categoryDistribution || []).map((entry, index) => (
                        <Cell
                          key={`cell-${index}`}
                          fill={CATEGORY_COLORS[entry.category] || '#64748b'}
                        />
                      ))}
                    </Pie>
                    <Tooltip
                      content={({ active, payload }) => {
                        if (active && payload && payload.length) {
                          const data = payload[0].payload;
                          return (
                            <div className="bg-navy-950 p-2.5 rounded-lg border border-navy-700 text-xs shadow-xl font-mono">
                              <p className="font-bold text-white">{data.category}</p>
                              <p className="text-cyan-300 font-bold">{formatINR(data.amount)}</p>
                              <p className="text-slate-400 text-[10px]">{data.percentage}% of total spend</p>
                            </div>
                          );
                        }
                        return null;
                      }}
                    />
                    <Legend
                      formatter={(val) => <span className="text-[10px] text-slate-300">{val}</span>}
                      wrapperStyle={{ fontSize: '10px' }}
                    />
                  </PieChart>
                </ResponsiveContainer>
              </div>
            </Card>

            {/* Monthly Trend Area Chart */}
            <Card className="p-6 bg-navy-900 border border-navy-700/80">
              <CardHeader className="p-0 pb-4">
                <CardTitle className="text-sm flex items-center justify-between">
                  <span className="flex items-center gap-2">
                    <TrendingUp className="w-4 h-4 text-emerald-400" />
                    Monthly Income, Expenses & Net Savings Trends
                  </span>
                  <span className="text-[10px] text-slate-400 font-mono">HISTORICAL MODEL</span>
                </CardTitle>
              </CardHeader>

              <div className="h-64 w-full">
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart data={insightsResult?.historicalMonthlyTrends || []}>
                    <defs>
                      <linearGradient id="incomeGrad" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#22c55e" stopOpacity={0.4} />
                        <stop offset="95%" stopColor="#22c55e" stopOpacity={0.0} />
                      </linearGradient>
                      <linearGradient id="expenseGrad" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#ef4444" stopOpacity={0.4} />
                        <stop offset="95%" stopColor="#ef4444" stopOpacity={0.0} />
                      </linearGradient>
                    </defs>
                    <XAxis
                      dataKey="month"
                      stroke="#64748b"
                      fontSize={10}
                      tickLine={false}
                    />
                    <YAxis
                      stroke="#64748b"
                      fontSize={10}
                      tickLine={false}
                      tickFormatter={(val) => formatCompactINR(val)}
                    />
                    <Tooltip
                      content={({ active, payload }) => {
                        if (active && payload && payload.length) {
                          const data = payload[0].payload;
                          return (
                            <div className="bg-navy-950 p-2.5 rounded-lg border border-navy-700 text-xs shadow-xl font-mono space-y-1">
                              <p className="font-bold text-white">{data.month}</p>
                              <p className="text-emerald-400">Income: {formatINR(data.income)}</p>
                              <p className="text-rose-400">Expenses: {formatINR(data.expenses)}</p>
                              <p className="text-cyan-300">Savings: {formatINR(data.savings)} ({data.savingsRate}%)</p>
                            </div>
                          );
                        }
                        return null;
                      }}
                    />
                    <Area
                      type="monotone"
                      dataKey="income"
                      stroke="#22c55e"
                      fillOpacity={1}
                      fill="url(#incomeGrad)"
                      strokeWidth={2}
                    />
                    <Area
                      type="monotone"
                      dataKey="expenses"
                      stroke="#ef4444"
                      fillOpacity={1}
                      fill="url(#expenseGrad)"
                      strokeWidth={2}
                    />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            </Card>
          </div>

          {/* 30-Day Forward Cashflow Forecast & Accounts */}
          <div className="space-y-3">
            <h3 className="text-sm font-bold text-white uppercase text-slate-400 tracking-wider">
              Connected Multi-Institution Accounts ({accounts.length})
            </h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              {accounts.map((acc) => (
                <Card key={acc.id} className="p-4 bg-navy-900 border border-navy-700/80">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-[10px] font-bold text-cyan-400 uppercase tracking-wider">
                      {acc.accountType}
                    </span>
                    <span className="text-[10px] text-slate-500 font-mono">{acc.accountMask}</span>
                  </div>
                  <h4 className="text-sm font-bold text-white truncate">{acc.institutionName}</h4>
                  <p
                    className={`text-xl font-extrabold font-mono tracking-tight mt-2 ${
                      acc.currentBalance < 0 ? 'text-amber-400' : 'text-slate-100'
                    }`}
                  >
                    {formatINR(Math.abs(acc.currentBalance))}
                  </p>
                  <p className="text-[10px] text-slate-400 mt-0.5">
                    Available: {formatINR(acc.availableBalance)}
                  </p>
                </Card>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 2: TRANSACTIONS MANAGEMENT & AI CATEGORIZATION */}
      {/* ========================================================================= */}
      {activeTab === 'transactions' && (
        <div className="space-y-6">
          {/* Search, Filter & Actions Toolbar */}
          <Card className="p-4 bg-navy-900 border border-navy-700/80">
            <div className="flex flex-col md:flex-row items-center gap-4 justify-between">
              <div className="relative w-full md:w-96">
                <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none" />
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  placeholder="Search merchant or account..."
                  className="w-full bg-navy-950 border border-navy-700 text-xs pl-9 pr-4 py-2 rounded-xl text-slate-200 placeholder-slate-500 focus:outline-none focus:border-cyan-400"
                />
              </div>

              <div className="flex items-center gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => setShowCsvModal(true)}
                  leftIcon={<Upload className="w-3.5 h-3.5" />}
                >
                  Import CSV
                </Button>
                <Button
                  variant="electric"
                  size="sm"
                  onClick={() => setShowAddTxModal(true)}
                  leftIcon={<PlusCircle className="w-4 h-4" />}
                >
                  Add Transaction
                </Button>
              </div>
            </div>

            {/* Category Filter Pills */}
            <div className="flex items-center gap-1.5 overflow-x-auto pt-3 border-t border-navy-800 mt-3 pb-1">
              {[
                'ALL',
                'HOUSING',
                'GROCERIES',
                'DINING',
                'TRANSPORTATION',
                'ENTERTAINMENT',
                'HEALTHCARE',
                'UTILITIES',
                'SHOPPING',
                'SALARY',
                'LOAN_PAYMENT',
              ].map((cat) => (
                <button
                  key={cat}
                  onClick={() => setSelectedCategory(cat)}
                  className={`px-3 py-1 rounded-lg text-[11px] font-semibold whitespace-nowrap transition-all ${
                    selectedCategory === cat
                      ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40 shadow-glow-sm'
                      : 'bg-navy-950 text-slate-400 border border-navy-800 hover:text-slate-200'
                  }`}
                >
                  {cat.replace('_', ' ')}
                </button>
              ))}
            </div>
          </Card>

          {/* Transactions Table */}
          <div className="overflow-x-auto rounded-xl border border-navy-800 bg-navy-900">
            <table className="w-full text-left text-xs">
              <thead className="bg-navy-950 text-slate-400 text-[10px] uppercase font-mono tracking-wider">
                <tr>
                  <th className="p-3.5">Date</th>
                  <th className="p-3.5">Merchant & Description</th>
                  <th className="p-3.5">Account</th>
                  <th className="p-3.5">AI Classification</th>
                  <th className="p-3.5 text-right">Amount (₹)</th>
                  <th className="p-3.5 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-navy-800 text-slate-200">
                {filteredTransactions.map((tx) => (
                  <tr key={tx.id} className="hover:bg-navy-800/40">
                    <td className="p-3.5 text-slate-400 font-mono text-[11px]">{tx.date}</td>
                    <td className="p-3.5">
                      <div className="font-bold text-white">{tx.merchantName}</div>
                      {tx.isRecurring && (
                        <span className="text-[10px] text-cyan-400 flex items-center gap-1 mt-0.5">
                          <RefreshCw className="w-2.5 h-2.5" /> Recurring
                        </span>
                      )}
                    </td>
                    <td className="p-3.5 text-slate-400 text-[11px]">{tx.accountName}</td>
                    <td className="p-3.5">
                      <button
                        onClick={() => {
                          setEditTx(tx);
                          setNewCategoryVal(tx.category as TransactionCategory);
                        }}
                        className="flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-navy-950 border border-navy-700 hover:border-cyan-400 text-[10px] font-mono text-cyan-300"
                      >
                        <span
                          className="w-2 h-2 rounded-full"
                          style={{ backgroundColor: CATEGORY_COLORS[tx.category] || '#64748b' }}
                        />
                        {tx.category}
                        <Edit2 className="w-2.5 h-2.5 opacity-60 ml-1" />
                      </button>
                    </td>
                    <td className="p-3.5 text-right font-mono font-bold text-sm">
                      <span className={tx.amount > 0 ? 'text-emerald-400' : 'text-slate-100'}>
                        {tx.amount > 0 ? '+' : ''}{formatINR(Math.abs(tx.amount))}
                      </span>
                    </td>
                    <td className="p-3.5 text-right">
                      <Button
                        variant="ghost"
                        size="sm"
                        className="text-[11px] h-7 px-2"
                        onClick={() => {
                          setEditTx(tx);
                          setNewCategoryVal(tx.category as TransactionCategory);
                        }}
                      >
                        Edit Category
                      </Button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 3: SMART BUDGETING */}
      {/* ========================================================================= */}
      {activeTab === 'budgets' && (
        <div className="space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <h2 className="text-base font-bold text-white flex items-center gap-2">
                <Scale className="w-5 h-5 text-cyan-400" />
                Category Budget Limits & Utilization
              </h2>
              <p className="text-xs text-slate-400">
                Deterministic monthly spending guardrails with automated overspending threshold alerts.
              </p>
            </div>

            <Button
              variant="electric"
              size="sm"
              onClick={() => setShowBudgetModal(true)}
              leftIcon={<PlusCircle className="w-4 h-4" />}
            >
              Set Category Budget
            </Button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            {budgets.map((b) => {
              const utilPercent = b.monthlyLimit > 0 ? Math.round((b.currentSpent / b.monthlyLimit) * 100) : 0;
              const isOverspent = utilPercent >= 100;
              const isWarning = utilPercent >= b.alertThresholdPercent && !isOverspent;

              return (
                <Card key={b.id} className="p-5 bg-navy-900 border border-navy-700/80">
                  <div className="flex items-center justify-between mb-2">
                    <div className="flex items-center gap-2">
                      <span
                        className="w-3 h-3 rounded-full"
                        style={{ backgroundColor: CATEGORY_COLORS[b.category] || '#3b82f6' }}
                      />
                      <h3 className="text-sm font-bold text-white">{b.category.replace('_', ' ')}</h3>
                    </div>

                    <Badge
                      variant={isOverspent ? 'danger' : isWarning ? 'warning' : 'success'}
                      size="sm"
                    >
                      {isOverspent ? 'EXCEEDED' : isWarning ? 'WARNING' : 'HEALTHY'}
                    </Badge>
                  </div>

                  <div className="flex justify-between items-baseline mb-2">
                    <span className="text-xl font-bold font-mono text-white">
                      {formatINR(b.currentSpent)}{' '}
                      <span className="text-xs text-slate-400 font-sans">
                        / {formatINR(b.monthlyLimit)} limit
                      </span>
                    </span>
                    <span
                      className={`text-xs font-mono font-bold ${
                        isOverspent ? 'text-rose-400' : isWarning ? 'text-amber-400' : 'text-emerald-400'
                      }`}
                    >
                      {utilPercent}% Consumed
                    </span>
                  </div>

                  {/* Progress bar */}
                  <div className="w-full bg-navy-950 rounded-full h-2.5 mb-3 overflow-hidden">
                    <div
                      className={`h-2.5 rounded-full transition-all duration-300 ${
                        isOverspent
                          ? 'bg-rose-500'
                          : isWarning
                          ? 'bg-amber-400'
                          : 'bg-gradient-to-r from-electric-500 to-cyan-400'
                      }`}
                      style={{ width: `${Math.min(100, utilPercent)}%` }}
                    />
                  </div>

                  <div className="flex justify-between text-[11px] text-slate-400 pt-2 border-t border-navy-800">
                    <span>Alert Trigger: {b.alertThresholdPercent}% threshold</span>
                    <span className="font-mono">
                      {formatINR(Math.max(0, b.monthlyLimit - b.currentSpent))} remaining
                    </span>
                  </div>
                </Card>
              );
            })}
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 4: SAVINGS GOALS PLANNER */}
      {/* ========================================================================= */}
      {activeTab === 'savings' && (
        <div className="space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <h2 className="text-base font-bold text-white flex items-center gap-2">
                <PiggyBank className="w-5 h-5 text-cyan-400" />
                Algorithmic Savings Goals Planner
              </h2>
              <p className="text-xs text-slate-400">
                Define milestones, compute deterministic monthly required contributions, and accelerate progress.
              </p>
            </div>

            <Button
              variant="electric"
              size="sm"
              onClick={() => setShowSavingsModal(true)}
              leftIcon={<PlusCircle className="w-4 h-4" />}
            >
              New Savings Goal
            </Button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            {savingsGoals.map((goal) => (
              <Card key={goal.id} className="p-5 bg-navy-900 border border-navy-700/80 flex flex-col justify-between">
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <Badge variant="cyan" size="sm">
                      {goal.category.replace('_', ' ')}
                    </Badge>
                    <span className="text-[10px] text-slate-400 font-mono">
                      Target: {goal.targetDate} ({goal.monthsRemaining} mo left)
                    </span>
                  </div>

                  <h3 className="text-base font-bold text-white mb-1">{goal.title}</h3>
                  <div className="flex justify-between items-baseline mb-3">
                    <span className="text-xl font-bold font-mono text-cyan-300">
                      {formatINR(goal.currentSavedAmount)}{' '}
                      <span className="text-xs text-slate-400 font-sans">
                        of {formatINR(goal.targetAmount)}
                      </span>
                    </span>
                    <span className="text-xs font-mono font-bold text-emerald-400">
                      {goal.progressPercentage}%
                    </span>
                  </div>

                  {/* Progress bar */}
                  <div className="w-full bg-navy-950 rounded-full h-2.5 mb-4 overflow-hidden">
                    <div
                      className="h-2.5 rounded-full bg-gradient-to-r from-electric-600 via-cyan-400 to-emerald-400"
                      style={{ width: `${Math.min(100, goal.progressPercentage)}%` }}
                    />
                  </div>

                  <div className="p-3 bg-navy-950 rounded-lg border border-navy-800 text-xs flex justify-between items-center mb-4">
                    <div>
                      <span className="text-[10px] uppercase font-bold text-slate-400 block">
                        Required Monthly Allocation
                      </span>
                      <span className="text-sm font-bold text-white font-mono">
                        {formatINR(goal.monthlyRequiredSavings)} / mo
                      </span>
                    </div>
                    <span className="text-[11px] text-cyan-400 font-mono">
                      {formatINR(goal.targetAmount - goal.currentSavedAmount)} to go
                    </span>
                  </div>
                </div>

                <div className="flex items-center gap-2 pt-3 border-t border-navy-800">
                  <Button
                    variant="electric"
                    size="sm"
                    className="w-full text-xs"
                    onClick={() => {
                      setDepositGoal(goal);
                      setDepositAmount('');
                    }}
                  >
                    Add Deposit Funds
                  </Button>
                  <Button
                    variant="ghost"
                    size="sm"
                    className="text-xs text-rose-400 hover:text-rose-300"
                    onClick={() => handleDeleteGoal(goal.id)}
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </Button>
                </div>
              </Card>
            ))}
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 5: AI FINANCIAL INSIGHTS & ANOMALIES */}
      {/* ========================================================================= */}
      {activeTab === 'insights' && (
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-base font-bold text-white flex items-center gap-2">
                <Sparkles className="w-5 h-5 text-cyan-400" />
                AI Autonomous Financial Expenditure Insights
              </h2>
              <p className="text-xs text-slate-400">
                Continuous machine learning analysis detecting spending anomalies, recurring leakage, and savings optimization opportunities.
              </p>
            </div>
            <Badge variant="cyan" size="sm">
              REAL-TIME AUDIT
            </Badge>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            {insightsResult?.insights.map((ins, i) => (
              <Card
                key={i}
                className={`p-5 border ${
                  ins.type === 'ACHIEVEMENT'
                    ? 'bg-emerald-500/10 border-emerald-500/30'
                    : ins.type === 'WARNING'
                    ? 'bg-amber-500/10 border-amber-500/30'
                    : ins.type === 'ANOMALY'
                    ? 'bg-rose-500/10 border-rose-500/30'
                    : 'bg-navy-900 border-cyan-500/30'
                }`}
              >
                <div className="flex items-center justify-between mb-2">
                  <Badge
                    variant={
                      ins.type === 'ACHIEVEMENT'
                        ? 'success'
                        : ins.type === 'WARNING'
                        ? 'warning'
                        : ins.type === 'ANOMALY'
                        ? 'danger'
                        : 'cyan'
                    }
                    size="sm"
                  >
                    {ins.type}
                  </Badge>

                  {ins.potentialMonthlySavings > 0 && (
                    <span className="text-xs font-mono font-bold text-emerald-400">
                      Save +{formatINR(ins.potentialMonthlySavings)}/mo
                    </span>
                  )}
                </div>

                <h3 className="text-sm font-bold text-white mb-2">{ins.title}</h3>
                <p className="text-xs text-slate-300 leading-relaxed mb-4">{ins.description}</p>

                <div className="p-3 bg-navy-950/80 rounded-lg border border-navy-800 text-xs flex items-start gap-2">
                  <ArrowRight className="w-3.5 h-3.5 text-cyan-400 mt-0.5 shrink-0" />
                  <span className="text-slate-200">{ins.actionRecommendation}</span>
                </div>
              </Card>
            ))}
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB: FINANCIAL HEALTH SCORE (PHASE 04) */}
      {/* ========================================================================= */}
      {activeTab === 'health-score' && (
        <HealthScoreView
          healthScore={healthScore}
          isLoading={isLoadingHealthScore}
          onRefresh={loadHealthScore}
        />
      )}

      {/* ========================================================================= */}
      {/* TAB: CASH FLOW FORECAST (PHASE 04) */}
      {/* ========================================================================= */}
      {activeTab === 'cashflow' && (
        <CashflowForecastView
          forecast={forecast}
          isLoading={isLoading}
        />
      )}

      {/* ========================================================================= */}
      {/* TAB: SMART EXPENSE DETECTION (PHASE 04) */}
      {/* ========================================================================= */}
      {activeTab === 'smart-expenses' && (
        <SmartExpensesView
          analysis={smartExpenses}
          isLoading={isLoadingSmartExpenses}
          onRefresh={loadSmartExpenses}
        />
      )}

      {/* ========================================================================= */}
      {/* TAB: WHAT-IF SIMULATOR (PHASE 04) */}
      {/* ========================================================================= */}
      {activeTab === 'what-if' && (
        <WhatIfSimulatorView />
      )}

      {/* ========================================================================= */}
      {/* MODAL 1: ADD TRANSACTION WITH REAL-TIME AI CATEGORIZATION */}
      {/* ========================================================================= */}
      <Modal
        isOpen={showAddTxModal}
        onClose={() => setShowAddTxModal(false)}
        title="Record Financial Transaction"
        description="Manual entry with real-time AI merchant recognition and category classification."
      >
        <form onSubmit={handleCreateTransaction} className="space-y-4">
          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">
              Select Financial Account
            </label>
            <select
              value={txAccountId}
              onChange={(e) => setTxAccountId(e.target.value)}
              className="w-full bg-navy-950 border border-navy-700 rounded-lg px-3 py-2 text-xs text-white"
            >
              {accounts.map((a) => (
                <option key={a.id} value={a.id}>
                  {a.institutionName} ({a.accountMask})
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">
              Merchant / Counterparty Name
            </label>
            <input
              type="text"
              value={txMerchant}
              onChange={(e) => handleMerchantChange(e.target.value)}
              placeholder="e.g. Whole Foods, Netflix, Uber, Shell..."
              className="w-full bg-navy-950 border border-navy-700 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-cyan-400"
              required
            />
          </div>

          {/* AI Categorization Preview Card */}
          {autoCatSuggestion && (
            <div className="p-3 rounded-lg bg-cyan-500/10 border border-cyan-500/30 text-xs animate-fade-in flex items-center justify-between">
              <div>
                <span className="text-[10px] text-slate-400 block uppercase font-mono">
                  AI Classified Category
                </span>
                <span className="font-bold text-cyan-300">{autoCatSuggestion.category}</span>
                <span className="text-[10px] text-slate-400 block mt-0.5">
                  Clean Name: {autoCatSuggestion.cleanMerchantName}
                </span>
              </div>
              <Badge variant="cyan" size="sm">
                {Math.round(autoCatSuggestion.confidenceScore * 100)}% Match
              </Badge>
            </div>
          )}

          <div className="grid grid-cols-2 gap-3">
            <Input
              label="Amount (₹)"
              type="number"
              step="0.01"
              value={txAmount}
              onChange={(e) => setTxAmount(e.target.value)}
              placeholder="0.00"
              required
            />

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Transaction Type
              </label>
              <div className="flex gap-2">
                <button
                  type="button"
                  onClick={() => setTxIsIncome(false)}
                  className={`flex-1 py-2 text-xs font-semibold rounded-lg border transition-colors ${
                    !txIsIncome
                      ? 'bg-rose-500/20 text-rose-300 border-rose-500/40'
                      : 'bg-navy-950 text-slate-400 border-navy-800'
                  }`}
                >
                  Expense
                </button>
                <button
                  type="button"
                  onClick={() => setTxIsIncome(true)}
                  className={`flex-1 py-2 text-xs font-semibold rounded-lg border transition-colors ${
                    txIsIncome
                      ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40'
                      : 'bg-navy-950 text-slate-400 border-navy-800'
                  }`}
                >
                  Income
                </button>
              </div>
            </div>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">
              Category Classification
            </label>
            <select
              value={txCategory}
              onChange={(e) => setTxCategory(e.target.value as TransactionCategory)}
              className="w-full bg-navy-950 border border-navy-700 rounded-lg px-3 py-2 text-xs text-white"
            >
              {[
                'HOUSING',
                'GROCERIES',
                'DINING',
                'TRANSPORTATION',
                'ENTERTAINMENT',
                'HEALTHCARE',
                'UTILITIES',
                'SHOPPING',
                'SALARY',
                'LOAN_PAYMENT',
                'INVESTMENTS',
                'MISCELLANEOUS',
              ].map((c) => (
                <option key={c} value={c}>
                  {c.replace('_', ' ')}
                </option>
              ))}
            </select>
          </div>

          <div className="flex justify-end gap-2 pt-2">
            <Button variant="ghost" size="sm" type="button" onClick={() => setShowAddTxModal(false)}>
              Cancel
            </Button>
            <Button variant="electric" size="sm" type="submit" isLoading={isSubmittingTx}>
              Record Transaction
            </Button>
          </div>
        </form>
      </Modal>

      {/* ========================================================================= */}
      {/* MODAL 2: EDIT CATEGORY CLASSIFICATION */}
      {/* ========================================================================= */}
      {editTx && (
        <Modal
          isOpen={Boolean(editTx)}
          onClose={() => setEditTx(null)}
          title="Correct Category Classification"
          description={`Update AI-assigned category for "${editTx.merchantName}"`}
        >
          <div className="space-y-4">
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">
                Select Correct Category
              </label>
              <select
                value={newCategoryVal}
                onChange={(e) => setNewCategoryVal(e.target.value as TransactionCategory)}
                className="w-full bg-navy-950 border border-navy-700 rounded-lg px-3 py-2 text-xs text-white"
              >
                {[
                  'HOUSING',
                  'GROCERIES',
                  'DINING',
                  'TRANSPORTATION',
                  'ENTERTAINMENT',
                  'HEALTHCARE',
                  'UTILITIES',
                  'SHOPPING',
                  'SALARY',
                  'LOAN_PAYMENT',
                  'INVESTMENTS',
                  'MISCELLANEOUS',
                ].map((c) => (
                  <option key={c} value={c}>
                    {c.replace('_', ' ')}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button variant="ghost" size="sm" onClick={() => setEditTx(null)}>
                Cancel
              </Button>
              <Button variant="electric" size="sm" onClick={handleUpdateCategory}>
                Save Correction
              </Button>
            </div>
          </div>
        </Modal>
      )}

      {/* ========================================================================= */}
      {/* MODAL 3: CSV IMPORT */}
      {/* ========================================================================= */}
      <Modal
        isOpen={showCsvModal}
        onClose={() => setShowCsvModal(false)}
        title="Import Transactions via CSV"
        description="Upload or paste comma-separated transactions. Format: Date,Merchant,Amount,Category"
      >
        <form onSubmit={handleImportCsv} className="space-y-4">
          <div className="flex justify-between items-center text-xs">
            <span className="text-slate-300">CSV Data</span>
            <button
              type="button"
              onClick={() => {
                setCsvContent(
                  'Date,Merchant,Amount,Category\n' +
                    '2026-11-04,"Trader Joe\'s Supermarket",-85.20,GROCERIES\n' +
                    '2026-11-05,"Chevron Gas Station",-52.00,TRANSPORTATION\n' +
                    '2026-11-06,"Spotify Premium",-11.99,ENTERTAINMENT\n' +
                    '2026-11-07,"Freelance Engineering Client",+1200.00,SALARY\n'
                );
              }}
              className="text-[11px] text-cyan-400 hover:underline"
            >
              Load Sample CSV
            </button>
          </div>

          <textarea
            value={csvContent}
            onChange={(e) => setCsvContent(e.target.value)}
            rows={6}
            placeholder="Date,Merchant,Amount,Category&#10;2026-11-01,Whole Foods,-65.40,GROCERIES"
            className="w-full bg-navy-950 border border-navy-700 rounded-lg p-3 text-xs text-white font-mono focus:outline-none focus:border-cyan-400"
            required
          />

          {csvFeedback && (
            <p className="text-xs font-mono text-cyan-300 bg-navy-950 p-2.5 rounded-lg border border-navy-800">
              {csvFeedback}
            </p>
          )}

          <div className="flex justify-end gap-2 pt-2">
            <Button variant="ghost" size="sm" type="button" onClick={() => setShowCsvModal(false)}>
              Cancel
            </Button>
            <Button variant="electric" size="sm" type="submit" isLoading={isImportingCsv}>
              Validate & Import
            </Button>
          </div>
        </form>
      </Modal>

      {/* ========================================================================= */}
      {/* MODAL 4: SET CATEGORY BUDGET */}
      {/* ========================================================================= */}
      <Modal
        isOpen={showBudgetModal}
        onClose={() => setShowBudgetModal(false)}
        title="Set Category Monthly Budget"
        description="Configure spending limit and warning threshold for automated overspending tracking."
      >
        <form onSubmit={handleSaveBudget} className="space-y-4">
          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">
              Spending Category
            </label>
            <select
              value={budgetCategory}
              onChange={(e) => setBudgetCategory(e.target.value as TransactionCategory)}
              className="w-full bg-navy-950 border border-navy-700 rounded-lg px-3 py-2 text-xs text-white"
            >
              {[
                'HOUSING',
                'GROCERIES',
                'DINING',
                'TRANSPORTATION',
                'ENTERTAINMENT',
                'HEALTHCARE',
                'UTILITIES',
                'SHOPPING',
              ].map((c) => (
                <option key={c} value={c}>
                  {c.replace('_', ' ')}
                </option>
              ))}
            </select>
          </div>

          <Input
            label="Monthly Limit (₹)"
            type="number"
            value={budgetLimit}
            onChange={(e) => setBudgetLimit(e.target.value)}
            required
          />

          <Input
            label="Alert Threshold (%)"
            type="number"
            value={budgetAlertThreshold}
            onChange={(e) => setBudgetAlertThreshold(e.target.value)}
            helperText="Receive warnings when consumption exceeds this percentage (e.g. 85%)"
            required
          />

          <div className="flex justify-end gap-2 pt-2">
            <Button variant="ghost" size="sm" type="button" onClick={() => setShowBudgetModal(false)}>
              Cancel
            </Button>
            <Button variant="electric" size="sm" type="submit" isLoading={isSavingBudget}>
              Save Budget Limit
            </Button>
          </div>
        </form>
      </Modal>

      {/* ========================================================================= */}
      {/* MODAL 5: NEW SAVINGS GOAL */}
      {/* ========================================================================= */}
      <Modal
        isOpen={showSavingsModal}
        onClose={() => setShowSavingsModal(false)}
        title="Create New Savings Goal"
        description="Set a target amount and date. Algorithmic engine will compute your required monthly savings."
      >
        <form onSubmit={handleCreateSavingsGoal} className="space-y-4">
          <Input
            label="Goal Title"
            type="text"
            value={goalTitle}
            onChange={(e) => setGoalTitle(e.target.value)}
            placeholder="e.g. Emergency Fund, Down Payment..."
            required
          />

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Goal Category</label>
            <select
              value={goalCategory}
              onChange={(e) => setGoalCategory(e.target.value)}
              className="w-full bg-navy-950 border border-navy-700 rounded-lg px-3 py-2 text-xs text-white"
            >
              <option value="EMERGENCY_FUND">Emergency Fund (Liquid Buffer)</option>
              <option value="NEW_LAPTOP">New Laptop & Workstation</option>
              <option value="EDUCATION">Higher Education & Upskilling</option>
              <option value="TRAVEL">Vacation & Travel</option>
              <option value="VEHICLE">Electric Vehicle Purchase</option>
              <option value="LONG_TERM_SAVINGS">Long-term Retirement & Wealth</option>
              <option value="HOUSING">Home Purchase / Down Payment</option>
            </select>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <Input
              label="Target Amount (₹)"
              type="number"
              value={goalTargetAmount}
              onChange={(e) => setGoalTargetAmount(e.target.value)}
              required
            />
            <Input
              label="Currently Saved (₹)"
              type="number"
              value={goalSavedAmount}
              onChange={(e) => setGoalSavedAmount(e.target.value)}
            />
          </div>

          <Input
            label="Target Completion Date"
            type="date"
            value={goalTargetDate}
            onChange={(e) => setGoalTargetDate(e.target.value)}
            required
          />

          <div className="flex justify-end gap-2 pt-2">
            <Button variant="ghost" size="sm" type="button" onClick={() => setShowSavingsModal(false)}>
              Cancel
            </Button>
            <Button variant="electric" size="sm" type="submit" isLoading={isSavingGoal}>
              Create Savings Goal
            </Button>
          </div>
        </form>
      </Modal>

      {/* ========================================================================= */}
      {/* MODAL 6: DEPOSIT FUNDS TO GOAL */}
      {/* ========================================================================= */}
      {depositGoal && (
        <Modal
          isOpen={Boolean(depositGoal)}
          onClose={() => setDepositGoal(null)}
          title={`Add Deposit to "${depositGoal.title}"`}
          description={`Current progress: ${formatINR(depositGoal.currentSavedAmount)} of ${formatINR(depositGoal.targetAmount)}`}
        >
          <div className="space-y-4">
            <Input
              label="Deposit Amount (₹)"
              type="number"
              value={depositAmount}
              onChange={(e) => setDepositAmount(e.target.value)}
              placeholder="e.g. 5000"
              required
            />

            <div className="flex justify-end gap-2 pt-2">
              <Button variant="ghost" size="sm" onClick={() => setDepositGoal(null)}>
                Cancel
              </Button>
              <Button variant="electric" size="sm" onClick={handleDepositToGoal}>
                Record Deposit
              </Button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
};
