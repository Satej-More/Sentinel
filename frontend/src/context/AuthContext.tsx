import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';

interface User {
  username: string;
  role: string;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  loading: boolean;
  login: (username: string, password: string) => Promise<void>;
  register: (username: string, password: string, role?: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    // Restore session on initial render
    const savedToken = localStorage.getItem('token');
    const savedUser = localStorage.getItem('user');

    if (savedToken && savedUser) {
      try {
        setToken(savedToken);
        setUser(JSON.parse(savedUser));
      } catch (e) {
        // Clear corrupted storage
        localStorage.removeItem('token');
        localStorage.removeItem('user');
      }
    }
    setLoading(false);
  }, []);

  const login = async (username: string, password: string) => {
    try {
      const response = await api.post('/api/auth/login', { username, password });
      const { token: jwtToken, role, username: responseUsername } = response.data;
      
      const loggedUser = { username: responseUsername, role };
      
      localStorage.setItem('token', jwtToken);
      localStorage.setItem('user', JSON.stringify(loggedUser));
      
      setToken(jwtToken);
      setUser(loggedUser);
    } catch (error: any) {
      throw new Error(error.message || 'Login failed');
    }
  };

  const register = async (username: string, password: string, role: string = 'ADMIN') => {
    try {
      // Backend AuthController /register expects: username, password, role
      await api.post('/api/auth/register', { 
        username, 
        password,
        role: role
      });
    } catch (error: any) {
      throw new Error(error.message || 'Registration failed');
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
