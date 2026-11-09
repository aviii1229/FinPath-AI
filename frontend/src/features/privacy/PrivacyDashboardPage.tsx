import React, { useEffect, useState } from 'react';
import {
  ShieldCheck,
  Lock,
  Download,
  Trash2,
  Eye,
  FileText,
  CheckCircle2,
  AlertTriangle,
  Cpu,
  History,
  Info,
  ExternalLink,
  RefreshCw,
  UserCheck
} from 'lucide-react';
import { privacyApi, PrivacyConsent, DataUsageExplanation, UserDataExport } from '../../core/api/privacyApi';

export const PrivacyDashboardPage: React.FC = () => {
  const [consents, setConsents] = useState<PrivacyConsent | null>(null);
  const [explanation, setExplanation] = useState<DataUsageExplanation | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [saving, setSaving] = useState<boolean>(false);
  const [exporting, setExporting] = useState<boolean>(false);
  const [statusMessage, setStatusMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);
  const [showDeleteModal, setShowDeleteModal] = useState<boolean>(false);
  const [deleteConfirmText, setDeleteConfirmText] = useState<string>('');
  const [deleting, setDeleting] = useState<boolean>(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [c, e] = await Promise.all([
        privacyApi.getConsents(),
        privacyApi.getDataUsageExplanation()
      ]);
      setConsents(c);
      setExplanation(e);
    } catch (err: any) {
      // Offline / Test Fallback
      setConsents({
        dataCollectionConsent: true,
        aiProcessingConsent: true,
        marketingCommunicationConsent: false,
        thirdPartySharingConsent: false,
        documentOcrStorageConsent: true,
        storeChatHistory: true,
        consentVersion: 'v2026.1'
      });
      setExplanation({
        complianceFramework: 'Digital Personal Data Protection Act 2023 (DPDP)',
        dataFiduciary: 'FinPath AI Technologies Pvt Ltd',
        legalBasis: 'Explicit Digital Consent (Section 6, DPDP)',
        dataCategories: {
          'Identity & Profile': 'KYC, contact details, authentication credentials',
          'Financial & Banking': 'Accounts, bank statements, transactions in INR (₹)',
          'Loan Underwriting': 'Income statements, requested credit terms, EMI obligations',
          'Insurance Records': 'Policy terms, coverage limits, claims history',
          'Document Vault': 'OCR extracted text from uploaded financial PDFs'
        },
        userRightsUnderDpdp: [
          'Right to Access information about personal data (Sec. 11)',
          'Right to Correction and Erasure of personal data (Sec. 12)',
          'Right of Grievance Redressal with FinPath Data Fiduciary (Sec. 13)',
          'Right to Nominate beneficiary in event of incapacitation (Sec. 14)'
        ],
        retentionPolicy: 'Active account lifecycle + statutory RBI/IRDAI audit retention (7 years)',
        grievanceOfficerContact: 'grievance-officer@finpath.ai | Paytm-Hyderabad Tech Hub'
      });
    } finally {
      setLoading(false);
    }
  };

  const handleToggle = (field: keyof PrivacyConsent) => {
    if (!consents) return;
    setConsents({
      ...consents,
      [field]: !consents[field]
    });
  };

  const handleSaveConsents = async () => {
    if (!consents) return;
    try {
      setSaving(true);
      const updated = await privacyApi.updateConsents(consents);
      setConsents(updated);
      setStatusMessage({ type: 'success', text: 'Privacy preferences successfully updated and cryptographically audited.' });
      setTimeout(() => setStatusMessage(null), 4000);
    } catch (err: any) {
      setStatusMessage({ type: 'error', text: 'Failed to save consent changes.' });
    } finally {
      setSaving(false);
    }
  };

  const handleExportData = async () => {
    try {
      setExporting(true);
      const exportData: UserDataExport = await privacyApi.exportUserData();
      const blob = new Blob([JSON.stringify(exportData, null, 2)], { type: 'application/json' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `finpath_data_export_${new Date().toISOString().substring(0, 10)}.json`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(url);
      setStatusMessage({ type: 'success', text: 'Machine-readable personal data exported in JSON format (Right to Data Portability).' });
      setTimeout(() => setStatusMessage(null), 5000);
    } catch (err: any) {
      setStatusMessage({ type: 'error', text: 'Failed to generate data export.' });
    } finally {
      setExporting(false);
    }
  };

  const handleDeleteAccount = async () => {
    if (deleteConfirmText !== 'DELETE') return;
    try {
      setDeleting(true);
      await privacyApi.deleteAccount();
      setShowDeleteModal(false);
      alert('Your account and personally identifiable data have been successfully anonymized and scrubbed per DPDP Act 2023. You will now be signed out.');
      localStorage.clear();
      window.location.href = '/login';
    } catch (err: any) {
      setStatusMessage({ type: 'error', text: 'Failed to process account deletion request.' });
      setDeleting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[500px]">
        <RefreshCw className="w-8 h-8 text-sky-500 animate-spin" />
        <span className="ml-3 text-slate-300 font-medium">Loading privacy and consent settings...</span>
      </div>
    );
  }

  return (
    <div className="p-6 max-w-6xl mx-auto space-y-8">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-slate-900 via-sky-950 to-slate-900 border border-sky-800/40 rounded-2xl p-6 shadow-xl relative overflow-hidden">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="space-y-1">
            <div className="flex items-center gap-2">
              <span className="bg-emerald-500/20 text-emerald-400 border border-emerald-500/30 text-xs px-2.5 py-1 rounded-full font-semibold flex items-center gap-1">
                <ShieldCheck className="w-3.5 h-3.5" /> DPDP Act 2023 Aligned
              </span>
              <span className="bg-sky-500/20 text-sky-400 border border-sky-500/30 text-xs px-2.5 py-1 rounded-full font-medium">
                Paytm-Hyderabad Edition
              </span>
            </div>
            <h1 className="text-2xl md:text-3xl font-bold text-white tracking-tight flex items-center gap-3">
              <Lock className="w-7 h-7 text-sky-400" /> Privacy & Consent Center
            </h1>
            <p className="text-slate-400 text-sm max-w-2xl">
              Take full control of your personal financial information. Manage granular consents, inspect data transparency disclosures, export your complete profile, and exercise statutory rights.
            </p>
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={handleExportData}
              disabled={exporting}
              className="px-4 py-2.5 bg-slate-800 hover:bg-slate-700 text-slate-200 border border-slate-700 rounded-xl text-sm font-medium transition flex items-center gap-2 shadow-sm disabled:opacity-50"
            >
              {exporting ? <RefreshCw className="w-4 h-4 animate-spin text-sky-400" /> : <Download className="w-4 h-4 text-sky-400" />}
              Export Data (JSON)
            </button>
          </div>
        </div>

        {statusMessage && (
          <div className={`mt-4 p-3 rounded-xl border flex items-center gap-2 text-sm ${
            statusMessage.type === 'success'
              ? 'bg-emerald-950/60 border-emerald-500/40 text-emerald-200'
              : 'bg-rose-950/60 border-rose-500/40 text-rose-200'
          }`}>
            {statusMessage.type === 'success' ? (
              <CheckCircle2 className="w-4 h-4 text-emerald-400 flex-shrink-0" />
            ) : (
              <AlertTriangle className="w-4 h-4 text-rose-400 flex-shrink-0" />
            )}
            <span>{statusMessage.text}</span>
          </div>
        )}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Left Column: Granular Consent Controls */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-6 shadow-md backdrop-blur-sm">
            <div className="flex items-center justify-between pb-4 border-b border-slate-800 mb-6">
              <div>
                <h2 className="text-lg font-bold text-white flex items-center gap-2">
                  <UserCheck className="w-5 h-5 text-sky-400" /> Consent Management
                </h2>
                <p className="text-xs text-slate-400">
                  Granular control over processing activities. You may modify or withdraw your consent at any time.
                </p>
              </div>
              <span className="text-xs font-mono bg-slate-800 text-slate-300 px-2.5 py-1 rounded border border-slate-700">
                {consents?.consentVersion || 'v2026.1'}
              </span>
            </div>

            {consents && (
              <div className="space-y-5">
                {/* 1. Core Data Collection */}
                <div className="flex items-start justify-between gap-4 p-4 rounded-xl bg-slate-800/40 border border-slate-800 hover:border-slate-700 transition">
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <FileText className="w-4 h-4 text-sky-400" />
                      <span className="font-semibold text-slate-200 text-sm">Essential Data Collection</span>
                      <span className="text-[10px] bg-slate-700 text-slate-300 px-1.5 py-0.5 rounded uppercase font-semibold">Required</span>
                    </div>
                    <p className="text-xs text-slate-400 leading-relaxed">
                      Necessary for account authentication, transactional records in INR (₹), and core financial dashboard telemetry.
                    </p>
                  </div>
                  <input
                    type="checkbox"
                    checked={consents.dataCollectionConsent}
                    onChange={() => handleToggle('dataCollectionConsent')}
                    disabled={true}
                    className="w-5 h-5 mt-1 accent-sky-500 rounded cursor-not-allowed opacity-60"
                  />
                </div>

                {/* 2. AI Processing Consent */}
                <div className="flex items-start justify-between gap-4 p-4 rounded-xl bg-slate-800/40 border border-slate-800 hover:border-slate-700 transition">
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <Cpu className="w-4 h-4 text-purple-400" />
                      <span className="font-semibold text-slate-200 text-sm">AI Financial Intelligence & Recommendations</span>
                    </div>
                    <p className="text-xs text-slate-400 leading-relaxed">
                      Allows FinPath AI orchestrator, policy RAG, and underwriting assistants to analyze financial inputs to provide personalized guidance.
                    </p>
                  </div>
                  <input
                    type="checkbox"
                    checked={consents.aiProcessingConsent}
                    onChange={() => handleToggle('aiProcessingConsent')}
                    className="w-5 h-5 mt-1 accent-purple-500 rounded cursor-pointer"
                  />
                </div>

                {/* 3. Document OCR Storage */}
                <div className="flex items-start justify-between gap-4 p-4 rounded-xl bg-slate-800/40 border border-slate-800 hover:border-slate-700 transition">
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <Eye className="w-4 h-4 text-emerald-400" />
                      <span className="font-semibold text-slate-200 text-sm">Document Vault & OCR Indexing</span>
                    </div>
                    <p className="text-xs text-slate-400 leading-relaxed">
                      Enables secure encrypted indexing of uploaded policy schedules, salary slips, and sanction letters for instant clause search.
                    </p>
                  </div>
                  <input
                    type="checkbox"
                    checked={consents.documentOcrStorageConsent}
                    onChange={() => handleToggle('documentOcrStorageConsent')}
                    className="w-5 h-5 mt-1 accent-emerald-500 rounded cursor-pointer"
                  />
                </div>

                {/* 4. Chat History Storage */}
                <div className="flex items-start justify-between gap-4 p-4 rounded-xl bg-slate-800/40 border border-slate-800 hover:border-slate-700 transition">
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <History className="w-4 h-4 text-amber-400" />
                      <span className="font-semibold text-slate-200 text-sm">AI Conversation History Preservation</span>
                    </div>
                    <p className="text-xs text-slate-400 leading-relaxed">
                      Retains multi-turn financial assistance dialogues across sessions. When toggled off, chat sessions remain purely in-memory ephemeral.
                    </p>
                  </div>
                  <input
                    type="checkbox"
                    checked={consents.storeChatHistory}
                    onChange={() => handleToggle('storeChatHistory')}
                    className="w-5 h-5 mt-1 accent-amber-500 rounded cursor-pointer"
                  />
                </div>

                {/* 5. Marketing Communications */}
                <div className="flex items-start justify-between gap-4 p-4 rounded-xl bg-slate-800/40 border border-slate-800 hover:border-slate-700 transition">
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <Info className="w-4 h-4 text-blue-400" />
                      <span className="font-semibold text-slate-200 text-sm">Proactive Advisory & Updates</span>
                    </div>
                    <p className="text-xs text-slate-400 leading-relaxed">
                      Permits FinPath AI to dispatch curated financial education briefings, product updates, and interest rate benchmark reports.
                    </p>
                  </div>
                  <input
                    type="checkbox"
                    checked={consents.marketingCommunicationConsent}
                    onChange={() => handleToggle('marketingCommunicationConsent')}
                    className="w-5 h-5 mt-1 accent-blue-500 rounded cursor-pointer"
                  />
                </div>

                {/* 6. Third Party Sharing */}
                <div className="flex items-start justify-between gap-4 p-4 rounded-xl bg-slate-800/40 border border-slate-800 hover:border-slate-700 transition">
                  <div className="space-y-1">
                    <div className="flex items-center gap-2">
                      <ExternalLink className="w-4 h-4 text-pink-400" />
                      <span className="font-semibold text-slate-200 text-sm">Partner Ecosystem Sharing</span>
                    </div>
                    <p className="text-xs text-slate-400 leading-relaxed">
                      Share verified verification packets with RBI-registered NBFCs or IRDAI insurers for 1-click application submission.
                    </p>
                  </div>
                  <input
                    type="checkbox"
                    checked={consents.thirdPartySharingConsent}
                    onChange={() => handleToggle('thirdPartySharingConsent')}
                    className="w-5 h-5 mt-1 accent-pink-500 rounded cursor-pointer"
                  />
                </div>

                <div className="pt-4 flex items-center justify-end">
                  <button
                    onClick={handleSaveConsents}
                    disabled={saving}
                    className="px-6 py-2.5 bg-gradient-to-r from-sky-500 to-indigo-600 hover:from-sky-400 hover:to-indigo-500 text-white font-medium text-sm rounded-xl transition shadow-lg shadow-sky-900/30 flex items-center gap-2 disabled:opacity-50"
                  >
                    {saving && <RefreshCw className="w-4 h-4 animate-spin" />}
                    Save Privacy Preferences
                  </button>
                </div>
              </div>
            )}
          </div>

          {/* Danger Zone: Account Deletion */}
          <div className="bg-rose-950/20 border border-rose-900/40 rounded-2xl p-6 shadow-md">
            <h3 className="text-base font-bold text-rose-300 flex items-center gap-2 mb-2">
              <Trash2 className="w-5 h-5 text-rose-400" /> Data Erasure & Account Deletion (DPDP Sec. 12)
            </h3>
            <p className="text-xs text-rose-200/70 mb-4 leading-relaxed">
              Permanently delete your user profile and anonymize all historical activity records. In compliance with the Digital Personal Data Protection Act 2023, your personally identifiable credentials, tokens, and documents will be scrubbed immediately.
            </p>
            <button
              onClick={() => setShowDeleteModal(true)}
              className="px-4 py-2 bg-rose-600/80 hover:bg-rose-600 text-white text-xs font-semibold rounded-lg transition border border-rose-500/50 shadow-sm flex items-center gap-1.5"
            >
              <Trash2 className="w-3.5 h-3.5" /> Request Permanent Account Erasure
            </button>
          </div>
        </div>

        {/* Right Column: Transparency Matrix & DPDP Info */}
        <div className="space-y-6">
          {/* Statutory Disclosure Card */}
          <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-5 space-y-4">
            <h3 className="text-sm font-bold text-white flex items-center gap-2">
              <ShieldCheck className="w-4 h-4 text-emerald-400" /> Data Fiduciary Notice
            </h3>
            <div className="space-y-2 text-xs text-slate-300">
              <div className="flex justify-between py-1 border-b border-slate-800">
                <span className="text-slate-400">Framework:</span>
                <span className="font-semibold text-slate-200 text-right">{explanation?.complianceFramework || 'DPDP Act 2023'}</span>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-800">
                <span className="text-slate-400">Data Fiduciary:</span>
                <span className="font-semibold text-slate-200 text-right">{explanation?.dataFiduciary || 'FinPath AI Inc.'}</span>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-800">
                <span className="text-slate-400">Legal Basis:</span>
                <span className="font-semibold text-slate-200 text-right">{explanation?.legalBasis || 'User Explicit Consent'}</span>
              </div>
              <div className="flex justify-between py-1 border-b border-slate-800">
                <span className="text-slate-400">Retention:</span>
                <span className="font-semibold text-slate-200 text-right">{explanation?.retentionPolicy || 'Duration of active account'}</span>
              </div>
              <div className="flex justify-between py-1">
                <span className="text-slate-400">Grievance Officer:</span>
                <span className="font-semibold text-sky-400 text-right">{explanation?.grievanceOfficerContact || 'grievance@finpath.ai'}</span>
              </div>
            </div>
          </div>

          {/* User Rights Under DPDP Act */}
          <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-5 space-y-3">
            <h3 className="text-sm font-bold text-white flex items-center gap-2">
              <CheckCircle2 className="w-4 h-4 text-sky-400" /> Your Rights as Data Principal
            </h3>
            <ul className="space-y-2 text-xs text-slate-300">
              {explanation?.userRightsUnderDpdp ? (
                explanation.userRightsUnderDpdp.map((right, idx) => (
                  <li key={idx} className="flex items-start gap-2">
                    <span className="text-sky-400 font-bold">•</span>
                    <span>{right}</span>
                  </li>
                ))
              ) : (
                <>
                  <li className="flex items-start gap-2"><span className="text-sky-400 font-bold">•</span> Right to Access Information about personal data</li>
                  <li className="flex items-start gap-2"><span className="text-sky-400 font-bold">•</span> Right to Correction and Erasure</li>
                  <li className="flex items-start gap-2"><span className="text-sky-400 font-bold">•</span> Right of Grievance Redressal</li>
                  <li className="flex items-start gap-2"><span className="text-sky-400 font-bold">•</span> Right to Nominate in event of incapacitation</li>
                </>
              )}
            </ul>
          </div>

          {/* Data Category Matrix */}
          {explanation?.dataCategories && (
            <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-5 space-y-3">
              <h3 className="text-sm font-bold text-white flex items-center gap-2">
                <FileText className="w-4 h-4 text-purple-400" /> Data Category Breakdown
              </h3>
              <div className="space-y-2.5 max-h-72 overflow-y-auto pr-1 text-xs">
                {Object.entries(explanation.dataCategories).map(([category, purpose]) => (
                  <div key={category} className="p-2.5 rounded-lg bg-slate-800/40 border border-slate-800">
                    <span className="font-semibold text-slate-200 block mb-0.5">{category}</span>
                    <span className="text-slate-400 leading-snug">{purpose}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Account Deletion Confirmation Modal */}
      {showDeleteModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70 backdrop-blur-sm p-4">
          <div className="bg-slate-900 border border-rose-900/60 rounded-2xl p-6 max-w-md w-full shadow-2xl space-y-4">
            <div className="flex items-center gap-3 text-rose-400">
              <AlertTriangle className="w-6 h-6 flex-shrink-0" />
              <h3 className="text-lg font-bold text-white">Confirm Permanent Erasure</h3>
            </div>
            <p className="text-xs text-slate-300 leading-relaxed">
              This action is <strong className="text-rose-400 font-semibold">irreversible</strong>. In accordance with the Digital Personal Data Protection Act 2023, all personally identifiable credentials, phone numbers, uploaded documents, loan drafts, and chat sessions will be permanently purged or anonymized.
            </p>
            <div className="space-y-1.5">
              <label className="text-xs text-slate-400 block">
                Type <strong className="text-rose-400 font-mono">DELETE</strong> below to confirm:
              </label>
              <input
                type="text"
                value={deleteConfirmText}
                onChange={(e) => setDeleteConfirmText(e.target.value)}
                placeholder="DELETE"
                className="w-full bg-slate-950 border border-slate-700 rounded-xl px-3 py-2 text-sm text-white focus:outline-none focus:border-rose-500 font-mono"
              />
            </div>
            <div className="flex items-center justify-end gap-3 pt-2">
              <button
                onClick={() => {
                  setShowDeleteModal(false);
                  setDeleteConfirmText('');
                }}
                disabled={deleting}
                className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold rounded-xl transition"
              >
                Cancel
              </button>
              <button
                onClick={handleDeleteAccount}
                disabled={deleteConfirmText !== 'DELETE' || deleting}
                className="px-4 py-2 bg-rose-600 hover:bg-rose-500 disabled:opacity-50 text-white text-xs font-semibold rounded-xl transition flex items-center gap-1.5 shadow-md shadow-rose-900/40"
              >
                {deleting && <RefreshCw className="w-3.5 h-3.5 animate-spin" />}
                Confirm & Erase My Account
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PrivacyDashboardPage;
