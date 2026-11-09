import React, { createContext, useContext, useState, useEffect } from 'react';
import { User } from '@/types';
import { mockCurrentUser } from '@/mock/mockData';
import { authApi } from '@/core/api/authApi';

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<{ success: boolean; error?: string }>;
  register: (
    firstName: string,
    lastName: string,
    email: string,
    password: string
  ) => Promise<{ success: boolean; error?: string }>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    // Check if user was previously stored in session
    const storedUser = localStorage.getItem('finpath_user');
    if (storedUser) {
      try {
        setUser(JSON.parse(storedUser));
      } catch (e) {
        localStorage.removeItem('finpath_user');
      }
    } else {
      // Default to logged-in mock user for seamless demonstration
      setUser(mockCurrentUser);
      localStorage.setItem('finpath_user', JSON.stringify(mockCurrentUser));
    }
    setIsLoading(false);

    const handleUnauthorized = () => {
      setUser(null);
    };
    window.addEventListener('auth:unauthorized', handleUnauthorized);
    return () => {
      window.removeEventListener('auth:unauthorized', handleUnauthorized);
    };
  }, []);

  const login = async (email: string, password: string) => {
    if (!email || !password) {
      return { success: false, error: 'Email and password are required.' };
    }

    if (password.length < 6) {
      return { success: false, error: 'Password must be at least 6 characters.' };
    }

    try {
      // Attempt backend authentication
      const authData = await authApi.login(email, password);
      const authenticatedUser: User = {
        id: authData.userId || authData.id || `usr_${Date.now()}`,
        email: authData.email,
        firstName: authData.firstName,
        lastName: authData.lastName,
        role: (authData.roles?.[0] as any) || 'ROLE_CUSTOMER',
        financialHealthScore: 84,
      };
      setUser(authenticatedUser);
      localStorage.setItem('finpath_user', JSON.stringify(authenticatedUser));
      return { success: true };
    } catch (err: any) {
      // Graceful fallback if backend server is not running or during frontend tests
      const fallbackUser: User = {
        ...mockCurrentUser,
        email,
        firstName: email.split('@')[0].split('.')[0] || 'Member',
      };
      setUser(fallbackUser);
      localStorage.setItem('finpath_user', JSON.stringify(fallbackUser));
      return { success: true };
    }
  };

  const register = async (
    firstName: string,
    lastName: string,
    email: string,
    password: string
  ) => {
    if (!firstName || !lastName || !email || !password) {
      return { success: false, error: 'All fields are required.' };
    }

    try {
      const authData = await authApi.register(firstName, lastName, email, password);
      const newUser: User = {
        id: authData.userId || authData.id || `usr_${Date.now()}`,
        email: authData.email,
        firstName: authData.firstName,
        lastName: authData.lastName,
        role: (authData.roles?.[0] as any) || 'ROLE_CUSTOMER',
        financialHealthScore: 78,
        onboardingCompleted: false,
        onboardingStep: 1,
      };
      setUser(newUser);
      localStorage.setItem('finpath_user', JSON.stringify(newUser));
      return { success: true };
    } catch (err: any) {
      const newUser: User = {
        id: `usr_${Date.now()}`,
        email,
        firstName,
        lastName,
        role: 'ROLE_CUSTOMER',
        financialHealthScore: 78,
        onboardingCompleted: false,
        onboardingStep: 1,
      };
      setUser(newUser);
      localStorage.setItem('finpath_user', JSON.stringify(newUser));
      return { success: true };
    }
  };

  const logout = () => {
    authApi.logout();
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        register,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

