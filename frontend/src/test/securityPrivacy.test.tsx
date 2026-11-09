import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, beforeEach } from 'vitest';
import App from '@/App';

describe('FinPath AI 2.0 — Upgrade Phase 08: Advanced Security, Privacy & Responsible AI Suite', () => {
  beforeEach(() => {
    localStorage.setItem('finpath_token', 'mock_privacy_token_123');
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
    window.history.pushState({}, 'Privacy & Consent Center', '/privacy');
  });

  it('renders Privacy & Consent Center with DPDP Act 2023 compliance badge and Paytm-Hyderabad Edition badge', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByRole('heading', { level: 1, name: /Privacy & Consent Center/i })).toBeInTheDocument();
      expect(screen.getByText(/DPDP Act 2023 Aligned/i)).toBeInTheDocument();
      expect(screen.getByText(/Paytm-Hyderabad Edition/i)).toBeInTheDocument();
      expect(screen.getByText(/Consent Management/i)).toBeInTheDocument();
      expect(screen.getByText(/Data Fiduciary Notice/i)).toBeInTheDocument();
      expect(screen.getByText(/Your Rights as Data Principal/i)).toBeInTheDocument();
    });
  });

  it('displays granular consent switches including AI Processing, OCR Vault, and Conversation History', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Essential Data Collection/i)).toBeInTheDocument();
      expect(screen.getByText(/AI Financial Intelligence & Recommendations/i)).toBeInTheDocument();
      expect(screen.getByText(/Document Vault & OCR Indexing/i)).toBeInTheDocument();
      expect(screen.getByText(/AI Conversation History Preservation/i)).toBeInTheDocument();
      expect(screen.getByText(/Partner Ecosystem Sharing/i)).toBeInTheDocument();
    });

    // Check save button
    expect(screen.getByRole('button', { name: /Save Privacy Preferences/i })).toBeInTheDocument();
    // Check export button
    expect(screen.getByRole('button', { name: /Export Data \(JSON\)/i })).toBeInTheDocument();
  });

  it('displays statutory DPDP Act 2023 data principal rights and data categories', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Right to Access information about personal data \(Sec\. 11\)/i)).toBeInTheDocument();
      expect(screen.getByText(/Right to Correction and Erasure of personal data \(Sec\. 12\)/i)).toBeInTheDocument();
      expect(screen.getByText(/Right of Grievance Redressal with FinPath Data Fiduciary \(Sec\. 13\)/i)).toBeInTheDocument();
      expect(screen.getByText(/Right to Nominate beneficiary in event of incapacitation \(Sec\. 14\)/i)).toBeInTheDocument();
      expect(screen.getByText(/grievance-officer@finpath\.ai/i)).toBeInTheDocument();
    });
  });

  it('opens permanent data erasure modal with confirmation challenge', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Data Erasure & Account Deletion/i)).toBeInTheDocument();
    });

    const eraseBtn = screen.getByRole('button', { name: /Request Permanent Account Erasure/i });
    fireEvent.click(eraseBtn);

    await waitFor(() => {
      expect(screen.getByText(/Confirm Permanent Erasure/i)).toBeInTheDocument();
      expect(screen.getByPlaceholderText('DELETE')).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /Confirm & Erase My Account/i })).toBeDisabled();
    });

    // Typing DELETE enables the button
    const input = screen.getByPlaceholderText('DELETE');
    fireEvent.change(input, { target: { value: 'DELETE' } });

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /Confirm & Erase My Account/i })).not.toBeDisabled();
    });
  });
});
