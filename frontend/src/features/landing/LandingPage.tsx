import React from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Sparkles,
  Shield,
  Landmark,
  Coins,
  ArrowRight,
  CheckCircle2,
  Lock,
  Cpu,
  FileText,
  TrendingUp,
  BarChart3,
  Bot,
} from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { Badge } from '@/shared/components/ui/Badge';

export const LandingPage: React.FC = () => {
  const navigate = useNavigate();

  const modules = [
    {
      id: 'insurance',
      title: 'Insurance Intelligence',
      tagline: 'Personalized Coverage & Automated Claim Triage',
      icon: Shield,
      accent: 'cyan',
      description:
        'Compare life, health, auto, and home insurance quotes in seconds. Our RAG-powered engine extracts policy terms, analyzes exclusions, and accelerates claims adjudication with AI fraud scoring.',
      features: [
        'Instant multi-carrier quote comparison',
        'PDF policy exclusion extractor (RAG)',
        'Digital claim filing with fast-track triage',
        'Automatic coverage gap detection',
      ],
      route: '/insurance',
    },
    {
      id: 'lending',
      title: 'Lending & Credit Engine',
      tagline: 'Algorithmic Underwriting & Amortization',
      icon: Landmark,
      accent: 'electric',
      description:
        'Pre-qualify for personal, mortgage, and auto loans without hurting your credit score. Transparent DTI calculations and interactive amortization simulators put you in control.',
      features: [
        'Soft-pull instant credit pre-qualification',
        'Interactive principal & APR EMI simulators',
        'Automated payslip & tax return extraction',
        'Underwriter assistant risk scoring',
      ],
      route: '/lending',
    },
    {
      id: 'fintech',
      title: 'Fintech & Wealth PFM',
      tagline: 'Multi-Account Aggregation & Cash Flow Forecasting',
      icon: Coins,
      accent: 'purple',
      description:
        'Aggregate checking, savings, cards, and investments into a single unified financial ledger. Predictive 30-day cash flow projections help you avoid overdrafts and optimize savings.',
      features: [
        'AI-driven auto-categorization of expenses',
        '30-day forward liquidity & burn-rate forecast',
        'Monthly budget limit breach alerts',
        'Automated surplus savings allocator',
      ],
      route: '/fintech',
    },
  ];

  const benefits = [
    {
      icon: Cpu,
      title: 'Provider-Agnostic AI Gateway',
      description:
        'Never locked into one LLM. FinPath AI dynamically orchestrates between OpenAI GPT-4o, Google Gemini 1.5, Anthropic Claude, and local models with zero downtime.',
    },
    {
      icon: FileText,
      title: 'RAG Document Processing',
      description:
        'Upload your 40-page insurance policy, pay stubs, or tax returns. Our vector pipeline indexes chunks into PostgreSQL pgvector for cited, hallucination-free answers.',
    },
    {
      icon: Lock,
      title: 'Bank-Grade AES-256 Security',
      description:
        'PII data masking ensures your SSN and account numbers never reach external AI models. All sensitive columns are encrypted at-rest and in-transit.',
    },
    {
      icon: TrendingUp,
      title: 'Zero-Hallucination Math',
      description:
        'All interest calculations, debt-to-income ratios, and amortization schedules are computed deterministically in Java, backed by LLM reasoning for human clarity.',
    },
  ];

  return (
    <div className="min-h-screen bg-navy-950 text-slate-100 flex flex-col selection:bg-cyan-500 selection:text-navy-950">
      {/* Top Navigation */}
      <nav className="sticky top-0 z-40 bg-navy-950/80 backdrop-blur-md border-b border-navy-800 px-6 py-4">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-2.5">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-electric-600 to-cyan-400 flex items-center justify-center shadow-glow-sm">
              <Sparkles className="w-5 h-5 text-white" />
            </div>
            <div className="flex items-center gap-1.5">
              <span className="font-bold text-lg tracking-tight text-white">FinPath</span>
              <span className="text-[10px] font-bold px-1.5 py-0.5 rounded-full bg-cyan-500/20 text-cyan-300 border border-cyan-500/30">
                AI
              </span>
            </div>
          </div>

          <div className="hidden md:flex items-center gap-8 text-sm font-medium text-slate-300">
            <a href="#modules" className="hover:text-cyan-300 transition-colors">
              Financial Modules
            </a>
            <a href="#ai-engine" className="hover:text-cyan-300 transition-colors">
              AI Orchestrator
            </a>
            <a href="#benefits" className="hover:text-cyan-300 transition-colors">
              Architecture & Security
            </a>
          </div>

          <div className="flex items-center gap-3">
            <Button variant="ghost" size="sm" onClick={() => navigate('/login')}>
              Sign In
            </Button>
            <Button
              variant="electric"
              size="sm"
              onClick={() => navigate('/register')}
              rightIcon={<ArrowRight className="w-3.5 h-3.5" />}
            >
              Get Started
            </Button>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="relative pt-20 pb-24 overflow-hidden bg-radial-gradient">
        <div className="max-w-7xl mx-auto px-6 relative z-10 text-center">
          <Badge variant="cyan" size="md" className="mb-6 shadow-glow-sm">
            <Sparkles className="w-3 h-3 mr-1.5 text-cyan-400 animate-pulse" />
            The Unified AI-Powered Financial Journey Platform
          </Badge>

          <h1 className="text-4xl sm:text-6xl lg:text-7xl font-extrabold tracking-tight text-white max-w-4xl mx-auto leading-tight">
            One Intelligent Platform for{' '}
            <span className="gradient-text-electric">Insurance, Lending & Wealth</span>
          </h1>

          <p className="mt-6 text-base sm:text-xl text-slate-400 max-w-2xl mx-auto leading-relaxed">
            Eliminate silos across your financial life. FinPath AI combines algorithmic loan
            pre-qualification, automated insurance comparison, and predictive cash-flow forecasting
            under an autonomous reasoning orchestrator.
          </p>

          <div className="mt-10 flex flex-col sm:flex-row items-center justify-center gap-4">
            <Button
              variant="electric"
              size="lg"
              onClick={() => navigate('/dashboard')}
              rightIcon={<ArrowRight className="w-4 h-4" />}
              className="w-full sm:w-auto shadow-glow"
            >
              Launch Live Platform Demo
            </Button>
            <Button
              variant="outline"
              size="lg"
              onClick={() => navigate('/ai-assistant')}
              leftIcon={<Bot className="w-4 h-4 text-cyan-400" />}
              className="w-full sm:w-auto"
            >
              Try AI Co-Pilot
            </Button>
          </div>

          {/* Social Proof / Security Badges */}
          <div className="mt-14 pt-8 border-t border-navy-800/80 flex flex-wrap items-center justify-center gap-8 text-xs text-slate-400">
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-400" />
              <span>Modular Monolith (Spring Boot 3 + Java 21)</span>
            </div>
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-400" />
              <span>PostgreSQL 16 + pgvector RAG</span>
            </div>
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-400" />
              <span>Provider-Independent LLM Gateway</span>
            </div>
            <div className="flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-emerald-400" />
              <span>Deterministic Financial Math</span>
            </div>
          </div>
        </div>
      </section>

      {/* Modules Section */}
      <section id="modules" className="py-20 bg-navy-900/60 border-y border-navy-800">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center max-w-2xl mx-auto mb-16">
            <Badge variant="electric" size="sm" className="mb-3">
              Core Financial Pillars
            </Badge>
            <h2 className="text-3xl sm:text-4xl font-bold text-white tracking-tight">
              Three Independent Verticals, One Unified Co-Pilot
            </h2>
            <p className="mt-3 text-sm text-slate-400">
              Each module operates autonomously or coordinates seamlessly with the central AI orchestrator.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {modules.map((m) => {
              const Icon = m.icon;
              return (
                <Card
                  key={m.id}
                  hoverEffect
                  className="flex flex-col justify-between p-6 bg-navy-900/80 border-navy-700/80"
                  onClick={() => navigate(m.route)}
                >
                  <div>
                    <div className="w-12 h-12 rounded-xl bg-electric-600/10 border border-electric-500/30 flex items-center justify-center text-cyan-400 mb-5 shadow-glow-sm">
                      <Icon className="w-6 h-6" />
                    </div>

                    <h3 className="text-xl font-bold text-white mb-1">{m.title}</h3>
                    <p className="text-xs font-semibold text-cyan-400 mb-3">{m.tagline}</p>
                    <p className="text-xs text-slate-400 leading-relaxed mb-6">{m.description}</p>

                    <div className="space-y-2.5 pt-4 border-t border-navy-800">
                      {m.features.map((feat, idx) => (
                        <div key={idx} className="flex items-start gap-2 text-xs text-slate-300">
                          <CheckCircle2 className="w-4 h-4 text-cyan-400 shrink-0 mt-0.5" />
                          <span>{feat}</span>
                        </div>
                      ))}
                    </div>
                  </div>

                  <div className="mt-8 pt-4 border-t border-navy-800 flex items-center justify-between text-xs font-semibold text-cyan-400">
                    <span>Explore {m.title}</span>
                    <ArrowRight className="w-4 h-4 group-hover:translate-x-1 transition-transform" />
                  </div>
                </Card>
              );
            })}
          </div>
        </div>
      </section>

      {/* AI Orchestrator Spotlight */}
      <section id="ai-engine" className="py-20 relative overflow-hidden">
        <div className="max-w-7xl mx-auto px-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
            <div>
              <Badge variant="cyan" size="sm" className="mb-3">
                Autonomous Cognition Fabric
              </Badge>
              <h2 className="text-3xl sm:text-4xl font-bold text-white tracking-tight leading-tight">
                How the Central AI Orchestrator Connects Your Journey
              </h2>
              <p className="mt-4 text-sm text-slate-300 leading-relaxed">
                Traditional platforms treat bank accounts, loans, and insurance as separate products.
                FinPath AI breaks these walls. When you ask a simple question like{' '}
                <span className="text-cyan-300 font-medium">"Can I buy a ₹8,00,000 car?"</span>, the orchestrator:
              </p>

              <div className="mt-6 space-y-4">
                <div className="p-4 rounded-xl bg-navy-900 border border-navy-700/80">
                  <div className="flex items-center gap-3">
                    <div className="w-7 h-7 rounded-lg bg-electric-600/20 text-electric-400 flex items-center justify-center font-bold text-xs">
                      1
                    </div>
                    <span className="text-sm font-semibold text-white">Queries Fintech Cash Flow</span>
                  </div>
                  <p className="text-xs text-slate-400 mt-1 pl-10">
                    Inspects monthly surplus (₹41,260/mo) and verifies your liquid emergency cushion.
                  </p>
                </div>

                <div className="p-4 rounded-xl bg-navy-900 border border-navy-700/80">
                  <div className="flex items-center gap-3">
                    <div className="w-7 h-7 rounded-lg bg-cyan-600/20 text-cyan-400 flex items-center justify-center font-bold text-xs">
                      2
                    </div>
                    <span className="text-sm font-semibold text-white">Executes Lending Underwriting</span>
                  </div>
                  <p className="text-xs text-slate-400 mt-1 pl-10">
                    Runs deterministic FOIR / DTI calculation and amortization formulas (EMI = ₹14,250/mo @ 8.5%).
                  </p>
                </div>

                <div className="p-4 rounded-xl bg-navy-900 border border-navy-700/80">
                  <div className="flex items-center gap-3">
                    <div className="w-7 h-7 rounded-lg bg-purple-600/20 text-purple-400 flex items-center justify-center font-bold text-xs">
                      3
                    </div>
                    <span className="text-sm font-semibold text-white">Estimates Insurance Impact</span>
                  </div>
                  <p className="text-xs text-slate-400 mt-1 pl-10">
                    Calculates required comprehensive vehicle coverage (₹1,208/mo) and ensures total debt stays healthy.
                  </p>
                </div>
              </div>
            </div>

            {/* Interactive Preview Mockup */}
            <div className="glass-panel rounded-2xl p-6 border-cyan-500/30 shadow-glow relative">
              <div className="flex items-center justify-between pb-4 border-b border-navy-700/60 mb-4">
                <div className="flex items-center gap-2">
                  <Bot className="w-5 h-5 text-cyan-400" />
                  <span className="text-sm font-semibold text-white">FinPath AI Reasoning Pipeline</span>
                </div>
                <Badge variant="cyan" size="sm">Active Session</Badge>
              </div>

              <div className="space-y-3 font-mono text-xs">
                <div className="p-3 rounded-lg bg-navy-950/70 border border-navy-800 text-slate-300">
                  <span className="text-cyan-400">User:</span> Can I afford a ₹8,00,000 car with my current rent?
                </div>

                <div className="p-3 rounded-lg bg-navy-900/90 border border-cyan-500/20 space-y-2">
                  <div className="flex items-center gap-1.5 text-slate-400 text-[11px]">
                    <Sparkles className="w-3.5 h-3.5 text-cyan-400 animate-spin" />
                    <span>Dispatched 3 parallel domain tools...</span>
                  </div>
                  <div className="pl-4 border-l-2 border-electric-500 space-y-1 text-[11px] text-slate-300">
                    <p>✓ Fintech.getCashFlowSummary() → Surplus: ₹41,260/mo</p>
                    <p>✓ Lending.calculateDti(amount: 800000) → New FOIR: 32.4% (Approved)</p>
                    <p>✓ Insurance.estimateAutoPolicy(type: EV) → Premium: ₹1,208/mo</p>
                  </div>
                  <p className="text-slate-100 font-sans text-xs pt-1">
                    "Yes, Sarah! Your combined monthly payment will be ₹15,458 (₹14,250 loan EMI + ₹1,208 insurance),
                    leaving a healthy ₹25,802/mo buffer in your cash flow."
                  </p>
                </div>
              </div>

              <div className="mt-5 pt-4 border-t border-navy-700/60 flex items-center justify-end">
                <Button variant="electric" size="sm" onClick={() => navigate('/ai-assistant')}>
                  Interact with Live Assistant
                </Button>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Architecture & Benefits Grid */}
      <section id="benefits" className="py-20 bg-navy-900/40 border-t border-navy-800">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center max-w-2xl mx-auto mb-16">
            <Badge variant="cyan" size="sm" className="mb-3">
              Engineering Excellence
            </Badge>
            <h2 className="text-3xl sm:text-4xl font-bold text-white tracking-tight">
              Built for Security, Precision & Speed
            </h2>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            {benefits.map((b, idx) => {
              const Icon = b.icon;
              return (
                <div
                  key={idx}
                  className="p-5 rounded-xl glass-panel border border-navy-700/80 hover:border-cyan-500/40 transition-colors"
                >
                  <div className="w-10 h-10 rounded-lg bg-electric-600/10 border border-electric-500/20 flex items-center justify-center text-electric-400 mb-4">
                    <Icon className="w-5 h-5" />
                  </div>
                  <h4 className="text-sm font-semibold text-white mb-2">{b.title}</h4>
                  <p className="text-xs text-slate-400 leading-relaxed">{b.description}</p>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {/* CTA Footer Section */}
      <footer className="mt-auto bg-navy-950 border-t border-navy-800/80 pt-16 pb-12">
        <div className="max-w-7xl mx-auto px-6">
          <div className="flex flex-col md:flex-row items-center justify-between pb-12 border-b border-navy-800 gap-6">
            <div>
              <div className="flex items-center gap-2 mb-2">
                <div className="w-7 h-7 rounded-lg bg-gradient-to-tr from-electric-600 to-cyan-400 flex items-center justify-center">
                  <Sparkles className="w-4 h-4 text-white" />
                </div>
                <span className="font-bold text-lg text-white">FinPath AI</span>
              </div>
              <p className="text-xs text-slate-400 max-w-sm">
                Empowering consumers and businesses with intelligent, connected financial journeys.
              </p>
            </div>

            <div className="flex flex-wrap items-center gap-4">
              <Button variant="outline" size="md" onClick={() => navigate('/login')}>
                Sign In
              </Button>
              <Button variant="electric" size="md" onClick={() => navigate('/register')}>
                Create Free Account
              </Button>
            </div>
          </div>

          <div className="mt-8 flex flex-col sm:flex-row items-center justify-between text-xs text-slate-500 gap-4">
            <p>© 2026 FinPath AI Platforms, Inc. All rights reserved.</p>
            <div className="flex items-center gap-6">
              <a href="#modules" className="hover:text-slate-300 transition-colors">
                Privacy Policy
              </a>
              <a href="#modules" className="hover:text-slate-300 transition-colors">
                Terms of Service
              </a>
              <a href="#modules" className="hover:text-slate-300 transition-colors">
                SOC2 Compliance
              </a>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

