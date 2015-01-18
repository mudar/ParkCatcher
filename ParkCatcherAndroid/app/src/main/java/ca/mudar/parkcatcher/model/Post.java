/*
    Park Catcher Montréal
    Find a free parking in the nearest residential street when driving in
    Montréal. A Montréal Open Data project.

    Copyright (C) 2012 Mudar Noufal <mn@mudar.ca>

    This file is part of Park Catcher Montréal.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.mudar.parkcatcher.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import ca.mudar.parkcatcher.Const;

public class Post {
    private static final String TAG = "Post";

    private final PostGeometry geometry;
    private final String type;
    private final PostProperties properties;
    private final int id;

    private LatLng latLng;
    private String desc;

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
        latLng = new LatLng(coordinates.get(1), coordinates.get(0));
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
                desc += Const.LINE_SEPARATOR;
            }
        }
    }

}
