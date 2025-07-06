package guc.internship.modelscrapper.model;

public class ModelPreview {
    private String imageLink;
    private String modelName;
    private String websiteName;
    private String websiteLink;
    private String downloadLink;
    private double price;
    private int makesCount;
    private String fileTypes;
    private String totalFilesSize;

    public ModelPreview(){}

    public ModelPreview(String imageLink, String modelName, String websiteName, String websiteLink, double price, int makesCount, String fileTypes, String totalFilesSize) {
        this.imageLink = imageLink;
        this.modelName = modelName;
        this.websiteName = websiteName;
        this.websiteLink = websiteLink;
        this.price = price;
        this.makesCount = makesCount;
        this.downloadLink = "";
        this.fileTypes = fileTypes;
        this.totalFilesSize = totalFilesSize;
    }
    public ModelPreview(String imageLink, String modelName, String websiteName, String websiteLink, double price, int makesCount, String fileTypes, String totalFilesSize, String downloadLink) {
        this(imageLink, modelName, websiteName, websiteLink, price, makesCount, fileTypes, totalFilesSize);
        this.downloadLink = downloadLink;
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
    public void setPrice(double price) {
        this.price = price;
    }
    public void setMakesCount(int makesCount) {
        this.makesCount = makesCount;
    }
    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }
    public void setFileTypes(String fileTypes) {
        this.fileTypes = fileTypes;
    }
    public void setTotalFilesSize(String totalFilesSize) {
        this.totalFilesSize = totalFilesSize;
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
    public String getDownloadLink() {
        return downloadLink;
    }
    public String getFileTypes() {
        return fileTypes;
    }
    public String getTotalFilesSize() {
        return totalFilesSize;
    }

}
