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

package ca.mudar.parkcatcher;

import android.app.AlarmManager;

public class Const {

    public static boolean IS_DEBUG = false;
    public static boolean HAS_OFFLINE = true;

    // Configuration
    public static final int DURATION_MIN = 1;
    public static final int DURATION_MAX = 48;
    public static final int DURATION_DEFAULT = 2;

    // Database
    public static final String DATABASE_NAME = "parkcatcher.db";
    public static final int DATABASE_VERSION = 12;
    
    // Prefs
    public static final String APP_PREFS_NAME = "parking_prefs";

    // Google Analytics
    public static interface GoogleAnalytics {
        final String ACCOUNT = "UA-xxxxxxx-xx";
    }

    // Crittercism
    public static final String CRITTERCISM_APP_ID = "__CRITTERCISM_APP_ID__";

    // Language
    public static interface LangValues {
        final String FR = "fr";
        final String EN = "en";
        final String DEFAULT = "fr";
    }

    // Tabs
    public static final int TABS_INDEX_MAP = 0x0;
    public static final int TABS_INDEX_FAVORITES = 0x1;

    public static final String TAG_TABS_MAP = "tabs_tag_map";
    public static final String TAG_TABS_FAVORITES = "tabs_tag_favorites";

    public static final String TAG_FRAGMENT_MAP = "tag_fragment_map";
    public static final String TAG_FRAGMENT_FAVORITES = "tag_fragment_favorites";
    public static final String TAG_FRAGMENT_PICKER_DATE = "tag_fragment_picker_date";
    public static final String TAG_FRAGMENT_PICKER_TIME = "tag_fragment_picker_time";
    public static final String TAG_FRAGMENT_PICKER_NUMBER = "tag_fragment_picker_number";
    public static final String TAG_FRAGMENT_SEEKBAR_NUMBER = "tag_fragment_seekbar_number";

    // API
    private static final String API_KEY_PARK_CATCHER = "__API_PRIVATE_KEY__";
    private static final String API_SERVER_NAME = "http://www.parkcatcher.com";
    
    public static interface Api {

        static final int PAGES_POSTS = 92;
        static final int PAGES_PANELS = 118;
        
        static final String POSTS_LIVE = API_SERVER_NAME
                + "/api/?day=%s&hour=%s&duration=%s&latNW=%s&lonNW=%s&latSE=%s&lonSE=%s"
                + "&api_key=" + API_KEY_PARK_CATCHER;

        static final String POSTS = API_SERVER_NAME + "/api/posts/"
                + API_KEY_PARK_CATCHER + "/%s/%s";
        static final String PANELS = API_SERVER_NAME + "/api/panels/"
                + API_KEY_PARK_CATCHER + "/%s/%s";
        static final String PANELS_CODES = API_SERVER_NAME + "/api/panels-codes/"
                + API_KEY_PARK_CATCHER;
        static final String PANELS_CODES_RULES = API_SERVER_NAME
                + "/api/panels-codes-rules/"
                + API_KEY_PARK_CATCHER;
        static final String DATABASE = API_SERVER_NAME + "/sqlite/" + DATABASE_NAME;
    }
    


    public static interface LocalAssets {
        final String LICENSE = "gpl-3.0-standalone.html";
    }

    // Intent and Bundle extras
    public static final String INTENT_EXTRA_POST_ID = "post_id";
    public static final String INTENT_EXTRA_SERVICE_LOCAL = "service_local";
    public static final String INTENT_EXTRA_SERVICE_REMOTE = "service_remote";

    public static final String KEY_BUNDLE_PROGRESS_INCREMENT = "bundle_progress_increment";

    public static final int INTENT_REQ_CODE_EULA = 0x10;
    // public static final int INTENT_REQ_CODE_SPLASH = 0x10;
    // public static String INTENT_GMAPS_PACKAGE_NAME = "";

    public static final String LOCATION_PROVIDER_DEFAULT = "DefaultLocationProvider";
    public static final String LOCATION_PROVIDER_LONG_PRESS = "LongPressLocationProvider";
    public static final String LOCATION_PROVIDER_SEARCH = "SearchLocationProvider";
    public static final String LOCATION_PROVIDER_INTENT = "IntentLocationProvider";
    public static final String LOCATION_PROVIDER_PREFS = "PrefsLocationProvider";
    public static final String LOCATION_PROVIDER_SERVICE = "ServiceLocationProvider";

    public static final int PADDING_BOTTOM_ZOOM = 40;

    public static final double MAPS_DEFAULT_COORDINATES[] = {
            45.5d, -73.666667d
    };

    public static final double MAPS_GEOCODER_LIMITS[] = {
            45.380127d, // lowerLeftLat
            -73.982620d, // lowerLeftLng
            45.720444d, // upperRightLat
            -73.466087d
    };

    // Preferences
    public static interface PrefsNames {
        final String LANGUAGE = "prefs_language";
        final String UNITS_SYSTEM = "prefs_units_system";
        final String LAST_UPDATE_LAT = "prefs_last_update_lat";
        final String LAST_UPDATE_LNG = "prefs_last_update_lng";
        final String LAST_UPDATE_TIME_GEO = "prefs_last_update_time_geo";
        final String FOLLOW_LOCATION_CHANGES = "prefs_follow_location_changes";
        final String HAS_ACCEPTED_EULA = "accepted_eula";
        final String HAS_LOADED_DATA = "loaded_data";
        final String IS_BETA_USER = "beta_user";
    }

    public static interface PrefsValues {
        final String LANG_FR = "fr";
        final String LANG_EN = "en";
        final String UNITS_ISO = "iso";
        final String UNITS_IMP = "imp";
    }

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    public static boolean SUPPORTS_JELLY_BEAN = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN;
    public static boolean SUPPORTS_ICS = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    public static boolean SUPPORTS_HONEYCOMB = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB;
    public static boolean SUPPORTS_GINGERBREAD = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD;
    // public static boolean SUPPORTS_FROYO = true;

    /*
     * TEMP
     */
    public static final String KEY_BUNDLE_SEARCH_ADDRESS = "bundle_search_address";
    public static final int BUNDLE_SEARCH_ADDRESS_SUCCESS = 0x1;
    public static final int BUNDLE_SEARCH_ADDRESS_ERROR = 0x0;
    public static final String KEY_BUNDLE_ADDRESS_LAT = "bundle_address_lat";
    public static final String KEY_BUNDLE_ADDRESS_LNG = "bundle_address_lng";

    public static final String INTENT_EXTRA_GEO_LAT = "geo_lat";
    public static final String INTENT_EXTRA_GEO_LNG = "geo_lng";
    public static final String INTENT_EXTRA_FORCE_UPDATE = "force_update";

    // Database
    public static interface DbValues {
        final String DATE_FORMAT = "yyyy-MM-dd";

        final int TYPE_PANEL_CODE_PARKING = 0x1;
        final int TYPE_PANEL_CODE_PAID = 0x2;
        
        final String CONCAT_SEPARATOR = "__SEP__";
    }

    /**
     * Minimum distance to center map on user location, otherwise center on
     * downtown. Units are meters.
     */
    public static final int MAPS_MIN_DISTANCE = 25000;

    public static interface UnitsDisplay {
        final float FEET_PER_MILE = 5280f;
        final float METER_PER_MILE = 1609.344f;
        final int ACCURACY_FEET_FAR = 100;
        final int ACCURACY_FEET_NEAR = 10;
        final int MIN_FEET = 200;
        final int MIN_METERS = 100;
    }

    /**
     * Location constants. Copied from
     * com.radioactiveyak.location_best_practices
     */
    public static String EXTRA_KEY_IN_BACKGROUND = "extra_key_in_background";

    // The default search radius when searching for places nearby.
    public static int DEFAULT_RADIUS = 150;
    // The maximum distance the user should travel between location updates.
    public static int MAX_DISTANCE = DEFAULT_RADIUS / 2;
    // The maximum time that should pass before the user gets a location update.
    public static long MAX_TIME = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

    public static int DB_MAX_DISTANCE = MAX_DISTANCE / 2;

    // You will generally want passive location updates to occur less frequently
    // than active updates. You need to balance location freshness with battery
    // life.
    // The location update distance for passive updates.
    public static int PASSIVE_MAX_DISTANCE = MAX_DISTANCE;
    // The location update time for passive updates
    public static long PASSIVE_MAX_TIME = AlarmManager.INTERVAL_HALF_DAY;
    // Use the GPS (fine location provider) when the Activity is visible?
    public static boolean USE_GPS_WHEN_ACTIVITY_VISIBLE = true;
    // When the user exits via the back button, do you want to disable
    // passive background updates.
    public static boolean DISABLE_PASSIVE_LOCATION_WHEN_USER_EXIT = false;

    public static String ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED = "ca.mudar.parkcatcher.data.ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED";
}
