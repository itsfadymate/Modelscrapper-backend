package guc.internship.modelscrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ThingiverseThing {
    private String id;
    private String name;
    @JsonProperty("preview_image")
    private String thumbnail;
    @JsonProperty("public_url")
    private String publicUrl;
    
    
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
    
    public String getUrl() {
        return publicUrl;
    }
    
    public void setUrl(String url) {
        this.publicUrl = url;
    }
}