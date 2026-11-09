package com.finpath.modules.documents.service;

import com.finpath.core.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Component
public class DocumentSecurityValidator {

    public static final long MAX_FILE_SIZE_BYTES = 15L * 1024 * 1024; // 15 MB
    public static final List<String> ALLOWED_EXTENSIONS = List.of(".pdf", ".jpg", ".jpeg", ".png");
    public static final List<String> ALLOWED_MIME_TYPES = List.of("application/pdf", "image/jpeg", "image/png");

    private static final byte[] PDF_MAGIC = new byte[]{0x25, 0x50, 0x44, 0x46, 0x2D}; // %PDF-
    private static final byte[] PNG_MAGIC = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] JPEG_MAGIC_PREFIX = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};

    public void validateUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Uploaded file cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BusinessException("File size exceeds 15 MB limit. Provided size: " + (file.getSize() / (1024 * 1024)) + " MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BusinessException("Invalid filename");
        }

        // Path traversal check
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new BusinessException("Security violation: Path traversal characters detected in filename");
        }

        // Extension check
        String lowerFilename = originalFilename.toLowerCase();
        boolean hasAllowedExtension = ALLOWED_EXTENSIONS.stream().anyMatch(lowerFilename::endsWith);
        if (!hasAllowedExtension) {
            throw new BusinessException("Unsupported file extension. Allowed extensions are: .pdf, .jpg, .jpeg, .png");
        }

        // MIME type validation
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            // Check extension as fallback if contentType was octet-stream
            if (!lowerFilename.endsWith(".pdf") && !lowerFilename.endsWith(".jpg") && !lowerFilename.endsWith(".jpeg") && !lowerFilename.endsWith(".png")) {
                throw new BusinessException("Unsupported MIME type: " + contentType + ". Allowed: PDF, JPG, PNG");
            }
        }

        // Magic byte validation
        validateMagicBytes(file);
    }

    private void validateMagicBytes(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[8];
            int read = is.read(header);
            if (read < 3) {
                throw new BusinessException("Corrupted or empty file stream");
            }

            boolean isPdf = isMatch(header, PDF_MAGIC);
            boolean isPng = isMatch(header, PNG_MAGIC);
            boolean isJpeg = header[0] == JPEG_MAGIC_PREFIX[0] && header[1] == JPEG_MAGIC_PREFIX[1] && header[2] == JPEG_MAGIC_PREFIX[2];

            if (!isPdf && !isPng && !isJpeg) {
                throw new BusinessException("File content header (magic bytes) does not match recognized PDF, PNG, or JPEG signatures");
            }
        } catch (IOException e) {
            throw new BusinessException("Failed to inspect file magic bytes: " + e.getMessage());
        }
    }

    private boolean isMatch(byte[] candidate, byte[] pattern) {
        if (candidate.length < pattern.length) return false;
        for (int i = 0; i < pattern.length; i++) {
            if (candidate[i] != pattern[i]) return false;
        }
        return true;
    }

    public String sanitizeFilename(String filename) {
        if (filename == null) return "document_" + System.currentTimeMillis() + ".pdf";
        String clean = Paths.get(filename).getFileName().toString();
        return clean.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
