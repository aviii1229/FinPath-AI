package com.finpath.modules.documents.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DocumentQueryResponse {

    private UUID documentId;
    private String fileName;
    private String question;
    private String answer;
    private List<DocumentCitationDto> citations = new ArrayList<>();
    private String disclaimer = "AI extraction and interpretation are for informational reference only and do not replace official terms or certified advice.";

    public DocumentQueryResponse() {}

    public DocumentQueryResponse(UUID documentId, String fileName, String question, String answer, List<DocumentCitationDto> citations) {
        this.documentId = documentId;
        this.fileName = fileName;
        this.question = question;
        this.answer = answer;
        this.citations = citations != null ? citations : new ArrayList<>();
    }

    public UUID getDocumentId() { return documentId; }
    public void setDocumentId(UUID documentId) { this.documentId = documentId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public List<DocumentCitationDto> getCitations() { return citations; }
    public void setCitations(List<DocumentCitationDto> citations) { this.citations = citations; }

    public String getDisclaimer() { return disclaimer; }
    public void setDisclaimer(String disclaimer) { this.disclaimer = disclaimer; }
}
