package guc.internship.modelscrapper.dto.thingiverse;

import com.fasterxml.jackson.annotation.JsonProperty;
import guc.internship.modelscrapper.model.ModelPreview;

import java.util.List;

public class ThingiverseThing {

    private String name;
    @JsonProperty("public_url")
    private String publicUrl;
    @JsonProperty("make_count")
    private int makeCount;
    @JsonProperty("zip_data")
    private ZipData zipData;
    @JsonProperty("like_count")
    private String likeCount;
    @JsonProperty("comment_count")
    private int commentCount;
    @JsonProperty("is_featured")
    private boolean isFeatured;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

    public int getMakeCount() {
        return makeCount;
    }

    public void setMakeCount(int makeCount) {
        this.makeCount = makeCount;
    }

    public List<ModelPreview.File> getFiles() {
        return zipData.getFiles();
    }

    public void setZipData(ZipData zipData) {
        this.zipData = zipData;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
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
