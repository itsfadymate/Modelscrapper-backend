package guc.internship.modelscrapper.model;

import java.util.List;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonAlias;

public class ModelPreview {
    private String id;
    private String imageLink;
    private String modelName;
    private String websiteName;
    private String websiteLink;
    private String price;
    private int makesCount;
    private List<ModelPreview.File> files;
    private String likesCount;
    private int commentsCount;
    private boolean isFeatured;



    public ModelPreview() {
        files = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public ModelPreview setId(String id) {
        this.id = id;
        return this;
    }

    public ModelPreview setImageLink(String imageLink) {
        this.imageLink = imageLink;
        return this;
    }

    public ModelPreview setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
        return this;
    }

    public ModelPreview setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
        return this;
    }

    public ModelPreview setModelName(String modelName) {
        this.modelName = modelName;
        return this;
    }
    public ModelPreview setPrice(String formattedPrice) {
        this.price = formattedPrice;
        return this;
    }


    public ModelPreview setMakesCount(int makesCount) {
        this.makesCount = makesCount;
        return this;
    }

    public ModelPreview setFiles(List<ModelPreview.File> files) {
        this.files = files;
        return this;
    }

    public ModelPreview setLikesCount(String likesCount) {
        this.likesCount = likesCount;
        return this;
    }

    public ModelPreview setFeatured(boolean featured) {
        isFeatured = featured;
        return this;
    }

    public ModelPreview setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
        return this;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getModelName() {
        return modelName;
    }

    public String getWebsiteName() {
        return websiteName;
    }

    public String getWebsiteLink() {
        return websiteLink;
    }
    public String getPrice() {
        return price;
    }
    public int getMakesCount() {
        return makesCount;
    }

    public List<ModelPreview.File> getFiles() {
        return files;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public static class File {
        @JsonAlias({"name","fileName","filename"})
        private String name;
        @JsonAlias({"url","download_url"})
        private String downloadUrl;

        public File() {}

        public File(String name, String downloadUrl) {
            this.name = name;
            this.downloadUrl = downloadUrl;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public String getName() {
            return name;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }
    }


}
