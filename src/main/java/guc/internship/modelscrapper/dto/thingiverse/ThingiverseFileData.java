package guc.internship.modelscrapper.dto.thingiverse;

import com.fasterxml.jackson.annotation.JsonProperty;
import guc.internship.modelscrapper.model.ModelPreview;

import java.util.List;

public class ThingiverseFileData {


    @JsonProperty("zip_data")
    private ZipData zipData;


    public List<ModelPreview.File> getFiles() {
        return zipData.getFiles();
    }

    public void setZipData(ZipData zipData) {
        this.zipData = zipData;
    }


    private static class ZipData{
        private List<ModelPreview.File> files;

        public List<ModelPreview.File> getFiles() {
            return files;
        }
        public void setFiles(List<ModelPreview.File> files) {
            this.files = files;
        }
    }
}
