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

package ca.mudar.parkcatcher.utils;

import ca.mudar.parkcatcher.Const;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.util.List;

public class GeoHelp {
    public static Location findLocatioFromName(Context c, String name) throws IOException {
        Geocoder geocoder = new Geocoder(c);
        List<Address> adr;

        adr = geocoder.getFromLocationName(name, 10, Const.MAPS_GEOCODER_LIMITS[0],
                Const.MAPS_GEOCODER_LIMITS[1], Const.MAPS_GEOCODER_LIMITS[2],
                Const.MAPS_GEOCODER_LIMITS[3]);
        if (!adr.isEmpty()) {
            Address address = adr.get(0);
            if (((int) address.getLatitude() != 0) && ((int) address.getLongitude() != 0)) {
                Location location = new Location("mylocation");
                location.setLatitude(address.getLatitude());
                location.setLongitude(address.getLongitude());

                return location;
            }
        }

        return null;
    }
}
