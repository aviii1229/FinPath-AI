import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Mail, Sparkles, ArrowLeft, CheckCircle2, ShieldCheck } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { Input } from '@/shared/components/ui/Input';

export const ForgotPasswordPage: React.FC = () => {
  const [email, setEmail] = useState('');
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email) {
      setError('Please provide your registered email address.');
      return;
    }
    if (!/\S+@\S+\.\S+/.test(email)) {
      setError('Please provide a valid email format.');
      return;
    }

    setIsLoading(true);
    setError('');

    // Simulated network delay
    await new Promise((res) => setTimeout(res, 600));
    setIsLoading(false);
    setIsSubmitted(true);
  };

  return (
    <div className="min-h-screen bg-navy-950 flex flex-col justify-center items-center px-4 py-12 relative overflow-hidden bg-radial-gradient">
      {/* Brand Header */}
      <div className="text-center mb-8 relative z-10">
        <Link to="/" className="inline-flex items-center gap-2.5 mb-3 group">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-electric-600 to-cyan-400 flex items-center justify-center shadow-glow-sm group-hover:scale-105 transition-transform">
            <Sparkles className="w-5 h-5 text-white" />
          </div>
          <span className="font-bold text-2xl tracking-tight text-white">FinPath AI</span>
        </Link>
        <p className="text-xs text-slate-400">Account Recovery</p>
      </div>

      <div className="w-full max-w-md glass-panel bg-navy-900/90 border border-navy-700/80 rounded-2xl p-7 shadow-glow relative z-10">
        {!isSubmitted ? (
          <>
            <div className="mb-6">
              <h2 className="text-xl font-bold text-white tracking-tight">Reset Password</h2>
              <p className="text-xs text-slate-400 mt-1">
                Enter your email address and we will send you secure instructions to reset your password.
              </p>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4">
              <Input
                label="Registered Email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="name@example.com"
                error={error}
                leftIcon={<Mail className="w-4 h-4" />}
              />

              <Button
                type="submit"
                variant="electric"
                size="md"
                className="w-full mt-2"
                isLoading={isLoading}
              >
                Send Password Reset Link
              </Button>
            </form>
          </>
        ) : (
          <div className="text-center py-4 space-y-4 animate-fade-in">
            <div className="w-14 h-14 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-emerald-400 mx-auto shadow-glow-sm">
              <CheckCircle2 className="w-7 h-7" />
            </div>

            <div>
              <h3 className="text-lg font-bold text-white">Reset Link Dispatched</h3>
              <p className="text-xs text-slate-300 mt-2 leading-relaxed">
                If an account exists for <span className="text-cyan-400 font-mono">{email}</span>, you will receive a secure token within 2 minutes.
              </p>
            </div>

            <Button
              variant="outline"
              size="sm"
              className="mt-4"
              onClick={() => setIsSubmitted(false)}
            >
              Didn't receive email? Try again
            </Button>
          </div>
        )}

        <div className="mt-6 pt-5 border-t border-navy-800 text-center">
          <Link
            to="/login"
            className="inline-flex items-center gap-2 text-xs text-slate-400 hover:text-cyan-300 transition-colors font-medium"
          >
            <ArrowLeft className="w-3.5 h-3.5" />
            <span>Back to Sign In</span>
          </Link>
        </div>
      </div>

      <div className="mt-8 flex items-center gap-2 text-xs text-slate-500">
        <ShieldCheck className="w-4 h-4 text-emerald-500" />
        <span>Self-service tokens expire in 15 minutes for security</span>
      </div>
    </div>
  );
};

