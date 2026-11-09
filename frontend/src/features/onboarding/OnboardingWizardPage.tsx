import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Sparkles,
  User,
  Shield,
  Landmark,
  Wallet,
  ArrowRight,
  ArrowLeft,
  CheckCircle2,
  Info,
  HeartHandshake,
  GraduationCap,
  Home,
  Receipt,
  PiggyBank,
  BookOpen,
  Check,
  HelpCircle,
  TrendingUp,
} from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Button } from '@/shared/components/ui/Button';
import { Badge } from '@/shared/components/ui/Badge';
import { Input } from '@/shared/components/ui/Input';
import { useAuth } from '@/core/auth/AuthContext';
import { personalizationApi, OnboardingRequest } from '@/core/api/personalizationApi';
import { formatINR } from '@/core/utils/currency';

export const OnboardingWizardPage: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  // Wizard Step (1: Basic Info, 2: Financial Overview, 3: Goals, 4: Preferences)
  const [currentStep, setCurrentStep] = useState<number>(1);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [saveSuccessMsg, setSaveSuccessMsg] = useState<string>('');

  // Step 1: Basic Information
  const [firstName, setFirstName] = useState(user?.firstName || '');
  const [lastName, setLastName] = useState(user?.lastName || '');
  const [age, setAge] = useState<number>(28);
  const [city, setCity] = useState<string>('Bengaluru');
  const [employmentCategory, setEmploymentCategory] = useState<string>('SALARIED');
  const [occupation, setOccupation] = useState<string>('Software Engineer');

  // Step 2: Financial Overview
  const [monthlyIncome, setMonthlyIncome] = useState<number>(75000);
  const [monthlyExpenses, setMonthlyExpenses] = useState<number>(33740);
  const [existingObligations, setExistingObligations] = useState<number>(8540);
  const [approximateSavings, setApproximateSavings] = useState<number>(345250);

  // Step 3: Financial Goals (Multi-select)
  const [selectedGoals, setSelectedGoals] = useState<string[]>([
    'HEALTH_INSURANCE',
    'MONTHLY_BUDGETING',
    'SAVINGS_PLANNING',
  ]);

  // Step 4: Preferences
  const [preferredAssistance, setPreferredAssistance] = useState<string>('AI_CONVERSATIONAL');
  const [financialKnowledge, setFinancialKnowledge] = useState<string>('INTERMEDIATE');
  const [communicationStyle, setCommunicationStyle] = useState<string>('ANALYTICAL');
  const [shortTermGoal, setShortTermGoal] = useState<string>('Build 6-month emergency reserve');
  const [longTermGoal, setLongTermGoal] = useState<string>('Comprehensive family health protection and home loan');

  useEffect(() => {
    // Load existing profile if available
    personalizationApi.getPersonalization().then((data) => {
      if (data && data.profile) {
        const p = data.profile;
        if (p.age) setAge(p.age);
        if (p.city) setCity(p.city);
        if (p.employmentCategory) setEmploymentCategory(p.employmentCategory);
        if (p.occupation) setOccupation(p.occupation);
        if (p.monthlyIncome) setMonthlyIncome(p.monthlyIncome);
        if (p.monthlyExpenses) setMonthlyExpenses(p.monthlyExpenses);
        if (p.existingObligations !== undefined) setExistingObligations(p.existingObligations);
        if (p.approximateSavings) setApproximateSavings(p.approximateSavings);
        if (p.financialGoals && p.financialGoals.length > 0) setSelectedGoals(p.financialGoals);
        if (p.preferredAssistance) setPreferredAssistance(p.preferredAssistance);
        if (p.financialKnowledge) setFinancialKnowledge(p.financialKnowledge);
        if (p.communicationStyle) setCommunicationStyle(p.communicationStyle);
        if (p.shortTermGoal) setShortTermGoal(p.shortTermGoal);
        if (p.longTermGoal) setLongTermGoal(p.longTermGoal);
        if (p.onboardingStep && p.onboardingStep < 4 && !p.onboardingCompleted) {
          setCurrentStep(p.onboardingStep);
        }
      }
    });
  }, []);

  // Live calculations for Step 2
  const projectedSurplus = Math.max(0, monthlyIncome - monthlyExpenses - existingObligations);
  const dtiRatio = monthlyIncome > 0 ? ((existingObligations / monthlyIncome) * 100).toFixed(1) : '0.0';
  const emergencyMonths = monthlyExpenses > 0 ? (approximateSavings / monthlyExpenses).toFixed(1) : '0';

  const toggleGoal = (goalKey: string) => {
    if (selectedGoals.includes(goalKey)) {
      if (selectedGoals.length > 1) {
        setSelectedGoals(selectedGoals.filter((g) => g !== goalKey));
      }
    } else {
      setSelectedGoals([...selectedGoals, goalKey]);
    }
  };

  const handleNextStep = async () => {
    setIsSubmitting(true);
    try {
      const payload: OnboardingRequest = {
        firstName,
        lastName,
        age,
        city,
        employmentCategory,
        occupation,
        monthlyIncome,
        monthlyExpenses,
        existingObligations,
        approximateSavings,
        financialGoals: selectedGoals,
        preferredAssistance,
        financialKnowledge,
        communicationStyle,
        shortTermGoal,
        longTermGoal,
        step: currentStep + 1,
      };

      await personalizationApi.saveOnboardingStep(payload);
      setCurrentStep((prev) => Math.min(4, prev + 1));
    } catch (e) {
      setCurrentStep((prev) => Math.min(4, prev + 1));
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleComplete = async () => {
    setIsSubmitting(true);
    try {
      const payload: OnboardingRequest = {
        firstName,
        lastName,
        age,
        city,
        employmentCategory,
        occupation,
        monthlyIncome,
        monthlyExpenses,
        existingObligations,
        approximateSavings,
        financialGoals: selectedGoals,
        preferredAssistance,
        financialKnowledge,
        communicationStyle,
        shortTermGoal,
        longTermGoal,
        isCompleted: true,
        step: 4,
      };

      await personalizationApi.completeOnboarding(payload);
      setSaveSuccessMsg('Personalization completed! Launching your custom financial dashboard...');
      setTimeout(() => {
        navigate('/dashboard');
      }, 1000);
    } catch (e) {
      navigate('/dashboard');
    } finally {
      setIsSubmitting(false);
    }
  };

  const goalsList = [
    {
      id: 'HEALTH_INSURANCE',
      title: 'Health Insurance',
      desc: 'Comprehensive hospitalization, critical illness & cashless network cover',
      icon: Shield,
      color: 'cyan',
    },
    {
      id: 'LIFE_INSURANCE',
      title: 'Life Insurance',
      desc: 'Term life coverage to secure financial stability for your dependents',
      icon: HeartHandshake,
      color: 'emerald',
    },
    {
      id: 'PERSONAL_LOAN',
      title: 'Personal Loan',
      desc: 'Flexible unsecured borrowing for urgent needs or debt consolidation',
      icon: Landmark,
      color: 'amber',
    },
    {
      id: 'EDUCATION_LOAN',
      title: 'Education Loan',
      desc: 'Financing for higher education, university tuition & career upskilling',
      icon: GraduationCap,
      color: 'electric',
    },
    {
      id: 'HOME_LOAN',
      title: 'Home Loan',
      desc: 'Residential property purchase, construction, or balance transfer',
      icon: Home,
      color: 'cyan',
    },
    {
      id: 'EXPENSE_MANAGEMENT',
      title: 'Expense Management',
      desc: 'Automated transaction categorization & spending leakage tracking',
      icon: Receipt,
      color: 'rose',
    },
    {
      id: 'MONTHLY_BUDGETING',
      title: 'Monthly Budgeting',
      desc: 'Category-wise spending limits with real-time overspending alerts',
      icon: Wallet,
      color: 'emerald',
    },
    {
      id: 'SAVINGS_PLANNING',
      title: 'Savings Planning',
      desc: 'Emergency fund targets, automated savings rules & wealth goals',
      icon: PiggyBank,
      color: 'electric',
    },
    {
      id: 'FINANCIAL_EDUCATION',
      title: 'Financial Education',
      desc: 'Interactive AI coaching, terminology tutoring & actionable guides',
      icon: BookOpen,
      color: 'amber',
    },
  ];

  const stepTitles = [
    { num: 1, title: 'Basic Information', desc: 'Identity & Occupation' },
    { num: 2, title: 'Financial Overview', desc: 'Income, Cashflow & Obligations' },
    { num: 3, title: 'Financial Goals', desc: 'Target Priorities & Coverage' },
    { num: 4, title: 'Preferences', desc: 'Assistance & Communication Style' },
  ];

  return (
    <div className="min-h-screen bg-navy-950 text-slate-100 flex flex-col justify-between p-4 sm:p-6 lg:p-8 bg-radial-gradient">
      <div className="max-w-4xl mx-auto w-full">
        {/* Header Branding & Skip Option */}
        <div className="flex items-center justify-between pb-6 border-b border-navy-800/80 mb-6">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-cyan-500 to-electric-500 flex items-center justify-center shadow-glow-sm">
              <Sparkles className="w-5 h-5 text-white" />
            </div>
            <div>
              <h1 className="text-xl sm:text-2xl font-black text-white tracking-tight">
                FinPath AI 2.0 <span className="text-cyan-400 font-mono text-sm font-semibold">| Smart Onboarding</span>
              </h1>
              <p className="text-xs text-slate-400">
                Personalizing your financial intelligence platform for your specific goals
              </p>
            </div>
          </div>

          <Button
            variant="ghost"
            size="sm"
            onClick={() => navigate('/dashboard')}
            className="text-xs text-slate-400 hover:text-white"
          >
            Skip to Dashboard
          </Button>
        </div>

        {/* Progress Bar & Step Chips */}
        <div className="mb-8">
          <div className="flex justify-between items-center mb-3">
            <span className="text-xs font-mono font-bold text-cyan-400 uppercase tracking-wider">
              Step {currentStep} of 4: {stepTitles[currentStep - 1].title}
            </span>
            <span className="text-xs font-mono text-slate-400">{currentStep * 25}% Completed</span>
          </div>

          {/* Progress Indicator Bar */}
          <div className="w-full h-2 bg-navy-900 rounded-full overflow-hidden border border-navy-800">
            <div
              className="h-full bg-gradient-to-r from-cyan-500 to-electric-400 transition-all duration-300 rounded-full"
              style={{ width: `${currentStep * 25}%` }}
            />
          </div>

          {/* Step Breadcrumbs */}
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 mt-4">
            {stepTitles.map((st) => (
              <div
                key={st.num}
                onClick={() => {
                  if (st.num < currentStep) setCurrentStep(st.num);
                }}
                className={`p-2.5 rounded-xl border text-xs transition-all cursor-pointer ${
                  currentStep === st.num
                    ? 'bg-navy-900 border-cyan-400/80 text-white shadow-glow-sm'
                    : currentStep > st.num
                    ? 'bg-navy-950/60 border-emerald-500/40 text-emerald-300'
                    : 'bg-navy-950/40 border-navy-800 text-slate-500'
                }`}
              >
                <div className="flex items-center gap-1.5 font-bold">
                  {currentStep > st.num ? (
                    <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                  ) : (
                    <span className="w-4 h-4 rounded-full bg-navy-800 flex items-center justify-center text-[10px] font-mono">
                      {st.num}
                    </span>
                  )}
                  <span>{st.title}</span>
                </div>
                <p className="text-[10px] text-slate-400 mt-0.5 truncate">{st.desc}</p>
              </div>
            ))}
          </div>
        </div>

        {saveSuccessMsg && (
          <div className="mb-6 p-4 rounded-xl bg-emerald-500/15 border border-emerald-500/30 text-emerald-300 text-xs flex items-center gap-2 animate-fade-in">
            <CheckCircle2 className="w-4 h-4 shrink-0" />
            <span>{saveSuccessMsg}</span>
          </div>
        )}

        {/* ========================================================================= */}
        {/* STEP 1: BASIC INFORMATION */}
        {/* ========================================================================= */}
        {currentStep === 1 && (
          <Card className="p-6 sm:p-8 bg-navy-900 border border-navy-700/80 shadow-glow animate-fade-in">
            <CardHeader className="p-0 pb-6 border-b border-navy-800">
              <div className="flex items-center gap-2 mb-1">
                <User className="w-5 h-5 text-cyan-400" />
                <CardTitle className="text-lg">Tell Us About Yourself</CardTitle>
              </div>
              <p className="text-xs text-slate-400">
                We use this information to determine eligible statutory deductions, insurance risk pricing, and regional living standards.
              </p>
            </CardHeader>

            <CardContent className="p-0 pt-6 space-y-5">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <Input
                  label="First Name"
                  type="text"
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                  placeholder="e.g. Sarah"
                  required
                />
                <Input
                  label="Last Name"
                  type="text"
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                  placeholder="e.g. Connor"
                  required
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <Input
                  label="Age"
                  type="number"
                  value={age}
                  onChange={(e) => setAge(Number(e.target.value))}
                  min={18}
                  max={90}
                  helperText="Insurance underwriting parameter"
                  required
                />

                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1.5">City / Region</label>
                  <select
                    value={city}
                    onChange={(e) => setCity(e.target.value)}
                    className="w-full glass-input rounded-xl px-3 py-2 text-xs text-slate-200 focus:border-cyan-500"
                  >
                    <option value="Bengaluru">Bengaluru (Tier 1)</option>
                    <option value="Mumbai">Mumbai (Tier 1)</option>
                    <option value="Delhi NCR">Delhi NCR (Tier 1)</option>
                    <option value="Pune">Pune (Tier 1)</option>
                    <option value="Hyderabad">Hyderabad (Tier 1)</option>
                    <option value="Chennai">Chennai (Tier 1)</option>
                    <option value="Kolkata">Kolkata (Tier 1)</option>
                    <option value="Ahmedabad">Ahmedabad (Tier 2)</option>
                    <option value="Jaipur">Jaipur (Tier 2)</option>
                    <option value="Chandigarh">Chandigarh (Tier 2)</option>
                    <option value="Other">Other City / District</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1.5">Employment Category</label>
                  <select
                    value={employmentCategory}
                    onChange={(e) => setEmploymentCategory(e.target.value)}
                    className="w-full glass-input rounded-xl px-3 py-2 text-xs text-slate-200 focus:border-cyan-500"
                  >
                    <option value="SALARIED">Salaried Professional</option>
                    <option value="SELF_EMPLOYED">Self-Employed / Consultant</option>
                    <option value="BUSINESS_OWNER">Business Owner / Merchant</option>
                    <option value="STUDENT">Student / Scholar</option>
                    <option value="HOMEMAKER">Homemaker</option>
                    <option value="RETIRED">Retired</option>
                    <option value="OTHER">Other / Freelance</option>
                  </select>
                </div>
              </div>

              <Input
                label="Occupation / Field of Work"
                type="text"
                value={occupation}
                onChange={(e) => setOccupation(e.target.value)}
                placeholder="e.g. Senior Software Architect, Research Scholar, Physician"
                helperText="Helps tailor domain guidance to your profession without stereotypes"
              />

              {/* Privacy Explanation Notice */}
              <div className="p-3.5 rounded-xl bg-cyan-950/40 border border-cyan-500/20 text-cyan-200 text-xs flex items-start gap-2.5">
                <Info className="w-4 h-4 text-cyan-400 shrink-0 mt-0.5" />
                <p>
                  <span className="font-semibold text-white">Why this is necessary:</span> Demographic information is strictly utilized to calculate underwriting age bands and tax deduction frameworks under Indian tax law. We never sell your personal data.
                </p>
              </div>

              {/* Navigation Actions */}
              <div className="flex justify-end pt-4">
                <Button
                  variant="electric"
                  size="md"
                  rightIcon={<ArrowRight className="w-4 h-4" />}
                  onClick={handleNextStep}
                  disabled={isSubmitting || !firstName.trim()}
                >
                  Save & Continue to Financial Overview
                </Button>
              </div>
            </CardContent>
          </Card>
        )}

        {/* ========================================================================= */}
        {/* STEP 2: FINANCIAL OVERVIEW */}
        {/* ========================================================================= */}
        {currentStep === 2 && (
          <Card className="p-6 sm:p-8 bg-navy-900 border border-navy-700/80 shadow-glow animate-fade-in">
            <CardHeader className="p-0 pb-6 border-b border-navy-800">
              <div className="flex items-center gap-2 mb-1">
                <Wallet className="w-5 h-5 text-cyan-400" />
                <CardTitle className="text-lg">Your Financial Overview</CardTitle>
              </div>
              <p className="text-xs text-slate-400">
                Provide an approximation of your income, living expenses, existing obligations, and savings.
              </p>
            </CardHeader>

            <CardContent className="p-0 pt-6 space-y-6">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <Input
                  label="Net Monthly In-Hand Income (₹)"
                  type="number"
                  value={monthlyIncome}
                  onChange={(e) => setMonthlyIncome(Number(e.target.value))}
                  helperText="Take-home pay after taxes & EPF contributions"
                  required
                />
                <Input
                  label="Monthly Living Expenses (₹)"
                  type="number"
                  value={monthlyExpenses}
                  onChange={(e) => setMonthlyExpenses(Number(e.target.value))}
                  helperText="Rent, groceries, utilities, lifestyle & discretionary spend"
                  required
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <Input
                  label="Existing Monthly Financial Obligations / EMIs (₹)"
                  type="number"
                  value={existingObligations}
                  onChange={(e) => setExistingObligations(Number(e.target.value))}
                  helperText="Active personal loan, car loan, education loan, or credit card dues"
                />
                <Input
                  label="Approximate Liquid Savings & Investments (₹)"
                  type="number"
                  value={approximateSavings}
                  onChange={(e) => setApproximateSavings(Number(e.target.value))}
                  helperText="Savings account balance, fixed deposits, mutual funds"
                />
              </div>

              {/* Real-Time Mathematical Financial Health Preview */}
              <div className="p-4 rounded-xl bg-navy-950/80 border border-navy-800 space-y-3">
                <span className="text-[10px] font-mono font-bold text-cyan-400 uppercase tracking-wider block">
                  Real-Time Mathematical Health Indicators
                </span>
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                  <div className="p-3 rounded-lg bg-navy-900/90 border border-navy-700/60">
                    <span className="text-[10px] text-slate-400">Monthly Surplus</span>
                    <p className="text-base font-bold text-emerald-400 font-mono mt-0.5">
                      {formatINR(projectedSurplus)}
                    </p>
                    <span className="text-[9px] text-slate-500">Available for savings & goals</span>
                  </div>

                  <div className="p-3 rounded-lg bg-navy-900/90 border border-navy-700/60">
                    <span className="text-[10px] text-slate-400">Debt-to-Income (FOIR)</span>
                    <p className="text-base font-bold text-cyan-300 font-mono mt-0.5">{dtiRatio}%</p>
                    <span className="text-[9px] text-slate-500">Safe threshold is below 45%</span>
                  </div>

                  <div className="p-3 rounded-lg bg-navy-900/90 border border-navy-700/60">
                    <span className="text-[10px] text-slate-400">Emergency Runway</span>
                    <p className="text-base font-bold text-electric-300 font-mono mt-0.5">
                      {emergencyMonths} Months
                    </p>
                    <span className="text-[9px] text-slate-500">Recommended target is 6 months</span>
                  </div>
                </div>
              </div>

              {/* Privacy Explanation Notice */}
              <div className="p-3.5 rounded-xl bg-cyan-950/40 border border-cyan-500/20 text-cyan-200 text-xs flex items-start gap-2.5">
                <Info className="w-4 h-4 text-cyan-400 shrink-0 mt-0.5" />
                <p>
                  <span className="font-semibold text-white">Why this is necessary:</span> Your income and existing obligations ensure that any loan or policy suggested has an affordable monthly commitment, protecting you from over-indebtedness.
                </p>
              </div>

              {/* Navigation Actions */}
              <div className="flex items-center justify-between pt-4">
                <Button
                  variant="outline"
                  size="md"
                  leftIcon={<ArrowLeft className="w-4 h-4" />}
                  onClick={() => setCurrentStep(1)}
                >
                  Back
                </Button>

                <div className="flex items-center gap-2">
                  <Button
                    variant="ghost"
                    size="sm"
                    onClick={() => setCurrentStep(3)}
                    className="text-xs text-slate-400"
                  >
                    Skip optional fields
                  </Button>
                  <Button
                    variant="electric"
                    size="md"
                    rightIcon={<ArrowRight className="w-4 h-4" />}
                    onClick={handleNextStep}
                    disabled={isSubmitting}
                  >
                    Continue to Financial Goals
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        )}

        {/* ========================================================================= */}
        {/* STEP 3: FINANCIAL GOALS */}
        {/* ========================================================================= */}
        {currentStep === 3 && (
          <Card className="p-6 sm:p-8 bg-navy-900 border border-navy-700/80 shadow-glow animate-fade-in">
            <CardHeader className="p-0 pb-6 border-b border-navy-800">
              <div className="flex items-center gap-2 mb-1">
                <TrendingUp className="w-5 h-5 text-cyan-400" />
                <CardTitle className="text-lg">What Are Your Primary Financial Goals?</CardTitle>
              </div>
              <p className="text-xs text-slate-400">
                Select all that apply. Your selections directly configure your dashboard modules, alerts, and recommended journeys.
              </p>
            </CardHeader>

            <CardContent className="p-0 pt-6 space-y-6">
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                {goalsList.map((g) => {
                  const Icon = g.icon;
                  const isSelected = selectedGoals.includes(g.id);
                  return (
                    <div
                      key={g.id}
                      onClick={() => toggleGoal(g.id)}
                      className={`p-4 rounded-xl border cursor-pointer transition-all flex flex-col justify-between ${
                        isSelected
                          ? 'bg-electric-600/15 border-cyan-400 shadow-glow-sm'
                          : 'bg-navy-950/60 border-navy-800 hover:border-slate-600'
                      }`}
                    >
                      <div>
                        <div className="flex items-center justify-between mb-2">
                          <div
                            className={`w-8 h-8 rounded-lg flex items-center justify-center ${
                              isSelected ? 'bg-cyan-500/20 text-cyan-300' : 'bg-navy-800 text-slate-400'
                            }`}
                          >
                            <Icon className="w-4 h-4" />
                          </div>
                          <div
                            className={`w-5 h-5 rounded-full border flex items-center justify-center ${
                              isSelected
                                ? 'bg-cyan-500 border-cyan-400 text-navy-950'
                                : 'border-slate-600 text-transparent'
                            }`}
                          >
                            <Check className="w-3.5 h-3.5 stroke-[3]" />
                          </div>
                        </div>

                        <h4 className="text-xs font-bold text-white">{g.title}</h4>
                        <p className="text-[11px] text-slate-400 mt-1 leading-relaxed">{g.desc}</p>
                      </div>

                      <div className="mt-3 pt-2 border-t border-navy-800/60 flex items-center justify-between text-[10px]">
                        <span className={isSelected ? 'text-cyan-400 font-semibold' : 'text-slate-500'}>
                          {isSelected ? 'Prioritized on Dashboard' : 'Click to prioritize'}
                        </span>
                      </div>
                    </div>
                  );
                })}
              </div>

              {/* Privacy Explanation Notice */}
              <div className="p-3.5 rounded-xl bg-cyan-950/40 border border-cyan-500/20 text-cyan-200 text-xs flex items-start gap-2.5">
                <Info className="w-4 h-4 text-cyan-400 shrink-0 mt-0.5" />
                <p>
                  <span className="font-semibold text-white">No demographic stereotyping:</span> We customize modules purely on your chosen goals. Whether you are a student exploring home loans or an executive tracking daily expenses, your dashboard is tailored to you.
                </p>
              </div>

              {/* Navigation Actions */}
              <div className="flex items-center justify-between pt-4">
                <Button
                  variant="outline"
                  size="md"
                  leftIcon={<ArrowLeft className="w-4 h-4" />}
                  onClick={() => setCurrentStep(2)}
                >
                  Back
                </Button>

                <Button
                  variant="electric"
                  size="md"
                  rightIcon={<ArrowRight className="w-4 h-4" />}
                  onClick={handleNextStep}
                  disabled={isSubmitting || selectedGoals.length === 0}
                >
                  Save Goals & Continue
                </Button>
              </div>
            </CardContent>
          </Card>
        )}

        {/* ========================================================================= */}
        {/* STEP 4: PREFERENCES & ASSISTANCE STYLE */}
        {/* ========================================================================= */}
        {currentStep === 4 && (
          <Card className="p-6 sm:p-8 bg-navy-900 border border-navy-700/80 shadow-glow animate-fade-in">
            <CardHeader className="p-0 pb-6 border-b border-navy-800">
              <div className="flex items-center gap-2 mb-1">
                <HeartHandshake className="w-5 h-5 text-cyan-400" />
                <CardTitle className="text-lg">Assistance & Communication Preferences</CardTitle>
              </div>
              <p className="text-xs text-slate-400">
                Choose how FinPath AI should interact with you, and define your immediate milestones.
              </p>
            </CardHeader>

            <CardContent className="p-0 pt-6 space-y-6">
              {/* Preferred Assistance Style */}
              <div>
                <label className="block text-xs font-bold text-slate-200 mb-2 uppercase font-mono tracking-wider">
                  Preferred Financial Assistance Method
                </label>
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                  {[
                    {
                      id: 'AI_CONVERSATIONAL',
                      title: 'AI Co-Pilot Guidance',
                      desc: 'Conversational chat, natural language advice, instant Q&A',
                    },
                    {
                      id: 'SELF_DIRECTED',
                      title: 'Self-Directed Exploration',
                      desc: 'Direct calculators, comparison matrices, raw data sheets',
                    },
                    {
                      id: 'STEP_BY_STEP',
                      title: 'Sequential Checklists',
                      desc: 'Milestone checklists, guided document tracking, linear steps',
                    },
                  ].map((a) => (
                    <div
                      key={a.id}
                      onClick={() => setPreferredAssistance(a.id)}
                      className={`p-3.5 rounded-xl border cursor-pointer transition-all ${
                        preferredAssistance === a.id
                          ? 'bg-electric-600/15 border-cyan-400 shadow-glow-sm'
                          : 'bg-navy-950/60 border-navy-800 hover:border-slate-600'
                      }`}
                    >
                      <span className="text-xs font-bold text-white block">{a.title}</span>
                      <p className="text-[11px] text-slate-400 mt-1 leading-snug">{a.desc}</p>
                    </div>
                  ))}
                </div>
              </div>

              {/* Knowledge Level & Communication Style */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1.5">
                    Financial Knowledge Level
                  </label>
                  <select
                    value={financialKnowledge}
                    onChange={(e) => setFinancialKnowledge(e.target.value)}
                    className="w-full glass-input rounded-xl px-3 py-2 text-xs text-slate-200 focus:border-cyan-500"
                  >
                    <option value="BEGINNER">Beginner (Explain concepts simply, avoid jargon)</option>
                    <option value="INTERMEDIATE">Intermediate (Familiar with APR, deductibles, and FOIR)</option>
                    <option value="ADVANCED">Advanced (Detailed formulas, amortization, risk models)</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-medium text-slate-300 mb-1.5">
                    Preferred Communication Style
                  </label>
                  <select
                    value={communicationStyle}
                    onChange={(e) => setCommunicationStyle(e.target.value)}
                    className="w-full glass-input rounded-xl px-3 py-2 text-xs text-slate-200 focus:border-cyan-500"
                  >
                    <option value="ANALYTICAL">Analytical & Numeric (Ratios, metrics, and percentages)</option>
                    <option value="SIMPLE_CONCISE">Simple & Concise (Direct takeaways and next steps)</option>
                    <option value="EDUCATIONAL">Guided & Educational (Contextual advice and coaching)</option>
                  </select>
                </div>
              </div>

              {/* Target Goals Freeform */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <Input
                  label="Short-Term Milestone (Next 3–6 Months)"
                  type="text"
                  value={shortTermGoal}
                  onChange={(e) => setShortTermGoal(e.target.value)}
                  placeholder="e.g. Build emergency reserve of ₹1,50,000"
                  helperText="Primary short-term priority tracked on dashboard"
                />

                <Input
                  label="Long-Term Vision (Next 1–5 Years)"
                  type="text"
                  value={longTermGoal}
                  onChange={(e) => setLongTermGoal(e.target.value)}
                  placeholder="e.g. Buy a home or family health security"
                  helperText="Strategic planning target for wealth & protection"
                />
              </div>

              {/* Navigation Actions */}
              <div className="flex items-center justify-between pt-4 border-t border-navy-800">
                <Button
                  variant="outline"
                  size="md"
                  leftIcon={<ArrowLeft className="w-4 h-4" />}
                  onClick={() => setCurrentStep(3)}
                >
                  Back
                </Button>

                <Button
                  variant="electric"
                  size="lg"
                  rightIcon={<CheckCircle2 className="w-5 h-5 text-cyan-200" />}
                  onClick={handleComplete}
                  disabled={isSubmitting}
                  className="shadow-glow"
                >
                  Complete Onboarding & View Personalized Dashboard
                </Button>
              </div>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
};

