package guc.internship.modelscrapper.dto.myminifactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MyMiniFactoryDTO {
    
    @JsonProperty("id")
    private int id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("url")
    private String url;
    
    @JsonProperty("images")
    private List<Image> images;
    
    public static class Image {
        @JsonProperty("id")
        private int id;
        
        @JsonProperty("is_primary")
        private boolean isPrimary;
        
        @JsonProperty("thumbnail")
        private ImageMetaData thumbnail;
        
        public static class ImageMetaData {
            @JsonProperty("url")
            private String url;
            
            public String getUrl() {
                return url;
            }
            
            public void setUrl(String url) {
                this.url = url;
            }
            
        }
        
        public int getId() {
            return id;
        }
        
        public void setId(int id) {
            this.id = id;
        }
        
        public boolean isPrimary() {
            return isPrimary;
        }
        
        public void setPrimary(boolean primary) {
            isPrimary = primary;
        }
        
        public ImageMetaData getThumbnail() {
            return thumbnail;
        }
        
        public void setThumbnail(ImageMetaData thumbnail) {
            this.thumbnail = thumbnail;
        }
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
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
    
    public List<Image> getImages() {
        return images;
    }
    
    public void setImages(List<Image> images) {
        this.images = images;
    }
    

    public String getThumbnailUrl() {
        if (images != null && !images.isEmpty()) {
            for (Image image : images) {
                if (image.isPrimary() && image.getThumbnail() != null) {
                    return image.getThumbnail().getUrl();
                }
            }

            if (images.get(0).getThumbnail() != null) {
                return images.get(0).getThumbnail().getUrl();
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "MyMiniFactoryDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", images=" + images +
                '}';
    }
}