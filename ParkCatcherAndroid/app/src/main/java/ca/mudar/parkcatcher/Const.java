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
import android.os.Build;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Const {

    /**
     * Parking configuration
     */
    public static final int DURATION_MIN = 1;
    public static final int DURATION_MAX = 48;
    public static final int DURATION_DEFAULT = 2;

    // Minimum distance to center map on user location, otherwise center on downtown
    public static final int MAPS_MIN_DISTANCE = 25000; // meters

    // The default search radius when searching for places nearby.
    public static int DEFAULT_RADIUS = 150;
    // The maximum distance the user should travel between location updates.
    public static int MAX_DISTANCE = DEFAULT_RADIUS / 2;

    public static int DB_MAX_DISTANCE = MAX_DISTANCE / 2;

    // The maximum time that should pass before the user gets a location update.
    public static long MAX_TIME = AlarmManager.INTERVAL_FIFTEEN_MINUTES;

    public static final float MONTREAL_NATURAL_NORTH_ROTATION = -34f;
    public static final LatLng MONTREAL_GEO_LAT_LNG = new LatLng(45.5d, -73.666667d);
    public static final LatLngBounds MONTREAL_GEO_BOUNDS = new LatLngBounds(
            new LatLng(45.380127d, -73.982620d), new LatLng(45.720444d, -73.466087d));

    public static final double MONTREAL_GEOCODER_LIMITS[] = {
            45.380127d, // lowerLeftLat
            -73.982620d, // lowerLeftLng
            45.720444d, // upperRightLat
            -73.466087d // upperRightLng
    };

    public static interface UnitsDisplay {
        final float FEET_PER_MILE = 5280f;
        final float METER_PER_MILE = 1609.344f;
        final int ACCURACY_FEET_FAR = 100;
        final int ACCURACY_FEET_NEAR = 10;
        final int MIN_FEET = 200;
        final int MIN_METERS = 100;
    }

    /**
     * Crashlytics error reporting
     */
    public static final boolean IS_CRASHLYTICS_ENABLED = !BuildConfig.DEBUG;

    /**
     * Share and external apps
     */
    public static final String URL_GMAPS_DIRECTIONS = "http://maps.google.com/maps?saddr=%1$s&daddr=%2$s&f=d";
    public static final String URI_INTENT_STREETVIEW = "google.streetview:cbll=%1$s,%2$s";
    public static final String URI_INSTALL_STREETVIEW = "market://details?id=com.google.android.street";
    public static final String URI_INTENT_NAVIGATION = "google.navigation:q=%1$s,%2$s";

    /**
     * API
     */
    public static boolean HAS_OFFLINE = true;
    private static final String API_KEY_PARK_CATCHER = "__API_PRIVATE_KEY__";
    private static final String API_SERVER_NAME = "http://www.parkcatcher.com";

    public static interface Api {
        final int PAGINATION = 1000;
        final int PAGES_POSTS = 92;
        final int PAGES_PANELS = 118;

        final String POSTS_LIVE = API_SERVER_NAME
                + "/api/?day=%s&hour=%s&duration=%s&latNW=%s&lonNW=%s&latSE=%s&lonSE=%s"
                + "&api_key=" + API_KEY_PARK_CATCHER;

        final String POSTS = API_SERVER_NAME + "/api/posts/"
                + API_KEY_PARK_CATCHER + "/%s/%s";
        final String PANELS = API_SERVER_NAME + "/api/panels/"
                + API_KEY_PARK_CATCHER + "/%s/%s";
        final String PANELS_CODES = API_SERVER_NAME + "/api/panels-codes/"
                + API_KEY_PARK_CATCHER;
        final String PANELS_CODES_RULES = API_SERVER_NAME
                + "/api/panels-codes-rules/"
                + API_KEY_PARK_CATCHER;
        final String DATABASE = API_SERVER_NAME + "/sqlite/" + DATABASE_NAME;
    }

    /**
     * Database
     */
    public static final String DATABASE_NAME = "parkcatcher.db";
    public static final int DATABASE_VERSION = 18;

    public static interface LocalAssets {
        final String LICENSE = "gpl-3.0-standalone.html";
    }

    public static interface DbValues {
        final String DATE_FORMAT = "yyyy-MM-dd";

        final int TYPE_PANEL_CODE_PARKING = 0x1;
        final int TYPE_PANEL_CODE_PAID = 0x2;

        final String CONCAT_SEPARATOR = "__SEP__";
    }

    /**
     * Initial app launch
     */
    public static interface StartupStatus {
        final int OK = 0;
        final int ERROR_CONNECTION = 1;
        final int ERROR_PLAYSERVICES = 2;
    }

    public static interface FragmentTags {
        final String MAP = "fragment_map";
        final String ERROR = "fragment_error";
        final String FAVORITES = "fragment_favorites";
        final String PICKER_DATE = "fragment_picker_date";
        final String PICKER_TIME = "fragment_picker_time";
        final String PICKER_NUMBER = "fragment_picker_number";
        final String SEEKBAR_NUMBER = "fragment_seekbar_number";
        final String SLIDING_UP_CALENDAR = "fragment_sliding_up_calendar"; // ref: R.strings.fragment_tag_sliding_up_calendar
    }

    public static interface RequestCodes {
        final int EULA = 100;
        final int DATE_PICKER = 110;
        final int TIME_PICKER = 120;
        final int NUMBER_PICKER = 130;
        final int NUMBER_SEEKBAR = 131;
        final int MAP = 140;
        final int FAVORITES = 141;
    }

    public static interface SavedInstanceKeys {
        final String PARCELABLE = "instance_parcelable";
        final String DIMENSION = "dimension";
    }

    public static interface BundleExtras {
        final String ID_POST = "id_post";
        final String HELP_PAGE = "help_page";
        final String SERVICE_LOCAL = "service_local";
        final String SERVICE_REMOTE = "service_remote";
        final String URL_PATH_MAP = "map";
        final String URL_PATH_SEARCH = "search";
        final String URL_PATH_POST_ID = "spot";
        final String GEO_LAT = "geo_lat";
        final String GEO_LNG = "geo_lng";
        final String FORCE_UPDATE = "force_update";
        final String ADDRESS_LAT = "bundle_address_lat";
        final String ADDRESS_LNG = "bundle_address_lng";
        final String ADDRESS_DESC = "bundle_address_desc";
        final String ADDRESS_PRIMARY = "bundle_address_primary";
        final String ADDRESS_SECONDARY = "bundle_address_secondary";
        final String SEARCH_ADDRESS = "bundle_search_address";
        final String REVERSE_GEOCODER = "bundle_reverse_geocoder";
        final String CURSOR_SELECTION = "bundle_cursor_selection";
        final String ERROR_CODE = "error_code";
    }

    public static interface BundleExtrasValues {
        int SEARCH_ADDRESS_SUCCESS = 0x1;
        int SEARCH_ADDRESS_ERROR = 0x0;
    }

    public static interface LocationProviders {
        final String DEFAULT = "DefaultLocationProvider";
        final String LONG_PRESS = "LongPressLocationProvider";
        final String SEARCH = "SearchLocationProvider";
        final String INTENT = "IntentLocationProvider";
        final String PREFS = "PrefsLocationProvider";
        final String SERVICE = "ServiceLocationProvider";
    }

    /**
     * Navigation Drawer
     */
    public static interface NavdrawerSection {
        final int _COUNT = 6;
        final int HEADER = -1;
        final int MAP = 0;
        final int FAVORITES = 1;
        final int HELP = 2;
        final int ABOUT = 3;
        final int SETTINGS = 4;
    }

    /**
     * Help pages
     */
    public static interface HelpTabs {
        static final int _COUNT = 9;
        static final int APP = 0;
        static final int STOPPING = 1;
        static final int PARKING = 2;
        static final int SRRR = 3;
        static final int RESTRICTED = 4;
        static final int PRIORITY = 5;
        static final int ARROW = 6;
        static final int RULES = 7;
        static final int CELL = 8;
    }

    /**
     * Preferences
     */
    public static final String APP_PREFS_NAME = "parking_prefs";

    public static interface PrefsNames {
        final String LANGUAGE = "prefs_language";
        final String UNITS_SYSTEM = "prefs_units_system";
        final String LAST_UPDATE_LAT = "prefs_last_update_lat";
        final String LAST_UPDATE_LNG = "prefs_last_update_lng";
        final String LAST_UPDATE_TIME_GEO = "prefs_last_update_time_geo";
        final String HAS_ACCEPTED_EULA = "accepted_eula";
        final String HAS_LOADED_DATA = "loaded_data";
        //        final String IS_BETA_USER = "beta_user";
        final String HAS_VIEWED_TUTORIAL = "viewed_tutorial";
    }

    public static interface PrefsValues {
        final String LANG_FR = "fr";
        final String LANG_EN = "en";
        final String UNITS_ISO = "iso";
        final String UNITS_IMP = "imp";
    }

    public static final int UNKNOWN = -1;
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * Compatibility
     */
    public static boolean SUPPORTS_LOLLIPOP = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    public static boolean SUPPORTS_JELLY_BEAN = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    public static boolean SUPPORTS_ICS = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    @Deprecated
    public static boolean SUPPORTS_HONEYCOMB = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    public static boolean SUPPORTS_HONEYCOMB_MR1 = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
}
