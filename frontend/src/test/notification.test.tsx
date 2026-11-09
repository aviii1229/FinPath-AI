import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, beforeEach } from 'vitest';
import App from '@/App';

describe('FinPath AI 2.0 — Upgrade Phase 07: Proactive Financial Assistance & Notifications Suite', () => {
  beforeEach(() => {
    localStorage.setItem('finpath_token', 'mock_notif_token_123');
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
    window.history.pushState({}, 'Notifications', '/notifications');
  });

  it('renders Notification Center with Phase 07 header, privacy badge, and KPI metrics', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('heading', { level: 1, name: /Proactive Financial Notification Center/i })).toBeInTheDocument();
      expect(screen.getByText(/Phase 07 Proactive Intelligence/i)).toBeInTheDocument();
      expect(screen.getByText(/Privacy-Safe Notification Protocol/i)).toBeInTheDocument();
      expect(screen.getByText(/Unread Alerts/i)).toBeInTheDocument();
      expect(screen.getByText(/High & Critical Priority/i)).toBeInTheDocument();
      expect(screen.getByText(/Automated Rules Active/i)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /Run Proactive Scan/i })).toBeInTheDocument();
    });
  });

  it('displays proactive financial reminders with Indian Rupee amounts, categories, and channels', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Upcoming Loan EMI Due/i)).toBeInTheDocument();
      expect(screen.getByText(/Monthly Budget Threshold Alert/i)).toBeInTheDocument();
    });

    // Check Indian Rupee amounts
    expect(screen.getByText(/₹8,450/i)).toBeInTheDocument();
    expect(screen.getByText(/₹13,200/i)).toBeInTheDocument();

    // Check action buttons
    expect(screen.getByRole('button', { name: /Review Schedule/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /View Budget Breakdown/i })).toBeInTheDocument();
  });

  it('filters notifications by category and handles mark as read', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Upcoming Loan EMI Due/i)).toBeInTheDocument();
    });

    // Filter by Budgets category
    const budgetTab = screen.getByRole('button', { name: /Budgets/i });
    fireEvent.click(budgetTab);

    await waitFor(() => {
      expect(screen.getByText(/Monthly Budget Threshold Alert/i)).toBeInTheDocument();
    });

    // Test Mark all read button
    const markAllBtn = screen.getByRole('button', { name: /Mark all read/i });
    fireEvent.click(markAllBtn);
  });

  it('opens Preferences modal and allows configuring alert categories and quiet hours', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Preferences/i })).toBeInTheDocument();
    });

    const prefBtn = screen.getByRole('button', { name: /Preferences/i });
    fireEvent.click(prefBtn);

    await waitFor(() => {
      expect(screen.getByText(/Notification & Reminder Preferences/i)).toBeInTheDocument();
      expect(screen.getByText(/Communication Channels/i)).toBeInTheDocument();
      expect(screen.getByText(/Proactive Alert Categories/i)).toBeInTheDocument();
      expect(screen.getByText(/Quiet Hours & Privacy Guardrails/i)).toBeInTheDocument();
    });

    const saveBtn = screen.getByRole('button', { name: /Save Preferences/i });
    fireEvent.click(saveBtn);
  });
});

