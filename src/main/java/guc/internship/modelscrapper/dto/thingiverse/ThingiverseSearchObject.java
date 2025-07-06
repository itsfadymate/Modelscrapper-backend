package guc.internship.modelscrapper.dto.thingiverse;

import com.fasterxml.jackson.annotation.JsonProperty;
import guc.internship.modelscrapper.model.ModelPreview;

import java.util.List;

public class ThingiverseSearchObject {
    private String id;
    private String name;
    @JsonProperty("preview_image")
    private String thumbnail;
    @JsonProperty("public_url")
    private String publicUrl;
    @JsonProperty("make_count")
    private int makeCount;
    @JsonProperty("zip_data")
    private ZipData zipData;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getThumbnail() {
        return thumbnail;
    }
    
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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
    
    public List<ModelPreview.File> getFiles(){
        return this.zipData != null ? this.zipData.files : null;
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