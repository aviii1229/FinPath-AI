package com.finpath.modules.insurance.service;

import com.finpath.modules.insurance.dto.PolicyCitationDto;
import com.finpath.modules.insurance.dto.PolicyRagQueryRequest;
import com.finpath.modules.insurance.dto.PolicyRagQueryResponse;
import com.finpath.modules.insurance.entity.PolicyDocumentChunkEntity;
import com.finpath.modules.insurance.repository.PolicyDocumentChunkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PolicyRagService {

    private static final Logger log = LoggerFactory.getLogger(PolicyRagService.class);

    public static final String UNTRUSTED_DOC_START = "<<<UNTRUSTED_FINANCIAL_DOCUMENT_START>>>";
    public static final String UNTRUSTED_DOC_END = "<<<UNTRUSTED_FINANCIAL_DOCUMENT_END>>>";
    private static final String RESPONSIBLE_AI_DISCLAIMER = "\n\n*Responsible AI Notice: Information extracted from policy documentation. This analysis is educational and does not constitute formal underwriting sanction, claim guarantee, or regulated financial advice.*";

    private final PolicyDocumentChunkRepository chunkRepository;

    public PolicyRagService(PolicyDocumentChunkRepository chunkRepository) {
        this.chunkRepository = chunkRepository;
    }

    @Transactional(readOnly = true)
    public PolicyRagQueryResponse answerPolicyQuestion(PolicyRagQueryRequest request) {
        String query = sanitizeInput(request.getQuestion() != null ? request.getQuestion().trim() : "");
        log.info("Processing grounded RAG query: {}", query);

        List<PolicyDocumentChunkEntity> candidates;
        if (request.getDocumentId() != null) {
            candidates = chunkRepository.findByDocumentId(request.getDocumentId());
        } else if (request.getPolicyCategory() != null && !request.getPolicyCategory().equalsIgnoreCase("ALL")) {
            candidates = chunkRepository.findByCategory(request.getPolicyCategory().toUpperCase());
        } else {
            candidates = chunkRepository.findAll();
        }

        if (candidates.isEmpty()) {
            candidates = chunkRepository.findTop20ByOrderByCreatedAtDesc();
        }

        // Rank candidates by query term overlap / lexical relevance
        String[] keywords = query.toLowerCase().split("\\W+");
        List<ScoredChunk> scoredChunks = new ArrayList<>();

        for (PolicyDocumentChunkEntity chunk : candidates) {
            double score = computeRelevanceScore(chunk, keywords);
            if (score > 0) {
                scoredChunks.add(new ScoredChunk(chunk, score));
            }
        }

        scoredChunks.sort(Comparator.comparingDouble(ScoredChunk::getScore).reversed());

        if (scoredChunks.isEmpty() && !candidates.isEmpty()) {
            // Default top candidate
            scoredChunks.add(new ScoredChunk(candidates.get(0), 0.50));
        }

        List<PolicyCitationDto> citations = new ArrayList<>();
        StringBuilder answerBuilder = new StringBuilder();
        String matchedDoc = "Policy Agreement";

        int maxCitations = Math.min(3, scoredChunks.size());
        for (int i = 0; i < maxCitations; i++) {
            ScoredChunk sc = scoredChunks.get(i);
            PolicyDocumentChunkEntity c = sc.getChunk();
            matchedDoc = c.getDocumentName();

            String snippet = extractRelevantSnippet(c.getChunkText(), keywords);

            citations.add(new PolicyCitationDto(
                    c.getDocumentName(),
                    c.getSectionTitle(),
                    c.getPageNumber(),
                    snippet,
                    Math.round(sc.getScore() * 100.0) / 100.0
            ));
        }

        // Formulate grounded response based on top matched chunks
        if (!citations.isEmpty()) {
            PolicyCitationDto topCitation = citations.get(0);
            answerBuilder.append("Based on **").append(topCitation.getDocumentName()).append("**, specifically **")
                    .append(topCitation.getSectionTitle()).append("** (Page ").append(topCitation.getPageNumber()).append("):\n\n")
                    .append(generateGroundedSummary(query, topCitation.getExcerpt()))
                    .append(RESPONSIBLE_AI_DISCLAIMER);
        } else {
            answerBuilder.append("No specific clause matching '").append(query)
                    .append("' was located in the uploaded policy agreements. Please verify your document indexing or consult your benefits administrator.")
                    .append(RESPONSIBLE_AI_DISCLAIMER);
        }

        return new PolicyRagQueryResponse(
                query,
                answerBuilder.toString(),
                citations,
                !citations.isEmpty(),
                matchedDoc
        );
    }

    private String sanitizeInput(String input) {
        if (input == null) return "";
        // Strip common prompt injection attempts and system prompt override directives
        return input.replaceAll("(?i)(ignore\\s+all\\s+previous\\s+instructions|system\\s+prompt|disregard\\s+previous\\s+rules|you\\s+are\\s+now\\s+dan)", "")
                .trim();
    }

    private double computeRelevanceScore(PolicyDocumentChunkEntity chunk, String[] keywords) {
        String text = (chunk.getSectionTitle() + " " + chunk.getChunkText()).toLowerCase();
        double matches = 0.0;
        for (String kw : keywords) {
            if (kw.length() <= 2) continue; // skip stop words like "is", "a", "or"
            if (text.contains(kw)) {
                matches += 1.0;
                // Extra weight if title contains keyword
                if (chunk.getSectionTitle().toLowerCase().contains(kw)) {
                    matches += 1.5;
                }
            }
        }
        return matches;
    }

    private String extractRelevantSnippet(String fullChunk, String[] keywords) {
        String[] sentences = fullChunk.split("[\\.\\n]+");
        for (String sentence : sentences) {
            String lower = sentence.toLowerCase();
            for (String kw : keywords) {
                if (kw.length() > 3 && lower.contains(kw)) {
                    String clean = sentence.trim();
                    return clean.length() > 220 ? clean.substring(0, 217) + "..." : clean;
                }
            }
        }
        String first = sentences.length > 0 ? sentences[0].trim() : fullChunk.trim();
        return first.length() > 220 ? first.substring(0, 217) + "..." : first;
    }

    private String generateGroundedSummary(String query, String excerpt) {
        String qLower = query.toLowerCase();
        if (qLower.contains("deductible") || qLower.contains("cost") || qLower.contains("pay")) {
            return "The policy stipulates that standard cost-sharing and deductible obligations apply as follows: \"" + excerpt + "\"";
        } else if (qLower.contains("exclusion") || qLower.contains("not covered") || qLower.contains("excluded")) {
            return "Regarding limitations and non-covered services: \"" + excerpt + "\"";
        } else if (qLower.contains("emergency") || qLower.contains("travel") || qLower.contains("abroad")) {
            return "Under emergency and urgent care provisions: \"" + excerpt + "\"";
        } else if (qLower.contains("claim") || qLower.contains("submit") || qLower.contains("file")) {
            return "For claim processing and reimbursement guidelines: \"" + excerpt + "\"";
        }
        return "The policy provides: \"" + excerpt + "\"";
    }

    private static class ScoredChunk {
        private final PolicyDocumentChunkEntity chunk;
        private final double score;

        public ScoredChunk(PolicyDocumentChunkEntity chunk, double score) {
            this.chunk = chunk;
            this.score = score;
        }

        public PolicyDocumentChunkEntity getChunk() {
            return chunk;
        }

        public double getScore() {
            return score;
        }
    }
}

