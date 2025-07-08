package guc.internship.modelscrapper.dto.sketchfab;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SketchfabSearchResponse {

    @JsonProperty("next")
    private String nextPageUrl;

    @JsonProperty("previous")
    private String previousPageUrl;

    @JsonProperty("results")
    private List<SketchfabSearchResponse> results;

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

    public List<SketchfabSearchResponse> getResults() {
        return results;
    }

    public void setResults(List<SketchfabSearchResponse> results) {
        this.results = results;
    }
}
