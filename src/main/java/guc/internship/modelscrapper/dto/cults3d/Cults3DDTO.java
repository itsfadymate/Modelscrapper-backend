package guc.internship.modelscrapper.dto.cults3d;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Cults3DDTO {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("illustrationImageUrl")
    private String illustrationImageUrl;
    
    @JsonProperty("blueprints")
    private List<Blueprint> blueprints;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("details")
    private String details;
    

    public static class Blueprint {
        @JsonProperty("fileName")
        private String fileName;
        
        public String getFileName() {
            return fileName;
        }
        
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
    

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public String getIllustrationImageUrl() {
        return illustrationImageUrl;
    }
    
    public void setIllustrationImageUrl(String illustrationImageUrl) {
        this.illustrationImageUrl = illustrationImageUrl;
    }
    
    public List<Blueprint> getBlueprints() {
        return blueprints;
    }
    
    public void setBlueprints(List<Blueprint> blueprints) {
        this.blueprints = blueprints;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    @Override
    public String toString() {
        return "Cults3DDTO{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", illustrationImageUrl='" + illustrationImageUrl + '\'' +
                ", blueprints=" + blueprints +
                ", description='" + description + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
