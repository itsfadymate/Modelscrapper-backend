package guc.internship.modelscrapper.dto;

import java.util.List;

public class ThingiverseSearchResponse {
    private List<ThingiverseThing> hits;
    private int total;
    
    public List<ThingiverseThing> getHits() {
        return hits;
    }
    
    public void setHits(List<ThingiverseThing> hits) {
        this.hits = hits;
    }
    
    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
}