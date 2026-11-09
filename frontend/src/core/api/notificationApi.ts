import { apiClient, ApiResponse } from './apiClient';

export interface NotificationItem {
  id: string;
  title: string;
  description: string;
  type: 'SUCCESS' | 'WARNING' | 'INFO' | 'ERROR';
  category: 'LOAN_EMI' | 'INSURANCE_RENEWAL' | 'BUDGET_ALERT' | 'SAVINGS_GOAL' | 'JOURNEY_INCOMPLETE' | 'DOCUMENT_MISSING' | 'SYSTEM';
  priority: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  actionUrl?: string;
  actionLabel?: string;
  isRead: boolean;
  readAt?: string;
  channelDeliveryStatus?: string;
  metadataJson?: string;
  createdAt: string;
}

export interface NotificationSummary {
  unreadCount: number;
  totalCount: number;
  criticalCount: number;
  highCount: number;
  recentNotifications: NotificationItem[];
  categoryCounts: Record<string, number>;
  lastEvaluatedAt?: string;
}

export interface NotificationPreference {
  inAppEnabled: boolean;
  emailEnabled: boolean;
  pushEnabled: boolean;
  loanEmiAlerts: boolean;
  insuranceRenewalAlerts: boolean;
  budgetThresholdAlerts: boolean;
  savingsGoalAlerts: boolean;
  journeyIncompleteAlerts: boolean;
  documentMissingAlerts: boolean;
  reminderDaysBeforeEmi: number;
  reminderDaysBeforeRenewal: number;
  quietHoursEnabled: boolean;
  quietHoursStart: string;
  quietHoursEnd: string;
  emailConsentGranted: boolean;
  pushConsentGranted: boolean;
  timeZone: string;
  updatedAt?: string;
}

export interface EvaluateNotificationsResult {
  evaluatedRulesCount: number;
  generatedCount: number;
  skippedDuplicatesCount: number;
  generatedNotifications: NotificationItem[];
  evaluationTimestamp: string;
}

export const notificationApi = {
  /**
   * Get user notification feed with optional filters
   */
  async getNotifications(params?: {
    category?: string;
    unreadOnly?: boolean;
    priority?: string;
  }): Promise<NotificationItem[]> {
    const query = new URLSearchParams();
    if (params?.category && params.category !== 'ALL') query.append('category', params.category);
    if (params?.unreadOnly) query.append('unreadOnly', 'true');
    if (params?.priority && params.priority !== 'ALL') query.append('priority', params.priority);

    const qs = query.toString() ? `?${query.toString()}` : '';
    const res = await apiClient.get<ApiResponse<NotificationItem[]>>(`/notifications${qs}`);
    return res.data.data;
  },

  /**
   * Get notification summary metrics and top alerts
   */
  async getSummary(): Promise<NotificationSummary> {
    const res = await apiClient.get<ApiResponse<NotificationSummary>>('/notifications/summary');
    return res.data.data;
  },

  /**
   * Mark a single notification as read
   */
  async markAsRead(id: string): Promise<NotificationItem> {
    const res = await apiClient.put<ApiResponse<NotificationItem>>(`/notifications/${id}/read`);
    return res.data.data;
  },

  /**
   * Mark all unread notifications as read
   */
  async markAllAsRead(): Promise<number> {
    const res = await apiClient.put<ApiResponse<number>>('/notifications/read-all');
    return res.data.data;
  },

  /**
   * Delete a notification
   */
  async deleteNotification(id: string): Promise<void> {
    await apiClient.delete<ApiResponse<void>>(`/notifications/${id}`);
  },

  /**
   * Delete all read notifications
   */
  async clearAllRead(): Promise<void> {
    await apiClient.delete<ApiResponse<void>>('/notifications/clear-read');
  },

  /**
   * Manually trigger proactive evaluation scan
   */
  async evaluateNotifications(): Promise<EvaluateNotificationsResult> {
    const res = await apiClient.post<ApiResponse<EvaluateNotificationsResult>>('/notifications/evaluate');
    return res.data.data;
  },

  /**
   * Get user notification preferences
   */
  async getPreferences(): Promise<NotificationPreference> {
    const res = await apiClient.get<ApiResponse<NotificationPreference>>('/notifications/preferences');
    return res.data.data;
  },

  /**
   * Update notification preferences
   */
  async updatePreferences(prefs: NotificationPreference): Promise<NotificationPreference> {
    const res = await apiClient.put<ApiResponse<NotificationPreference>>('/notifications/preferences', prefs);
    return res.data.data;
  },
};

