
package ca.mudar.parkcatcher.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Post {
    protected static final String TAG = "Post";

    private final PostGeometry geometry;
    private final String type;
    private final PostProperties properties;
    private final int id;

    private LatLng latLng;
    private String desc;

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public Post(PostGeometry geometry, String type, PostProperties properties, int id) {
        this.geometry = geometry;
        this.type = type;
        this.properties = properties;
        this.id = id;

        initLatLng();
        initDesc();
    }

    public PostGeometry getGeometry() {
        return geometry;
    }

    public String getType() {
        return type;
    }

    public PostProperties getProperties() {
        return properties;
    }

    public int getId() {
        return id;
    }

    public LatLng getLatLng() {
        // Verification required to avoid null values when using Gson.fromJson()
        if (latLng == null) {
            initLatLng();
        }

        return latLng;
    }

    public String getDesc() {
        // Verification required to avoid null values when using Gson.fromJson()
        if (desc == null) {
            initDesc();
        }
        return desc;
    }

    @Override
    public String toString() {
        return String.format("(post: %s. type: %s. geometry: %s. properties: %s. latLng: %s)", id,
                type, geometry, properties, latLng);
    }

    /**
     * Initalize latLng value. Called in constructor and in getLatLng when
     * creating Post object using Gson.fromJson()
     */
    private void initLatLng() {
        List<Double> coordinates = geometry.getCoordinates();
        latLng = new LatLng(coordinates.get(1).doubleValue(), coordinates.get(0).doubleValue());
    }

    /**
     * Initalize desc value. Called in constructor and in getDesc when creating
     * Post object using Gson.fromJson()
     */
    private void initDesc() {
        desc = "";

        List<Panel> panels = properties.getPanels();
        int nbPanels = panels.size();
        for (int i = 0; i < nbPanels; i++) {
            Panel panel = panels.get(i);
            desc += panel.getDesc();

            // Add line separators, except for last item.
            if (i + 1 != nbPanels) {
                desc += LINE_SEPARATOR;
            }
        }
    }

}
