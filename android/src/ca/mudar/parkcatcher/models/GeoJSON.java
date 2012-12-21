
package ca.mudar.parkcatcher.models;

import java.util.List;

public class GeoJSON {
    private final String status;
    private final String name;
    private final String type;
    private final int count;
    private final List<Post> features;

    public GeoJSON(String status, String name, String type, int count, List<Post> features) {
        this.status = status;
        this.name = name;
        this.type = type;
        this.count = count;
        this.features = features;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public List<Post> getFeatures() {
        return features;
    }

    @Override
    public String toString() {
        return String.format("(status: %s. name: %s. type: %s. count: %s. features: %s)",
                status, name, type, count, features);
    }
}
