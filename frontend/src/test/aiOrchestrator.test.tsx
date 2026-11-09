import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, it, expect, beforeEach, vi } from 'vitest';
import App from '@/App';

describe('FinPath AI 2.0 — Upgrade Phase 03: AI Orchestration & Tool Execution Suite', () => {
  beforeEach(() => {
    window.history.pushState({}, 'AI Assistant', '/ai-assistant');
  });

  it('renders AI Journey Orchestrator v2.0 with all Phase 3 scenario starters', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/FinPath AI Financial Advisor/i)).toBeInTheDocument();
      expect(screen.getByText(/Agentic Orchestration v2.0/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/Health Cover \+ Budget/i)).toBeInTheDocument();
    expect(screen.getAllByText(/What is FOIR\?/i)[0]).toBeInTheDocument();
    expect(screen.getByText(/Clear History & Reset/i)).toBeInTheDocument();
  });

  it('triggers Multi-Module query (Health Insurance + Budget) and displays cross-domain cards', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Health Cover \+ Budget/i)).toBeInTheDocument();
    });

    const multiModuleBtn = screen.getByText(/Health Cover \+ Budget/i);
    fireEvent.click(multiModuleBtn);

    await waitFor(
      () => {
        expect(screen.getByText(/Integrated Strategy: Family Health Shield & Budget Harmonization/i)).toBeInTheDocument();
        expect(screen.getByText(/Harmonized Cashflow Plan/i)).toBeInTheDocument();
      },
      { timeout: 4000 }
    );
  });

  it('triggers Financial Education query (What is FOIR?) and displays regulatory formula', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getAllByText(/What is FOIR\?/i)[0]).toBeInTheDocument();
    });

    const foirBtn = screen.getAllByText(/What is FOIR\?/i)[0];
    fireEvent.click(foirBtn);

    await waitFor(
      () => {
        expect(screen.getByText(/Fixed Obligation to Income Ratio \(FOIR\) Explained/i)).toBeInTheDocument();
        expect(screen.getByText(/Standard Safe Ceiling/i)).toBeInTheDocument();
      },
      { timeout: 4000 }
    );
  });

  it('clears chat history and resets consultation when Clear History is clicked', async () => {
    render(<App />);

    await waitFor(() => {
      expect(screen.getByText(/Clear History & Reset/i)).toBeInTheDocument();
    });

    const clearBtn = screen.getByText(/Clear History & Reset/i);
    fireEvent.click(clearBtn);

    await waitFor(() => {
      expect(screen.getByText(/Welcome to the FinPath AI Central Journey Orchestrator/i)).toBeInTheDocument();
    });
  });
});

