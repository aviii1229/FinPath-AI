import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, beforeEach } from 'vitest';
import App from '@/App';

describe('FinPath AI 2.0 — Upgrade Phase 06: Intelligent Financial Journey Management Suite', () => {
  beforeEach(() => {
    localStorage.setItem('finpath_token', 'mock_journey_token_123');
    localStorage.setItem(
      'finpath_user',
      JSON.stringify({
        id: '11111111-1111-1111-1111-111111111111',
        email: 'sarah.connor@finpath.ai',
        firstName: 'Sarah',
        lastName: 'Connor',
        role: 'USER',
      })
    );
    window.history.pushState({}, 'Customer Journeys', '/journeys');
  });

  it('renders Journey Dashboard with Phase 06 header, human approval badge, and KPI metrics', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('heading', { level: 1, name: /Customer Financial Journey Management/i })).toBeInTheDocument();
      expect(screen.getByText(/Phase 06 Automation/i)).toBeInTheDocument();
      expect(screen.getByText(/Human Approval Enforced/i)).toBeInTheDocument();
      expect(screen.getByText(/Active Workflows/i)).toBeInTheDocument();
      expect(screen.getByText(/Completed Journeys/i)).toBeInTheDocument();
      expect(screen.getByText(/Average Progress/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /Launch New Journey/i })).toBeInTheDocument();
    });
  });

  it('displays active journey cards with progress bars and next recommended actions', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Comprehensive Insurance Discovery & Application/i)).toBeInTheDocument();
      expect(screen.getByText(/Retail Loan Discovery, Sanction & Tracking/i)).toBeInTheDocument();
    });

    expect(screen.getAllByText(/Simulation Mode/i)[0]).toBeInTheDocument();
    expect(screen.getByText(/Complete Step 3: Multi-Insurer Policy Matrix Comparison/i)).toBeInTheDocument();
  });

  it('opens step progression wizard and displays workflow stages and AI guidance', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Comprehensive Insurance Discovery & Application/i)).toBeInTheDocument();
    });

    const manageBtn = screen.getAllByRole('button', { name: /Manage/i })[0];
    fireEvent.click(manageBtn);

    await waitFor(() => {
      expect(screen.getByText(/Workflow Stages/i)).toBeInTheDocument();
      expect(screen.getByText(/FinPath AI Assistant Guidance:/i)).toBeInTheDocument();
      expect(screen.getByText(/Submitted Step Information & Parameters/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /Save Draft/i })).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /Confirm & Complete Step/i })).toBeInTheDocument();
    });
  });

  it('opens Launch New Journey dialog and displays available workflow templates', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Launch New Journey/i })).toBeInTheDocument();
    });

    const launchBtn = screen.getByRole('button', { name: /Launch New Journey/i });
    fireEvent.click(launchBtn);

    await waitFor(() => {
      expect(screen.getByText(/Start New Financial Journey/i)).toBeInTheDocument();
      expect(screen.getByText(/Wealth Discipline & Financial Health Journey/i)).toBeInTheDocument();
    });
  });
});
