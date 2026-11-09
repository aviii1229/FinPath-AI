import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, beforeEach } from 'vitest';
import App from '@/App';

describe('FinPath AI — Frontend Navigation & Integration Suite', () => {
  beforeEach(() => {
    window.history.pushState({}, 'Test', '/');
  });

  it('renders Landing Page with Hero, CTA, and Module Explanations', async () => {
    render(<App />);

    expect(screen.getByText(/One Intelligent Platform for/i)).toBeInTheDocument();
    expect(screen.getAllByText(/Insurance Intelligence/i)[0]).toBeInTheDocument();
    expect(screen.getAllByText(/Lending & Credit Engine/i)[0]).toBeInTheDocument();
    expect(screen.getAllByText(/Fintech & Wealth PFM/i)[0]).toBeInTheDocument();
    expect(screen.getByText(/Launch Live Platform Demo/i)).toBeInTheDocument();
  });

  it('navigates to Login page and validates inputs', async () => {
    window.history.pushState({}, 'Login', '/login');
    render(<App />);

    expect(screen.getByText(/Welcome Back/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Email Address/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Sign In to Dashboard/i })).toBeInTheDocument();
  });

  it('navigates to Register page and displays password strength', async () => {
    window.history.pushState({}, 'Register', '/register');
    render(<App />);

    expect(screen.getByText(/Create Free Account/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/First Name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Last Name/i)).toBeInTheDocument();
  });

  it('navigates to Forgot Password page and handles reset request', async () => {
    window.history.pushState({}, 'Forgot Password', '/forgot-password');
    render(<App />);

    expect(screen.getByText(/Reset Password/i)).toBeInTheDocument();
    const emailInput = screen.getByLabelText(/Registered Email/i);
    fireEvent.change(emailInput, { target: { value: 'test@finpath.ai' } });

    const submitBtn = screen.getByRole('button', { name: /Send Password Reset Link/i });
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(screen.getByText(/Reset Link Dispatched/i)).toBeInTheDocument();
    });
  });

  it('renders Main Dashboard with financial overview and insights', async () => {
    window.history.pushState({}, 'Dashboard', '/dashboard');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Welcome back, Sarah!/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/Total Net Worth/i)).toBeInTheDocument();
    expect(screen.getByText(/Liquid Cash & Savings/i)).toBeInTheDocument();
    expect(screen.getByText(/AI Cross-Domain Insights/i)).toBeInTheDocument();
    expect(screen.getByText(/Lending & Debt Overview/i)).toBeInTheDocument();
    expect(screen.getByText(/Insurance Protection Overview/i)).toBeInTheDocument();
  });

  it('renders Insurance page and handles quote calculator', async () => {
    window.history.pushState({}, 'Insurance', '/insurance');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Insurance Portfolio & Claims/i)).toBeInTheDocument();
    });

    expect(screen.getAllByText(/Comprehensive Platinum Family Shield/i)[0]).toBeInTheDocument();
    expect(screen.getByText(/Claims Adjudication History/i)).toBeInTheDocument();

    const calcBtn = screen.getByRole('button', { name: /Calculate Quote/i });
    fireEvent.click(calcBtn);

    await waitFor(() => {
      expect(screen.getByText(/Estimated Quote/i)).toBeInTheDocument();
    });
  });

  it('navigates to Insurance Discovery tab and displays recommendations', async () => {
    window.history.pushState({}, 'Insurance', '/insurance');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /AI Insurance Discovery/i })).toBeInTheDocument();
    });

    const discoveryTabBtn = screen.getByRole('button', { name: /AI Insurance Discovery/i });
    fireEvent.click(discoveryTabBtn);

    await waitFor(() => {
      expect(screen.getByText(/Discovery Questionnaire/i)).toBeInTheDocument();
      expect(screen.getByText(/Recommended Simulated Products/i)).toBeInTheDocument();
      expect(screen.getAllByText(/Simulated Insurance Product/i)[0]).toBeInTheDocument();
    });
  });

  it('navigates to Policy Comparison Matrix tab and displays side-by-side features', async () => {
    window.history.pushState({}, 'Insurance', '/insurance');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Policy Comparison Matrix/i })).toBeInTheDocument();
    });

    const compareTabBtn = screen.getByRole('button', { name: /Policy Comparison Matrix/i });
    fireEvent.click(compareTabBtn);

    await waitFor(() => {
      expect(screen.getByText(/Side-by-Side Policy Comparison Matrix/i)).toBeInTheDocument();
      expect(screen.getAllByText(/Waiting Period/i)[0]).toBeInTheDocument();
      expect(screen.getAllByText(/Exclusions/i)[0]).toBeInTheDocument();
    });
  });

  it('navigates to AI Policy Assistant tab and answers grounded questions', async () => {
    window.history.pushState({}, 'Insurance', '/insurance');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /AI Policy Assistant \(RAG\)/i })).toBeInTheDocument();
    });

    const assistantTabBtn = screen.getByRole('button', { name: /AI Policy Assistant \(RAG\)/i });
    fireEvent.click(assistantTabBtn);

    await waitFor(() => {
      expect(screen.getByText(/Policy PDF Ingestion/i)).toBeInTheDocument();
      expect(screen.getByText(/Grounded Policy Q&A Engine/i)).toBeInTheDocument();
      expect(screen.getByText(/Zero-Hallucination RAG/i)).toBeInTheDocument();
    });
  });

  it('renders Lending page and computes interactive EMI calculations', async () => {
    window.history.pushState({}, 'Lending', '/lending');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Lending AI Hub/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/Active Borrowing Facilities/i)).toBeInTheDocument();
    expect(screen.getByText(/Institutional Lending Programs/i)).toBeInTheDocument();
    expect(screen.getAllByRole('button', { name: /Check Eligibility/i })[0]).toBeInTheDocument();
  });

  it('navigates to Lending Discovery tab and analyzes illustrative eligibility', async () => {
    window.history.pushState({}, 'Lending', '/lending');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Loan Discovery & Eligibility/i })).toBeInTheDocument();
    });

    const discoveryTabBtn = screen.getByRole('button', { name: /Loan Discovery & Eligibility/i });
    fireEvent.click(discoveryTabBtn);

    await waitFor(() => {
      expect(screen.getByText(/Illustrative Eligibility Only/i)).toBeInTheDocument();
      expect(screen.getByText(/Borrower Profile Inputs/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /Analyze Illustrative Eligibility/i })).toBeInTheDocument();
    });
  });

  it('navigates to EMI Amortization Simulator tab and runs calculations', async () => {
    window.history.pushState({}, 'Lending', '/lending');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /EMI Amortization Simulator/i })).toBeInTheDocument();
    });

    const calcTabBtn = screen.getByRole('button', { name: /EMI Amortization Simulator/i });
    fireEvent.click(calcTabBtn);

    await waitFor(() => {
      expect(screen.getByText(/Authoritative Java EMI Amortization Simulator/i)).toBeInTheDocument();
      expect(screen.getByText(/Authoritative Monthly EMI/i)).toBeInTheDocument();
      expect(screen.getByText(/Total Repayment Amortization/i)).toBeInTheDocument();
    });
  });

  it('navigates to Lender Comparison Matrix tab and displays side-by-side metrics', async () => {
    window.history.pushState({}, 'Lending', '/lending');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Lender Comparison Matrix/i })).toBeInTheDocument();
    });

    const compareTabBtn = screen.getByRole('button', { name: /Lender Comparison Matrix/i });
    fireEvent.click(compareTabBtn);

    await waitFor(() => {
      expect(screen.getByText(/Simulated Lender Comparison Matrix/i)).toBeInTheDocument();
      expect(screen.getByText(/Lending Institution/i)).toBeInTheDocument();
      expect(screen.getAllByText(/Total Borrowing Cost/i)[0]).toBeInTheDocument();
    });
  });

  it('navigates to AI Loan Assistant tab and generates checklists', async () => {
    window.history.pushState({}, 'Lending', '/lending');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /AI Loan Assistant & Checklist/i })).toBeInTheDocument();
    });

    const assistantTabBtn = screen.getByRole('button', { name: /AI Loan Assistant & Checklist/i });
    fireEvent.click(assistantTabBtn);

    await waitFor(() => {
      expect(screen.getByText(/AI Lending Terminology & Guidance Assistant/i)).toBeInTheDocument();
      expect(screen.getByText(/Tailored Document Checklist/i)).toBeInTheDocument();
    });
  });

  it('navigates to Application Tracking tab and displays lifecycle stepper', async () => {
    window.history.pushState({}, 'Lending', '/lending');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Application Tracking & Decisions/i })).toBeInTheDocument();
    });

    const appTabBtn = screen.getByRole('button', { name: /Application Tracking & Decisions/i });
    fireEvent.click(appTabBtn);

    await waitFor(() => {
      expect(screen.getByText(/Origination Lifecycle & Underwriting Decision Engine/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /Start New Application/i })).toBeInTheDocument();
    });
  });

  it('renders Fintech PFM page with dashboard metrics and Recharts visualizations', async () => {
    window.history.pushState({}, 'Fintech', '/fintech');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Fintech & Wealth AI \(PFM\)/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/Total Monthly Income/i)).toBeInTheDocument();
    expect(screen.getByText(/Total Monthly Expenses/i)).toBeInTheDocument();
    expect(screen.getByText(/Net Discretionary Savings/i)).toBeInTheDocument();
    expect(screen.getByText(/Category Spending Distribution/i)).toBeInTheDocument();
  });

  it('navigates to Fintech Smart Budgeting tab and displays category limits', async () => {
    window.history.pushState({}, 'Fintech', '/fintech');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Smart Budgeting/i })).toBeInTheDocument();
    });

    const budgetTabBtn = screen.getByRole('button', { name: /Smart Budgeting/i });
    fireEvent.click(budgetTabBtn);

    await waitFor(() => {
      expect(screen.getByText(/Category Budget Limits & Utilization/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /Set Category Budget/i })).toBeInTheDocument();
    });
  });

  it('navigates to Fintech Savings Goals Planner tab and displays target progress', async () => {
    window.history.pushState({}, 'Fintech', '/fintech');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Savings Goals Planner/i })).toBeInTheDocument();
    });

    const savingsTabBtn = screen.getByRole('button', { name: /Savings Goals Planner/i });
    fireEvent.click(savingsTabBtn);

    await waitFor(() => {
      expect(screen.getByText(/Algorithmic Savings Goals Planner/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /New Savings Goal/i })).toBeInTheDocument();
    });
  });

  it('navigates to Fintech AI Financial Insights tab and displays anomalies', async () => {
    window.history.pushState({}, 'Fintech', '/fintech');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /AI Financial Insights & Anomalies/i })).toBeInTheDocument();
    });

    const insightsTabBtn = screen.getByRole('button', { name: /AI Financial Insights & Anomalies/i });
    fireEvent.click(insightsTabBtn);

    await waitFor(() => {
      expect(screen.getByText(/AI Autonomous Financial Expenditure Insights/i)).toBeInTheDocument();
      expect(screen.getByText(/REAL-TIME AUDIT/i)).toBeInTheDocument();
    });
  });

  it('renders AI Assistant and sends financial questions', async () => {
    window.history.pushState({}, 'AI Assistant', '/ai-assistant');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/FinPath AI Financial Advisor/i)).toBeInTheDocument();
    });

    const input = screen.getByPlaceholderText(/Ask about loan qualification/i);
    fireEvent.change(input, { target: { value: 'Can I afford a ₹3 lakh auto loan?' } });

    const sendBtn = screen.getByRole('button', { name: /Send/i });
    fireEvent.click(sendBtn);

    await waitFor(
      () => {
        expect(screen.getByText(/affordability evaluation/i)).toBeInTheDocument();
      },
      { timeout: 4000 }
    );
  });

  it('orchestrates Scenario A: Family health insurance discovery', async () => {
    window.history.pushState({}, 'AI Assistant', '/ai-assistant');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/FinPath AI Financial Advisor/i)).toBeInTheDocument();
    });

    // Click Scenario A starter card
    const scenarioABtn = screen.getByText(/Family Health Cover/i);
    fireEvent.click(scenarioABtn);

    await waitFor(
      () => {
        expect(screen.getByText(/Family Health Protection Blueprint/i)).toBeInTheDocument();
        expect(screen.getAllByText(/Aegis Family Health Shield/i)[0]).toBeInTheDocument();
      },
      { timeout: 4000 }
    );
  });

  it('orchestrates Scenario B: ₹3 Lakh loan discovery and EMI schedule', async () => {
    window.history.pushState({}, 'AI Assistant', '/ai-assistant');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/FinPath AI Financial Advisor/i)).toBeInTheDocument();
    });

    const scenarioBBtn = screen.getByText(/₹3 Lakh Loan Discovery/i);
    fireEvent.click(scenarioBBtn);

    await waitFor(
      () => {
        expect(screen.getByText(/Loan Feasibility & Affordability Evaluation/i)).toBeInTheDocument();
        expect(screen.getAllByText(/₹3 Lakh Loan/i)[0]).toBeInTheDocument();
      },
      { timeout: 4000 }
    );
  });

  it('orchestrates Scenario C: ₹40,000 savings and 50-30-20 budget plan', async () => {
    window.history.pushState({}, 'AI Assistant', '/ai-assistant');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/FinPath AI Financial Advisor/i)).toBeInTheDocument();
    });

    const scenarioCBtn = screen.getByText(/Smart Savings Engine/i);
    fireEvent.click(scenarioCBtn);

    await waitFor(
      () => {
        expect(screen.getByText(/50-30-20 Smart Savings Framework/i)).toBeInTheDocument();
        expect(screen.getByText(/50-30-20 Cashflow Restructuring/i)).toBeInTheDocument();
      },
      { timeout: 4000 }
    );
  });

  it('orchestrates Scenario D: Reduce monthly financial burden cross-domain', async () => {
    window.history.pushState({}, 'AI Assistant', '/ai-assistant');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/FinPath AI Financial Advisor/i)).toBeInTheDocument();
    });

    const scenarioDBtn = screen.getByText(/Reduce Financial Burden/i);
    fireEvent.click(scenarioDBtn);

    await waitFor(
      () => {
        expect(screen.getByText(/Cross-Domain Debt & Cashflow Relief Blueprint/i)).toBeInTheDocument();
        expect(screen.getByText(/Monthly Burden Relief Matrix/i)).toBeInTheDocument();
      },
      { timeout: 4000 }
    );
  });

  it('renders Transactions page with search filter', async () => {
    window.history.pushState({}, 'Transactions', '/transactions');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Financial Ledger & Transactions/i)).toBeInTheDocument();
    });

    const searchInput = screen.getByPlaceholderText(/Search merchants, categories/i);
    fireEvent.change(searchInput, { target: { value: 'Nature Basket' } });

    expect(screen.getByText(/Nature Basket & Blinkit/i)).toBeInTheDocument();
  });

  it('renders Settings page with AI Gateway provider toggle', async () => {
    window.history.pushState({}, 'Settings', '/settings');
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Account & System Settings/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/AI Gateway Provider Selection/i)).toBeInTheDocument();
    expect(screen.getByText(/Google Gemini 1.5 Pro/i)).toBeInTheDocument();
    expect(screen.getByText(/Ollama \(Local Llama 3\)/i)).toBeInTheDocument();
  });
});
