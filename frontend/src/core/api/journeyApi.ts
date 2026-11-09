import { apiClient, ApiResponse } from './apiClient';

export interface JourneyStepTemplate {
  stepKey: string;
  stepOrder: number;
  title: string;
  description: string;
  requiredDocuments: string[];
  aiGuidance: string;
  fieldKeys: string[];
}

export interface JourneyTemplate {
  journeyType: string;
  domain: 'INSURANCE' | 'LENDING' | 'FINTECH';
  title: string;
  description: string;
  estimatedMinutes: number;
  totalSteps: number;
  steps: JourneyStepTemplate[];
}

export interface JourneyStepRecord {
  id: string;
  stepKey: string;
  stepOrder: number;
  title: string;
  description: string;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'SKIPPED';
  stepData?: Record<string, any>;
  requiredDocuments: string[];
  aiGuidance: string;
  completedAt?: string;
  updatedAt: string;
}

export interface UserJourney {
  id: string;
  journeyType: string;
  journeyDomain: 'INSURANCE' | 'LENDING' | 'FINTECH';
  title: string;
  description: string;
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'PAUSED' | 'COMPLETED' | 'CANCELLED';
  currentStepKey: string;
  currentStepIndex: number;
  totalSteps: number;
  progressPercentage: number;
  journeyData: Record<string, any>;
  cancellationReason?: string;
  simulated: boolean;
  version: number;
  steps: JourneyStepRecord[];
  nextRecommendedAction: string;
  pendingRequiredDocuments: string[];
  createdAt: string;
  updatedAt: string;
  completedAt?: string;
}

export interface StartJourneyRequest {
  journeyType: string;
  initialData?: Record<string, any>;
}

export interface CompleteStepRequest {
  stepKey: string;
  stepData?: Record<string, any>;
  userConfirmationApproved?: boolean;
  expectedVersion?: number;
}

export interface UpdateStepDataRequest {
  stepKey: string;
  stepData?: Record<string, any>;
  expectedVersion?: number;
}

export interface CancelJourneyRequest {
  reason?: string;
  expectedVersion?: number;
}

export interface JourneyStats {
  totalJourneys: number;
  activeJourneys: number;
  completedJourneys: number;
  pausedJourneys: number;
  cancelledJourneys: number;
  averageProgressPercentage: number;
  domainDistribution: Record<string, number>;
}

export interface JourneyRecommendation {
  journeyType: string;
  domain: string;
  title: string;
  rationale: string;
  estimatedMinutes: number;
  suggestedFirstStep: string;
  priorityOrder: number;
}

export const journeyApi = {
  async getTemplates(): Promise<JourneyTemplate[]> {
    const res = await apiClient.get<JourneyTemplate[]>('/journeys/templates');
    return res.data;
  },

  async getUserJourneys(params?: { status?: string; domain?: string }): Promise<UserJourney[]> {
    const query = new URLSearchParams();
    if (params?.status && params.status !== 'ALL') query.append('status', params.status);
    if (params?.domain && params.domain !== 'ALL') query.append('domain', params.domain);

    const qs = query.toString();
    const res = await apiClient.get<UserJourney[]>(`/journeys${qs ? `?${qs}` : ''}`);
    return res.data;
  },

  async getJourney(id: string): Promise<UserJourney> {
    const res = await apiClient.get<UserJourney>(`/journeys/${id}`);
    return res.data;
  },

  async startJourney(req: StartJourneyRequest): Promise<UserJourney> {
    const res = await apiClient.post<UserJourney>('/journeys/start', req);
    return res.data;
  },

  async pauseJourney(id: string, expectedVersion?: number): Promise<UserJourney> {
    const qs = expectedVersion !== undefined ? `?expectedVersion=${expectedVersion}` : '';
    const res = await apiClient.post<UserJourney>(`/journeys/${id}/pause${qs}`);
    return res.data;
  },

  async resumeJourney(id: string, expectedVersion?: number): Promise<UserJourney> {
    const qs = expectedVersion !== undefined ? `?expectedVersion=${expectedVersion}` : '';
    const res = await apiClient.post<UserJourney>(`/journeys/${id}/resume${qs}`);
    return res.data;
  },

  async updateStepData(id: string, req: UpdateStepDataRequest): Promise<UserJourney> {
    const res = await apiClient.put<UserJourney>(`/journeys/${id}/step`, req);
    return res.data;
  },

  async completeStep(id: string, req: CompleteStepRequest): Promise<UserJourney> {
    const res = await apiClient.post<UserJourney>(`/journeys/${id}/step/complete`, req);
    return res.data;
  },

  async cancelJourney(id: string, req?: CancelJourneyRequest): Promise<UserJourney> {
    const res = await apiClient.post<UserJourney>(`/journeys/${id}/cancel`, req || {});
    return res.data;
  },

  async getJourneyStats(): Promise<JourneyStats> {
    const res = await apiClient.get<JourneyStats>('/journeys/stats');
    return res.data;
  },

  async getRecommendations(): Promise<JourneyRecommendation[]> {
    const res = await apiClient.get<JourneyRecommendation[]>('/journeys/recommendations');
    return res.data;
  },
};
