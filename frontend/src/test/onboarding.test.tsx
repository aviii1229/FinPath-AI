import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, beforeEach } from 'vitest';
import App from '@/App';

describe('FinPath AI 2.0 — Smart Onboarding & Personalization Suite', () => {
  beforeEach(() => {
    window.history.pushState({}, 'Test', '/');
    localStorage.clear();
  });

  it('navigates to /onboarding and renders Step 1 (Basic Information)', async () => {
    window.history.pushState({}, 'Onboarding', '/onboarding');
    render(<App />);

    expect(screen.getByText(/Tell Us About Yourself/i)).toBeInTheDocument();
    expect(screen.getByText(/Step 1 of 4: Basic Information/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/First Name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Last Name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Age/i)).toBeInTheDocument();
    expect(screen.getByText(/City \/ Region/i)).toBeInTheDocument();
    expect(screen.getByText(/Employment Category/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Occupation \/ Field of Work/i)).toBeInTheDocument();
  });

  it('advances through Step 1 to Step 2 (Financial Overview) and calculates live surplus', async () => {
    window.history.pushState({}, 'Onboarding', '/onboarding');
    render(<App />);

    // Step 1: Input details
    const firstName = screen.getByLabelText(/First Name/i);
    fireEvent.change(firstName, { target: { value: 'Aarav' } });

    const continueBtn = screen.getByRole('button', { name: /Save & Continue to Financial Overview/i });
    fireEvent.click(continueBtn);

    // Step 2 should now be visible
    await waitFor(() => {
      expect(screen.getByText(/Your Financial Overview/i)).toBeInTheDocument();
      expect(screen.getByText(/Step 2 of 4: Financial Overview/i)).toBeInTheDocument();
    });

    expect(screen.getByLabelText(/Net Monthly In-Hand Income/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Monthly Living Expenses/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Existing Monthly Financial Obligations/i)).toBeInTheDocument();
    expect(screen.getByText(/Real-Time Mathematical Health Indicators/i)).toBeInTheDocument();
  });

  it('advances to Step 3 (Financial Goals) and permits multi-selection', async () => {
    window.history.pushState({}, 'Onboarding', '/onboarding');
    render(<App />);

    // Advance to Step 2
    fireEvent.click(screen.getByRole('button', { name: /Save & Continue to Financial Overview/i }));

    await waitFor(() => {
      expect(screen.getByText(/Your Financial Overview/i)).toBeInTheDocument();
    });

    // Advance to Step 3
    fireEvent.click(screen.getByRole('button', { name: /Continue to Financial Goals/i }));

    await waitFor(() => {
      expect(screen.getByText(/What Are Your Primary Financial Goals\?/i)).toBeInTheDocument();
      expect(screen.getByText(/Step 3 of 4: Financial Goals/i)).toBeInTheDocument();
    });

    // Goals options
    expect(screen.getByRole('heading', { name: /Health Insurance/i, level: 4 })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: /Life Insurance/i, level: 4 })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: /Personal Loan/i, level: 4 })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: /Education Loan/i, level: 4 })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: /Home Loan/i, level: 4 })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: /Monthly Budgeting/i, level: 4 })).toBeInTheDocument();

    // Click Home Loan to toggle
    const homeLoanCard = screen.getByRole('heading', { name: /Home Loan/i, level: 4 });
    fireEvent.click(homeLoanCard);
  });

  it('completes Step 4 (Preferences) and redirects to Dashboard', async () => {
    window.history.pushState({}, 'Onboarding', '/onboarding');
    render(<App />);

    // Step 1 -> 2
    fireEvent.click(screen.getByRole('button', { name: /Save & Continue to Financial Overview/i }));
    await waitFor(() => expect(screen.getByText(/Your Financial Overview/i)).toBeInTheDocument());

    // Step 2 -> 3
    fireEvent.click(screen.getByRole('button', { name: /Continue to Financial Goals/i }));
    await waitFor(() => expect(screen.getByText(/What Are Your Primary Financial Goals\?/i)).toBeInTheDocument());

    // Step 3 -> 4
    fireEvent.click(screen.getByRole('button', { name: /Save Goals & Continue/i }));
    await waitFor(() => {
      expect(screen.getByText(/Assistance & Communication Preferences/i)).toBeInTheDocument();
      expect(screen.getByText(/Step 4 of 4: Preferences/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/AI Co-Pilot Guidance/i)).toBeInTheDocument();
    expect(screen.getByText(/Self-Directed Exploration/i)).toBeInTheDocument();
    expect(screen.getByText(/Sequential Checklists/i)).toBeInTheDocument();

    // Complete Onboarding
    const completeBtn = screen.getByRole('button', { name: /Complete Onboarding & View Personalized Dashboard/i });
    fireEvent.click(completeBtn);

    await waitFor(() => {
      expect(screen.getByText(/Personalization completed!/i)).toBeInTheDocument();
    });
  });

  it('renders Personalized Dashboard with contextual recommendations and metrics', async () => {
    window.history.pushState({}, 'Dashboard', '/dashboard');
    render(<App />);

    expect(screen.getByText(/Welcome back/i)).toBeInTheDocument();
    expect(screen.getByText(/Financial Health Score/i)).toBeInTheDocument();
    expect(screen.getByText(/Total Net Worth/i)).toBeInTheDocument();
    expect(screen.getByText(/Monthly Surplus/i)).toBeInTheDocument();
    expect(screen.getByText(/Personalization Settings/i)).toBeInTheDocument();

    await waitFor(() => {
      expect(screen.getByText(/Recommended Financial Journeys/i)).toBeInTheDocument();
      expect(screen.getByText(/Debt-to-Income \(FOIR\)/i)).toBeInTheDocument();
    });
  });
});
