import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Sparkles,
  ArrowUpRight,
  ArrowDownLeft,
  Shield,
  Landmark,
  Coins,
  Bot,
  Receipt,
  FileUp,
  CheckCircle2,
  AlertTriangle,
  Clock,
  Wallet,
  TrendingUp,
  ChevronRight,
  PlusCircle,
  Sliders,
  Compass,
  ArrowRight,
} from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Button } from '@/shared/components/ui/Button';
import { Badge } from '@/shared/components/ui/Badge';
import { StatCard } from '@/shared/components/ui/StatCard';
import { Modal } from '@/shared/components/ui/Modal';
import { useAuth } from '@/core/auth/AuthContext';
import { formatINR } from '@/core/utils/currency';
import {
  personalizationApi,
  PersonalizationResponse,
} from '@/core/api/personalizationApi';
import {
  mockAccounts,
  mockTransactions,
  mockActiveLoans,
  mockPolicies,
  mockClaims,
  mockInsights,
} from '@/mock/mockData';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  // Personalization State
  const [personalization, setPersonalization] = useState<PersonalizationResponse | null>(null);

  // Quick action modals
  const [showUploadModal, setShowUploadModal] = useState(false);
  const [uploadFile, setUploadFile] = useState<File | null>(null);
  const [uploadStatus, setUploadStatus] = useState<'idle' | 'uploading' | 'success'>('idle');

  useEffect(() => {
    personalizationApi.getPersonalization().then((res) => {
      if (res) setPersonalization(res);
    }).catch(() => {});
  }, []);

  // Aggregated calculations
  const totalAssets = mockAccounts
    .filter((a) => a.currentBalance > 0)
    .reduce((sum, a) => sum + a.currentBalance, 0);

  const totalDebt = Math.abs(
    mockAccounts
      .filter((a) => a.currentBalance < 0)
      .reduce((sum, a) => sum + a.currentBalance, 0)
  ) + mockActiveLoans.reduce((sum, l) => sum + l.remainingBalance, 0);

  const netWorth = totalAssets - totalDebt;
  const liquidSavings = mockAccounts
    .filter((a) => a.accountType === 'CHECKING' || a.accountType === 'SAVINGS')
    .reduce((sum, a) => sum + a.currentBalance, 0);

  const totalMonthlyEmi = mockActiveLoans.reduce((sum, l) => sum + l.monthlyEmi, 0);
  const monthlyIncome = personalization?.profile?.monthlyIncome || 75000.0;
  const monthlyExpenses = personalization?.profile?.monthlyExpenses || 33740.0;
  const monthlySurplus = personalization?.dashboardCustomization?.calculatedMonthlySurplus ??
    (monthlyIncome - monthlyExpenses - totalMonthlyEmi);
  const primaryFocus = personalization?.dashboardCustomization?.primaryFocusArea || 'Health Insurance, Smart Budgeting';

  const handleSimulateUpload = () => {
    if (!uploadFile) return;
    setUploadStatus('uploading');
    setTimeout(() => {
      setUploadStatus('success');
      setTimeout(() => {
        setUploadStatus('idle');
        setUploadFile(null);
        setShowUploadModal(false);
      }, 1500);
    }, 1200);
  };

  return (
    <div className="space-y-6">
      {/* 1. Personalized Welcome Section */}
      <div className="glass-panel rounded-2xl p-6 bg-gradient-to-r from-navy-900 via-navy-850 to-navy-900 border border-electric-500/20 shadow-glow relative overflow-hidden">
        {/* Decorative blur */}
        <div className="absolute right-0 top-0 w-96 h-96 bg-cyan-500/10 rounded-full blur-3xl pointer-events-none" />

        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 relative z-10">
          <div>
            <div className="flex items-center gap-2 mb-1.5 flex-wrap">
              <span className="text-xs font-semibold text-cyan-400 uppercase tracking-wider font-mono">
                {new Date().toLocaleDateString('en-US', {
                  weekday: 'long',
                  month: 'short',
                  day: 'numeric',
                  year: 'numeric',
                })}
              </span>
              <span className="text-slate-600">•</span>
              <Badge variant="cyan" size="sm" className="font-mono text-[11px]">
                Focus: {primaryFocus}
              </Badge>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight flex items-center gap-2">
              {personalization?.contextualWelcomeTitle || `Welcome back, ${user?.firstName}!`}
            </h1>
            <p className="text-xs text-slate-300 mt-1 max-w-xl leading-relaxed">
              {personalization?.contextualWelcomeMessage ||
                'FinPath AI is continuously monitoring your liquidity, debt-to-income stability, and insurance coverage gap ratios.'}
            </p>
          </div>

          {/* Health Score Pill */}
          <div className="flex items-center gap-4 bg-navy-950/60 border border-navy-700/80 px-4 py-3 rounded-xl shrink-0">
            <div>
              <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">
                Financial Health Score
              </p>
              <div className="flex items-baseline gap-1.5 mt-0.5">
                <span className="text-2xl font-black text-cyan-400 font-sans">
                  {user?.financialHealthScore || 84}
                </span>
                <span className="text-xs text-slate-400 font-medium">/ 100</span>
                <Badge variant="cyan" size="sm" className="ml-2 font-mono">
                  EXCELLENT
                </Badge>
              </div>
            </div>
            <div className="w-10 h-10 rounded-full border-2 border-cyan-400/40 border-t-cyan-400 flex items-center justify-center text-cyan-300">
              <TrendingUp className="w-5 h-5" />
            </div>
          </div>
        </div>

        {/* Quick Action Toolbar */}
        <div className="mt-5 pt-4 border-t border-navy-700/50 flex flex-wrap items-center gap-2.5">
          <Button
            variant="electric"
            size="sm"
            leftIcon={<Bot className="w-4 h-4" />}
            onClick={() => navigate('/ai-assistant')}
          >
            Ask Financial Co-Pilot
          </Button>
          <Button
            variant="outline"
            size="sm"
            leftIcon={<Landmark className="w-4 h-4 text-electric-400" />}
            onClick={() => navigate('/lending')}
          >
            Check Loan Eligibility
          </Button>
          <Button
            variant="outline"
            size="sm"
            leftIcon={<Shield className="w-4 h-4 text-cyan-400" />}
            onClick={() => navigate('/insurance')}
          >
            Compare Insurance Quotes
          </Button>
          <Button
            variant="outline"
            size="sm"
            leftIcon={<Sliders className="w-4 h-4 text-cyan-400" />}
            onClick={() => navigate('/onboarding')}
          >
            Personalization Settings
          </Button>
          <Button
            variant="cyan"
            size="sm"
            leftIcon={<FileUp className="w-4 h-4" />}
            onClick={() => setShowUploadModal(true)}
          >
            Upload Document (RAG)
          </Button>
        </div>
      </div>

      {/* 2. Financial Overview Stat Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total Net Worth"
          value={formatINR(netWorth)}
          subtitle="Assets minus total liabilities"
          icon={Wallet}
          accentColor="cyan"
          trend={{ value: 4.8, isPositive: true }}
          onClick={() => navigate('/fintech')}
        />
        <StatCard
          title="Liquid Cash & Savings"
          value={formatINR(liquidSavings)}
          subtitle="Immediate reserve buffer"
          icon={Coins}
          accentColor="electric"
          trend={{ value: 2.1, isPositive: true }}
          onClick={() => navigate('/fintech')}
        />
        <StatCard
          title="Total Active Debt"
          value={formatINR(totalDebt)}
          subtitle="Car loan + LOC + credit cards"
          icon={Landmark}
          accentColor="amber"
          trend={{ value: 3.4, isPositive: false, label: 'principal paid down' }}
          onClick={() => navigate('/lending')}
        />
        <StatCard
          title="Monthly Surplus"
          value={formatINR(monthlySurplus)}
          subtitle="Income after debt & expenses"
          icon={TrendingUp}
          accentColor="emerald"
          trend={{ value: 6.2, isPositive: true }}
          onClick={() => navigate('/fintech')}
        />
      </div>

      {/* 2.2 Key Personalization Ratios */}
      {personalization && personalization.dashboardCustomization && (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 animate-fade-in">
          <div className="p-4 rounded-xl bg-navy-900 border border-navy-700/80 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-400">
                <Landmark className="w-5 h-5" />
              </div>
              <div>
                <p className="text-[10px] uppercase font-bold text-slate-400 font-mono">Debt-to-Income (FOIR)</p>
                <p className="text-base font-bold text-white font-mono mt-0.5">
                  {personalization.dashboardCustomization.debtToIncomeRatio}%
                  <span className="text-xs text-slate-400 font-normal ml-1.5 font-sans">
                    {personalization.dashboardCustomization.debtToIncomeRatio <= 45 ? '(Safe Threshold)' : '(High Obligation)'}
                  </span>
                </p>
              </div>
            </div>
            <Badge variant={personalization.dashboardCustomization.debtToIncomeRatio <= 45 ? 'success' : 'danger'} size="sm">
              {personalization.dashboardCustomization.debtToIncomeRatio <= 45 ? 'HEALTHY' : 'ATTENTION'}
            </Badge>
          </div>

          <div className="p-4 rounded-xl bg-navy-900 border border-navy-700/80 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-emerald-400">
                <Shield className="w-5 h-5" />
              </div>
              <div>
                <p className="text-[10px] uppercase font-bold text-slate-400 font-mono">Emergency Reserve Buffer</p>
                <p className="text-base font-bold text-white font-mono mt-0.5">
                  {personalization.dashboardCustomization.monthsOfEmergencyRunway} Months
                  <span className="text-xs text-slate-400 font-normal ml-1.5 font-sans">
                    (Target: {formatINR(personalization.dashboardCustomization.recommendedEmergencyFund)})
                  </span>
                </p>
              </div>
            </div>
            <Badge variant="cyan" size="sm">
              LIQUID
            </Badge>
          </div>
        </div>
      )}

      {/* 2.5 Personalized Suggested Journeys (From Smart Onboarding) */}
      {personalization && personalization.suggestedJourneys && personalization.suggestedJourneys.length > 0 && (
        <div className="space-y-3 animate-fade-in">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-2">
              <Compass className="w-4 h-4 text-cyan-400" />
              <h2 className="text-base font-bold text-white tracking-tight">
                Recommended Financial Journeys
              </h2>
            </div>
            <span className="text-xs text-slate-400">Tailored to your selected goals & surplus</span>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {personalization.suggestedJourneys.map((j) => (
              <Card
                key={j.journeyId}
                className="p-5 bg-navy-900 border border-navy-700/80 hover:border-cyan-500/40 transition-all flex flex-col justify-between group shadow-glow-sm"
              >
                <div>
                  <div className="flex items-center justify-between gap-2 mb-2.5">
                    <Badge variant="cyan" size="sm" className="font-mono text-[10px]">
                      {j.category}
                    </Badge>
                    <span className="text-[10px] text-slate-400 flex items-center gap-1 font-mono">
                      <Clock className="w-3 h-3 text-cyan-400" /> {j.estimatedMinutes} min
                    </span>
                  </div>

                  <h3 className="text-sm font-bold text-white group-hover:text-cyan-300 transition-colors">
                    {j.title}
                  </h3>
                  <p className="text-xs text-slate-400 mt-1.5 leading-relaxed">
                    {j.description}
                  </p>
                </div>

                <div className="mt-4 pt-3 border-t border-navy-800 flex items-center justify-between">
                  <Badge variant="outline" size="sm" className="text-emerald-400 border-emerald-500/30 text-[10px]">
                    {j.impactBadge}
                  </Badge>

                  <Button
                    variant="electric"
                    size="sm"
                    className="text-xs"
                    rightIcon={<ArrowRight className="w-3 h-3" />}
                    onClick={() => navigate(j.route)}
                  >
                    {j.actionLabel}
                  </Button>
                </div>
              </Card>
            ))}
          </div>
        </div>
      )}

      {/* 3. Proactive AI Financial Insights Section */}
      <div className="space-y-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Sparkles className="w-4 h-4 text-cyan-400" />
            <h2 className="text-base font-bold text-white tracking-tight">
              AI Cross-Domain Insights & Actions
            </h2>
          </div>
          <span className="text-xs text-slate-400">Continuous background evaluation</span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {mockInsights.map((insight) => (
            <Card
              key={insight.id}
              className="p-4 bg-navy-900/90 border border-navy-700/80 hover:border-cyan-500/30 flex flex-col justify-between"
            >
              <div>
                <div className="flex items-start justify-between gap-2 mb-2">
                  <Badge
                    variant={
                      insight.urgency === 'HIGH'
                        ? 'danger'
                        : insight.urgency === 'MEDIUM'
                        ? 'warning'
                        : 'cyan'
                    }
                    size="sm"
                  >
                    {insight.urgency} PRIORITY
                  </Badge>
                  {insight.impactAmount && (
                    <span className="text-xs font-mono font-bold text-emerald-400">
                      +{formatINR(insight.impactAmount)}/mo
                    </span>
                  )}
                </div>

                <h4 className="text-xs font-bold text-white mb-1.5">{insight.title}</h4>
                <p className="text-[11px] text-slate-400 leading-relaxed mb-4">
                  {insight.description}
                </p>
              </div>

              <div className="pt-3 border-t border-navy-800 flex items-center justify-between">
                <Button
                  variant="ghost"
                  size="sm"
                  className="p-0 text-cyan-400 hover:text-cyan-300 text-xs font-semibold gap-1"
                  onClick={() => navigate(insight.actionRoute)}
                >
                  <span>{insight.actionText}</span>
                  <ChevronRight className="w-3.5 h-3.5" />
                </Button>
              </div>
            </Card>
          ))}
        </div>
      </div>

      {/* 4. Domain Breakdowns: Lending & Insurance Overviews */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Lending Snapshot Card */}
        <Card className="p-5 bg-navy-900 border border-navy-700/80">
          <CardHeader className="p-0 pb-4 flex flex-row items-center justify-between">
            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-lg bg-electric-600/10 border border-electric-500/20 flex items-center justify-center text-electric-400">
                <Landmark className="w-4 h-4" />
              </div>
              <div>
                <CardTitle>Lending & Debt Overview</CardTitle>
                <p className="text-[11px] text-slate-400">
                  {mockActiveLoans.length} active facilities | DTI: 24.2% (Healthy)
                </p>
              </div>
            </div>
            <Button
              variant="outline"
              size="sm"
              onClick={() => navigate('/lending')}
              className="text-xs"
            >
              Loan Hub
            </Button>
          </CardHeader>

          <CardContent className="p-0 space-y-3">
            {mockActiveLoans.map((loan) => (
              <div
                key={loan.id}
                className="p-3.5 rounded-xl bg-navy-950/70 border border-navy-800 flex items-center justify-between"
              >
                <div>
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-semibold text-white">{loan.productName}</span>
                    <Badge variant="cyan" size="sm">
                      {loan.interestRateApr}% APR
                    </Badge>
                  </div>
                  <p className="text-[11px] text-slate-400 mt-1 font-mono">
                    Balance: {formatINR(loan.remainingBalance)} of {formatINR(loan.totalPrincipal)}
                  </p>
                </div>

                <div className="text-right">
                  <p className="text-xs font-bold text-white font-mono">
                    {formatINR(loan.monthlyEmi)}/mo
                  </p>
                  <span className="text-[10px] text-slate-500 flex items-center gap-1 justify-end mt-0.5">
                    <Clock className="w-3 h-3 text-cyan-400" />
                    Due {loan.nextPaymentDate}
                  </span>
                </div>
              </div>
            ))}

            <div className="p-3 rounded-xl bg-electric-600/10 border border-electric-500/20 flex items-center justify-between">
              <span className="text-xs text-electric-300 font-medium">
                Instant Borrowing Pre-Qualification Available
              </span>
              <Button
                variant="cyan"
                size="sm"
                onClick={() => navigate('/lending')}
                className="text-xs py-1 h-7"
              >
                Pre-Qualify
              </Button>
            </div>
          </CardContent>
        </Card>

        {/* Insurance Snapshot Card */}
        <Card className="p-5 bg-navy-900 border border-navy-700/80">
          <CardHeader className="p-0 pb-4 flex flex-row items-center justify-between">
            <div className="flex items-center gap-2.5">
              <div className="w-8 h-8 rounded-lg bg-cyan-600/10 border border-cyan-500/20 flex items-center justify-center text-cyan-400">
                <Shield className="w-4 h-4" />
              </div>
              <div>
                <CardTitle>Insurance Protection Overview</CardTitle>
                <p className="text-[11px] text-slate-400">
                  {mockPolicies.length} bound policies | {mockClaims.length} active claims
                </p>
              </div>
            </div>
            <Button
              variant="outline"
              size="sm"
              onClick={() => navigate('/insurance')}
              className="text-xs"
            >
              Policies Hub
            </Button>
          </CardHeader>

          <CardContent className="p-0 space-y-3">
            {mockPolicies.map((pol) => (
              <div
                key={pol.id}
                className="p-3.5 rounded-xl bg-navy-950/70 border border-navy-800 flex items-center justify-between"
              >
                <div>
                  <div className="flex items-center gap-2">
                    <span className="text-xs font-semibold text-white">{pol.productName}</span>
                    <Badge
                      variant={pol.status === 'ACTIVE' ? 'success' : 'warning'}
                      size="sm"
                    >
                      {pol.status.replace('_', ' ')}
                    </Badge>
                  </div>
                  <p className="text-[11px] text-slate-400 mt-1">
                    Carrier: {pol.carrierName} • Coverage: {formatINR(pol.coverageAmount)}
                  </p>
                </div>

                <div className="text-right">
                  <p className="text-xs font-bold text-white font-mono">
                    {formatINR(pol.monthlyPremium)}/mo
                  </p>
                  <span className="text-[10px] text-slate-500 block mt-0.5">
                    Renews {pol.renewalDate}
                  </span>
                </div>
              </div>
            ))}

            <div className="p-3 rounded-xl bg-cyan-500/10 border border-cyan-500/20 flex items-center justify-between">
              <span className="text-xs text-cyan-300 font-medium">
                Claim in review: CLM-2026-9021 ({formatINR(4200)})
              </span>
              <Button
                variant="outline"
                size="sm"
                onClick={() => navigate('/insurance')}
                className="text-xs py-1 h-7 border-cyan-500/30"
              >
                Track Claim
              </Button>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* 5. Income, Expenses & Recent Transactions */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Income vs Expenses Card */}
        <Card className="p-5 bg-navy-900 border border-navy-700/80">
          <CardHeader className="p-0 pb-4">
            <CardTitle>Cash Flow Allocation</CardTitle>
            <p className="text-[11px] text-slate-400">Monthly budget consumption</p>
          </CardHeader>
          <CardContent className="p-0 space-y-4">
            <div>
              <div className="flex justify-between text-xs mb-1">
                <span className="text-slate-400">Monthly Inflow (Salary)</span>
                <span className="text-emerald-400 font-mono font-bold">
                  +{formatINR(monthlyIncome)}
                </span>
              </div>
              <div className="w-full bg-navy-950 rounded-full h-2">
                <div className="bg-emerald-500 h-2 rounded-full w-full" />
              </div>
            </div>

            <div>
              <div className="flex justify-between text-xs mb-1">
                <span className="text-slate-400">Living Expenses & Bills</span>
                <span className="text-slate-200 font-mono font-bold">
                  {formatINR(monthlyExpenses)}
                </span>
              </div>
              <div className="w-full bg-navy-950 rounded-full h-2">
                <div
                  className="bg-electric-500 h-2 rounded-full"
                  style={{ width: `${(monthlyExpenses / monthlyIncome) * 100}%` }}
                />
              </div>
            </div>

            <div>
              <div className="flex justify-between text-xs mb-1">
                <span className="text-slate-400">Debt & EMI Servicing</span>
                <span className="text-amber-400 font-mono font-bold">
                  {formatINR(totalMonthlyEmi)}
                </span>
              </div>
              <div className="w-full bg-navy-950 rounded-full h-2">
                <div
                  className="bg-amber-500 h-2 rounded-full"
                  style={{ width: `${(totalMonthlyEmi / monthlyIncome) * 100}%` }}
                />
              </div>
            </div>

            <div className="pt-3 border-t border-navy-800 flex items-center justify-between text-xs">
              <span className="text-slate-300 font-medium">Net Savings Rate</span>
              <span className="text-cyan-400 font-mono font-bold text-sm">
                {(((monthlyIncome - monthlyExpenses - totalMonthlyEmi) / monthlyIncome) * 100).toFixed(1)}%
              </span>
            </div>
          </CardContent>
        </Card>

        {/* Recent Transactions Table */}
        <Card className="lg:col-span-2 p-5 bg-navy-900 border border-navy-700/80">
          <CardHeader className="p-0 pb-4 flex flex-row items-center justify-between">
            <div>
              <CardTitle>Recent Activity & Transactions</CardTitle>
              <p className="text-[11px] text-slate-400">Aggregated across all linked accounts</p>
            </div>
            <Button
              variant="outline"
              size="sm"
              onClick={() => navigate('/transactions')}
              className="text-xs"
            >
              View All ({mockTransactions.length})
            </Button>
          </CardHeader>

          <CardContent className="p-0">
            <div className="divide-y divide-navy-800">
              {mockTransactions.slice(0, 5).map((tx) => (
                <div key={tx.id} className="py-2.5 flex items-center justify-between text-xs">
                  <div className="flex items-center gap-3">
                    <div
                      className={`w-7 h-7 rounded-lg flex items-center justify-center ${
                        tx.amount > 0
                          ? 'bg-emerald-500/10 text-emerald-400'
                          : 'bg-navy-800 text-slate-400'
                      }`}
                    >
                      {tx.amount > 0 ? (
                        <ArrowDownLeft className="w-3.5 h-3.5" />
                      ) : (
                        <ArrowUpRight className="w-3.5 h-3.5" />
                      )}
                    </div>
                    <div>
                      <p className="font-semibold text-slate-200">{tx.merchantName}</p>
                      <p className="text-[10px] text-slate-500">
                        {tx.accountName} • {tx.date}
                      </p>
                    </div>
                  </div>

                  <div className="text-right">
                    <p
                      className={`font-mono font-bold ${
                        tx.amount > 0 ? 'text-emerald-400' : 'text-slate-200'
                      }`}
                    >
                      {tx.amount > 0 ? '+' : '-'}{formatINR(Math.abs(tx.amount))}
                    </p>
                    <Badge variant={tx.status === 'POSTED' ? 'default' : 'warning'} size="sm">
                      {tx.category}
                    </Badge>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* RAG Document Upload Modal */}
      <Modal
        isOpen={showUploadModal}
        onClose={() => setShowUploadModal(false)}
        title="Upload Financial Document for AI RAG Processing"
        description="Supported formats: PDF, TIFF (Max 20MB). Automatically chunked and indexed into pgvector."
      >
        <div className="space-y-4">
          <div className="border-2 border-dashed border-navy-700 hover:border-cyan-500/50 rounded-xl p-8 text-center bg-navy-950/60 transition-colors">
            <FileUp className="w-10 h-10 text-cyan-400 mx-auto mb-3" />
            <p className="text-xs font-semibold text-white mb-1">
              Drag & Drop your policy, payslip, or bank statement
            </p>
            <p className="text-[11px] text-slate-400 mb-4">
              Our Apache Tika parser extracts clauses, exclusions, and financial numbers with PII redaction.
            </p>
            <label className="cursor-pointer">
              <span className="px-3.5 py-1.5 rounded-lg bg-electric-600 hover:bg-electric-500 text-white text-xs font-medium shadow-glow-sm">
                Choose Local PDF
              </span>
              <input
                type="file"
                accept=".pdf,.png,.jpg"
                className="hidden"
                onChange={(e) => {
                  if (e.target.files?.[0]) setUploadFile(e.target.files[0]);
                }}
              />
            </label>
          </div>

          {uploadFile && (
            <div className="p-3 rounded-xl bg-navy-800/80 border border-navy-700 flex items-center justify-between text-xs">
              <div className="flex items-center gap-2">
                <FileUp className="w-4 h-4 text-cyan-400" />
                <span className="font-semibold text-slate-200 truncate max-w-[200px]">
                  {uploadFile.name}
                </span>
                <span className="text-[10px] text-slate-500 font-mono">
                  ({(uploadFile.size / 1024 / 1024).toFixed(2)} MB)
                </span>
              </div>
              <Badge variant="cyan" size="sm">Ready to Index</Badge>
            </div>
          )}

          {uploadStatus === 'success' && (
            <div className="p-3 rounded-xl bg-emerald-500/15 border border-emerald-500/30 text-emerald-300 text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4" />
              <span>Document indexed successfully! 14 vector chunks stored in pgvector.</span>
            </div>
          )}

          <div className="flex justify-end gap-2 pt-2">
            <Button variant="ghost" size="sm" onClick={() => setShowUploadModal(false)}>
              Cancel
            </Button>
            <Button
              variant="electric"
              size="sm"
              disabled={!uploadFile}
              isLoading={uploadStatus === 'uploading'}
              onClick={handleSimulateUpload}
            >
              Parse & Index Vectors
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

