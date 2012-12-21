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
 * - Added an implementation for requestLocationUpdates() 
 */

package ca.mudar.parkcatcher.utils;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.utils.base.LocationUpdateRequester;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Build;

/**
 * Provides support for initiating active and passive location updates optimized
 * for the Froyo release. Includes use of the Passive Location Provider. Uses
 * broadcast Intents to notify the app of location changes.
 */
@TargetApi(Build.VERSION_CODES.FROYO)
public class FroyoLocationUpdateRequester extends LocationUpdateRequester {
    protected static final String TAG = "FroyoLocationUpdateRequester";

    public FroyoLocationUpdateRequester(LocationManager locationManager) {
        super(locationManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestLocationUpdates(long minTime, long minDistance, Criteria criteria,
            PendingIntent pendingIntent) {
        String bestAvailableProvider = locationManager.getBestProvider(criteria, true);

        if (bestAvailableProvider != null) {
            try {
                locationManager.requestLocationUpdates(bestAvailableProvider, minTime, minDistance,
                        pendingIntent);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestPassiveLocationUpdates(long minTime, long minDistance,
            PendingIntent pendingIntent) {
        /**
         * Froyo introduced the Passive Location Provider, which receives
         * updates whenever a 3rd party app receives location updates.
         */
        // Log.v("FroyoLocationUpdateRequester", "intent = " +
        // pendingIntent.toString());
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, Const.MAX_TIME,
                Const.MAX_DISTANCE, pendingIntent);
    }

    // @Override
    // public void removeLocationUpdates(PendingIntent pendingIntent) {
    // if (pendingIntent != null) {
    // try {
    // locationManager.removeUpdates(pendingIntent);
    // } catch (IllegalArgumentException e) {
    // Log.e(TAG, e.getMessage());
    // }
    // }
    // }

}
