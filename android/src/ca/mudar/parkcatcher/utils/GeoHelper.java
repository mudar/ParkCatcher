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

import java.io.IOException;
import java.util.List;

public class GeoHelper {
    private static final String TAG = "GeoHelper";
    private static final int MAX_RESULTS = 5;

    public static Address findAddressFromName(Context c, String name) throws IOException {
        Geocoder geocoder = new Geocoder(c);
        List<Address> adr;

        adr = geocoder.getFromLocationName(name, MAX_RESULTS, Const.MAPS_GEOCODER_LIMITS[0],
                Const.MAPS_GEOCODER_LIMITS[1], Const.MAPS_GEOCODER_LIMITS[2],
                Const.MAPS_GEOCODER_LIMITS[3]);

        if (!adr.isEmpty()) {
            final int nbAdresses = adr.size();

            for (int i = 0; i < nbAdresses; i++) {
                Address address = adr.get(i);

                if (address.hasLatitude() && address.hasLongitude()) {
                    if (Double.compare(address.getLatitude(), Const.MAPS_GEOCODER_LIMITS[0]) >= 0
                            &&
                            Double.compare(address.getLongitude(), Const.MAPS_GEOCODER_LIMITS[1]) >= 0
                            &&
                            Double.compare(address.getLatitude(), Const.MAPS_GEOCODER_LIMITS[2]) <= 0
                            &&
                            Double.compare(address.getLongitude(), Const.MAPS_GEOCODER_LIMITS[3]) <= 0) {

                        return address;
                    }
                }
            }
        }

        return null;
    }

    public static String findAddressFromLocation(Context c, double latitude, double longitude)
            throws IOException {
        Geocoder geocoder = new Geocoder(c);

        List<Address> adr;

        adr = geocoder.getFromLocation(latitude, longitude, 1);

        if (!adr.isEmpty() && (adr.size() == 1)) {
            Address address = adr.get(0);
            int nbLines =address.getMaxAddressLineIndex();
            
            String sAddress = "";
            for ( int i = 0; i < nbLines ; i++) {
                sAddress += address.getAddressLine(i);
                if ( i +1 < nbLines ) {
                    sAddress += " " + Const.LINE_SEPARATOR;
                }
            }
            
            return sAddress;
        }

        return null;
    }
}
