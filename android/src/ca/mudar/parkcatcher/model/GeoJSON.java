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
