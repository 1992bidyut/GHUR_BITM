package bdnath.lictproject.info.ghur.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerItem implements ClusterItem {
    private LatLng latLng;
    private String title;
    private String snippet;

    public MarkerItem(LatLng latLng, String title) {
        this.latLng = latLng;
        this.title = title;
    }

    public MarkerItem(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}
