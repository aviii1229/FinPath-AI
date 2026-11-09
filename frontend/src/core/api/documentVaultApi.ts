import { apiClient, ApiResponse } from './apiClient';

export interface ExtractedEntity {
  fieldName: string;
  fieldLabel: string;
  extractedValue: string;
  normalizedValue?: string;
  confidence: number;
  pageNumber: number;
  sourceSnippet: string;
}

export interface DocumentVaultItem {
  id: string;
  fileName: string;
  fileSizeBytes: number;
  formattedSize: string;
  mimeType: string;
  documentCategory: string;
  categoryLabel: string;
  processingStatus: string;
  classificationConfidence: number;
  userCorrected: boolean;
  sourceJourney: string;
  documentDate?: string;
  expiryDate?: string;
  extractedSummary?: string;
  extractedEntities: ExtractedEntity[];
  createdAt: string;
  updatedAt?: string;
}

export interface CategoryCorrectionRequest {
  category: string;
  userNote?: string;
}

export interface DocumentCitation {
  pageNumber: number;
  sectionTitle: string;
  quotedSnippet: string;
  relevanceScore: number;
}

export interface DocumentQueryResponse {
  documentId: string;
  fileName: string;
  question: string;
  answer: string;
  citations: DocumentCitation[];
  disclaimer: string;
}

export interface DocumentRequirementItem {
  requirementKey: string;
  title: string;
  description: string;
  category: string;
  required: boolean;
  fulfilled: boolean;
  matchedDocumentId?: string;
  matchedDocumentName?: string;
  expired: boolean;
  statusMessage: string;
}

export interface JourneyCompleteness {
  journeyType: string;
  journeyTitle: string;
  totalRequired: number;
  totalFulfilled: number;
  completionPercentage: number;
  readyToSubmit: boolean;
  requirements: DocumentRequirementItem[];
  missingDocuments: string[];
  warnings: string[];
  regulatoryDisclaimer: string;
}

export interface VaultSummaryStats {
  totalDocuments: number;
  totalProcessed: number;
  totalFailed: number;
  totalSizeBytes: number;
  formattedTotalSize: string;
  countByCategory: Record<string, number>;
  averageCompletenessScore: number;
}

export const documentVaultApi = {
  async uploadDocument(file: File, journey?: string): Promise<DocumentVaultItem> {
    const formData = new FormData();
    formData.append('file', file);
    if (journey) {
      formData.append('journey', journey);
    }
    const res = await apiClient.postMultipart<DocumentVaultItem>(
      `/documents/upload${journey ? `?journey=${encodeURIComponent(journey)}` : ''}`,
      formData
    );
    return res.data;
  },

  async getDocuments(params?: { category?: string; status?: string; search?: string }): Promise<DocumentVaultItem[]> {
    const query = new URLSearchParams();
    if (params?.category && params.category !== 'ALL') query.append('category', params.category);
    if (params?.status && params.status !== 'ALL') query.append('status', params.status);
    if (params?.search) query.append('search', params.search);

    const qs = query.toString();
    const res = await apiClient.get<DocumentVaultItem[]>(`/documents${qs ? `?${qs}` : ''}`);
    return res.data;
  },

  async getDocument(id: string): Promise<DocumentVaultItem> {
    const res = await apiClient.get<DocumentVaultItem>(`/documents/${id}`);
    return res.data;
  },

  async correctCategory(id: string, req: CategoryCorrectionRequest): Promise<DocumentVaultItem> {
    const res = await apiClient.patch<DocumentVaultItem>(`/documents/${id}/category`, req);
    return res.data;
  },

  async deleteDocument(id: string): Promise<void> {
    await apiClient.delete<void>(`/documents/${id}`);
  },

  async downloadDocument(id: string, fileName: string): Promise<void> {
    const token = localStorage.getItem('finpath_token');
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';
    const response = await fetch(`${baseUrl}/documents/${id}/download`, {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
    });
    if (!response.ok) {
      throw new Error('Download failed');
    }
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
  },

  async queryDocument(id: string, question: string): Promise<DocumentQueryResponse> {
    const res = await apiClient.post<DocumentQueryResponse>(`/documents/${id}/query`, { question });
    return res.data;
  },

  async getCompleteness(journeyType: string): Promise<JourneyCompleteness> {
    const res = await apiClient.get<JourneyCompleteness>(`/documents/completeness/${journeyType}`);
    return res.data;
  },

  async getVaultStats(): Promise<VaultSummaryStats> {
    const res = await apiClient.get<VaultSummaryStats>('/documents/stats');
    return res.data;
  },
};

