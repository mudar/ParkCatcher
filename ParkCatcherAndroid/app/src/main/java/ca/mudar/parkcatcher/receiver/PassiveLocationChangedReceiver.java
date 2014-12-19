/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Modifications:
 * - Copied from radioactiveyak.location_best_practices
 * - Renamed package
 * - Change type from long to float for lastLat and lastLng
 * - Verify value type, lines 81-83
 */

package ca.mudar.parkcatcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.service.DistanceUpdateService;
import ca.mudar.parkcatcher.utils.LegacyLastLocationFinder;

/**
 * This Receiver class is used to listen for Broadcast Intents that announce
 * that a location change has occurred while this application isn't visible.
 * Where possible, this is triggered by a Passive Location listener.
 */
public class PassiveLocationChangedReceiver extends BroadcastReceiver {

    protected static String TAG = "PassiveLocationChangedReceiver";
    protected SharedPreferences prefs;
    protected SharedPreferences.Editor prefsEditor;

    /**
     * When a new location is received, extract it from the Intent and use it to
     * start the Service used to update the list of nearby places. This is the
     * Passive receiver, used to receive Location updates from third party apps
     * when the Activity is not visible.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String key = LocationManager.KEY_LOCATION_CHANGED;
        Location location = null;

        if (intent.hasExtra(key)) {
            // This update came from Passive provider, so we can extract the
            // location directly.
            location = (Location) intent.getExtras().get(key);
        }
        else {
            // This update came from a recurring alarm. We need to determine if
            // there has been a more recent Location received than the last
            // location we used.

            // Get the best last location detected from the providers.
            LegacyLastLocationFinder lastLocationFinder = new LegacyLastLocationFinder(context);
            location = lastLocationFinder.getLastBestLocation(Const.MAX_DISTANCE,
                    System.currentTimeMillis() - Const.MAX_TIME);
            SharedPreferences prefs = context.getSharedPreferences(Const.APP_PREFS_NAME,
                    Context.MODE_PRIVATE);

            // Get the last location we used to get a listing.
            long lastTime = prefs.getLong(Const.PrefsNames.LAST_UPDATE_TIME_GEO, Long.MIN_VALUE);
            Float lastLat = prefs.getFloat(Const.PrefsNames.LAST_UPDATE_LAT, Float.NaN);
            Float lastLng = prefs.getFloat(Const.PrefsNames.LAST_UPDATE_LNG, Float.NaN);

            if (lastLat.equals(Float.NaN) || lastLng.equals(Float.NaN)) {
                return;
            }

            Location lastLocation = new Location(Const.LOCATION_PROVIDER_SERVICE);
            lastLocation.setLatitude(lastLat.doubleValue());
            lastLocation.setLongitude(lastLng.doubleValue());

            // Check if the last location detected from the providers is either
            // too soon, or too close to the last value we used. If it is within
            // those thresholds we set the location to null to prevent the
            // update Service being run unnecessarily (and spending battery on
            // data transfers).
            if ((lastTime > System.currentTimeMillis() - Const.MAX_TIME) ||
                    (lastLocation.distanceTo(location) < Const.MAX_DISTANCE)) {
                location = null;
            }
        }

        // Start the Service used to find nearby points of interest based on the
        // last detected location.
        if (location != null) {
            Intent updateIntent = new Intent(context, DistanceUpdateService.class);
            updateIntent.putExtra(Const.INTENT_EXTRA_GEO_LAT, location.getLatitude());
            updateIntent.putExtra(Const.INTENT_EXTRA_GEO_LNG, location.getLongitude());

            context.startService(updateIntent);
        }
    }
}
