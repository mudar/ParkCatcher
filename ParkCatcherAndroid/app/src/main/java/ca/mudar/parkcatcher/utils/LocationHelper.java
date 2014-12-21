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

package ca.mudar.parkcatcher.utils;

import android.content.Intent;
import android.location.Location;

import ca.mudar.parkcatcher.Const;

public class LocationHelper {

    public static Location getLocationFromIntent(Intent intent) {
        Location location = null;

        final double latitude = intent.getDoubleExtra(Const.INTENT_EXTRA_GEO_LAT, Double.MIN_VALUE);
        final double longitude = intent.getDoubleExtra(Const.INTENT_EXTRA_GEO_LNG, Double.MIN_VALUE);

        if (Double.compare(latitude, Double.MIN_VALUE) != 0
                && Double.compare(latitude, Double.MIN_VALUE) != 0) {
            location = new Location(Const.LOCATION_PROVIDER_INTENT);

            location.setLatitude(latitude);
            location.setLongitude(longitude);
        }

        return location;
    }
}
