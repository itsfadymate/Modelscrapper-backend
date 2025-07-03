package guc.internship.modelscrapper.model;

public class Model3D {
    private String imagelink;
    private String modelname;
    private String websitename;
    private String websitelink;

    public Model3D(){}

    public Model3D(String imagelink, String modelname, String websitename, String websitelink) {
        this.imagelink = imagelink;
        this.modelname = modelname;
        this.websitename = websitename;
        this.websitelink = websitelink;
    }

    public void setImagelink(String imagelink) {
        this.imagelink = imagelink;
    }

    public void setWebsitelink(String websitelink) {
        this.websitelink = websitelink;
    }

    public void setWebsitename(String websitename) {
        this.websitename = websitename;
    }

    public void setModelname(String modelname) {
        this.modelname = modelname;
    }

    public String getImagelink() {
        return imagelink;
    }

    public String getModelname() {
        return modelname;
    }

    public String getWebsitename() {
        return websitename;
    }

    public String getWebsitelink() {
        return websitelink;
    }
}
