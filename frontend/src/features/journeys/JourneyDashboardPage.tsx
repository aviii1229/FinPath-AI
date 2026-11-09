import React, { useState, useEffect } from 'react';
import {
  Compass,
  Play,
  Pause,
  CheckCircle2,
  AlertCircle,
  XCircle,
  ArrowRight,
  Shield,
  Landmark,
  Coins,
  Sparkles,
  Bot,
  RefreshCw,
  PlusCircle,
  FileText,
  Clock,
  Save,
  Check,
  X,
  ChevronRight,
  Info
} from 'lucide-react';
import {
  journeyApi,
  UserJourney,
  JourneyStats,
  JourneyRecommendation,
  JourneyTemplate,
  JourneyStepRecord
} from '@/core/api/journeyApi';
import { Card } from '@/shared/components/ui/Card';
import { Button } from '@/shared/components/ui/Button';
import { Badge } from '@/shared/components/ui/Badge';
import { LoadingSpinner } from '@/shared/components/ui/LoadingSpinner';

const MOCK_FALLBACK_STATS: JourneyStats = {
  totalJourneys: 3,
  activeJourneys: 2,
  completedJourneys: 1,
  pausedJourneys: 0,
  cancelledJourneys: 0,
  averageProgressPercentage: 62,
  domainDistribution: { INSURANCE: 1, LENDING: 1, FINTECH: 1 },
};

const MOCK_FALLBACK_JOURNEYS: UserJourney[] = [
  {
    id: 'mock-j-1',
    journeyType: 'INSURANCE_APPLICATION_JOURNEY',
    journeyDomain: 'INSURANCE',
    title: 'Comprehensive Insurance Discovery & Application',
    description: 'End-to-end guided workflow from health/life insurance discovery through requirement collection, comparison, KYC verification, and policy issuance.',
    status: 'IN_PROGRESS',
    currentStepKey: 'POLICY_COMPARISON',
    currentStepIndex: 2,
    totalSteps: 6,
    progressPercentage: 33,
    journeyData: {
      INSURANCE_DISCOVERY: { category: 'HEALTH_FAMILY_FLOATER', targetBudget: '₹25,000/yr' },
      REQUIREMENT_COLLECTION: { proposerAge: 32, sumInsuredInr: '₹15,00,000', dependentsCount: 3 },
    },
    simulated: true,
    version: 2,
    nextRecommendedAction: 'Complete Step 3: Multi-Insurer Policy Matrix Comparison',
    pendingRequiredDocuments: ['IDENTITY_PROOF', 'ADDRESS_PROOF'],
    createdAt: '2026-08-10T09:00:00Z',
    updatedAt: '2026-08-10T14:30:00Z',
    steps: [
      {
        id: 's-1',
        stepKey: 'INSURANCE_DISCOVERY',
        stepOrder: 1,
        title: 'Insurance Discovery & Plan Selection',
        description: 'Identify the optimal insurance category matching your financial life stage.',
        status: 'COMPLETED',
        stepData: { category: 'HEALTH_FAMILY_FLOATER' },
        requiredDocuments: [],
        aiGuidance: 'Family floater plans offer shared sum insured covering spouse and children with maximum tax deduction.',
        completedAt: '2026-08-10T09:15:00Z',
        updatedAt: '2026-08-10T09:15:00Z',
      },
      {
        id: 's-2',
        stepKey: 'REQUIREMENT_COLLECTION',
        stepOrder: 2,
        title: 'Health & Financial Requirement Profiling',
        description: 'Capture age of insured members, city tier, and pre-existing declarations.',
        status: 'COMPLETED',
        stepData: { proposerAge: 32, sumInsuredInr: '₹15,00,000' },
        requiredDocuments: [],
        aiGuidance: 'Transparent health declarations protect against claim rejection under IRDAI standard guidelines.',
        completedAt: '2026-08-10T11:00:00Z',
        updatedAt: '2026-08-10T11:00:00Z',
      },
      {
        id: 's-3',
        stepKey: 'POLICY_COMPARISON',
        stepOrder: 3,
        title: 'Multi-Insurer Policy Matrix Comparison',
        description: 'Evaluate room rent limits, cashless hospital network, and premiums in INR (₹).',
        status: 'IN_PROGRESS',
        requiredDocuments: [],
        aiGuidance: 'Ensure zero co-pay and zero room-rent sub-limits for comprehensive hospitalization protection.',
        updatedAt: '2026-08-10T14:30:00Z',
      },
      {
        id: 's-4',
        stepKey: 'DOCUMENT_PREPARATION',
        stepOrder: 4,
        title: 'Document Preparation & Verification',
        description: 'Prepare and verify KYC identity proof and prior medical records in Document Vault.',
        status: 'PENDING',
        requiredDocuments: ['IDENTITY_PROOF', 'ADDRESS_PROOF'],
        aiGuidance: 'Ensure PAN card name matches insurer proposal records for instant e-KYC approval.',
        updatedAt: '2026-08-10T09:00:00Z',
      },
      {
        id: 's-5',
        stepKey: 'APPLICATION_PREPARATION',
        stepOrder: 5,
        title: 'Nominee Designation & Proposal Review',
        description: 'Specify nominee designations and review underwriting terms.',
        status: 'PENDING',
        requiredDocuments: [],
        aiGuidance: 'Explicit user confirmation required before submitting simulated proposal.',
        updatedAt: '2026-08-10T09:00:00Z',
      },
      {
        id: 's-6',
        stepKey: 'APPLICATION_TRACKING',
        stepOrder: 6,
        title: 'Proposal Submission & Underwriting Tracking',
        description: 'Track real-time underwriting milestones and digital health e-card delivery.',
        status: 'PENDING',
        requiredDocuments: [],
        aiGuidance: 'Simulated submission with regulatory compliance assurance.',
        updatedAt: '2026-08-10T09:00:00Z',
      },
    ],
  },
  {
    id: 'mock-j-2',
    journeyType: 'LENDING_APPLICATION_JOURNEY',
    journeyDomain: 'LENDING',
    title: 'Retail Loan Discovery, Sanction & Tracking',
    description: 'Guided credit journey: loan discovery, FOIR assessment, EMI simulation, multi-lender comparison, and paperless sanction tracking.',
    status: 'IN_PROGRESS',
    currentStepKey: 'EMI_CALCULATION',
    currentStepIndex: 2,
    totalSteps: 7,
    progressPercentage: 28,
    journeyData: {
      LOAN_DISCOVERY: { loanType: 'HOME_LOAN', requestedAmountInr: '₹45,00,000' },
      FINANCIAL_INFO_COLLECTION: { monthlyIncomeInr: '₹1,25,000', employerType: 'IT_SERVICES' },
    },
    simulated: true,
    version: 2,
    nextRecommendedAction: 'Complete Step 3: Interactive EMI & Amortization Modeling',
    pendingRequiredDocuments: ['IDENTITY_PROOF', 'SALARY_SLIP', 'BANK_STATEMENT'],
    createdAt: '2026-08-08T10:00:00Z',
    updatedAt: '2026-08-09T16:00:00Z',
    steps: [],
  },
];

const MOCK_FALLBACK_RECS: JourneyRecommendation[] = [
  {
    journeyType: 'FINTECH_WEALTH_JOURNEY',
    domain: 'FINTECH',
    title: 'Wealth Discipline & Financial Health Journey',
    rationale: 'Build a predictable monthly surplus and emergency reserve with structured 50-30-20 budget rules.',
    estimatedMinutes: 12,
    suggestedFirstStep: 'EXPENSE_ANALYSIS',
    priorityOrder: 1,
  },
];

export const JourneyDashboardPage: React.FC = () => {
  const [journeys, setJourneys] = useState<UserJourney[]>([]);
  const [stats, setStats] = useState<JourneyStats | null>(null);
  const [recommendations, setRecommendations] = useState<JourneyRecommendation[]>([]);
  const [templates, setTemplates] = useState<JourneyTemplate[]>([]);
  const [loading, setLoading] = useState(true);

  // Filters
  const [selectedStatus, setSelectedStatus] = useState<string>('ALL');
  const [selectedDomain, setSelectedDomain] = useState<string>('ALL');

  // Active Journey Drawer / Stepper Modal
  const [activeJourney, setActiveJourney] = useState<UserJourney | null>(null);
  const [activeStepRecord, setActiveStepRecord] = useState<JourneyStepRecord | null>(null);
  const [stepFormData, setStepFormData] = useState<Record<string, any>>({});
  const [savingStep, setSavingStep] = useState(false);
  const [completingStep, setCompletingStep] = useState(false);
  const [pausingJourney, setPausingJourney] = useState(false);

  // New Journey Launch Dialog
  const [showLaunchModal, setShowLaunchModal] = useState(false);
  const [launching, setLaunching] = useState(false);

  useEffect(() => {
    loadDashboardData();
  }, [selectedStatus, selectedDomain]);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const [journeysData, statsData, recsData, templatesData] = await Promise.all([
        journeyApi.getUserJourneys({
          status: selectedStatus,
          domain: selectedDomain,
        }),
        journeyApi.getJourneyStats(),
        journeyApi.getRecommendations(),
        journeyApi.getTemplates(),
      ]);
      setJourneys(journeysData);
      setStats(statsData);
      setRecommendations(recsData);
      setTemplates(templatesData);
    } catch (err: any) {
      // Fallback for offline / mock testing
      setJourneys(MOCK_FALLBACK_JOURNEYS);
      setStats(MOCK_FALLBACK_STATS);
      setRecommendations(MOCK_FALLBACK_RECS);
    } finally {
      setLoading(false);
    }
  };

  const handleStartJourney = async (journeyType: string) => {
    try {
      setLaunching(true);
      const newJourney = await journeyApi.startJourney({ journeyType });
      setJourneys(prev => [newJourney, ...prev]);
      setShowLaunchModal(false);
      openJourneyModal(newJourney);
      const updatedStats = await journeyApi.getJourneyStats().catch(() => MOCK_FALLBACK_STATS);
      setStats(updatedStats);
    } catch (err: any) {
      alert(`Could not start journey: ${err.message}`);
    } finally {
      setLaunching(false);
    }
  };

  const openJourneyModal = (journey: UserJourney) => {
    setActiveJourney(journey);
    const inProgressStep = journey.steps.find(s => s.status === 'IN_PROGRESS') || journey.steps[0];
    if (inProgressStep) {
      setActiveStepRecord(inProgressStep);
      setStepFormData(inProgressStep.stepData || {});
    }
  };

  const selectStepInModal = (step: JourneyStepRecord) => {
    setActiveStepRecord(step);
    setStepFormData(step.stepData || {});
  };

  const handleSaveStepDraft = async () => {
    if (!activeJourney || !activeStepRecord) return;
    try {
      setSavingStep(true);
      const updated = await journeyApi.updateStepData(activeJourney.id, {
        stepKey: activeStepRecord.stepKey,
        stepData: stepFormData,
        expectedVersion: activeJourney.version,
      });
      setActiveJourney(updated);
      setJourneys(prev => prev.map(j => (j.id === updated.id ? updated : j)));
      alert('Draft step information saved successfully.');
    } catch (err: any) {
      alert(`Save failed: ${err.message}`);
    } finally {
      setSavingStep(false);
    }
  };

  const handleCompleteStep = async () => {
    if (!activeJourney || !activeStepRecord) return;
    try {
      setCompletingStep(true);
      const updated = await journeyApi.completeStep(activeJourney.id, {
        stepKey: activeStepRecord.stepKey,
        stepData: stepFormData,
        userConfirmationApproved: true,
        expectedVersion: activeJourney.version,
      });
      setActiveJourney(updated);
      setJourneys(prev => prev.map(j => (j.id === updated.id ? updated : j)));

      const next = updated.steps.find(s => s.status === 'IN_PROGRESS') || updated.steps[updated.steps.length - 1];
      setActiveStepRecord(next);
      setStepFormData(next.stepData || {});

      const updatedStats = await journeyApi.getJourneyStats().catch(() => MOCK_FALLBACK_STATS);
      setStats(updatedStats);
    } catch (err: any) {
      alert(`Step completion error: ${err.message}`);
    } finally {
      setCompletingStep(false);
    }
  };

  const handlePauseJourney = async (journey: UserJourney) => {
    try {
      setPausingJourney(true);
      const updated = await journeyApi.pauseJourney(journey.id, journey.version);
      setJourneys(prev => prev.map(j => (j.id === updated.id ? updated : j)));
      if (activeJourney?.id === journey.id) setActiveJourney(updated);
      const updatedStats = await journeyApi.getJourneyStats().catch(() => MOCK_FALLBACK_STATS);
      setStats(updatedStats);
    } catch (err: any) {
      alert(`Failed to pause journey: ${err.message}`);
    } finally {
      setPausingJourney(false);
    }
  };

  const handleResumeJourney = async (journey: UserJourney) => {
    try {
      const updated = await journeyApi.resumeJourney(journey.id, journey.version);
      setJourneys(prev => prev.map(j => (j.id === updated.id ? updated : j)));
      if (activeJourney?.id === journey.id) setActiveJourney(updated);
      const updatedStats = await journeyApi.getJourneyStats().catch(() => MOCK_FALLBACK_STATS);
      setStats(updatedStats);
    } catch (err: any) {
      alert(`Failed to resume journey: ${err.message}`);
    }
  };

  const handleCancelJourney = async (journey: UserJourney) => {
    const reason = prompt('Please specify a reason for cancelling this financial journey:');
    if (reason === null) return;
    try {
      const updated = await journeyApi.cancelJourney(journey.id, {
        reason: reason || 'Cancelled by customer',
        expectedVersion: journey.version,
      });
      setJourneys(prev => prev.map(j => (j.id === updated.id ? updated : j)));
      if (activeJourney?.id === journey.id) setActiveJourney(null);
      const updatedStats = await journeyApi.getJourneyStats().catch(() => MOCK_FALLBACK_STATS);
      setStats(updatedStats);
    } catch (err: any) {
      alert(`Cancellation failed: ${err.message}`);
    }
  };

  return (
    <div className="space-y-6 pb-12">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-bold text-white tracking-tight">Customer Financial Journey Management</h1>
            <Badge variant="electric" className="px-2 py-0.5 text-xs">
              Phase 06 Automation
            </Badge>
          </div>
          <p className="text-slate-400 text-sm mt-1">
            Transform financial recommendations into trackable, state-machine validated workflows with human confirmation
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Badge variant="cyan" className="text-xs flex items-center gap-1.5 py-1 px-3">
            <Shield className="w-3.5 h-3.5 text-cyan-400" />
            Human Approval Enforced
          </Badge>
          <Button
            onClick={() => setShowLaunchModal(true)}
            className="bg-electric-600 hover:bg-electric-500 text-white font-medium flex items-center gap-2 shadow-glow-sm text-xs"
          >
            <PlusCircle className="w-4 h-4" />
            Launch New Journey
          </Button>
        </div>
      </div>

      {/* KPI Overview Cards */}
      {stats && (
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
          <Card className="bg-navy-900/90 border-navy-700/60 p-4">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Active Workflows</span>
              <Play className="w-4 h-4 text-electric-400" />
            </div>
            <div className="text-2xl font-bold text-white mt-2">{stats.activeJourneys}</div>
            <div className="text-xs text-electric-300 mt-1">Currently in progress</div>
          </Card>

          <Card className="bg-navy-900/90 border-navy-700/60 p-4">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Completed Journeys</span>
              <CheckCircle2 className="w-4 h-4 text-emerald-400" />
            </div>
            <div className="text-2xl font-bold text-emerald-400 mt-2">{stats.completedJourneys}</div>
            <div className="text-xs text-slate-500 mt-1">Underwritten & tracked</div>
          </Card>

          <Card className="bg-navy-900/90 border-navy-700/60 p-4">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Paused / In Review</span>
              <Pause className="w-4 h-4 text-amber-400" />
            </div>
            <div className="text-2xl font-bold text-amber-400 mt-2">{stats.pausedJourneys}</div>
            <div className="text-xs text-slate-500 mt-1">Ready to resume anytime</div>
          </Card>

          <Card className="bg-navy-900/90 border-navy-700/60 p-4">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-400 uppercase tracking-wider">Average Progress</span>
              <Sparkles className="w-4 h-4 text-cyan-400" />
            </div>
            <div className="text-2xl font-bold text-cyan-400 mt-2">{stats.averageProgressPercentage}%</div>
            <div className="w-full bg-navy-950 h-1.5 rounded-full overflow-hidden mt-2">
              <div
                className="bg-gradient-to-r from-cyan-500 to-electric-500 h-full rounded-full"
                style={{ width: `${stats.averageProgressPercentage}%` }}
              />
            </div>
          </Card>
        </div>
      )}

      {/* AI Recommendations Banner */}
      {recommendations.length > 0 && (
        <Card className="bg-gradient-to-r from-electric-950/40 via-navy-900 to-navy-950 border border-electric-500/30 p-5">
          <div className="flex items-start justify-between gap-4">
            <div className="flex items-start gap-3">
              <div className="p-2 rounded-xl bg-electric-600/20 text-electric-400 mt-0.5">
                <Bot className="w-5 h-5" />
              </div>
              <div>
                <span className="text-xs font-bold uppercase tracking-wider text-electric-400">AI Suggested Journey</span>
                <h3 className="text-base font-bold text-white mt-0.5">{recommendations[0].title}</h3>
                <p className="text-xs text-slate-400 mt-1 max-w-2xl">{recommendations[0].rationale}</p>
                <div className="flex items-center gap-3 text-[11px] text-slate-500 mt-2">
                  <span>⏱️ Est. Completion: {recommendations[0].estimatedMinutes} mins</span>
                  <span>•</span>
                  <span>🎯 Initial Step: {recommendations[0].suggestedFirstStep}</span>
                </div>
              </div>
            </div>

            <Button
              onClick={() => handleStartJourney(recommendations[0].journeyType)}
              disabled={launching}
              className="bg-electric-600 hover:bg-electric-500 text-white shrink-0 text-xs flex items-center gap-1.5 shadow-glow-sm"
            >
              <span>Launch Journey</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </Button>
          </div>
        </Card>
      )}

      {/* Filter Toolbar */}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-3">
        <div className="flex items-center gap-2">
          {['ALL', 'IN_PROGRESS', 'PAUSED', 'COMPLETED'].map(st => (
            <button
              key={st}
              onClick={() => setSelectedStatus(st)}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all ${
                selectedStatus === st
                  ? 'bg-electric-600 text-white shadow-glow-sm'
                  : 'bg-navy-900 text-slate-400 hover:text-white border border-navy-700/80'
              }`}
            >
              {st === 'ALL' ? 'All Journeys' : st === 'IN_PROGRESS' ? 'Active' : st === 'PAUSED' ? 'Paused' : 'Completed'}
            </button>
          ))}
        </div>

        <div className="flex items-center gap-2 w-full sm:w-auto">
          <select
            value={selectedDomain}
            onChange={(e) => setSelectedDomain(e.target.value)}
            className="bg-navy-900 border border-navy-700 text-xs text-white rounded-xl px-3 py-2"
          >
            <option value="ALL">All Domains</option>
            <option value="INSURANCE">Insurance Workflows</option>
            <option value="LENDING">Lending Workflows</option>
            <option value="FINTECH">Fintech & PFM Workflows</option>
          </select>

          <Button variant="outline" size="sm" onClick={loadDashboardData}>
            <RefreshCw className="w-3.5 h-3.5" />
          </Button>
        </div>
      </div>

      {/* Journey Cards Grid */}
      {loading ? (
        <div className="py-16 text-center">
          <LoadingSpinner size="lg" text="Retrieving Active Financial Journeys..." />
        </div>
      ) : journeys.length === 0 ? (
        <Card className="bg-navy-900/60 border-navy-800 p-12 text-center">
          <Compass className="w-12 h-12 text-slate-600 mx-auto mb-3" />
          <h3 className="text-base font-semibold text-white">No journeys found</h3>
          <p className="text-xs text-slate-400 mt-1 max-w-sm mx-auto">
            You currently have no active or completed journeys matching the selected filters.
          </p>
          <Button
            onClick={() => setShowLaunchModal(true)}
            className="mt-4 bg-electric-600 hover:bg-electric-500 text-white text-xs"
          >
            Start a Financial Journey
          </Button>
        </Card>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {journeys.map((j) => (
            <Card
              key={j.id}
              onClick={() => openJourneyModal(j)}
              className="bg-navy-900/90 border-navy-700/60 hover:border-electric-500/50 p-6 rounded-2xl cursor-pointer transition-all hover:shadow-card-hover group flex flex-col justify-between"
            >
              <div>
                {/* Header Row */}
                <div className="flex items-start justify-between gap-3">
                  <div className="flex items-center gap-2">
                    <Badge
                      variant={
                        j.journeyDomain === 'INSURANCE' ? 'electric' :
                        j.journeyDomain === 'LENDING' ? 'cyan' : 'success'
                      }
                      className="text-[11px] font-semibold"
                    >
                      {j.journeyDomain}
                    </Badge>

                    {j.simulated && (
                      <span className="text-[10px] font-mono font-semibold px-2 py-0.5 rounded-full bg-slate-800 text-slate-300 border border-slate-700">
                        Simulation Mode
                      </span>
                    )}
                  </div>

                  <Badge
                    variant={
                      j.status === 'COMPLETED' ? 'success' :
                      j.status === 'PAUSED' ? 'warning' :
                      j.status === 'CANCELLED' ? 'danger' : 'electric'
                    }
                    className="text-xs"
                  >
                    {j.status}
                  </Badge>
                </div>

                {/* Title & Description */}
                <h3 className="text-base font-bold text-white mt-3 group-hover:text-electric-300 transition-colors">
                  {j.title}
                </h3>
                <p className="text-xs text-slate-400 mt-1 line-clamp-2">{j.description}</p>

                {/* Progress Bar & Current Step */}
                <div className="mt-4 space-y-1.5">
                  <div className="flex justify-between items-center text-xs">
                    <span className="text-slate-400 text-[11px]">
                      Step {j.currentStepIndex + 1} of {j.totalSteps}: <strong className="text-white">{j.currentStepKey}</strong>
                    </span>
                    <span className="font-bold text-cyan-400">{j.progressPercentage}%</span>
                  </div>
                  <div className="w-full bg-navy-950 h-2 rounded-full overflow-hidden">
                    <div
                      className="bg-gradient-to-r from-electric-500 to-cyan-400 h-full rounded-full transition-all"
                      style={{ width: `${j.progressPercentage}%` }}
                    />
                  </div>
                </div>

                {/* Next Recommended Action */}
                <div className="mt-4 p-3 rounded-xl bg-navy-950/80 border border-navy-800 text-xs space-y-1">
                  <div className="text-[10px] uppercase font-bold text-slate-500 tracking-wider">Next Recommended Step</div>
                  <div className="font-semibold text-slate-200 flex items-center gap-1.5">
                    <ArrowRight className="w-3.5 h-3.5 text-electric-400 shrink-0" />
                    <span>{j.nextRecommendedAction}</span>
                  </div>
                </div>

                {/* Pending Documents if any */}
                {j.pendingRequiredDocuments && j.pendingRequiredDocuments.length > 0 && (
                  <div className="mt-2.5 flex items-center gap-1.5 text-[11px] text-amber-300 font-medium">
                    <FileText className="w-3.5 h-3.5 text-amber-400" />
                    <span>Checklist Pending: {j.pendingRequiredDocuments.join(', ')}</span>
                  </div>
                )}
              </div>

              {/* Bottom Actions */}
              <div className="mt-5 pt-4 border-t border-navy-800/80 flex items-center justify-between">
                <span className="text-[11px] text-slate-500">
                  Updated: {new Date(j.updatedAt).toLocaleDateString('en-IN')}
                </span>

                <div className="flex items-center gap-2" onClick={(e) => e.stopPropagation()}>
                  {j.status === 'IN_PROGRESS' ? (
                    <button
                      onClick={() => handlePauseJourney(j)}
                      disabled={pausingJourney}
                      className="px-2.5 py-1 rounded-lg bg-navy-800 hover:bg-navy-700 text-slate-300 text-xs font-medium flex items-center gap-1"
                    >
                      <Pause className="w-3 h-3" /> Pause
                    </button>
                  ) : j.status === 'PAUSED' ? (
                    <button
                      onClick={() => handleResumeJourney(j)}
                      className="px-2.5 py-1 rounded-lg bg-electric-600 hover:bg-electric-500 text-white text-xs font-medium flex items-center gap-1"
                    >
                      <Play className="w-3 h-3" /> Resume
                    </button>
                  ) : null}

                  {j.status !== 'COMPLETED' && j.status !== 'CANCELLED' && (
                    <button
                      onClick={() => handleCancelJourney(j)}
                      className="px-2.5 py-1 rounded-lg bg-navy-800 hover:bg-red-500/20 text-slate-400 hover:text-red-400 text-xs font-medium"
                    >
                      Cancel
                    </button>
                  )}

                  <button
                    onClick={() => openJourneyModal(j)}
                    className="px-3 py-1 rounded-lg bg-electric-600 hover:bg-electric-500 text-white text-xs font-semibold flex items-center gap-1 shadow-glow-sm"
                  >
                    Manage <ChevronRight className="w-3 h-3" />
                  </button>
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}

      {/* STEP PROGRESSION WIZARD DRAWER / MODAL */}
      {activeJourney && (
        <div className="fixed inset-0 z-50 bg-navy-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-navy-900 border border-navy-700/80 rounded-2xl w-full max-w-4xl max-h-[92vh] flex flex-col p-6 shadow-2xl animate-scale-in">
            {/* Modal Header */}
            <div className="flex items-start justify-between border-b border-navy-800 pb-4">
              <div>
                <div className="flex items-center gap-2">
                  <Badge variant="electric" className="text-xs">{activeJourney.journeyDomain}</Badge>
                  <span className="text-xs font-mono text-slate-400">Version #{activeJourney.version}</span>
                </div>
                <h2 className="text-lg font-bold text-white mt-1">{activeJourney.title}</h2>
                <p className="text-xs text-slate-400 mt-0.5">{activeJourney.description}</p>
              </div>

              <button
                onClick={() => setActiveJourney(null)}
                className="p-1.5 rounded-lg hover:bg-navy-800 text-slate-400 hover:text-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Stepper + Details Layout */}
            <div className="flex-1 overflow-y-auto py-5 flex flex-col md:flex-row gap-6">
              {/* Left Column: Vertical Stepper */}
              <div className="w-full md:w-72 shrink-0 space-y-2 border-r border-navy-800 pr-4">
                <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-3">Workflow Stages</h4>
                {activeJourney.steps && activeJourney.steps.map((step) => {
                  const isSelected = activeStepRecord?.stepKey === step.stepKey;
                  return (
                    <div
                      key={step.id}
                      onClick={() => selectStepInModal(step)}
                      className={`p-3 rounded-xl border text-xs cursor-pointer transition-all flex items-center justify-between ${
                        isSelected
                          ? 'bg-electric-600/20 border-electric-500 text-white shadow-glow-sm'
                          : step.status === 'COMPLETED'
                          ? 'bg-navy-950/60 border-emerald-500/30 text-slate-300'
                          : 'bg-navy-950/40 border-navy-800 text-slate-400 hover:text-white'
                      }`}
                    >
                      <div className="flex items-center gap-2.5">
                        {step.status === 'COMPLETED' ? (
                          <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0" />
                        ) : step.status === 'IN_PROGRESS' ? (
                          <div className="w-4 h-4 rounded-full border-2 border-electric-400 flex items-center justify-center">
                            <div className="w-1.5 h-1.5 rounded-full bg-electric-400 animate-pulse" />
                          </div>
                        ) : (
                          <Clock className="w-4 h-4 text-slate-500 shrink-0" />
                        )}
                        <span className="font-medium truncate max-w-[160px]">{step.stepOrder}. {step.title}</span>
                      </div>
                      <ChevronRight className="w-3 h-3 text-slate-500" />
                    </div>
                  );
                })}
              </div>

              {/* Right Column: Step Interaction Panel */}
              <div className="flex-1 space-y-5">
                {activeStepRecord ? (
                  <>
                    <div>
                      <div className="flex items-center gap-2">
                        <Badge
                          variant={
                            activeStepRecord.status === 'COMPLETED' ? 'success' :
                            activeStepRecord.status === 'IN_PROGRESS' ? 'electric' : 'default'
                          }
                          className="text-xs"
                        >
                          Step {activeStepRecord.stepOrder} • {activeStepRecord.status}
                        </Badge>
                      </div>
                      <h3 className="text-base font-bold text-white mt-1.5">{activeStepRecord.title}</h3>
                      <p className="text-xs text-slate-400 mt-1">{activeStepRecord.description}</p>
                    </div>

                    {/* AI Guidance Box */}
                    {activeStepRecord.aiGuidance && (
                      <div className="p-4 rounded-xl bg-gradient-to-r from-electric-950/40 to-navy-950 border border-electric-500/30 text-xs space-y-1">
                        <div className="flex items-center gap-2 font-bold text-electric-300">
                          <Bot className="w-4 h-4 text-electric-400" />
                          FinPath AI Assistant Guidance:
                        </div>
                        <p className="text-slate-300 leading-relaxed pl-6">{activeStepRecord.aiGuidance}</p>
                      </div>
                    )}

                    {/* Required Documents Notice */}
                    {activeStepRecord.requiredDocuments && activeStepRecord.requiredDocuments.length > 0 && (
                      <div className="p-3 rounded-xl bg-navy-950 border border-navy-800 text-xs flex items-center justify-between">
                        <div className="flex items-center gap-2 text-slate-300">
                          <FileText className="w-4 h-4 text-cyan-400" />
                          <span>Mandatory Documents: <strong>{activeStepRecord.requiredDocuments.join(', ')}</strong></span>
                        </div>
                        <a
                          href="/documents"
                          target="_blank"
                          rel="noreferrer"
                          className="text-xs font-semibold text-cyan-400 hover:text-cyan-300 flex items-center gap-1"
                        >
                          Verify in Vault <ArrowRight className="w-3 h-3" />
                        </a>
                      </div>
                    )}

                    {/* Interactive Step Parameter Input Fields */}
                    <div className="p-4 rounded-xl bg-navy-950 border border-navy-800 space-y-3">
                      <h4 className="text-xs font-bold text-slate-300 uppercase tracking-wider">
                        Submitted Step Information & Parameters (INR - ₹)
                      </h4>

                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                        <div>
                          <label className="text-xs text-slate-400 font-medium block mb-1">Primary Choice / Tier</label>
                          <input
                            type="text"
                            value={stepFormData['primarySelection'] || stepFormData['category'] || stepFormData['loanType'] || ''}
                            onChange={(e) => setStepFormData({ ...stepFormData, primarySelection: e.target.value })}
                            placeholder="e.g., Comprehensive Platinum Cover or Retail Home Loan"
                            className="w-full bg-navy-900 border border-navy-700 text-xs text-white rounded-lg px-3 py-2 focus:border-electric-500"
                          />
                        </div>

                        <div>
                          <label className="text-xs text-slate-400 font-medium block mb-1">Financial Amount (INR - ₹)</label>
                          <input
                            type="text"
                            value={stepFormData['amountInr'] || stepFormData['requestedAmountInr'] || stepFormData['sumInsuredInr'] || ''}
                            onChange={(e) => setStepFormData({ ...stepFormData, amountInr: e.target.value })}
                            placeholder="e.g., ₹15,00,000"
                            className="w-full bg-navy-900 border border-navy-700 text-xs text-white rounded-lg px-3 py-2 focus:border-electric-500"
                          />
                        </div>

                        <div className="sm:col-span-2">
                          <label className="text-xs text-slate-400 font-medium block mb-1">Applicant Notes & Declaration</label>
                          <textarea
                            rows={2}
                            value={stepFormData['notes'] || ''}
                            onChange={(e) => setStepFormData({ ...stepFormData, notes: e.target.value })}
                            placeholder="Provide any additional underwriting details or instructions..."
                            className="w-full bg-navy-900 border border-navy-700 text-xs text-white rounded-lg px-3 py-2 focus:border-electric-500"
                          />
                        </div>
                      </div>
                    </div>

                    {/* Human Approval Notice */}
                    <div className="p-3 rounded-lg bg-navy-950 border border-navy-800 text-[11px] text-slate-400 flex items-center gap-2">
                      <Info className="w-4 h-4 text-cyan-400 shrink-0" />
                      <span>
                        <strong>Explicit User Approval:</strong> Advancing this step will record your confirmed input in PostgreSQL. All actions operate in simulated regulatory mode.
                      </span>
                    </div>

                    {/* Action Buttons */}
                    <div className="flex items-center justify-between pt-2">
                      <div className="flex items-center gap-2">
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={handleSaveStepDraft}
                          disabled={savingStep}
                          className="text-xs text-slate-300 border-navy-700 hover:text-white flex items-center gap-1.5"
                        >
                          <Save className="w-3.5 h-3.5" />
                          <span>{savingStep ? 'Saving...' : 'Save Draft'}</span>
                        </Button>
                      </div>

                      <div className="flex items-center gap-2">
                        <Button
                          size="sm"
                          onClick={handleCompleteStep}
                          disabled={completingStep || activeJourney.status === 'PAUSED' || activeStepRecord.status === 'COMPLETED'}
                          className="bg-electric-600 hover:bg-electric-500 text-white font-semibold text-xs flex items-center gap-1.5 shadow-glow-sm"
                        >
                          <Check className="w-3.5 h-3.5" />
                          <span>{completingStep ? 'Advancing Workflow...' : 'Confirm & Complete Step'}</span>
                        </Button>
                      </div>
                    </div>
                  </>
                ) : null}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* LAUNCH NEW JOURNEY MODAL */}
      {showLaunchModal && (
        <div className="fixed inset-0 z-50 bg-navy-950/80 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-navy-900 border border-navy-700/80 rounded-2xl w-full max-w-xl p-6 shadow-2xl animate-scale-in space-y-5">
            <div className="flex items-start justify-between border-b border-navy-800 pb-3">
              <div>
                <h3 className="text-lg font-bold text-white">Start New Financial Journey</h3>
                <p className="text-xs text-slate-400 mt-0.5">Select a customer workflow template to initialize:</p>
              </div>
              <button
                onClick={() => setShowLaunchModal(false)}
                className="p-1 rounded-lg hover:bg-navy-800 text-slate-400 hover:text-white"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-3">
              {[
                {
                  type: 'INSURANCE_APPLICATION_JOURNEY',
                  domain: 'INSURANCE',
                  title: 'Comprehensive Insurance Discovery & Application',
                  desc: 'Guided health/life insurance discovery, requirement profiling, multi-insurer comparison, and KYC proposal review.',
                  icon: Shield,
                  color: 'electric',
                },
                {
                  type: 'LENDING_APPLICATION_JOURNEY',
                  domain: 'LENDING',
                  title: 'Retail Loan Discovery, Sanction & Tracking',
                  desc: 'Credit product discovery, FOIR assessment, interactive EMI calculation, and paperless loan sanction tracking.',
                  icon: Landmark,
                  color: 'cyan',
                },
                {
                  type: 'FINTECH_WEALTH_JOURNEY',
                  domain: 'FINTECH',
                  title: 'Wealth Discipline & Financial Health Journey',
                  desc: 'Expense analysis, recurring subscription audit, 50-30-20 budget creation, and emergency fund formulation.',
                  icon: Coins,
                  color: 'success',
                },
              ].map((tpl) => {
                const IconComponent = tpl.icon;
                return (
                  <div
                    key={tpl.type}
                    onClick={() => handleStartJourney(tpl.type)}
                    className="p-4 rounded-xl border border-navy-800 hover:border-electric-500/60 bg-navy-950/60 hover:bg-navy-950 cursor-pointer transition-all flex items-start justify-between group"
                  >
                    <div className="flex items-start gap-3">
                      <div className="p-2 rounded-xl bg-navy-900 border border-navy-800 text-electric-400 group-hover:scale-105 transition-transform">
                        <IconComponent className="w-5 h-5" />
                      </div>
                      <div>
                        <div className="flex items-center gap-2">
                          <span className="text-sm font-bold text-white group-hover:text-electric-300 transition-colors">
                            {tpl.title}
                          </span>
                          <Badge variant={tpl.color as any} className="text-[10px]">{tpl.domain}</Badge>
                        </div>
                        <p className="text-xs text-slate-400 mt-1 leading-relaxed">{tpl.desc}</p>
                      </div>
                    </div>
                    <ArrowRight className="w-4 h-4 text-slate-500 group-hover:text-electric-400 group-hover:translate-x-1 transition-all shrink-0 mt-1 ml-2" />
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default JourneyDashboardPage;
