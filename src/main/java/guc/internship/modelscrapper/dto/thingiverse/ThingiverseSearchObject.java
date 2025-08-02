package guc.internship.modelscrapper.dto.thingiverse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ThingiverseSearchObject {
    @JsonProperty
    private String id;
    @JsonProperty
    private String name;
    @JsonProperty("public_url")
    private String publicUrl;
    @JsonProperty("make_count")
    private int makeCount;
    @JsonProperty("preview_image")
    private String previewImage;
    @JsonProperty("like_count")
    private String likeCount;
    @JsonProperty("comment_count")
    private String commentCount;
    @JsonProperty("is_featured")
    private boolean isFeatured;
    @JsonProperty("is_nsfw")
    private boolean isNsfw;
    @JsonProperty("license")
    private String license;
    @JsonProperty("description")
    private String description;



    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public int getMakeCount() {
        return makeCount;
    }

    public String getPreviewImage() {
        return previewImage;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public boolean isNsfw() {
        return isNsfw;
    }

    public String getLicense() {
        return license;
    }

    public String getDescription() {
        return description;
    }
}