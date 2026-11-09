package com.finpath.modules.documents.service;

import com.finpath.modules.documents.dto.DocumentClassificationResultDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class DocumentClassificationService {

    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new LinkedHashMap<>();

    static {
        CATEGORY_KEYWORDS.put("INSURANCE_POLICY", List.of(
                "policy", "premium", "sum insured", "coverage", "irdai", "uin", "claim",
                "insured", "policyholder", "tpa", "cashless", "deductible", "copay", "nominee", "grace period"
        ));
        CATEGORY_KEYWORDS.put("LOAN_AGREEMENT", List.of(
                "loan", "sanction", "borrower", "lender", "principal", "interest rate", "emi",
                "tenure", "tenor", "disbursement", "collateral", "hypothecation", "repayment schedule", "foreclosure"
        ));
        CATEGORY_KEYWORDS.put("BANK_STATEMENT", List.of(
                "statement of account", "bank statement", "transaction date", "withdrawal", "deposit",
                "closing balance", "opening balance", "ifsc", "ledger balance", "cheque no", "cr/dr", "debit", "credit"
        ));
        CATEGORY_KEYWORDS.put("SALARY_SLIP", List.of(
                "payslip", "salary slip", "basic pay", "hra", "house rent allowance", "provident fund",
                "pf deduction", "gross earnings", "net pay", "ctc", "epf", "professional tax"
        ));
        CATEGORY_KEYWORDS.put("TAX_DOCUMENT", List.of(
                "form 16", "income tax", "itr", "itr-v", "assessment year", "financial year", "tan",
                "tds", "section 80c", "taxable income", "tax refund", "pan no"
        ));
        CATEGORY_KEYWORDS.put("IDENTITY_PROOF", List.of(
                "permanent account number", "income tax department", "govt of india", "government of india",
                "unique identification authority", "uidai", "aadhaar", "e-aadhaar", "passport", "voter id"
        ));
        CATEGORY_KEYWORDS.put("ADDRESS_PROOF", List.of(
                "electricity bill", "water bill", "piped gas", "broadband bill", "consumer number",
                "meter reading", "billing cycle", "service address", "rent agreement"
        ));
    }

    public DocumentClassificationResultDto classify(String filename, String content) {
        String lowerFilename = (filename != null ? filename.toLowerCase() : "");
        String lowerContent = (content != null ? content.toLowerCase() : "");

        Map<String, Integer> matchScores = new HashMap<>();
        Map<String, List<String>> matchedKeywordsMap = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();

            int score = 0;
            List<String> matchedKeywords = new ArrayList<>();

            for (String kw : keywords) {
                if (lowerContent.contains(kw)) {
                    score += 2;
                    matchedKeywords.add(kw);
                }
                if (lowerFilename.contains(kw.replace(" ", "_")) || lowerFilename.contains(kw.replace(" ", ""))) {
                    score += 3;
                    if (!matchedKeywords.contains(kw)) {
                        matchedKeywords.add(kw);
                    }
                }
            }

            // Filename specific boosts
            if (category.equals("INSURANCE_POLICY") && (lowerFilename.contains("policy") || lowerFilename.contains("insurance"))) score += 4;
            if (category.equals("LOAN_AGREEMENT") && (lowerFilename.contains("loan") || lowerFilename.contains("sanction"))) score += 4;
            if (category.equals("BANK_STATEMENT") && (lowerFilename.contains("statement") || lowerFilename.contains("bank"))) score += 4;
            if (category.equals("SALARY_SLIP") && (lowerFilename.contains("salary") || lowerFilename.contains("payslip"))) score += 4;
            if (category.equals("TAX_DOCUMENT") && (lowerFilename.contains("tax") || lowerFilename.contains("form16") || lowerFilename.contains("itr"))) score += 4;
            if (category.equals("IDENTITY_PROOF") && (lowerFilename.contains("pan") || lowerFilename.contains("aadhaar") || lowerFilename.contains("id_proof"))) score += 4;
            if (category.equals("ADDRESS_PROOF") && (lowerFilename.contains("bill") || lowerFilename.contains("utility") || lowerFilename.contains("rent"))) score += 4;

            matchScores.put(category, score);
            matchedKeywordsMap.put(category, matchedKeywords);
        }

        String bestCategory = "OTHER_FINANCIAL";
        int maxScore = 0;

        for (Map.Entry<String, Integer> scoreEntry : matchScores.entrySet()) {
            if (scoreEntry.getValue() > maxScore) {
                maxScore = scoreEntry.getValue();
                bestCategory = scoreEntry.getKey();
            }
        }

        BigDecimal confidence;
        String reasoning;
        List<String> detected = matchedKeywordsMap.getOrDefault(bestCategory, Collections.emptyList());

        if (maxScore >= 8) {
            confidence = BigDecimal.valueOf(0.96).setScale(3, RoundingMode.HALF_UP);
            reasoning = "High-confidence automatic classification based on strong semantic indicators (" + String.join(", ", detected.stream().limit(4).toList()) + ").";
        } else if (maxScore >= 4) {
            confidence = BigDecimal.valueOf(0.88).setScale(3, RoundingMode.HALF_UP);
            reasoning = "Moderate-confidence classification based on detected terms (" + String.join(", ", detected.stream().limit(3).toList()) + ").";
        } else if (maxScore > 0) {
            confidence = BigDecimal.valueOf(0.72).setScale(3, RoundingMode.HALF_UP);
            reasoning = "Low-confidence match based on limited keyword occurrences (" + String.join(", ", detected) + "). User verification recommended.";
        } else {
            bestCategory = "OTHER_FINANCIAL";
            confidence = BigDecimal.valueOf(0.50).setScale(3, RoundingMode.HALF_UP);
            reasoning = "Unclassified financial document. Categorized as Other Financial Document pending manual verification.";
            detected = List.of();
        }

        return new DocumentClassificationResultDto(bestCategory, confidence, reasoning, detected);
    }
}

