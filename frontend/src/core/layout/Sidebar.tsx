import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard,
  Shield,
  Landmark,
  Coins,
  Bot,
  FileText,
  Compass,
  Receipt,
  Settings,
  LogOut,
  Sparkles,
  ChevronRight,
  X,
} from 'lucide-react';
import { useAuth } from '@/core/auth/AuthContext';
import { cn } from '@/shared/utils/cn';

interface SidebarProps {
  mobileOpen: boolean;
  onCloseMobile: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ mobileOpen, onCloseMobile }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const navItems = [
    { label: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { label: 'Personalization', path: '/onboarding', icon: Sparkles },
    { label: 'Fintech & PFM', path: '/fintech', icon: Coins },
    { label: 'Lending', path: '/lending', icon: Landmark },
    { label: 'Insurance', path: '/insurance', icon: Shield },
    { label: 'Customer Journeys', path: '/journeys', icon: Compass },
    { label: 'Document Vault', path: '/documents', icon: FileText },
    { label: 'AI Assistant', path: '/ai-assistant', icon: Bot, isHighlight: true },
    { label: 'Transactions', path: '/transactions', icon: Receipt },
    { label: 'Profile & Settings', path: '/settings', icon: Settings },
  ];

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <>
      {/* Mobile Backdrop */}
      {mobileOpen && (
        <div
          onClick={onCloseMobile}
          className="fixed inset-0 z-40 bg-navy-950/80 backdrop-blur-sm lg:hidden animate-fade-in"
        />
      )}

      {/* Sidebar Container */}
      <aside
        className={cn(
          'fixed top-0 bottom-0 left-0 z-40 w-64 bg-navy-900 border-r border-navy-700/70 flex flex-col transition-transform duration-300 ease-in-out lg:translate-x-0',
          mobileOpen ? 'translate-x-0' : '-translate-x-full'
        )}
      >
        {/* Brand Header */}
        <div className="h-16 flex items-center justify-between px-5 border-b border-navy-700/60 bg-navy-950/40">
          <NavLink to="/dashboard" className="flex items-center gap-2.5 group">
            <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-electric-600 to-cyan-400 flex items-center justify-center shadow-glow-sm group-hover:scale-105 transition-transform">
              <Sparkles className="w-5 h-5 text-white" />
            </div>
            <div>
              <div className="flex items-center gap-1.5">
                <span className="font-bold text-base tracking-tight text-white">FinPath</span>
                <span className="text-[10px] font-bold px-1.5 py-0.5 rounded-full bg-cyan-500/20 text-cyan-300 border border-cyan-500/30">
                  AI
                </span>
              </div>
              <p className="text-[10px] text-slate-400 font-mono">FINANCIAL JOURNEY</p>
            </div>
          </NavLink>

          <button
            onClick={onCloseMobile}
            className="lg:hidden p-1 text-slate-400 hover:text-white rounded-lg hover:bg-navy-800"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Navigation Links */}
        <div className="flex-1 overflow-y-auto px-3 py-4 space-y-1">
          <p className="px-3 text-[11px] font-semibold tracking-wider text-slate-500 uppercase mb-2">
            Navigation
          </p>

          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.path}
                to={item.path}
                onClick={onCloseMobile}
                className={({ isActive }) =>
                  cn(
                    'flex items-center justify-between px-3.5 py-2.5 rounded-xl text-xs font-medium transition-all group select-none',
                    isActive
                      ? 'bg-electric-600/15 text-cyan-300 border border-cyan-500/30 shadow-glow-sm font-semibold'
                      : 'text-slate-400 hover:text-slate-100 hover:bg-navy-800/80 border border-transparent',
                    item.isHighlight &&
                      !window.location.pathname.includes(item.path) &&
                      'text-electric-400 hover:text-electric-300'
                  )
                }
              >
                <div className="flex items-center gap-3">
                  <Icon
                    className={cn(
                      'w-4 h-4 transition-colors',
                      item.isHighlight ? 'text-cyan-400 group-hover:text-cyan-300' : 'text-slate-400 group-hover:text-slate-200'
                    )}
                  />
                  <span>{item.label}</span>
                </div>
                {item.isHighlight && (
                  <span className="w-2 h-2 rounded-full bg-cyan-400 animate-pulse-slow shadow-glow-sm" />
                )}
              </NavLink>
            );
          })}
        </div>

        {/* AI Engine Status Pill */}
        <div className="p-3 mx-3 mb-3 rounded-xl bg-navy-950/60 border border-cyan-500/20 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-cyan-400 opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-cyan-500"></span>
            </span>
            <div>
              <p className="text-[11px] font-semibold text-slate-200 leading-tight">AI Orchestrator</p>
              <p className="text-[10px] text-cyan-400 font-mono">GPT-4o + Gemini</p>
            </div>
          </div>
          <NavLink
            to="/ai-assistant"
            className="p-1 rounded-md text-slate-400 hover:text-cyan-300 hover:bg-navy-800 transition-colors"
            title="Open Assistant"
          >
            <ChevronRight className="w-4 h-4" />
          </NavLink>
        </div>

        {/* User Profile & Sign-out */}
        <div className="p-3 border-t border-navy-700/60 bg-navy-950/40 flex items-center justify-between">
          <div className="flex items-center gap-2.5 overflow-hidden">
            <img
              src={user?.avatarUrl || 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&auto=format&fit=crop&q=80'}
              alt={user?.firstName || 'User'}
              className="w-8 h-8 rounded-full border border-electric-400/40 object-cover"
            />
            <div className="overflow-hidden">
              <p className="text-xs font-semibold text-white truncate">
                {user?.firstName} {user?.lastName}
              </p>
              <p className="text-[10px] text-slate-400 truncate">{user?.email}</p>
            </div>
          </div>

          <button
            onClick={handleLogout}
            title="Log out"
            className="p-1.5 rounded-lg text-slate-400 hover:text-rose-400 hover:bg-rose-500/10 transition-colors"
          >
            <LogOut className="w-4 h-4" />
          </button>
        </div>
      </aside>
    </>
  );
};

