package guc.internship.modelscrapper.dto.myminifactory;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MyMiniFactoryObjectMakes {
    @JsonProperty("total_count")
    private int makesCount;

    public void setMakesCount(int makesCount) {
        this.makesCount = makesCount;
    }

    public int getMakesCount() {
        return makesCount;
    }
}
