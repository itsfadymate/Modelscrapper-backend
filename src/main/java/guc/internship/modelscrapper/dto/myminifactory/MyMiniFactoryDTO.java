package guc.internship.modelscrapper.dto.myminifactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import guc.internship.modelscrapper.model.ModelPreview;

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

    @JsonProperty("files")
    private Files files;

    private Makes prints;

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

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public void setFiles(Files files) {
        this.files = files;
    }

    public List<ModelPreview.File> getFiles(){
        return this.files.getItems();
    }

    public String getPreviewImageUrl() {
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

    public void setPrints(Makes prints) {
        this.prints = prints;
    }

    public int getMakesCount(){
        return this.prints.getMakeCount();
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

    private static class Image {
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

    private static class Files{
        @JsonProperty("items")
        private List<ModelPreview.File> items;
        public void setItems(List<ModelPreview.File> items) {
            this.items = items;
        }

        public List<ModelPreview.File> getItems() {
            return items;
        }
    }

    private static class Makes {
        @JsonProperty("total_count")
        private int makeCount;

        public void setMakeCount(int makeCount) {
            this.makeCount = makeCount;
        }

        public int getMakeCount() {
            return makeCount;
        }
    }
}