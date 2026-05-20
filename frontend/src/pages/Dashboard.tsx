import React, { useState, useEffect, useCallback } from 'react';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useInterval } from '../hooks/useInterval';
import SubmitTransaction from '../components/SubmitTransaction';
import AnalyticsCharts from '../components/AnalyticsCharts';
import { 
  Shield, 
  Plus, 
  RefreshCw, 
  LogOut, 
  Clock, 
  AlertTriangle, 
  CheckCircle2, 
  Server, 
  Play, 
  Loader2,
  Calendar,
  AlertOctagon,
  HelpCircle,
  TrendingUp,
  Cpu
} from 'lucide-react';

interface FraudReason {
  ruleCode: string;
  message: string;
}

interface TransactionDetails {
  id: string;
  externalTransactionId: string;
  userId: string;
  amount: number;
  currency: string;
  merchantId: string;
  ipAddress: string | null;
  deviceId: string | null;
  status: string;
  createdAt: string;
  fraudScore: number | null;
  reasons: FraudReason[];
}

const Dashboard: React.FC = () => {
  const { user, logout } = useAuth();
  const [transactions, setTransactions] = useState<TransactionDetails[]>([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  const [isSubmitOpen, setIsSubmitOpen] = useState(false);
  const [autoRefresh, setAutoRefresh] = useState(true);
  const [evaluatingId, setEvaluatingId] = useState<string | null>(null);
  
  // Search and filter states
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  const fetchTransactions = useCallback(async (isSilent = false) => {
    if (!isSilent) setLoading(true);
    else setRefreshing(true);
    setError(null);

    try {
      const response = await api.get<TransactionDetails[]>('/transactions');
      setTransactions(response.data || []);
    } catch (err: any) {
      setError(err.message || 'Failed to retrieve transactions streams.');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  // Hydrate on mount
  useEffect(() => {
    fetchTransactions();
  }, [fetchTransactions]);

  // Polling Auto-Refresh every 5 seconds if enabled
  useInterval(() => {
    fetchTransactions(true);
  }, autoRefresh ? 5000 : null);

  const handleEvaluate = async (txnId: string) => {
    setEvaluatingId(txnId);
    setError(null);

    try {
      // Backend exposes: POST /transactions/{id}/evaluate
      await api.post(`/transactions/${txnId}/evaluate`);
      
      // Fetch silent reload to update list immediately
      await fetchTransactions(true);
    } catch (err: any) {
      setError(err.message || 'Evaluation trigger failed.');
    } finally {
      setEvaluatingId(null);
    }
  };

  // Stats derivation
  const totalCount = transactions.length;
  const approvedCount = transactions.filter(t => t.status === 'APPROVED').length;
  const reviewCount = transactions.filter(t => t.status === 'UNDER_REVIEW').length;
  const rejectedCount = transactions.filter(t => t.status === 'REJECTED').length;
  const receivedCount = transactions.filter(t => t.status === 'RECEIVED').length;

  // Filters application
  const filteredTransactions = transactions.filter(t => {
    const matchesSearch = 
      t.externalTransactionId.toLowerCase().includes(searchQuery.toLowerCase()) ||
      t.merchantId.toLowerCase().includes(searchQuery.toLowerCase()) ||
      t.userId.toLowerCase().includes(searchQuery.toLowerCase());
      
    const matchesStatus = statusFilter === 'ALL' || t.status === statusFilter;
    
    return matchesSearch && matchesStatus;
  });

  // Relative time helper
  const formatRelativeTime = (isoString: string) => {
    try {
      const date = new Date(isoString);
      const diffMs = Date.now() - date.getTime();
      const diffMins = Math.floor(diffMs / 60000);
      
      if (diffMins < 1) return 'Just now';
      if (diffMins === 1) return '1 min ago';
      if (diffMins < 60) return `${diffMins} mins ago`;
      
      const diffHrs = Math.floor(diffMins / 60);
      if (diffHrs === 1) return '1 hour ago';
      if (diffHrs < 24) return `${diffHrs} hours ago`;
      
      return date.toLocaleDateString(undefined, { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' });
    } catch {
      return isoString;
    }
  };

  return (
    <div className="min-h-screen bg-dark-950 flex flex-col text-slate-100 font-sans">
      
      {/* Top Banner Navigation */}
      <header className="border-b border-dark-800 bg-dark-900/80 backdrop-blur sticky top-0 z-40">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-emerald-500/10 border border-emerald-500/20 rounded-lg text-emerald-400">
              <Shield className="w-6 h-6" />
            </div>
            <div>
              <h1 className="text-md font-bold tracking-wide text-white">SENTINEL</h1>
              <p className="text-[10px] text-dark-400 uppercase tracking-widest font-semibold">Distributed Fraud Detection Platform</p>
            </div>
          </div>

          <div className="flex items-center gap-4">
            {/* Server Status indicator */}
            <div className="hidden sm:flex items-center gap-1.5 px-3 py-1 bg-dark-950 border border-dark-800 rounded-full text-[11px] text-emerald-400">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-500 animate-pulse"></span>
              <span>Backend Core: Active</span>
            </div>

            {/* Operator Hydration Badge */}
            <div className="flex items-center gap-2 border-l border-dark-800 pl-4">
              <div className="w-8 h-8 rounded-full bg-dark-800 border border-dark-700 flex items-center justify-center text-emerald-400 font-bold uppercase text-xs">
                {user?.username.charAt(0) || 'A'}
              </div>
              <div className="hidden md:block text-left">
                <p className="text-xs font-semibold text-white">{user?.username}</p>
                <p className="text-[9px] text-dark-400 font-mono tracking-wider">{user?.role || 'ANALYST'}</p>
              </div>
              <button 
                onClick={logout} 
                className="p-2 ml-1 bg-dark-950/60 border border-dark-800 hover:bg-red-500/10 hover:text-red-400 hover:border-red-500/20 rounded-lg text-dark-400 transition-colors"
                title="Disconnect Analyst Console"
              >
                <LogOut className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Container */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-8">
        
        {/* Error Bar */}
        {error && (
          <div className="mb-6 p-4 bg-red-500/10 border border-red-500/20 rounded-2xl flex items-start gap-3 text-red-400 text-sm">
            <AlertTriangle className="w-5 h-5 shrink-0 mt-0.5" />
            <div className="flex-1">
              <h5 className="font-bold">Execution Error</h5>
              <p className="text-xs text-red-400/90 mt-0.5">{error}</p>
            </div>
            <button onClick={() => setError(null)} className="text-xs hover:underline text-dark-400 hover:text-white uppercase font-bold">dismiss</button>
          </div>
        )}

        {/* Dashboard Title & Actions Bar */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
          <div>
            <h2 className="text-2xl font-bold tracking-tight text-white">Monitoring Console</h2>
            <p className="text-xs text-dark-400 mt-1">Audit high-velocity transaction streams processed by Spring Boot, Kafka, and Redis</p>
          </div>

          <div className="flex items-center gap-3">
            {/* Auto Refresh toggle */}
            <label className="flex items-center gap-2 cursor-pointer bg-dark-900 border border-dark-800 rounded-xl px-4 py-2 text-xs font-semibold text-dark-300 select-none">
              <input 
                type="checkbox" 
                checked={autoRefresh} 
                onChange={(e) => setAutoRefresh(e.target.checked)}
                className="rounded border-dark-700 bg-dark-950 text-emerald-500 focus:ring-0 focus:ring-offset-0 w-3.5 h-3.5"
              />
              <span className={autoRefresh ? 'text-emerald-400' : ''}>Auto-Refresh (5s)</span>
            </label>

            {/* Refresh buttons */}
            <button 
              onClick={() => fetchTransactions()}
              disabled={loading || refreshing}
              className="p-2.5 bg-dark-900 border border-dark-800 hover:bg-dark-850 active:scale-95 transition-all text-dark-300 hover:text-white rounded-xl disabled:opacity-50"
              title="Force stream refresh"
            >
              <RefreshCw className={`w-4 h-4 ${refreshing ? 'animate-spin' : ''}`} />
            </button>

            {/* Submit transactions trigger */}
            <button 
              onClick={() => setIsSubmitOpen(true)}
              className="bg-emerald-600 hover:bg-emerald-500 active:scale-95 transition-all text-white font-medium text-sm py-2.5 px-4 rounded-xl shadow-lg shadow-emerald-950/20 flex items-center gap-2"
            >
              <Plus className="w-4 h-4" />
              <span>Submit Txn</span>
            </button>
          </div>
        </div>

        {/* Overview KPI Cards */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
          
          {/* Card 1: APPROVED */}
          <div className="glass rounded-2xl p-5 relative overflow-hidden">
            <div className="absolute top-0 left-0 w-1.5 h-full bg-emerald-500"></div>
            <div className="flex items-center justify-between text-dark-400 mb-2">
              <span className="text-xs font-bold uppercase tracking-wider">Approved</span>
              <CheckCircle2 className="w-5 h-5 text-emerald-400" />
            </div>
            <p className="text-2xl font-bold text-white tracking-tight">{approvedCount}</p>
            <div className="flex items-center gap-1.5 mt-2 text-[10px] text-dark-500 font-medium">
              <TrendingUp className="w-3.5 h-3.5 text-emerald-400" />
              <span>Safe clear rate: {totalCount > 0 ? Math.round((approvedCount / totalCount) * 100) : 0}%</span>
            </div>
          </div>

          {/* Card 2: UNDER REVIEW */}
          <div className="glass rounded-2xl p-5 relative overflow-hidden">
            <div className="absolute top-0 left-0 w-1.5 h-full bg-amber-500"></div>
            <div className="flex items-center justify-between text-dark-400 mb-2">
              <span className="text-xs font-bold uppercase tracking-wider">Under Review</span>
              <Clock className="w-5 h-5 text-amber-400" />
            </div>
            <p className="text-2xl font-bold text-white tracking-tight">{reviewCount}</p>
            <p className="text-[10px] text-dark-500 font-medium mt-2 flex items-center gap-1">
              <Cpu className="w-3.5 h-3.5 text-amber-400" />
              Velocity risk buffers active
            </p>
          </div>

          {/* Card 3: REJECTED */}
          <div className="glass rounded-2xl p-5 relative overflow-hidden">
            <div className="absolute top-0 left-0 w-1.5 h-full bg-red-500"></div>
            <div className="flex items-center justify-between text-dark-400 mb-2">
              <span className="text-xs font-bold uppercase tracking-wider">Rejected</span>
              <AlertOctagon className="w-5 h-5 text-red-500" />
            </div>
            <p className="text-2xl font-bold text-white tracking-tight">{rejectedCount}</p>
            <p className="text-[10px] text-dark-500 font-medium mt-2 flex items-center gap-1">
              <AlertTriangle className="w-3.5 h-3.5 text-red-400" />
              Block rate: {totalCount > 0 ? Math.round((rejectedCount / totalCount) * 100) : 0}%
            </p>
          </div>

          {/* Card 4: PENDING EVALUATION */}
          <div className="glass rounded-2xl p-5 relative overflow-hidden">
            <div className="absolute top-0 left-0 w-1.5 h-full bg-slate-500"></div>
            <div className="flex items-center justify-between text-dark-400 mb-2">
              <span className="text-xs font-bold uppercase tracking-wider">Pending Eval</span>
              <Server className="w-5 h-5 text-slate-400" />
            </div>
            <p className="text-2xl font-bold text-white tracking-tight">{receivedCount}</p>
            <p className="text-[10px] text-dark-500 font-medium mt-2">
              Requires manual or Kafka trigger
            </p>
          </div>

        </div>

        {/* Custom SVG Charts */}
        <AnalyticsCharts transactions={transactions} />

        {/* Monitoring Controls & Table Section */}
        <div className="glass rounded-2xl overflow-hidden">
          
          {/* Table Header / Filters */}
          <div className="p-6 border-b border-dark-800 flex flex-col md:flex-row md:items-center justify-between gap-4 bg-dark-900/30">
            <div>
              <h4 className="text-sm font-bold text-white uppercase tracking-wider">Transaction Stream</h4>
              <p className="text-xs text-dark-400 mt-1">Live monitoring ledger showing submitted transactions and velocity ratings</p>
            </div>

            <div className="flex flex-col sm:flex-row items-center gap-3">
              {/* Filter by Status */}
              <div className="flex bg-dark-950 border border-dark-800 rounded-xl p-1 w-full sm:w-auto">
                {['ALL', 'APPROVED', 'UNDER_REVIEW', 'REJECTED', 'RECEIVED'].map((status) => (
                  <button
                    key={status}
                    onClick={() => setStatusFilter(status)}
                    className={`px-3 py-1.5 rounded-lg text-xs font-semibold uppercase transition-colors whitespace-nowrap flex-1 sm:flex-none ${
                      statusFilter === status 
                        ? 'bg-dark-800 text-white shadow' 
                        : 'text-dark-400 hover:text-white'
                    }`}
                  >
                    {status === 'UNDER_REVIEW' ? 'Review' : status === 'RECEIVED' ? 'Pending' : status}
                  </button>
                ))}
              </div>

              {/* Search query input */}
              <div className="w-full sm:w-64">
                <input
                  type="text"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full bg-dark-950 border border-dark-800 rounded-xl py-2 px-4 text-xs text-white placeholder-dark-500 focus:outline-none focus:border-emerald-500/50"
                  placeholder="Search merchant, ID, user..."
                />
              </div>
            </div>
          </div>

          {/* Table Body */}
          <div className="overflow-x-auto">
            {loading ? (
              <div className="py-20 flex flex-col items-center justify-center">
                <Loader2 className="w-10 h-10 text-emerald-500 animate-spin" />
                <p className="mt-4 text-dark-400 text-sm font-medium tracking-wide">Syncing streams with H2 cache...</p>
              </div>
            ) : filteredTransactions.length === 0 ? (
              <div className="py-20 flex flex-col items-center justify-center text-center">
                <HelpCircle className="w-12 h-12 text-dark-500" />
                <p className="mt-4 text-white text-sm font-bold">No transactions found</p>
                <p className="mt-1 text-dark-400 text-xs max-w-sm">No transaction matches your search parameters. Try submitting a new transaction stream.</p>
              </div>
            ) : (
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="border-b border-dark-800 bg-dark-950/40 text-[10px] uppercase tracking-wider text-dark-400 font-bold">
                    <th className="py-4 px-6">Transaction ID / Origin</th>
                    <th className="py-4 px-6 text-right">Amount</th>
                    <th className="py-4 px-6">Decision Status</th>
                    <th className="py-4 px-6">Fraud Risk Score</th>
                    <th className="py-4 px-6">Violation Reason Codes</th>
                    <th className="py-4 px-6 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-dark-850">
                  {filteredTransactions.map((txn) => {
                    const isEvaluating = evaluatingId === txn.id;
                    const hasScore = txn.fraudScore !== null;
                    const score = txn.fraudScore || 0;

                    return (
                      <tr key={txn.id} className="hover:bg-dark-900/40 transition-colors text-xs">
                        
                        {/* ID / Origin info */}
                        <td className="py-4 px-6">
                          <div className="flex flex-col gap-0.5">
                            <span className="font-mono font-bold text-white">{txn.externalTransactionId}</span>
                            <span className="text-[10px] text-dark-400 font-medium">Merchant: <strong className="text-slate-300 font-mono">{txn.merchantId}</strong></span>
                            <div className="flex items-center gap-1 mt-1 text-[10px] text-dark-500">
                              <Calendar className="w-3.5 h-3.5" />
                              <span>{formatRelativeTime(txn.createdAt)}</span>
                            </div>
                          </div>
                        </td>

                        {/* Amount */}
                        <td className="py-4 px-6 text-right font-mono font-semibold text-white">
                          {new Intl.NumberFormat(undefined, { style: 'currency', currency: txn.currency }).format(txn.amount)}
                        </td>

                        {/* Decision status badge */}
                        <td className="py-4 px-6">
                          {txn.status === 'APPROVED' && (
                            <span className="px-2.5 py-1 bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 rounded-full font-bold text-[10px]">
                              APPROVED
                            </span>
                          )}
                          {txn.status === 'UNDER_REVIEW' && (
                            <span className="px-2.5 py-1 bg-amber-500/10 text-amber-400 border border-amber-500/20 rounded-full font-bold text-[10px]">
                              UNDER REVIEW
                            </span>
                          )}
                          {txn.status === 'REJECTED' && (
                            <span className="px-2.5 py-1 bg-red-500/10 text-red-400 border border-red-500/20 rounded-full font-bold text-[10px]">
                              REJECTED
                            </span>
                          )}
                          {txn.status === 'RECEIVED' && (
                            <span className="px-2.5 py-1 bg-slate-500/10 text-slate-400 border border-slate-500/20 rounded-full font-bold text-[10px]">
                              RECEIVED
                            </span>
                          )}
                        </td>

                        {/* Fraud risk score with bar */}
                        <td className="py-4 px-6">
                          {hasScore ? (
                            <div className="flex flex-col gap-1 w-32">
                              <div className="flex justify-between font-mono text-[10px]">
                                <span className={`font-bold ${
                                  score < 30 ? 'text-emerald-400' :
                                  score < 70 ? 'text-amber-400' :
                                  'text-red-400'
                                }`}>
                                  Score: {score}
                                </span>
                              </div>
                              <div className="w-full bg-dark-950 rounded-full h-1.5 overflow-hidden">
                                <div 
                                  className={`h-1.5 rounded-full ${
                                    score < 30 ? 'bg-emerald-500' :
                                    score < 70 ? 'bg-amber-500' :
                                    'bg-red-500'
                                  }`} 
                                  style={{ width: `${score}%` }}
                                ></div>
                              </div>
                            </div>
                          ) : (
                            <span className="text-dark-500 italic">No score</span>
                          )}
                        </td>

                        {/* Violations and rule messages */}
                        <td className="py-4 px-6">
                          {txn.reasons && txn.reasons.length > 0 ? (
                            <div className="flex flex-wrap gap-1.5 max-w-xs">
                              {txn.reasons.map((reason, idx) => (
                                <span 
                                  key={idx} 
                                  className="px-2 py-0.5 bg-dark-950 border border-dark-800 text-red-400 text-[10px] rounded font-medium"
                                  title={reason.message}
                                >
                                  {reason.ruleCode}
                                </span>
                              ))}
                            </div>
                          ) : txn.status === 'RECEIVED' ? (
                            <span className="text-[10px] text-dark-500 italic">Evaluation pending</span>
                          ) : (
                            <span className="text-[10px] text-emerald-400 font-medium">Clear of flags</span>
                          )}
                        </td>

                        {/* Actions (manual evaluate triggers) */}
                        <td className="py-4 px-6 text-right">
                          {txn.status === 'RECEIVED' ? (
                            <button
                              onClick={() => handleEvaluate(txn.id)}
                              disabled={isEvaluating}
                              className="px-3 py-1.5 bg-emerald-600/10 text-emerald-400 border border-emerald-500/20 hover:bg-emerald-600 hover:text-white rounded-lg transition-all font-bold text-[10px] flex items-center gap-1 ml-auto"
                            >
                              {isEvaluating ? (
                                <Loader2 className="w-3.5 h-3.5 animate-spin" />
                              ) : (
                                <Play className="w-3 h-3 fill-current" />
                              )}
                              <span>Evaluate</span>
                            </button>
                          ) : (
                            <span className="text-[10px] text-dark-500 font-mono select-none">Audited</span>
                          )}
                        </td>

                      </tr>
                    );
                  })}
                </tbody>
              </table>
            )}
          </div>

          {/* Table footer count */}
          {!loading && (
            <div className="p-4 border-t border-dark-800 bg-dark-900/10 text-right text-[10px] text-dark-400 uppercase tracking-wider font-semibold">
              Showing {filteredTransactions.length} of {transactions.length} total transaction entries
            </div>
          )}

        </div>

      </main>

      {/* Slide-over Submit Drawer component */}
      <SubmitTransaction 
        isOpen={isSubmitOpen} 
        onClose={() => setIsSubmitOpen(false)} 
        onSuccess={() => fetchTransactions(true)} 
      />

    </div>
  );
};

export default Dashboard;
