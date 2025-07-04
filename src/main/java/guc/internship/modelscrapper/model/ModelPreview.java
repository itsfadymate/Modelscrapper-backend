package guc.internship.modelscrapper.model;

public class ModelPreview {
    private String imageLink;
    private String modelName;
    private String websiteName;
    private String websiteLink;

    public ModelPreview(){}

    public ModelPreview(String imageLink, String modelName, String websiteName, String websiteLink) {
        this.imageLink = imageLink;
        this.modelName = modelName;
        this.websiteName = websiteName;
        this.websiteLink = websiteLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public void setWebsiteName(String websiteName) {
        this.websiteName = websiteName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
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
}
