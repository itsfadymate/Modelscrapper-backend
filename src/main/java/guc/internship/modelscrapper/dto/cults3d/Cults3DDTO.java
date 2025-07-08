package guc.internship.modelscrapper.dto.cults3d;

import com.fasterxml.jackson.annotation.JsonProperty;
import guc.internship.modelscrapper.model.ModelPreview;

import java.util.List;

public class Cults3DDTO {
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("illustrationImageUrl")
    private String illustrationImageUrl;
    
    @JsonProperty("blueprints")
    private List<ModelPreview.File> files;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("details")
    private String details;

    @JsonProperty("price")
    private Price price;

    @JsonProperty("makes")
    private List<Object> makes;

    @JsonProperty("comments")
    private List<Object> commentCount;

    @JsonProperty("likesCount")
    private String likesCount;

    @JsonProperty("featured")
    private boolean featured;

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
    
    public List<ModelPreview.File> getFiles() {
        return files;
    }
    
    public void setFiles(List<ModelPreview.File> files) {
        this.files = files;
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

    public void setPrice(Price price) {
        this.price = price;
    }

    public String getFormattedPrice(){
        return this.price.getFormatted();
    }

    public void setMakes(List<Object> makes) {
        this.makes = makes;
    }

    public int getMakeCount(){
        return this.makes.size();
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(String likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentCount() {
        return commentCount.size();
    }

    public void setCommentCount(List<Object> commentCount) {
        this.commentCount = commentCount;
    }

    @Override
    public String toString() {
        return "Cults3DDTO{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", illustrationImageUrl='" + illustrationImageUrl + '\'' +
                ", blueprints=" + files +
                ", description='" + description + '\'' +
                ", details='" + details + '\'' +
                '}';
    }


     public static class Price {
        private String formatted;

        public void setFormatted(String formatted) {
            this.formatted = formatted;
        }
        public String getFormatted(){
            return formatted;
        }
    }
}
