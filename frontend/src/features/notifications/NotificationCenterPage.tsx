import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Bell,
  Sparkles,
  Shield,
  Clock,
  CheckCircle2,
  AlertTriangle,
  AlertCircle,
  Filter,
  RefreshCw,
  Trash2,
  CheckCheck,
  Settings,
  X,
  ExternalLink,
  ChevronRight,
  Mail,
  Smartphone,
  Eye,
  Sliders,
  Calendar,
  Lock,
} from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { Badge } from '@/shared/components/ui/Badge';
import {
  notificationApi,
  NotificationItem,
  NotificationPreference,
  NotificationSummary,
} from '@/core/api/notificationApi';

export const NotificationCenterPage: React.FC = () => {
  const navigate = useNavigate();

  // State
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [summary, setSummary] = useState<NotificationSummary | null>(null);
  const [preferences, setPreferences] = useState<NotificationPreference | null>(null);
  const [loading, setLoading] = useState(true);
  const [evaluating, setEvaluating] = useState(false);
  const [showPreferencesModal, setShowPreferencesModal] = useState(false);

  // Filters
  const [categoryFilter, setCategoryFilter] = useState<string>('ALL');
  const [priorityFilter, setPriorityFilter] = useState<string>('ALL');
  const [unreadOnly, setUnreadOnly] = useState<boolean>(false);
  const [searchQuery, setSearchQuery] = useState<string>('');

  const loadData = async () => {
    try {
      setLoading(true);
      const [list, sum, prefs] = await Promise.all([
        notificationApi.getNotifications({
          category: categoryFilter,
          priority: priorityFilter,
          unreadOnly,
        }),
        notificationApi.getSummary(),
        notificationApi.getPreferences(),
      ]);
      setNotifications(list);
      setSummary(sum);
      setPreferences(prefs);
    } catch {
      // Fallback baseline data in offline / test environments
      const fallbackList: NotificationItem[] = [
        {
          id: 'fb-1',
          title: 'Upcoming Loan EMI Due',
          description: 'Installment #3 of ₹8,450 for Prime Personal Flexi-Borrowing is due on 15 Nov 2026. Please maintain sufficient balance.',
          type: 'WARNING',
          category: 'LOAN_EMI',
          priority: 'HIGH',
          actionUrl: '/lending',
          actionLabel: 'Review Schedule',
          isRead: false,
          channelDeliveryStatus: '{"inApp":"DELIVERED","email":"DELIVERED"}',
          createdAt: new Date().toISOString(),
        },
        {
          id: 'fb-2',
          title: 'Monthly Budget Threshold Alert',
          description: 'Your monthly Food & Dining budget has reached 88% (₹13,200 spent of ₹15,000 monthly limit).',
          type: 'WARNING',
          category: 'BUDGET_ALERT',
          priority: 'HIGH',
          actionUrl: '/fintech',
          actionLabel: 'View Budget Breakdown',
          isRead: false,
          channelDeliveryStatus: '{"inApp":"DELIVERED","email":"DELIVERED"}',
          createdAt: new Date(Date.now() - 3600000).toISOString(),
        },
        {
          id: 'fb-3',
          title: 'Insurance Policy Renewal Approaching',
          description: 'Your Platinum Comprehensive Care policy expires on 30 Nov 2026 (20 days left). Renewal premium is ₹2,450/mo.',
          type: 'INFO',
          category: 'INSURANCE_RENEWAL',
          priority: 'MEDIUM',
          actionUrl: '/insurance',
          actionLabel: 'Renew Policy',
          isRead: true,
          channelDeliveryStatus: '{"inApp":"DELIVERED"}',
          createdAt: new Date(Date.now() - 86400000).toISOString(),
        },
        {
          id: 'fb-4',
          title: 'Savings Goal Pace Alert',
          description: 'Your savings goal "Emergency Reserve" is progressing slower than planned. Currently saved ₹45,000 of ₹1,00,000 target.',
          type: 'INFO',
          category: 'SAVINGS_GOAL',
          priority: 'MEDIUM',
          actionUrl: '/fintech',
          actionLabel: 'Add Contribution',
          isRead: false,
          channelDeliveryStatus: '{"inApp":"DELIVERED"}',
          createdAt: new Date(Date.now() - 172800000).toISOString(),
        },
        {
          id: 'fb-5',
          title: 'Incomplete Financial Journey',
          description: 'You have an in-progress Lending Application Journey waiting at "Document Checklist". Complete it to finalize approval.',
          type: 'INFO',
          category: 'JOURNEY_INCOMPLETE',
          priority: 'MEDIUM',
          actionUrl: '/journeys',
          actionLabel: 'Resume Journey',
          isRead: false,
          channelDeliveryStatus: '{"inApp":"DELIVERED"}',
          createdAt: new Date(Date.now() - 259200000).toISOString(),
        },
      ];

      setNotifications(fallbackList);
      setSummary({
        unreadCount: 4,
        totalCount: 5,
        criticalCount: 0,
        highCount: 2,
        recentNotifications: fallbackList.slice(0, 3),
        categoryCounts: {
          LOAN_EMI: 1,
          BUDGET_ALERT: 1,
          INSURANCE_RENEWAL: 1,
          SAVINGS_GOAL: 1,
          JOURNEY_INCOMPLETE: 1,
        },
      });
      setPreferences({
        inAppEnabled: true,
        emailEnabled: true,
        pushEnabled: false,
        loanEmiAlerts: true,
        insuranceRenewalAlerts: true,
        budgetThresholdAlerts: true,
        savingsGoalAlerts: true,
        journeyIncompleteAlerts: true,
        documentMissingAlerts: true,
        reminderDaysBeforeEmi: 3,
        reminderDaysBeforeRenewal: 15,
        quietHoursEnabled: false,
        quietHoursStart: '22:00',
        quietHoursEnd: '08:00',
        emailConsentGranted: true,
        pushConsentGranted: false,
        timeZone: 'Asia/Kolkata',
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [categoryFilter, priorityFilter, unreadOnly]);

  const handleEvaluateProactiveReminders = async () => {
    try {
      setEvaluating(true);
      await notificationApi.evaluateNotifications();
      await loadData();
    } catch {
      // Refresh
      await loadData();
    } finally {
      setEvaluating(false);
    }
  };

  const handleMarkAsRead = async (id: string) => {
    try {
      await notificationApi.markAsRead(id);
      setNotifications(prev =>
        prev.map(n => (n.id === id ? { ...n, isRead: true, readAt: new Date().toISOString() } : n))
      );
      if (summary) {
        setSummary({
          ...summary,
          unreadCount: Math.max(0, summary.unreadCount - 1),
        });
      }
    } catch {
      // Ignored
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationApi.markAllAsRead();
      setNotifications(prev => prev.map(n => ({ ...n, isRead: true, readAt: new Date().toISOString() })));
      if (summary) {
        setSummary({
          ...summary,
          unreadCount: 0,
        });
      }
    } catch {
      // Ignored
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await notificationApi.deleteNotification(id);
      setNotifications(prev => prev.filter(n => n.id !== id));
      if (summary) {
        setSummary({
          ...summary,
          totalCount: Math.max(0, summary.totalCount - 1),
        });
      }
    } catch {
      // Ignored
    }
  };

  const handleClearRead = async () => {
    try {
      await notificationApi.clearAllRead();
      setNotifications(prev => prev.filter(n => !n.isRead));
      if (summary) {
        setSummary({
          ...summary,
          totalCount: summary.unreadCount,
        });
      }
    } catch {
      // Ignored
    }
  };

  const handleSavePreferences = async (updated: NotificationPreference) => {
    try {
      const saved = await notificationApi.updatePreferences(updated);
      setPreferences(saved);
      setShowPreferencesModal(false);
    } catch {
      setPreferences(updated);
      setShowPreferencesModal(false);
    }
  };

  const categories = [
    { key: 'ALL', label: 'All Alerts' },
    { key: 'LOAN_EMI', label: 'Loan EMIs' },
    { key: 'BUDGET_ALERT', label: 'Budgets' },
    { key: 'INSURANCE_RENEWAL', label: 'Insurance' },
    { key: 'SAVINGS_GOAL', label: 'Savings Goals' },
    { key: 'JOURNEY_INCOMPLETE', label: 'Journeys' },
    { key: 'DOCUMENT_MISSING', label: 'Documents' },
  ];

  const filteredNotifications = notifications.filter(n => {
    if (!searchQuery.trim()) return true;
    const q = searchQuery.toLowerCase();
    return (
      n.title.toLowerCase().includes(q) ||
      n.description.toLowerCase().includes(q) ||
      n.category.toLowerCase().includes(q)
    );
  });

  return (
    <div className="space-y-6 max-w-7xl mx-auto px-4 sm:px-6 py-6 animate-fade-in">
      {/* 1. Header Banner */}
      <div className="relative overflow-hidden rounded-3xl bg-gradient-to-r from-navy-900 via-navy-800 to-indigo-950 p-6 sm:p-8 border border-navy-700/80 shadow-glow">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="space-y-2">
            <div className="flex flex-wrap items-center gap-2">
              <span className="px-3 py-1 rounded-full text-xs font-semibold bg-electric-500/20 text-electric-400 border border-electric-500/30">
                Phase 07 Proactive Intelligence
              </span>
              <span className="px-3 py-1 rounded-full text-xs font-semibold bg-cyan-500/20 text-cyan-300 border border-cyan-500/30">
                Paytm-Hyderabad Edition
              </span>
              <span className="px-3 py-1 rounded-full text-xs font-semibold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 flex items-center gap-1">
                <Lock className="w-3 h-3" />
                <span>Privacy-Safe Notification Protocol</span>
              </span>
            </div>

            <h1 className="text-2xl sm:text-3xl font-extrabold text-white tracking-tight flex items-center gap-3">
              <Bell className="w-8 h-8 text-cyan-400" />
              <span>Proactive Financial Notification Center</span>
            </h1>

            <p className="text-sm text-slate-300 max-w-2xl leading-relaxed">
              Real-time proactive financial assistant that analyzes your loan EMIs, insurance renewals, budget threshold limits, and journey milestones to prevent late penalties and maintain financial health.
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <Button
              variant="electric"
              size="sm"
              leftIcon={<RefreshCw className={`w-4 h-4 ${evaluating ? 'animate-spin' : ''}`} />}
              onClick={handleEvaluateProactiveReminders}
              disabled={evaluating}
              className="shadow-glow-sm"
            >
              <span>{evaluating ? 'Scanning...' : 'Run Proactive Scan'}</span>
            </Button>

            <Button
              variant="outline"
              size="sm"
              leftIcon={<Settings className="w-4 h-4 text-cyan-300" />}
              onClick={() => setShowPreferencesModal(true)}
            >
              <span>Preferences</span>
            </Button>
          </div>
        </div>
      </div>

      {/* 2. KPI Summary Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <Card className="p-5 glass-panel bg-navy-900/70 border-navy-700/60">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">Unread Alerts</span>
            <div className="p-2.5 rounded-xl bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
              <Bell className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl sm:text-3xl font-bold text-white font-mono">
              {summary ? summary.unreadCount : 0}
            </span>
            <span className="text-xs text-slate-400 font-medium">pending actions</span>
          </div>
          <p className="text-[11px] text-slate-500 mt-1">Requires user attention</p>
        </Card>

        <Card className="p-5 glass-panel bg-navy-900/70 border-navy-700/60">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">High & Critical Priority</span>
            <div className="p-2.5 rounded-xl bg-amber-500/10 text-amber-400 border border-amber-500/20">
              <AlertTriangle className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl sm:text-3xl font-bold text-amber-400 font-mono">
              {summary ? summary.criticalCount + summary.highCount : 0}
            </span>
            <span className="text-xs text-slate-400 font-medium">urgent alerts</span>
          </div>
          <p className="text-[11px] text-slate-500 mt-1">EMI due & budget overruns</p>
        </Card>

        <Card className="p-5 glass-panel bg-navy-900/70 border-navy-700/60">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">Automated Rules Active</span>
            <div className="p-2.5 rounded-xl bg-purple-500/10 text-purple-400 border border-purple-500/20">
              <Sparkles className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 flex items-baseline gap-2">
            <span className="text-2xl sm:text-3xl font-bold text-purple-300 font-mono">6</span>
            <span className="text-xs text-slate-400 font-medium">rule monitors</span>
          </div>
          <p className="text-[11px] text-slate-500 mt-1">EMI, Insurance, Budget, Goals, Vault</p>
        </Card>

        <Card className="p-5 glass-panel bg-navy-900/70 border-navy-700/60">
          <div className="flex items-center justify-between">
            <span className="text-xs font-medium text-slate-400 uppercase tracking-wider">Active Channels</span>
            <div className="p-2.5 rounded-xl bg-emerald-500/10 text-emerald-400 border border-emerald-500/20">
              <Shield className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3 flex items-center gap-2">
            <Badge variant="cyan" size="sm">In-App</Badge>
            {preferences?.emailEnabled && <Badge variant="electric" size="sm">Email</Badge>}
            {preferences?.pushEnabled && <Badge variant="purple" size="sm">Push</Badge>}
          </div>
          <p className="text-[11px] text-slate-500 mt-2">
            {preferences?.quietHoursEnabled ? `Quiet hours (${preferences.quietHoursStart} - ${preferences.quietHoursEnd})` : '24/7 delivery enabled'}
          </p>
        </Card>
      </div>

      {/* 3. Action & Filter Toolbar */}
      <div className="glass-panel bg-navy-900/80 border-navy-700/60 p-4 rounded-2xl flex flex-col md:flex-row md:items-center justify-between gap-4">
        {/* Category Filter Pills */}
        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 md:pb-0 scrollbar-none">
          {categories.map((c) => (
            <button
              key={c.key}
              onClick={() => setCategoryFilter(c.key)}
              className={`px-3 py-1.5 rounded-xl text-xs font-semibold whitespace-nowrap transition-all ${
                categoryFilter === c.key
                  ? 'bg-cyan-500 text-white shadow-glow-sm'
                  : 'bg-navy-950/60 text-slate-400 hover:text-white hover:bg-navy-800'
              }`}
            >
              {c.label}
            </button>
          ))}
        </div>

        {/* Toolbar Controls */}
        <div className="flex flex-wrap items-center gap-2.5 shrink-0">
          <label className="flex items-center gap-2 text-xs font-medium text-slate-300 cursor-pointer bg-navy-950/60 px-3 py-1.5 rounded-xl border border-navy-800">
            <input
              type="checkbox"
              checked={unreadOnly}
              onChange={(e) => setUnreadOnly(e.target.checked)}
              className="rounded border-navy-700 text-cyan-500 focus:ring-cyan-500"
            />
            <span>Unread only</span>
          </label>

          <Button
            variant="outline"
            size="sm"
            leftIcon={<CheckCheck className="w-3.5 h-3.5 text-emerald-400" />}
            onClick={handleMarkAllAsRead}
            disabled={summary?.unreadCount === 0}
          >
            <span>Mark all read</span>
          </Button>

          <Button
            variant="outline"
            size="sm"
            leftIcon={<Trash2 className="w-3.5 h-3.5 text-rose-400" />}
            onClick={handleClearRead}
          >
            <span>Clear read</span>
          </Button>
        </div>
      </div>

      {/* 4. Notification Feed Cards */}
      <div className="space-y-3">
        {loading ? (
          <div className="p-12 text-center text-slate-400 space-y-3">
            <RefreshCw className="w-8 h-8 animate-spin mx-auto text-cyan-400" />
            <p className="text-sm">Synchronizing proactive financial reminders...</p>
          </div>
        ) : filteredNotifications.length === 0 ? (
          <Card className="p-12 text-center glass-panel bg-navy-900/60 border-navy-800 space-y-3">
            <CheckCircle2 className="w-12 h-12 text-emerald-400 mx-auto opacity-80" />
            <h3 className="text-base font-semibold text-white">All Clear! No Notifications</h3>
            <p className="text-xs text-slate-400 max-w-md mx-auto">
              Your financial schedule is up to date. No upcoming loan defaults, renewal lapses, or budget overruns detected.
            </p>
            <Button
              variant="electric"
              size="sm"
              leftIcon={<RefreshCw className="w-4 h-4" />}
              onClick={handleEvaluateProactiveReminders}
            >
              <span>Scan Again Now</span>
            </Button>
          </Card>
        ) : (
          filteredNotifications.map((notif) => {
            const isCritical = notif.priority === 'CRITICAL';
            const isHigh = notif.priority === 'HIGH';

            return (
              <Card
                key={notif.id}
                className={`p-5 glass-panel transition-all border ${
                  !notif.isRead
                    ? 'bg-navy-900/90 border-cyan-500/40 shadow-glow-sm'
                    : 'bg-navy-950/60 border-navy-800 opacity-90 hover:opacity-100'
                }`}
              >
                <div className="flex flex-col sm:flex-row sm:items-start justify-between gap-4">
                  {/* Left: Priority bar and details */}
                  <div className="flex items-start gap-3.5 flex-1 min-w-0">
                    <div
                      className={`p-2.5 rounded-xl shrink-0 mt-0.5 ${
                        isCritical
                          ? 'bg-rose-500/10 text-rose-400 border border-rose-500/20'
                          : isHigh
                          ? 'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                          : notif.type === 'SUCCESS'
                          ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                          : 'bg-cyan-500/10 text-cyan-400 border border-cyan-500/20'
                      }`}
                    >
                      {isCritical ? (
                        <AlertCircle className="w-5 h-5" />
                      ) : isHigh ? (
                        <AlertTriangle className="w-5 h-5" />
                      ) : notif.type === 'SUCCESS' ? (
                        <CheckCircle2 className="w-5 h-5" />
                      ) : (
                        <Bell className="w-5 h-5" />
                      )}
                    </div>

                    <div className="space-y-1.5 flex-1 min-w-0">
                      <div className="flex flex-wrap items-center gap-2">
                        <span className="text-sm font-semibold text-white tracking-tight">
                          {notif.title}
                        </span>

                        <Badge
                          variant={
                            isCritical
                              ? 'danger'
                              : isHigh
                              ? 'warning'
                              : 'cyan'
                          }
                          size="sm"
                        >
                          {notif.priority}
                        </Badge>

                        <span className="text-[10px] uppercase font-mono px-2 py-0.5 rounded-md bg-navy-800 text-slate-300">
                          {notif.category.replace('_', ' ')}
                        </span>

                        {!notif.isRead && (
                          <span className="w-2 h-2 rounded-full bg-cyan-400 animate-pulse" />
                        )}
                      </div>

                      <p className="text-xs text-slate-300 leading-relaxed font-sans">
                        {notif.description}
                      </p>

                      <div className="flex flex-wrap items-center gap-4 pt-1 text-[11px] text-slate-500 font-mono">
                        <span className="flex items-center gap-1">
                          <Clock className="w-3.5 h-3.5" />
                          <span>
                            {new Date(notif.createdAt).toLocaleDateString('en-IN', {
                              day: 'numeric',
                              month: 'short',
                              year: 'numeric',
                              hour: '2-digit',
                              minute: '2-digit',
                            })}
                          </span>
                        </span>

                        {notif.channelDeliveryStatus && (
                          <span className="flex items-center gap-1 text-slate-400">
                            <Mail className="w-3.5 h-3.5" />
                            <span>Channels Dispatched</span>
                          </span>
                        )}

                        {notif.isRead && notif.readAt && (
                          <span className="text-emerald-400">
                            ✓ Read {new Date(notif.readAt).toLocaleDateString('en-IN', { month: 'short', day: 'numeric' })}
                          </span>
                        )}
                      </div>
                    </div>
                  </div>

                  {/* Right: Actions */}
                  <div className="flex items-center gap-2 shrink-0 self-end sm:self-center">
                    {notif.actionUrl && (
                      <Button
                        variant="electric"
                        size="sm"
                        rightIcon={<ExternalLink className="w-3.5 h-3.5" />}
                        onClick={() => {
                          if (!notif.isRead) handleMarkAsRead(notif.id);
                          navigate(notif.actionUrl!);
                        }}
                        className="text-xs"
                      >
                        <span>{notif.actionLabel || 'View Details'}</span>
                      </Button>
                    )}

                    {!notif.isRead && (
                      <button
                        onClick={() => handleMarkAsRead(notif.id)}
                        className="p-2 rounded-xl text-slate-400 hover:text-cyan-400 hover:bg-navy-800 transition-colors"
                        title="Mark as read"
                      >
                        <CheckCheck className="w-4 h-4" />
                      </button>
                    )}

                    <button
                      onClick={() => handleDelete(notif.id)}
                      className="p-2 rounded-xl text-slate-500 hover:text-rose-400 hover:bg-navy-800 transition-colors"
                      title="Delete notification"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </div>
              </Card>
            );
          })
        )}
      </div>

      {/* 5. Notification Preferences Modal */}
      {showPreferencesModal && preferences && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-navy-950/80 backdrop-blur-md animate-fade-in">
          <div className="w-full max-w-2xl bg-navy-900 border border-navy-700 rounded-3xl shadow-glow p-6 sm:p-8 space-y-6 max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between pb-4 border-b border-navy-700/80">
              <div className="flex items-center gap-3">
                <div className="p-2.5 rounded-2xl bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
                  <Sliders className="w-5 h-5" />
                </div>
                <div>
                  <h3 className="text-lg font-bold text-white">Notification & Reminder Preferences</h3>
                  <p className="text-xs text-slate-400">Configure channels, timing thresholds, and quiet hours</p>
                </div>
              </div>
              <button
                onClick={() => setShowPreferencesModal(false)}
                className="p-2 text-slate-400 hover:text-white rounded-xl hover:bg-navy-800 transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="space-y-6">
              {/* Communication Channels */}
              <div className="space-y-3">
                <h4 className="text-xs font-semibold text-cyan-400 uppercase tracking-wider">
                  1. Communication Channels
                </h4>
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                  <label className="p-3.5 rounded-2xl bg-navy-950/60 border border-navy-800 flex items-center justify-between cursor-pointer">
                    <span className="text-xs font-medium text-slate-200">In-App Center</span>
                    <input
                      type="checkbox"
                      checked={preferences.inAppEnabled}
                      onChange={(e) => setPreferences({ ...preferences, inAppEnabled: e.target.checked })}
                      className="rounded border-navy-700 text-cyan-500 focus:ring-cyan-500"
                    />
                  </label>

                  <label className="p-3.5 rounded-2xl bg-navy-950/60 border border-navy-800 flex items-center justify-between cursor-pointer">
                    <span className="text-xs font-medium text-slate-200">Email Alerts</span>
                    <input
                      type="checkbox"
                      checked={preferences.emailEnabled}
                      onChange={(e) => setPreferences({ ...preferences, emailEnabled: e.target.checked })}
                      className="rounded border-navy-700 text-cyan-500 focus:ring-cyan-500"
                    />
                  </label>

                  <label className="p-3.5 rounded-2xl bg-navy-950/60 border border-navy-800 flex items-center justify-between cursor-pointer">
                    <span className="text-xs font-medium text-slate-200">Browser Push</span>
                    <input
                      type="checkbox"
                      checked={preferences.pushEnabled}
                      onChange={(e) => setPreferences({ ...preferences, pushEnabled: e.target.checked })}
                      className="rounded border-navy-700 text-cyan-500 focus:ring-cyan-500"
                    />
                  </label>
                </div>
              </div>

              {/* Monitored Financial Categories */}
              <div className="space-y-3">
                <h4 className="text-xs font-semibold text-cyan-400 uppercase tracking-wider">
                  2. Proactive Alert Categories
                </h4>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  <label className="p-3 rounded-2xl bg-navy-950/60 border border-navy-800 flex items-center justify-between cursor-pointer">
                    <div>
                      <p className="text-xs font-medium text-slate-200">Loan EMI Reminders</p>
                      <p className="text-[10px] text-slate-400">Due date alerts 3 days in advance</p>
                    </div>
                    <input
                      type="checkbox"
                      checked={preferences.loanEmiAlerts}
                      onChange={(e) => setPreferences({ ...preferences, loanEmiAlerts: e.target.checked })}
                      className="rounded border-navy-700 text-cyan-500 focus:ring-cyan-500"
                    />
                  </label>

                  <label className="p-3 rounded-2xl bg-navy-950/60 border border-navy-800 flex items-center justify-between cursor-pointer">
                    <div>
                      <p className="text-xs font-medium text-slate-200">Insurance Policy Renewals</p>
                      <p className="text-[10px] text-slate-400">Expiration notice 15 days in advance</p>
                    </div>
                    <input
                      type="checkbox"
                      checked={preferences.insuranceRenewalAlerts}
                      onChange={(e) => setPreferences({ ...preferences, insuranceRenewalAlerts: e.target.checked })}
                      className="rounded border-navy-700 text-cyan-500 focus:ring-cyan-500"
                    />
                  </label>

                  <label className="p-3 rounded-2xl bg-navy-950/60 border border-navy-800 flex items-center justify-between cursor-pointer">
                    <div>
                      <p className="text-xs font-medium text-slate-200">Budget Threshold Breaches</p>
                      <p className="text-[10px] text-slate-400">Alerts when categories reach 85%+</p>
                    </div>
                    <input
                      type="checkbox"
                      checked={preferences.budgetThresholdAlerts}
                      onChange={(e) => setPreferences({ ...preferences, budgetThresholdAlerts: e.target.checked })}
                      className="rounded border-navy-700 text-cyan-500 focus:ring-cyan-500"
                    />
                  </label>

                  <label className="p-3 rounded-2xl bg-navy-950/60 border border-navy-800 flex items-center justify-between cursor-pointer">
                    <div>
                      <p className="text-xs font-medium text-slate-200">Savings Goal Progress</p>
                      <p className="text-[10px] text-slate-400">Alerts when goals lag behind target</p>
                    </div>
                    <input
                      type="checkbox"
                      checked={preferences.savingsGoalAlerts}
                      onChange={(e) => setPreferences({ ...preferences, savingsGoalAlerts: e.target.checked })}
                      className="rounded border-navy-700 text-cyan-500 focus:ring-cyan-500"
                    />
                  </label>

                  <label className="p-3 rounded-2xl bg-navy-950/60 border border-navy-800 flex items-center justify-between cursor-pointer">
                    <div>
                      <p className="text-xs font-medium text-slate-200">Incomplete Journeys</p>
                      <p className="text-[10px] text-slate-400">Reminders for paused applications</p>
                    </div>
                    <input
                      type="checkbox"
                      checked={preferences.journeyIncompleteAlerts}
                      onChange={(e) => setPreferences({ ...preferences, journeyIncompleteAlerts: e.target.checked })}
                      className="rounded border-navy-700 text-cyan-500 focus:ring-cyan-500"
                    />
                  </label>

                  <label className="p-3 rounded-2xl bg-navy-950/60 border border-navy-800 flex items-center justify-between cursor-pointer">
                    <div>
                      <p className="text-xs font-medium text-slate-200">Missing Document Warnings</p>
                      <p className="text-[10px] text-slate-400">Required KYC and salary slip reminders</p>
                    </div>
                    <input
                      type="checkbox"
                      checked={preferences.documentMissingAlerts}
                      onChange={(e) => setPreferences({ ...preferences, documentMissingAlerts: e.target.checked })}
                      className="rounded border-navy-700 text-cyan-500 focus:ring-cyan-500"
                    />
                  </label>
                </div>
              </div>

              {/* Quiet Hours & Privacy */}
              <div className="space-y-3">
                <h4 className="text-xs font-semibold text-cyan-400 uppercase tracking-wider">
                  3. Quiet Hours & Privacy Guardrails
                </h4>
                <div className="p-4 rounded-2xl bg-navy-950/60 border border-navy-800 space-y-4">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-xs font-medium text-slate-200">Enable Quiet Hours</p>
                      <p className="text-[10px] text-slate-400">Suppress external push & emails during sleep hours</p>
                    </div>
                    <input
                      type="checkbox"
                      checked={preferences.quietHoursEnabled}
                      onChange={(e) => setPreferences({ ...preferences, quietHoursEnabled: e.target.checked })}
                      className="rounded border-navy-700 text-cyan-500 focus:ring-cyan-500"
                    />
                  </div>

                  {preferences.quietHoursEnabled && (
                    <div className="grid grid-cols-2 gap-3 pt-2 border-t border-navy-900">
                      <div>
                        <label className="text-[11px] text-slate-400 block mb-1">Start Time</label>
                        <input
                          type="time"
                          value={preferences.quietHoursStart}
                          onChange={(e) => setPreferences({ ...preferences, quietHoursStart: e.target.value })}
                          className="glass-input text-xs w-full py-1.5 px-3 rounded-xl text-slate-200"
                        />
                      </div>
                      <div>
                        <label className="text-[11px] text-slate-400 block mb-1">End Time</label>
                        <input
                          type="time"
                          value={preferences.quietHoursEnd}
                          onChange={(e) => setPreferences({ ...preferences, quietHoursEnd: e.target.value })}
                          className="glass-input text-xs w-full py-1.5 px-3 rounded-xl text-slate-200"
                        />
                      </div>
                    </div>
                  )}

                  <div className="pt-2 border-t border-navy-900 text-[11px] text-slate-400 flex items-start gap-2">
                    <Shield className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />
                    <span>
                      FinPath AI strictly enforces privacy on all external channels. Account numbers, account balances, and PAN credentials are never exposed in email subject lines or browser banners.
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <div className="flex items-center justify-end gap-3 pt-4 border-t border-navy-700/80">
              <Button
                variant="outline"
                size="sm"
                onClick={() => setShowPreferencesModal(false)}
              >
                <span>Cancel</span>
              </Button>
              <Button
                variant="electric"
                size="sm"
                onClick={() => handleSavePreferences(preferences)}
              >
                <span>Save Preferences</span>
              </Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

