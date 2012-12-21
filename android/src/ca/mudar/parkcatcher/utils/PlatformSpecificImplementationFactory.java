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
/* Modifications:
 * - Copied from radioactiveyak.location_best_practices
 * - Renamed package
 * - Removed IStrictMode and SharedPreferenceSaver
 */

package ca.mudar.parkcatcher.utils;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.utils.base.ILastLocationFinder;
import ca.mudar.parkcatcher.utils.base.LocationUpdateRequester;

import android.app.AlarmManager;
import android.content.Context;
import android.location.LocationManager;

/**
 * Factory class to create the correct instances of a variety of classes with
 * platform specific implementations.
 */
public class PlatformSpecificImplementationFactory {

    /**
     * Create a new LastLocationFinder instance
     * 
     * @param context Context
     * @return LastLocationFinder
     */
    public static ILastLocationFinder getLastLocationFinder(Context context) {
        return Const.SUPPORTS_GINGERBREAD ? new GingerbreadLastLocationFinder(context)
                : new LegacyLastLocationFinder(context);
    }

    /**
     * Create a new LocationUpdateRequester
     * 
     * @param context Context
     * @param locationManager Location Manager
     * @return LocationUpdateRequester
     */

    public static LocationUpdateRequester getLocationUpdateRequester(Context context,
            LocationManager locationManager) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // TWEAK: Froyo devices use LegacyLocationUpdateRequester instead of the
        // original FroyoLocationUpdateRequester, to avoid FC!
        return Const.SUPPORTS_GINGERBREAD ? new GingerbreadLocationUpdateRequester(locationManager)
                : new LegacyLocationUpdateRequester(locationManager, alarmManager);

        // return Const.SUPPORTS_GINGERBREAD ? new
        // GingerbreadLocationUpdateRequester(locationManager)
        // : (Const.SUPPORTS_FROYO ? new
        // FroyoLocationUpdateRequester(locationManager)
        // : new LegacyLocationUpdateRequester(locationManager, alarmManager));
    }
}
