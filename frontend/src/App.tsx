import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from '@/core/auth/AuthContext';
import { DashboardLayout } from '@/core/layout/DashboardLayout';
import { LandingPage } from '@/features/landing/LandingPage';
import { LoginPage } from '@/features/auth/LoginPage';
import { RegisterPage } from '@/features/auth/RegisterPage';
import { ForgotPasswordPage } from '@/features/auth/ForgotPasswordPage';
import { DashboardPage } from '@/features/dashboard/DashboardPage';
import { InsurancePage } from '@/features/insurance/InsurancePage';
import { LendingPage } from '@/features/lending/LendingPage';
import { FintechPage } from '@/features/fintech/FintechPage';
import { AiAssistantPage } from '@/features/ai-assistant/AiAssistantPage';
import { TransactionsPage } from '@/features/transactions/TransactionsPage';
import { SettingsPage } from '@/features/settings/SettingsPage';
import { OnboardingWizardPage } from '@/features/onboarding/OnboardingWizardPage';
import { DocumentVaultPage } from '@/features/documents/DocumentVaultPage';
import { JourneyDashboardPage } from '@/features/journeys/JourneyDashboardPage';
import { LoadingSpinner } from '@/shared/components/ui/LoadingSpinner';

// Route Guard component
const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen bg-navy-950 flex items-center justify-center">
        <LoadingSpinner size="lg" text="Authenticating FinPath Session..." />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};

export const App: React.FC = () => {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />

          {/* Standalone Smart Onboarding Wizard Route */}
          <Route
            path="/onboarding"
            element={
              <ProtectedRoute>
                <OnboardingWizardPage />
              </ProtectedRoute>
            }
          />

          {/* Protected Financial Journey Application Routes */}
          <Route
            element={
              <ProtectedRoute>
                <DashboardLayout />
              </ProtectedRoute>
            }
          >
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/insurance" element={<InsurancePage />} />
            <Route path="/lending" element={<LendingPage />} />
            <Route path="/fintech" element={<FintechPage />} />
            <Route path="/ai-assistant" element={<AiAssistantPage />} />
            <Route path="/journeys" element={<JourneyDashboardPage />} />
            <Route path="/documents" element={<DocumentVaultPage />} />
            <Route path="/transactions" element={<TransactionsPage />} />
            <Route path="/settings" element={<SettingsPage />} />
          </Route>

          {/* Catch-all Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
