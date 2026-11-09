export type Role = 'ROLE_CUSTOMER' | 'ROLE_LOAN_OFFICER' | 'ROLE_UNDERWRITER' | 'ROLE_ADMIN';

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  avatarUrl?: string;
  financialHealthScore: number;
  onboardingCompleted?: boolean;
  onboardingStep?: number;
}

export type AccountType = 'CHECKING' | 'SAVINGS' | 'CREDIT_CARD' | 'INVESTMENT';

export interface FinancialAccount {
  id: string;
  institutionName: string;
  accountType: AccountType;
  accountMask: string;
  currentBalance: number;
  availableBalance: number;
  currency: string;
  lastSyncedAt: string;
  trendPercentage: number;
}

export type TransactionCategory =
  | 'HOUSING'
  | 'GROCERIES'
  | 'DINING'
  | 'HEALTHCARE'
  | 'LOAN_PAYMENT'
  | 'SALARY'
  | 'INVESTMENTS'
  | 'ENTERTAINMENT'
  | 'TRANSPORTATION'
  | 'UTILITIES'
  | 'SHOPPING'
  | 'MISCELLANEOUS';

export interface Transaction {
  id: string;
  accountId: string;
  accountName: string;
  amount: number; // positive = income, negative = expense
  currency: string;
  date: string;
  merchantName: string;
  category: TransactionCategory;
  isRecurring: boolean;
  status: 'POSTED' | 'PENDING';
}

export interface Budget {
  id: string;
  category: TransactionCategory;
  monthlyLimit: number;
  currentSpent: number;
  alertThresholdPercent: number;
}

export type LoanType = 'PERSONAL' | 'MORTGAGE' | 'AUTO' | 'SME_CREDIT';
export type LoanStatus = 'DRAFT' | 'SUBMITTED' | 'UNDERWRITING' | 'APPROVED' | 'DISBURSED' | 'REJECTED';

export interface LoanProduct {
  id: string;
  name: string;
  type: LoanType;
  minPrincipal: number;
  maxPrincipal: number;
  apr: number;
  minTermMonths: number;
  maxTermMonths: number;
  features: string[];
}

export interface ActiveLoan {
  id: string;
  applicationNumber: string;
  productName: string;
  type: LoanType;
  totalPrincipal: number;
  remainingBalance: number;
  interestRateApr: number;
  monthlyEmi: number;
  nextPaymentDate: string;
  termMonths: number;
  monthsRemaining: number;
  status: LoanStatus;
}

export type InsuranceCategory = 'HEALTH' | 'TERM_LIFE' | 'AUTO' | 'HOMEOWNERS';
export type PolicyStatus = 'ACTIVE' | 'LAPSED' | 'RENEWAL_DUE' | 'CLAIM_PENDING';

export interface InsurancePolicy {
  id: string;
  policyNumber: string;
  carrierName: string;
  category: InsuranceCategory;
  productName: string;
  coverageAmount: number;
  monthlyPremium: number;
  renewalDate: string;
  status: PolicyStatus;
  deductible: number;
}

export interface InsuranceClaim {
  id: string;
  claimNumber: string;
  policyId: string;
  policyName: string;
  category: InsuranceCategory;
  claimAmount: number;
  incidentDate: string;
  submissionDate: string;
  status: 'SUBMITTED' | 'IN_REVIEW' | 'APPROVED' | 'SETTLED' | 'REJECTED';
  aiFraudRiskScore: number;
}

export interface FinancialInsight {
  id: string;
  title: string;
  description: string;
  category: 'SAVINGS' | 'LOAN_OPTIMIZATION' | 'INSURANCE_GAP' | 'INVESTMENT';
  impactAmount?: number;
  actionText: string;
  actionRoute: string;
  urgency: 'HIGH' | 'MEDIUM' | 'LOW';
  createdAt: string;
}

export interface ChatMessage {
  id: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
  timestamp: string;
  toolInvocations?: {
    tool: string;
    params: Record<string, any>;
    resultSummary: string;
  }[];
  citations?: {
    documentName: string;
    page: number;
    textExcerpt: string;
  }[];
}

