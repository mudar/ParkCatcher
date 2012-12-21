
package ca.mudar.parkcatcher.models;

import java.util.List;

public class PostGeometry {
    private final String type;
    private final List<Double> coordinates;

    public PostGeometry(String type, List<Double> coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    @Override
    public String toString() {
        return String.format("(type: %s. coordinates: %s)", type, coordinates);
    }
}
