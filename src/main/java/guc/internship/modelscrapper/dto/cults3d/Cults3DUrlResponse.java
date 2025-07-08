package guc.internship.modelscrapper.dto.cults3d;

public class Cults3DUrlResponse {

    private Data data;

    public String getUrl() {
        return data.getCreation().getUrl();
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private Creation creation;

        public Creation getCreation() {
            return creation;
        }

        public void setCreation(Creation creation) {
            this.creation = creation;
        }
    }

    public static class Creation {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
