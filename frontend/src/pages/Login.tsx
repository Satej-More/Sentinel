import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Shield, Lock, User, AlertCircle, Loader2 } from 'lucide-react';

const Login: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [searchParams] = useSearchParams();
  const { login } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (searchParams.get('expired') === 'true') {
      setError('Your session has expired. Please log in again.');
    }
  }, [searchParams]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !password) {
      setError('Please fill in all fields.');
      return;
    }

    setError(null);
    setSubmitting(true);

    try {
      await login(username.trim(), password);
      navigate('/');
    } catch (err: any) {
      setError(err.message || 'Invalid username or password. Please try again.');
    } finally {
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
          <h2 className="text-2xl font-bold text-white tracking-wide">SENTINEL</h2>
          <p className="text-dark-400 text-sm mt-1">Distributed Fraud Detection Platform</p>
        </div>

        {error && (
          <div className="mb-6 p-4 bg-red-500/10 border border-red-500/20 rounded-xl flex items-start gap-3 text-red-400 text-sm animate-fade-in">
            <AlertCircle className="w-5 h-5 shrink-0 mt-0.5" />
            <span>{error}</span>
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
                placeholder="Enter username"
                disabled={submitting}
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
                placeholder="Enter password"
                disabled={submitting}
                required
              />
            </div>
          </div>

          <button
            type="submit"
            className="w-full bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white font-medium py-3 px-4 rounded-xl shadow-lg shadow-emerald-950/20 active:scale-[0.98] transition-transform duration-100 flex items-center justify-center gap-2 mt-8 disabled:opacity-50"
            disabled={submitting}
          >
            {submitting ? (
              <>
                <Loader2 className="w-5 h-5 animate-spin" />
                <span>Authenticating...</span>
              </>
            ) : (
              <span>Sign In</span>
            )}
          </button>
        </form>

        <p className="mt-8 text-center text-sm text-dark-400">
          New system user?{' '}
          <Link to="/register" className="text-emerald-400 hover:text-emerald-300 font-medium hover:underline">
            Register console account
          </Link>
        </p>
      </div>
    </div>
  );
};

export default Login;
