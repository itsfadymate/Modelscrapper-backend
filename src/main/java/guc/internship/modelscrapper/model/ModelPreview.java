package guc.internship.modelscrapper.model;

import java.util.List;
import java.util.ArrayList;

public class ModelPreview {
    private String imageLink;
    private String modelName;
    private String websiteName;
    private String websiteLink;
    private double price;
    private int makesCount;
    private List<File> files = new ArrayList<>();


    public static class File {
        private String name;
        private String downloadUrl;

        public File() {}

        public File(String name, String downloadUrl) {
            this.name = name;
            this.downloadUrl = downloadUrl;
        }

        public File setName(String name) {
            this.name = name;
            return this;
        }

        public File setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
            return this;
        }

        public String getName() {
            return name;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }
    }

    public ModelPreview() {}

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
    public ModelPreview setPrice(double price) {
        this.price = price;
        return this;
    }
    public ModelPreview setMakesCount(int makesCount) {
        this.makesCount = makesCount;
        return this;
    }

    public ModelPreview setFiles(List<File> files) {
        this.files = files;
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
    public double getPrice() {
        return price;
    }
    public int getMakesCount() {
        return makesCount;
    }

    public List<File> getFiles() {
        return files;
    }

}
