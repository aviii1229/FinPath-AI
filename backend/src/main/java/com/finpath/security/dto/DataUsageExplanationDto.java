package com.finpath.security.dto;

import java.util.List;
import java.util.Map;

public class DataUsageExplanationDto {

    private String complianceFramework;
    private String dataFiduciary;
    private String legalBasis;
    private Map<String, String> dataCategories;
    private List<String> userRightsUnderDpdp;
    private String retentionPolicy;
    private String grievanceOfficerContact;

    public DataUsageExplanationDto() {}

    public DataUsageExplanationDto(String complianceFramework, String dataFiduciary, String legalBasis,
                                  Map<String, String> dataCategories, List<String> userRightsUnderDpdp,
                                  String retentionPolicy, String grievanceOfficerContact) {
        this.complianceFramework = complianceFramework;
        this.dataFiduciary = dataFiduciary;
        this.legalBasis = legalBasis;
        this.dataCategories = dataCategories;
        this.userRightsUnderDpdp = userRightsUnderDpdp;
        this.retentionPolicy = retentionPolicy;
        this.grievanceOfficerContact = grievanceOfficerContact;
    }

    public String getComplianceFramework() {
        return complianceFramework;
    }

    public void setComplianceFramework(String complianceFramework) {
        this.complianceFramework = complianceFramework;
    }

    public String getDataFiduciary() {
        return dataFiduciary;
    }

    public void setDataFiduciary(String dataFiduciary) {
        this.dataFiduciary = dataFiduciary;
    }

    public String getLegalBasis() {
        return legalBasis;
    }

    public void setLegalBasis(String legalBasis) {
        this.legalBasis = legalBasis;
    }

    public Map<String, String> getDataCategories() {
        return dataCategories;
    }

    public void setDataCategories(Map<String, String> dataCategories) {
        this.dataCategories = dataCategories;
    }

    public List<String> getUserRightsUnderDpdp() {
        return userRightsUnderDpdp;
    }

    public void setUserRightsUnderDpdp(List<String> userRightsUnderDpdp) {
        this.userRightsUnderDpdp = userRightsUnderDpdp;
    }

    public String getRetentionPolicy() {
        return retentionPolicy;
    }

    public void setRetentionPolicy(String retentionPolicy) {
        this.retentionPolicy = retentionPolicy;
    }

    public String getGrievanceOfficerContact() {
        return grievanceOfficerContact;
    }

    public void setGrievanceOfficerContact(String grievanceOfficerContact) {
        this.grievanceOfficerContact = grievanceOfficerContact;
    }
}
