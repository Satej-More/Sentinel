import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { X, Send, AlertCircle, CheckCircle2, Loader2, Sparkles } from 'lucide-react';
import { useAuth } from '../context/AuthContext';

interface SubmitTransactionProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

const SubmitTransaction: React.FC<SubmitTransactionProps> = ({ isOpen, onClose, onSuccess }) => {
  const { user } = useAuth();
  const [externalTransactionId, setExternalTransactionId] = useState('');
  const [userId, setUserId] = useState('');
  const [amount, setAmount] = useState('');
  const [currency, setCurrency] = useState('USD');
  const [merchantId, setMerchantId] = useState('');
  const [ipAddress, setIpAddress] = useState('');
  const [deviceId, setDeviceId] = useState('');
  
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  // Generate a random ID and set analyst on open
  useEffect(() => {
    if (isOpen) {
      const uniqueId = `TXN-${Date.now()}-${Math.floor(1000 + Math.random() * 9000)}`;
      setExternalTransactionId(uniqueId);
      setUserId(user?.username || 'analyst-1');
      setAmount('');
      setMerchantId('merchant-' + Math.floor(100 + Math.random() * 900));
      
      // Auto-populate random IP/Device for quick testing convenience
      const testIps = ['192.168.1.1', '10.0.0.1', '192.0.2.99', '172.16.254.1', '8.8.8.8'];
      setIpAddress(testIps[Math.floor(Math.random() * testIps.length)]);
      
      const testDevices = ['dev-chrome-windows', 'dev-safari-ios', 'dev-firefox-macos', 'dev-android-app'];
      setDeviceId(testDevices[Math.floor(Math.random() * testDevices.length)]);
      
      setError(null);
      setSuccess(null);
    }
  }, [isOpen, user]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    // Validate inputs
    const parsedAmount = parseFloat(amount);
    if (isNaN(parsedAmount) || parsedAmount <= 0) {
      setError('Amount must be a valid number greater than 0.');
      return;
    }

    if (!externalTransactionId.trim() || !userId.trim() || !merchantId.trim()) {
      setError('Transaction ID, User ID, and Merchant ID are required.');
      return;
    }

    if (!/^[A-Za-z]{3}$/.test(currency)) {
      setError('Currency must be a 3-letter ISO code.');
      return;
    }

    setSubmitting(true);

    try {
      await api.post('/transactions', {
        externalTransactionId: externalTransactionId.trim(),
        userId: userId.trim(),
        amount: parsedAmount,
        currency: currency.trim().toUpperCase(),
        merchantId: merchantId.trim(),
        ipAddress: ipAddress.trim() || null,
        deviceId: deviceId.trim() || null
      });

      setSuccess('Transaction submitted successfully!');
      setTimeout(() => {
        onSuccess();
        onClose();
      }, 1000);
    } catch (err: any) {
      setError(err.message || 'Failed to submit transaction.');
    } finally {
      setSubmitting(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex justify-end bg-black/60 backdrop-blur-sm">
      {/* Click outside to close */}
      <div className="absolute inset-0 -z-10" onClick={onClose}></div>

      {/* Drawer Body */}
      <div className="w-full max-w-lg bg-dark-900 border-l border-dark-800 text-white h-full flex flex-col p-6 shadow-2xl relative animate-slide-in">
        
        {/* Header */}
        <div className="flex items-center justify-between border-b border-dark-800 pb-4 mb-6">
          <div>
            <h3 className="text-lg font-bold flex items-center gap-2">
              <Sparkles className="w-5 h-5 text-emerald-400" />
              Submit Live Transaction
            </h3>
            <p className="text-xs text-dark-400 mt-1">Inject test transaction data into the detection pipeline</p>
          </div>
          <button onClick={onClose} className="p-2 hover:bg-dark-800 rounded-lg text-dark-400 hover:text-white transition-colors">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Error / Success Notifications */}
        {error && (
          <div className="mb-6 p-4 bg-red-500/10 border border-red-500/20 rounded-xl flex items-start gap-3 text-red-400 text-sm">
            <AlertCircle className="w-5 h-5 shrink-0 mt-0.5" />
            <span>{error}</span>
          </div>
        )}

        {success && (
          <div className="mb-6 p-4 bg-emerald-500/10 border border-emerald-500/20 rounded-xl flex items-start gap-3 text-emerald-400 text-sm">
            <CheckCircle2 className="w-5 h-5 shrink-0 mt-0.5" />
            <span>{success}</span>
          </div>
        )}

        {/* Form */}
        <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto space-y-5 pr-1">
          <div>
            <label className="block text-dark-300 text-xs font-semibold uppercase tracking-wider mb-2">
              External Transaction ID
            </label>
            <input
              type="text"
              value={externalTransactionId}
              onChange={(e) => setExternalTransactionId(e.target.value)}
              className="w-full bg-dark-950 border border-dark-800 rounded-xl py-2.5 px-4 text-white placeholder-dark-500 focus:outline-none focus:border-emerald-500/50"
              placeholder="e.g. TXN-12345"
              required
              disabled={submitting || !!success}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-dark-300 text-xs font-semibold uppercase tracking-wider mb-2">
                Merchant ID
              </label>
              <input
                type="text"
                value={merchantId}
                onChange={(e) => setMerchantId(e.target.value)}
                className="w-full bg-dark-950 border border-dark-800 rounded-xl py-2.5 px-4 text-white placeholder-dark-500 focus:outline-none focus:border-emerald-500/50"
                placeholder="e.g. merchant-77"
                required
                disabled={submitting || !!success}
              />
            </div>
            <div>
              <label className="block text-dark-300 text-xs font-semibold uppercase tracking-wider mb-2">
                User / Account ID
              </label>
              <input
                type="text"
                value={userId}
                onChange={(e) => setUserId(e.target.value)}
                className="w-full bg-dark-950 border border-dark-800 rounded-xl py-2.5 px-4 text-white placeholder-dark-500 focus:outline-none focus:border-emerald-500/50"
                placeholder="e.g. user-99"
                required
                disabled={submitting || !!success}
              />
            </div>
          </div>

          <div className="grid grid-cols-3 gap-4">
            <div className="col-span-2">
              <label className="block text-dark-300 text-xs font-semibold uppercase tracking-wider mb-2">
                Amount
              </label>
              <input
                type="number"
                step="0.01"
                min="0.01"
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                className="w-full bg-dark-950 border border-dark-800 rounded-xl py-2.5 px-4 text-white placeholder-dark-500 focus:outline-none focus:border-emerald-500/50"
                placeholder="0.00"
                required
                disabled={submitting || !!success}
              />
            </div>
            <div>
              <label className="block text-dark-300 text-xs font-semibold uppercase tracking-wider mb-2">
                Currency
              </label>
              <input
                type="text"
                maxLength={3}
                value={currency}
                onChange={(e) => setCurrency(e.target.value)}
                className="w-full bg-dark-950 border border-dark-800 rounded-xl py-2.5 px-4 text-white placeholder-dark-500 focus:outline-none focus:border-emerald-500/50 text-center uppercase"
                placeholder="USD"
                required
                disabled={submitting || !!success}
              />
            </div>
          </div>

          <div>
            <label className="block text-dark-300 text-xs font-semibold uppercase tracking-wider mb-2">
              IP Address (Optional)
            </label>
            <input
              type="text"
              value={ipAddress}
              onChange={(e) => setIpAddress(e.target.value)}
              className="w-full bg-dark-950 border border-dark-800 rounded-xl py-2.5 px-4 text-white placeholder-dark-500 focus:outline-none focus:border-emerald-500/50"
              placeholder="e.g. 192.0.2.99"
              disabled={submitting || !!success}
            />
            <p className="text-[10px] text-dark-400 mt-1">Note: IP 192.0.2.99 and 10.0.0.1 trigger suspicious IP alerts.</p>
          </div>

          <div>
            <label className="block text-dark-300 text-xs font-semibold uppercase tracking-wider mb-2">
              Device ID (Optional)
            </label>
            <input
              type="text"
              value={deviceId}
              onChange={(e) => setDeviceId(e.target.value)}
              className="w-full bg-dark-950 border border-dark-800 rounded-xl py-2.5 px-4 text-white placeholder-dark-500 focus:outline-none focus:border-emerald-500/50"
              placeholder="e.g. browser-fingerprint-abc"
              disabled={submitting || !!success}
            />
          </div>
        </form>

        {/* Footer actions */}
        <div className="border-t border-dark-800 pt-4 mt-6 flex items-center justify-end gap-3">
          <button
            type="button"
            onClick={onClose}
            className="px-5 py-2.5 bg-dark-800 hover:bg-dark-750 text-white rounded-xl text-sm font-medium transition-colors"
            disabled={submitting}
          >
            Cancel
          </button>
          <button
            onClick={handleSubmit}
            className="px-5 py-2.5 bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white font-medium rounded-xl text-sm transition-all flex items-center gap-2 active:scale-[0.98] disabled:opacity-50"
            disabled={submitting || !!success}
          >
            {submitting ? (
              <>
                <Loader2 className="w-4 h-4 animate-spin" />
                <span>Injecting...</span>
              </>
            ) : (
              <>
                <Send className="w-4 h-4" />
                <span>Submit Transaction</span>
              </>
            )}
          </button>
        </div>
      </div>
    </div>
  );
};

export default SubmitTransaction;
