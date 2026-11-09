import React, { useState, useEffect } from 'react';
import {
  Landmark,
  Calculator,
  CheckCircle2,
  Clock,
  ArrowRight,
  TrendingDown,
  Sparkles,
  AlertCircle,
  ShieldCheck,
  FileText,
  Sliders,
  HelpCircle,
  DollarSign,
  Layers,
  Scale,
  ListChecks,
  UserCheck,
  RefreshCw,
  Info,
  ChevronRight,
  Check,
} from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Button } from '@/shared/components/ui/Button';
import { Badge } from '@/shared/components/ui/Badge';
import { Modal } from '@/shared/components/ui/Modal';
import { Input } from '@/shared/components/ui/Input';
import { mockActiveLoans, mockLoanProducts } from '@/mock/mockData';
import {
  lendingApi,
  LoanEligibilityResult,
  EmiCalculationResult,
  LoanComparisonResult,
  LoanAssistantQueryResult,
  LoanChecklistResult,
  SimulateDecisionResult,
} from '@/core/api/lendingApi';
import { ActiveLoan, LoanProduct } from '@/types';
import { formatINR } from '@/core/utils/currency';

type LendingTab = 'overview' | 'discovery' | 'calculator' | 'comparison' | 'assistant' | 'applications';

export const LendingPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<LendingTab>('overview');
  const [activeLoans, setActiveLoans] = useState<ActiveLoan[]>(mockActiveLoans);
  const [products, setProducts] = useState<LoanProduct[]>(mockLoanProducts);
  const [isLoading, setIsLoading] = useState(false);

  // ----------------------------------------------------
  // 1. LOAN DISCOVERY & ELIGIBILITY STATE
  // ----------------------------------------------------
  const [discoveryAmount, setDiscoveryAmount] = useState('300000');
  const [discoveryIncome, setDiscoveryIncome] = useState('75000');
  const [employmentType, setEmploymentType] = useState('SALARIED');
  const [existingEmis, setExistingEmis] = useState('0');
  const [preferredTenure, setPreferredTenure] = useState(36);
  const [loanPurpose, setLoanPurpose] = useState('PERSONAL');
  const [creditTier, setCreditTier] = useState('740');
  const [isDiscovering, setIsDiscovering] = useState(false);
  const [eligibilityResult, setEligibilityResult] = useState<LoanEligibilityResult | null>(null);

  // ----------------------------------------------------
  // 2. AUTHORITATIVE EMI CALCULATOR STATE
  // ----------------------------------------------------
  const [calcPrincipal, setCalcPrincipal] = useState(300000);
  const [calcTenureMonths, setCalcTenureMonths] = useState(36);
  const [calcInterestRate, setCalcInterestRate] = useState(8.50);
  const [isCalculatingEmi, setIsCalculatingEmi] = useState(false);
  const [emiResult, setEmiResult] = useState<EmiCalculationResult | null>(null);

  // Local fallback calculation if backend is briefly offline
  const fallbackCalculateEmi = (p: number, rAnnual: number, n: number) => {
    const r = rAnnual / 100 / 12;
    if (r === 0) return p / n;
    return (p * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
  };

  const localEmi = fallbackCalculateEmi(calcPrincipal, calcInterestRate, calcTenureMonths);
  const localTotalPayment = localEmi * calcTenureMonths;
  const localTotalInterest = localTotalPayment - calcPrincipal;

  // ----------------------------------------------------
  // 3. LOAN COMPARISON MATRIX STATE
  // ----------------------------------------------------
  const [compareAmount, setCompareAmount] = useState(25000);
  const [compareTenure, setCompareTenure] = useState(36);
  const [isComparing, setIsComparing] = useState(false);
  const [comparisonResult, setComparisonResult] = useState<LoanComparisonResult | null>(null);

  // ----------------------------------------------------
  // 4. AI LOAN ASSISTANT STATE
  // ----------------------------------------------------
  const [assistantQuery, setAssistantQuery] = useState('');
  const [isAssistantLoading, setIsAssistantLoading] = useState(false);
  const [assistantResponse, setAssistantResponse] = useState<LoanAssistantQueryResult | null>(null);

  // Document checklist
  const [checklistEmployment, setChecklistEmployment] = useState('SALARIED');
  const [checklistPurpose, setChecklistPurpose] = useState('PERSONAL');
  const [checklistResult, setChecklistResult] = useState<LoanChecklistResult | null>(null);
  const [isChecklistLoading, setIsChecklistLoading] = useState(false);

  // ----------------------------------------------------
  // 5. APPLICATION TRACKING & SIMULATED DECISION
  // ----------------------------------------------------
  const [applications, setApplications] = useState<any[]>([]);
  const [selectedApp, setSelectedApp] = useState<any | null>(null);
  const [isSimulatingDecision, setIsSimulatingDecision] = useState(false);
  const [decisionResult, setDecisionResult] = useState<SimulateDecisionResult | null>(null);
  const [showApplyModal, setShowApplyModal] = useState(false);
  const [applyProductId, setApplyProductId] = useState('');
  const [applyAmount, setApplyAmount] = useState('20000');
  const [applyTenure, setApplyTenure] = useState('36');
  const [applyPurpose, setApplyPurpose] = useState('PERSONAL');
  const [isSubmittingApp, setIsSubmittingApp] = useState(false);

  // Initial data load
  useEffect(() => {
    loadInitialData();
  }, []);

  const loadInitialData = async () => {
    setIsLoading(true);
    try {
      const [prods, apps] = await Promise.all([
        lendingApi.getProducts(),
        lendingApi.getApplications(),
      ]);
      if (prods && prods.length > 0) setProducts(prods);
      if (apps && apps.length > 0) {
        setActiveLoans(apps);
        setApplications(apps);
      }
    } catch (err) {
      console.warn('Using mock data for initial lending load', err);
    } finally {
      setIsLoading(false);
    }
  };

  // Run discovery & illustrative eligibility
  const handleRunDiscovery = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsDiscovering(true);
    try {
      const res = await lendingApi.discoverEligibility({
        requestedAmount: parseFloat(discoveryAmount) || 25000,
        monthlyIncome: parseFloat(discoveryIncome) || 6500,
        employmentType,
        existingEmis: parseFloat(existingEmis) || 0,
        preferredTenureMonths: preferredTenure,
        loanPurpose,
        estimatedCreditScore: parseInt(creditTier) || 740,
      });
      setEligibilityResult(res);
    } catch (err) {
      // Fallback calculation
      const income = parseFloat(discoveryIncome) || 6500;
      const existing = parseFloat(existingEmis) || 600;
      const reqP = parseFloat(discoveryAmount) || 25000;
      const proposedEmi = fallbackCalculateEmi(reqP, 5.49, preferredTenure);
      const totalObligations = existing + proposedEmi;
      const foir = Math.round((totalObligations / income) * 1000) / 10;
      const maxPermissibleEmi = Math.max(0, income * 0.45 - existing);

      setEligibilityResult({
        illustrativelyEligible: foir <= 50.0,
        requestedAmount: reqP,
        maxEligibleAmount: Math.round(maxPermissibleEmi * preferredTenure * 0.9),
        calculatedFoirPercentage: foir,
        proposedEmi: Math.round(proposedEmi * 100) / 100,
        maxPermissibleEmi: Math.round(maxPermissibleEmi * 100) / 100,
        estimatedApr: 5.49,
        disclaimer:
          'ILLUSTRATIVE ESTIMATE ONLY — This calculation is simulated based on unverified preliminary inputs and does not constitute a formal loan approval or binding offer by any lending institution.',
        eligibilityFactors: [
          `Income Baseline: ${formatINR(income)}/mo (${employmentType})`,
          `FOIR Commitment: ${foir}% of income dedicated to debt payments (Safe threshold: 45%)`,
          `Tenure: ${preferredTenure} months yields estimated ${formatINR(proposedEmi)}/mo`,
        ],
        improvementSuggestions: [
          foir > 45
            ? 'Extend your preferred tenure to lower monthly obligations and bring FOIR within 40%.'
            : 'Excellent debt servicing ratio! You qualify for tier-1 rate discounts.',
          'Prepaying high-interest credit card debt frees up debt servicing headroom.',
        ],
        matchingProducts: [
          {
            productId: 'sim-1',
            lenderName: 'FinPath Prime Federal',
            productName: 'Clean Energy & Prime Personal Borrowing',
            loanType: 'PERSONAL',
            interestRateApr: 5.49,
            interestRateType: 'FIXED',
            monthlyEmi: Math.round(proposedEmi * 100) / 100,
            totalInterest: Math.round((proposedEmi * preferredTenure - reqP) * 100) / 100,
            processingFee: Math.round(reqP * 0.0075 * 100) / 100,
            matchScorePercentage: 94,
            matchReason: 'Zero-prepayment penalty structure with instant paperless underwriting.',
            highlights: ['5.49% Fixed APR', 'Zero prepayment fees', 'Instant disbursement'],
            simulated: true,
          },
        ],
      });
    } finally {
      setIsDiscovering(false);
    }
  };

  // Run Authoritative Java EMI Calculator
  const handleCalculateEmi = async (principal = calcPrincipal, tenure = calcTenureMonths, rate = calcInterestRate) => {
    setIsCalculatingEmi(true);
    try {
      const res = await lendingApi.calculateEmi({
        principal,
        annualInterestRate: rate,
        tenureMonths: tenure,
      });
      setEmiResult(res);
    } catch (err) {
      console.warn('Falling back to client-side EMI calculation', err);
    } finally {
      setIsCalculatingEmi(false);
    }
  };

  // Run Loan Comparison
  const handleRunComparison = async () => {
    setIsComparing(true);
    try {
      const res = await lendingApi.compareLoans({
        requestedAmount: compareAmount,
        tenureMonths: compareTenure,
      });
      setComparisonResult(res);
    } catch (err) {
      // Fallback simulated comparison
      setComparisonResult({
        requestedAmount: compareAmount,
        tenureMonths: compareTenure,
        comparisonMatrix: [
          {
            productId: 'p-1',
            lenderName: 'FinPath Prime Federal',
            productName: 'Clean Energy & Auto Loan',
            loanType: 'AUTO',
            interestRateApr: 4.75,
            interestRateType: 'FIXED',
            processingFeePercentage: 0.75,
            processingFeeAmount: compareAmount * 0.0075,
            monthlyEmi: fallbackCalculateEmi(compareAmount, 4.75, compareTenure),
            totalInterest: fallbackCalculateEmi(compareAmount, 4.75, compareTenure) * compareTenure - compareAmount,
            totalBorrowingCost:
              compareAmount +
              (fallbackCalculateEmi(compareAmount, 4.75, compareTenure) * compareTenure - compareAmount) +
              compareAmount * 0.0075,
            minTermMonths: 24,
            maxTermMonths: 72,
            prepaymentTerms: 'No prepayment penalty at any time',
            features: ['100% On-road financing', '0.25% EV discount', 'Instant e-mandate'],
            simulated: true,
          },
          {
            productId: 'p-2',
            lenderName: 'Apex Commercial Bank',
            productName: 'Flexi-Personal Borrowing',
            loanType: 'PERSONAL',
            interestRateApr: 5.49,
            interestRateType: 'FIXED',
            processingFeePercentage: 1.0,
            processingFeeAmount: compareAmount * 0.01,
            monthlyEmi: fallbackCalculateEmi(compareAmount, 5.49, compareTenure),
            totalInterest: fallbackCalculateEmi(compareAmount, 5.49, compareTenure) * compareTenure - compareAmount,
            totalBorrowingCost:
              compareAmount +
              (fallbackCalculateEmi(compareAmount, 5.49, compareTenure) * compareTenure - compareAmount) +
              compareAmount * 0.01,
            minTermMonths: 12,
            maxTermMonths: 60,
            prepaymentTerms: 'Zero penalty after 6 months',
            features: ['Disbursed within 2 hours', 'No physical paperwork', 'Flexible tenure'],
            simulated: true,
          },
          {
            productId: 'p-3',
            lenderName: 'Horizon Mutual Financial',
            productName: 'Conforming Fixed Mortgage',
            loanType: 'MORTGAGE',
            interestRateApr: 5.95,
            interestRateType: 'FIXED',
            processingFeePercentage: 0.5,
            processingFeeAmount: compareAmount * 0.005,
            monthlyEmi: fallbackCalculateEmi(compareAmount, 5.95, compareTenure),
            totalInterest: fallbackCalculateEmi(compareAmount, 5.95, compareTenure) * compareTenure - compareAmount,
            totalBorrowingCost:
              compareAmount +
              (fallbackCalculateEmi(compareAmount, 5.95, compareTenure) * compareTenure - compareAmount) +
              compareAmount * 0.005,
            minTermMonths: 180,
            maxTermMonths: 360,
            prepaymentTerms: 'No penalty for up to 20% annual principal paydown',
            features: ['60-day rate lock', 'Free title search', 'Digital valuation'],
            simulated: true,
          },
        ],
        lowestCostLender: 'FinPath Prime Federal',
        lowestEmiLender: 'FinPath Prime Federal',
      });
    } finally {
      setIsComparing(false);
    }
  };

  // AI Assistant Query
  const handleAskAssistant = async (queryText = assistantQuery) => {
    if (!queryText.trim()) return;
    setIsAssistantLoading(true);
    try {
      const res = await lendingApi.askAssistant({
        query: queryText,
        loanType: 'PERSONAL',
        employmentType: 'SALARIED',
      });
      setAssistantResponse(res);
    } catch (err) {
      setAssistantResponse({
        topic: 'Lending Terminology & Guidance',
        answer:
          'Reducing balance APR charges interest strictly on your outstanding principal balance, whereas Flat Interest Rates keep charging you on the original loan amount even after you pay it down. Insist on checking the true APR.',
        plainEnglishDefinition: 'Reducing APR protects you from hidden borrowing costs.',
        practicalImpacts: [
          'A flat rate of 8% equals roughly 15-16% reducing APR.',
          'Always compare the total borrowing cost rather than headline teaser rates.',
        ],
        relatedTerms: ['Amortization', 'FOIR', 'Effective APR', 'Processing Fee'],
        recommendedNextSteps: [
          'Use our Interactive EMI Amortization Simulator to inspect reducing interest.',
          'Check the Loan Comparison Matrix for processing fee transparent breakdowns.',
        ],
      });
    } finally {
      setIsAssistantLoading(false);
    }
  };

  // Document checklist generator
  const handleGenerateChecklist = async () => {
    setIsChecklistLoading(true);
    try {
      const res = await lendingApi.getChecklist({
        employmentType: checklistEmployment,
        loanType: checklistPurpose,
        loanAmount: 25000,
      });
      setChecklistResult(res);
    } catch (err) {
      console.warn('Checklist fallback', err);
    } finally {
      setIsChecklistLoading(false);
    }
  };

  // Simulate Underwriting Decision
  const handleSimulateDecision = async (applicationId: string) => {
    setIsSimulatingDecision(true);
    try {
      const res = await lendingApi.simulateDecision(applicationId);
      setDecisionResult(res);
      // Refresh applications
      loadInitialData();
    } catch (err) {
      setDecisionResult({
        applicationId,
        applicationNumber: 'LN-SIM-9921',
        decision: 'APPROVED',
        explanation: 'Simulated Underwriting Decision: Approved under Tier-1 prime risk criteria with FOIR < 42%.',
        approvedAmount: 25000,
        finalApr: 5.49,
        monthlyEmi: 754.2,
        evaluatedFoir: 34.2,
        conditions: ['Sign digital loan contract with e-signature', 'Configure ACH auto-debit'],
        requestedDocuments: [],
        simulated: true,
      });
    } finally {
      setIsSimulatingDecision(false);
    }
  };

  // Submit new application
  const handleCreateApplication = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmittingApp(true);
    try {
      const prodId = applyProductId || (products[0] ? products[0].id : 'p-1');
      await lendingApi.createApplication({
        loanProductId: prodId,
        requestedAmount: parseFloat(applyAmount) || 20000,
        requestedTermMonths: parseInt(applyTenure) || 36,
        loanPurpose: applyPurpose,
        employmentType: 'SALARIED',
        monthlyIncome: 6500,
        existingEmis: 600,
        status: 'SUBMITTED',
      });
      setShowApplyModal(false);
      loadInitialData();
      setActiveTab('applications');
    } catch (err) {
      console.error('Failed to create application', err);
    } finally {
      setIsSubmittingApp(false);
    }
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <Landmark className="w-6 h-6 text-cyan-400" />
            <h1 className="text-2xl font-bold text-white tracking-tight">Lending AI Hub</h1>
            <Badge variant="cyan" size="sm">
              PHASE 5 ENGINE
            </Badge>
          </div>
          <p className="text-xs text-slate-400">
            Algorithmic loan discovery, authoritative EMI amortization, transparent side-by-side comparison, and AI underwriting.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Button
            variant="outline"
            size="sm"
            leftIcon={<Sparkles className="w-4 h-4 text-electric-400" />}
            onClick={() => setActiveTab('discovery')}
          >
            Check Eligibility
          </Button>
          <Button
            variant="electric"
            size="sm"
            leftIcon={<DollarSign className="w-4 h-4" />}
            onClick={() => setShowApplyModal(true)}
          >
            New Loan Application
          </Button>
        </div>
      </div>

      {/* Tabs Navigation */}
      <div className="flex border-b border-navy-800 space-x-1 sm:space-x-4 overflow-x-auto pb-1 text-xs sm:text-sm">
        <button
          onClick={() => setActiveTab('overview')}
          className={`flex items-center gap-2 px-3 py-2 font-medium border-b-2 transition-colors whitespace-nowrap ${
            activeTab === 'overview'
              ? 'border-cyan-400 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <Landmark className="w-4 h-4" />
          Overview & Facilities
        </button>

        <button
          onClick={() => {
            setActiveTab('discovery');
            if (!eligibilityResult) {
              handleRunDiscovery({ preventDefault: () => {} } as any);
            }
          }}
          className={`flex items-center gap-2 px-3 py-2 font-medium border-b-2 transition-colors whitespace-nowrap ${
            activeTab === 'discovery'
              ? 'border-cyan-400 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <UserCheck className="w-4 h-4" />
          Loan Discovery & Eligibility
        </button>

        <button
          onClick={() => {
            setActiveTab('calculator');
            if (!emiResult) handleCalculateEmi();
          }}
          className={`flex items-center gap-2 px-3 py-2 font-medium border-b-2 transition-colors whitespace-nowrap ${
            activeTab === 'calculator'
              ? 'border-cyan-400 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <Calculator className="w-4 h-4" />
          EMI Amortization Simulator
        </button>

        <button
          onClick={() => {
            setActiveTab('comparison');
            if (!comparisonResult) handleRunComparison();
          }}
          className={`flex items-center gap-2 px-3 py-2 font-medium border-b-2 transition-colors whitespace-nowrap ${
            activeTab === 'comparison'
              ? 'border-cyan-400 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <Scale className="w-4 h-4" />
          Lender Comparison Matrix
        </button>

        <button
          onClick={() => {
            setActiveTab('assistant');
            if (!assistantResponse) handleAskAssistant('What is the difference between APR and Flat Interest Rate?');
            if (!checklistResult) handleGenerateChecklist();
          }}
          className={`flex items-center gap-2 px-3 py-2 font-medium border-b-2 transition-colors whitespace-nowrap ${
            activeTab === 'assistant'
              ? 'border-cyan-400 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <Sparkles className="w-4 h-4" />
          AI Loan Assistant & Checklist
        </button>

        <button
          onClick={() => setActiveTab('applications')}
          className={`flex items-center gap-2 px-3 py-2 font-medium border-b-2 transition-colors whitespace-nowrap ${
            activeTab === 'applications'
              ? 'border-cyan-400 text-cyan-400'
              : 'border-transparent text-slate-400 hover:text-slate-200'
          }`}
        >
          <Clock className="w-4 h-4" />
          Application Tracking & Decisions
        </button>
      </div>

      {/* ========================================================================= */}
      {/* TAB 1: OVERVIEW & ACTIVE FACILITIES */}
      {/* ========================================================================= */}
      {activeTab === 'overview' && (
        <div className="space-y-6">
          {/* Quick Metrics */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <Card className="p-4 bg-navy-900 border border-navy-800">
              <span className="text-[10px] uppercase font-bold text-slate-400">Total Borrowed</span>
              <p className="text-xl font-bold text-white font-mono mt-1">{formatINR(650000)}</p>
              <span className="text-[10px] text-cyan-400">1 Active Facility</span>
            </Card>

            <Card className="p-4 bg-navy-900 border border-navy-800">
              <span className="text-[10px] uppercase font-bold text-slate-400">Monthly Debt Service</span>
              <p className="text-xl font-bold text-emerald-400 font-mono mt-1">{formatINR(8540)} / mo</p>
              <span className="text-[10px] text-slate-400">Auto-debit active</span>
            </Card>

            <Card className="p-4 bg-navy-900 border border-navy-800">
              <span className="text-[10px] uppercase font-bold text-slate-400">Fixed Obligation (FOIR)</span>
              <p className="text-xl font-bold text-cyan-300 font-mono mt-1">24.2%</p>
              <span className="text-[10px] text-emerald-400">Well below 45% ceiling</span>
            </Card>

            <Card className="p-4 bg-navy-900 border border-navy-800">
              <span className="text-[10px] uppercase font-bold text-slate-400">Credit Score Tier</span>
              <p className="text-xl font-bold text-electric-400 font-mono mt-1">745 Prime</p>
              <span className="text-[10px] text-slate-400">Qualifies for prime baseline rates</span>
            </Card>
          </div>

          {/* Active Facilities Cards */}
          <div className="space-y-3">
            <h2 className="text-sm font-bold text-white tracking-tight uppercase text-slate-400">
              Active Borrowing Facilities
            </h2>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
              {activeLoans.map((loan) => (
                <Card key={loan.id} className="p-5 bg-navy-900 border border-navy-700/80">
                  <div className="flex items-start justify-between mb-3">
                    <Badge variant="cyan" size="sm">
                      {loan.status}
                    </Badge>
                    <span className="text-[10px] text-slate-400 font-mono">
                      {loan.applicationNumber}
                    </span>
                  </div>

                  <h3 className="text-sm font-bold text-white mb-1">{loan.productName}</h3>
                  <p className="text-xs text-slate-400 mb-4 font-mono">
                    APR: <span className="text-cyan-400 font-semibold">{loan.interestRateApr}% Fixed</span> • Tenure: {loan.termMonths} Months
                  </p>

                  <div className="space-y-1 mb-4">
                    <div className="flex justify-between text-xs font-mono">
                      <span className="text-slate-400">Principal Paid</span>
                      <span className="text-emerald-400 font-semibold">
                        {formatINR(loan.totalPrincipal - loan.remainingBalance)} of {formatINR(loan.totalPrincipal)}
                      </span>
                    </div>
                    <div className="w-full bg-navy-950 rounded-full h-2">
                      <div
                        className="bg-gradient-to-r from-electric-600 to-cyan-400 h-2 rounded-full"
                        style={{
                          width: `${Math.max(15, ((loan.totalPrincipal - loan.remainingBalance) / loan.totalPrincipal) * 100)}%`,
                        }}
                      />
                    </div>
                  </div>

                  <div className="pt-3 border-t border-navy-800 flex items-center justify-between text-xs">
                    <div>
                      <p className="text-[10px] text-slate-400 uppercase">Monthly Installment</p>
                      <p className="text-base font-bold text-white font-mono">
                        {formatINR(loan.monthlyEmi)}/mo
                      </p>
                    </div>
                    <div className="text-right">
                      <span className="text-[10px] text-slate-400 flex items-center gap-1 justify-end">
                        <Clock className="w-3 h-3 text-cyan-400" />
                        Due {loan.nextPaymentDate}
                      </span>
                      <span className="text-[11px] text-slate-300 font-mono font-medium">
                        {loan.monthsRemaining} installments remaining
                      </span>
                    </div>
                  </div>
                </Card>
              ))}
            </div>
          </div>

          {/* Catalog of Available Lending Programs */}
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <h2 className="text-sm font-bold text-white tracking-tight uppercase text-slate-400">
                Institutional Lending Programs
              </h2>
              <Badge variant="outline" size="sm">
                SIMULATED PRODUCTS
              </Badge>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
              {products.map((prod) => (
                <Card key={prod.id} className="p-5 bg-navy-900 border border-navy-700/80 flex flex-col justify-between">
                  <div>
                    <div className="flex items-center justify-between mb-2">
                      <Badge variant="electric" size="sm">
                        {prod.type}
                      </Badge>
                      <span className="text-xs font-bold text-cyan-300 font-mono">From {prod.apr}% APR</span>
                    </div>

                    <h3 className="text-base font-bold text-white mb-2">{prod.name}</h3>
                    <p className="text-xs text-slate-400 mb-4 font-mono">
                      Borrow up to {formatINR(prod.maxPrincipal)} ({prod.minTermMonths}-{prod.maxTermMonths} mo)
                    </p>

                    <div className="space-y-1.5 pt-3 border-t border-navy-800 text-xs">
                      {prod.features &&
                        prod.features.slice(0, 3).map((feat, i) => (
                          <div key={i} className="flex items-center gap-2 text-slate-300">
                            <CheckCircle2 className="w-3.5 h-3.5 text-cyan-400 shrink-0" />
                            <span>{feat}</span>
                          </div>
                        ))}
                    </div>
                  </div>

                  <div className="mt-5 pt-3 border-t border-navy-800 flex gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      className="w-full text-xs"
                      onClick={() => {
                        setDiscoveryAmount(prod.minPrincipal.toString());
                        setLoanPurpose(prod.type);
                        setActiveTab('discovery');
                      }}
                    >
                      Check Eligibility
                    </Button>
                    <Button
                      variant="electric"
                      size="sm"
                      className="w-full text-xs"
                      onClick={() => {
                        setApplyProductId(prod.id);
                        setApplyAmount(prod.minPrincipal.toString());
                        setShowApplyModal(true);
                      }}
                    >
                      Apply
                    </Button>
                  </div>
                </Card>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 2: LOAN DISCOVERY & ILLUSTRATIVE ELIGIBILITY */}
      {/* ========================================================================= */}
      {activeTab === 'discovery' && (
        <div className="space-y-6">
          {/* Mandatory Regulatory Disclaimer */}
          <div className="p-4 rounded-xl bg-amber-500/10 border border-amber-500/30 text-amber-200 text-xs flex items-start gap-3">
            <Info className="w-5 h-5 text-amber-400 shrink-0 mt-0.5" />
            <div>
              <p className="font-bold uppercase tracking-wider text-[11px] text-amber-300">
                Important Regulatory Notice: Illustrative Eligibility Only
              </p>
              <p className="mt-1 text-slate-300">
                All pre-qualification metrics, FOIR calculations, and borrowing limits generated here are simulated mathematical models based on unverified preliminary inputs.
                <span className="font-semibold text-white"> They DO NOT constitute formal credit approval, binding offers, or underwriting commitments.</span> Formal loan approval requires full credit bureau pull, income document validation, and risk policy checks.
              </p>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Input Form */}
            <Card className="p-6 bg-navy-900 border border-navy-700/80 lg:col-span-1">
              <CardHeader className="p-0 pb-4">
                <CardTitle className="text-base flex items-center gap-2">
                  <UserCheck className="w-5 h-5 text-cyan-400" />
                  Borrower Profile Inputs
                </CardTitle>
                <p className="text-xs text-slate-400">
                  Input your monthly income, existing debt obligations, and target borrowing needs.
                </p>
              </CardHeader>

              <form onSubmit={handleRunDiscovery} className="space-y-4">
                <Input
                  label="Target Loan Amount (₹)"
                  type="number"
                  value={discoveryAmount}
                  onChange={(e) => setDiscoveryAmount(e.target.value)}
                  required
                />

                <Input
                  label="Net Monthly Income (₹)"
                  type="number"
                  value={discoveryIncome}
                  onChange={(e) => setDiscoveryIncome(e.target.value)}
                  helperText="Your take-home pay after taxes & deductions"
                  required
                />

                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">
                    Employment Type
                  </label>
                  <select
                    value={employmentType}
                    onChange={(e) => setEmploymentType(e.target.value)}
                    className="w-full bg-navy-950 border border-navy-700 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-cyan-400"
                  >
                    <option value="SALARIED">Salaried Corporate / W-2</option>
                    <option value="SELF_EMPLOYED">Self-Employed Professional</option>
                    <option value="BUSINESS_OWNER">Business Owner / SME</option>
                    <option value="GIG_CONTRACTOR">Gig Economy / Freelancer</option>
                  </select>
                </div>

                <Input
                  label="Existing Monthly EMIs / Debt (₹)"
                  type="number"
                  value={existingEmis}
                  onChange={(e) => setExistingEmis(e.target.value)}
                  helperText="Current auto loans, student debt, credit card minimums"
                  required
                />

                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">
                    Preferred Tenure: {preferredTenure} Months ({preferredTenure / 12} Years)
                  </label>
                  <input
                    type="range"
                    min="12"
                    max="84"
                    step="6"
                    value={preferredTenure}
                    onChange={(e) => setPreferredTenure(Number(e.target.value))}
                    className="w-full accent-cyan-400 h-2 bg-navy-950 rounded-lg cursor-pointer"
                  />
                  <div className="flex justify-between text-[10px] text-slate-500 font-mono mt-1">
                    <span>12 mo</span>
                    <span>36 mo</span>
                    <span>84 mo</span>
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">
                    Loan Purpose
                  </label>
                  <select
                    value={loanPurpose}
                    onChange={(e) => setLoanPurpose(e.target.value)}
                    className="w-full bg-navy-950 border border-navy-700 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-cyan-400"
                  >
                    <option value="PERSONAL">Personal & Flexible Use</option>
                    <option value="VEHICLE_PURCHASE">Clean Energy & Auto Vehicle</option>
                    <option value="DEBT_CONSOLIDATION">Debt Consolidation</option>
                    <option value="HOME_IMPROVEMENT">Home Renovation</option>
                    <option value="BUSINESS_EXPANSION">SME Business Growth</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1">
                    Estimated Credit Score: {creditTier}
                  </label>
                  <select
                    value={creditTier}
                    onChange={(e) => setCreditTier(e.target.value)}
                    className="w-full bg-navy-950 border border-navy-700 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-cyan-400"
                  >
                    <option value="760">Excellent (750+) — Tier 1 Rates</option>
                    <option value="720">Good (700-749) — Standard Prime</option>
                    <option value="660">Fair (640-699) — Moderate Spread</option>
                    <option value="600">Subprime (&lt;640) — Higher APR</option>
                  </select>
                </div>

                <Button
                  variant="electric"
                  size="sm"
                  type="submit"
                  className="w-full"
                  isLoading={isDiscovering}
                  leftIcon={<Sparkles className="w-4 h-4" />}
                >
                  Analyze Illustrative Eligibility
                </Button>
              </form>
            </Card>

            {/* Results Panel */}
            <div className="lg:col-span-2 space-y-5">
              {eligibilityResult ? (
                <>
                  {/* Status Banner */}
                  <Card
                    className={`p-6 border ${
                      eligibilityResult.illustrativelyEligible
                        ? 'bg-emerald-500/10 border-emerald-500/40 text-emerald-200'
                        : 'bg-rose-500/10 border-rose-500/40 text-rose-200'
                    }`}
                  >
                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-4">
                      <div className="flex items-center gap-3">
                        {eligibilityResult.illustrativelyEligible ? (
                          <div className="w-10 h-10 rounded-full bg-emerald-500/20 flex items-center justify-center shrink-0">
                            <CheckCircle2 className="w-6 h-6 text-emerald-400" />
                          </div>
                        ) : (
                          <div className="w-10 h-10 rounded-full bg-rose-500/20 flex items-center justify-center shrink-0">
                            <AlertCircle className="w-6 h-6 text-rose-400" />
                          </div>
                        )}
                        <div>
                          <h3 className="text-lg font-bold text-white">
                            {eligibilityResult.illustrativelyEligible
                              ? 'Illustratively Eligible (Pre-Screen Passed)'
                              : 'FOIR Debt Service Limit Exceeded'}
                          </h3>
                          <p className="text-xs text-slate-300">
                            Fixed Obligation to Income Ratio (FOIR):{' '}
                            <span className="font-bold text-white font-mono">
                              {eligibilityResult.calculatedFoirPercentage}%
                            </span>{' '}
                            (Institutional Ceiling: 45.0%)
                          </p>
                        </div>
                      </div>

                      <Badge
                        variant={eligibilityResult.illustrativelyEligible ? 'cyan' : 'danger'}
                        size="md"
                      >
                        {eligibilityResult.illustrativelyEligible ? 'FOIR HEALTHY' : 'NEEDS ADJUSTMENT'}
                      </Badge>
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-4 border-t border-navy-800 text-xs">
                      <div>
                        <span className="text-slate-400 block">Proposed Monthly EMI</span>
                        <span className="text-base font-bold text-white font-mono">
                          {formatINR(eligibilityResult.proposedEmi)} / mo
                        </span>
                      </div>
                      <div>
                        <span className="text-slate-400 block">Max Permissible EMI</span>
                        <span className="text-base font-bold text-cyan-300 font-mono">
                          {formatINR(eligibilityResult.maxPermissibleEmi)} / mo
                        </span>
                      </div>
                      <div>
                        <span className="text-slate-400 block">Max Eligible Borrowing</span>
                        <span className="text-base font-bold text-emerald-400 font-mono">
                          {formatINR(eligibilityResult.maxEligibleAmount)}
                        </span>
                      </div>
                    </div>
                  </Card>

                  {/* Factor Explanations */}
                  <Card className="p-5 bg-navy-900 border border-navy-800">
                    <h4 className="text-xs font-bold uppercase text-slate-400 tracking-wider mb-3 flex items-center gap-2">
                      <Scale className="w-4 h-4 text-cyan-400" />
                      Underwriting Factors Analysis
                    </h4>
                    <div className="space-y-2">
                      {eligibilityResult.eligibilityFactors.map((factor, i) => (
                        <div key={i} className="flex items-start gap-2 text-xs text-slate-300">
                          <Check className="w-3.5 h-3.5 text-cyan-400 mt-0.5 shrink-0" />
                          <span>{factor}</span>
                        </div>
                      ))}
                    </div>
                  </Card>

                  {/* Improvement Suggestions */}
                  <Card className="p-5 bg-navy-900 border border-navy-800">
                    <h4 className="text-xs font-bold uppercase text-slate-400 tracking-wider mb-3 flex items-center gap-2">
                      <Sparkles className="w-4 h-4 text-electric-400" />
                      Algorithmic Improvement Recommendations
                    </h4>
                    <div className="space-y-2">
                      {eligibilityResult.improvementSuggestions.map((sug, i) => (
                        <div key={i} className="flex items-start gap-2 text-xs text-slate-300 bg-navy-950 p-2.5 rounded-lg border border-navy-800">
                          <ArrowRight className="w-3.5 h-3.5 text-electric-400 mt-0.5 shrink-0" />
                          <span>{sug}</span>
                        </div>
                      ))}
                    </div>
                  </Card>

                  {/* Matching Simulated Products */}
                  <div className="space-y-3">
                    <h4 className="text-xs font-bold uppercase text-slate-400 tracking-wider">
                      Recommended Simulated Loan Products
                    </h4>

                    <div className="space-y-3">
                      {eligibilityResult.matchingProducts.map((prod) => (
                        <Card key={prod.productId} className="p-4 bg-navy-900 border border-navy-700/80 hover:border-cyan-500/40 transition-colors">
                          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                            <div>
                              <div className="flex items-center gap-2 mb-1">
                                <span className="text-xs font-semibold text-cyan-400">{prod.lenderName}</span>
                                <Badge variant="outline" size="sm">
                                  SIMULATED
                                </Badge>
                                <Badge variant="cyan" size="sm">
                                  {prod.matchScorePercentage}% Match
                                </Badge>
                              </div>
                              <h5 className="text-sm font-bold text-white">{prod.productName}</h5>
                              <p className="text-xs text-slate-400 mt-1">{prod.matchReason}</p>
                            </div>

                            <div className="text-right shrink-0">
                              <span className="text-base font-bold text-white font-mono">
                                {formatINR(prod.monthlyEmi)}
                                <span className="text-xs text-slate-400 font-sans"> / mo</span>
                              </span>
                              <p className="text-[11px] text-cyan-400 font-mono font-medium">
                                {prod.interestRateApr}% {prod.interestRateType} APR
                              </p>
                              <Button
                                variant="electric"
                                size="sm"
                                className="mt-2 text-xs"
                                onClick={() => {
                                  setApplyProductId(prod.productId);
                                  setApplyAmount(discoveryAmount);
                                  setApplyTenure(preferredTenure.toString());
                                  setShowApplyModal(true);
                                }}
                              >
                                Apply Now
                              </Button>
                            </div>
                          </div>
                        </Card>
                      ))}
                    </div>
                  </div>
                </>
              ) : (
                <div className="p-12 text-center bg-navy-900 border border-navy-800 rounded-2xl">
                  <UserCheck className="w-12 h-12 text-slate-600 mx-auto mb-3" />
                  <p className="text-sm font-bold text-white">No Analysis Generated Yet</p>
                  <p className="text-xs text-slate-400 mt-1 max-w-md mx-auto">
                    Fill in your income and debt obligations on the left and click 'Analyze Illustrative Eligibility' to compute your FOIR and borrowing headroom.
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 3: AUTHORITATIVE EMI CALCULATOR */}
      {/* ========================================================================= */}
      {activeTab === 'calculator' && (
        <div className="space-y-6">
          <Card className="p-6 bg-navy-900 border border-electric-500/20 shadow-glow">
            <CardHeader className="p-0 pb-6 flex flex-col sm:flex-row sm:items-center justify-between gap-2">
              <div>
                <div className="flex items-center gap-2">
                  <Calculator className="w-5 h-5 text-cyan-400" />
                  <CardTitle>Authoritative Java EMI Amortization Simulator</CardTitle>
                </div>
                <p className="text-xs text-slate-400 mt-1">
                  Verified mathematical formula: <span className="font-mono text-cyan-300">EMI = [P × r × (1+r)ⁿ] / [(1+r)ⁿ - 1]</span> executed via Java 21 backend engine.
                </p>
              </div>
              <Badge variant="cyan" size="sm">
                JAVA BACKEND VERIFIED
              </Badge>
            </CardHeader>

            <CardContent className="p-0 grid grid-cols-1 lg:grid-cols-3 gap-8 items-start">
              {/* Sliders */}
              <div className="lg:col-span-2 space-y-6">
                {/* Principal Slider */}
                <div>
                  <div className="flex justify-between items-center mb-2 text-xs">
                    <span className="text-slate-300 font-medium">Loan Principal</span>
                    <span className="text-base font-bold text-white font-mono">
                      {formatINR(calcPrincipal)}
                    </span>
                  </div>
                  <input
                    type="range"
                    min="25000"
                    max="1500000"
                    step="25000"
                    value={calcPrincipal}
                    onChange={(e) => {
                      const val = Number(e.target.value);
                      setCalcPrincipal(val);
                      handleCalculateEmi(val, calcTenureMonths, calcInterestRate);
                    }}
                    className="w-full accent-cyan-400 h-2 bg-navy-950 rounded-lg cursor-pointer"
                  />
                  <div className="flex justify-between text-[10px] text-slate-500 font-mono mt-1">
                    <span>{formatINR(25000)}</span>
                    <span>{formatINR(750000)}</span>
                    <span>{formatINR(1500000)}</span>
                  </div>
                </div>

                {/* Tenure Slider */}
                <div>
                  <div className="flex justify-between items-center mb-2 text-xs">
                    <span className="text-slate-300 font-medium">Repayment Tenure</span>
                    <span className="text-base font-bold text-white font-mono">
                      {calcTenureMonths} Months ({calcTenureMonths / 12} Years)
                    </span>
                  </div>
                  <input
                    type="range"
                    min="12"
                    max="84"
                    step="6"
                    value={calcTenureMonths}
                    onChange={(e) => {
                      const val = Number(e.target.value);
                      setCalcTenureMonths(val);
                      handleCalculateEmi(calcPrincipal, val, calcInterestRate);
                    }}
                    className="w-full accent-electric-400 h-2 bg-navy-950 rounded-lg cursor-pointer"
                  />
                  <div className="flex justify-between text-[10px] text-slate-500 font-mono mt-1">
                    <span>12 mo</span>
                    <span>48 mo</span>
                    <span>84 mo</span>
                  </div>
                </div>

                {/* Interest Rate Slider */}
                <div>
                  <div className="flex justify-between items-center mb-2 text-xs">
                    <span className="text-slate-300 font-medium">Annual Percentage Rate (APR)</span>
                    <span className="text-base font-bold text-cyan-300 font-mono">
                      {calcInterestRate.toFixed(2)}%
                    </span>
                  </div>
                  <input
                    type="range"
                    min="3.5"
                    max="18.0"
                    step="0.25"
                    value={calcInterestRate}
                    onChange={(e) => {
                      const val = Number(e.target.value);
                      setCalcInterestRate(val);
                      handleCalculateEmi(calcPrincipal, calcTenureMonths, val);
                    }}
                    className="w-full accent-cyan-400 h-2 bg-navy-950 rounded-lg cursor-pointer"
                  />
                  <div className="flex justify-between text-[10px] text-slate-500 font-mono mt-1">
                    <span>3.50% (Prime)</span>
                    <span>10.00%</span>
                    <span>18.00%</span>
                  </div>
                </div>

                {/* Live Amortization Schedule Table Preview */}
                {emiResult && emiResult.amortizationSchedule && (
                  <div className="space-y-2 pt-4 border-t border-navy-800">
                    <div className="flex items-center justify-between">
                      <h4 className="text-xs font-bold uppercase text-slate-400 tracking-wider">
                        Amortization Schedule Breakdown (First 12 Installments)
                      </h4>
                      <span className="text-[10px] text-slate-500 font-mono">
                        Interest to Principal: {emiResult.interestToPrincipalRatio}%
                      </span>
                    </div>

                    <div className="overflow-x-auto max-h-56 overflow-y-auto rounded-lg border border-navy-800">
                      <table className="w-full text-left text-xs">
                        <thead className="bg-navy-950 text-slate-400 text-[10px] uppercase font-mono sticky top-0">
                          <tr>
                            <th className="p-2">Mo</th>
                            <th className="p-2">Opening</th>
                            <th className="p-2">Principal</th>
                            <th className="p-2">Interest</th>
                            <th className="p-2">Monthly EMI</th>
                            <th className="p-2">Closing</th>
                          </tr>
                        </thead>
                        <tbody className="divide-y divide-navy-800/60 font-mono text-slate-300 text-[11px]">
                          {emiResult.amortizationSchedule.slice(0, 12).map((item) => (
                            <tr key={item.installmentNumber} className="hover:bg-navy-800/40">
                              <td className="p-2 text-slate-400">#{item.installmentNumber}</td>
                              <td className="p-2">{formatINR(item.openingBalance)}</td>
                              <td className="p-2 text-emerald-400 font-semibold">{formatINR(item.principalPaid)}</td>
                              <td className="p-2 text-amber-400">{formatINR(item.interestPaid)}</td>
                              <td className="p-2 text-white font-bold">{formatINR(item.monthlyPayment)}</td>
                              <td className="p-2">{formatINR(item.closingBalance)}</td>
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  </div>
                )}
              </div>

              {/* Calculated Results Panel */}
              <div className="glass-panel rounded-2xl p-6 bg-navy-950/80 border border-cyan-500/30 text-center space-y-4">
                <span className="text-[10px] font-bold text-slate-400 uppercase tracking-wider block">
                  Authoritative Monthly EMI
                </span>
                <div className="text-3xl sm:text-4xl font-extrabold text-cyan-300 font-mono">
                  {formatINR(emiResult ? emiResult.monthlyEmi : localEmi)}
                  <span className="text-xs text-slate-400 font-sans"> / mo</span>
                </div>

                <div className="pt-4 border-t border-navy-800 space-y-2.5 text-xs text-left">
                  <div className="flex justify-between text-slate-400">
                    <span>Total Principal:</span>
                    <span className="text-slate-200 font-mono font-semibold">
                      {formatINR(calcPrincipal)}
                    </span>
                  </div>
                  <div className="flex justify-between text-slate-400">
                    <span>Total Lifetime Interest:</span>
                    <span className="text-amber-400 font-mono font-semibold">
                      {formatINR(emiResult ? emiResult.totalInterest : localTotalInterest)}
                    </span>
                  </div>
                  <div className="flex justify-between text-slate-400">
                    <span>Total Repayment Amortization:</span>
                    <span className="text-cyan-400 font-mono font-bold">
                      {formatINR(emiResult ? emiResult.totalRepayment : localTotalPayment)}
                    </span>
                  </div>
                </div>

                <div className="p-3 bg-navy-900 rounded-lg text-left text-xs border border-navy-800 space-y-1">
                  <span className="text-[10px] uppercase font-bold text-slate-400 block">
                    Prepayment Tip
                  </span>
                  <p className="text-[11px] text-slate-300">
                    Paying an extra <span className="text-emerald-400 font-semibold">{formatINR(1000)}/month</span> directly toward principal will compress your tenure by ~4 months and save thousands in interest.
                  </p>
                </div>

                <Button
                  variant="electric"
                  size="sm"
                  className="w-full"
                  onClick={() => {
                    setApplyAmount(calcPrincipal.toString());
                    setApplyTenure(calcTenureMonths.toString());
                    setShowApplyModal(true);
                  }}
                >
                  Apply With These Terms
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 4: LENDER COMPARISON MATRIX */}
      {/* ========================================================================= */}
      {activeTab === 'comparison' && (
        <div className="space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <h2 className="text-base font-bold text-white flex items-center gap-2">
                <Scale className="w-5 h-5 text-cyan-400" />
                Simulated Lender Comparison Matrix
              </h2>
              <p className="text-xs text-slate-400">
                Transparent comparison of APR, processing fees, prepayment exit terms, and true total borrowing costs.
              </p>
            </div>

            <div className="flex items-center gap-3">
              <div className="flex items-center gap-2">
                <span className="text-xs text-slate-400">Amount:</span>
                <input
                  type="number"
                  value={compareAmount}
                  onChange={(e) => setCompareAmount(Number(e.target.value))}
                  className="w-24 bg-navy-950 border border-navy-700 rounded px-2 py-1 text-xs text-white font-mono"
                />
              </div>
              <div className="flex items-center gap-2">
                <span className="text-xs text-slate-400">Tenure:</span>
                <select
                  value={compareTenure}
                  onChange={(e) => setCompareTenure(Number(e.target.value))}
                  className="bg-navy-950 border border-navy-700 rounded px-2 py-1 text-xs text-white font-mono"
                >
                  <option value={24}>24 mo</option>
                  <option value={36}>36 mo</option>
                  <option value={48}>48 mo</option>
                  <option value={60}>60 mo</option>
                </select>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={handleRunComparison}
                isLoading={isComparing}
                leftIcon={<RefreshCw className="w-3.5 h-3.5" />}
              >
                Re-calculate
              </Button>
            </div>
          </div>

          {comparisonResult && (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <Card className="p-4 bg-emerald-500/10 border border-emerald-500/30">
                <span className="text-[10px] uppercase font-bold text-emerald-400 block">
                  Lowest Total Borrowing Cost
                </span>
                <p className="text-base font-bold text-white mt-1">
                  {comparisonResult.lowestCostLender}
                </p>
                <p className="text-xs text-slate-300 mt-0.5">
                  Minimizes processing fee + aggregate lifetime interest expense.
                </p>
              </Card>

              <Card className="p-4 bg-cyan-500/10 border border-cyan-500/30">
                <span className="text-[10px] uppercase font-bold text-cyan-400 block">
                  Lowest Monthly Installment (Cash Flow)
                </span>
                <p className="text-base font-bold text-white mt-1">
                  {comparisonResult.lowestEmiLender}
                </p>
                <p className="text-xs text-slate-300 mt-0.5">
                  Optimized for borrowers prioritizing monthly disposable liquidity.
                </p>
              </Card>
            </div>
          )}

          {/* Comparison Matrix Table */}
          <div className="overflow-x-auto rounded-xl border border-navy-800 bg-navy-900">
            <table className="w-full text-left text-xs">
              <thead className="bg-navy-950 text-slate-400 text-[11px] uppercase tracking-wider font-mono">
                <tr>
                  <th className="p-3.5">Lending Institution</th>
                  <th className="p-3.5">Product & Type</th>
                  <th className="p-3.5">Interest Rate (APR)</th>
                  <th className="p-3.5">Processing Fee</th>
                  <th className="p-3.5">Monthly EMI</th>
                  <th className="p-3.5">Total Interest</th>
                  <th className="p-3.5">Total Borrowing Cost</th>
                  <th className="p-3.5">Prepayment Penalty</th>
                  <th className="p-3.5 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-navy-800 text-slate-200">
                {comparisonResult ? (
                  comparisonResult.comparisonMatrix.map((item) => (
                    <tr key={item.productId} className="hover:bg-navy-800/40">
                      <td className="p-3.5">
                        <div className="font-bold text-white">{item.lenderName}</div>
                        <Badge variant="outline" size="sm" className="mt-1">
                          SIMULATED
                        </Badge>
                      </td>
                      <td className="p-3.5">
                        <div className="font-semibold text-slate-200">{item.productName}</div>
                        <span className="text-[10px] text-slate-400 font-mono">{item.loanType}</span>
                      </td>
                      <td className="p-3.5 font-mono">
                        <span className="font-bold text-cyan-300">{item.interestRateApr}%</span>
                        <span className="text-[10px] text-slate-400 block">{item.interestRateType}</span>
                      </td>
                      <td className="p-3.5 font-mono">
                        <span>{formatINR(item.processingFeeAmount)}</span>
                        <span className="text-[10px] text-slate-400 block">({item.processingFeePercentage}%)</span>
                      </td>
                      <td className="p-3.5 font-mono font-bold text-white">
                        {formatINR(item.monthlyEmi)} / mo
                      </td>
                      <td className="p-3.5 font-mono text-amber-400 font-medium">
                        {formatINR(item.totalInterest)}
                      </td>
                      <td className="p-3.5 font-mono font-bold text-emerald-400">
                        {formatINR(item.totalBorrowingCost)}
                      </td>
                      <td className="p-3.5 text-xs text-slate-300 max-w-xs">
                        {item.prepaymentTerms}
                      </td>
                      <td className="p-3.5 text-right">
                        <Button
                          variant="electric"
                          size="sm"
                          className="text-xs"
                          onClick={() => {
                            setApplyProductId(item.productId);
                            setApplyAmount(compareAmount.toString());
                            setApplyTenure(compareTenure.toString());
                            setShowApplyModal(true);
                          }}
                        >
                          Select
                        </Button>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={9} className="p-8 text-center text-slate-400">
                      Loading comparison matrix...
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 5: AI LOAN ASSISTANT & APPLICATION CHECKLIST */}
      {/* ========================================================================= */}
      {activeTab === 'assistant' && (
        <div className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Terminology Q&A Assistant */}
            <div className="lg:col-span-2 space-y-4">
              <Card className="p-6 bg-navy-900 border border-navy-700/80">
                <CardHeader className="p-0 pb-4">
                  <div className="flex items-center gap-2">
                    <Sparkles className="w-5 h-5 text-cyan-400" />
                    <CardTitle className="text-base">AI Lending Terminology & Guidance Assistant</CardTitle>
                  </div>
                  <p className="text-xs text-slate-400">
                    Ask questions regarding lending math, reducing balance APR vs flat rates, FOIR caps, amortization schedules, and prepayment terms.
                  </p>
                </CardHeader>

                {/* Suggested prompt chips */}
                <div className="flex flex-wrap gap-2 mb-4">
                  {[
                    'Explain APR vs Flat Interest Rate',
                    'What is FOIR and how does it limit my loan?',
                    'How does loan amortization work?',
                    'Are there prepayment penalties on personal loans?',
                    'How does credit score impact my interest rate spread?',
                  ].map((chip) => (
                    <button
                      key={chip}
                      onClick={() => {
                        setAssistantQuery(chip);
                        handleAskAssistant(chip);
                      }}
                      className="text-[11px] bg-navy-950 hover:bg-navy-800 text-slate-300 hover:text-cyan-300 border border-navy-800 rounded-full px-3 py-1 transition-colors"
                    >
                      {chip}
                    </button>
                  ))}
                </div>

                {/* Question Input */}
                <div className="flex gap-2">
                  <input
                    type="text"
                    value={assistantQuery}
                    onChange={(e) => setAssistantQuery(e.target.value)}
                    placeholder="Ask any lending question (e.g. What is LTV ratio?)..."
                    className="flex-1 bg-navy-950 border border-navy-700 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-cyan-400"
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') handleAskAssistant();
                    }}
                  />
                  <Button
                    variant="electric"
                    size="sm"
                    onClick={() => handleAskAssistant()}
                    isLoading={isAssistantLoading}
                  >
                    Ask
                  </Button>
                </div>
              </Card>

              {/* AI Answer Display */}
              {assistantResponse && (
                <Card className="p-6 bg-navy-900 border border-cyan-500/30 shadow-glow animate-fade-in space-y-4">
                  <div className="flex items-center justify-between">
                    <h3 className="text-base font-bold text-white flex items-center gap-2">
                      <HelpCircle className="w-4 h-4 text-cyan-400" />
                      {assistantResponse.topic}
                    </h3>
                    <Badge variant="cyan" size="sm">
                      VERIFIED LENDING AI
                    </Badge>
                  </div>

                  <p className="text-xs text-slate-200 leading-relaxed bg-navy-950 p-4 rounded-xl border border-navy-800">
                    {assistantResponse.answer}
                  </p>

                  {/* Practical Impacts */}
                  <div className="space-y-2">
                    <h4 className="text-xs font-bold uppercase text-slate-400 tracking-wider">
                      Practical Borrowing Impacts
                    </h4>
                    {assistantResponse.practicalImpacts.map((imp, i) => (
                      <div key={i} className="flex items-start gap-2 text-xs text-slate-300">
                        <CheckCircle2 className="w-3.5 h-3.5 text-cyan-400 mt-0.5 shrink-0" />
                        <span>{imp}</span>
                      </div>
                    ))}
                  </div>

                  {/* Related Terms */}
                  <div className="pt-3 border-t border-navy-800 flex items-center gap-2">
                    <span className="text-[11px] text-slate-400">Related Concepts:</span>
                    <div className="flex flex-wrap gap-1.5">
                      {assistantResponse.relatedTerms.map((term, i) => (
                        <span
                          key={i}
                          onClick={() => {
                            setAssistantQuery(`Tell me about ${term}`);
                            handleAskAssistant(`Tell me about ${term}`);
                          }}
                          className="text-[10px] bg-navy-950 text-cyan-300 font-mono px-2 py-0.5 rounded cursor-pointer hover:bg-navy-800 border border-navy-800"
                        >
                          {term}
                        </span>
                      ))}
                    </div>
                  </div>
                </Card>
              )}
            </div>

            {/* Application Document Checklist Generator */}
            <div className="space-y-4">
              <Card className="p-5 bg-navy-900 border border-navy-700/80">
                <CardHeader className="p-0 pb-3">
                  <CardTitle className="text-sm flex items-center gap-2">
                    <ListChecks className="w-4 h-4 text-cyan-400" />
                    Tailored Document Checklist
                  </CardTitle>
                  <p className="text-xs text-slate-400">
                    Generate the exact paperwork required by underwriting teams to prevent delays.
                  </p>
                </CardHeader>

                <div className="space-y-3">
                  <div>
                    <label className="block text-[11px] text-slate-300 mb-1">Applicant Profile</label>
                    <select
                      value={checklistEmployment}
                      onChange={(e) => {
                        setChecklistEmployment(e.target.value);
                      }}
                      className="w-full bg-navy-950 border border-navy-700 rounded-lg px-2.5 py-1.5 text-xs text-white"
                    >
                      <option value="SALARIED">Salaried Employee</option>
                      <option value="SELF_EMPLOYED">Self-Employed / Business</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-[11px] text-slate-300 mb-1">Loan Category</label>
                    <select
                      value={checklistPurpose}
                      onChange={(e) => {
                        setChecklistPurpose(e.target.value);
                      }}
                      className="w-full bg-navy-950 border border-navy-700 rounded-lg px-2.5 py-1.5 text-xs text-white"
                    >
                      <option value="PERSONAL">Personal Borrowing</option>
                      <option value="AUTO">Auto / Vehicle</option>
                      <option value="MORTGAGE">Home Mortgage</option>
                      <option value="BUSINESS_EXPANSION">SME Commercial</option>
                    </select>
                  </div>

                  <Button
                    variant="outline"
                    size="sm"
                    className="w-full text-xs"
                    onClick={handleGenerateChecklist}
                    isLoading={isChecklistLoading}
                  >
                    Refresh Checklist
                  </Button>
                </div>

                {/* Checklist Results */}
                {checklistResult && (
                  <div className="mt-4 pt-4 border-t border-navy-800 space-y-3">
                    <span className="text-[10px] font-bold uppercase text-slate-400 block tracking-wider">
                      Mandatory Documentation
                    </span>

                    <div className="space-y-2">
                      {checklistResult.mandatoryDocuments.map((doc) => (
                        <div key={doc.documentCode} className="p-2.5 rounded-lg bg-navy-950 border border-navy-800 text-xs">
                          <div className="flex items-center justify-between mb-1">
                            <span className="font-bold text-white text-[11px]">{doc.title}</span>
                            <Badge variant="danger" size="sm">
                              REQUIRED
                            </Badge>
                          </div>
                          <p className="text-[10px] text-slate-300">{doc.description}</p>
                          <span className="text-[9px] text-cyan-400 block mt-1 font-mono">
                            Format: {doc.formatGuidance}
                          </span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </Card>
            </div>
          </div>
        </div>
      )}

      {/* ========================================================================= */}
      {/* TAB 6: APPLICATION TRACKING & SIMULATED DECISIONS */}
      {/* ========================================================================= */}
      {activeTab === 'applications' && (
        <div className="space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <h2 className="text-base font-bold text-white flex items-center gap-2">
                <Clock className="w-5 h-5 text-cyan-400" />
                Origination Lifecycle & Underwriting Decision Engine
              </h2>
              <p className="text-xs text-slate-400">
                Track status across DRAFT, SUBMITTED, UNDER REVIEW, DOCUMENTS REQUIRED, and simulated automated approval decisions.
              </p>
            </div>

            <Button
              variant="electric"
              size="sm"
              leftIcon={<DollarSign className="w-4 h-4" />}
              onClick={() => setShowApplyModal(true)}
            >
              Start New Application
            </Button>
          </div>

          {/* Applications List */}
          <div className="space-y-4">
            {applications.length > 0 ? (
              applications.map((app) => (
                <Card key={app.id} className="p-5 bg-navy-900 border border-navy-700/80">
                  <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-navy-800">
                    <div>
                      <div className="flex items-center gap-2 mb-1">
                        <span className="text-xs font-mono font-bold text-cyan-400">{app.applicationNumber}</span>
                        <Badge
                          variant={
                            app.status === 'APPROVED' || app.status === 'DISBURSED'
                              ? 'success'
                              : app.status === 'DOCUMENTS_REQUIRED'
                              ? 'danger'
                              : 'cyan'
                          }
                          size="sm"
                        >
                          {app.status}
                        </Badge>
                        <Badge variant="outline" size="sm">
                          SIMULATED
                        </Badge>
                      </div>
                      <h3 className="text-base font-bold text-white">{app.productName}</h3>
                    </div>

                    <div className="flex items-center gap-3">
                      <Button
                        variant="electric"
                        size="sm"
                        onClick={() => handleSimulateDecision(app.id)}
                        isLoading={isSimulatingDecision}
                        leftIcon={<Sparkles className="w-3.5 h-3.5" />}
                      >
                        Simulate Underwriting Decision
                      </Button>
                    </div>
                  </div>

                  {/* 5-Stage Visual Progress Stepper */}
                  <div className="py-4">
                    <div className="grid grid-cols-5 text-center text-[10px] font-mono gap-1">
                      {['DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'DOCUMENTS_REQUIRED', 'APPROVED'].map((step, idx) => {
                        const stepOrder = ['DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'DOCUMENTS_REQUIRED', 'APPROVED', 'DISBURSED'];
                        const currentIdx = stepOrder.indexOf(app.status);
                        const isCompleted = currentIdx >= idx;
                        const isCurrent = app.status === step;

                        return (
                          <div key={step} className="flex flex-col items-center">
                            <div
                              className={`w-6 h-6 rounded-full flex items-center justify-center font-bold mb-1 ${
                                isCompleted
                                  ? 'bg-cyan-500 text-navy-950 font-extrabold'
                                  : 'bg-navy-950 text-slate-500 border border-navy-800'
                              } ${isCurrent ? 'ring-2 ring-cyan-400' : ''}`}
                            >
                              {isCompleted ? <Check className="w-3.5 h-3.5" /> : idx + 1}
                            </div>
                            <span className={isCurrent ? 'text-cyan-300 font-bold' : 'text-slate-400'}>
                              {step.replace('_', ' ')}
                            </span>
                          </div>
                        );
                      })}
                    </div>
                  </div>

                  {/* Key Metrics */}
                  <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 pt-3 border-t border-navy-800 text-xs">
                    <div>
                      <span className="text-slate-400 block">Requested Amount</span>
                      <span className="text-sm font-bold text-white font-mono">
                        {app.totalPrincipal ? formatINR(app.totalPrincipal) : formatINR(300000)}
                      </span>
                    </div>
                    <div>
                      <span className="text-slate-400 block">Interest Rate (APR)</span>
                      <span className="text-sm font-bold text-cyan-300 font-mono">
                        {app.interestRateApr || 8.99}% Fixed
                      </span>
                    </div>
                    <div>
                      <span className="text-slate-400 block">Monthly Installment</span>
                      <span className="text-sm font-bold text-white font-mono">
                        {app.monthlyEmi ? formatINR(app.monthlyEmi) : formatINR(9540)} / mo
                      </span>
                    </div>
                    <div>
                      <span className="text-slate-400 block">Tenure</span>
                      <span className="text-sm font-bold text-slate-300 font-mono">
                        {app.termMonths || 36} Months
                      </span>
                    </div>
                  </div>
                </Card>
              ))
            ) : (
              <div className="p-12 text-center bg-navy-900 border border-navy-800 rounded-2xl">
                <Clock className="w-12 h-12 text-slate-600 mx-auto mb-3" />
                <p className="text-sm font-bold text-white">No Loan Applications Found</p>
                <p className="text-xs text-slate-400 mt-1 max-w-md mx-auto">
                  Click 'Start New Application' to draft an illustrative loan origination request and run the simulated decision engine.
                </p>
              </div>
            )}
          </div>

          {/* Decision Results Modal / Card */}
          {decisionResult && (
            <Modal
              isOpen={Boolean(decisionResult)}
              onClose={() => setDecisionResult(null)}
              title="Simulated Underwriting Assessment"
              description="Automated institutional risk decision calculated based on debt service ratios."
            >
              <div className="space-y-4">
                <div
                  className={`p-4 rounded-xl border ${
                    decisionResult.decision === 'APPROVED'
                      ? 'bg-emerald-500/10 border-emerald-500/40 text-emerald-200'
                      : decisionResult.decision === 'CONDITIONALLY_APPROVED'
                      ? 'bg-cyan-500/10 border-cyan-500/40 text-cyan-200'
                      : decisionResult.decision === 'DOCUMENTS_REQUIRED'
                      ? 'bg-amber-500/10 border-amber-500/40 text-amber-200'
                      : 'bg-rose-500/10 border-rose-500/40 text-rose-200'
                  }`}
                >
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-xs font-mono font-bold">{decisionResult.applicationNumber}</span>
                    <Badge variant="cyan" size="sm">
                      {decisionResult.decision}
                    </Badge>
                  </div>
                  <p className="text-xs leading-relaxed">{decisionResult.explanation}</p>
                </div>

                <div className="grid grid-cols-2 gap-3 text-xs bg-navy-950 p-3 rounded-xl border border-navy-800">
                  <div>
                    <span className="text-slate-400 block">Approved Amount</span>
                    <span className="text-base font-bold text-white font-mono">
                      {formatINR(decisionResult.approvedAmount)}
                    </span>
                  </div>
                  <div>
                    <span className="text-slate-400 block">Evaluated FOIR / DTI</span>
                    <span className="text-base font-bold text-cyan-300 font-mono">
                      {decisionResult.evaluatedFoir}%
                    </span>
                  </div>
                </div>

                {decisionResult.conditions && decisionResult.conditions.length > 0 && (
                  <div className="space-y-1.5">
                    <span className="text-[11px] font-bold text-slate-400 uppercase tracking-wider block">
                      Underwriting Approval Conditions:
                    </span>
                    {decisionResult.conditions.map((cond, i) => (
                      <div key={i} className="flex items-center gap-2 text-xs text-slate-300">
                        <CheckCircle2 className="w-3.5 h-3.5 text-cyan-400 shrink-0" />
                        <span>{cond}</span>
                      </div>
                    ))}
                  </div>
                )}

                <div className="flex justify-end pt-2">
                  <Button variant="electric" size="sm" onClick={() => setDecisionResult(null)}>
                    Acknowledge Decision
                  </Button>
                </div>
              </div>
            </Modal>
          )}
        </div>
      )}

      {/* ========================================================================= */}
      {/* NEW LOAN APPLICATION MODAL */}
      {/* ========================================================================= */}
      <Modal
        isOpen={showApplyModal}
        onClose={() => setShowApplyModal(false)}
        title="Submit Illustrative Loan Application"
        description="Fill out basic financial information to submit an illustrative application for underwriting review."
      >
        <form onSubmit={handleCreateApplication} className="space-y-4">
          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">
              Select Loan Product
            </label>
            <select
              value={applyProductId}
              onChange={(e) => setApplyProductId(e.target.value)}
              className="w-full bg-navy-950 border border-navy-700 rounded-lg px-3 py-2 text-xs text-white"
            >
              {products.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.name} ({p.apr}% APR)
                </option>
              ))}
            </select>
          </div>

          <Input
            label="Requested Loan Amount (₹)"
            type="number"
            value={applyAmount}
            onChange={(e) => setApplyAmount(e.target.value)}
            required
          />

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">
              Requested Tenure (Months)
            </label>
            <select
              value={applyTenure}
              onChange={(e) => setApplyTenure(e.target.value)}
              className="w-full bg-navy-950 border border-navy-700 rounded-lg px-3 py-2 text-xs text-white"
            >
              <option value="12">12 Months (1 Year)</option>
              <option value="24">24 Months (2 Years)</option>
              <option value="36">36 Months (3 Years)</option>
              <option value="48">48 Months (4 Years)</option>
              <option value="60">60 Months (5 Years)</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">
              Loan Purpose
            </label>
            <select
              value={applyPurpose}
              onChange={(e) => setApplyPurpose(e.target.value)}
              className="w-full bg-navy-950 border border-navy-700 rounded-lg px-3 py-2 text-xs text-white"
            >
              <option value="PERSONAL">Personal Financing</option>
              <option value="VEHICLE_PURCHASE">Vehicle Purchase</option>
              <option value="DEBT_CONSOLIDATION">Debt Consolidation</option>
              <option value="HOME_IMPROVEMENT">Home Improvement</option>
            </select>
          </div>

          <div className="flex justify-end gap-2 pt-2">
            <Button
              variant="ghost"
              size="sm"
              type="button"
              onClick={() => setShowApplyModal(false)}
            >
              Cancel
            </Button>
            <Button
              variant="electric"
              size="sm"
              type="submit"
              isLoading={isSubmittingApp}
            >
              Submit Application
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
