import React from 'react';
import { ShieldCheck, ShieldAlert, ShieldQuestion, Award } from 'lucide-react';

interface TransactionItem {
  id: string;
  amount: number;
  currency: string;
  status: string;
  fraudScore: number | null;
}

interface AnalyticsChartsProps {
  transactions: TransactionItem[];
}

const AnalyticsCharts: React.FC<AnalyticsChartsProps> = ({ transactions }) => {
  // 1. Calculate Decision Counts
  const total = transactions.length;
  const approved = transactions.filter(t => t.status === 'APPROVED').length;
  const review = transactions.filter(t => t.status === 'UNDER_REVIEW').length;
  const rejected = transactions.filter(t => t.status === 'REJECTED').length;
  const pending = transactions.filter(t => t.status === 'RECEIVED').length;

  // 2. Risk Score Stats
  const scoredTxns = transactions.filter(t => t.fraudScore !== null && t.fraudScore !== undefined);
  const avgScore = scoredTxns.length > 0 
    ? Math.round(scoredTxns.reduce((acc, t) => acc + (t.fraudScore || 0), 0) / scoredTxns.length) 
    : 0;

  const lowRisk = scoredTxns.filter(t => (t.fraudScore || 0) < 30).length;
  const medRisk = scoredTxns.filter(t => (t.fraudScore || 0) >= 30 && (t.fraudScore || 0) < 70).length;
  const highRisk = scoredTxns.filter(t => (t.fraudScore || 0) >= 70).length;

  // Calculate SVGs percentages
  const approvedPct = total > 0 ? (approved / total) * 100 : 0;
  const reviewPct = total > 0 ? (review / total) * 100 : 0;
  const rejectedPct = total > 0 ? (rejected / total) * 100 : 0;
  const pendingPct = total > 0 ? (pending / total) * 100 : 0;

  // SVG Pie Chart calculations (Radius = 50, Circumference = 2 * PI * R = 314.16)
  const r = 50;
  const c = 2 * Math.PI * r; // 314.16

  // Stroke offsets
  const approvedStroke = (approvedPct / 100) * c;
  const reviewStroke = (reviewPct / 100) * c;
  const rejectedStroke = (rejectedPct / 100) * c;
  const pendingStroke = (pendingPct / 100) * c;

  const approvedOffset = c;
  const reviewOffset = approvedOffset - approvedStroke;
  const rejectedOffset = reviewOffset - reviewStroke;
  const pendingOffset = rejectedOffset - rejectedStroke;

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
      
      {/* Chart 1: Decision Distribution Donut */}
      <div className="glass rounded-2xl p-6 flex flex-col justify-between">
        <div>
          <h4 className="text-sm font-bold text-white uppercase tracking-wider mb-1">Decision Share</h4>
          <p className="text-xs text-dark-400">Ratio of system decisions on submitted streams</p>
        </div>

        <div className="flex items-center justify-around py-4 mt-2">
          {/* SVG Donut */}
          <div className="relative w-36 h-36 flex items-center justify-center">
            {total === 0 ? (
              <div className="text-center">
                <p className="text-xs text-dark-500 font-semibold uppercase">No Stream</p>
              </div>
            ) : (
              <>
                <svg className="w-full h-full transform -rotate-90" viewBox="0 0 120 120">
                  {/* Background Track */}
                  <circle cx="60" cy="60" r={r} fill="transparent" stroke="#111827" strokeWidth="12" />
                  
                  {/* APPROVED Section (Green) */}
                  {approvedStroke > 0 && (
                    <circle
                      cx="60"
                      cy="60"
                      r={r}
                      fill="transparent"
                      stroke="#10b981"
                      strokeWidth="12"
                      strokeDasharray={`${approvedStroke} ${c}`}
                      strokeDashoffset={approvedOffset}
                      strokeLinecap="round"
                    />
                  )}

                  {/* UNDER_REVIEW Section (Amber) */}
                  {reviewStroke > 0 && (
                    <circle
                      cx="60"
                      cy="60"
                      r={r}
                      fill="transparent"
                      stroke="#f59e0b"
                      strokeWidth="12"
                      strokeDasharray={`${reviewStroke} ${c}`}
                      strokeDashoffset={reviewOffset}
                      strokeLinecap="round"
                    />
                  )}

                  {/* REJECTED Section (Red) */}
                  {rejectedStroke > 0 && (
                    <circle
                      cx="60"
                      cy="60"
                      r={r}
                      fill="transparent"
                      stroke="#ef4444"
                      strokeWidth="12"
                      strokeDasharray={`${rejectedStroke} ${c}`}
                      strokeDashoffset={rejectedOffset}
                      strokeLinecap="round"
                    />
                  )}

                  {/* RECEIVED/PENDING Section (Slate) */}
                  {pendingStroke > 0 && (
                    <circle
                      cx="60"
                      cy="60"
                      r={r}
                      fill="transparent"
                      stroke="#64748b"
                      strokeWidth="12"
                      strokeDasharray={`${pendingStroke} ${c}`}
                      strokeDashoffset={pendingOffset}
                      strokeLinecap="round"
                    />
                  )}
                </svg>
                {/* Center Badge */}
                <div className="absolute inset-0 flex flex-col items-center justify-center">
                  <span className="text-xl font-bold text-white">{total}</span>
                  <span className="text-[10px] text-dark-400 uppercase font-semibold">Total txns</span>
                </div>
              </>
            )}
          </div>

          {/* Legend Grid */}
          <div className="space-y-2">
            <div className="flex items-center gap-2 text-xs">
              <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 block"></span>
              <span className="text-dark-300 w-24">Approved:</span>
              <span className="font-semibold text-white">{approved}</span>
            </div>
            <div className="flex items-center gap-2 text-xs">
              <span className="w-2.5 h-2.5 rounded-full bg-amber-500 block"></span>
              <span className="text-dark-300 w-24">Under Review:</span>
              <span className="font-semibold text-white">{review}</span>
            </div>
            <div className="flex items-center gap-2 text-xs">
              <span className="w-2.5 h-2.5 rounded-full bg-red-500 block"></span>
              <span className="text-dark-300 w-24">Rejected:</span>
              <span className="font-semibold text-white">{rejected}</span>
            </div>
            <div className="flex items-center gap-2 text-xs">
              <span className="w-2.5 h-2.5 rounded-full bg-slate-500 block"></span>
              <span className="text-dark-300 w-24">Pending Eval:</span>
              <span className="font-semibold text-white">{pending}</span>
            </div>
          </div>
        </div>
      </div>

      {/* Chart 2: Risk Profile Histograms */}
      <div className="glass rounded-2xl p-6 flex flex-col justify-between">
        <div>
          <h4 className="text-sm font-bold text-white uppercase tracking-wider mb-1">Risk Profile Summary</h4>
          <p className="text-xs text-dark-400">Risk rating allocations of evaluated sessions</p>
        </div>

        <div className="space-y-4 my-2">
          {/* Average Badge */}
          <div className="flex items-center justify-between border-b border-dark-800 pb-2 mb-2">
            <span className="text-xs text-dark-300">Average Risk Score:</span>
            <span className={`text-sm font-bold px-2.5 py-0.5 rounded-lg flex items-center gap-1 ${
              avgScore < 30 ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' :
              avgScore < 70 ? 'bg-amber-500/10 text-amber-400 border border-amber-500/20' :
              'bg-red-500/10 text-red-400 border border-red-500/20'
            }`}>
              <Award className="w-3.5 h-3.5" />
              {avgScore} / 100
            </span>
          </div>

          {/* Bar 1: Low Risk (0-29) */}
          <div>
            <div className="flex justify-between text-xs mb-1">
              <span className="text-emerald-400 font-medium flex items-center gap-1">
                <ShieldCheck className="w-3.5 h-3.5" /> Low Risk (0-29)
              </span>
              <span className="text-dark-400 font-semibold">{lowRisk} txns</span>
            </div>
            <div className="w-full bg-dark-950 rounded-full h-2.5 overflow-hidden">
              <div 
                className="bg-emerald-500 h-2.5 rounded-full transition-all duration-300"
                style={{ width: `${scoredTxns.length > 0 ? (lowRisk / scoredTxns.length) * 100 : 0}%` }}
              ></div>
            </div>
          </div>

          {/* Bar 2: Medium Risk (30-69) */}
          <div>
            <div className="flex justify-between text-xs mb-1">
              <span className="text-amber-400 font-medium flex items-center gap-1">
                <ShieldQuestion className="w-3.5 h-3.5" /> Medium Risk (30-69)
              </span>
              <span className="text-dark-400 font-semibold">{medRisk} txns</span>
            </div>
            <div className="w-full bg-dark-950 rounded-full h-2.5 overflow-hidden">
              <div 
                className="bg-amber-500 h-2.5 rounded-full transition-all duration-300"
                style={{ width: `${scoredTxns.length > 0 ? (medRisk / scoredTxns.length) * 100 : 0}%` }}
              ></div>
            </div>
          </div>

          {/* Bar 3: High Risk (70-100) */}
          <div>
            <div className="flex justify-between text-xs mb-1">
              <span className="text-red-400 font-medium flex items-center gap-1">
                <ShieldAlert className="w-3.5 h-3.5" /> High Risk (70-100)
              </span>
              <span className="text-dark-400 font-semibold">{highRisk} txns</span>
            </div>
            <div className="w-full bg-dark-950 rounded-full h-2.5 overflow-hidden">
              <div 
                className="bg-red-500 h-2.5 rounded-full transition-all duration-300"
                style={{ width: `${scoredTxns.length > 0 ? (highRisk / scoredTxns.length) * 100 : 0}%` }}
              ></div>
            </div>
          </div>
        </div>
      </div>

    </div>
  );
};

export default AnalyticsCharts;
