import { apiClient, ApiResponse } from './apiClient';

export interface PrivacyConsent {
  dataCollectionConsent: boolean;
  aiProcessingConsent: boolean;
  marketingCommunicationConsent: boolean;
  thirdPartySharingConsent: boolean;
  documentOcrStorageConsent: boolean;
  storeChatHistory: boolean;
  consentVersion: string;
  consentTimestamp?: string;
  updatedAt?: string;
}

export interface DataUsageExplanation {
  complianceFramework: string;
  dataFiduciary: string;
  legalBasis: string;
  dataCategories: Record<string, string>;
  userRightsUnderDpdp: string[];
  retentionPolicy: string;
  grievanceOfficerContact: string;
}

export interface UserDataExport {
  exportTimestamp: string;
  exportCurrency: string;
  dpdpNotice: string;
  userId: string;
  email: string;
  fullName: string;
  phoneNumber?: string;
  roles: string[];
  privacyConsents?: PrivacyConsent;
  accounts: Array<Record<string, unknown>>;
  transactions: Array<Record<string, unknown>>;
  budgets: Array<Record<string, unknown>>;
  savingsGoals: Array<Record<string, unknown>>;
  documents: Array<Record<string, unknown>>;
  loanApplications: Array<Record<string, unknown>>;
  insurancePolicies: Array<Record<string, unknown>>;
  insuranceClaims: Array<Record<string, unknown>>;
  journeys: Array<Record<string, unknown>>;
  notifications: Array<Record<string, unknown>>;
  auditLogsCount: number;
}

export const privacyApi = {
  getConsents: async (): Promise<PrivacyConsent> => {
    const res = await apiClient.get<ApiResponse<PrivacyConsent>>('/privacy/consents');
    return res.data.data;
  },

  updateConsents: async (consents: PrivacyConsent): Promise<PrivacyConsent> => {
    const res = await apiClient.put<ApiResponse<PrivacyConsent>>('/privacy/consents', consents);
    return res.data.data;
  },

  getDataUsageExplanation: async (): Promise<DataUsageExplanation> => {
    const res = await apiClient.get<ApiResponse<DataUsageExplanation>>('/privacy/data-usage');
    return res.data.data;
  },

  exportUserData: async (): Promise<UserDataExport> => {
    const res = await apiClient.get<ApiResponse<UserDataExport>>('/privacy/export');
    return res.data.data;
  },

  deleteAccount: async (): Promise<void> => {
    await apiClient.post<ApiResponse<void>>('/privacy/delete-account');
  },
};
