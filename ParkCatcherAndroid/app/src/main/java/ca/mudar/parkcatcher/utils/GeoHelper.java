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
import ca.mudar.parkcatcher.Const.UnitsDisplay;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;

import android.content.Context;
import android.content.res.Resources;
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
            int nbLines = address.getMaxAddressLineIndex();

            String sAddress = "";
            for (int i = 0; i < nbLines; i++) {
                sAddress += address.getAddressLine(i);
                if (i + 1 < nbLines) {
                    sAddress += " " + Const.LINE_SEPARATOR;
                }
            }

            return sAddress;
        }

        return null;
    }

    /**
     * Get distance in Metric or Imperial units. Display changes depending on
     * the value: different approximationss in ft when > 1000. Very short
     * distances are not displayed to avoid problems with Location accuracy.
     * 
     * @param c
     * @param fDistanceM The distance in Meters.
     * @return String Display the distance.
     */
    public static String getDistanceDisplay(Context c, float fDistanceM) {
        String sDistance;

        ParkingApp parkingApp = (ParkingApp) c.getApplicationContext();
        Resources res = c.getResources();
        String units = parkingApp.getUnits();

        if (units.equals(Const.PrefsValues.UNITS_IMP)) {
            /**
             * Imperial units system, Miles and Feet.
             */

            float fDistanceMi = fDistanceM / UnitsDisplay.METER_PER_MILE;

            if (fDistanceMi + (UnitsDisplay.ACCURACY_FEET_FAR / UnitsDisplay.FEET_PER_MILE) < 1) {
                /**
                 * Display distance in Feet if less than one mile.
                 */
                int iDistanceFt = Math.round(fDistanceMi * UnitsDisplay.FEET_PER_MILE);

                if (iDistanceFt <= UnitsDisplay.MIN_FEET) {
                    /**
                     * Display "Less than 200 ft", which is +/- equal to the GPS
                     * accuracy.
                     */
                    sDistance = res.getString(R.string.park_distance_imp_min);
                }
                else {
                    /**
                     * When displaying in feet, we round up by 100 ft for
                     * distances greater than 1000 ft and by 100 ft for smaller
                     * distances. Example: 1243 ft becomes 1200 and 943 ft
                     * becomes 940 ft.
                     */
                    if (iDistanceFt > 1000) {
                        iDistanceFt = Math.round(iDistanceFt / UnitsDisplay.ACCURACY_FEET_FAR)
                                * UnitsDisplay.ACCURACY_FEET_FAR;
                    }
                    else {
                        iDistanceFt = Math.round(iDistanceFt / UnitsDisplay.ACCURACY_FEET_NEAR)
                                * UnitsDisplay.ACCURACY_FEET_NEAR;
                    }
                    sDistance = String.format(res.getString(R.string.park_distance_imp_feet),
                            iDistanceFt);
                }
            }
            else {
                /**
                 * Display distance in Miles when greater than 1 mile.
                 */
                sDistance = String.format(res.getString(R.string.park_distance_imp),
                        fDistanceMi);
            }
        }
        else {
            /**
             * International Units system, Meters and Km.
             */

            if (fDistanceM <= UnitsDisplay.MIN_METERS) {
                /**
                 * Display "Less than 100 m".
                 */
                sDistance = res.getString(R.string.park_distance_iso_min);
            }
            else {
                /**
                 * No need to have a constant for 1 Km = 1000 M
                 */
                float fDistanceKm = (fDistanceM / 1000);
                sDistance = String
                        .format(res.getString(R.string.park_distance_iso), fDistanceKm);
            }
        }

        return sDistance;
    }

}
