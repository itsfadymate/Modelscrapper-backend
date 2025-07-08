package guc.internship.modelscrapper.dto.sketchfab;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class sketchfabSearchResponse {

    @JsonProperty("next")
    private String nextPageUrl;

    @JsonProperty("previous")
    private String previousPageUrl;

    @JsonProperty("results")
    private List<sketchfabSearchResponse> results;

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    public String getPreviousPageUrl() {
        return previousPageUrl;
    }

    public void setPreviousPageUrl(String previousPageUrl) {
        this.previousPageUrl = previousPageUrl;
    }

    public List<sketchfabSearchResponse> getResults() {
        return results;
    }

    public void setResults(List<sketchfabSearchResponse> results) {
        this.results = results;
    }
}
