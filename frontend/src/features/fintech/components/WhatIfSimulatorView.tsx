import React, { useState } from 'react';
import {
  Sliders,
  TrendingUp,
  ShieldCheck,
  AlertTriangle,
  ArrowRight,
  CheckCircle2,
  Scale,
  RefreshCw,
  Coins,
  Shield,
  HelpCircle,
} from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Badge } from '@/shared/components/ui/Badge';
import { Button } from '@/shared/components/ui/Button';
import { fintechApi, WhatIfSimulationRequest, WhatIfSimulationResponse } from '@/core/api/fintechApi';
import { formatINR } from '@/core/utils/currency';

interface WhatIfSimulatorViewProps {
  onRefresh?: () => void;
}

export const WhatIfSimulatorView: React.FC<WhatIfSimulatorViewProps> = () => {
  // Input simulation state
  const [extraSavings, setExtraSavings] = useState(5000);
  const [diningCut, setDiningCut] = useState(20);
  const [shoppingCut, setShoppingCut] = useState(15);
  const [loanAmount, setLoanAmount] = useState(0);
  const [loanTenure, setLoanTenure] = useState(36);
  const [loanApr, setLoanApr] = useState(9.5);
  const [insurancePremium, setInsurancePremium] = useState(0);

  // Result state
  const [result, setResult] = useState<WhatIfSimulationResponse | null>(null);
  const [isSimulating, setIsSimulating] = useState(false);

  // Auto-run simulation on mount
  React.useEffect(() => {
    runSimulation();
  }, []);

  const runSimulation = async () => {
    setIsSimulating(true);
    try {
      const simReq: WhatIfSimulationRequest = {
        additionalMonthlySavings: extraSavings,
        categoryReductionsPercent: {
          DINING: diningCut,
          SHOPPING: shoppingCut,
        },
        simulatedLoanAmount: loanAmount > 0 ? loanAmount : undefined,
        simulatedLoanTenureMonths: loanTenure,
        simulatedLoanApr: loanApr,
        simulatedInsuranceMonthlyPremium: insurancePremium,
      };
      const res = await fintechApi.simulateWhatIf(simReq);
      setResult(res);
    } catch (err) {
      console.error('Simulation failed', err);
    } finally {
      setIsSimulating(false);
    }
  };

  const getVerdictBadge = (verdict: string) => {
    switch (verdict) {
      case 'HIGHLY_FEASIBLE':
        return (
          <Badge variant="success" size="md">
            HIGHLY FEASIBLE
          </Badge>
        );
      case 'BALANCED':
        return (
          <Badge variant="cyan" size="md">
            BALANCED BUFFER
          </Badge>
        );
      default:
        return (
          <Badge variant="danger" size="md">
            CASHFLOW STRAIN
          </Badge>
        );
    }
  };

  return (
    <div className="space-y-6">
      {/* Simulation Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-base font-bold text-white flex items-center gap-2">
            <Sliders className="w-5 h-5 text-cyan-400" />
            Interactive Financial Scenario & What-If Simulator
          </h2>
          <p className="text-xs text-slate-400">
            Model the tangible cashflow consequences of new loan EMIs, discretionary expense trimming, health insurance policies, or aggressive savings targets in real-time.
          </p>
        </div>

        <Button
          variant="electric"
          size="sm"
          onClick={runSimulation}
          isLoading={isSimulating}
          leftIcon={<RefreshCw className="w-3.5 h-3.5" />}
        >
          Recalculate Scenario
        </Button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
        {/* Controls Column (5 cols) */}
        <div className="lg:col-span-5 space-y-4">
          <Card className="p-5 bg-navy-900 border border-navy-700/80 space-y-5">
            <CardHeader className="p-0 pb-1">
              <CardTitle className="text-xs uppercase font-bold text-slate-400 tracking-wider flex items-center justify-between">
                <span>Simulation Levers</span>
                <span className="text-[10px] text-cyan-400 font-mono">ADJUSTABLE</span>
              </CardTitle>
            </CardHeader>

            {/* Slider 1: Extra Monthly Savings */}
            <div>
              <div className="flex justify-between items-center text-xs mb-1">
                <span className="text-slate-300 font-medium">Extra Monthly Savings:</span>
                <span className="font-bold text-cyan-300 font-mono">{formatINR(extraSavings)}/mo</span>
              </div>
              <input
                type="range"
                min="0"
                max="30000"
                step="1000"
                value={extraSavings}
                onChange={(e) => setExtraSavings(Number(e.target.value))}
                className="w-full accent-cyan-400 cursor-pointer"
              />
              <span className="text-[10px] text-slate-400">Automate recurring deposit to wealth buffer</span>
            </div>

            {/* Slider 2: Dining Out Reduction */}
            <div>
              <div className="flex justify-between items-center text-xs mb-1">
                <span className="text-slate-300 font-medium">Trim Dining & Food Delivery:</span>
                <span className="font-bold text-emerald-400 font-mono">-{diningCut}%</span>
              </div>
              <input
                type="range"
                min="0"
                max="60"
                step="5"
                value={diningCut}
                onChange={(e) => setDiningCut(Number(e.target.value))}
                className="w-full accent-emerald-400 cursor-pointer"
              />
              <span className="text-[10px] text-slate-400">
                Saves approx {formatINR(Math.round(9840 * (diningCut / 100)))}/mo from dining
              </span>
            </div>

            {/* Slider 3: Shopping Discretionary Trim */}
            <div>
              <div className="flex justify-between items-center text-xs mb-1">
                <span className="text-slate-300 font-medium">Trim Discretionary Shopping:</span>
                <span className="font-bold text-emerald-400 font-mono">-{shoppingCut}%</span>
              </div>
              <input
                type="range"
                min="0"
                max="60"
                step="5"
                value={shoppingCut}
                onChange={(e) => setShoppingCut(Number(e.target.value))}
                className="w-full accent-emerald-400 cursor-pointer"
              />
              <span className="text-[10px] text-slate-400">
                Saves approx {formatINR(Math.round(6500 * (shoppingCut / 100)))}/mo from retail
              </span>
            </div>

            <hr className="border-navy-800" />

            {/* Illustrative Loan Commitments */}
            <div className="space-y-3">
              <span className="text-xs font-bold text-white flex items-center gap-1.5">
                <Scale className="w-3.5 h-3.5 text-amber-400" />
                Add Illustrative Loan Commitment
              </span>

              <div>
                <div className="flex justify-between items-center text-xs mb-1">
                  <span className="text-slate-300">Loan Principal:</span>
                  <span className="font-bold text-amber-400 font-mono">
                    {loanAmount === 0 ? 'None (₹0)' : formatINR(loanAmount)}
                  </span>
                </div>
                <input
                  type="range"
                  min="0"
                  max="1000000"
                  step="25000"
                  value={loanAmount}
                  onChange={(e) => setLoanAmount(Number(e.target.value))}
                  className="w-full accent-amber-400 cursor-pointer"
                />
              </div>

              {loanAmount > 0 && (
                <div className="grid grid-cols-2 gap-3 pt-1">
                  <div>
                    <label className="text-[11px] text-slate-400 block mb-1">Tenure (Months)</label>
                    <select
                      value={loanTenure}
                      onChange={(e) => setLoanTenure(Number(e.target.value))}
                      className="w-full bg-navy-950 border border-navy-700 rounded-lg px-2 py-1.5 text-xs text-white"
                    >
                      <option value="12">12 Months (1 yr)</option>
                      <option value="24">24 Months (2 yrs)</option>
                      <option value="36">36 Months (3 yrs)</option>
                      <option value="48">48 Months (4 yrs)</option>
                      <option value="60">60 Months (5 yrs)</option>
                    </select>
                  </div>

                  <div>
                    <label className="text-[11px] text-slate-400 block mb-1">APR Interest Rate</label>
                    <input
                      type="number"
                      step="0.1"
                      value={loanApr}
                      onChange={(e) => setLoanApr(Number(e.target.value))}
                      className="w-full bg-navy-950 border border-navy-700 rounded-lg px-2 py-1 text-xs text-white"
                    />
                  </div>
                </div>
              )}
            </div>

            <hr className="border-navy-800" />

            {/* Insurance Premium Addition */}
            <div>
              <div className="flex justify-between items-center text-xs mb-1">
                <span className="text-slate-300 font-medium flex items-center gap-1">
                  <Shield className="w-3.5 h-3.5 text-cyan-400" />
                  Add Insurance Premium:
                </span>
                <span className="font-bold text-cyan-300 font-mono">
                  {insurancePremium === 0 ? '₹0/mo' : `${formatINR(insurancePremium)}/mo`}
                </span>
              </div>
              <input
                type="range"
                min="0"
                max="10000"
                step="500"
                value={insurancePremium}
                onChange={(e) => setInsurancePremium(Number(e.target.value))}
                className="w-full accent-cyan-400 cursor-pointer"
              />
              <span className="text-[10px] text-slate-400">e.g. ₹3,308/mo Family Super Health Plan</span>
            </div>
          </Card>
        </div>

        {/* Results & Feasibility Column (7 cols) */}
        <div className="lg:col-span-7 space-y-4">
          {result && (
            <>
              {/* Feasibility Verdict Card */}
              <Card className="p-6 bg-navy-900 border border-navy-700/80">
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 mb-4">
                  <div className="flex items-center gap-2">
                    <span className="text-xs uppercase font-bold text-slate-400 tracking-wider">
                      Scenario Feasibility Assessment
                    </span>
                  </div>
                  {getVerdictBadge(result.feasibilityVerdict)}
                </div>

                {/* 4 Comparative KPI Blocks */}
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 mb-5">
                  {/* Monthly Surplus */}
                  <div className="p-3 bg-navy-950 rounded-lg border border-navy-800">
                    <span className="text-[10px] text-slate-400 block mb-1">Simulated Surplus</span>
                    <span className="text-lg font-bold text-white font-mono block">
                      {formatINR(result.simulatedMonthlySurplus)}
                    </span>
                    <span
                      className={`text-[10px] font-mono ${
                        result.netMonthlyCashflowDelta >= 0 ? 'text-emerald-400' : 'text-rose-400'
                      }`}
                    >
                      {result.netMonthlyCashflowDelta >= 0 ? '+' : ''}
                      {formatINR(result.netMonthlyCashflowDelta)}/mo
                    </span>
                  </div>

                  {/* Savings Rate */}
                  <div className="p-3 bg-navy-950 rounded-lg border border-navy-800">
                    <span className="text-[10px] text-slate-400 block mb-1">Savings Rate</span>
                    <span className="text-lg font-bold text-cyan-300 font-mono block">
                      {result.simulatedSavingsRate}%
                    </span>
                    <span className="text-[10px] text-slate-400 font-mono">
                      Base: {result.baselineSavingsRate}%
                    </span>
                  </div>

                  {/* Emergency Runway */}
                  <div className="p-3 bg-navy-950 rounded-lg border border-navy-800">
                    <span className="text-[10px] text-slate-400 block mb-1">Emergency Runway</span>
                    <span className="text-lg font-bold text-purple-300 font-mono block">
                      {result.simulatedRunwayMonths} mo
                    </span>
                    <span className="text-[10px] text-slate-400 font-mono">
                      Base: {result.baselineRunwayMonths} mo
                    </span>
                  </div>

                  {/* Monthly Expenses */}
                  <div className="p-3 bg-navy-950 rounded-lg border border-navy-800">
                    <span className="text-[10px] text-slate-400 block mb-1">Total Outflows</span>
                    <span className="text-lg font-bold text-slate-200 font-mono block">
                      {formatINR(result.simulatedExpenses)}
                    </span>
                    <span className="text-[10px] text-slate-400 font-mono">
                      Base: {formatINR(result.baselineExpenses)}
                    </span>
                  </div>
                </div>

                {/* Impact Observations */}
                <div className="space-y-2 pt-2">
                  <span className="text-xs font-semibold text-slate-300 block">AI Scenario Observations:</span>
                  {result.impactObservations.map((obs, i) => (
                    <div
                      key={i}
                      className="flex items-start gap-2.5 p-2.5 bg-navy-950/70 rounded-lg border border-navy-800/80 text-xs text-slate-200"
                    >
                      <CheckCircle2 className="w-3.5 h-3.5 text-cyan-400 shrink-0 mt-0.5" />
                      <span>{obs}</span>
                    </div>
                  ))}
                </div>
              </Card>

              {/* Side-by-Side Balance Waterfall */}
              <Card className="p-5 bg-navy-900 border border-navy-700/80">
                <CardHeader className="p-0 pb-3">
                  <CardTitle className="text-xs uppercase font-bold text-slate-400 tracking-wider">
                    Detailed Monthly Cashflow Delta
                  </CardTitle>
                </CardHeader>

                <div className="space-y-2 text-xs">
                  <div className="flex justify-between py-1.5 border-b border-navy-800">
                    <span className="text-slate-300">Baseline Monthly Income</span>
                    <span className="font-mono text-emerald-400 font-bold">
                      +{formatINR(result.baselineIncome)}
                    </span>
                  </div>

                  <div className="flex justify-between py-1.5 border-b border-navy-800">
                    <span className="text-slate-300">Discretionary Category Savings (Dining + Shopping)</span>
                    <span className="font-mono text-emerald-400 font-bold">
                      +{formatINR(result.categorySavingsTotal)}
                    </span>
                  </div>

                  {result.simulatedLoanEmi > 0 && (
                    <div className="flex justify-between py-1.5 border-b border-navy-800">
                      <span className="text-slate-300">Simulated Loan EMI Outflow</span>
                      <span className="font-mono text-rose-400 font-bold">
                        -{formatINR(result.simulatedLoanEmi)}
                      </span>
                    </div>
                  )}

                  {insurancePremium > 0 && (
                    <div className="flex justify-between py-1.5 border-b border-navy-800">
                      <span className="text-slate-300">Simulated Health Insurance Premium</span>
                      <span className="font-mono text-cyan-300 font-bold">
                        -{formatINR(insurancePremium)}
                      </span>
                    </div>
                  )}

                  {extraSavings > 0 && (
                    <div className="flex justify-between py-1.5 border-b border-navy-800">
                      <span className="text-slate-300">Automated Additional Savings Outflow</span>
                      <span className="font-mono text-purple-400 font-bold">
                        -{formatINR(extraSavings)}
                      </span>
                    </div>
                  )}

                  <div className="flex justify-between py-2 bg-navy-950 p-2 rounded-lg font-bold">
                    <span className="text-white">Net Liquid Margin Left After All Outflows</span>
                    <span className="font-mono text-cyan-300">
                      {formatINR(result.simulatedMonthlySurplus)}
                    </span>
                  </div>
                </div>
              </Card>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

