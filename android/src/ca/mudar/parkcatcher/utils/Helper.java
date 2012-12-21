/*
    Patiner Montréal for Android.
    Information about outdoor rinks in the city of Montréal: conditions,
    services, contact, map, etc.

    Copyright (C) 2010 Mudar Noufal <mn@mudar.ca>

    This file is part of Patiner Montréal for Android.

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
import android.location.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

// TODO Refactor this into geohelper and iohelper
public class Helper {
    @SuppressWarnings("unused")
    private static final String TAG = "Helper";

    public static int getRinkImage(int kindId, int condition) {
        return 0;
    }

    public static int getConditionBackground(int condition) {
        int bgResource = 0;

        return bgResource;
    }

    public static int getConditionTextColor(int condition) {

        return 0;
    }

    public static String getSqliteConditionsFilter(boolean[] conditionsFilter) {

        return null;

    }

    public static String capitalize(final String string)
    {
        if (string == null)
            throw new NullPointerException("string");
        if (string.equals(""))
            throw new NullPointerException("string");

        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }

    public static String inputStreamToString(InputStream inputStream) {
        BufferedReader r;
        String resultString = "";
        try {
            r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            resultString = total.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultString;
    }

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

        ParkingApp appHelper = (ParkingApp) c.getApplicationContext();
        Resources res = c.getResources();
        String units = appHelper.getUnits();

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

    /**
     * Convert Location to GeoPoint
     * 
     * @param Location
     * @return GeoPoint
     */
//    public static GeoPoint locationToGeoPoint(Location location) {
//        if (location == null) {
//            return null;
//        }
//        GeoPoint geoPoint = new GeoPoint((int) (location.getLatitude() * 1E6),
//                (int) (location.getLongitude() * 1E6));
//
//        return geoPoint;
//    }

    /**
     * Convert GeoPoint to Location
     * 
     * @param GeoPoint
     * @return Location
     */
//    public static Location geoPointToLocation(GeoPoint geoPoint) {
//        Location location = new Location(Const.LOCATION_PROVIDER);
//
//        location.setLatitude(geoPoint.getLatitudeE6() / 1E6);
//        location.setLongitude(geoPoint.getLongitudeE6() / 1E6);
//
//        return location;
//    }
}
