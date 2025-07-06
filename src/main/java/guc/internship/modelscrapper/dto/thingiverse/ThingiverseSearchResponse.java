package guc.internship.modelscrapper.dto.thingiverse;

import java.util.List;

public class ThingiverseSearchResponse {
    private List<ThingiverseSearchObject> hits;
    private int total;
    
    public List<ThingiverseSearchObject> getHits() {
        return hits;
    }
    
    public void setHits(List<ThingiverseSearchObject> hits) {
        this.hits = hits;
    }
    
    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
}