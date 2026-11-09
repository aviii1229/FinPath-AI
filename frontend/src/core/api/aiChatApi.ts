import { apiClient } from './apiClient';
import { ChatMessage } from '@/types';
import { initialChatMessages } from '@/mock/mockData';

export interface ChatSession {
  id: string;
  userId: string;
  sessionTitle: string;
  createdAt: string;
  updatedAt: string;
}

export interface AiJourneyStage {
  stageName: string;
  currentStep: number;
  totalSteps: number;
  stepTitle: string;
  completedSteps: string[];
  pendingSteps: string[];
}

export interface AiRecommendationCard {
  cardType: string;
  title: string;
  subtitle: string;
  badge: string;
  metrics: Record<string, string>;
  bulletPoints: string[];
  primaryActionLabel?: string;
  primaryActionUrl?: string;
}

export interface AiInteractiveAction {
  label: string;
  actionType: string; // PROMPT, NAVIGATE, ACTION
  payload: string;
  targetUrl?: string;
}

export interface AiExplainability {
  recommendationSummary: string;
  relevanceRationale: string;
  dataSourcesUsed: string[];
  underlyingAssumptions: string[];
  constraintsAndLimitations: string[];
}

export interface ToolInvocation {
  toolName: string;
  summary: string;
}

export interface ChatQueryResponse {
  messageId?: string;
  sessionId?: string;
  reply?: string;
  content?: string;
  senderRole?: string;
  toolExecutions?: ToolInvocation[];
  toolInvocations?: ToolInvocation[];
  ragCitations?: any[];
  citations?: any[];
  intent?: string;
  journeyStage?: AiJourneyStage;
  cards?: AiRecommendationCard[];
  suggestedActions?: AiInteractiveAction[];
  requiresUserInput?: boolean;
  missingFields?: string[];
  timestamp?: string;
  explainability?: AiExplainability;
  confidenceScore?: number;
  clarificationNeeded?: boolean;
  clarificationOptions?: string[];
  latencyMs?: number;
  tokensUsed?: number;
}

export const aiChatApi = {
  getSessions: async (): Promise<ChatSession[]> => {
    try {
      const res = await apiClient.get<ChatSession[]>('/ai/chat/sessions');
      return res.data;
    } catch (err) {
      return [
        {
          id: 'sess-default-1',
          userId: 'usr_sarah',
          sessionTitle: 'Default Financial Journey Consultation',
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
        },
      ];
    }
  },

  createSession: async (title?: string): Promise<ChatSession> => {
    const res = await apiClient.post<ChatSession>(
      `/ai/chat/sessions${title ? `?title=${encodeURIComponent(title)}` : ''}`
    );
    return res.data;
  },

  getSessionMessages: async (sessionId: string): Promise<ChatMessage[]> => {
    try {
      const res = await apiClient.get<ChatMessage[]>(`/ai/chat/sessions/${sessionId}/messages`);
      return res.data;
    } catch (err) {
      return initialChatMessages;
    }
  },

  sendQuery: async (
    sessionId: string,
    message: string,
    includeDomainData: boolean = true
  ): Promise<ChatQueryResponse> => {
    const res = await apiClient.post<ChatQueryResponse>('/ai/chat/orchestrate', {
      sessionId,
      message,
      includeDomainData,
    });
    return res.data;
  },

  deleteSession: async (sessionId: string): Promise<void> => {
    await apiClient.delete(`/ai/chat/sessions/${sessionId}`);
  },

  clearAllSessions: async (): Promise<void> => {
    await apiClient.delete('/ai/chat/sessions');
  },
};
