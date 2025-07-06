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
