package guc.internship.modelscrapper.dto.sketchfab;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SketchfabSearchObject {
    @JsonProperty("uid")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("staffPickedAt")
    private String featuredDate;
    @JsonProperty("likeCount")
    private String likesCount;
    @JsonProperty("commentCount")
    private String commentCount;
    @JsonProperty("isAgeRestricted")
    private boolean isNsfw;
    @JsonProperty("thumbnails")
    private Thumbnails thumbnails;
    @JsonProperty("viewerUrl")
    private String viewerUrl;
    @JsonProperty("isDownloadable")
    private boolean isDownloadable;
    @JsonProperty("embedUrl")
    private String embedUrl;
    @JsonProperty("price")
    private String price;
    @JsonProperty("license")
    private License license;
    @JsonProperty("description")
    private String description;


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public boolean isNsfw() {
        return isNsfw;
    }

    public String getUrl() {
        return viewerUrl;
    }

    public boolean isFeatured(){
        return this.featuredDate!=null;
    }

    public String getPreviewImageUrl(){
        return this.thumbnails.getPreviewImageUrl();
    }

    public boolean isDownloadable() {
        return isDownloadable;
    }

    public String getEmbedUrl() {
        return embedUrl;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getLicense() {
        return license.fullName;
    }

    static class Thumbnails {
        @JsonProperty("images")
        private List<Images> images;

        public String getPreviewImageUrl() {
            return images.get(images.size()-2).getUrl();
        }

        public void setImages(List<Images> images) {
            this.images = images;
        }

        public static class Images {
            @JsonProperty("url")
            private String url;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }

    static class License{
        @JsonProperty("fullName")
        private String fullName;
    }
}
