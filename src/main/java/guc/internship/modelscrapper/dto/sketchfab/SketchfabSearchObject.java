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
    private int commentCount;
    @JsonProperty("isAgeRestricted")
    private boolean isNsfw;
    @JsonProperty("thumbnails")
    private Thumbnails thumbnails;
    @JsonProperty("viewerUrl")
    private String viewerUrl;
    @JsonProperty("isDownloadable")
    private boolean isDownloadable;

    public void setId(String id) {
        this.id = id;
    }

    public void setViewerUrl(String viewerUrl) {
        this.viewerUrl = viewerUrl;
    }

    public void setThumbnails(Thumbnails thumbnails) {
        this.thumbnails = thumbnails;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public void setNsfw(boolean nsfw) {
        isNsfw = nsfw;
    }

    public void setLikesCount(String likesCount) {
        this.likesCount = likesCount;
    }

    public void setFeaturedDate(String featuredDate) {
        this.featuredDate = featuredDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDownloadable(boolean downloadable) {
        isDownloadable = downloadable;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public int getCommentCount() {
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

    public static class Thumbnails {
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
}
