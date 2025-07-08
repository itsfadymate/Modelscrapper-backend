package guc.internship.modelscrapper.dto.thingiverse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ThingiverseSearchObject {
    private String id;
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
    private int commentCount;
    @JsonProperty("is_featured")
    private boolean isFeatured;
    @JsonProperty("is_nsfw")
    private boolean isNsfw;

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMakeCount(int makeCount) {
        this.makeCount = makeCount;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public void setNsfw(boolean nsfw) {
        isNsfw = nsfw;
    }

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

    public int getCommentCount() {
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
}