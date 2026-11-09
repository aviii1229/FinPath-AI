package com.finpath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finpath.modules.documents.dto.CategoryCorrectionRequest;
import com.finpath.modules.documents.dto.DocumentQueryRequest;
import com.finpath.security.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DocumentVaultIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setupToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest("sarah.connor@finpath.ai", "SecurePass123!");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();
        if (token == null || token.isEmpty()) {
            token = objectMapper.readTree(result.getResponse().getContentAsString())
                    .path("data").path("token").asText();
        }
        authToken = token;
    }

    private byte[] createMockPdfBytes(String text) {
        byte[] header = "%PDF-1.4\n".getBytes(StandardCharsets.UTF_8);
        byte[] body = text.getBytes(StandardCharsets.UTF_8);
        byte[] trailer = "\n%%EOF".getBytes(StandardCharsets.UTF_8);

        byte[] all = new byte[header.length + body.length + trailer.length];
        System.arraycopy(header, 0, all, 0, header.length);
        System.arraycopy(body, 0, all, header.length, body.length);
        System.arraycopy(trailer, 0, all, header.length + body.length, trailer.length);
        return all;
    }

    @Test
    void testUploadDocumentWithAutoClassificationAndExtraction() throws Exception {
        String pdfContent = "HDFC ERGO Health Insurance Policy Schedule\n" +
                "Policy No: POL-99283741\n" +
                "Insured Name: Sarah Connor\n" +
                "Sum Insured: ₹10,00,000\n" +
                "Total Premium: ₹18,450\n" +
                "Date: 15-08-2026\n" +
                "Expiry Date: 14-08-2027\n" +
                "IRDAI Reg: 125. Waiting period 24 months for pre-existing disease.\n";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "hdfc_health_policy.pdf",
                "application/pdf",
                createMockPdfBytes(pdfContent)
        );

        mockMvc.perform(multipart("/api/v1/documents/upload")
                        .file(file)
                        .param("journey", "HEALTH_INSURANCE")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.documentCategory", is("INSURANCE_POLICY")))
                .andExpect(jsonPath("$.data.fileName", containsString("hdfc_health_policy.pdf")))
                .andExpect(jsonPath("$.data.classificationConfidence", notNullValue()))
                .andExpect(jsonPath("$.data.extractedEntities", not(empty())))
                .andExpect(jsonPath("$.data.extractedEntities[?(@.fieldName == 'POLICY_NUMBER')].extractedValue", hasItem("POL-99283741")))
                .andExpect(jsonPath("$.data.extractedEntities[?(@.fieldName == 'COVERAGE_AMOUNT')].normalizedValue", hasItem("₹10,00,000")));
    }

    @Test
    void testUploadMaliciousFileOrInvalidExtensionRejected() throws Exception {
        MockMultipartFile badFile = new MockMultipartFile(
                "file",
                "malicious_script.exe",
                "application/octet-stream",
                "MZ_some_binary_executable".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/v1/documents/upload")
                        .file(badFile)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testListAndFilterUserDocuments() throws Exception {
        mockMvc.perform(get("/api/v1/documents")
                        .param("category", "ALL")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    void testManualCategoryCorrection() throws Exception {
        String pdfContent = "HDFC Bank Statement of Account\n" +
                "Transaction Date: 01/10/2026\n" +
                "Closing Balance: ₹2,45,000\n";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "bank_stmt.pdf",
                "application/pdf",
                createMockPdfBytes(pdfContent)
        );

        MvcResult uploadRes = mockMvc.perform(multipart("/api/v1/documents/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isCreated())
                .andReturn();

        String docId = objectMapper.readTree(uploadRes.getResponse().getContentAsString())
                .path("data").path("id").asText();

        CategoryCorrectionRequest correction = new CategoryCorrectionRequest("TAX_DOCUMENT", "User re-classified as tax proof");

        mockMvc.perform(patch("/api/v1/documents/" + docId + "/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(correction))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.documentCategory", is("TAX_DOCUMENT")))
                .andExpect(jsonPath("$.data.userCorrected", is(true)));
    }

    @Test
    void testDocumentGroundedQueryAndCitations() throws Exception {
        String content = "Bajaj Allianz Loan Sanction Letter\n" +
                "Sanctioned Amount: ₹25,00,000\n" +
                "Rate of Interest: 8.75% p.a.\n" +
                "Tenure: 180 months\n" +
                "Foreclosure charges are zero for individual borrowers.\n" +
                "Processing fee: ₹5,000 plus GST.\n";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "loan_sanction.pdf",
                "application/pdf",
                createMockPdfBytes(content)
        );

        MvcResult uploadRes = mockMvc.perform(multipart("/api/v1/documents/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isCreated())
                .andReturn();

        String docId = objectMapper.readTree(uploadRes.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // 1. Query conditions
        DocumentQueryRequest q1 = new DocumentQueryRequest("What are the important conditions?");
        mockMvc.perform(post("/api/v1/documents/" + docId + "/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(q1))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.citations", not(empty())))
                .andExpect(jsonPath("$.data.answer", containsStringIgnoringCase("foreclosure")));

        // 2. Query charges
        DocumentQueryRequest q2 = new DocumentQueryRequest("What are the charges?");
        mockMvc.perform(post("/api/v1/documents/" + docId + "/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(q2))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.answer", containsString("GST")));
    }

    @Test
    void testJourneyCompletenessEvaluation() throws Exception {
        mockMvc.perform(get("/api/v1/documents/completeness/HOME_LOAN")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.journeyType", is("HOME_LOAN")))
                .andExpect(jsonPath("$.data.requirements", not(empty())))
                .andExpect(jsonPath("$.data.regulatoryDisclaimer", containsString("does not constitute official regulatory verification")));

        mockMvc.perform(get("/api/v1/documents/completeness/PERSONAL_LOAN")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.journeyType", is("PERSONAL_LOAN")))
                .andExpect(jsonPath("$.data.completionPercentage", notNullValue()));
    }

    @Test
    void testVaultAggregateStats() throws Exception {
        mockMvc.perform(get("/api/v1/documents/stats")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.totalDocuments", notNullValue()))
                .andExpect(jsonPath("$.data.formattedTotalSize", notNullValue()));
    }

    @Test
    void testDownloadAndDeleteDocument() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "temp_download_test.pdf",
                "application/pdf",
                createMockPdfBytes("Sample PDF for download testing")
        );

        MvcResult uploadRes = mockMvc.perform(multipart("/api/v1/documents/upload")
                        .file(file)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isCreated())
                .andReturn();

        String docId = objectMapper.readTree(uploadRes.getResponse().getContentAsString())
                .path("data").path("id").asText();

        // Download
        mockMvc.perform(get("/api/v1/documents/" + docId + "/download")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", containsString("temp_download_test.pdf")));

        // Delete
        mockMvc.perform(delete("/api/v1/documents/" + docId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        // Verify 404 after delete
        mockMvc.perform(get("/api/v1/documents/" + docId)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAiOrchestratorDocumentIntent() throws Exception {
        String jsonPayload = """
                {
                    "message": "Are my KYC documents and checklist complete for a home loan?"
                }
                """;

        mockMvc.perform(post("/api/v1/ai/chat/query")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.intent", is("DOCUMENT_INTELLIGENCE")))
                .andExpect(jsonPath("$.data.toolInvocations[?(@.tool == 'getDocumentCompleteness')]", not(empty())))
                .andExpect(jsonPath("$.data.content", containsString("Readiness Score")))
                .andExpect(jsonPath("$.data.content", containsString("Regulatory Notice")));
    }
}
