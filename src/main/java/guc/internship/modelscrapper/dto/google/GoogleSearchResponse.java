package guc.internship.modelscrapper.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GoogleSearchResponse {

    @JsonProperty("items")
    private List<Item> items;

    public List<String> getIds(){
        return items.stream().map(Item::getID).toList();
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "GoogleSearchResponse{" +
                "items=" + items +
                '}';
    }

    static class Item {
        @JsonProperty("link")
        private String link;

        public String getID() {
            return link.substring(link.lastIndexOf(":")+1);
        }

        public void setLink(String link) {
            this.link = link;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "link='" + link + '\'' +
                    '}';
        }
    }
}
