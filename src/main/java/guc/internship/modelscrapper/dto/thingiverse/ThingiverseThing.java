package guc.internship.modelscrapper.dto.thingiverse;

import com.fasterxml.jackson.annotation.JsonProperty;
import guc.internship.modelscrapper.model.ModelPreview;

import java.util.List;

public class ThingiverseThing {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("public_url")
    private String publicUrl;
    @JsonProperty("make_count")
    private int makeCount;
    @JsonProperty("default_image")
    private Images previewImages;
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
    @JsonProperty("zip_data")
    private ZipData zipData;
    @JsonProperty("error")
    private String error;

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

    public String getPreviewImages() {
        return previewImages.getThumbnail();
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

    public String getLicense(){return license; }

    public String getDescription(){return description;}

    public String getError() {
        return  error;
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

    private static class Images{
        @JsonProperty("sizes")
        List<Image> sizes;

        public void setSizes(List<Image> sizes) {
            this.sizes = sizes;
        }
        public String getThumbnail(){
            return sizes.getFirst().getUrl();
        }
    }
    private static class Image{
        @JsonProperty("size")
        private String size;
        @JsonProperty("url")
        private String url;

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
