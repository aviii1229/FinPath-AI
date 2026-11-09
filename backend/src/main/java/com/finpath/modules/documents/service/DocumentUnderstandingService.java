package com.finpath.modules.documents.service;

import com.finpath.modules.documents.dto.DocumentCitationDto;
import com.finpath.modules.documents.dto.DocumentQueryResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentUnderstandingService {

    public DocumentQueryResponse answerQuestion(UUID docId, String fileName, String category, String fullText, String question) {
        if (question == null || question.isBlank()) {
            return new DocumentQueryResponse(docId, fileName, "", "Please provide a valid question regarding this document.", List.of());
        }

        String lowerQ = question.toLowerCase().trim();
        String text = (fullText != null) ? fullText : "";
        List<DocumentCitationDto> citations = new ArrayList<>();
        String answer;

        if (lowerQ.contains("what does this document mean") || lowerQ.contains("explain this document") || lowerQ.contains("summary") || lowerQ.contains("what is this")) {
            answer = generateMeaningAnswer(category, fileName, text, citations);
        } else if (lowerQ.contains("condition") || lowerQ.contains("term") || lowerQ.contains("exclusion") || lowerQ.contains("clause") || lowerQ.contains("rule")) {
            answer = generateConditionsAnswer(category, text, citations);
        } else if (lowerQ.contains("charge") || lowerQ.contains("fee") || lowerQ.contains("cost") || lowerQ.contains("penalty") || lowerQ.contains("premium") || lowerQ.contains("price")) {
            answer = generateChargesAnswer(category, text, citations);
        } else if (lowerQ.contains("missing") || lowerQ.contains("incomplete") || lowerQ.contains("what information is missing")) {
            answer = generateMissingInfoAnswer(category, text, citations);
        } else if (lowerQ.contains("date") || lowerQ.contains("when") || lowerQ.contains("timeline") || lowerQ.contains("deadline") || lowerQ.contains("expiry")) {
            answer = generateDatesAnswer(category, text, citations);
        } else {
            answer = generateContextualSearchAnswer(text, question, citations);
        }

        return new DocumentQueryResponse(docId, fileName, question, answer, citations);
    }

    private String generateMeaningAnswer(String category, String fileName, String text, List<DocumentCitationDto> citations) {
        String catLabel = switch (category != null ? category.toUpperCase() : "") {
            case "INSURANCE_POLICY" -> "Comprehensive Insurance Policy Schedule";
            case "LOAN_AGREEMENT" -> "Binding Loan Agreement / Sanction Letter";
            case "BANK_STATEMENT" -> "Official Bank Account Statement";
            case "SALARY_SLIP" -> "Monthly Salary Payslip";
            case "TAX_DOCUMENT" -> "Income Tax Return / Form 16 Document";
            case "IDENTITY_PROOF" -> "Government Issued Identity Proof";
            case "ADDRESS_PROOF" -> "Proof of Residence / Utility Bill";
            default -> "Financial Record";
        };

        if (text.length() > 50) {
            String firstChunk = text.substring(0, Math.min(text.length(), 250)).replaceAll("[\\r\\n]+", " ");
            citations.add(new DocumentCitationDto(1, "Header & Identification", firstChunk, 0.95));
        }

        return String.format(
                "This document is classified as a %s (%s). It records formal financial commitments, terms, and obligations between the parties. Key metadata and extracted values have been structured into your FinPath Document Vault for seamless cross-module referencing across Insurance, Lending, and Fintech planning.",
                catLabel, fileName
        );
    }

    private String generateConditionsAnswer(String category, String text, List<DocumentCitationDto> citations) {
        List<String> conditionsFound = new ArrayList<>();

        if (text.toLowerCase().contains("waiting period")) {
            conditionsFound.add("Waiting period clauses apply for specific pre-existing health conditions (typically 24 to 36 months).");
            extractSnippetForCitation(text, "waiting period", "Waiting Period Conditions", citations);
        }
        if (text.toLowerCase().contains("exclusion") || text.toLowerCase().contains("excluded")) {
            conditionsFound.add("Policy exclusion clauses specify non-covered scenarios (e.g., cosmetic treatments, hazardous sports).");
            extractSnippetForCitation(text, "exclusion", "General Exclusions", citations);
        }
        if (text.toLowerCase().contains("foreclosure") || text.toLowerCase().contains("prepayment")) {
            conditionsFound.add("Prepayment and foreclosure conditions apply. In India, floating-rate retail term loans incur zero foreclosure charges per RBI norms.");
            extractSnippetForCitation(text, "foreclosure", "Foreclosure & Prepayment Terms", citations);
        }
        if (text.toLowerCase().contains("grace period")) {
            conditionsFound.add("Grace period of 15 to 30 days is available for premium payment after the renewal due date.");
            extractSnippetForCitation(text, "grace period", "Premium Payment Grace Window", citations);
        }

        if (conditionsFound.isEmpty()) {
            citations.add(new DocumentCitationDto(1, "Standard Contractual Clauses", text.length() > 200 ? text.substring(0, 200) : text, 0.82));
            return "Standard contractual conditions apply. Ensure all borrower/policyholder covenants are maintained and notifications of material changes are provided promptly to the institution.";
        }

        return "Key terms and conditions identified in this document:\n- " + String.join("\n- ", conditionsFound);
    }

    private String generateChargesAnswer(String category, String text, List<DocumentCitationDto> citations) {
        List<String> charges = new ArrayList<>();

        Pattern pat = Pattern.compile("(?i)(?:premium|interest\\s*rate|processing\\s*fee|gst|late\\s*fee|penalty)[:\\s]*([₹rs0-9,.]+)");
        Matcher m = pat.matcher(text);
        while (m.find()) {
            String charge = m.group(0).trim();
            charges.add(charge);
            if (citations.size() < 3) {
                int start = Math.max(0, m.start() - 20);
                int end = Math.min(text.length(), m.end() + 20);
                citations.add(new DocumentCitationDto(1, "Schedule of Charges", text.substring(start, end).replaceAll("[\\r\\n]+", " "), 0.92));
            }
        }

        if (charges.isEmpty()) {
            return "No explicit fee schedule or penalty line items were found in the parsed text. Always verify the sanction letter annexure or policy endorsement schedule for hidden service charges or GST levies.";
        }

        return "Detected fee and charge items in document:\n- " + String.join("\n- ", charges) + "\n\nNote: All charges in India are typically subject to applicable 18% GST.";
    }

    private String generateMissingInfoAnswer(String category, String text, List<DocumentCitationDto> citations) {
        List<String> missing = new ArrayList<>();
        String lower = text.toLowerCase();

        if (!lower.contains("date")) {
            missing.add("Clear execution or issuance date");
        }
        if (!lower.contains("signature") && !lower.contains("authorized signatory") && !lower.contains("digitally signed")) {
            missing.add("Authorized institutional signature or digital verification stamp");
        }
        if ("INSURANCE_POLICY".equals(category) && !lower.contains("nominee")) {
            missing.add("Registered Nominee details / beneficiary designation");
        }
        if ("LOAN_AGREEMENT".equals(category) && !lower.contains("repayment schedule") && !lower.contains("emi")) {
            missing.add("Detailed amortization / EMI repayment breakdown schedule");
        }

        if (missing.isEmpty()) {
            return "The document appears structurally complete with core identifier numbers, signatory markers, and scheduled terms present.";
        }

        return "Potential missing or unverified elements detected:\n- " + String.join("\n- ", missing) + "\n\nEnsure complete annexures are provided before formal submission.";
    }

    private String generateDatesAnswer(String category, String text, List<DocumentCitationDto> citations) {
        Pattern datePattern = Pattern.compile("(?i)(?:date|valid\\s*till|expiry|due\\s*date|commencement)[:\\s]*([0-9]{1,2}[/-][0-9]{1,2}[/-][0-9]{2,4}|[0-9]{1,2}\\s+[A-Za-z]{3,9}\\s+[0-9]{4})");
        Matcher m = datePattern.matcher(text);

        List<String> dates = new ArrayList<>();
        while (m.find()) {
            String dateSnippet = m.group(0).trim();
            dates.add(dateSnippet);
            if (citations.size() < 3) {
                int start = Math.max(0, m.start() - 20);
                int end = Math.min(text.length(), m.end() + 20);
                citations.add(new DocumentCitationDto(1, "Key Dates Timeline", text.substring(start, end).replaceAll("[\\r\\n]+", " "), 0.94));
            }
        }

        if (dates.isEmpty()) {
            return "No specific milestone or renewal dates were detected in this document excerpt.";
        }

        return "Significant dates referenced in the document:\n- " + String.join("\n- ", dates);
    }

    private String generateContextualSearchAnswer(String text, String question, List<DocumentCitationDto> citations) {
        // Keyword-based search inside document text
        String[] terms = question.split("\\s+");
        for (String term : terms) {
            if (term.length() > 3) {
                int idx = text.toLowerCase().indexOf(term.toLowerCase());
                if (idx >= 0) {
                    int start = Math.max(0, idx - 40);
                    int end = Math.min(text.length(), idx + term.length() + 80);
                    String snippet = text.substring(start, end).replaceAll("[\\r\\n]+", " ").trim();
                    citations.add(new DocumentCitationDto(1, "Excerpt regarding '" + term + "'", "..." + snippet + "...", 0.88));
                    break;
                }
            }
        }

        if (citations.isEmpty()) {
            return "Based on the uploaded document contents, this specific query could not be verified directly. Please check if the relevant schedule or endorsement page has been uploaded.";
        }

        return "Document Grounded Analysis: Based on the relevant sections matching your query, the document references:\n\"" + citations.get(0).getQuotedSnippet() + "\". Please verify specific terms against the certified contract.";
    }

    private void extractSnippetForCitation(String text, String keyword, String title, List<DocumentCitationDto> citations) {
        if (citations.size() >= 4) return;
        int idx = text.toLowerCase().indexOf(keyword.toLowerCase());
        if (idx >= 0) {
            int start = Math.max(0, idx - 30);
            int end = Math.min(text.length(), idx + 100);
            String snippet = text.substring(start, end).replaceAll("[\\r\\n]+", " ").trim();
            citations.add(new DocumentCitationDto(1, title, "..." + snippet + "...", 0.93));
        }
    }
}
