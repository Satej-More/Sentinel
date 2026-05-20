import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Shield, Lock, User, AlertCircle, CheckCircle2, Loader2 } from 'lucide-react';

const Register: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [role, setRole] = useState('ADMIN');
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (!username.trim() || !password || !confirmPassword) {
      setError('Please fill in all fields.');
      return;
    }

    if (username.trim().length < 3) {
      setError('Username must be at least 3 characters.');
      return;
    }

    if (password.length < 6) {
      setError('Password must be at least 6 characters.');
      return;
    }

    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    setSubmitting(true);

    try {
      await register(username.trim(), password, role);
      setSuccess('Account registered successfully! Redirecting to login...');
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (err: any) {
      setError(err.message || 'Registration failed. The username may already exist.');
      setSubmitting(false);
    }
  };

  return (
    <div className="min-h-screen bg-dark-950 flex items-center justify-center p-4 relative overflow-hidden">
      {/* Subtle glowing ambient lights for premium style */}
      <div className="absolute top-1/4 left-1/4 w-80 h-80 bg-emerald-500/10 rounded-full blur-3xl -z-10"></div>
      <div className="absolute bottom-1/4 right-1/4 w-80 h-80 bg-blue-500/10 rounded-full blur-3xl -z-10"></div>

      <div className="w-full max-w-md glass rounded-2xl p-8 shadow-2xl relative">
        <div className="flex flex-col items-center mb-8">
          <div className="p-3 bg-emerald-500/10 border border-emerald-500/20 rounded-xl mb-4 text-emerald-500 shadow-lg shadow-emerald-500/10">
            <Shield className="w-8 h-8" />
          </div>
          <h2 className="text-2xl font-bold text-white tracking-wide">SENTINEL Console</h2>
          <p className="text-dark-400 text-sm mt-1">Register an analyst operator workspace on SENTINEL</p>
        </div>

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

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label className="block text-dark-300 text-xs font-semibold uppercase tracking-wider mb-2" htmlFor="username">
              Username
            </label>
            <div className="relative">
              <span className="absolute inset-y-0 left-0 pl-3.5 flex items-center text-dark-400">
                <User className="w-5 h-5" />
              </span>
              <input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full bg-dark-900 border border-dark-800 rounded-xl py-3 pl-11 pr-4 text-white placeholder-dark-500 focus:outline-none focus:border-emerald-500/50 transition-colors"
                placeholder="Choose username"
                disabled={submitting || !!success}
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-dark-300 text-xs font-semibold uppercase tracking-wider mb-2" htmlFor="password">
              Password
            </label>
            <div className="relative">
              <span className="absolute inset-y-0 left-0 pl-3.5 flex items-center text-dark-400">
                <Lock className="w-5 h-5" />
              </span>
              <input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full bg-dark-900 border border-dark-800 rounded-xl py-3 pl-11 pr-4 text-white placeholder-dark-500 focus:outline-none focus:border-emerald-500/50 transition-colors"
                placeholder="Choose secure password"
                disabled={submitting || !!success}
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-dark-300 text-xs font-semibold uppercase tracking-wider mb-2" htmlFor="confirmPassword">
              Confirm Password
            </label>
            <div className="relative">
              <span className="absolute inset-y-0 left-0 pl-3.5 flex items-center text-dark-400">
                <Lock className="w-5 h-5" />
              </span>
              <input
                id="confirmPassword"
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="w-full bg-dark-900 border border-dark-800 rounded-xl py-3 pl-11 pr-4 text-white placeholder-dark-500 focus:outline-none focus:border-emerald-500/50 transition-colors"
                placeholder="Confirm password"
                disabled={submitting || !!success}
                required
              />
            </div>
          </div>

          <div>
            <label className="block text-dark-300 text-xs font-semibold uppercase tracking-wider mb-2" htmlFor="role">
              Console Work Role
            </label>
            <select
              id="role"
              value={role}
              onChange={(e) => setRole(e.target.value)}
              className="w-full bg-dark-900 border border-dark-800 rounded-xl py-3 px-4 text-white focus:outline-none focus:border-emerald-500/50 transition-colors cursor-pointer"
              disabled={submitting || !!success}
            >
              <option value="ADMIN" className="bg-dark-950 text-white">ADMIN (Lead Sandbox Operator)</option>
              <option value="FRAUD_ANALYST" className="bg-dark-950 text-white">FRAUD_ANALYST (Risk Auditor)</option>
              <option value="USER" className="bg-dark-950 text-white">USER (Merchant Ingestion Portal)</option>
            </select>
          </div>

          <button
            type="submit"
            className="w-full bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white font-medium py-3 px-4 rounded-xl shadow-lg shadow-emerald-950/20 active:scale-[0.98] transition-transform duration-100 flex items-center justify-center gap-2 mt-8 disabled:opacity-50"
            disabled={submitting || !!success}
          >
            {submitting ? (
              <>
                <Loader2 className="w-5 h-5 animate-spin" />
                <span>Registering...</span>
              </>
            ) : (
              <span>Create Account</span>
            )}
          </button>
        </form>

        <p className="mt-8 text-center text-sm text-dark-400">
          Already registered?{' '}
          <Link to="/login" className="text-emerald-400 hover:text-emerald-300 font-medium hover:underline">
            Sign In here
          </Link>
        </p>
      </div>
    </div>
  );
};

export default Register;
