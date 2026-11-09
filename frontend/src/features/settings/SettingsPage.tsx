import React, { useState, useEffect } from 'react';
import {
  Settings,
  User,
  Shield,
  Bot,
  Lock,
  Cpu,
  CheckCircle2,
  Key,
} from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Button } from '@/shared/components/ui/Button';
import { Badge } from '@/shared/components/ui/Badge';
import { Input } from '@/shared/components/ui/Input';
import { useAuth } from '@/core/auth/AuthContext';

export const SettingsPage: React.FC = () => {
  const { user } = useAuth();

  // Profile Form
  const [firstName, setFirstName] = useState(user?.firstName || 'Sarah');
  const [lastName, setLastName] = useState(user?.lastName || 'Connor');
  const [email] = useState(user?.email || 'sarah.connor@finpath.ai');
  const [riskTolerance, setRiskTolerance] = useState('MODERATE');

  // Security
  const [mfaEnabled, setMfaEnabled] = useState(true);

  // AI Provider Setting (Provider-independent demo)
  const [selectedProvider, setSelectedProvider] = useState<'OPENAI' | 'GEMINI' | 'ANTHROPIC' | 'OLLAMA'>('OPENAI');
  const [savedSuccess, setSavedSuccess] = useState(false);

  useEffect(() => {
    const saved = localStorage.getItem('finpath_preferences');
    if (saved) {
      try {
        const parsed = JSON.parse(saved);
        if (parsed.firstName) setFirstName(parsed.firstName);
        if (parsed.lastName) setLastName(parsed.lastName);
        if (parsed.riskTolerance) setRiskTolerance(parsed.riskTolerance);
        if (parsed.mfaEnabled !== undefined) setMfaEnabled(parsed.mfaEnabled);
        if (parsed.selectedProvider) setSelectedProvider(parsed.selectedProvider);
      } catch (e) {
        // ignore
      }
    }
  }, []);

  const handleSave = (e: React.FormEvent) => {
    e.preventDefault();
    localStorage.setItem(
      'finpath_preferences',
      JSON.stringify({
        firstName,
        lastName,
        riskTolerance,
        mfaEnabled,
        selectedProvider,
      })
    );
    setSavedSuccess(true);
    setTimeout(() => setSavedSuccess(false), 2500);
  };

  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      {/* Header */}
      <div>
        <div className="flex items-center gap-2 mb-1">
          <Settings className="w-5 h-5 text-cyan-400" />
          <h1 className="text-2xl font-bold text-white tracking-tight">Account & System Settings</h1>
        </div>
        <p className="text-xs text-slate-400">
          Manage identity security, data privacy, and provider-independent AI gateway preferences.
        </p>
      </div>

      {savedSuccess && (
        <div className="p-3.5 rounded-xl bg-emerald-500/15 border border-emerald-500/30 text-emerald-300 text-xs flex items-center gap-2 animate-fade-in">
          <CheckCircle2 className="w-4 h-4 shrink-0" />
          <span>Configuration preferences updated and synchronized across all financial domains.</span>
        </div>
      )}

      {/* 1. Profile & Identity Information */}
      <Card className="p-6 bg-navy-900 border border-navy-700/80">
        <CardHeader className="p-0 pb-4">
          <div className="flex items-center gap-2">
            <User className="w-4 h-4 text-cyan-400" />
            <CardTitle>Member Profile & Identity</CardTitle>
          </div>
          <p className="text-[11px] text-slate-400">
            Legal identity information utilized for loan underwriting and insurance certification.
          </p>
        </CardHeader>

        <CardContent className="p-0 space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="First Name"
              type="text"
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
            />
            <Input
              label="Last Name"
              type="text"
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Primary Email"
              type="email"
              value={email}
              disabled
              helperText="Verified authentication identifier"
            />
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1.5">
                Financial Risk Tolerance
              </label>
              <select
                value={riskTolerance}
                onChange={(e) => setRiskTolerance(e.target.value)}
                className="w-full glass-input rounded-lg px-3 py-2 text-xs text-slate-200 focus:border-cyan-500"
              >
                <option value="CONSERVATIVE">Conservative (Capital Preservation)</option>
                <option value="MODERATE">Moderate (Balanced Growth)</option>
                <option value="AGGRESSIVE">Aggressive (Maximum Alpha)</option>
              </select>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 2. Provider-Independent AI Gateway Configuration */}
      <Card className="p-6 bg-navy-900 border border-electric-500/30 shadow-glow-sm">
        <CardHeader className="p-0 pb-4 flex flex-row items-center justify-between">
          <div>
            <div className="flex items-center gap-2">
              <Cpu className="w-5 h-5 text-cyan-400" />
              <CardTitle>AI Gateway Provider Selection</CardTitle>
            </div>
            <p className="text-[11px] text-slate-400">
              Select primary orchestration model. If the active model hits a rate limit, the fallback router automatically engages.
            </p>
          </div>
          <Badge variant="electric" size="sm">
            Zero Lock-in Architecture
          </Badge>
        </CardHeader>

        <CardContent className="p-0 space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {[
              {
                id: 'OPENAI',
                name: 'OpenAI GPT-4o',
                desc: 'Balanced reasoning, multi-tool function calling, 128k context.',
                latency: '450ms',
              },
              {
                id: 'GEMINI',
                name: 'Google Gemini 1.5 Pro',
                desc: 'Deep document comprehension, 1M token window, RAG optimized.',
                latency: '390ms',
              },
              {
                id: 'ANTHROPIC',
                name: 'Anthropic Claude 3.5 Sonnet',
                desc: 'Superior financial reasoning, strict instruction following.',
                latency: '480ms',
              },
              {
                id: 'OLLAMA',
                name: 'Ollama (Local Llama 3)',
                desc: 'Zero-cloud egress, on-premise privacy, offline development.',
                latency: '120ms (local)',
              },
            ].map((p) => (
              <div
                key={p.id}
                onClick={() => setSelectedProvider(p.id as any)}
                className={`p-4 rounded-xl border cursor-pointer transition-all ${
                  selectedProvider === p.id
                    ? 'bg-electric-600/15 border-cyan-400 shadow-glow-sm'
                    : 'bg-navy-950/60 border-navy-800 hover:border-slate-600'
                }`}
              >
                <div className="flex items-center justify-between mb-1">
                  <span className="text-xs font-bold text-white">{p.name}</span>
                  <span className="text-[10px] text-cyan-400 font-mono">{p.latency}</span>
                </div>
                <p className="text-[11px] text-slate-400">{p.desc}</p>
              </div>
            ))}
          </div>
        </CardContent>
      </Card>

      {/* 3. Security & Multi-Factor Auth */}
      <Card className="p-6 bg-navy-900 border border-navy-700/80">
        <CardHeader className="p-0 pb-4">
          <div className="flex items-center gap-2">
            <Lock className="w-4 h-4 text-emerald-400" />
            <CardTitle>Security & Encryption Controls</CardTitle>
          </div>
          <p className="text-[11px] text-slate-400">
            Control authentication barriers and cryptographic privacy settings.
          </p>
        </CardHeader>

        <CardContent className="p-0 space-y-4">
          <div className="flex items-center justify-between p-3.5 rounded-xl bg-navy-950/60 border border-navy-800">
            <div>
              <p className="text-xs font-semibold text-white">Two-Factor Authentication (TOTP)</p>
              <p className="text-[11px] text-slate-400">
                Requires authenticator code on unrecognized device logins.
              </p>
            </div>
            <button
              type="button"
              onClick={() => setMfaEnabled(!mfaEnabled)}
              className={`w-11 h-6 flex items-center rounded-full p-1 transition-colors ${
                mfaEnabled ? 'bg-cyan-500' : 'bg-navy-800'
              }`}
            >
              <div
                className={`bg-white w-4 h-4 rounded-full shadow-md transform transition-transform ${
                  mfaEnabled ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>

          <div className="flex items-center justify-between p-3.5 rounded-xl bg-navy-950/60 border border-navy-800">
            <div>
              <p className="text-xs font-semibold text-white">Blind Index PII Masking</p>
              <p className="text-[11px] text-slate-400">
                HMAC-SHA256 blind indexing prevents cleartext storage of National IDs and SSNs.
              </p>
            </div>
            <Badge variant="success" size="sm">
              AES-256 Active
            </Badge>
          </div>
        </CardContent>
      </Card>

      {/* Save Button */}
      <div className="flex justify-end pt-2">
        <Button variant="electric" size="md" onClick={handleSave}>
          Save All Changes
        </Button>
      </div>
    </div>
  );
};

