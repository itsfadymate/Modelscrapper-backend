package guc.internship.modelscrapper.dto.thingiverse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ThingiverseSearchObject {
    private String id;
    @JsonProperty("preview_image")
    private String previewImage;

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getPreviewImage() {
        return previewImage;
    }
    
    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }

}