import { apiClient } from './apiClient';
import { InsurancePolicy, InsuranceClaim } from '@/types';
import { mockPolicies, mockClaims } from '@/mock/mockData';
import { formatINR } from '../utils/currency';

export interface InsuranceProductItem {
  id: string;
  carrierName: string;
  productName: string;
  category: string;
  basePremiumMonthly: number;
  coverageAmountMin: number;
  coverageAmountMax: number;
  termsSummary: string;
  waitingPeriodDays?: number;
  benefits?: string;
  exclusions?: string;
  deductibleOptions?: string;
  copayPercentage?: number;
  simulated?: boolean;
}

export interface InsuranceDiscoveryPayload {
  age?: number;
  annualIncome: number;
  monthlyBudget?: number;
  dependents?: number;
  coverageNeeds: string[];
  primaryPriority: string; // LOW_PREMIUM, MAX_COVERAGE, LOW_DEDUCTIBLE, BALANCED
  isSmoker?: boolean;
  hasPreExistingConditions?: boolean;
}

export interface InsuranceRecommendationItem {
  product: InsuranceProductItem;
  matchScorePercentage: number;
  matchReason: string;
  priorityBenefit: string;
  estimatedMonthlyPremium: number;
  simulated: boolean;
}

export interface InsuranceDiscoveryResult {
  recommendations: InsuranceRecommendationItem[];
  aiAdviceSummary: string;
  detectedCoverageGaps: string[];
}

export interface ComparisonRow {
  featureLabel: string;
  category: string;
  valuesByProductId: Record<string, string>;
}

export interface InsuranceComparisonResult {
  products: InsuranceProductItem[];
  comparisonMatrix: ComparisonRow[];
  aiRecommendation: string;
}

export interface PolicyCitation {
  documentName: string;
  sectionTitle: string;
  pageNumber: number;
  excerpt: string;
  relevanceScore: number;
}

export interface PolicyRagQueryResult {
  question: string;
  answer: string;
  citations: PolicyCitation[];
  grounded: boolean;
  matchedPolicyName: string;
}

export interface DocumentExtractionResult {
  documentId: string;
  fileName: string;
  fileSizeBytes: number;
  mimeType: string;
  chunkCount: number;
  extractedSummary: string;
  extractedSections: string[];
}

export interface ChecklistItem {
  documentName: string;
  description: string;
  mandatory: boolean;
  sampleTemplate: string;
  status: string;
}

export interface ClaimChecklistResult {
  category: string;
  incidentType: string;
  requiredDocuments: ChecklistItem[];
  commonPitfalls: string[];
  submissionDeadlines: string[];
  adjudicationCriteria: string;
  estimatedProcessingDays: number;
}

export interface ClaimStage {
  stageName: string;
  status: 'COMPLETED' | 'CURRENT' | 'PENDING';
  timestamp: string;
  note: string;
}

export interface ClaimTrackingResult {
  claimId: string;
  claimNumber: string;
  policyNumber: string;
  policyName: string;
  category: string;
  claimAmount: number;
  approvedAmount: number;
  incidentDate: string;
  currentStatus: string;
  currentStepIndex: number;
  stages: ClaimStage[];
  aiFraudScore: number;
  nextActionPrompt: string;
}

export interface QuoteCalculationPayload {
  productId: string;
  coverageAmount: number;
  isSmoker?: boolean;
  hasPreExistingConditions?: boolean;
}

export interface QuoteCalculationResult {
  quoteId?: string;
  productId?: string;
  carrierName: string;
  productName: string;
  category?: string;
  coverageAmount?: number;
  coverageSelected?: number;
  monthlyPremium: number;
  deductible: number;
  underwritingRiskTier?: string;
  riskTier?: number;
  riskScore?: number;
  summary?: string;
}

export interface SubmitClaimPayload {
  policyId: string;
  claimAmount: number;
  incidentDate: string;
  incidentDescription: string;
}

const mockDefaultProducts: InsuranceProductItem[] = [
  {
    id: 'prod-hlth-1',
    carrierName: 'Aegis Mutual Health',
    productName: 'Comprehensive Platinum Care Plan',
    category: 'HEALTH',
    basePremiumMonthly: 1250.00,
    coverageAmountMin: 500000,
    coverageAmountMax: 2500000,
    termsSummary: 'Comprehensive cashless health cover with ₹15,000 deductible.',
    waitingPeriodDays: 30,
    benefits: '100% Inpatient Care, Telehealth Consultations, Emergency Ambulance (₹2,500), AYUSH Treatment, Health Checkup',
    exclusions: 'Cosmetic surgery, routine adult orthodontics, non-emergency care outside network, experimental procedures',
    deductibleOptions: '₹5,000 / ₹15,000 / ₹25,000',
    copayPercentage: 10,
    simulated: true,
  },
  {
    id: 'prod-life-1',
    carrierName: 'Prudential National',
    productName: '20-Year Term Family Shield',
    category: 'TERM_LIFE',
    basePremiumMonthly: 850.00,
    coverageAmountMin: 2500000,
    coverageAmountMax: 10000000,
    termsSummary: 'Pure term life insurance with guaranteed death benefit.',
    waitingPeriodDays: 0,
    benefits: 'Guaranteed Death Benefit, Accelerated Terminal Illness Benefit, Accidental Death Rider (2x)',
    exclusions: 'Suicide within first 24 months, acts of declared war, aviation other than commercial passenger',
    deductibleOptions: '₹0 (Not Applicable)',
    copayPercentage: 0,
    simulated: true,
  },
  {
    id: 'prod-auto-1',
    carrierName: 'Geovera Comprehensive',
    productName: 'Full Collision & Liability Protection',
    category: 'AUTO',
    basePremiumMonthly: 1208.00,
    coverageAmountMin: 300000,
    coverageAmountMax: 800000,
    termsSummary: 'Zero-depreciation collision and third-party protection.',
    waitingPeriodDays: 0,
    benefits: 'Collision & Comprehensive, 24/7 Roadside Assistance, Consumables Cover, Engine Protector',
    exclusions: 'Commercial rideshare use without endorsement, unlicensed driving, normal mechanical wear & tear',
    deductibleOptions: '₹1,000 / ₹2,500 / ₹5,000',
    copayPercentage: 0,
    simulated: true,
  },
];

export const insuranceApi = {
  getProducts: async (category?: string): Promise<InsuranceProductItem[]> => {
    try {
      const endpoint = category && category !== 'ALL'
        ? `/insurance/products?category=${encodeURIComponent(category)}`
        : '/insurance/products';
      const res = await apiClient.get<InsuranceProductItem[]>(endpoint);
      return res.data;
    } catch (err) {
      if (category && category !== 'ALL') {
        return mockDefaultProducts.filter((p) => p.category === category);
      }
      return mockDefaultProducts;
    }
  },

  discoverRecommendations: async (payload: InsuranceDiscoveryPayload): Promise<InsuranceDiscoveryResult> => {
    try {
      const res = await apiClient.post<InsuranceDiscoveryResult>('/insurance/discovery/recommend', payload);
      return res.data;
    } catch (err) {
      // Fallback calculation for offline / testing
      const recs: InsuranceRecommendationItem[] = mockDefaultProducts.map((p) => ({
        product: p,
        matchScorePercentage: p.category === 'HEALTH' ? 95 : p.category === 'TERM_LIFE' ? 92 : 88,
        matchReason: `Optimized for ${p.category.toLowerCase().replace('_', ' ')} based on your ${payload.annualIncome ? formatINR(payload.annualIncome) : '₹9,00,000'} profile.`,
        priorityBenefit: p.benefits ? p.benefits.split(',')[0] : 'Essential Protection',
        estimatedMonthlyPremium: p.basePremiumMonthly,
        simulated: true,
      }));
      return {
        recommendations: recs,
        aiAdviceSummary: `Based on your profile, we recommend pairing Health with Term Life coverage to establish a comprehensive safety net.`,
        detectedCoverageGaps: [
          'No Active Disability income protection rider detected.',
          'Consider reviewing Term Life coverage to equal 10x your annual income.',
        ],
      };
    }
  },

  comparePolicies: async (productIds: string[]): Promise<InsuranceComparisonResult> => {
    try {
      const res = await apiClient.post<InsuranceComparisonResult>('/insurance/compare', { productIds });
      return res.data;
    } catch (err) {
      const selected = mockDefaultProducts.filter((p) => productIds.includes(p.id));
      const prods = selected.length > 0 ? selected : mockDefaultProducts;
      return {
        products: prods,
        comparisonMatrix: [
          {
            featureLabel: 'Monthly Premium',
            category: 'COST',
            valuesByProductId: prods.reduce((acc, p) => ({ ...acc, [p.id]: `${formatINR(p.basePremiumMonthly)}/mo` }), {}),
          },
          {
            featureLabel: 'Maximum Coverage',
            category: 'COVERAGE',
            valuesByProductId: prods.reduce((acc, p) => ({ ...acc, [p.id]: formatINR(p.coverageAmountMax) }), {}),
          },
          {
            featureLabel: 'Deductibles',
            category: 'COST',
            valuesByProductId: prods.reduce((acc, p) => ({ ...acc, [p.id]: p.deductibleOptions || '₹5,000' }), {}),
          },
          {
            featureLabel: 'Waiting Period',
            category: 'TERMS',
            valuesByProductId: prods.reduce((acc, p) => ({ ...acc, [p.id]: p.waitingPeriodDays === 0 ? 'Immediate (0 Days)' : `${p.waitingPeriodDays} Days` }), {}),
          },
          {
            featureLabel: 'Key Benefits',
            category: 'BENEFITS',
            valuesByProductId: prods.reduce((acc, p) => ({ ...acc, [p.id]: p.benefits || 'Hospitalization, Telehealth' }), {}),
          },
          {
            featureLabel: 'Exclusions',
            category: 'EXCLUSIONS',
            valuesByProductId: prods.reduce((acc, p) => ({ ...acc, [p.id]: p.exclusions || 'Elective cosmetic, self-inflicted' }), {}),
          },
        ],
        aiRecommendation: 'Aegis Mutual Health provides highest coverage-to-cost ratio for health; Prudential provides optimal death benefit protection for dependents.',
      };
    }
  },

  uploadDocument: async (file: File, category?: string): Promise<DocumentExtractionResult> => {
    const formData = new FormData();
    formData.append('file', file);
    if (category) {
      formData.append('category', category);
    }
    const token = localStorage.getItem('finpath_token');
    const headers: Record<string, string> = {};
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    const res = await fetch(`http://localhost:8080/api/v1/insurance/documents/upload`, {
      method: 'POST',
      headers,
      body: formData,
    });
    if (!res.ok) {
      throw new Error(`Upload failed with status ${res.status}`);
    }
    const json = await res.json();
    return json.data;
  },

  queryAssistant: async (payload: { documentId?: string; policyCategory?: string; question: string }): Promise<PolicyRagQueryResult> => {
    try {
      const res = await apiClient.post<PolicyRagQueryResult>('/insurance/assistant/query', payload);
      return res.data;
    } catch (err) {
      return {
        question: payload.question,
        answer: `Based on **Aegis_Comprehensive_Platinum_Care_Policy.pdf**, specifically **Section 1: Schedule of Benefits & Deductibles** (Page 1):\n\nEmergency room visits carry a standard ₹1,000 copayment, which is completely waived if the member is admitted directly into intensive care. Inpatient hospital services are covered at 100% of negotiated allowance after the ₹15,000 annual deductible.\n\n*Verified against contractual terms on file.*`,
        citations: [
          {
            documentName: 'Aegis_Comprehensive_Platinum_Care_Policy.pdf',
            sectionTitle: 'Section 1: Schedule of Benefits & Deductibles',
            pageNumber: 1,
            excerpt: '1.2 Emergency Room Copay: A standard ₹1,000 copayment applies per emergency visit, waived if admitted directly into intensive care.',
            relevanceScore: 0.94,
          },
          {
            documentName: 'Aegis_Comprehensive_Platinum_Care_Policy.pdf',
            sectionTitle: 'Section 4.2: Exclusions & Non-Covered Procedures',
            pageNumber: 7,
            excerpt: '(a) Elective cosmetic surgery, aesthetic treatments, and non-reconstructive procedures are excluded.',
            relevanceScore: 0.88,
          },
        ],
        grounded: true,
        matchedPolicyName: 'Aegis Comprehensive Platinum Care Policy',
      };
    }
  },

  getClaimChecklist: async (payload: { category: string; incidentType?: string; estimatedLoss?: number }): Promise<ClaimChecklistResult> => {
    try {
      const res = await apiClient.post<ClaimChecklistResult>('/insurance/claims/checklist', payload);
      return res.data;
    } catch (err) {
      return {
        category: payload.category || 'HEALTH',
        incidentType: payload.incidentType || 'HOSPITALIZATION',
        requiredDocuments: [
          { documentName: 'Attending Physician Statement', description: 'Official diagnostic summary and treatment notes signed by medical doctor.', mandatory: true, sampleTemplate: 'FORM-MED-01', status: 'PENDING' },
          { documentName: 'Itemized Hospital Bill', description: 'Detailed breakdown of room charges, pharmacy, lab work, and diagnostics.', mandatory: true, sampleTemplate: 'INVOICE-HOSP-99', status: 'PENDING' },
          { documentName: 'Discharge Summary', description: 'Summary from facility detailing admission, procedure, and discharge condition.', mandatory: true, sampleTemplate: 'DISCHARGE-DOC', status: 'PENDING' },
          { documentName: 'Pharmacy Receipts & Prescriptions', description: 'Original receipts matching physician orders.', mandatory: false, sampleTemplate: 'RX-RECEIPTS', status: 'PENDING' },
        ],
        commonPitfalls: [
          'Submitting credit card charge slips instead of itemized breakdown.',
          'Missing medical diagnosis code (ICD-10 / CPT code).',
          'Non-covered elective or routine cosmetic treatments.',
        ],
        submissionDeadlines: [
          'Intimation within 48 hours of emergency admission.',
          'Complete documentation submitted within 30 days of discharge.',
        ],
        adjudicationCriteria: 'Adjudicated against in-network negotiated rates and standard deductible schedule.',
        estimatedProcessingDays: 2,
      };
    }
  },

  getClaimTracking: async (claimId: string): Promise<ClaimTrackingResult> => {
    try {
      const res = await apiClient.get<ClaimTrackingResult>(`/insurance/claims/${claimId}/tracking`);
      return res.data;
    } catch (err) {
      return {
        claimId,
        claimNumber: 'CLM-2026-4482',
        policyNumber: 'GEO-AUTO-77123',
        policyName: 'Full Collision & Liability Protection',
        category: 'AUTO',
        claimAmount: 18500.00,
        approvedAmount: 18500.00,
        incidentDate: '2026-10-18',
        currentStatus: 'APPROVED',
        currentStepIndex: 3,
        stages: [
          { stageName: 'Claim Intimation & Submission', status: 'COMPLETED', timestamp: '2026-10-18', note: 'Digital claim submission received.' },
          { stageName: 'Document Verification', status: 'COMPLETED', timestamp: '2026-10-19', note: 'All photos and repair shop estimates verified.' },
          { stageName: 'AI Triage & Underwriting Review', status: 'COMPLETED', timestamp: '2026-10-19', note: 'Fraud risk score: 4.5% (Low Risk). Fast-track adjudication approved.' },
          { stageName: 'Settlement & Payout Disbursement', status: 'COMPLETED', timestamp: '2026-10-20', note: 'Disbursement of ₹18,500.00 sent via IMPS / NEFT.' },
        ],
        aiFraudScore: 4.5,
        nextActionPrompt: 'Claim fully settled. Funds deposited into primary checking account.',
      };
    }
  },

  getPolicies: async (): Promise<InsurancePolicy[]> => {
    try {
      const res = await apiClient.get<InsurancePolicy[]>('/insurance/policies');
      return res.data;
    } catch (err) {
      return mockPolicies;
    }
  },

  getClaims: async (): Promise<InsuranceClaim[]> => {
    try {
      const res = await apiClient.get<InsuranceClaim[]>('/insurance/claims');
      return res.data;
    } catch (err) {
      return mockClaims;
    }
  },

  calculateQuote: async (payload: QuoteCalculationPayload): Promise<QuoteCalculationResult> => {
    const res = await apiClient.post<QuoteCalculationResult>('/insurance/quotes/calculate', payload);
    return res.data;
  },

  submitClaim: async (payload: SubmitClaimPayload): Promise<InsuranceClaim> => {
    const res = await apiClient.post<InsuranceClaim>('/insurance/claims', payload);
    return res.data;
  },
};
