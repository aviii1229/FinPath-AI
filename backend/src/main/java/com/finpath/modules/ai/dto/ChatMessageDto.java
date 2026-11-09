package com.finpath.modules.ai.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ChatMessageDto {
    private UUID id;
    private String senderRole;
    private String content;
    private Instant timestamp;
    private List<ToolInvocationDto> toolInvocations;
    private List<CitationDto> citations;

    public ChatMessageDto() {}

    public ChatMessageDto(UUID id, String senderRole, String content, Instant timestamp) {
        this.id = id;
        this.senderRole = senderRole;
        this.content = content;
        this.timestamp = timestamp;
    }

    public static class ToolInvocationDto {
        private String tool;
        private String resultSummary;

        public ToolInvocationDto() {}

        public ToolInvocationDto(String tool, String resultSummary) {
            this.tool = tool;
            this.resultSummary = resultSummary;
        }

        public String getTool() {
            return tool;
        }

        public String getToolName() {
            return tool;
        }

        public void setTool(String tool) {
            this.tool = tool;
        }

        public String getResultSummary() {
            return resultSummary;
        }

        public void setResultSummary(String resultSummary) {
            this.resultSummary = resultSummary;
        }
    }

    public static class CitationDto {
        private String documentName;
        private int page;
        private String textExcerpt;

        public CitationDto() {}

        public CitationDto(String documentName, int page, String textExcerpt) {
            this.documentName = documentName;
            this.page = page;
            this.textExcerpt = textExcerpt;
        }

        public String getDocumentName() {
            return documentName;
        }

        public void setDocumentName(String documentName) {
            this.documentName = documentName;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public String getTextExcerpt() {
            return textExcerpt;
        }

        public void setTextExcerpt(String textExcerpt) {
            this.textExcerpt = textExcerpt;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public List<ToolInvocationDto> getToolInvocations() {
        return toolInvocations;
    }

    public void setToolInvocations(List<ToolInvocationDto> toolInvocations) {
        this.toolInvocations = toolInvocations;
    }

    public List<CitationDto> getCitations() {
        return citations;
    }

    public void setCitations(List<CitationDto> citations) {
        this.citations = citations;
    }
}

