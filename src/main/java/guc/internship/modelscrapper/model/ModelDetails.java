package guc.internship.modelscrapper.model;

import java.util.List;

public class ModelDetails {
    private String license;
    private String description;
    private List<ModelPreview.File> files;

    public ModelDetails(String license, String description, List<ModelPreview.File> files) {
        this.license = license;
        this.description = description;
        this.files = files;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ModelPreview.File> getFiles() {
        return files;
    }

    public void setFiles(List<ModelPreview.File> files) {
        this.files = files;
    }
}
