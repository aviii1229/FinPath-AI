import React from 'react';
import {
  TrendingUp,
  AlertTriangle,
  Calendar,
  Layers,
  ArrowDownLeft,
  ArrowUpRight,
  ShieldAlert,
  Info,
  Clock,
} from 'lucide-react';
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
} from 'recharts';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Badge } from '@/shared/components/ui/Badge';
import { CashFlowForecast } from '@/core/api/fintechApi';
import { formatINR, formatCompactINR } from '@/core/utils/currency';

interface CashflowForecastViewProps {
  forecast: CashFlowForecast | null;
  isLoading: boolean;
}

export const CashflowForecastView: React.FC<CashflowForecastViewProps> = ({
  forecast,
  isLoading,
}) => {
  if (isLoading && !forecast) {
    return (
      <div className="flex justify-center items-center py-20 text-slate-400">
        <TrendingUp className="w-6 h-6 animate-spin text-cyan-400 mr-2" />
        <span>Calculating probabilistic cashflow forecast and recurring liabilities...</span>
      </div>
    );
  }

  const fc = forecast || {
    currentLiquidCash: 345250,
    projectedCashIn30Days: 386510,
    expectedInflows30Days: 75000,
    expectedOutflows30Days: 33740,
    burnRateDailyAverage: 1124.66,
    liquidityHealth: 'HEALTHY',
    forecastPoints: [
      { date: 'Day +0', projectedBalance: 345250, confidenceLow: 345250, confidenceHigh: 345250 },
      { date: 'Day +7', projectedBalance: 337377, confidenceLow: 332000, confidenceHigh: 342000 },
      { date: 'Day +14', projectedBalance: 329504, confidenceLow: 320000, confidenceHigh: 339000 },
      { date: 'Day +21', projectedBalance: 321631, confidenceLow: 308000, confidenceHigh: 335000 },
      { date: 'Day +30', projectedBalance: 386510, confidenceLow: 368000, confidenceHigh: 405000 },
    ],
    recurringBills: [
      { billName: 'Apartment Rental Lease', category: 'HOUSING', amount: 22000, dueDayOfMonth: 1, status: 'SCHEDULED' },
      { billName: 'Broadband Fiber Internet', category: 'UTILITIES', amount: 1499, dueDayOfMonth: 8, status: 'SCHEDULED' },
      { billName: 'Health Insurance Family Float', category: 'HEALTHCARE', amount: 3308, dueDayOfMonth: 12, status: 'SCHEDULED' },
      { billName: 'Electricity & Utility Board', category: 'UTILITIES', amount: 2450, dueDayOfMonth: 18, status: 'SCHEDULED' },
      { billName: 'Cloud & Entertainment Subscriptions', category: 'ENTERTAINMENT', amount: 1699, dueDayOfMonth: 24, status: 'SCHEDULED' },
    ],
    recurringBillsTotal: 30956,
    potentialShortageDetected: false,
    shortageRiskLevel: 'LOW',
    lowestProjectedBalance: 321631,
    forecastAssumptions: [
      'Assumes regular payroll inflow on day 28/30 of the month.',
      'Models historical 90-day discretionary spending variance within 95% confidence intervals.',
      'Includes all confirmed active recurring obligations and scheduled bill payments.',
    ],
    uncertaintyNotes: 'Discretionary outflows can fluctuate ±12% based on weekend dining, online shopping, and unplanned personal transactions.',
    disclaimer: 'Cash flow projections are simulated approximations for liquidity planning and do not guarantee future account balances or prevent bank overdrafts.',
  };

  return (
    <div className="space-y-6">
      {/* Shortage Risk Alert if detected or healthy indicator */}
      {fc.potentialShortageDetected ? (
        <div className="p-4 bg-rose-500/10 border border-rose-500/40 rounded-xl flex items-start gap-3">
          <ShieldAlert className="w-5 h-5 text-rose-400 shrink-0 mt-0.5" />
          <div className="space-y-1">
            <div className="flex items-center gap-2">
              <span className="font-bold text-rose-300 text-sm">Potential Liquidity Shortfall Warning</span>
              <Badge variant="danger" size="sm">RISK LEVEL: {fc.shortageRiskLevel}</Badge>
            </div>
            <p className="text-xs text-rose-200">
              Projected balance dips to a low of <span className="font-mono font-bold text-white">{formatINR(fc.lowestProjectedBalance || 0)}</span> before expected payroll inflows. Review discretionary expenses or defer large purchases.
            </p>
          </div>
        </div>
      ) : (
        <div className="p-3 bg-emerald-500/10 border border-emerald-500/30 rounded-xl flex items-center justify-between text-xs">
          <div className="flex items-center gap-2">
            <div className="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse" />
            <span className="font-semibold text-emerald-300">Liquidity Health: {fc.liquidityHealth}</span>
            <span className="text-slate-400">| No cash shortfalls projected over the next 30 days. Lowest estimated liquid buffer: {formatINR(fc.lowestProjectedBalance || 0)}.</span>
          </div>
          <Badge variant="success" size="sm">CONFIDENCE 95%</Badge>
        </div>
      )}

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card className="p-4 bg-navy-900 border border-navy-800">
          <span className="text-[10px] uppercase font-bold text-slate-400 block mb-1">Current Liquid Cash</span>
          <p className="text-2xl font-bold text-white font-mono">{formatINR(fc.currentLiquidCash)}</p>
          <span className="text-[10px] text-slate-400">Available across savings & checking</span>
        </Card>

        <Card className="p-4 bg-navy-900 border border-navy-800">
          <div className="flex justify-between items-start">
            <span className="text-[10px] uppercase font-bold text-slate-400">Expected 30-Day Inflows</span>
            <ArrowDownLeft className="w-4 h-4 text-emerald-400" />
          </div>
          <p className="text-2xl font-bold text-emerald-400 font-mono mt-1">+{formatINR(fc.expectedInflows30Days)}</p>
          <span className="text-[10px] text-slate-400">Salary & interest credits</span>
        </Card>

        <Card className="p-4 bg-navy-900 border border-navy-800">
          <div className="flex justify-between items-start">
            <span className="text-[10px] uppercase font-bold text-slate-400">Expected 30-Day Outflows</span>
            <ArrowUpRight className="w-4 h-4 text-rose-400" />
          </div>
          <p className="text-2xl font-bold text-rose-400 font-mono mt-1">-{formatINR(fc.expectedOutflows30Days)}</p>
          <span className="text-[10px] text-slate-400">Includes {formatINR(fc.recurringBillsTotal || 0)} recurring</span>
        </Card>

        <Card className="p-4 bg-navy-900 border border-navy-800">
          <span className="text-[10px] uppercase font-bold text-slate-400 block mb-1">Projected 30-Day Balance</span>
          <p className="text-2xl font-bold text-cyan-300 font-mono">{formatINR(fc.projectedCashIn30Days)}</p>
          <span className="text-[10px] text-emerald-400 font-mono">
            +{formatINR(fc.projectedCashIn30Days - fc.currentLiquidCash)} net surplus
          </span>
        </Card>
      </div>

      {/* Visual Chart with Confidence Interval Bounds */}
      <Card className="p-6 bg-navy-900 border border-navy-700/80">
        <CardHeader className="p-0 pb-4">
          <CardTitle className="text-sm flex items-center justify-between">
            <span className="flex items-center gap-2">
              <TrendingUp className="w-4 h-4 text-cyan-400" />
              <span>30-Day Projected Cash Flow with Confidence Interval Bands</span>
            </span>
            <span className="text-[10px] text-slate-400 font-mono">MONTE-CARLO STOCHASTIC PROJECTION</span>
          </CardTitle>
        </CardHeader>

        <div className="h-64 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={fc.forecastPoints || []}>
              <defs>
                <linearGradient id="projectedGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#06b6d4" stopOpacity={0.4} />
                  <stop offset="95%" stopColor="#06b6d4" stopOpacity={0.0} />
                </linearGradient>
                <linearGradient id="confidenceBand" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#38bdf8" stopOpacity={0.15} />
                  <stop offset="95%" stopColor="#38bdf8" stopOpacity={0.02} />
                </linearGradient>
              </defs>
              <XAxis dataKey="date" stroke="#64748b" fontSize={10} tickLine={false} />
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
                        <p className="font-bold text-white">{data.date}</p>
                        <p className="text-cyan-300">Projected: {formatINR(data.projectedBalance)}</p>
                        <p className="text-slate-400 text-[10px]">
                          Confidence Bounds: {formatINR(data.confidenceLow)} – {formatINR(data.confidenceHigh)}
                        </p>
                      </div>
                    );
                  }
                  return null;
                }}
              />
              <Legend formatter={(val) => <span className="text-[10px] text-slate-300">{val}</span>} />
              <Area
                type="monotone"
                dataKey="confidenceHigh"
                stroke="#38bdf8"
                strokeDasharray="3 3"
                fillOpacity={1}
                fill="url(#confidenceBand)"
                name="Confidence Ceiling (95%)"
              />
              <Area
                type="monotone"
                dataKey="projectedBalance"
                stroke="#06b6d4"
                strokeWidth={2.5}
                fillOpacity={1}
                fill="url(#projectedGrad)"
                name="Projected Balance"
              />
              <Area
                type="monotone"
                dataKey="confidenceLow"
                stroke="#64748b"
                strokeDasharray="3 3"
                fill="none"
                name="Confidence Floor"
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </Card>

      {/* 2-Column: Upcoming Recurring Bills & Assumptions */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recurring Bills Schedule */}
        <Card className="p-6 bg-navy-900 border border-navy-700/80">
          <CardHeader className="p-0 pb-3">
            <CardTitle className="text-sm flex items-center justify-between">
              <span className="flex items-center gap-2">
                <Calendar className="w-4 h-4 text-cyan-400" />
                Upcoming Recurring Bills & Subscriptions
              </span>
              <span className="text-[10px] font-mono text-cyan-400 font-bold">
                {formatINR(fc.recurringBillsTotal || 0)} / mo
              </span>
            </CardTitle>
          </CardHeader>
          <p className="text-xs text-slate-400 mb-3">
            Detected recurring obligations factored into your 30-day outflow estimates.
          </p>

          <div className="space-y-2">
            {(fc.recurringBills || []).map((bill, i) => (
              <div
                key={i}
                className="flex items-center justify-between p-2.5 bg-navy-950/70 rounded-lg border border-navy-800 text-xs"
              >
                <div className="flex items-center gap-2.5">
                  <Clock className="w-3.5 h-3.5 text-slate-400" />
                  <div>
                    <span className="font-semibold text-white block">{bill.billName}</span>
                    <span className="text-[10px] text-slate-400">Due on Day {bill.dueDayOfMonth} of every month</span>
                  </div>
                </div>

                <div className="text-right">
                  <span className="font-bold text-white font-mono">{formatINR(bill.amount)}</span>
                  <Badge variant="cyan" size="sm" className="ml-2">
                    {bill.status}
                  </Badge>
                </div>
              </div>
            ))}
          </div>
        </Card>

        {/* Assumptions & Uncertainty */}
        <Card className="p-6 bg-navy-900 border border-navy-700/80 flex flex-col justify-between">
          <div>
            <CardHeader className="p-0 pb-3">
              <CardTitle className="text-sm flex items-center gap-2">
                <Layers className="w-4 h-4 text-cyan-400" />
                <span>Forecasting Model Assumptions & Uncertainty</span>
              </CardTitle>
            </CardHeader>

            <div className="space-y-3 mb-4">
              <span className="text-xs font-semibold text-slate-300 block">Baseline Hypotheses:</span>
              {(fc.forecastAssumptions || []).map((asm, i) => (
                <div key={i} className="flex items-start gap-2 text-xs text-slate-300">
                  <div className="w-1.5 h-1.5 rounded-full bg-cyan-400 mt-1.5 shrink-0" />
                  <span>{asm}</span>
                </div>
              ))}

              <div className="pt-2">
                <span className="text-xs font-semibold text-slate-300 block mb-1">Uncertainty Note:</span>
                <p className="text-xs text-slate-400 leading-relaxed bg-navy-950 p-2.5 rounded border border-navy-800">
                  {fc.uncertaintyNotes}
                </p>
              </div>
            </div>
          </div>

          <div className="p-3 bg-navy-950 border border-slate-700/60 rounded-lg flex items-start gap-2 text-[11px] text-slate-400">
            <Info className="w-4 h-4 text-cyan-400 shrink-0 mt-0.5" />
            <p>{fc.disclaimer}</p>
          </div>
        </Card>
      </div>
    </div>
  );
};

