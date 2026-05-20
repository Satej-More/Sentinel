import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface ProtectedRouteProps {
  children: React.ReactElement;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen bg-dark-950 flex flex-col items-center justify-center">
        <div className="relative w-12 h-12">
          <div className="absolute inset-0 rounded-full border-4 border-dark-800"></div>
          <div className="absolute inset-0 rounded-full border-4 border-emerald-500 border-t-transparent animate-spin"></div>
        </div>
        <p className="mt-4 text-dark-400 text-sm font-medium tracking-wide">Securing workspace...</p>
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default ProtectedRoute;
