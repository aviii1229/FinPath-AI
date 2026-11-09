import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Menu,
  Search,
  Bell,
  Sparkles,
  ShieldCheck,
  CheckCircle2,
  AlertCircle,
} from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { useAuth } from '@/core/auth/AuthContext';

interface NavbarProps {
  onOpenMobileSidebar: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({ onOpenMobileSidebar }) => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [showNotifications, setShowNotifications] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearchKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && searchQuery.trim()) {
      navigate(`/transactions?q=${encodeURIComponent(searchQuery.trim())}`);
    }
  };

  const notifications = [
    {
      id: 1,
      title: 'Underwriting Approved',
      description: 'Your Auto Loan pre-qualification passed with 8.50% APR.',
      time: '12m ago',
      type: 'success',
    },
    {
      id: 2,
      title: 'Insurance Renewal',
      description: 'Term Life Shield policy renewal in 50 days.',
      time: '2h ago',
      type: 'warning',
    },
    {
      id: 3,
      title: 'Fintech Sync',
      description: 'HDFC Bank & Zerodha accounts successfully synced.',
      time: '5h ago',
      type: 'info',
    },
  ];

  return (
    <header className="sticky top-0 z-30 h-16 bg-navy-900/85 backdrop-blur-md border-b border-navy-700/60 px-4 sm:px-6 flex items-center justify-between">
      {/* Left: Mobile Toggle & Page Search */}
      <div className="flex items-center gap-3 flex-1 max-w-md">
        <button
          onClick={onOpenMobileSidebar}
          className="lg:hidden p-2 text-slate-400 hover:text-white rounded-lg hover:bg-navy-800 transition-colors"
          aria-label="Open navigation"
        >
          <Menu className="w-5 h-5" />
        </button>

        <div className="relative w-full hidden sm:block">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyDown={handleSearchKeyDown}
            placeholder="Search transactions, policies, or loans..."
            className="w-full glass-input text-xs pl-9 pr-4 py-2 rounded-xl text-slate-200 placeholder-slate-500 focus:border-cyan-500"
          />
        </div>
      </div>

      {/* Right: Actions, Notifications, AI Ask Button */}
      <div className="flex items-center gap-3">
        {/* Ask AI Pill Button */}
        <Button
          variant="electric"
          size="sm"
          leftIcon={<Sparkles className="w-3.5 h-3.5 text-cyan-200" />}
          onClick={() => navigate('/ai-assistant')}
          className="shadow-glow-sm hidden xs:flex"
        >
          <span>Ask FinPath AI</span>
        </Button>

        {/* Security Verified Indicator */}
        <div className="hidden md:flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-[11px] font-medium">
          <ShieldCheck className="w-3.5 h-3.5" />
          <span>Bank-Grade 256-Bit</span>
        </div>

        {/* Notifications Dropdown */}
        <div className="relative">
          <button
            onClick={() => setShowNotifications(!showNotifications)}
            className="relative p-2 rounded-xl text-slate-300 hover:text-white hover:bg-navy-800/80 border border-navy-700/60 transition-colors"
            aria-label="Notifications"
          >
            <Bell className="w-4 h-4" />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-cyan-400 ring-2 ring-navy-900" />
          </button>

          {showNotifications && (
            <div className="absolute right-0 mt-2 w-80 rounded-2xl glass-panel bg-navy-900 border border-navy-700 shadow-glow p-4 animate-slide-up z-50">
              <div className="flex items-center justify-between pb-3 border-b border-navy-700/60 mb-2">
                <span className="text-xs font-semibold text-white">Notifications</span>
                <span className="text-[10px] text-cyan-400 font-medium">3 unread</span>
              </div>
              <div className="space-y-2">
                {notifications.map((n) => (
                  <div
                    key={n.id}
                    className="p-2.5 rounded-xl bg-navy-950/60 hover:bg-navy-800/60 transition-colors border border-navy-800"
                  >
                    <div className="flex items-start gap-2">
                      {n.type === 'success' && <CheckCircle2 className="w-4 h-4 text-emerald-400 shrink-0 mt-0.5" />}
                      {n.type === 'warning' && <AlertCircle className="w-4 h-4 text-amber-400 shrink-0 mt-0.5" />}
                      {n.type === 'info' && <Sparkles className="w-4 h-4 text-cyan-400 shrink-0 mt-0.5" />}
                      <div>
                        <p className="text-xs font-semibold text-slate-200">{n.title}</p>
                        <p className="text-[11px] text-slate-400 leading-tight mt-0.5">{n.description}</p>
                        <span className="text-[10px] text-slate-500 font-mono mt-1 block">{n.time}</span>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* User Mini Avatar & Name */}
        <div
          onClick={() => navigate('/settings')}
          className="flex items-center gap-2 pl-2 cursor-pointer group"
          title="Account settings"
        >
          <img
            src={user?.avatarUrl || 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&auto=format&fit=crop&q=80'}
            alt={user?.firstName}
            className="w-8 h-8 rounded-full border border-cyan-400/30 object-cover group-hover:border-cyan-400 transition-colors"
          />
        </div>
      </div>
    </header>
  );
};

