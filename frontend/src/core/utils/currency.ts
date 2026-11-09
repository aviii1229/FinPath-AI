/**
 * FinPath AI — Centralized Currency & Localization Utility
 * Standard: Indian Rupee (INR - ₹) with en-IN numbering format
 */

export const DEFAULT_CURRENCY = 'INR';
export const CURRENCY_SYMBOL = '₹';
export const LOCALE_IN = 'en-IN';

export interface FormatCurrencyOptions {
  showSymbol?: boolean;
  minimumFractionDigits?: number;
  maximumFractionDigits?: number;
  compact?: boolean;
}

/**
 * Formats a numeric value into the Indian numbering system with the ₹ symbol.
 * Examples:
 *   formatINR(1000) => "₹1,000"
 *   formatINR(25000) => "₹25,000"
 *   formatINR(150000) => "₹1,50,000"
 *   formatINR(1000000) => "₹10,00,000"
 *   formatINR(12550000) => "₹1,25,50,000"
 */
export const formatINR = (
  amount: number | null | undefined,
  options?: FormatCurrencyOptions
): string => {
  if (amount === null || amount === undefined || isNaN(amount)) {
    return options?.showSymbol === false ? '0' : `${CURRENCY_SYMBOL}0`;
  }

  const {
    showSymbol = true,
    minimumFractionDigits = 0,
    maximumFractionDigits = 2,
    compact = false,
  } = options || {};

  if (compact) {
    return formatCompactINR(amount, showSymbol);
  }

  try {
    const formatted = new Intl.NumberFormat(LOCALE_IN, {
      minimumFractionDigits,
      maximumFractionDigits,
    }).format(amount);

    return showSymbol ? `${CURRENCY_SYMBOL}${formatted}` : formatted;
  } catch {
    // Fallback if Intl fails
    const formatted = amount.toLocaleString(LOCALE_IN);
    return showSymbol ? `${CURRENCY_SYMBOL}${formatted}` : formatted;
  }
};

/**
 * Formats numbers in standard Indian compact units (Lakh, Crore)
 * Examples:
 *   formatCompactINR(150000) => "₹1.5 Lakh"
 *   formatCompactINR(10000000) => "₹1.0 Crore"
 */
export const formatCompactINR = (amount: number, showSymbol = true): string => {
  const prefix = showSymbol ? CURRENCY_SYMBOL : '';
  const abs = Math.abs(amount);
  const sign = amount < 0 ? '-' : '';

  if (abs >= 10000000) {
    const crore = (abs / 10000000).toFixed(2).replace(/\.00$/, '');
    return `${sign}${prefix}${crore} Cr`;
  }
  if (abs >= 100000) {
    const lakh = (abs / 100000).toFixed(2).replace(/\.00$/, '');
    return `${sign}${prefix}${lakh} Lakh`;
  }
  if (abs >= 1000) {
    const k = (abs / 1000).toFixed(1).replace(/\.0$/, '');
    return `${sign}${prefix}${k}K`;
  }
  return `${sign}${prefix}${abs.toLocaleString(LOCALE_IN)}`;
};

/**
 * Parses user input containing currency symbols, commas, or Lakh/Crore notation into a clean number.
 * Examples:
 *   parseINR("₹3,00,000") => 300000
 *   parseINR("3 lakh") => 300000
 *   parseINR("1.5 crore") => 15000000
 */
export const parseINR = (input: string | null | undefined): number => {
  if (!input) return 0;
  const clean = input.trim().toLowerCase();

  // Handle Lakh / Lacs notation
  const lakhMatch = clean.match(/([\d.]+)\s*(?:lakh|lacs|lac)/);
  if (lakhMatch) {
    return parseFloat(lakhMatch[1]) * 100000;
  }

  // Handle Crore / Cr notation
  const croreMatch = clean.match(/([\d.]+)\s*(?:crore|cr)/);
  if (croreMatch) {
    return parseFloat(croreMatch[1]) * 10000000;
  }

  // Handle standard numbers with commas or currency symbols
  const numericString = clean.replace(/[₹$,\s]/g, '');
  const parsed = parseFloat(numericString);
  return isNaN(parsed) ? 0 : parsed;
};

/**
 * Formats date strings to Indian standard: DD-MM-YYYY
 */
export const formatDateIN = (dateInput: string | Date | null | undefined): string => {
  if (!dateInput) return '';
  const date = typeof dateInput === 'string' ? new Date(dateInput) : dateInput;
  if (isNaN(date.getTime())) return String(dateInput);

  const day = String(date.getDate()).padStart(2, '0');
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const year = date.getFullYear();

  return `${day}-${month}-${year}`;
};

