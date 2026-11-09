import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, beforeEach } from 'vitest';
import App from '@/App';

describe('FinPath AI 2.0 — Upgrade Phase 05: Advanced Financial Document Intelligence Suite', () => {
  beforeEach(() => {
    localStorage.setItem('finpath_token', 'mock_vault_token_123');
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
    window.history.pushState({}, 'Document Vault', '/documents');
  });

  it('renders Document Vault with Phase 05 header, statutory badge, and KPI metrics', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('heading', { level: 1, name: /AI Financial Document Vault/i })).toBeInTheDocument();
      expect(screen.getByText(/Phase 05 Intelligence/i)).toBeInTheDocument();
    });

    expect(screen.getByRole('button', { name: /Document Vault/i })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Journey Readiness/i })).toBeInTheDocument();
    expect(screen.getByText(/Secure Financial Document Ingestion/i)).toBeInTheDocument();
    expect(screen.getByText(/Choose Document File/i)).toBeInTheDocument();
  });

  it('switches between Document Vault and Journey Readiness tabs cleanly', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Journey Readiness/i })).toBeInTheDocument();
    });

    const readinessTabBtn = screen.getByRole('button', { name: /Journey Readiness/i });
    fireEvent.click(readinessTabBtn);

    await waitFor(() => {
      expect(screen.getByText(/Application Journey Document Completeness/i)).toBeInTheDocument();
      expect(screen.getByText(/Required Document Checklist/i)).toBeInTheDocument();
      expect(screen.getByText(/Regulatory Compliance Notice/i)).toBeInTheDocument();
    });
  });
});
