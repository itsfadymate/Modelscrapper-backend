package guc.internship.modelscrapper.dto.search;

import java.util.List;

public class SearchFilter {
    private List<String> sources;
    private Boolean showFreeOnly;
    private String descriptionSearchTerm;
    private String licenseSearchTerm;
    private List<String> sourcesToGoogle;


    public List<String> getSources() {
        return sources;
    }

    public Boolean getShowFreeOnly() {
        return showFreeOnly;
    }

    public String getDescriptionSearchTerm() {
        return descriptionSearchTerm;
    }

    public String getLicenseSearchTerm() {
        return licenseSearchTerm;
    }

    public List<String> getSourcesToGoogle() {
        return sourcesToGoogle;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public void setShowFreeOnly(Boolean showFreeOnly) {
        this.showFreeOnly = showFreeOnly;
    }

    public void setDescriptionSearchTerm(String descriptionSearchTerm) {
        this.descriptionSearchTerm = descriptionSearchTerm;
    }

    public void setLicenseSearchTerm(String licenseSearchTerm) {
        this.licenseSearchTerm = licenseSearchTerm;
    }

    public void setSourcesToGoogle(List<String> sourcesToGoogle) {
        this.sourcesToGoogle = sourcesToGoogle;
    }
}
