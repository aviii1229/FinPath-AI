import React, { useState } from 'react';
import {
  Sparkles,
  Repeat,
  Copy,
  AlertTriangle,
  TrendingUp,
  CheckCircle2,
  XCircle,
  HelpCircle,
  Clock,
  ArrowRight,
} from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import { SmartExpenseAnalysis, DuplicateSuspect, fintechApi } from '@/core/api/fintechApi';
import { formatINR } from '@/core/utils/currency';

interface SmartExpensesViewProps {
  analysis: SmartExpenseAnalysis | null;
  isLoading: boolean;
  onRefresh: () => void;
}

export const SmartExpensesView: React.FC<SmartExpensesViewProps> = ({
  analysis,
  isLoading,
  onRefresh,
}) => {
  const [suspects, setSuspects] = useState<DuplicateSuspect[]>(analysis?.duplicateSuspects || []);
  const [actionInProgress, setActionInProgress] = useState<string | null>(null);
  const [feedbackMessage, setFeedbackMessage] = useState<string | null>(null);

  // Keep local state synced if parent updates
  React.useEffect(() => {
    if (analysis?.duplicateSuspects) {
      setSuspects(analysis.duplicateSuspects);
    }
  }, [analysis]);

  const handleDuplicateAction = async (suspect: DuplicateSuspect, confirmed: boolean) => {
    setActionInProgress(suspect.suspectId);
    try {
      await fintechApi.confirmDuplicate(suspect.duplicateTransactionId, confirmed);
      setSuspects((prev) =>
        prev.map((s) =>
          s.suspectId === suspect.suspectId
            ? { ...s, status: confirmed ? 'CONFIRMED_DUPLICATE' : 'DISMISSED' }
            : s
        )
      );
      setFeedbackMessage(
        confirmed
          ? `Marked "${suspect.merchantName}" charge of ${formatINR(suspect.amount)} as duplicate. Reflected in expense reconciliation.`
          : `Dismissed duplicate alert. "${suspect.merchantName}" retained as legitimate separate transaction.`
      );
    } catch (err) {
      console.error('Failed to update duplicate confirmation', err);
    } finally {
      setActionInProgress(null);
    }
  };

  if (isLoading && !analysis) {
    return (
      <div className="flex justify-center items-center py-20 text-slate-400">
        <Sparkles className="w-6 h-6 animate-spin text-cyan-400 mr-2" />
        <span>Scanning transactions for recurring charges, duplicate anomalies, and spikes...</span>
      </div>
    );
  }

  const data = analysis || {
    subscriptions: [
      { merchantName: 'AWS Cloud Services', category: 'UTILITIES', monthlyAmount: 3200, lastChargedDate: '2026-11-01', frequency: 'MONTHLY', utilityLevel: 'ESSENTIAL' as const },
      { merchantName: 'Netflix Premium 4K', category: 'ENTERTAINMENT', monthlyAmount: 649, lastChargedDate: '2026-11-03', frequency: 'MONTHLY', utilityLevel: 'DISCRETIONARY' as const },
      { merchantName: 'Spotify Family Plan', category: 'ENTERTAINMENT', monthlyAmount: 179, lastChargedDate: '2026-11-05', frequency: 'MONTHLY', utilityLevel: 'DISCRETIONARY' as const },
      { merchantName: 'Gym Membership Cult.fit', category: 'HEALTHCARE', monthlyAmount: 1850, lastChargedDate: '2026-11-02', frequency: 'MONTHLY', utilityLevel: 'UNDERUTILIZED' as const },
    ],
    monthlySubscriptionsTotal: 5878,
    unusualExpenses: [
      { transactionId: 'tx-unusual-1', merchantName: 'Luxury Electronics Hub', category: 'SHOPPING', amount: 34999, date: '2026-11-06', categoryAverage: 6500, reason: 'Transaction is 5.4x the 90-day category average for Shopping.' },
    ],
    categoryIncreases: [
      { category: 'DINING', currentMonthSpent: 9840, previousMonthSpent: 6200, percentageIncrease: 58.7, insightText: 'Dining expenses jumped +58.7% MoM driven by 8 weekend restaurant bills.' },
      { category: 'SHOPPING', currentMonthSpent: 12400, previousMonthSpent: 8900, percentageIncrease: 39.3, insightText: 'Festive season shopping increased retail discretionary outflows.' },
    ],
    duplicateSuspects: suspects,
  };

  return (
    <div className="space-y-6">
      {/* Feedback banner */}
      {feedbackMessage && (
        <div className="p-3 bg-cyan-500/10 border border-cyan-500/30 rounded-lg flex items-center justify-between text-xs text-cyan-300">
          <div className="flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-cyan-400" />
            <span>{feedbackMessage}</span>
          </div>
          <button
            onClick={() => setFeedbackMessage(null)}
            className="text-slate-400 hover:text-white text-xs"
          >
            Dismiss
          </button>
        </div>
      )}

      {/* Top Banner: Suspected Duplicates Requiring User Confirmation */}
      <Card className="p-6 bg-navy-900 border border-amber-500/40">
        <CardHeader className="p-0 pb-3">
          <CardTitle className="text-sm flex items-center justify-between">
            <span className="flex items-center gap-2 text-amber-300">
              <Copy className="w-4 h-4 text-amber-400" />
              Suspected Duplicate Transactions (Requires Explicit User Consent)
            </span>
            <Badge variant="warning" size="sm">
              HUMAN-IN-THE-LOOP SAFETY
            </Badge>
          </CardTitle>
        </CardHeader>
        <p className="text-xs text-slate-300 mb-4">
          FinPath AI identifies candidate duplicate charges occurring within narrow time windows with identical amounts. Per banking safety policies, we never auto-delete or reclassify without your explicit confirmation.
        </p>

        {data.duplicateSuspects.length === 0 ? (
          <div className="p-4 bg-navy-950 rounded-lg text-xs text-slate-400 text-center">
            No suspected duplicate transactions detected in your recent transaction history.
          </div>
        ) : (
          <div className="space-y-3">
            {data.duplicateSuspects.map((dup) => (
              <div
                key={dup.suspectId}
                className="p-4 bg-navy-950/80 rounded-xl border border-navy-800 flex flex-col md:flex-row md:items-center justify-between gap-4"
              >
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-white text-sm">{dup.merchantName}</span>
                    <span className="text-amber-400 font-bold font-mono text-sm">{formatINR(dup.amount)}</span>
                    <Badge
                      variant={
                        dup.status === 'CONFIRMED_DUPLICATE'
                          ? 'danger'
                          : dup.status === 'DISMISSED'
                          ? 'outline'
                          : 'warning'
                      }
                      size="sm"
                    >
                      {dup.status.replace('_', ' ')}
                    </Badge>
                  </div>
                  <p className="text-xs text-slate-400">{dup.explanation}</p>
                  <div className="flex items-center gap-4 text-[11px] text-slate-400 font-mono pt-1">
                    <span>Charge 1: {dup.primaryDate}</span>
                    <span>Charge 2: {dup.duplicateDate}</span>
                  </div>
                </div>

                <div className="flex items-center gap-2 shrink-0">
                  {dup.status === 'PENDING_CONFIRMATION' ? (
                    <>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleDuplicateAction(dup, false)}
                        isLoading={actionInProgress === dup.suspectId}
                      >
                        Keep (Distinct)
                      </Button>
                      <Button
                        variant="electric"
                        size="sm"
                        className="bg-rose-600 hover:bg-rose-500 text-white"
                        onClick={() => handleDuplicateAction(dup, true)}
                        isLoading={actionInProgress === dup.suspectId}
                      >
                        Confirm Duplicate
                      </Button>
                    </>
                  ) : (
                    <span className="text-xs text-slate-400 italic">Action recorded</span>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </Card>

      {/* 2-Column: Recurring Subscriptions & Unusually High Expenses */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Subscriptions Tracker */}
        <Card className="p-6 bg-navy-900 border border-navy-700/80">
          <CardHeader className="p-0 pb-3">
            <CardTitle className="text-sm flex items-center justify-between">
              <span className="flex items-center gap-2">
                <Repeat className="w-4 h-4 text-cyan-400" />
                Active Recurring Subscriptions Tracker
              </span>
              <span className="text-xs font-mono font-bold text-cyan-300">
                Total: {formatINR(data.monthlySubscriptionsTotal)} / mo
              </span>
            </CardTitle>
          </CardHeader>
          <p className="text-xs text-slate-400 mb-3">
            Automated recurring charges tagged by utility to help identify forgotten or underutilized services.
          </p>

          <div className="space-y-2.5">
            {data.subscriptions.map((sub, i) => (
              <div
                key={i}
                className="flex items-center justify-between p-3 bg-navy-950/70 rounded-lg border border-navy-800 text-xs"
              >
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-semibold text-white">{sub.merchantName}</span>
                    <Badge
                      variant={
                        sub.utilityLevel === 'ESSENTIAL'
                          ? 'cyan'
                          : sub.utilityLevel === 'UNDERUTILIZED'
                          ? 'warning'
                          : 'outline'
                      }
                      size="sm"
                    >
                      {sub.utilityLevel}
                    </Badge>
                  </div>
                  <span className="text-[10px] text-slate-400">
                    Last charged on {sub.lastChargedDate} ({sub.frequency})
                  </span>
                </div>

                <span className="font-bold text-white font-mono">{formatINR(sub.monthlyAmount)}</span>
              </div>
            ))}
          </div>
        </Card>

        {/* Unusual High Expenses & MoM Increases */}
        <div className="space-y-6">
          {/* Unusual High Expenses */}
          <Card className="p-6 bg-navy-900 border border-navy-700/80">
            <CardHeader className="p-0 pb-3">
              <CardTitle className="text-sm flex items-center gap-2">
                <AlertTriangle className="w-4 h-4 text-rose-400" />
                <span>Unusual Out-of-Band High Expenses</span>
              </CardTitle>
            </CardHeader>

            <div className="space-y-3">
              {data.unusualExpenses.map((un, i) => (
                <div key={i} className="p-3 bg-rose-500/10 border border-rose-500/30 rounded-lg text-xs space-y-1">
                  <div className="flex justify-between items-center">
                    <span className="font-bold text-white">{un.merchantName}</span>
                    <span className="font-bold text-rose-300 font-mono">{formatINR(un.amount)}</span>
                  </div>
                  <p className="text-rose-200 text-[11px]">{un.reason}</p>
                  <span className="text-[10px] text-slate-400 font-mono block">
                    Category Baseline: {formatINR(un.categoryAverage)} | Date: {un.date}
                  </span>
                </div>
              ))}
            </div>
          </Card>

          {/* Month-over-Month Category Spending Increases */}
          <Card className="p-6 bg-navy-900 border border-navy-700/80">
            <CardHeader className="p-0 pb-3">
              <CardTitle className="text-sm flex items-center gap-2">
                <TrendingUp className="w-4 h-4 text-amber-400" />
                <span>Month-over-Month Spending Acceleration</span>
              </CardTitle>
            </CardHeader>

            <div className="space-y-2.5">
              {data.categoryIncreases.map((inc, i) => (
                <div key={i} className="p-3 bg-navy-950/70 rounded-lg border border-navy-800 text-xs space-y-1">
                  <div className="flex items-center justify-between">
                    <span className="font-semibold text-white">{inc.category}</span>
                    <span className="font-bold text-amber-400 font-mono">+{inc.percentageIncrease}% MoM</span>
                  </div>
                  <div className="flex items-center justify-between text-[11px] text-slate-400 font-mono">
                    <span>Prev: {formatINR(inc.previousMonthSpent)}</span>
                    <ArrowRight className="w-3 h-3 text-slate-500" />
                    <span className="text-white">Current: {formatINR(inc.currentMonthSpent)}</span>
                  </div>
                  <p className="text-[11px] text-slate-300 pt-0.5">{inc.insightText}</p>
                </div>
              ))}
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
};
