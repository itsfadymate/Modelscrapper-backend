package guc.internship.modelscrapper.dto.thingiverse;

import com.fasterxml.jackson.annotation.JsonProperty;
import guc.internship.modelscrapper.model.ModelPreview;

import java.util.List;

public class ThingiverseThing {

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
    private String commentCount;
    @JsonProperty("is_featured")
    private boolean isFeatured;
    @JsonProperty("is_nsfw")
    private boolean isNsfw;

    @JsonProperty("zip_data")
    private ZipData zipData;

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

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
    }

    public void setNsfw(boolean nsfw) {
        isNsfw = nsfw;
    }

    public void setZipData(ZipData zipData) {
        this.zipData = zipData;
    }

    public List<ModelPreview.File> getFiles() {
        return zipData.getFiles();
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


    private static class ZipData{
        private List<ModelPreview.File> files;

        public List<ModelPreview.File> getFiles() {
            return files;
        }
        public void setFiles(List<ModelPreview.File> files) {
            this.files = files;
        }
    }
}
