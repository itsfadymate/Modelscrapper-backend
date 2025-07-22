package guc.internship.modelscrapper.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GoogleSearchResponse {

    @JsonProperty("items")
    private List<GoogleItem> items;


    public List<String> getLinks(){
        return items.stream().map(GoogleItem::getLink).toList();
    }

    public List<GoogleItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "GoogleSearchResponse{" +
                "items=" + items +
                '}';
    }


}
