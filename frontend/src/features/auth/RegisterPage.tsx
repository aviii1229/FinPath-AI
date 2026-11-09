import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Mail, Lock, User, Sparkles, ArrowRight, ShieldCheck, AlertCircle, Check } from 'lucide-react';
import { Button } from '@/shared/components/ui/Button';
import { Input } from '@/shared/components/ui/Input';
import { useAuth } from '@/core/auth/AuthContext';

export const RegisterPage: React.FC = () => {
  const navigate = useNavigate();
  const { register } = useAuth();

  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [agreeTerms, setAgreeTerms] = useState(false);
  const [errors, setErrors] = useState<{
    firstName?: string;
    lastName?: string;
    email?: string;
    password?: string;
    terms?: string;
    general?: string;
  }>({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Password strength logic
  const hasMinLength = password.length >= 8;
  const hasNumber = /\d/.test(password);
  const hasSpecial = /[!@#$%^&*]/.test(password);
  const strengthScore = [hasMinLength, hasNumber, hasSpecial].filter(Boolean).length;

  const validate = () => {
    const errs: typeof errors = {};
    if (!firstName.trim()) errs.firstName = 'First name is required.';
    if (!lastName.trim()) errs.lastName = 'Last name is required.';
    if (!email.trim()) {
      errs.email = 'Email address is required.';
    } else if (!/\S+@\S+\.\S+/.test(email)) {
      errs.email = 'Please provide a valid email format.';
    }

    if (!password) {
      errs.password = 'Password is required.';
    } else if (password.length < 8) {
      errs.password = 'Password must be at least 8 characters long.';
    }

    if (!agreeTerms) {
      errs.terms = 'You must agree to the Terms of Service & Privacy Policy.';
    }

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    setIsSubmitting(true);
    setErrors({});

    try {
      const res = await register(firstName, lastName, email, password);
      if (res.success) {
        navigate('/onboarding');
      } else {
        setErrors({ general: res.error || 'Registration failed.' });
      }
    } catch (err: any) {
      setErrors({ general: 'Network error. Please try again.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-navy-950 flex flex-col justify-center items-center px-4 py-12 relative overflow-hidden bg-radial-gradient">
      {/* Glow Orbs */}
      <div className="absolute top-1/4 -right-20 w-80 h-80 bg-electric-600/10 rounded-full blur-3xl pointer-events-none" />
      <div className="absolute bottom-1/4 -left-20 w-80 h-80 bg-cyan-500/10 rounded-full blur-3xl pointer-events-none" />

      {/* Brand Header */}
      <div className="text-center mb-8 relative z-10">
        <Link to="/" className="inline-flex items-center gap-2.5 mb-3 group">
          <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-electric-600 to-cyan-400 flex items-center justify-center shadow-glow-sm group-hover:scale-105 transition-transform">
            <Sparkles className="w-5 h-5 text-white" />
          </div>
          <span className="font-bold text-2xl tracking-tight text-white">FinPath AI</span>
        </Link>
        <p className="text-xs text-slate-400">Join the AI-powered financial platform</p>
      </div>

      {/* Card */}
      <div className="w-full max-w-md glass-panel bg-navy-900/90 border border-navy-700/80 rounded-2xl p-7 shadow-glow relative z-10">
        <div className="mb-6">
          <h2 className="text-xl font-bold text-white tracking-tight">Create Free Account</h2>
          <p className="text-xs text-slate-400 mt-1">
            Experience cross-domain intelligence across Insurance, Lending & Wealth.
          </p>
        </div>

        {errors.general && (
          <div className="mb-5 p-3 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-300 text-xs flex items-center gap-2">
            <AlertCircle className="w-4 h-4 shrink-0" />
            <span>{errors.general}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <Input
              label="First Name"
              type="text"
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
              placeholder="Sarah"
              error={errors.firstName}
              leftIcon={<User className="w-4 h-4" />}
            />
            <Input
              label="Last Name"
              type="text"
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
              placeholder="Connor"
              error={errors.lastName}
            />
          </div>

          <Input
            label="Email Address"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="sarah@example.com"
            error={errors.email}
            leftIcon={<Mail className="w-4 h-4" />}
          />

          <Input
            label="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="At least 8 characters"
            error={errors.password}
            leftIcon={<Lock className="w-4 h-4" />}
          />

          {/* Password Strength Indicator */}
          {password.length > 0 && (
            <div className="space-y-1.5 pt-1">
              <div className="flex gap-1 h-1.5 w-full">
                <div
                  className={`flex-1 rounded-full ${
                    strengthScore >= 1 ? 'bg-rose-500' : 'bg-navy-800'
                  }`}
                />
                <div
                  className={`flex-1 rounded-full ${
                    strengthScore >= 2 ? 'bg-amber-500' : 'bg-navy-800'
                  }`}
                />
                <div
                  className={`flex-1 rounded-full ${
                    strengthScore >= 3 ? 'bg-emerald-500' : 'bg-navy-800'
                  }`}
                />
              </div>
              <div className="flex items-center justify-between text-[11px] text-slate-400">
                <span className="flex items-center gap-1">
                  {hasMinLength && <Check className="w-3 h-3 text-emerald-400" />} 8+ chars
                </span>
                <span className="flex items-center gap-1">
                  {hasNumber && <Check className="w-3 h-3 text-emerald-400" />} Numbers
                </span>
                <span className="flex items-center gap-1">
                  {hasSpecial && <Check className="w-3 h-3 text-emerald-400" />} Symbol
                </span>
              </div>
            </div>
          )}

          <div className="space-y-1 pt-1">
            <label className="flex items-start gap-2.5 text-xs text-slate-300 cursor-pointer">
              <input
                type="checkbox"
                checked={agreeTerms}
                onChange={(e) => setAgreeTerms(e.target.checked)}
                className="mt-0.5 rounded bg-navy-950 border-navy-700 text-cyan-500 focus:ring-0 focus:ring-offset-0"
              />
              <span className="text-[11px] text-slate-400 leading-normal">
                I agree to the{' '}
                <a href="#terms" className="text-cyan-400 hover:underline">
                  Terms of Service
                </a>{' '}
                and understand that financial data is protected by AES-256 encryption.
              </span>
            </label>
            {errors.terms && <p className="text-xs text-rose-400">{errors.terms}</p>}
          </div>

          <Button
            type="submit"
            variant="electric"
            size="md"
            className="w-full mt-3"
            isLoading={isSubmitting}
            rightIcon={<ArrowRight className="w-4 h-4" />}
          >
            Create My Account
          </Button>
        </form>

        <div className="mt-6 pt-5 border-t border-navy-800 text-center text-xs text-slate-400">
          <span>Already have an account? </span>
          <Link to="/login" className="text-cyan-400 hover:text-cyan-300 font-semibold transition-colors">
            Sign In
          </Link>
        </div>
      </div>

      {/* Security */}
      <div className="mt-8 flex items-center gap-2 text-xs text-slate-500">
        <ShieldCheck className="w-4 h-4 text-emerald-500" />
        <span>No credit card required. Pure algorithmic financial intelligence.</span>
      </div>
    </div>
  );
};

