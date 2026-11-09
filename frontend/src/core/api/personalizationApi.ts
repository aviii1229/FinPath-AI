import { apiClient, ApiResponse } from './apiClient';

export interface UserPersonalizationProfile {
  age?: number;
  employmentCategory?: string;
  occupation?: string;
  monthlyIncome?: number;
  monthlyExpenses?: number;
  existingObligations?: number;
  approximateSavings?: number;
  financialGoals?: string[];
  preferredAssistance?: string;
  financialKnowledge?: string;
  communicationStyle?: string;
  shortTermGoal?: string;
  longTermGoal?: string;
  onboardingCompleted?: boolean;
  onboardingStep?: number;
  city?: string;
}

export interface RelevantModule {
  moduleId: string; // 'INSURANCE' | 'LENDING' | 'FINTECH' | 'AI_ORCHESTRATOR'
  title: string;
  subtitle: string;
  priority: 'HIGH' | 'MEDIUM' | 'LOW';
  matchReason: string;
  route: string;
  iconName: string;
}

export interface SuggestedJourney {
  journeyId: string;
  title: string;
  category: string;
  description: string;
  actionLabel: string;
  route: string;
  estimatedMinutes: number;
  impactBadge: string;
}

export interface DashboardCustomization {
  primaryFocusArea: string;
  calculatedMonthlySurplus: number;
  debtToIncomeRatio: number; // percentage
  savingsRatePercentage: number; // percentage
  recommendedEmergencyFund: number;
  monthsOfEmergencyRunway: number;
  recommendedFeatureTags: string[];
  keyActionAlerts: string[];
}

export interface PersonalizationResponse {
  onboardingCompleted: boolean;
  currentStep: number;
  contextualWelcomeTitle: string;
  contextualWelcomeMessage: string;
  relevantModules: RelevantModule[];
  suggestedJourneys: SuggestedJourney[];
  dashboardCustomization: DashboardCustomization;
  profile: UserPersonalizationProfile;
}

export interface OnboardingRequest {
  firstName?: string;
  lastName?: string;
  age?: number;
  city?: string;
  employmentCategory?: string;
  occupation?: string;
  monthlyIncome?: number;
  monthlyExpenses?: number;
  existingObligations?: number;
  approximateSavings?: number;
  financialGoals?: string[];
  preferredAssistance?: string;
  financialKnowledge?: string;
  communicationStyle?: string;
  shortTermGoal?: string;
  longTermGoal?: string;
  step?: number;
  isCompleted?: boolean;
}

// Resilient default Indian Rupee fallback if backend server is starting up or offline
const defaultFallbackPersonalization: PersonalizationResponse = {
  onboardingCompleted: true,
  currentStep: 4,
  contextualWelcomeTitle: 'Personalized Financial Dashboard | Sarah',
  contextualWelcomeMessage:
    'Based on your ₹75,000 monthly income, your Fixed Obligation Ratio is 11.4% with a healthy 43.6% monthly savings rate. Your primary focus areas (Health Insurance, Life Insurance, Smart Budgeting) are prioritized below.',
  relevantModules: [
    {
      moduleId: 'INSURANCE',
      title: 'Insurance AI',
      subtitle: 'Coverage Discovery & Policy RAG',
      priority: 'HIGH',
      matchReason: 'Matches your selected Health & Term Life insurance goals',
      route: '/insurance',
      iconName: 'Shield',
    },
    {
      moduleId: 'FINTECH',
      title: 'Fintech & PFM',
      subtitle: 'Smart Budgeting & Cashflow Forecasting',
      priority: 'HIGH',
      matchReason: 'Optimizes your surplus to reach your emergency reserve and savings milestones',
      route: '/fintech',
      iconName: 'Wallet',
    },
    {
      moduleId: 'LENDING',
      title: 'Lending AI',
      subtitle: 'FOIR Pre-qualification & EMI Amortization',
      priority: 'MEDIUM',
      matchReason: 'Calculates borrowing power at prime 8.50% APR with zero score impact',
      route: '/lending',
      iconName: 'Landmark',
    },
    {
      moduleId: 'AI_ORCHESTRATOR',
      title: 'FinPath AI Co-Pilot',
      subtitle: 'Unified Cross-Domain Financial Advisor',
      priority: 'HIGH',
      matchReason: 'Answers complex financial questions with step-by-step mathematical explanations',
      route: '/ai-assistant',
      iconName: 'Sparkles',
    },
  ],
  suggestedJourneys: [
    {
      journeyId: 'j-insurance-compare',
      title: 'Compare Comprehensive Health & Life Policies',
      category: 'INSURANCE',
      description: 'Review top insurers side-by-side with zero deductibles and clear waiting period metrics.',
      actionLabel: 'Explore Policies',
      route: '/insurance',
      estimatedMinutes: 4,
      impactBadge: 'High Protection',
    },
    {
      journeyId: 'j-fintech-budget',
      title: 'Establish Dynamic Category Spending Limits',
      category: 'FINTECH',
      description: 'Set monthly spending limits for Groceries, Dining, and Subscriptions with AI alerts.',
      actionLabel: 'Setup Budgets',
      route: '/fintech',
      estimatedMinutes: 5,
      impactBadge: 'Wealth Building',
    },
    {
      journeyId: 'j-lending-eligibility',
      title: 'Check Real-Time Borrowing Power & EMIs',
      category: 'LENDING',
      description: 'Calculate monthly EMI schedules at prime APRs with zero impact on credit bureau scores.',
      actionLabel: 'Calculate EMI',
      route: '/lending',
      estimatedMinutes: 3,
      impactBadge: 'Low Cost Borrowing',
    },
  ],
  dashboardCustomization: {
    primaryFocusArea: 'Health Insurance, Life Insurance, Smart Budgeting',
    calculatedMonthlySurplus: 32720,
    debtToIncomeRatio: 11.4,
    savingsRatePercentage: 43.6,
    recommendedEmergencyFund: 202440,
    monthsOfEmergencyRunway: 10,
    recommendedFeatureTags: [
      'Policy Comparison Matrix',
      'Live EMI Amortization',
      '30-Day Liquidity Forecast',
      'Automated Expense Tagging',
    ],
    keyActionAlerts: [
      'Emergency reserve is solid at 10 months of living expenses.',
    ],
  },
  profile: {
    age: 32,
    city: 'Bengaluru',
    employmentCategory: 'SALARIED',
    occupation: 'Senior Systems Architect',
    monthlyIncome: 75000,
    monthlyExpenses: 33740,
    existingObligations: 8540,
    approximateSavings: 345250,
    financialGoals: ['HEALTH_INSURANCE', 'LIFE_INSURANCE', 'MONTHLY_BUDGETING', 'SAVINGS_PLANNING'],
    preferredAssistance: 'AI_CONVERSATIONAL',
    financialKnowledge: 'INTERMEDIATE',
    communicationStyle: 'ANALYTICAL',
    shortTermGoal: 'Optimize monthly savings rate to 35% with emergency reserve',
    longTermGoal: 'Family health security and term insurance portfolio',
    onboardingCompleted: true,
    onboardingStep: 4,
  },
};

export const personalizationApi = {
  getPersonalization: async (): Promise<PersonalizationResponse> => {
    try {
      const res = await apiClient.get<PersonalizationResponse>('/personalization');
      return res.data;
    } catch (err) {
      // Check if local storage has saved personalization
      const local = localStorage.getItem('finpath_personalization');
      if (local) {
        try {
          return JSON.parse(local);
        } catch (e) {}
      }
      return defaultFallbackPersonalization;
    }
  },

  saveOnboardingStep: async (req: OnboardingRequest): Promise<PersonalizationResponse> => {
    try {
      const res = await apiClient.post<PersonalizationResponse>('/personalization/onboarding/step', req);
      localStorage.setItem('finpath_personalization', JSON.stringify(res.data));
      return res.data;
    } catch (err) {
      const updated: PersonalizationResponse = {
        ...defaultFallbackPersonalization,
        currentStep: req.step || 2,
        onboardingCompleted: false,
        profile: {
          ...defaultFallbackPersonalization.profile,
          ...req,
        },
      };
      localStorage.setItem('finpath_personalization', JSON.stringify(updated));
      return updated;
    }
  },

  completeOnboarding: async (req: OnboardingRequest): Promise<PersonalizationResponse> => {
    try {
      const res = await apiClient.post<PersonalizationResponse>('/personalization/onboarding/complete', req);
      localStorage.setItem('finpath_personalization', JSON.stringify(res.data));
      return res.data;
    } catch (err) {
      const income = req.monthlyIncome || 75000;
      const expenses = req.monthlyExpenses || 33740;
      const obligations = req.existingObligations || 0;
      const savings = req.approximateSavings || 100000;
      const surplus = income - expenses - obligations;
      const dti = income > 0 ? (obligations / income) * 100 : 0;
      const savingsRate = income > 0 ? (surplus / income) * 100 : 0;

      const completed: PersonalizationResponse = {
        ...defaultFallbackPersonalization,
        onboardingCompleted: true,
        currentStep: 4,
        contextualWelcomeTitle: `Personalized Financial Plan | ${req.firstName || 'Member'}`,
        contextualWelcomeMessage: `Welcome! Your custom financial roadmap is active. Monthly surplus is ₹${Math.max(0, surplus).toLocaleString('en-IN')} with an emergency runway of ${Math.floor(savings / (expenses || 1))} months.`,
        dashboardCustomization: {
          ...defaultFallbackPersonalization.dashboardCustomization,
          calculatedMonthlySurplus: surplus,
          debtToIncomeRatio: parseFloat(dti.toFixed(1)),
          savingsRatePercentage: parseFloat(savingsRate.toFixed(1)),
          monthsOfEmergencyRunway: Math.floor(savings / (expenses || 1)),
        },
        profile: {
          ...defaultFallbackPersonalization.profile,
          ...req,
          onboardingCompleted: true,
          onboardingStep: 4,
        },
      };
      localStorage.setItem('finpath_personalization', JSON.stringify(completed));
      return completed;
    }
  },

  updateProfile: async (req: OnboardingRequest): Promise<PersonalizationResponse> => {
    try {
      const res = await apiClient.put<PersonalizationResponse>('/personalization/profile', req);
      localStorage.setItem('finpath_personalization', JSON.stringify(res.data));
      return res.data;
    } catch (err) {
      return personalizationApi.completeOnboarding(req);
    }
  },
};

