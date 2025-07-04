package guc.internship.modelscrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MyMiniFactorySearchResponse {
    
    @JsonProperty("total_count")
    private int totalCount;
    
    @JsonProperty("items")
    private List<MyMiniFactoryDTO> items;
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
    
    public List<MyMiniFactoryDTO> getItems() {
        return items;
    }
    
    public void setItems(List<MyMiniFactoryDTO> items) {
        this.items = items;
    }
    
    @Override
    public String toString() {
        return "MyMiniFactorySearchResponse{" +
                "totalCount=" + totalCount +
                ", items=" + items +
                '}';
    }
}