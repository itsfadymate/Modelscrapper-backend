package guc.internship.modelscrapper.dto.search;

import java.util.List;

public class SearchFilter {
    private final List<String> sources;
    private final Boolean showFreeOnly;
    private final String descriptionSearchTerm;
    private final String licenseSearchTerm;
    private final List<String> sourcesToGoogle;

    public SearchFilter(List<String> sources, Boolean showFreeOnly, String descriptionSearchTerm, String licenseSearchTerm, List<String> sourcesToGoogle) {
        this.sources = sources;
        this.showFreeOnly = showFreeOnly;
        this.descriptionSearchTerm = descriptionSearchTerm;
        this.licenseSearchTerm = licenseSearchTerm;
        this.sourcesToGoogle = sourcesToGoogle;
    }

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
}
