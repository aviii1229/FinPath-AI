package com.finpath.modules.documents.service;

import com.finpath.modules.documents.dto.ExtractedEntityDto;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FinancialDocumentExtractionService {

    // Regex patterns for key financial entities
    private static final Pattern POLICY_NUM_PATTERN = Pattern.compile("(?i)(?:policy\\s*(?:no|num|number|#)\\s*[:#-]?\\s*|policy[:#-]+\\s*)([A-Z0-9/-]{6,25})");
    private static final Pattern LOAN_NUM_PATTERN = Pattern.compile("(?i)(?:loan\\s*(?:account|acc|a/c|application|no|#)\\s*[:#-]?\\s*|loan[:#-]+\\s*)([A-Z0-9/-]{6,25})");
    
    // Indian Currency extraction: captures Rs. / INR / ₹ followed by numbers and Indian comma grouping
    private static final Pattern SUM_INSURED_PATTERN = Pattern.compile("(?i)(?:sum\\s*insured|coverage|base\\s*cover)[:\\s]*(?:₹|rs\\.?|inr)?\\s*([0-9,]+(?:\\.[0-9]{2})?)");
    private static final Pattern PREMIUM_PATTERN = Pattern.compile("(?i)(?:total\\s*premium|gross\\s*premium|annual\\s*premium|net\\s*premium)[:\\s]*(?:₹|rs\\.?|inr)?\\s*([0-9,]+(?:\\.[0-9]{2})?)");
    private static final Pattern LOAN_AMOUNT_PATTERN = Pattern.compile("(?i)(?:sanction(?:ed)?\\s*amount|loan\\s*amount|principal\\s*amount)[:\\s]*(?:₹|rs\\.?|inr)?\\s*([0-9,]+(?:\\.[0-9]{2})?)");
    private static final Pattern INTEREST_RATE_PATTERN = Pattern.compile("(?i)(?:rate\\s*of\\s*interest|interest\\s*rate|roi|apr)[:\\s]*([0-9]{1,2}(?:\\.[0-9]{1,3})?)\\s*%");
    private static final Pattern TENURE_PATTERN = Pattern.compile("(?i)(?:tenure|tenor|repayment\\s*period|duration)[:\\s]*([0-9]{1,3})\\s*(months?|years?|yrs?)");
    
    // Dates in common Indian formats: DD/MM/YYYY, DD-MM-YYYY, DD Month YYYY
    private static final Pattern DATE_PATTERN = Pattern.compile("(?i)(?:date|dated|issue\\s*date)[:\\s]*(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}|\\d{1,2}\\s+[A-Za-z]{3,9}\\s+\\d{4})");
    private static final Pattern EXPIRY_PATTERN = Pattern.compile("(?i)(?:valid\\s*till|expiry\\s*date|valid\\s*upto|period\\s*to)[:\\s]*(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}|\\d{1,2}\\s+[A-Za-z]{3,9}\\s+\\d{4})");
    private static final Pattern STATEMENT_PERIOD_PATTERN = Pattern.compile("(?i)(?:statement\\s*period|period)[:\\s]*(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4}\\s*(?:to|-)\\s*\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})");

    public List<ExtractedEntityDto> extractEntities(String content, String category) {
        if (content == null || content.isBlank()) {
            return Collections.emptyList();
        }

        List<ExtractedEntityDto> entities = new ArrayList<>();
        int page = 1;

        // 1. Policy Number
        extractPattern(content, POLICY_NUM_PATTERN, "POLICY_NUMBER", "Policy Number", page, entities, 0.94);

        // 2. Loan Account / Application Number
        extractPattern(content, LOAN_NUM_PATTERN, "LOAN_NUMBER", "Loan Account / Reference", page, entities, 0.92);

        // 3. Sum Insured / Coverage
        extractCurrencyPattern(content, SUM_INSURED_PATTERN, "COVERAGE_AMOUNT", "Sum Insured (Coverage)", page, entities, 0.95);

        // 4. Premium Amount
        extractCurrencyPattern(content, PREMIUM_PATTERN, "PREMIUM_AMOUNT", "Annual Premium Amount", page, entities, 0.93);

        // 5. Loan Sanction Amount
        extractCurrencyPattern(content, LOAN_AMOUNT_PATTERN, "LOAN_AMOUNT", "Sanctioned Loan Principal", page, entities, 0.95);

        // 6. Interest Rate
        Matcher roiMatcher = INTEREST_RATE_PATTERN.matcher(content);
        if (roiMatcher.find()) {
            String val = roiMatcher.group(1).trim() + "% p.a.";
            String snippet = getSnippet(content, roiMatcher.start(), roiMatcher.end());
            entities.add(new ExtractedEntityDto("INTEREST_RATE", "Annual Interest Rate (ROI)", val, val, 0.96, page, snippet));
        }

        // 7. Tenure
        Matcher tenureMatcher = TENURE_PATTERN.matcher(content);
        if (tenureMatcher.find()) {
            String val = tenureMatcher.group(1).trim() + " " + tenureMatcher.group(2).trim();
            String snippet = getSnippet(content, tenureMatcher.start(), tenureMatcher.end());
            entities.add(new ExtractedEntityDto("REPAYMENT_PERIOD", "Tenure / Repayment Period", val, val, 0.91, page, snippet));
        }

        // 8. Statement Period
        Matcher stmMatcher = STATEMENT_PERIOD_PATTERN.matcher(content);
        if (stmMatcher.find()) {
            String val = stmMatcher.group(1).trim();
            String snippet = getSnippet(content, stmMatcher.start(), stmMatcher.end());
            entities.add(new ExtractedEntityDto("STATEMENT_PERIOD", "Statement Period", val, val, 0.90, page, snippet));
        }

        // 9. Document Date
        Matcher dateMatcher = DATE_PATTERN.matcher(content);
        if (dateMatcher.find()) {
            String val = dateMatcher.group(1).trim();
            String snippet = getSnippet(content, dateMatcher.start(), dateMatcher.end());
            entities.add(new ExtractedEntityDto("DOCUMENT_DATE", "Document Date", val, val, 0.89, page, snippet));
        }

        // 10. Expiry Date
        Matcher expMatcher = EXPIRY_PATTERN.matcher(content);
        if (expMatcher.find()) {
            String val = expMatcher.group(1).trim();
            String snippet = getSnippet(content, expMatcher.start(), expMatcher.end());
            entities.add(new ExtractedEntityDto("EXPIRY_DATE", "Policy / Document Expiry Date", val, val, 0.92, page, snippet));
        }

        return entities;
    }

    private void extractPattern(String content, Pattern pattern, String fieldKey, String fieldLabel, int page, List<ExtractedEntityDto> out, double confidence) {
        Matcher m = pattern.matcher(content);
        while (m.find()) {
            String val = m.group(1).trim();
            if (val.length() >= 4 && val.matches(".*\\d.*") && !out.stream().anyMatch(e -> e.getFieldName().equals(fieldKey))) {
                String snippet = getSnippet(content, m.start(), m.end());
                out.add(new ExtractedEntityDto(fieldKey, fieldLabel, val, val, confidence, page, snippet));
                break;
            }
        }
    }

    private void extractCurrencyPattern(String content, Pattern pattern, String fieldKey, String fieldLabel, int page, List<ExtractedEntityDto> out, double confidence) {
        Matcher m = pattern.matcher(content);
        if (m.find()) {
            String rawVal = m.group(1).trim();
            // Format to standard INR ₹ presentation
            String normalizedVal = "₹" + rawVal;
            String snippet = getSnippet(content, m.start(), m.end());
            out.add(new ExtractedEntityDto(fieldKey, fieldLabel, rawVal, normalizedVal, confidence, page, snippet));
        }
    }

    private String getSnippet(String content, int start, int end) {
        int left = Math.max(0, start - 40);
        int right = Math.min(content.length(), end + 40);
        String snippet = content.substring(left, right).replaceAll("[\\r\\n]+", " ").trim();
        return (left > 0 ? "..." : "") + snippet + (right < content.length() ? "..." : "");
    }

    public String generateSummary(String category, List<ExtractedEntityDto> entities) {
        StringBuilder sb = new StringBuilder();
        sb.append("Document processed successfully. ");
        if (entities.isEmpty()) {
            sb.append("Classified under category ").append(category).append(". No critical monetary or identifier fields detected.");
            return sb.toString();
        }

        sb.append("Extracted key parameters: ");
        for (int i = 0; i < entities.size(); i++) {
            ExtractedEntityDto e = entities.get(i);
            sb.append(e.getFieldLabel()).append(": ").append(e.getNormalizedValue() != null ? e.getNormalizedValue() : e.getExtractedValue());
            if (i < entities.size() - 1) {
                sb.append(" | ");
            }
        }
        sb.append(".");
        return sb.toString();
    }
}
