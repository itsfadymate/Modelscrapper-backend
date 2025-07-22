package guc.internship.modelscrapper.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GoogleItem {
    @JsonProperty("link")
    private String link;

    @JsonProperty("title")
    private String title;

    @JsonProperty("pagemap")
    private PageMap pageMap;


    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return pageMap.thumbnails.getFirst().url;
    }


    @Override
    public String toString() {
        return "Item{" +
                "link='" + link + '\'' +
                '}';
    }


    private static class PageMap {
        @JsonProperty("cse_thumbnail")
        private List<thumbnailAttributes> thumbnails;
    }

    private static class thumbnailAttributes {
        @JsonProperty("src")
        private String url;
    }
}
