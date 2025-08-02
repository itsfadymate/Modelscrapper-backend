package guc.internship.modelscrapper.dto.search;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchFilter {
    private List<String> sources;
    private Boolean showFreeOnly;
    private String descriptionSearchTerm;
    private String licenseSearchTerm;
    private List<String> sourcesToGoogle;
    private Set<String> sourcesToGoogleSet;
    private Set<String> enabledSourcesSet;


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

    public boolean isSourceToGoogle(String sourceName){
        return sourcesToGoogleSet.contains(sourceName.toLowerCase());
    }

    public boolean isEnabledSource(String sourceName){
        return enabledSourcesSet.contains(sourceName.toLowerCase());
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
        this.enabledSourcesSet = new HashSet<>();
        for (String s:sources)
            enabledSourcesSet.add(s.toLowerCase());
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
        this.sourcesToGoogleSet = new HashSet<>();
        for (String source : sourcesToGoogle)
            sourcesToGoogleSet.add(source.toLowerCase());
    }

    @Override
    public String toString() {
        return "SearchFilter{\n" +
                "sources=" + sources +
                "\n, showFreeOnly=" + showFreeOnly +
                "\n, descriptionSearchTerm='" + descriptionSearchTerm + '\'' +
                "\n, licenseSearchTerm='" + licenseSearchTerm + '\'' +
                "\n, sourcesToGoogle=" + sourcesToGoogle +
                "\n}";
    }
}
