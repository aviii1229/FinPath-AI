import { apiClient } from './apiClient';
import { LoanProduct, ActiveLoan } from '@/types';
import { mockLoanProducts, mockActiveLoans } from '@/mock/mockData';

export interface PreQualifyPayload {
  requestedAmount: number;
  preferredTermMonths: number;
  loanType?: string;
}

export interface PreQualifyResult {
  approved: boolean;
  maxEligibleAmount: number;
  estimatedApr: number;
  estimatedMonthlyPayment: number;
  dtiPercentage: number;
  recommendations?: string[];
  reason?: string;
}

export interface CreateLoanApplicationPayload {
  loanProductId: string;
  requestedAmount: number;
  requestedTermMonths: number;
  loanPurpose: string;
  employmentType?: string;
  monthlyIncome?: number;
  existingEmis?: number;
  status?: string;
}

export interface LoanDiscoveryPayload {
  requestedAmount: number;
  monthlyIncome: number;
  employmentType: string;
  existingEmis: number;
  preferredTenureMonths: number;
  loanPurpose: string;
  estimatedCreditScore?: number;
}

export interface LoanRecommendation {
  productId: string;
  lenderName: string;
  productName: string;
  loanType: string;
  interestRateApr: number;
  interestRateType: string;
  monthlyEmi: number;
  totalInterest: number;
  processingFee: number;
  matchScorePercentage: number;
  matchReason: string;
  highlights: string[];
  simulated: boolean;
}

export interface LoanEligibilityResult {
  illustrativelyEligible: boolean;
  requestedAmount: number;
  maxEligibleAmount: number;
  calculatedFoirPercentage: number;
  proposedEmi: number;
  maxPermissibleEmi: number;
  estimatedApr: number;
  disclaimer: string;
  eligibilityFactors: string[];
  improvementSuggestions: string[];
  matchingProducts: LoanRecommendation[];
}

export interface EmiCalculationPayload {
  principal: number;
  annualInterestRate: number;
  tenureMonths: number;
}

export interface EmiScheduleItem {
  installmentNumber: number;
  openingBalance: number;
  principalPaid: number;
  interestPaid: number;
  monthlyPayment: number;
  closingBalance: number;
}

export interface EmiCalculationResult {
  principal: number;
  annualInterestRate: number;
  tenureMonths: number;
  monthlyEmi: number;
  totalInterest: number;
  totalRepayment: number;
  interestToPrincipalRatio: number;
  amortizationSchedule: EmiScheduleItem[];
}

export interface LoanComparisonPayload {
  productIds?: string[];
  requestedAmount: number;
  tenureMonths: number;
}

export interface LoanComparisonItem {
  productId: string;
  lenderName: string;
  productName: string;
  loanType: string;
  interestRateApr: number;
  interestRateType: string;
  processingFeePercentage: number;
  processingFeeAmount: number;
  monthlyEmi: number;
  totalInterest: number;
  totalBorrowingCost: number;
  minTermMonths: number;
  maxTermMonths: number;
  prepaymentTerms: string;
  features: string[];
  simulated: boolean;
}

export interface LoanComparisonResult {
  requestedAmount: number;
  tenureMonths: number;
  comparisonMatrix: LoanComparisonItem[];
  lowestCostLender: string;
  lowestEmiLender: string;
}

export interface LoanAssistantQueryPayload {
  query: string;
  loanType?: string;
  employmentType?: string;
  loanAmount?: number;
}

export interface LoanAssistantQueryResult {
  topic: string;
  answer: string;
  plainEnglishDefinition: string;
  practicalImpacts: string[];
  relatedTerms: string[];
  recommendedNextSteps: string[];
}

export interface LoanChecklistPayload {
  loanType: string;
  employmentType: string;
  loanAmount?: number;
}

export interface LoanChecklistItem {
  documentCode: string;
  title: string;
  description: string;
  mandatory: boolean;
  formatGuidance: string;
}

export interface LoanChecklistResult {
  loanType: string;
  employmentType: string;
  mandatoryDocuments: LoanChecklistItem[];
  optionalSupportingDocuments: LoanChecklistItem[];
  underwritingCriteria: string[];
  documentPreparationTips: string[];
}

export interface SimulateDecisionResult {
  applicationId: string;
  applicationNumber: string;
  decision: 'APPROVED' | 'CONDITIONALLY_APPROVED' | 'DOCUMENTS_REQUIRED' | 'DECLINED';
  explanation: string;
  approvedAmount: number;
  finalApr: number;
  monthlyEmi: number;
  evaluatedFoir: number;
  conditions: string[];
  requestedDocuments: string[];
  simulated: boolean;
}

export const lendingApi = {
  getProducts: async (): Promise<LoanProduct[]> => {
    try {
      const res = await apiClient.get<any[]>('/lending/products');
      return res.data.map((p) => ({
        id: p.id,
        name: p.productName || p.name,
        type: p.loanType || p.type,
        minPrincipal: p.minPrincipal,
        maxPrincipal: p.maxPrincipal,
        apr: p.minInterestRate || p.apr,
        minTermMonths: p.minTermMonths,
        maxTermMonths: p.maxTermMonths,
        features: p.features ? (Array.isArray(p.features) ? p.features : p.features.split(',')) : ['Instant Approval', 'Low Processing Fee'],
      }));
    } catch (err) {
      return mockLoanProducts;
    }
  },

  getApplications: async (): Promise<ActiveLoan[]> => {
    try {
      const res = await apiClient.get<any[]>('/lending/applications');
      return res.data.map((a) => ({
        id: a.id,
        applicationNumber: a.applicationNumber,
        productName: a.productName,
        type: a.loanType,
        totalPrincipal: a.requestedAmount,
        remainingBalance: a.requestedAmount, // or amortization balance
        interestRateApr: a.apr,
        monthlyEmi: a.monthlyEmi,
        nextPaymentDate: 'In 30 days',
        termMonths: a.requestedTermMonths,
        monthsRemaining: a.requestedTermMonths,
        status: a.status,
      }));
    } catch (err) {
      return mockActiveLoans;
    }
  },

  getApplicationById: async (id: string): Promise<any> => {
    const res = await apiClient.get<any>(`/lending/applications/${id}`);
    return res.data;
  },

  preQualify: async (payload: PreQualifyPayload): Promise<PreQualifyResult> => {
    const res = await apiClient.post<PreQualifyResult>('/lending/eligibility/pre-qualify', payload);
    return res.data;
  },

  discoverEligibility: async (payload: LoanDiscoveryPayload): Promise<LoanEligibilityResult> => {
    const res = await apiClient.post<LoanEligibilityResult>('/lending/discovery/eligibility', payload);
    return res.data;
  },

  calculateEmi: async (payload: EmiCalculationPayload): Promise<EmiCalculationResult> => {
    const res = await apiClient.post<EmiCalculationResult>('/lending/calculator/emi', payload);
    return res.data;
  },

  compareLoans: async (payload: LoanComparisonPayload): Promise<LoanComparisonResult> => {
    const res = await apiClient.post<LoanComparisonResult>('/lending/compare', payload);
    return res.data;
  },

  createApplication: async (payload: CreateLoanApplicationPayload): Promise<any> => {
    const res = await apiClient.post<any>('/lending/applications', payload);
    return res.data;
  },

  updateApplicationStatus: async (
    applicationId: string,
    status: string,
    notes?: string,
    missingDocuments?: string
  ): Promise<any> => {
    const res = await apiClient.patch<any>(`/lending/applications/${applicationId}/status`, {
      status,
      notes,
      missingDocuments,
    });
    return res.data;
  },

  simulateDecision: async (applicationId: string): Promise<SimulateDecisionResult> => {
    const res = await apiClient.post<SimulateDecisionResult>(`/lending/applications/${applicationId}/simulate-decision`);
    return res.data;
  },

  askAssistant: async (payload: LoanAssistantQueryPayload): Promise<LoanAssistantQueryResult> => {
    const res = await apiClient.post<LoanAssistantQueryResult>('/lending/assistant/query', payload);
    return res.data;
  },

  getChecklist: async (payload: LoanChecklistPayload): Promise<LoanChecklistResult> => {
    const res = await apiClient.post<LoanChecklistResult>('/lending/assistant/checklist', payload);
    return res.data;
  },
};
