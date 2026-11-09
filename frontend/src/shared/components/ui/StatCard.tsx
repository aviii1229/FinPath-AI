import React from 'react';
import { Card } from './Card';
import { LucideIcon, TrendingUp, TrendingDown } from 'lucide-react';
import { cn } from '@/shared/utils/cn';

export interface StatCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  icon: LucideIcon;
  trend?: {
    value: number; // e.g. 5.2%
    isPositive: boolean;
    label?: string;
  };
  accentColor?: 'electric' | 'cyan' | 'emerald' | 'amber';
  onClick?: () => void;
}

export const StatCard: React.FC<StatCardProps> = ({
  title,
  value,
  subtitle,
  icon: Icon,
  trend,
  accentColor = 'electric',
  onClick,
}) => {
  const accentClasses = {
    electric: 'text-electric-400 bg-electric-600/10 border-electric-500/25',
    cyan: 'text-cyan-400 bg-cyan-500/10 border-cyan-500/25',
    emerald: 'text-emerald-400 bg-emerald-500/10 border-emerald-500/25',
    amber: 'text-amber-400 bg-amber-500/10 border-amber-500/25',
  };

  return (
    <Card
      hoverEffect={!!onClick}
      onClick={onClick}
      className="relative overflow-hidden group border border-navy-700/60"
    >
      <div className="flex items-start justify-between">
        <div>
          <p className="text-xs font-medium text-slate-400 uppercase tracking-wider">{title}</p>
          <h4 className="text-2xl font-bold text-white mt-1.5 tracking-tight font-sans">
            {value}
          </h4>
          {subtitle && <p className="text-xs text-slate-400 mt-1">{subtitle}</p>}
        </div>
        <div
          className={cn(
            'w-11 h-11 rounded-xl flex items-center justify-center border shadow-glow-sm transition-transform duration-200 group-hover:scale-105',
            accentClasses[accentColor]
          )}
        >
          <Icon className="w-5 h-5" />
        </div>
      </div>

      {trend && (
        <div className="mt-3.5 pt-3 border-t border-navy-700/40 flex items-center gap-1.5 text-xs">
          {trend.isPositive ? (
            <div className="flex items-center text-emerald-400 font-semibold gap-0.5">
              <TrendingUp className="w-3.5 h-3.5" />
              <span>+{Math.abs(trend.value)}%</span>
            </div>
          ) : (
            <div className="flex items-center text-rose-400 font-semibold gap-0.5">
              <TrendingDown className="w-3.5 h-3.5" />
              <span>-{Math.abs(trend.value)}%</span>
            </div>
          )}
          <span className="text-slate-500">{trend.label || 'vs last month'}</span>
        </div>
      )}
    </Card>
  );
};

