import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Attach JWT Token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response Interceptor: Global Error Handling & Unauthorized Redirection
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Standard error details extract
    const message = 
      error.response?.data?.message || 
      (typeof error.response?.data === 'string' ? error.response.data : null) || 
      error.message || 
      'An unexpected error occurred';
    
    if (error.response?.status === 401) {
      // Clear auth tokens and trigger redirect/reload
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      if (!window.location.pathname.includes('/login') && !window.location.pathname.includes('/register')) {
        window.location.href = '/login?expired=true';
      }
    }
    
    return Promise.reject({
      ...error,
      message,
      status: error.response?.status,
    });
  }
);

export default api;
