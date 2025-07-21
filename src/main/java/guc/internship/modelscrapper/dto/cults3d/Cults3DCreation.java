package guc.internship.modelscrapper.dto.cults3d;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cults3DCreation {
    @JsonProperty("data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data{
        @JsonProperty("creation")
        private Cults3DDTO creation;

        public Cults3DDTO getCreation() {
            return creation;
        }

        public void setCreation(Cults3DDTO creation) {
            this.creation = creation;
        }
    }
}
