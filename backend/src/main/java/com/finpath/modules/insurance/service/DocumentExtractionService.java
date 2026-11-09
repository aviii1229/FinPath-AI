package com.finpath.modules.insurance.service;

import com.finpath.modules.ai.entity.DocumentUploadEntity;
import com.finpath.modules.ai.repository.DocumentUploadRepository;
import com.finpath.modules.insurance.dto.DocumentExtractionResponse;
import com.finpath.modules.insurance.entity.PolicyDocumentChunkEntity;
import com.finpath.modules.insurance.repository.PolicyDocumentChunkRepository;
import com.finpath.security.entity.UserEntity;
import com.finpath.security.repository.UserRepository;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentExtractionService {

    private static final Logger log = LoggerFactory.getLogger(DocumentExtractionService.class);

    private final DocumentUploadRepository uploadRepository;
    private final PolicyDocumentChunkRepository chunkRepository;
    private final UserRepository userRepository;
    private final Tika tika;

    public DocumentExtractionService(DocumentUploadRepository uploadRepository,
                                   PolicyDocumentChunkRepository chunkRepository,
                                   UserRepository userRepository) {
        this.uploadRepository = uploadRepository;
        this.chunkRepository = chunkRepository;
        this.userRepository = userRepository;
        this.tika = new Tika();
    }

    @Transactional
    public DocumentExtractionResponse extractAndStoreDocument(UUID userId, MultipartFile file, String category) {
        UserEntity user = userRepository.findById(userId).orElse(null);

        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "policy_document.pdf";
        long size = file.getSize();
        String mimeType = file.getContentType() != null ? file.getContentType() : "application/pdf";

        log.info("Extracting document: {} (size: {} bytes, mime: {})", fileName, size, mimeType);

        String extractedText;
        try (InputStream is = file.getInputStream()) {
            extractedText = tika.parseToString(is);
        } catch (Exception e) {
            log.warn("Tika parsing failed, falling back to basic extraction: {}", e.getMessage());
            extractedText = "Policy Document: " + fileName + "\nExtracted text stream unavailable.";
        }

        return processAndSaveText(user, fileName, size, mimeType, category, extractedText);
    }

    @Transactional
    public DocumentExtractionResponse processAndSaveText(UserEntity user, String fileName, long size, String mimeType, String category, String text) {
        DocumentUploadEntity upload = new DocumentUploadEntity(
                user,
                fileName,
                size,
                mimeType,
                "/storage/policies/" + UUID.randomUUID() + "_" + fileName,
                category != null ? category : "INSURANCE_POLICY",
                "INDEXED",
                "Extracted " + text.length() + " characters of policy terms and conditions."
        );
        DocumentUploadEntity savedUpload = uploadRepository.save(upload);

        List<PolicyDocumentChunkEntity> chunks = chunkPolicyText(savedUpload.getId(), fileName, category, text);
        chunkRepository.saveAll(chunks);

        List<String> sectionTitles = chunks.stream().map(PolicyDocumentChunkEntity::getSectionTitle).distinct().toList();

        return new DocumentExtractionResponse(
                savedUpload.getId(),
                fileName,
                size,
                mimeType,
                chunks.size(),
                savedUpload.getExtractedSummary(),
                sectionTitles
        );
    }

    private List<PolicyDocumentChunkEntity> chunkPolicyText(UUID documentId, String fileName, String category, String fullText) {
        List<PolicyDocumentChunkEntity> chunks = new ArrayList<>();

        if (fullText == null || fullText.trim().isEmpty()) {
            fullText = "Standard Comprehensive Policy Terms and Coverage Schedule.";
        }

        // Section markers common to insurance agreements
        String[] sections = fullText.split("(?i)(?=(?:SECTION|ARTICLE|SCHEDULE OF BENEFITS|EXCLUSIONS|DEFINITIONS|CLAIMS PROCEDURE|LIMITATIONS))");

        int pageCounter = 1;
        for (int i = 0; i < sections.length; i++) {
            String rawSec = sections[i].trim();
            if (rawSec.length() < 30 && i < sections.length - 1) {
                continue;
            }

            // Derive section title from first line
            String firstLine = rawSec.split("\n")[0].trim();
            String title = firstLine.length() > 60 ? firstLine.substring(0, 57) + "..." : firstLine;
            if (title.isEmpty()) {
                title = "Section " + (i + 1) + " - General Policy Provisions";
            }

            chunks.add(new PolicyDocumentChunkEntity(
                    documentId,
                    "POL-" + Math.abs(fileName.hashCode() % 100000),
                    fileName,
                    title,
                    pageCounter,
                    rawSec,
                    category != null ? category : "HEALTH"
            ));

            pageCounter++;
        }

        if (chunks.isEmpty()) {
            chunks.add(new PolicyDocumentChunkEntity(
                    documentId,
                    "POL-GEN",
                    fileName,
                    "General Coverage & Benefits",
                    1,
                    fullText,
                    category != null ? category : "HEALTH"
            ));
        }

        return chunks;
    }
}

