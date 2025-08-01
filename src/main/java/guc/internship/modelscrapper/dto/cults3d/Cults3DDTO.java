package guc.internship.modelscrapper.dto.cults3d;

import com.fasterxml.jackson.annotation.JsonProperty;
import guc.internship.modelscrapper.model.ModelPreview;

import java.util.List;

public class Cults3DDTO {
    @JsonProperty("slug")
    private String slug;
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

    @JsonProperty("license")
    private License license;

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getIllustrationImageUrl() {
        return illustrationImageUrl;
    }
    
    public List<ModelPreview.File> getFiles() {
        return files;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getDetails() {
        return details;
    }

    public String getFormattedPrice(){
        return this.price.getFormatted();
    }

    public int getMakeCount(){
        return this.makes.size();
    }

    public boolean isFeatured() {
        return featured;
    }

    public String getLikesCount() {
        return likesCount;
    }

    public int getCommentCount() {
        return commentCount.size() ;
    }

    public String getLicenseName(){
        return license.name;
    }

    public boolean isForCommercialUse(){
        return license.allowsCommercialUse;
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


    static class Price {
        @JsonProperty("formatted")
        private String formatted;

        public String getFormatted(){
            return formatted;
        }
    }

    static class License{
        @JsonProperty("name")
        private String name;
        @JsonProperty("allowsCommercialUse")
        private boolean allowsCommercialUse;
    }
}
