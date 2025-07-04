package guc.internship.modelscrapper.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Cults3DSearchResponse {
    
    @JsonProperty("data")
    private DataWrapper data;
    
    public static class DataWrapper {
        @JsonProperty("search")
        private List<Cults3DDTO> search;
        
        public List<Cults3DDTO> getSearch() {
            return search;
        }
        
        public void setSearch(List<Cults3DDTO> search) {
            this.search = search;
        }
    }
    
    public DataWrapper getData() {
        return data;
    }
    
    public void setData(DataWrapper data) {
        this.data = data;
    }
 
    public List<Cults3DDTO> getSearchResults() {
        return data != null ? data.getSearch() : null;
    }
}