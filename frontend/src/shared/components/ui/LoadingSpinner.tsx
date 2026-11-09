import React from 'react';
import { Loader2 } from 'lucide-react';
import { cn } from '@/shared/utils/cn';

export interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg';
  text?: string;
  className?: string;
}

export const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({
  size = 'md',
  text,
  className,
}) => {
  const sizes = {
    sm: 'w-4 h-4',
    md: 'w-8 h-8',
    lg: 'w-12 h-12',
  };

  return (
    <div className={cn('flex flex-col items-center justify-center p-6 gap-3 text-cyan-400', className)}>
      <Loader2 className={cn(sizes[size], 'animate-spin text-electric-500')} />
      {text && <p className="text-xs text-slate-400 font-medium tracking-wide animate-pulse">{text}</p>}
    </div>
  );
};

