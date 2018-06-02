package bdnath.lictproject.info.ghur.FireBasePojoClass;

import bdnath.lictproject.info.ghur.R;

public class GalleryHandeler {
    private String url;
    private String ptohoID;
    private String localUrl;

    public GalleryHandeler(String url, String ptohoID, String localUrl) {
        this.url = url;
        this.ptohoID = ptohoID;
        this.localUrl = localUrl;
    }

    public String getLocalUrl() {

        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public GalleryHandeler() {
    }

    public GalleryHandeler(String url, String ptohoID) {
        this.url = url;
        this.ptohoID = ptohoID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPtohoID() {
        return ptohoID;
    }

    public void setPtohoID(String ptohoID) {
        this.ptohoID = ptohoID;
    }
}
