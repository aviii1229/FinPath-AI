import React from 'react';
import {
  Activity,
  ShieldCheck,
  AlertCircle,
  TrendingUp,
  CheckCircle2,
  AlertTriangle,
  Info,
  ArrowUpRight,
} from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import { FinancialHealthScore } from '@/core/api/fintechApi';
import { formatINR } from '@/core/utils/currency';

interface HealthScoreViewProps {
  healthScore: FinancialHealthScore | null;
  isLoading: boolean;
  onRefresh: () => void;
}

export const HealthScoreView: React.FC<HealthScoreViewProps> = ({
  healthScore,
  isLoading,
  onRefresh,
}) => {
  if (isLoading && !healthScore) {
    return (
      <div className="flex justify-center items-center py-20 text-slate-400">
        <Activity className="w-6 h-6 animate-spin text-cyan-400 mr-2" />
        <span>Evaluating multi-dimensional financial health...</span>
      </div>
    );
  }

  const score = healthScore?.overallScore ?? 78;
  const tier = healthScore?.healthTier ?? 'GOOD';
  const factors = healthScore?.scoringFactors ?? [];
  const improvements = healthScore?.improvementAreas ?? [];
  const disclaimer =
    healthScore?.disclaimer ??
    'This financial health score is an illustrative personal finance evaluation for informational and planning purposes only. It DOES NOT constitute a credit score (e.g., CIBIL/Experian), official underwriting rating, or formal creditworthiness assessment.';

  const getTierColor = (t: string) => {
    switch (t) {
      case 'EXCELLENT':
        return 'text-emerald-400 border-emerald-500/30 bg-emerald-500/10';
      case 'GOOD':
        return 'text-cyan-400 border-cyan-500/30 bg-cyan-500/10';
      case 'FAIR':
        return 'text-amber-400 border-amber-500/30 bg-amber-500/10';
      default:
        return 'text-rose-400 border-rose-500/30 bg-rose-500/10';
    }
  };

  const getFactorStatusBadge = (status: string) => {
    switch (status) {
      case 'OPTIMAL':
        return <Badge variant="success" size="sm">OPTIMAL</Badge>;
      case 'GOOD':
        return <Badge variant="cyan" size="sm">GOOD</Badge>;
      case 'FAIR':
        return <Badge variant="warning" size="sm">FAIR</Badge>;
      default:
        return <Badge variant="danger" size="sm">VULNERABLE</Badge>;
    }
  };

  return (
    <div className="space-y-6">
      {/* Header and Hero Score */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Score Gauge Card */}
        <Card className="p-6 bg-navy-900 border border-navy-700/80 flex flex-col items-center justify-center text-center relative overflow-hidden">
          <div className="absolute top-3 right-3">
            <Button variant="ghost" size="sm" onClick={onRefresh} isLoading={isLoading}>
              Recalculate
            </Button>
          </div>

          <div className="relative w-40 h-40 flex items-center justify-center my-4">
            <svg className="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
              <circle
                cx="50"
                cy="50"
                r="40"
                stroke="#1e293b"
                strokeWidth="10"
                fill="transparent"
              />
              <circle
                cx="50"
                cy="50"
                r="40"
                stroke={score >= 80 ? '#22c55e' : score >= 65 ? '#06b6d4' : score >= 50 ? '#f59e0b' : '#ef4444'}
                strokeWidth="10"
                strokeDasharray={`${(score / 100) * 251.2} 251.2`}
                strokeLinecap="round"
                fill="transparent"
                className="transition-all duration-1000 ease-out"
              />
            </svg>
            <div className="absolute flex flex-col items-center">
              <span className="text-4xl font-extrabold text-white font-mono">{score}</span>
              <span className="text-[10px] text-slate-400 uppercase tracking-widest">out of 100</span>
            </div>
          </div>

          <div className={`px-3 py-1 rounded-full border text-xs font-semibold ${getTierColor(tier)}`}>
            {tier} FINANCIAL HEALTH
          </div>

          <p className="text-xs text-slate-300 mt-3 max-w-xs leading-relaxed">
            {healthScore?.summaryText ||
              `Your Financial Health Score is ${score}/100. Strong cashflow discipline with opportunities in emergency buffers.`}
          </p>
        </Card>

        {/* Areas for Improvement */}
        <Card className="lg:col-span-2 p-6 bg-navy-900 border border-navy-700/80 flex flex-col justify-between">
          <div>
            <CardHeader className="p-0 pb-3">
              <CardTitle className="text-sm flex items-center gap-2">
                <TrendingUp className="w-4 h-4 text-cyan-400" />
                <span>Priority Financial Health Optimizations</span>
              </CardTitle>
            </CardHeader>
            <p className="text-xs text-slate-400 mb-4">
              Algorithmic recommendations to elevate your financial resilience, lower fixed debt burdens, and accelerate wealth generation.
            </p>

            <div className="space-y-3">
              {improvements.map((item, idx) => (
                <div
                  key={idx}
                  className="flex items-start gap-3 p-3 bg-navy-950/70 rounded-lg border border-navy-800 text-xs"
                >
                  <div className="w-5 h-5 rounded-full bg-cyan-500/20 text-cyan-400 flex items-center justify-center shrink-0 mt-0.5 font-bold text-[10px]">
                    {idx + 1}
                  </div>
                  <span className="text-slate-200 leading-relaxed">{item}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Statutory Disclaimer Alert */}
          <div className="mt-4 p-3 bg-navy-950/90 border border-slate-700/60 rounded-lg flex items-start gap-2.5 text-[11px] text-slate-400">
            <Info className="w-4 h-4 text-cyan-400 shrink-0 mt-0.5" />
            <div className="space-y-0.5">
              <span className="font-semibold text-slate-300 block">Transparent Analytical Model Notice:</span>
              <p>{disclaimer}</p>
            </div>
          </div>
        </Card>
      </div>

      {/* 5 Transparent Scoring Factors */}
      <div>
        <h3 className="text-sm font-bold text-white mb-3 flex items-center gap-2">
          <Activity className="w-4 h-4 text-cyan-400" />
          <span>Factor-by-Factor Scoring Breakdown (100 Points Model)</span>
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {factors.map((factor, idx) => {
            const pct = Math.round((factor.scoreAwarded / factor.maxScore) * 100);
            return (
              <Card key={idx} className="p-4 bg-navy-900 border border-navy-800 flex flex-col justify-between">
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-xs font-bold text-white">{factor.factorName}</span>
                    {getFactorStatusBadge(factor.status)}
                  </div>

                  <div className="flex items-baseline justify-between mb-1">
                    <span className="text-lg font-bold font-mono text-cyan-300">
                      {factor.scoreAwarded} / {factor.maxScore} pts
                    </span>
                    <span className="text-[11px] text-slate-400 font-mono">{pct}%</span>
                  </div>

                  {/* Progress bar */}
                  <div className="w-full bg-navy-950 rounded-full h-1.5 mb-3 overflow-hidden">
                    <div
                      className={`h-full rounded-full transition-all duration-700 ${
                        pct >= 85 ? 'bg-emerald-400' : pct >= 65 ? 'bg-cyan-400' : pct >= 45 ? 'bg-amber-400' : 'bg-rose-400'
                      }`}
                      style={{ width: `${pct}%` }}
                    />
                  </div>

                  <div className="space-y-1 text-[11px] text-slate-300 bg-navy-950/60 p-2.5 rounded border border-navy-800/80 mb-2">
                    <div className="flex justify-between">
                      <span className="text-slate-400">Current Metric:</span>
                      <span className="font-semibold text-slate-200 font-mono">{factor.currentMetricValue}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-slate-400">Healthy Benchmark:</span>
                      <span className="font-semibold text-emerald-400 font-mono">{factor.benchmark}</span>
                    </div>
                  </div>

                  <p className="text-[11px] text-slate-400 leading-normal">{factor.explanation}</p>
                </div>
              </Card>
            );
          })}
        </div>
      </div>
    </div>
  );
};

