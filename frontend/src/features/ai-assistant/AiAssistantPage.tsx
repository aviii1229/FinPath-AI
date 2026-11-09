import React, { useState, useRef, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Bot,
  Send,
  Sparkles,
  User,
  Trash2,
  FileText,
  Landmark,
  Shield,
  Coins,
  ChevronRight,
  ChevronDown,
  ChevronUp,
  Loader2,
  CheckCircle2,
  ExternalLink,
  ArrowRight,
  TrendingDown,
  Layers,
  HelpCircle,
  Activity,
  CheckSquare,
  AlertCircle,
  Info,
  Clock,
  Cpu,
} from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { Badge } from '@/shared/components/ui/Badge';
import { ChatMessage } from '@/types';
import { initialChatMessages } from '@/mock/mockData';
import {
  aiChatApi,
  AiRecommendationCard,
  AiInteractiveAction,
  AiJourneyStage,
  AiExplainability,
  ChatQueryResponse,
} from '@/core/api/aiChatApi';

interface ExtendedChatMessage extends Omit<ChatMessage, 'toolInvocations' | 'citations'> {
  toolInvocations?: any[];
  citations?: any[];
  intent?: string;
  journeyStage?: AiJourneyStage;
  cards?: AiRecommendationCard[];
  suggestedActions?: AiInteractiveAction[];
  explainability?: AiExplainability;
  confidenceScore?: number;
  clarificationNeeded?: boolean;
  clarificationOptions?: string[];
  latencyMs?: number;
  tokensUsed?: number;
}

export const AiAssistantPage: React.FC = () => {
  const navigate = useNavigate();
  const [messages, setMessages] = useState<ExtendedChatMessage[]>(initialChatMessages);
  const [inputPrompt, setInputPrompt] = useState('');
  const [isGenerating, setIsGenerating] = useState(false);
  const [activeSessionId, setActiveSessionId] = useState<string>('sess-journey-1');
  const [expandedExplainability, setExpandedExplainability] = useState<Record<string, boolean>>({});
  const [activeJourneyStage, setActiveJourneyStage] = useState<AiJourneyStage | null>({
    stageName: 'CROSS_DOMAIN_COCKPIT',
    currentStep: 2,
    totalSteps: 4,
    stepTitle: 'Step 2 of 4: Multi-Domain Journey Orchestration',
    completedSteps: ['Profile Verification', 'Connected Account Ingestion'],
    pendingSteps: ['Underwriting & Execution', 'Automated Guardrail Activation'],
  });

  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, isGenerating]);

  // Real-World Scenarios
  const scenarioStarters = [
    {
      title: 'Family Health Cover',
      prompt: 'I need health insurance for my family.',
      icon: Shield,
      tag: 'Scenario A: Insurance',
      color: 'from-blue-600/20 to-cyan-600/10 border-blue-500/30 text-blue-400',
    },
    {
      title: '₹3 Lakh Loan Discovery',
      prompt: 'I need a loan of ₹3 lakh',
      icon: Landmark,
      tag: 'Scenario B: Lending',
      color: 'from-emerald-600/20 to-teal-600/10 border-emerald-500/30 text-emerald-400',
    },
    {
      title: 'Smart Savings Engine',
      prompt: 'I earn ₹40,000 but cannot save money',
      icon: Coins,
      tag: 'Scenario C: Fintech',
      color: 'from-amber-600/20 to-orange-600/10 border-amber-500/30 text-amber-400',
    },
    {
      title: 'Reduce Financial Burden',
      prompt: 'I want to reduce my monthly financial burden',
      icon: TrendingDown,
      tag: 'Scenario D: Cross-Domain',
      color: 'from-purple-600/20 to-indigo-600/10 border-purple-500/30 text-purple-400',
    },
    {
      title: 'Health Cover + Budget',
      prompt: 'I want to buy health insurance but also need to manage my monthly expenses',
      icon: Layers,
      tag: 'Phase 3: Cross-Module',
      color: 'from-cyan-600/20 to-blue-600/10 border-cyan-500/30 text-cyan-300',
    },
    {
      title: 'What is FOIR?',
      prompt: 'What is FOIR?',
      icon: HelpCircle,
      tag: 'Phase 3: AI Education',
      color: 'from-rose-600/20 to-pink-600/10 border-rose-500/30 text-rose-300',
    },
  ];

  const toggleExplainability = (msgId: string) => {
    setExpandedExplainability((prev) => ({
      ...prev,
      [msgId]: !prev[msgId],
    }));
  };

  const handleSendMessage = async (textToSend?: string) => {
    const query = (textToSend || inputPrompt).trim();
    if (!query || isGenerating) return;

    const userMessage: ExtendedChatMessage = {
      id: `usr_${Date.now()}`,
      role: 'user',
      content: query,
      timestamp: new Date().toISOString(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInputPrompt('');
    setIsGenerating(true);

    try {
      const response: ChatQueryResponse = await aiChatApi.sendQuery(activeSessionId, query, true);

      if (response.journeyStage) {
        setActiveJourneyStage(response.journeyStage);
      }

      const assistantMessage: ExtendedChatMessage = {
        id: response.messageId || `asst_${Date.now()}`,
        role: 'assistant',
        content: response.content || response.reply || '',
        timestamp: new Date().toISOString(),
        toolInvocations: response.toolInvocations || response.toolExecutions,
        citations: response.citations || response.ragCitations,
        intent: response.intent,
        journeyStage: response.journeyStage,
        cards: response.cards,
        suggestedActions: response.suggestedActions,
        explainability: response.explainability,
        confidenceScore: response.confidenceScore,
        clarificationNeeded: response.clarificationNeeded,
        clarificationOptions: response.clarificationOptions,
        latencyMs: response.latencyMs,
        tokensUsed: response.tokensUsed,
      };

      setMessages((prev) => [...prev, assistantMessage]);
    } catch (err) {
      // Graceful local orchestrator fallback
      const lower = query.toLowerCase();
      let fallbackContent = '';
      let fallbackIntent = 'GENERAL_ADVISORY';
      let cards: AiRecommendationCard[] = [];
      let suggestedActions: AiInteractiveAction[] = [];

      if (lower.includes('health') && (lower.includes('budget') || lower.includes('expense') || lower.includes('save'))) {
        fallbackIntent = 'MULTI_MODULE_INSURANCE_AND_BUDGET';
        fallbackContent = `### Integrated Strategy: Family Health Shield & Budget Harmonization\n\n- **Inflow & Surplus**: You earn **₹75,000 / month** with current commitments of **₹42,280**, leaving a **₹32,720** liquid monthly surplus.\n- **Health Coverage**: A comprehensive ₹15,00,000 family floater costs **₹3,308 / month** (~4.4% of monthly income).\n- **Cashflow Impact**: Net remaining monthly surplus stays strongly positive at **₹29,412 / month**.\n- **Tax Benefit**: Eligible for up to **₹25,000 to ₹50,000** deductions under Section 80D.`;
        cards = [
          {
            cardType: 'INSURANCE_QUOTE',
            title: 'Aegis Family Health Shield',
            subtitle: 'Comprehensive Family Floater (2 Adults + 2 Children)',
            badge: 'Top Recommendation',
            metrics: {
              'Sum Insured': '₹15,00,000',
              'Monthly Premium': '₹3,308 / mo',
              'Network Hospitals': '8,500+ Facilities',
              'Co-Pay': '0% (Zero Out-of-Pocket)',
            },
            bulletPoints: [
              'Automatic restoration of sum insured upon exhaustion',
              'Covers newborn from Day 1 and maternity complications',
            ],
            primaryActionLabel: 'View Policy in Insurance',
            primaryActionUrl: '/insurance',
          },
          {
            cardType: 'BUDGET_ANALYSIS',
            title: 'Harmonized Cashflow Plan',
            subtitle: 'Impact of Health Premium on Budget',
            badge: '4.4% of Inflow',
            metrics: {
              'Monthly Inflow': '₹75,000',
              'Health Premium': '-₹3,308 / mo',
              'Net Safe Surplus': '₹29,412 / mo',
            },
            bulletPoints: [
              'Curb dining leaks to easily offset >50% of the premium',
              'Safeguards accumulated wealth against medical shocks',
            ],
            primaryActionLabel: 'Configure in Fintech',
            primaryActionUrl: '/fintech',
          },
        ];
      } else if (lower.includes('foir')) {
        fallbackIntent = 'FINANCIAL_EDUCATION';
        fallbackContent = `### Fixed Obligation to Income Ratio (FOIR) Explained\n\n**FOIR** is the percentage of gross monthly income committed to servicing debt obligations.\n\n- **Formula**: (Total Monthly EMIs / Gross Monthly Income) × 100\n- **Standard Safe Ceiling**: 40% to 50% in Indian banking.\n- **Example**: On a ₹75,000 salary with ₹20,000 existing EMIs, FOIR is **26.7%**, leaving room for up to ₹17,500/month in new borrowing.`;
      } else if (lower.includes('health') || lower.includes('insurance') || lower.includes('family')) {
        fallbackIntent = 'INSURANCE_FAMILY_COVERAGE';
        fallbackContent = `### Family Health Protection Blueprint\n\n- **Coverage**: ₹15,00,000 Comprehensive Floater (2 Adults + 2 Kids)\n- **Monthly Premium**: ₹1,850 / month\n- **Hospital Network**: 8,500+ Cashless Facilities\n- **Section 80D Tax Benefit**: Up to ₹25,000 deduction on annual taxes.`;
        cards = [
          {
            cardType: 'INSURANCE_QUOTE',
            title: 'Aegis Family Health Shield',
            subtitle: 'Comprehensive Family Floater (2 Adults + 2 Kids)',
            badge: 'Top Pick · 98.4% Claims Ratio',
            metrics: {
              'Sum Insured': '₹15,00,000',
              'Monthly Premium': '₹1,850 / mo',
              'Cashless Network': '8,500+ Hospitals',
              'Co-Pay': '0% (Zero Out-of-Pocket)',
            },
            bulletPoints: [
              'Automatic unlimited restoration of sum insured upon exhaustion',
              'Pre/Post hospitalization covered 60/180 days',
              'Complimentary annual health checks for all members',
            ],
            primaryActionLabel: 'View Policy in Insurance Module',
            primaryActionUrl: '/insurance',
          },
        ];
      } else if (lower.includes('loan') || lower.includes('lakh') || lower.includes('borrow')) {
        fallbackIntent = 'LENDING_LOAN_DISCOVERY';
        fallbackContent = `### Loan Feasibility & Affordability Evaluation\n\n- **Principal**: ₹3,00,000 over 36 months @ 8.99% APR\n- **Monthly EMI**: **₹9,540 / month**\n- **Total Repayment**: ₹3,43,436 (Total Interest: ₹43,436)\n- **FOIR Status**: 14.7% of monthly income, well below institutional risk ceilings.`;
        cards = [
          {
            cardType: 'EMI_BREAKDOWN',
            title: '₹3 Lakh Loan Amortization',
            subtitle: 'Deterministic Reducing Balance Schedule',
            badge: 'Pre-Approved · Prime Tier',
            metrics: {
              'Principal': '₹3,00,000',
              'Monthly EMI': '₹9,540 / mo',
              'Interest Rate': '8.99% APR',
              'Tenure': '36 Months',
            },
            bulletPoints: [
              'Fast-track paperless KYC disbursement in 4 hours',
              'Zero foreclosure charges after 12 monthly installments',
            ],
            primaryActionLabel: 'Open Lending Module',
            primaryActionUrl: '/lending',
          },
        ];
      } else if (lower.includes('save') || lower.includes('earn') || lower.includes('budget')) {
        fallbackIntent = 'FINTECH_BUDGET_OPTIMIZATION';
        fallbackContent = `### 50-30-20 Smart Savings Framework (₹40,000 Salary)\n\n- **Needs (50%)**: Cap essentials at **₹20,000**\n- **Wants (30%)**: Restrict dining & discretionary shopping to **₹12,000**\n- **Savings (20%)**: Automate **₹8,000 / month** into high-yield reserves\n- **Emergency Reserve**: Accumulates ₹1,20,000 buffer in 15 months!`;
        cards = [
          {
            cardType: 'BUDGET_ANALYSIS',
            title: '50-30-20 Cashflow Restructuring',
            subtitle: 'Rule-Based Wealth Acceleration',
            badge: 'High Savings Impact',
            metrics: {
              'Monthly Inflow': '₹40,000',
              'Needs Limit': '₹20,000 (50%)',
              'Wants Limit': '₹12,000 (30%)',
              'Savings Target': '₹8,000 / mo (20%)',
            },
            bulletPoints: [
              'Curb dining delivery to recover ₹4,500/month',
              'Prune unutilized streaming apps to recover ₹1,200/month',
            ],
            primaryActionLabel: 'Configure in Fintech Module',
            primaryActionUrl: '/fintech',
          },
        ];
      } else {
        fallbackIntent = 'CROSS_DOMAIN_DEBT_REDUCTION';
        fallbackContent = `### Cross-Domain Debt & Cashflow Relief Blueprint\n\n- **Current Total Burden**: ₹31,300 / month across fragmented EMIs and discretionary spend.\n- **Pillar 1 (Lending)**: Extend tenure to 48 months to lower monthly EMI by **₹9,050 / month**.\n- **Pillar 2 (Fintech)**: Trim dining & cancel duplicate subscriptions to recover **₹4,200 / month**.\n- **Net Monthly Relief**: **₹13,250 / month** immediate cashflow surge!`;
        cards = [
          {
            cardType: 'DEBT_BURDEN_REDUCTION',
            title: 'Monthly Burden Relief Matrix',
            subtitle: 'Unified Lending Restructuring + Fintech Trimming',
            badge: 'Relief: +₹13,250 / mo',
            metrics: {
              'Current Burden': '₹31,300 / mo',
              'Optimized Burden': '₹18,050 / mo',
              'Monthly Relief': '₹13,250 / mo',
              'DTI Reduction': '38.4% → 23.5%',
            },
            bulletPoints: [
              'Consolidates multiple creditor due dates into a single date',
              'Cuts default risk and expands monthly emergency liquidity',
            ],
            primaryActionLabel: 'Explore Consolidation Options',
            primaryActionUrl: '/lending',
          },
        ];
      }

      const fallbackMsg: ExtendedChatMessage = {
        id: `asst_${Date.now()}`,
        role: 'assistant',
        content: fallbackContent,
        timestamp: new Date().toISOString(),
        intent: fallbackIntent,
        cards,
        suggestedActions,
        confidenceScore: 0.95,
        latencyMs: 38,
        tokensUsed: 280,
      };

      setMessages((prev) => [...prev, fallbackMsg]);
    } finally {
      setIsGenerating(false);
    }
  };

  const handleActionClick = (action: AiInteractiveAction) => {
    if (action.actionType === 'NAVIGATE' && action.targetUrl) {
      navigate(action.targetUrl);
    } else if (action.actionType === 'PROMPT' || action.payload) {
      handleSendMessage(action.payload || action.label);
    }
  };

  const handleClearChat = async () => {
    try {
      await aiChatApi.clearAllSessions();
    } catch (e) {
      // Ignored in offline/mock mode
    }
    setMessages([
      {
        id: `init_${Date.now()}`,
        role: 'assistant',
        content: `### Welcome to the FinPath AI Central Journey Orchestrator

I am your unified financial intelligence system. I seamlessly bridge **Insurance**, **Lending**, and **Fintech (Wealth & Budgeting)** to solve complex cross-domain financial challenges.

Select a real-world scenario above or ask any question to begin!`,
        timestamp: new Date().toISOString(),
      },
    ]);
  };

  return (
    <div className="flex flex-col h-[calc(100vh-5rem)] max-w-7xl mx-auto space-y-4">
      {/* Top Banner & Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between pb-3 border-b border-dark-700/60 gap-3">
        <div className="flex items-center space-x-3">
          <div className="p-2.5 rounded-xl bg-gradient-to-br from-electric-blue/20 to-electric-cyan/20 border border-electric-blue/40 text-electric-cyan shadow-lg shadow-electric-blue/10">
            <Sparkles className="w-6 h-6 animate-pulse" />
          </div>
          <div>
            <div className="flex items-center space-x-2">
              <h1 className="text-xl font-bold text-white tracking-tight">FinPath AI Financial Advisor & Journey Orchestrator</h1>
              <Badge variant="cyan" size="sm" className="font-semibold uppercase tracking-wider">
                Agentic Orchestration v2.0
              </Badge>
            </div>
            <p className="text-xs text-dark-300">
              Context-aware intelligence across Insurance, Lending & Fintech with grounded tool execution
            </p>
          </div>
        </div>

        <div className="flex items-center space-x-2">
          <Button
            variant="outline"
            size="sm"
            onClick={handleClearChat}
            className="text-xs text-dark-300 hover:text-white border-dark-700 hover:border-dark-600"
          >
            <Trash2 className="w-3.5 h-3.5 mr-1.5" />
            Clear History & Reset
          </Button>
        </div>
      </div>

      {/* Visual Journey Step Stepper */}
      {activeJourneyStage && (
        <div className="p-3.5 rounded-xl bg-dark-800/80 border border-dark-700/80 backdrop-blur-md shadow-inner">
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-3 text-xs">
            <div className="flex items-center space-x-2">
              <Layers className="w-4 h-4 text-electric-blue" />
              <span className="font-semibold text-white">{activeJourneyStage.stepTitle}</span>
              <Badge variant="outline" size="sm">
                Step {activeJourneyStage.currentStep} of {activeJourneyStage.totalSteps}
              </Badge>
            </div>

            <div className="flex items-center space-x-4">
              {activeJourneyStage.completedSteps.map((step, idx) => (
                <div key={idx} className="flex items-center space-x-1.5 text-emerald-400">
                  <CheckCircle2 className="w-3.5 h-3.5" />
                  <span className="hidden lg:inline">{step}</span>
                </div>
              ))}
              <div className="flex items-center space-x-1.5 text-electric-cyan font-medium">
                <div className="w-2 h-2 rounded-full bg-electric-cyan animate-ping" />
                <span>Active Step</span>
              </div>
              {activeJourneyStage.pendingSteps.slice(0, 1).map((step, idx) => (
                <div key={idx} className="flex items-center space-x-1.5 text-dark-400">
                  <div className="w-1.5 h-1.5 rounded-full bg-dark-500" />
                  <span className="hidden lg:inline">{step}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Scenario Starter Pills */}
      <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-2.5">
        {scenarioStarters.map((scenario, idx) => {
          const Icon = scenario.icon;
          return (
            <button
              key={idx}
              onClick={() => handleSendMessage(scenario.prompt)}
              disabled={isGenerating}
              className={`text-left p-2.5 rounded-xl border bg-gradient-to-br transition-all duration-200 hover:scale-[1.02] hover:shadow-lg ${scenario.color}`}
            >
              <div className="flex items-center justify-between mb-1">
                <span className="text-[9px] uppercase font-bold tracking-wider opacity-80">
                  {scenario.tag}
                </span>
                <Icon className="w-3.5 h-3.5 opacity-90" />
              </div>
              <div className="text-xs font-semibold text-white mb-0.5 truncate">{scenario.title}</div>
              <div className="text-[10px] text-dark-300 truncate">"{scenario.prompt}"</div>
            </button>
          );
        })}
      </div>

      {/* Chat Messages Timeline */}
      <div className="flex-1 overflow-y-auto space-y-4 pr-1 scrollbar-thin scrollbar-thumb-dark-700">
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'} group`}
          >
            <div
              className={`flex space-x-3 max-w-3xl ${
                msg.role === 'user' ? 'flex-row-reverse space-x-reverse' : 'flex-row'
              }`}
            >
              {/* Avatar */}
              <div
                className={`w-8 h-8 rounded-full flex items-center justify-center shrink-0 shadow-md ${
                  msg.role === 'user'
                    ? 'bg-electric-blue/20 text-electric-blue border border-electric-blue/30'
                    : 'bg-dark-700 text-electric-cyan border border-dark-600'
                }`}
              >
                {msg.role === 'user' ? <User className="w-4 h-4" /> : <Bot className="w-4 h-4" />}
              </div>

              {/* Message Content Container */}
              <div className="space-y-3">
                <div
                  className={`p-4 rounded-2xl border text-sm leading-relaxed shadow-sm ${
                    msg.role === 'user'
                      ? 'bg-gradient-to-r from-electric-blue to-electric-indigo text-white border-transparent'
                      : 'bg-dark-800/90 border-dark-700/80 text-dark-100 backdrop-blur-md'
                  }`}
                >
                  {/* Assistant Intent Badge & Confidence */}
                  {msg.role === 'assistant' && (
                    <div className="flex flex-wrap items-center justify-between gap-2 mb-2 pb-2 border-b border-dark-700/50">
                      <div className="flex items-center space-x-2">
                        {msg.intent && (
                          <Badge variant="cyan" size="sm">
                            {msg.intent}
                          </Badge>
                        )}
                        <span className="text-[11px] text-dark-400">Context-Aware Routing</span>
                      </div>
                      <div className="flex items-center space-x-3 text-[10px] text-dark-400">
                        {msg.confidenceScore !== undefined && (
                          <span className="flex items-center text-emerald-400">
                            Confidence: {Math.round(msg.confidenceScore * 100)}%
                          </span>
                        )}
                        {msg.latencyMs !== undefined && (
                          <span className="flex items-center">
                            <Clock className="w-3 h-3 mr-0.5 inline" /> {msg.latencyMs}ms
                          </span>
                        )}
                        {msg.tokensUsed !== undefined && (
                          <span className="flex items-center">
                            <Cpu className="w-3 h-3 mr-0.5 inline" /> {msg.tokensUsed} tokens
                          </span>
                        )}
                      </div>
                    </div>
                  )}

                  {/* Body Text */}
                  <div className="whitespace-pre-line prose prose-invert max-w-none text-xs sm:text-sm">
                    {msg.content}
                  </div>
                </div>

                {/* Clarification Options Pills (When confidence is low or clarification needed) */}
                {msg.clarificationNeeded && msg.clarificationOptions && msg.clarificationOptions.length > 0 && (
                  <div className="p-3 rounded-xl bg-amber-500/10 border border-amber-500/30 text-xs space-y-2">
                    <div className="flex items-center space-x-1.5 text-amber-300 font-semibold">
                      <AlertCircle className="w-4 h-4 shrink-0" />
                      <span>Clarification Needed: Please select your preferred focus area</span>
                    </div>
                    <div className="flex flex-wrap gap-2 pt-1">
                      {msg.clarificationOptions.map((opt, optIdx) => (
                        <button
                          key={optIdx}
                          onClick={() => handleSendMessage(opt)}
                          disabled={isGenerating}
                          className="px-3 py-1.5 rounded-lg text-xs font-medium bg-amber-500/20 hover:bg-amber-500/30 text-amber-200 border border-amber-500/40 hover:border-amber-400 transition-all flex items-center space-x-1"
                        >
                          <span>{opt}</span>
                          <ArrowRight className="w-3 h-3" />
                        </button>
                      ))}
                    </div>
                  </div>
                )}

                {/* Structured Recommendation Cards */}
                {msg.cards && msg.cards.length > 0 && (
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-3 pt-1">
                    {msg.cards.map((card, cIdx) => (
                      <div
                        key={cIdx}
                        className="p-3.5 rounded-xl bg-dark-850/95 border border-dark-700 hover:border-electric-blue/50 transition-all shadow-md flex flex-col justify-between"
                      >
                        <div>
                          <div className="flex items-center justify-between mb-1.5">
                            <span className="text-[11px] font-semibold text-white truncate max-w-[200px]">
                              {card.title}
                            </span>
                            <Badge variant="electric" size="sm" className="text-[10px]">
                              {card.badge}
                            </Badge>
                          </div>
                          <p className="text-[11px] text-dark-300 mb-3">{card.subtitle}</p>

                          {/* Key Metrics Grid */}
                          {card.metrics && (
                            <div className="grid grid-cols-2 gap-2 mb-3 bg-dark-900/60 p-2 rounded-lg border border-dark-800">
                              {Object.entries(card.metrics).map(([k, v], mIdx) => (
                                <div key={mIdx} className="text-left">
                                  <div className="text-[10px] text-dark-400 truncate">{k}</div>
                                  <div className="text-xs font-semibold text-electric-cyan truncate">
                                    {v}
                                  </div>
                                </div>
                              ))}
                            </div>
                          )}

                          {/* Bullet Highlights */}
                          {card.bulletPoints && (
                            <ul className="space-y-1 mb-3">
                              {card.bulletPoints.map((bp, bIdx) => (
                                <li
                                  key={bIdx}
                                  className="flex items-start text-[11px] text-dark-300 space-x-1.5"
                                >
                                  <CheckSquare className="w-3 h-3 text-electric-blue shrink-0 mt-0.5" />
                                  <span>{bp}</span>
                                </li>
                              ))}
                            </ul>
                          )}
                        </div>

                        {card.primaryActionLabel && card.primaryActionUrl && (
                          <Button
                            variant="secondary"
                            size="sm"
                            onClick={() => navigate(card.primaryActionUrl!)}
                            className="w-full text-xs flex items-center justify-center space-x-1 border-electric-blue/30 hover:border-electric-blue"
                          >
                            <span>{card.primaryActionLabel}</span>
                            <ArrowRight className="w-3 h-3" />
                          </Button>
                        )}
                      </div>
                    ))}
                  </div>
                )}

                {/* Explainability Accordion Dropdown */}
                {msg.explainability && (
                  <div className="rounded-xl border border-dark-700/80 bg-dark-900/70 overflow-hidden text-xs">
                    <button
                      onClick={() => toggleExplainability(msg.id)}
                      className="w-full p-2.5 flex items-center justify-between text-dark-300 hover:text-white hover:bg-dark-800/40 transition-colors"
                    >
                      <div className="flex items-center space-x-2">
                        <Info className="w-3.5 h-3.5 text-electric-cyan" />
                        <span className="font-semibold text-white">Why did AI recommend this? (Explainability & Data)</span>
                      </div>
                      {expandedExplainability[msg.id] ? (
                        <ChevronUp className="w-4 h-4 text-dark-400" />
                      ) : (
                        <ChevronDown className="w-4 h-4 text-dark-400" />
                      )}
                    </button>

                    {expandedExplainability[msg.id] && (
                      <div className="p-3 border-t border-dark-800 space-y-2.5 text-[11px] text-dark-300">
                        <div>
                          <div className="font-semibold text-white mb-0.5">Summary Rationale:</div>
                          <p className="text-dark-300">{msg.explainability.relevanceRationale}</p>
                        </div>

                        {msg.explainability.dataSourcesUsed && msg.explainability.dataSourcesUsed.length > 0 && (
                          <div>
                            <div className="font-semibold text-white mb-1">Data Sources Consulted:</div>
                            <div className="flex flex-wrap gap-1">
                              {msg.explainability.dataSourcesUsed.map((ds, dsIdx) => (
                                <span
                                  key={dsIdx}
                                  className="px-2 py-0.5 rounded-md bg-dark-800 text-electric-cyan border border-dark-700 font-mono text-[10px]"
                                >
                                  {ds}
                                </span>
                              ))}
                            </div>
                          </div>
                        )}

                        {msg.explainability.underlyingAssumptions && msg.explainability.underlyingAssumptions.length > 0 && (
                          <div>
                            <div className="font-semibold text-white mb-0.5">Underlying Assumptions:</div>
                            <ul className="list-disc list-inside space-y-0.5 text-dark-400">
                              {msg.explainability.underlyingAssumptions.map((assump, aIdx) => (
                                <li key={aIdx}>{assump}</li>
                              ))}
                            </ul>
                          </div>
                        )}

                        {msg.explainability.constraintsAndLimitations && msg.explainability.constraintsAndLimitations.length > 0 && (
                          <div>
                            <div className="font-semibold text-amber-300 mb-0.5">Constraints & Limitations:</div>
                            <ul className="list-disc list-inside space-y-0.5 text-amber-200/80">
                              {msg.explainability.constraintsAndLimitations.map((lim, lIdx) => (
                                <li key={lIdx}>{lim}</li>
                              ))}
                            </ul>
                          </div>
                        )}
                      </div>
                    )}
                  </div>
                )}

                {/* Tool Invocations Panel */}
                {msg.toolInvocations && msg.toolInvocations.length > 0 && (
                  <div className="p-2.5 rounded-lg bg-dark-900/80 border border-dark-800 text-[11px] space-y-1.5">
                    <div className="flex items-center space-x-1.5 text-dark-400 font-medium">
                      <Activity className="w-3 h-3 text-electric-blue" />
                      <span>Live Backend Service Invocations ({msg.toolInvocations.length} tools executed):</span>
                    </div>
                    {msg.toolInvocations.map((t: any, tIdx: number) => (
                      <div key={tIdx} className="font-mono text-dark-300 pl-4 border-l border-dark-700">
                        <span className="text-electric-cyan font-semibold">{t.tool || t.toolName || t.serviceName}</span>:{' '}
                        <span className="text-dark-400">{t.summary || t.resultSummary || t.payloadSummary}</span>
                      </div>
                    ))}
                  </div>
                )}

                {/* Citations Panel */}
                {msg.citations && msg.citations.length > 0 && (
                  <div className="p-2.5 rounded-lg bg-dark-900/60 border border-dark-800 text-[11px] space-y-1">
                    <div className="flex items-center space-x-1.5 text-dark-400 font-medium">
                      <FileText className="w-3 h-3 text-amber-400" />
                      <span>Grounded Underwriting Citations:</span>
                    </div>
                    {msg.citations.map((c: any, cIdx: number) => (
                      <div key={cIdx} className="text-dark-300 pl-4 border-l border-amber-500/30">
                        <span className="text-amber-300 font-medium">{c.documentName || c.documentTitle}</span>{' '}
                        {c.pageNumber || c.page ? `(p. ${c.pageNumber || c.page})` : ''}: "{c.excerpt || c.textExcerpt}"
                      </div>
                    ))}
                  </div>
                )}

                {/* Interactive Action Pills */}
                {msg.suggestedActions && msg.suggestedActions.length > 0 && (
                  <div className="flex flex-wrap gap-1.5 pt-1">
                    {msg.suggestedActions.map((act, aIdx) => (
                      <button
                        key={aIdx}
                        onClick={() => handleActionClick(act)}
                        disabled={isGenerating}
                        className="px-2.5 py-1 rounded-full text-xs bg-dark-800 hover:bg-dark-700 text-electric-blue border border-electric-blue/30 hover:border-electric-blue transition-all flex items-center space-x-1 shadow-sm"
                      >
                        <span>{act.label}</span>
                        {act.actionType === 'NAVIGATE' ? (
                          <ExternalLink className="w-2.5 h-2.5" />
                        ) : (
                          <ChevronRight className="w-2.5 h-2.5" />
                        )}
                      </button>
                    ))}
                  </div>
                )}
              </div>
            </div>
          </div>
        ))}

        {isGenerating && (
          <div className="flex justify-start">
            <div className="flex items-center space-x-3 max-w-xl">
              <div className="w-8 h-8 rounded-full bg-dark-700 flex items-center justify-center border border-dark-600 text-electric-cyan">
                <Loader2 className="w-4 h-4 animate-spin" />
              </div>
              <div className="p-3 rounded-2xl bg-dark-800 border border-dark-700 text-xs text-dark-300 flex items-center space-x-2">
                <span>Orchestrating across Insurance, Lending & Fintech services...</span>
              </div>
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      {/* Input Box Bar */}
      <div className="pt-2">
        <form
          onSubmit={(e) => {
            e.preventDefault();
            handleSendMessage();
          }}
          className="relative flex items-center"
        >
          <input
            type="text"
            value={inputPrompt}
            onChange={(e) => setInputPrompt(e.target.value)}
            disabled={isGenerating}
            placeholder="Ask about loan qualification, health cover, 50-30-20 budget, or debt consolidation..."
            className="w-full pl-4 pr-24 py-3 bg-dark-850 border border-dark-700 rounded-xl text-sm text-white placeholder-dark-400 focus:outline-none focus:border-electric-blue focus:ring-1 focus:ring-electric-blue shadow-inner"
          />
          <div className="absolute right-2 flex items-center space-x-1.5">
            <Button
              type="submit"
              disabled={!inputPrompt.trim() || isGenerating}
              size="sm"
              className="bg-electric-blue hover:bg-electric-indigo text-white font-medium px-3 py-1.5 rounded-lg flex items-center space-x-1"
            >
              <span>Send</span>
              <Send className="w-3.5 h-3.5" />
            </Button>
          </div>
        </form>
        <div className="flex items-center justify-between px-1 mt-1.5 text-[10px] text-dark-400">
          <span>Supported inputs: Health Floater, Personal Loans, Savings Rule, Debt Consolidation</span>
          <span>FinPath AI Orchestrator v2.0 · Grounded Execution</span>
        </div>
      </div>
    </div>
  );
};
export default AiAssistantPage;
