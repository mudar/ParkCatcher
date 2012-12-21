
package ca.mudar.parkcatcher;

import android.app.AlarmManager;

public class Const {

    public static boolean IS_DEBUG = false;

    // Configuration
    public static final int DURATION_MIN = 1;
    public static final int DURATION_MAX = 48;
    public static final int DURATION_DEFAULT = 2;

    // Prefs
    public static final String APP_PREFS_NAME = "parking_prefs";

    // Google Analytics
    public static interface GoogleAnalytics {
        final String ACCOUNT = "UA-6687310-35";
    }

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
    private static final String API_SERVER_NAME = "http://www.parkcatcher.com";

    public static final String API_POSTS = API_SERVER_NAME + "/api/?day=%s&hour=%s&duration=%s&latNW=%s&lonNW=%s&latSE=%s&lonSE=%s";
    
    
    // Intent and Bundle extras
    public static final String INTENT_EXTRA_POST_ID = "post_id";

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
    }

    public static interface PrefsValues {
        final String LANG_FR = "fr";
        final String LANG_EN = "en";
        final String UNITS_ISO = "iso";
        final String UNITS_IMP = "imp";
    }

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

        final int KIND_PP = 0x4; // paysagée
        final int KIND_PPL = 0x5; // patin libre
        final int KIND_PSE = 0x6; // sport d'équipe
        final int KIND_C = 0xff; // citoyens
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

    public static String ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED = "ca.mudar.patinoires.data.ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED";
}
