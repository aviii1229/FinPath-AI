import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import {
  Receipt,
  Search,
  Filter,
  PlusCircle,
  ArrowDownLeft,
  ArrowUpRight,
  Download,
  Calendar,
  Sparkles,
} from 'lucide-react';
import { Card, CardHeader, CardTitle, CardContent } from '@/shared/components/ui/Card';
import { Button } from '@/shared/components/ui/Button';
import { Badge } from '@/shared/components/ui/Badge';
import { Modal } from '@/shared/components/ui/Modal';
import { Input } from '@/shared/components/ui/Input';
import { mockTransactions, mockAccounts } from '@/mock/mockData';
import { fintechApi, AutoCategorizeResult } from '@/core/api/fintechApi';
import { formatINR } from '@/core/utils/currency';
import { Transaction, TransactionCategory } from '@/types';

export const TransactionsPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const [transactions, setTransactions] = useState<Transaction[]>(mockTransactions);
  const [searchQuery, setSearchQuery] = useState(searchParams.get('q') || '');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');
  const [selectedStatus, setSelectedStatus] = useState<string>('ALL');

  useEffect(() => {
    const q = searchParams.get('q');
    if (q !== null) {
      setSearchQuery(q);
    }
  }, [searchParams]);

  // Add Transaction Modal
  const [showAddModal, setShowAddModal] = useState(false);
  const [merchantName, setMerchantName] = useState('');
  const [amount, setAmount] = useState('');
  const [category, setCategory] = useState<TransactionCategory>('GROCERIES');
  const [isIncome, setIsIncome] = useState(false);
  const [accountId, setAccountId] = useState(mockAccounts[0].id);
  const [autoCatSuggestion, setAutoCatSuggestion] = useState<AutoCategorizeResult | null>(null);

  useEffect(() => {
    fintechApi.getTransactions().then((res) => {
      if (res && res.length > 0) setTransactions(res);
    }).catch(() => {});
  }, []);

  const handleMerchantChange = async (name: string) => {
    setMerchantName(name);
    if (name.trim().length >= 3) {
      try {
        const catRes = await fintechApi.autoCategorize(name, parseFloat(amount) || undefined);
        setAutoCatSuggestion(catRes);
        if (catRes.category && catRes.category !== 'MISCELLANEOUS') {
          setCategory(catRes.category as TransactionCategory);
        }
      } catch (err) {}
    } else {
      setAutoCatSuggestion(null);
    }
  };

  const categories: (string | TransactionCategory)[] = [
    'ALL',
    'SALARY',
    'HOUSING',
    'GROCERIES',
    'LOAN_PAYMENT',
    'HEALTHCARE',
    'TRANSPORTATION',
    'ENTERTAINMENT',
  ];

  const filteredTransactions = transactions.filter((tx) => {
    const matchesSearch =
      tx.merchantName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      tx.accountName.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = selectedCategory === 'ALL' || tx.category === selectedCategory;
    const matchesStatus = selectedStatus === 'ALL' || tx.status === selectedStatus;
    return matchesSearch && matchesCategory && matchesStatus;
  });

  const handleAddTransaction = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!merchantName || !amount) return;

    const numAmount = parseFloat(amount);
    const selectedAcc = mockAccounts.find((a) => a.id === accountId);
    const finalAmount = isIncome ? Math.abs(numAmount) : -Math.abs(numAmount);

    try {
      const created = await fintechApi.createTransaction({
        accountId,
        amount: finalAmount,
        transactionDate: new Date().toISOString().split('T')[0],
        merchantName,
        category,
        isRecurring: false,
      });
      setTransactions([created, ...transactions]);
    } catch (err) {
      const newTx: Transaction = {
        id: `tx_${Date.now()}`,
        accountId,
        accountName: selectedAcc?.institutionName || 'Account',
        amount: finalAmount,
        currency: 'INR',
        date: new Date().toISOString().split('T')[0],
        merchantName,
        category,
        isRecurring: false,
        status: 'POSTED',
      };
      setTransactions([newTx, ...transactions]);
    }

    setShowAddModal(false);
    setMerchantName('');
    setAmount('');
    setAutoCatSuggestion(null);
  };

  return (
    <div className="space-y-6">
      {/* Page Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <Receipt className="w-5 h-5 text-cyan-400" />
            <h1 className="text-2xl font-bold text-white tracking-tight">Financial Ledger & Transactions</h1>
          </div>
          <p className="text-xs text-slate-400">
            Multi-account real-time debit/credit tracking with AI-assisted expense classification.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Button
            variant="cyan"
            size="sm"
            onClick={() => setShowAddModal(true)}
            leftIcon={<PlusCircle className="w-4 h-4" />}
          >
            Record Transaction
          </Button>
        </div>
      </div>

      {/* Search & Filter Toolbar */}
      <Card className="p-4 bg-navy-900 border border-navy-700/80">
        <div className="flex flex-col md:flex-row items-center gap-4 justify-between">
          {/* Search Input */}
          <div className="relative w-full md:w-96">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none" />
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Search merchants, categories, or accounts..."
              className="w-full glass-input text-xs pl-9 pr-4 py-2 rounded-xl text-slate-200 placeholder-slate-500 focus:border-cyan-500"
            />
          </div>

          {/* Status Filter */}
          <div className="flex items-center gap-2 w-full md:w-auto justify-end text-xs">
            <span className="text-slate-400">Status:</span>
            {['ALL', 'POSTED', 'PENDING'].map((st) => (
              <button
                key={st}
                onClick={() => setSelectedStatus(st)}
                className={`px-3 py-1 rounded-lg text-xs font-medium transition-colors ${
                  selectedStatus === st
                    ? 'bg-electric-600/20 text-cyan-300 border border-cyan-500/30'
                    : 'text-slate-400 hover:text-white'
                }`}
              >
                {st}
              </button>
            ))}
          </div>
        </div>

        {/* Category Pills */}
        <div className="flex items-center gap-2 overflow-x-auto pt-3 border-t border-navy-800 mt-3 pb-1">
          {categories.map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-3 py-1 rounded-lg text-[11px] font-semibold whitespace-nowrap transition-all select-none ${
                selectedCategory === cat
                  ? 'bg-cyan-500/20 text-cyan-300 border border-cyan-500/40 shadow-glow-sm'
                  : 'bg-navy-950 text-slate-400 border border-navy-800 hover:text-slate-200'
              }`}
            >
              {cat.replace('_', ' ')}
            </button>
          ))}
        </div>
      </Card>

      {/* Transactions Table */}
      <Card className="p-0 bg-navy-900 border border-navy-700/80 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-navy-950/70 text-slate-400 uppercase tracking-wider text-[10px] border-b border-navy-800">
              <tr>
                <th className="py-3.5 px-4 font-semibold">Transaction / Merchant</th>
                <th className="py-3.5 px-4 font-semibold">Account</th>
                <th className="py-3.5 px-4 font-semibold">Category</th>
                <th className="py-3.5 px-4 font-semibold">Date</th>
                <th className="py-3.5 px-4 font-semibold">Status</th>
                <th className="py-3.5 px-4 font-semibold text-right">Amount (₹)</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-navy-800 text-slate-200">
              {filteredTransactions.map((tx) => (
                <tr key={tx.id} className="hover:bg-navy-800/40 transition-colors">
                  <td className="py-3.5 px-4 flex items-center gap-3">
                    <div
                      className={`w-7 h-7 rounded-lg flex items-center justify-center shrink-0 ${
                        tx.amount > 0
                          ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                          : 'bg-navy-800 text-slate-400 border border-navy-700'
                      }`}
                    >
                      {tx.amount > 0 ? (
                        <ArrowDownLeft className="w-3.5 h-3.5" />
                      ) : (
                        <ArrowUpRight className="w-3.5 h-3.5" />
                      )}
                    </div>
                    <div>
                      <p className="font-semibold text-white truncate max-w-xs">{tx.merchantName}</p>
                      {tx.isRecurring && (
                        <span className="text-[10px] text-cyan-400 font-mono">Recurring Subscription</span>
                      )}
                    </div>
                  </td>
                  <td className="py-3.5 px-4 text-slate-300 font-mono text-[11px]">{tx.accountName}</td>
                  <td className="py-3.5 px-4">
                    <Badge variant="default" size="sm">
                      {tx.category}
                    </Badge>
                  </td>
                  <td className="py-3.5 px-4 text-slate-400 font-mono text-[11px]">{tx.date}</td>
                  <td className="py-3.5 px-4">
                    <Badge variant={tx.status === 'POSTED' ? 'cyan' : 'warning'} size="sm">
                      {tx.status}
                    </Badge>
                  </td>
                  <td className="py-3.5 px-4 text-right">
                    <span
                      className={`font-mono font-bold text-sm ${
                        tx.amount > 0 ? 'text-emerald-400' : 'text-slate-100'
                      }`}
                    >
                      {tx.amount > 0 ? '+' : '-'}{formatINR(Math.abs(tx.amount))}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {filteredTransactions.length === 0 && (
            <div className="py-12 text-center text-slate-400 text-xs">
              No transactions match your search filter criteria.
            </div>
          )}
        </div>
      </Card>

      {/* Record Transaction Modal */}
      <Modal
        isOpen={showAddModal}
        onClose={() => setShowAddModal(false)}
        title="Record New Transaction"
        description="Manually insert an expense or income entry into your FinPath ledger."
      >
        <form onSubmit={handleAddTransaction} className="space-y-4">
          <Input
            label="Merchant or Description"
            type="text"
            value={merchantName}
            onChange={(e) => handleMerchantChange(e.target.value)}
            placeholder="e.g. Target, Apple Store, Consulting Invoice"
            required
          />

          {autoCatSuggestion && (
            <div className="p-3 rounded-lg bg-cyan-500/10 border border-cyan-500/30 text-xs flex items-center justify-between">
              <div>
                <span className="text-[10px] text-slate-400 block uppercase font-mono">
                  AI Suggested Category
                </span>
                <span className="font-bold text-cyan-300">{autoCatSuggestion.category}</span>
              </div>
              <Badge variant="cyan" size="sm">
                {Math.round(autoCatSuggestion.confidenceScore * 100)}% Match
              </Badge>
            </div>
          )}

          <div className="grid grid-cols-2 gap-3">
            <Input
              label="Amount (₹)"
              type="number"
              step="0.01"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              placeholder="0.00"
              required
            />

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1">Type</label>
              <div className="grid grid-cols-2 gap-2">
                <button
                  type="button"
                  onClick={() => setIsIncome(false)}
                  className={`py-2 text-xs font-medium rounded-lg border transition-colors ${
                    !isIncome
                      ? 'bg-rose-500/20 text-rose-300 border-rose-500/40'
                      : 'bg-navy-950 text-slate-400 border-navy-700'
                  }`}
                >
                  Expense
                </button>
                <button
                  type="button"
                  onClick={() => setIsIncome(true)}
                  className={`py-2 text-xs font-medium rounded-lg border transition-colors ${
                    isIncome
                      ? 'bg-emerald-500/20 text-emerald-300 border-emerald-500/40'
                      : 'bg-navy-950 text-slate-400 border-navy-700'
                  }`}
                >
                  Income
                </button>
              </div>
            </div>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Account</label>
            <select
              value={accountId}
              onChange={(e) => setAccountId(e.target.value)}
              className="w-full glass-input rounded-lg px-3 py-2 text-xs text-slate-200 focus:border-cyan-500"
            >
              {mockAccounts.map((a) => (
                <option key={a.id} value={a.id}>
                  {a.institutionName} ({a.accountMask})
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-medium text-slate-300 mb-1">Expense Category</label>
            <select
              value={category}
              onChange={(e) => setCategory(e.target.value as TransactionCategory)}
              className="w-full glass-input rounded-lg px-3 py-2 text-xs text-slate-200 focus:border-cyan-500"
            >
              <option value="GROCERIES">Groceries & Dining</option>
              <option value="HOUSING">Housing & Rent</option>
              <option value="HEALTHCARE">Healthcare & Pharmacy</option>
              <option value="LOAN_PAYMENT">Loan Payment / Debt</option>
              <option value="TRANSPORTATION">Transportation & Fuel</option>
              <option value="ENTERTAINMENT">Entertainment & Media</option>
              <option value="SALARY">Salary / Earnings</option>
            </select>
          </div>

          <div className="flex justify-end gap-2 pt-2">
            <Button variant="ghost" size="sm" type="button" onClick={() => setShowAddModal(false)}>
              Cancel
            </Button>
            <Button variant="electric" size="sm" type="submit">
              Save Entry
            </Button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

