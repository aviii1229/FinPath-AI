import React from 'react';
import { Button } from './Button';
import { LucideIcon } from 'lucide-react';
import { cn } from '@/shared/utils/cn';

export interface EmptyStateProps {
  icon: LucideIcon;
  title: string;
  description: string;
  actionText?: string;
  onAction?: () => void;
  className?: string;
}

export const EmptyState: React.FC<EmptyStateProps> = ({
  icon: Icon,
  title,
  description,
  actionText,
  onAction,
  className,
}) => {
  return (
    <div
      className={cn(
        'glass-panel border-dashed border-navy-700/80 rounded-xl p-8 flex flex-col items-center justify-center text-center',
        className
      )}
    >
      <div className="w-12 h-12 rounded-xl bg-electric-600/10 border border-electric-500/20 flex items-center justify-center text-electric-400 mb-3.5 shadow-glow-sm">
        <Icon className="w-6 h-6" />
      </div>
      <h4 className="text-sm font-semibold text-white mb-1">{title}</h4>
      <p className="text-xs text-slate-400 max-w-sm mb-4 leading-relaxed">{description}</p>
      {actionText && onAction && (
        <Button variant="cyan" size="sm" onClick={onAction}>
          {actionText}
        </Button>
      )}
    </div>
  );
};

