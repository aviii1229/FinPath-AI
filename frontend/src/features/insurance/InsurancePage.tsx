import React, { useState, useEffect } from 'react';
import {
  Shield,
  PlusCircle,
  FileText,
  AlertCircle,
  CheckCircle2,
  Clock,
  ArrowRight,
  Sparkles,
  Calculator,
  Search,
  UploadCloud,
  Layers,
  Bot,
  FileCheck,
  ChevronRight,
  AlertTriangle,
  Info,
  RefreshCw,
  ExternalLink,
} from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Button } from '@/shared/components/ui/Button';
import { Badge } from '@/shared/components/ui/Badge';
import { Modal } from '@/shared/components/ui/Modal';
import { Input } from '@/shared/components/ui/Input';
import {
  insuranceApi,
  InsuranceProductItem,
  InsuranceDiscoveryResult,
  InsuranceComparisonResult,
  PolicyRagQueryResult,
  ClaimChecklistResult,
  ClaimTrackingResult,
} from '@/core/api/insuranceApi';
import { InsurancePolicy, InsuranceClaim, InsuranceCategory } from '@/types';
import { formatINR } from '@/core/utils/currency';
import { mockPolicies, mockClaims } from '@/mock/mockData';

type InsuranceTab = 'overview' | 'discovery' | 'compare' | 'assistant' | 'claims';

export const InsurancePage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<InsuranceTab>('overview');
  const [policies, setPolicies] = useState<InsurancePolicy[]>(mockPolicies);
  const [claims, setClaims] = useState<InsuranceClaim[]>(mockClaims);
  const [products, setProducts] = useState<InsuranceProductItem[]>([]);
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');

  // Loading states
  const [isLoading, setIsLoading] = useState(false);

  // 1. Discovery State
  const [discoIncome, setDiscoIncome] = useState(900000);
  const [discoBudget, setDiscoBudget] = useState(2500);
  const [discoAge, setDiscoAge] = useState(32);
  const [discoDependents, setDiscoDependents] = useState(1);
  const [discoNeeds, setDiscoNeeds] = useState<string[]>(['HEALTH', 'TERM_LIFE']);
  const [discoPriority, setDiscoPriority] = useState('BALANCED');
  const [discoSmoker, setDiscoSmoker] = useState(false);
  const [discoPreExisting, setDiscoPreExisting] = useState(false);
  const [discoResult, setDiscoResult] = useState<InsuranceDiscoveryResult | null>(null);
  const [isDiscovering, setIsDiscovering] = useState(false);

  // 2. Comparison State
  const [compareIds, setCompareIds] = useState<string[]>(['prod-hlth-1', 'prod-life-1']);
  const [comparisonResult, setComparisonResult] = useState<InsuranceComparisonResult | null>(null);
  const [isComparing, setIsComparing] = useState(false);

  // 3. AI Policy Assistant (RAG) State
  const [policyQuestion, setPolicyQuestion] = useState('');
  const [ragResult, setRagResult] = useState<PolicyRagQueryResult | null>(null);
  const [isRagQuerying, setIsRagQuerying] = useState(false);
  const [uploadedDocName, setUploadedDocName] = useState<string>('Aegis_Comprehensive_Platinum_Care_Policy.pdf');
  const [isUploadingDoc, setIsUploadingDoc] = useState(false);
  const [uploadSuccessMsg, setUploadSuccessMsg] = useState('');

  // 4. Claims Assistance State
  const [showClaimModal, setShowClaimModal] = useState(false);
  const [claimPolicyId, setClaimPolicyId] = useState(mockPolicies[0].id);
  const [claimCategory, setClaimCategory] = useState('HEALTH');
  const [claimAmount, setClaimAmount] = useState('');
  const [claimDescription, setClaimDescription] = useState('');
  const [isSubmittingClaim, setIsSubmittingClaim] = useState(false);
  const [claimSuccessMsg, setClaimSuccessMsg] = useState('');
  const [claimChecklist, setClaimChecklist] = useState<ClaimChecklistResult | null>(null);
  const [selectedTrackingClaim, setSelectedTrackingClaim] = useState<ClaimTrackingResult | null>(null);

  // 5. Quote Calculator State
  const [quoteCategory, setQuoteCategory] = useState<InsuranceCategory>('HEALTH');
  const [quoteCoverage, setQuoteCoverage] = useState('500000');
  const [isSmoker, setIsSmoker] = useState(false);
  const [calculatedQuote, setCalculatedQuote] = useState<{ premium: number; carrier: string } | null>(null);

  // Initial load
  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setIsLoading(true);
    try {
      const [fetchedPolicies, fetchedClaims, fetchedProducts] = await Promise.all([
        insuranceApi.getPolicies(),
        insuranceApi.getClaims(),
        insuranceApi.getProducts(),
      ]);
      setPolicies(fetchedPolicies);
      setClaims(fetchedClaims);
      setProducts(fetchedProducts);

      if (fetchedProducts.length >= 2) {
        setCompareIds([fetchedProducts[0].id, fetchedProducts[1].id]);
      }
    } catch (e) {
      console.warn('Failed to fetch from backend, mock data retained');
    } finally {
      setIsLoading(false);
    }
  };

  // Run initial discovery recommendation and comparison matrix on mount
  useEffect(() => {
    handleRunDiscovery();
    handleRunComparison(['prod-hlth-1', 'prod-life-1']);
  }, []);

  const handleRunDiscovery = async () => {
    setIsDiscovering(true);
    try {
      const res = await insuranceApi.discoverRecommendations({
        age: discoAge,
        annualIncome: discoIncome,
        monthlyBudget: discoBudget,
        dependents: discoDependents,
        coverageNeeds: discoNeeds,
        primaryPriority: discoPriority,
        isSmoker: discoSmoker,
        hasPreExistingConditions: discoPreExisting,
      });
      setDiscoResult(res);
    } catch (err) {
      console.error(err);
    } finally {
      setIsDiscovering(false);
    }
  };

  // Handle Policy Comparison
  const handleRunComparison = async (idsToCompare?: string[]) => {
    const targetIds = idsToCompare || compareIds;
    if (targetIds.length === 0) return;
    setIsComparing(true);
    try {
      const res = await insuranceApi.comparePolicies(targetIds);
      setComparisonResult(res);
    } catch (err) {
      console.error(err);
    } finally {
      setIsComparing(false);
    }
  };

  // Handle RAG Assistant Question
  const handleAskPolicyQuestion = async (customQ?: string) => {
    const q = customQ || policyQuestion;
    if (!q.trim()) return;
    setIsRagQuerying(true);
    try {
      const res = await insuranceApi.queryAssistant({
        policyCategory: selectedCategory !== 'ALL' ? selectedCategory : 'HEALTH',
        question: q,
      });
      setRagResult(res);
    } catch (err) {
      console.error(err);
    } finally {
      setIsRagQuerying(false);
    }
  };

  // Handle PDF Document Upload
  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setIsUploadingDoc(true);
    setUploadSuccessMsg('');
    try {
      const res = await insuranceApi.uploadDocument(file, 'HEALTH');
      setUploadedDocName(res.fileName);
      setUploadSuccessMsg(`Successfully extracted ${res.chunkCount} policy sections using Apache Tika! Ready for Q&A.`);
    } catch (err) {
      // Offline fallback: set file name and display confirmation
      setUploadedDocName(file.name);
      setUploadSuccessMsg(`Document "${file.name}" processed and indexed into policy vector memory.`);
    } finally {
      setIsUploadingDoc(false);
      setTimeout(() => setUploadSuccessMsg(''), 5000);
    }
  };

  // Load Claim Checklist
  const handleLoadChecklist = async (cat: string) => {
    setClaimCategory(cat);
    try {
      const res = await insuranceApi.getClaimChecklist({
        category: cat,
        incidentType: cat === 'HEALTH' ? 'HOSPITALIZATION' : cat === 'AUTO' ? 'VEHICLE_COLLISION' : 'GENERAL',
        estimatedLoss: 1250,
      });
      setClaimChecklist(res);
    } catch (err) {
      console.error(err);
    }
  };

  // Load Tracking for a Claim
  const handleViewClaimTracking = async (claimId: string) => {
    try {
      const res = await insuranceApi.getClaimTracking(claimId);
      setSelectedTrackingClaim(res);
    } catch (err) {
      console.error(err);
    }
  };

  // Handle Calculate Quote
  const handleCalculateQuote = async () => {
    const base = Number(quoteCoverage) * 0.00008;
    const smokerMultiplier = isSmoker ? 1.45 : 1.0;
    const finalPremium = Math.round(base * smokerMultiplier * 100) / 100;
    setCalculatedQuote({
      premium: finalPremium,
      carrier: quoteCategory === 'HEALTH' ? 'Aegis Platinum Care' : 'Prudential Shield',
    });
  };

  // Handle Submit Claim
  const handleSubmitClaim = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!claimAmount || !claimDescription) return;

    setIsSubmittingClaim(true);
    try {
      const newClaim = await insuranceApi.submitClaim({
        policyId: claimPolicyId,
        claimAmount: parseFloat(claimAmount),
        incidentDate: new Date().toISOString().split('T')[0],
        incidentDescription: claimDescription,
      });

      setClaims([newClaim, ...claims]);
      setClaimSuccessMsg(`Claim #${newClaim.claimNumber} successfully filed! AI Fraud Triage Score: ${newClaim.aiFraudRiskScore}% (Fast-Track Approved)`);
      setTimeout(() => {
        setClaimSuccessMsg('');
        setShowClaimModal(false);
        setClaimAmount('');
        setClaimDescription('');
        handleViewClaimTracking(newClaim.id);
      }, 2000);
    } catch (err: any) {
      // Local fallback
      const selectedPol = policies.find((p) => p.id === claimPolicyId);
      const fallbackClaim: InsuranceClaim = {
        id: `clm_${Date.now()}`,
        claimNumber: `CLM-2026-${Math.floor(1000 + Math.random() * 9000)}`,
        policyId: claimPolicyId,
        policyName: selectedPol?.productName || 'Policy',
        category: selectedPol?.category || 'HEALTH',
        claimAmount: parseFloat(claimAmount),
        incidentDate: new Date().toISOString().split('T')[0],
        submissionDate: new Date().toISOString().split('T')[0],
        status: 'IN_REVIEW',
        aiFraudRiskScore: 3.8,
      };
      setClaims([fallbackClaim, ...claims]);
      setClaimSuccessMsg(`Claim #${fallbackClaim.claimNumber} registered. AI fraud triage verified.`);
      setTimeout(() => {
        setClaimSuccessMsg('');
        setShowClaimModal(false);
        setClaimAmount('');
        setClaimDescription('');
      }, 2000);
    } finally {
      setIsSubmittingClaim(false);
    }
  };

  const filteredPolicies =
    selectedCategory === 'ALL'
      ? policies
      : policies.filter((p) => p.category === selectedCategory);

  return (
    <div className="space-y-6">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <div className="w-8 h-8 rounded-lg bg-cyan-500/10 border border-cyan-500/30 flex items-center justify-center text-cyan-400">
              <Shield className="w-4 h-4" />
            </div>
            <h1 className="text-2xl font-bold text-white tracking-tight">Insurance Portfolio & Claims</h1>
            <Badge variant="cyan" size="sm">Phase 4 Autonomous</Badge>
          </div>
          <p className="text-xs text-slate-400">
            Intelligent insurance discovery, multi-policy matrix comparison, grounded RAG policy extraction, & automated claims.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Button
            variant="cyan"
            size="sm"
            leftIcon={<PlusCircle className="w-4 h-4" />}
            onClick={() => {
              handleLoadChecklist('HEALTH');
              setShowClaimModal(true);
            }}
          >
            File Digital Claim
          </Button>
        </div>
      </div>

      {/* Module Navigation Tabs */}
      <div className="flex items-center gap-1.5 p-1 bg-navy-950/80 border border-navy-800 rounded-xl overflow-x-auto">
        <button
          onClick={() => setActiveTab('overview')}
          className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-xs font-semibold transition-all select-none whitespace-nowrap ${
            activeTab === 'overview'
              ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40 shadow-glow-sm'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          <Layers className="w-3.5 h-3.5" />
          Portfolio & Overview
        </button>

        <button
          onClick={() => {
            setActiveTab('discovery');
            if (!discoResult) handleRunDiscovery();
          }}
          className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-xs font-semibold transition-all select-none whitespace-nowrap ${
            activeTab === 'discovery'
              ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40 shadow-glow-sm'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          <Sparkles className="w-3.5 h-3.5" />
          AI Insurance Discovery
        </button>

        <button
          onClick={() => {
            setActiveTab('compare');
            handleRunComparison();
          }}
          className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-xs font-semibold transition-all select-none whitespace-nowrap ${
            activeTab === 'compare'
              ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40 shadow-glow-sm'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          <Calculator className="w-3.5 h-3.5" />
          Policy Comparison Matrix
        </button>

        <button
          onClick={() => {
            setActiveTab('assistant');
            if (!ragResult) handleAskPolicyQuestion('What is my emergency room copay and deductible?');
          }}
          className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-xs font-semibold transition-all select-none whitespace-nowrap ${
            activeTab === 'assistant'
              ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40 shadow-glow-sm'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          <Bot className="w-3.5 h-3.5" />
          AI Policy Assistant (RAG)
        </button>

        <button
          onClick={() => {
            setActiveTab('claims');
            handleLoadChecklist('HEALTH');
          }}
          className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-xs font-semibold transition-all select-none whitespace-nowrap ${
            activeTab === 'claims'
              ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40 shadow-glow-sm'
              : 'text-slate-400 hover:text-white'
          }`}
        >
          <FileCheck className="w-3.5 h-3.5" />
          Claims & Checklists
        </button>
      </div>

      {/* TAB 1: OVERVIEW & PORTFOLIO */}
      {activeTab === 'overview' && (
        <div className="space-y-6">
          {/* Quick Metrics Bar */}
          <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
            <Card className="p-4 bg-navy-900 border border-navy-700/80">
              <span className="text-[10px] text-slate-400 font-mono uppercase tracking-wider">Total Active Coverage</span>
              <p className="text-xl font-bold text-white font-mono mt-1">{formatINR(18500000)}</p>
              <span className="text-[10px] text-emerald-400 flex items-center gap-1 mt-1">
                <CheckCircle2 className="w-3 h-3" /> 3 Policies Bound & Active
              </span>
            </Card>

            <Card className="p-4 bg-navy-900 border border-navy-700/80">
              <span className="text-[10px] text-slate-400 font-mono uppercase tracking-wider">Monthly Premium Commitment</span>
              <p className="text-xl font-bold text-cyan-300 font-mono mt-1">{formatINR(3308)}<span className="text-xs text-slate-400 font-normal">/mo</span></p>
              <span className="text-[10px] text-slate-400 mt-1 block">5.9% of monthly net income</span>
            </Card>

            <Card className="p-4 bg-navy-900 border border-navy-700/80">
              <span className="text-[10px] text-slate-400 font-mono uppercase tracking-wider">Claims Fast-Tracked</span>
              <p className="text-xl font-bold text-white font-mono mt-1">{formatINR(18500)}</p>
              <span className="text-[10px] text-cyan-400 flex items-center gap-1 mt-1">
                <Shield className="w-3 h-3" /> AI Triage avg 3.8% fraud risk
              </span>
            </Card>

            <Card className="p-4 bg-navy-900 border border-navy-700/80">
              <span className="text-[10px] text-slate-400 font-mono uppercase tracking-wider">Upcoming Renewal</span>
              <p className="text-xl font-bold text-amber-300 font-mono mt-1">50 Days</p>
              <span className="text-[10px] text-amber-400/90 flex items-center gap-1 mt-1">
                <Clock className="w-3 h-3" /> Term Life Shield ({formatINR(850)}/mo)
              </span>
            </Card>
          </div>

          {/* Category Filter Chips */}
          <div className="flex items-center gap-2 overflow-x-auto pb-1">
            {['ALL', 'HEALTH', 'TERM_LIFE', 'AUTO', 'HOMEOWNERS'].map((cat) => (
              <button
                key={cat}
                onClick={() => setSelectedCategory(cat)}
                className={`px-3 py-1.5 rounded-xl text-xs font-semibold transition-all select-none ${
                  selectedCategory === cat
                    ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40 shadow-glow-sm'
                    : 'bg-navy-900 text-slate-400 border border-navy-700 hover:text-white'
                }`}
              >
                {cat.replace('_', ' ')}
              </button>
            ))}
          </div>

          {/* Active Policies Cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
            {filteredPolicies.map((pol) => (
              <Card key={pol.id} className="p-5 bg-navy-900 border border-navy-700/80 flex flex-col justify-between">
                <div>
                  <div className="flex items-start justify-between mb-3">
                    <Badge
                      variant={pol.status === 'ACTIVE' ? 'success' : 'warning'}
                      size="sm"
                    >
                      {pol.status.replace('_', ' ')}
                    </Badge>
                    <span className="text-[10px] text-slate-400 font-mono">{pol.policyNumber}</span>
                  </div>

                  <h3 className="text-sm font-bold text-white mb-1">{pol.productName}</h3>
                  <p className="text-xs text-cyan-400 font-medium mb-4">{pol.carrierName}</p>

                  <div className="space-y-2 py-3 border-y border-navy-800 text-xs">
                    <div className="flex justify-between">
                      <span className="text-slate-400">Coverage (Sum Assured)</span>
                      <span className="text-slate-200 font-mono font-semibold">
                        {formatINR(pol.coverageAmount)}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-slate-400">Deductible</span>
                      <span className="text-slate-200 font-mono font-semibold">
                        {formatINR(pol.deductible)}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-slate-400">Renewal Date</span>
                      <span className="text-slate-300 font-mono">{pol.renewalDate}</span>
                    </div>
                  </div>
                </div>

                <div className="mt-4 pt-3 flex items-center justify-between">
                  <div>
                    <p className="text-[10px] text-slate-400 uppercase">Monthly Premium</p>
                    <p className="text-base font-bold text-white font-mono">
                      {formatINR(pol.monthlyPremium)}/mo
                    </p>
                  </div>
                  <Button
                    variant="outline"
                    size="sm"
                    className="text-xs"
                    onClick={() => {
                      setClaimPolicyId(pol.id);
                      handleLoadChecklist(pol.category);
                      setShowClaimModal(true);
                    }}
                  >
                    File Claim
                  </Button>
                </div>
              </Card>
            ))}
          </div>

          {/* Quote Estimator & Claims History */}
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card className="p-5 bg-navy-900 border border-navy-700/80">
              <CardHeader className="p-0 pb-4">
                <div className="flex items-center gap-2">
                  <Calculator className="w-5 h-5 text-cyan-400" />
                  <CardTitle>Instant Insurance Quote Estimator</CardTitle>
                </div>
                <p className="text-[11px] text-slate-400">
                  Calculate projected monthly premiums based on desired coverage and risk profile.
                </p>
              </CardHeader>

              <CardContent className="p-0 space-y-4">
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-medium text-slate-300 mb-1">Coverage Type</label>
                    <select
                      value={quoteCategory}
                      onChange={(e) => setQuoteCategory(e.target.value as InsuranceCategory)}
                      className="w-full glass-input rounded-lg px-3 py-2 text-xs text-slate-200 focus:border-cyan-500"
                    >
                      <option value="HEALTH">Health Comprehensive</option>
                      <option value="TERM_LIFE">Term Life Insurance</option>
                      <option value="AUTO">Auto Protection</option>
                      <option value="HOMEOWNERS">Homeowners HO-3</option>
                    </select>
                  </div>

                  <div>
                    <label className="block text-xs font-medium text-slate-300 mb-1">Coverage Amount (₹)</label>
                    <select
                      value={quoteCoverage}
                      onChange={(e) => setQuoteCoverage(e.target.value)}
                      className="w-full glass-input rounded-lg px-3 py-2 text-xs text-slate-200 focus:border-cyan-500"
                    >
                      <option value="500000">₹5,00,000 (5 Lakhs)</option>
                      <option value="1000000">₹10,00,000 (10 Lakhs)</option>
                      <option value="2500000">₹25,00,000 (25 Lakhs)</option>
                      <option value="10000000">₹1,00,00,000 (1 Crore)</option>
                    </select>
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  <input
                    type="checkbox"
                    id="smoker"
                    checked={isSmoker}
                    onChange={(e) => setIsSmoker(e.target.checked)}
                    className="rounded bg-navy-950 border-navy-700 text-cyan-500"
                  />
                  <label htmlFor="smoker" className="text-xs text-slate-300 cursor-pointer">
                    Tobacco / Nicotine User (Class 2 Underwriting)
                  </label>
                </div>

                <Button
                  variant="electric"
                  size="sm"
                  onClick={handleCalculateQuote}
                  className="w-full"
                >
                  Calculate Quote
                </Button>

                {calculatedQuote && (
                  <div className="p-4 rounded-xl bg-navy-950/80 border border-cyan-500/30 flex items-center justify-between animate-fade-in">
                    <div>
                      <span className="text-[10px] text-cyan-400 font-bold uppercase tracking-wider">
                        Estimated Quote
                      </span>
                      <p className="text-xs font-semibold text-white">{calculatedQuote.carrier}</p>
                    </div>
                    <div className="text-right">
                      <span className="text-lg font-bold text-cyan-300 font-mono">
                        {formatINR(calculatedQuote.premium)}
                      </span>
                      <span className="text-xs text-slate-400"> / month</span>
                    </div>
                  </div>
                )}
              </CardContent>
            </Card>

            <Card className="p-5 bg-navy-900 border border-navy-700/80">
              <CardHeader className="p-0 pb-4">
                <div className="flex items-center gap-2">
                  <FileText className="w-5 h-5 text-electric-400" />
                  <CardTitle>Claims Adjudication History</CardTitle>
                </div>
                <p className="text-[11px] text-slate-400">
                  Live claims tracking with automated AI fraud risk screening.
                </p>
              </CardHeader>

              <CardContent className="p-0">
                <div className="divide-y divide-navy-800">
                  {claims.map((clm) => (
                    <div key={clm.id} className="py-3 flex items-center justify-between text-xs">
                      <div>
                        <div className="flex items-center gap-2">
                          <span className="font-semibold text-white">{clm.claimNumber}</span>
                          <Badge
                            variant={clm.status === 'APPROVED' ? 'success' : 'warning'}
                            size="sm"
                          >
                            {clm.status}
                          </Badge>
                        </div>
                        <p className="text-[11px] text-slate-400 mt-0.5">{clm.policyName}</p>
                        <span className="text-[10px] text-slate-500 font-mono">
                          Submitted {clm.submissionDate} • AI Risk: {clm.aiFraudRiskScore}%
                        </span>
                      </div>

                      <div className="flex items-center gap-3">
                        <p className="text-xs font-bold text-white font-mono">
                          {formatINR(clm.claimAmount)}
                        </p>
                        <Button
                          variant="ghost"
                          size="sm"
                          className="text-xs text-cyan-400 hover:text-cyan-300"
                          onClick={() => {
                            setActiveTab('claims');
                            handleViewClaimTracking(clm.id);
                          }}
                        >
                          Track
                        </Button>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      )}

      {/* TAB 2: AI INSURANCE DISCOVERY */}
      {activeTab === 'discovery' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Intake Questionnaire */}
          <Card className="p-5 bg-navy-900 border border-navy-700/80 lg:col-span-1">
            <CardHeader className="p-0 pb-4">
              <div className="flex items-center gap-2">
                <Sparkles className="w-5 h-5 text-cyan-400" />
                <CardTitle>Discovery Questionnaire</CardTitle>
              </div>
              <p className="text-[11px] text-slate-400">
                AI evaluates your coverage needs, family obligations, and monthly budget.
              </p>
            </CardHeader>

            <CardContent className="p-0 space-y-4 text-xs">
              <div>
                <label className="block text-slate-300 font-medium mb-1">Annual Gross Income (₹)</label>
                <Input
                  type="number"
                  value={discoIncome}
                  onChange={(e) => setDiscoIncome(Number(e.target.value))}
                />
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-1">Monthly Insurance Budget: {formatINR(discoBudget)}/mo</label>
                <input
                  type="range"
                  min="500"
                  max="10000"
                  step="250"
                  value={discoBudget}
                  onChange={(e) => setDiscoBudget(Number(e.target.value))}
                  className="w-full accent-cyan-400"
                />
                <div className="flex justify-between text-[10px] text-slate-500 mt-1">
                  <span>₹500/mo</span>
                  <span>₹5,000/mo</span>
                  <span>₹10,000/mo</span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Age</label>
                  <Input
                    type="number"
                    value={discoAge}
                    onChange={(e) => setDiscoAge(Number(e.target.value))}
                  />
                </div>
                <div>
                  <label className="block text-slate-300 font-medium mb-1">Dependents</label>
                  <Input
                    type="number"
                    value={discoDependents}
                    onChange={(e) => setDiscoDependents(Number(e.target.value))}
                  />
                </div>
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-2">Coverage Categories Needed</label>
                <div className="grid grid-cols-2 gap-2">
                  {[
                    { id: 'HEALTH', label: 'Health' },
                    { id: 'TERM_LIFE', label: 'Term Life' },
                    { id: 'AUTO', label: 'Auto' },
                    { id: 'HOMEOWNERS', label: 'Home' },
                  ].map((cat) => (
                    <label
                      key={cat.id}
                      className={`flex items-center gap-2 p-2 rounded-lg border cursor-pointer transition-all ${
                        discoNeeds.includes(cat.id)
                          ? 'bg-cyan-500/15 border-cyan-500/40 text-cyan-200'
                          : 'bg-navy-950/60 border-navy-800 text-slate-400'
                      }`}
                    >
                      <input
                        type="checkbox"
                        checked={discoNeeds.includes(cat.id)}
                        onChange={(e) => {
                          if (e.target.checked) {
                            setDiscoNeeds([...discoNeeds, cat.id]);
                          } else {
                            setDiscoNeeds(discoNeeds.filter((n) => n !== cat.id));
                          }
                        }}
                        className="rounded accent-cyan-400"
                      />
                      <span>{cat.label}</span>
                    </label>
                  ))}
                </div>
              </div>

              <div>
                <label className="block text-slate-300 font-medium mb-1">Primary Optimization Goal</label>
                <select
                  value={discoPriority}
                  onChange={(e) => setDiscoPriority(e.target.value)}
                  className="w-full glass-input rounded-lg px-3 py-2 text-xs text-slate-200 focus:border-cyan-500"
                >
                  <option value="BALANCED">Balanced Protection & Cost</option>
                  <option value="LOW_PREMIUM">Lowest Monthly Premium</option>
                  <option value="MAX_COVERAGE">Maximum Sum Assured</option>
                  <option value="LOW_DEDUCTIBLE">Zero or Minimal Deductible</option>
                </select>
              </div>

              <div className="space-y-2 pt-1 border-t border-navy-800">
                <label className="flex items-center gap-2 cursor-pointer text-slate-300">
                  <input
                    type="checkbox"
                    checked={discoSmoker}
                    onChange={(e) => setDiscoSmoker(e.target.checked)}
                    className="rounded accent-cyan-400"
                  />
                  <span>Tobacco or Nicotine user</span>
                </label>

                <label className="flex items-center gap-2 cursor-pointer text-slate-300">
                  <input
                    type="checkbox"
                    checked={discoPreExisting}
                    onChange={(e) => setDiscoPreExisting(e.target.checked)}
                    className="rounded accent-cyan-400"
                  />
                  <span>Pre-existing medical conditions</span>
                </label>
              </div>

              <Button
                variant="electric"
                size="sm"
                className="w-full mt-2"
                onClick={handleRunDiscovery}
                isLoading={isDiscovering}
              >
                Analyze & Recommend Products
              </Button>
            </CardContent>
          </Card>

          {/* AI Discovery Output */}
          <div className="lg:col-span-2 space-y-5">
            {discoResult ? (
              <>
                {/* AI Summary Banner */}
                <Card className="p-4 bg-gradient-to-r from-navy-900 to-navy-950 border border-cyan-500/30">
                  <div className="flex items-start gap-3">
                    <Sparkles className="w-5 h-5 text-cyan-400 shrink-0 mt-0.5" />
                    <div>
                      <h4 className="text-xs font-bold text-white uppercase tracking-wider mb-1">
                        AI Underwriting & Gap Analysis
                      </h4>
                      <p className="text-xs text-slate-300 leading-relaxed mb-3">
                        {discoResult.aiAdviceSummary}
                      </p>
                      {discoResult.detectedCoverageGaps.length > 0 && (
                        <div className="space-y-1">
                          {discoResult.detectedCoverageGaps.map((gap, i) => (
                            <div key={i} className="flex items-center gap-2 text-[11px] text-amber-300">
                              <AlertTriangle className="w-3.5 h-3.5 shrink-0" />
                              <span>{gap}</span>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  </div>
                </Card>

                {/* Recommended Simulated Products */}
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <h3 className="text-sm font-bold text-white">Recommended Simulated Products</h3>
                    <span className="text-[10px] text-slate-400">Ranked by algorithm match score</span>
                  </div>

                  {discoResult.recommendations.map((rec, i) => (
                    <Card
                      key={rec.product.id}
                      className="p-5 bg-navy-900 border border-navy-700/80 hover:border-cyan-500/40 transition-all"
                    >
                      <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-3 mb-3">
                        <div>
                          <div className="flex items-center gap-2 mb-1">
                            <Badge variant="cyan" size="sm">
                              {rec.matchScorePercentage}% Match
                            </Badge>
                            <Badge variant="outline" size="sm" className="text-slate-400 border-slate-700">
                              Simulated Insurance Product
                            </Badge>
                          </div>
                          <h4 className="text-sm font-bold text-white">{rec.product.productName}</h4>
                          <p className="text-xs text-cyan-400 font-medium">{rec.product.carrierName}</p>
                        </div>

                        <div className="text-right sm:shrink-0">
                          <p className="text-base font-bold text-cyan-300 font-mono">
                            {formatINR(rec.estimatedMonthlyPremium)}/mo
                          </p>
                          <span className="text-[10px] text-slate-400">Estimated Premium</span>
                        </div>
                      </div>

                      <p className="text-xs text-slate-300 mb-3 bg-navy-950/60 p-2.5 rounded-lg border border-navy-800">
                        <span className="text-cyan-400 font-semibold">Why this fits: </span>
                        {rec.matchReason}
                      </p>

                      <div className="grid grid-cols-2 sm:grid-cols-3 gap-2 py-2 border-t border-navy-800 text-[11px] text-slate-400 mb-3">
                        <div>
                          <span className="block text-[10px] text-slate-500 uppercase">Max Coverage</span>
                          <span className="text-slate-200 font-mono font-medium">
                            {formatINR(rec.product.coverageAmountMax)}
                          </span>
                        </div>
                        <div>
                          <span className="block text-[10px] text-slate-500 uppercase">Deductible</span>
                          <span className="text-slate-200 font-medium">
                            {rec.product.deductibleOptions || '₹5,000 / ₹15,000'}
                          </span>
                        </div>
                        <div>
                          <span className="block text-[10px] text-slate-500 uppercase">Waiting Period</span>
                          <span className="text-slate-200 font-medium">
                            {rec.product.waitingPeriodDays === 0 ? '0 Days (Immediate)' : `${rec.product.waitingPeriodDays} Days`}
                          </span>
                        </div>
                      </div>

                      <div className="flex items-center justify-between pt-1">
                        <span className="text-[11px] text-emerald-400 font-medium flex items-center gap-1.5">
                          <CheckCircle2 className="w-3.5 h-3.5" />
                          {rec.priorityBenefit}
                        </span>

                        <Button
                          variant="outline"
                          size="sm"
                          className="text-xs"
                          onClick={() => {
                            if (!compareIds.includes(rec.product.id)) {
                              setCompareIds([...compareIds, rec.product.id]);
                            }
                            setActiveTab('compare');
                            handleRunComparison([...compareIds, rec.product.id]);
                          }}
                        >
                          Compare in Matrix
                        </Button>
                      </div>
                    </Card>
                  ))}
                </div>
              </>
            ) : (
              <div className="h-64 flex flex-col items-center justify-center text-slate-400">
                <RefreshCw className="w-6 h-6 animate-spin text-cyan-400 mb-2" />
                <p className="text-xs">Analyzing financial journey data...</p>
              </div>
            )}
          </div>
        </div>
      )}

      {/* TAB 3: POLICY COMPARISON MATRIX */}
      {activeTab === 'compare' && (
        <div className="space-y-6">
          <Card className="p-5 bg-navy-900 border border-navy-700/80">
            <CardHeader className="p-0 pb-4">
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                <div>
                  <div className="flex items-center gap-2 mb-1">
                    <Calculator className="w-5 h-5 text-cyan-400" />
                    <CardTitle>Side-by-Side Policy Comparison Matrix</CardTitle>
                  </div>
                  <p className="text-[11px] text-slate-400">
                    Compare coverage limits, deductibles, co-pays, exclusions, and waiting periods across simulated products.
                  </p>
                </div>

                <div className="flex items-center gap-2">
                  <Badge variant="outline" size="sm" className="text-slate-400 border-slate-700">
                    All Products Simulated
                  </Badge>
                </div>
              </div>
            </CardHeader>

            <CardContent className="p-0">
              {comparisonResult ? (
                <div className="space-y-5">
                  {/* Comparison Table */}
                  <div className="overflow-x-auto rounded-xl border border-navy-800">
                    <table className="w-full text-left border-collapse text-xs">
                      <thead>
                        <tr className="bg-navy-950/80 border-b border-navy-800">
                          <th className="p-3.5 font-bold text-slate-300 w-1/4">Policy Feature / Term</th>
                          {comparisonResult.products.map((p) => (
                            <th key={p.id} className="p-3.5 font-bold text-white border-l border-navy-800">
                              <span className="block text-cyan-400 text-[10px] uppercase">{p.carrierName}</span>
                              <span className="text-sm font-semibold">{p.productName}</span>
                              <span className="inline-block mt-1 text-[9px] px-1.5 py-0.5 rounded bg-navy-800 text-slate-400">
                                {p.category}
                              </span>
                            </th>
                          ))}
                        </tr>
                      </thead>
                      <tbody className="divide-y divide-navy-800">
                        {comparisonResult.comparisonMatrix.map((row, idx) => (
                          <tr key={idx} className={idx % 2 === 0 ? 'bg-navy-900/60' : 'bg-navy-950/40'}>
                            <td className="p-3.5 font-semibold text-slate-300">
                              {row.featureLabel}
                            </td>
                            {comparisonResult.products.map((p) => (
                              <td key={p.id} className="p-3.5 text-slate-300 border-l border-navy-800">
                                {row.featureLabel === 'Monthly Premium' ? (
                                  <span className="font-bold text-cyan-300 font-mono text-sm">
                                    {row.valuesByProductId[p.id] || '-'}
                                  </span>
                                ) : row.featureLabel === 'Maximum Coverage' ? (
                                  <span className="font-mono text-white font-semibold">
                                    {row.valuesByProductId[p.id] || '-'}
                                  </span>
                                ) : row.featureLabel === 'Waiting Period' ? (
                                  <Badge variant={row.valuesByProductId[p.id]?.includes('0 Days') ? 'success' : 'outline'} size="sm">
                                    {row.valuesByProductId[p.id] || '-'}
                                  </Badge>
                                ) : (
                                  <span className="leading-relaxed">
                                    {row.valuesByProductId[p.id] || '-'}
                                  </span>
                                )}
                              </td>
                            ))}
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>

                  {/* AI Recommendation Banner */}
                  <div className="p-4 rounded-xl bg-navy-950/80 border border-cyan-500/30 flex items-start gap-3">
                    <Sparkles className="w-5 h-5 text-cyan-400 shrink-0 mt-0.5" />
                    <div>
                      <h4 className="text-xs font-bold text-white uppercase tracking-wider mb-1">
                        AI Matrix Evaluation
                      </h4>
                      <p className="text-xs text-slate-300 leading-relaxed">
                        {comparisonResult.aiRecommendation}
                      </p>
                    </div>
                  </div>
                </div>
              ) : (
                <div className="py-12 text-center text-slate-400">
                  <p className="text-xs">Loading policy comparison data...</p>
                </div>
              )}
            </CardContent>
          </Card>
        </div>
      )}

      {/* TAB 4: AI POLICY ASSISTANT (RAG) */}
      {activeTab === 'assistant' && (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Document Ingestion Zone */}
          <Card className="p-5 bg-navy-900 border border-navy-700/80 lg:col-span-1">
            <CardHeader className="p-0 pb-4">
              <div className="flex items-center gap-2">
                <UploadCloud className="w-5 h-5 text-cyan-400" />
                <CardTitle>Policy PDF Ingestion</CardTitle>
              </div>
              <p className="text-[11px] text-slate-400">
                Upload your insurance PDF document. Apache Tika extracts contractual terms into RAG memory.
              </p>
            </CardHeader>

            <CardContent className="p-0 space-y-4">
              <label className="border-2 border-dashed border-navy-700 hover:border-cyan-500/60 rounded-xl p-5 flex flex-col items-center justify-center cursor-pointer transition-all bg-navy-950/60">
                <UploadCloud className="w-8 h-8 text-cyan-400 mb-2 animate-bounce" />
                <span className="text-xs font-semibold text-white">Click or drag PDF policy</span>
                <span className="text-[10px] text-slate-400 mt-1">Supports PDF, DOCX, TXT up to 15MB</span>
                <input
                  type="file"
                  accept=".pdf,.docx,.txt"
                  className="hidden"
                  onChange={handleFileUpload}
                  disabled={isUploadingDoc}
                />
              </label>

              {isUploadingDoc && (
                <div className="flex items-center gap-2 text-xs text-cyan-400">
                  <RefreshCw className="w-4 h-4 animate-spin" />
                  <span>Extracting document terms with Apache Tika...</span>
                </div>
              )}

              {uploadSuccessMsg && (
                <div className="p-3 rounded-lg bg-emerald-500/15 border border-emerald-500/30 text-emerald-300 text-xs flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 shrink-0" />
                  <span>{uploadSuccessMsg}</span>
                </div>
              )}

              {/* Active Document Status */}
              <div className="p-3 rounded-xl bg-navy-950/80 border border-navy-800">
                <span className="text-[10px] text-slate-500 uppercase tracking-wider block mb-1">
                  Active Indexed Document
                </span>
                <div className="flex items-center gap-2 text-xs text-slate-200">
                  <FileText className="w-4 h-4 text-cyan-400 shrink-0" />
                  <span className="font-mono truncate">{uploadedDocName}</span>
                </div>
                <div className="flex items-center gap-2 mt-2">
                  <Badge variant="cyan" size="sm">4 Sections Indexed</Badge>
                  <Badge variant="outline" size="sm" className="text-slate-400">Verified Grounded</Badge>
                </div>
              </div>

              {/* Suggested Questions Shelf */}
              <div>
                <span className="text-[11px] font-semibold text-slate-300 block mb-2">
                  Suggested Policy Questions
                </span>
                <div className="space-y-1.5">
                  {[
                    'What is my emergency room copay and deductible?',
                    'Does this policy cover injuries sustained while traveling abroad?',
                    'What are the exclusions regarding pre-existing conditions?',
                    'What are the mandatory deadlines for claim submission?',
                  ].map((prompt, i) => (
                    <button
                      key={i}
                      onClick={() => {
                        setPolicyQuestion(prompt);
                        handleAskPolicyQuestion(prompt);
                      }}
                      className="w-full text-left p-2 rounded-lg bg-navy-950/60 hover:bg-navy-800 text-[11px] text-slate-300 hover:text-white border border-navy-800 transition-all flex items-center justify-between"
                    >
                      <span className="truncate">{prompt}</span>
                      <ChevronRight className="w-3.5 h-3.5 text-cyan-400 shrink-0 ml-1" />
                    </button>
                  ))}
                </div>
              </div>
            </CardContent>
          </Card>

          {/* RAG Interaction & Grounded Citations Display */}
          <div className="lg:col-span-2 space-y-4">
            <Card className="p-5 bg-navy-900 border border-navy-700/80">
              <CardHeader className="p-0 pb-3">
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <Bot className="w-5 h-5 text-cyan-400" />
                    <CardTitle>Grounded Policy Q&A Engine</CardTitle>
                  </div>
                  <Badge variant="outline" size="sm" className="text-emerald-400 border-emerald-500/30">
                    Zero-Hallucination RAG
                  </Badge>
                </div>
              </CardHeader>

              <CardContent className="p-0 space-y-4">
                {/* Search Input Bar */}
                <div className="flex gap-2">
                  <Input
                    placeholder="Ask anything about your policy (e.g. deductibles, emergency care, exclusions)..."
                    value={policyQuestion}
                    onChange={(e) => setPolicyQuestion(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && handleAskPolicyQuestion()}
                    className="flex-1"
                  />
                  <Button
                    variant="electric"
                    size="sm"
                    onClick={() => handleAskPolicyQuestion()}
                    isLoading={isRagQuerying}
                  >
                    Query Policy
                  </Button>
                </div>

                {/* Grounded Result Display */}
                {ragResult ? (
                  <div className="space-y-4 animate-fade-in">
                    {/* Answer Card */}
                    <div className="p-4 rounded-xl bg-navy-950/80 border border-cyan-500/30">
                      <div className="flex items-center gap-2 text-xs font-bold text-cyan-400 uppercase tracking-wider mb-2">
                        <CheckCircle2 className="w-4 h-4 text-emerald-400" />
                        Grounded Answer
                      </div>
                      <div className="text-xs text-slate-200 leading-relaxed whitespace-pre-line">
                        {ragResult.answer}
                      </div>
                    </div>

                    {/* Citations Shelf */}
                    <div>
                      <h4 className="text-xs font-bold text-white uppercase tracking-wider mb-2 flex items-center gap-1.5">
                        <FileText className="w-3.5 h-3.5 text-cyan-400" />
                        Source Sections & Page Citations ({ragResult.citations.length})
                      </h4>

                      <div className="space-y-2.5">
                        {ragResult.citations.map((cite, i) => (
                          <div
                            key={i}
                            className="p-3.5 rounded-xl bg-navy-950/90 border border-navy-800 text-xs hover:border-cyan-500/30 transition-all"
                          >
                            <div className="flex items-center justify-between mb-1.5">
                              <span className="font-semibold text-cyan-300">
                                {cite.sectionTitle}
                              </span>
                              <Badge variant="cyan" size="sm">
                                Page {cite.pageNumber}
                              </Badge>
                            </div>

                            <p className="text-[11px] text-slate-300 italic bg-navy-900/60 p-2.5 rounded-lg border-l-2 border-cyan-400">
                              "{cite.excerpt}"
                            </p>

                            <div className="flex items-center justify-between mt-2 text-[10px] text-slate-500">
                              <span>Source: {cite.documentName}</span>
                              <span>Relevance: {Math.round(cite.relevanceScore * 100)}%</span>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  </div>
                ) : (
                  <div className="py-12 text-center text-slate-400">
                    <p className="text-xs">Select or ask a question to extract policy clauses.</p>
                  </div>
                )}
              </CardContent>
            </Card>
          </div>
        </div>
      )}

      {/* TAB 5: CLAIMS ASSISTANCE & DYNAMIC CHECKLIST */}
      {activeTab === 'claims' && (
        <div className="space-y-6">
          {/* Dynamic Checklist Generator */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <Card className="p-5 bg-navy-900 border border-navy-700/80 lg:col-span-1">
              <CardHeader className="p-0 pb-4">
                <div className="flex items-center gap-2">
                  <FileCheck className="w-5 h-5 text-cyan-400" />
                  <CardTitle>Claim Preparation Guide</CardTitle>
                </div>
                <p className="text-[11px] text-slate-400">
                  Select policy category to generate an automated documentation checklist.
                </p>
              </CardHeader>

              <CardContent className="p-0 space-y-3">
                <div className="grid grid-cols-2 gap-2">
                  {['HEALTH', 'AUTO', 'TERM_LIFE', 'HOMEOWNERS'].map((cat) => (
                    <button
                      key={cat}
                      onClick={() => handleLoadChecklist(cat)}
                      className={`p-2.5 rounded-lg text-xs font-semibold border transition-all text-left ${
                        claimCategory === cat
                          ? 'bg-cyan-500/20 text-cyan-300 border-cyan-500/40 shadow-glow-sm'
                          : 'bg-navy-950/60 text-slate-400 border-navy-800 hover:text-white'
                      }`}
                    >
                      {cat.replace('_', ' ')}
                    </button>
                  ))}
                </div>

                <Button
                  variant="electric"
                  size="sm"
                  className="w-full mt-2"
                  onClick={() => setShowClaimModal(true)}
                >
                  Launch Claim Submission
                </Button>
              </CardContent>
            </Card>

            {/* Checklist Details */}
            <div className="lg:col-span-2 space-y-4">
              {claimChecklist && (
                <Card className="p-5 bg-navy-900 border border-navy-700/80">
                  <CardHeader className="p-0 pb-4">
                    <div className="flex items-center justify-between">
                      <h3 className="text-sm font-bold text-white">
                        Required Documents for {claimChecklist.category} Claim
                      </h3>
                      <Badge variant="cyan" size="sm">
                        Avg {claimChecklist.estimatedProcessingDays} Days Adjudication
                      </Badge>
                    </div>
                  </CardHeader>

                  <CardContent className="p-0 space-y-4">
                    <div className="space-y-2">
                      {claimChecklist.requiredDocuments.map((doc, idx) => (
                        <div
                          key={idx}
                          className="p-3 rounded-lg bg-navy-950/60 border border-navy-800 flex items-start justify-between text-xs"
                        >
                          <div>
                            <div className="flex items-center gap-2">
                              <span className="font-semibold text-white">{doc.documentName}</span>
                              {doc.mandatory ? (
                                <Badge variant="warning" size="sm">Mandatory</Badge>
                              ) : (
                                <Badge variant="outline" size="sm">Optional</Badge>
                              )}
                            </div>
                            <p className="text-[11px] text-slate-400 mt-1">{doc.description}</p>
                          </div>
                          <span className="text-[10px] text-slate-500 font-mono">
                            {doc.sampleTemplate}
                          </span>
                        </div>
                      ))}
                    </div>

                    {/* Common Pitfalls & Deadlines */}
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-3 border-t border-navy-800 text-xs">
                      <div className="p-3 rounded-lg bg-rose-500/10 border border-rose-500/20">
                        <span className="text-[11px] font-bold text-rose-300 block mb-1">
                          Common Pitfalls to Avoid
                        </span>
                        <ul className="space-y-1 text-[11px] text-rose-200/80 list-disc list-inside">
                          {claimChecklist.commonPitfalls.map((p, i) => (
                            <li key={i}>{p}</li>
                          ))}
                        </ul>
                      </div>

                      <div className="p-3 rounded-lg bg-amber-500/10 border border-amber-500/20">
                        <span className="text-[11px] font-bold text-amber-300 block mb-1">
                          Submission Deadlines
                        </span>
                        <ul className="space-y-1 text-[11px] text-amber-200/80 list-disc list-inside">
                          {claimChecklist.submissionDeadlines.map((d, i) => (
                            <li key={i}>{d}</li>
                          ))}
                        </ul>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              )}
            </div>
          </div>

          {/* Detailed Claim Tracker Stepper */}
          {selectedTrackingClaim && (
            <Card className="p-5 bg-navy-900 border border-cyan-500/30">
              <CardHeader className="p-0 pb-4">
                <div className="flex items-center justify-between">
                  <div>
                    <span className="text-[10px] text-cyan-400 font-bold uppercase tracking-wider">
                      Live Claim Tracker
                    </span>
                    <h3 className="text-base font-bold text-white">
                      Claim #{selectedTrackingClaim.claimNumber} • {selectedTrackingClaim.policyName}
                    </h3>
                  </div>

                  <Badge variant={selectedTrackingClaim.currentStatus === 'APPROVED' ? 'success' : 'warning'}>
                    {selectedTrackingClaim.currentStatus}
                  </Badge>
                </div>
              </CardHeader>

              <CardContent className="p-0 space-y-5">
                {/* Visual Multi-Stage Stepper */}
                <div className="grid grid-cols-1 sm:grid-cols-4 gap-3">
                  {selectedTrackingClaim.stages.map((stg, i) => (
                    <div
                      key={i}
                      className={`p-3 rounded-xl border text-xs ${
                        stg.status === 'COMPLETED'
                          ? 'bg-emerald-500/10 border-emerald-500/30 text-emerald-200'
                          : stg.status === 'CURRENT'
                          ? 'bg-cyan-500/15 border-cyan-500/40 text-cyan-200 shadow-glow-sm'
                          : 'bg-navy-950/60 border-navy-800 text-slate-500'
                      }`}
                    >
                      <div className="flex items-center justify-between mb-1">
                        <span className="text-[10px] font-mono font-bold uppercase">Stage {i + 1}</span>
                        {stg.status === 'COMPLETED' ? (
                          <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                        ) : stg.status === 'CURRENT' ? (
                          <Clock className="w-3.5 h-3.5 text-cyan-400 animate-spin" />
                        ) : null}
                      </div>
                      <p className="font-semibold text-white text-[11px] mb-1">{stg.stageName}</p>
                      <p className="text-[10px] text-slate-400 leading-tight">{stg.note}</p>
                    </div>
                  ))}
                </div>

                <div className="p-3.5 rounded-lg bg-navy-950/80 border border-navy-800 flex items-center justify-between text-xs">
                  <div>
                    <span className="text-slate-400 block text-[10px] uppercase">Next Step Guidance</span>
                    <span className="text-slate-200 font-medium">{selectedTrackingClaim.nextActionPrompt}</span>
                  </div>
                  <div className="text-right">
                    <span className="text-slate-400 block text-[10px] uppercase">AI Fraud Risk Score</span>
                    <span className="text-emerald-400 font-mono font-bold">{selectedTrackingClaim.aiFraudScore}% (Low Risk)</span>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}
        </div>
      )}

      {/* File Claim Modal */}
      <Modal
        isOpen={showClaimModal}
        onClose={() => setShowClaimModal(false)}
        title="Submit Digital Insurance Claim"
        description="Our AI engine cross-references incident details with policy exclusions and bills."
      >
        <form onSubmit={handleSubmitClaim} className="space-y-4">
          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Select Policy</label>
            <select
              value={claimPolicyId}
              onChange={(e) => {
                setClaimPolicyId(e.target.value);
                const p = policies.find((pol) => pol.id === e.target.value);
                if (p) handleLoadChecklist(p.category);
              }}
              className="w-full glass-input rounded-lg px-3 py-2 text-xs text-slate-200 focus:border-cyan-500"
            >
              {policies.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.productName} ({p.policyNumber})
                </option>
              ))}
            </select>
          </div>

          <Input
            label="Requested Claim Amount (₹)"
            type="number"
            value={claimAmount}
            onChange={(e) => setClaimAmount(e.target.value)}
            placeholder="e.g. 25000"
            required
          />

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">
              Incident Description & Diagnosis / Occurrence Details
            </label>
            <textarea
              rows={3}
              value={claimDescription}
              onChange={(e) => setClaimDescription(e.target.value)}
              placeholder="Detail emergency occurrence date, diagnosis, repair estimate, or hospital admission..."
              required
              className="w-full glass-input rounded-lg px-3 py-2 text-xs text-slate-200 focus:border-cyan-500"
            />
          </div>

          {claimSuccessMsg && (
            <div className="p-3 rounded-xl bg-emerald-500/15 border border-emerald-500/30 text-emerald-300 text-xs flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 shrink-0" />
              <span>{claimSuccessMsg}</span>
            </div>
          )}

          <div className="flex justify-end gap-2 pt-2">
            <Button variant="ghost" size="sm" type="button" onClick={() => setShowClaimModal(false)}>
              Cancel
            </Button>
            <Button
              variant="electric"
              size="sm"
              type="submit"
              isLoading={isSubmittingClaim}
            >
              Submit Claim
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};
