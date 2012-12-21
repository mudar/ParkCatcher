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
 * - Copied from IOSched
 * - Renamed package
 * - Removed almost everything!
 */

package ca.mudar.parkcatcher.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for interacting with {@link RinksProvider}. Unless otherwise
 * noted, all time-based fields are milliseconds since epoch and can be compared
 * against {@link System#currentTimeMillis()}.
 * <p>
 * The backing {@link android.content.ContentProvider} assumes that {@link Uri}
 * are generated using stronger {@link String} identifiers, instead of
 * {@code int} {@link BaseColumns#_ID} values, which are prone to shuffle during
 * sync.
 */
public class RinksContract {
    @SuppressWarnings("unused")
    private static final String TAG = "RinksContract";
    /**
     * Special value for {@link SyncColumns#UPDATED} indicating that an entry
     * has never been updated, or doesn't exist yet.
     */
    public static final long UPDATED_NEVER = -2;

    /**
     * Special value for {@link SyncColumns#UPDATED} indicating that the last
     * update time is unknown, usually when inserted from a local file source.
     */
    public static final long UPDATED_UNKNOWN = -1;

    public static interface SyncColumns {
        /** Last time this entry was updated or synchronized. */
        final String UPDATED = "updated";
    }

    public static interface BoroughsColumns {
        final String BOROUGH_ID = "borough_id";
        final String BOROUGH_NAME = "borough_name";
        final String BOROUGH_CREATED_AT = "borough_created_at";
        // Boroughs are the only table with an updated_at column.
        // Conditions update timestamp are logically the same as the borrough's.
        final String BOROUGH_UPDATED_AT = "borough_updated_at";
    }

    public static interface ParksColumns {
        final String PARK_ID = "park_id";
        final String PARK_BOROUGH_ID = "park_borough_id";
        final String PARK_NAME = "park_name";
        final String PARK_GEO_LAT = "park_geo_lat";
        final String PARK_GEO_LNG = "park_geo_lng";
        final String PARK_GEO_DISTANCE = "park_geo_distance";
        final String PARK_ADDRESS = "park_address";
        final String PARK_PHONE = "park_phone";
        final String PARK_CREATED_AT = "park_updated_at";
        final String PARK_TOTAL_RINKS = "park_total_rinks";
        // TODO Cleanup, goes into Provider or Parks
        final String PARK_TOTAL_RINKS_MAPPED = "COUNT( rinks.rink_rink_id )";
    }

    public static interface RinksColumns {
        final String RINK_ID = "rink_rink_id";
        final String RINK_PARK_ID = "rink_park_id";
        final String RINK_KIND_ID = "rink_kind_id";
        final String RINK_NAME = "rink_name";
        final String RINK_DESC_FR = "rink_desc_fr";
        final String RINK_DESC_EN = "rink_desc_en";
        final String RINK_IS_CLEARED = "rink_is_cleared";
        final String RINK_IS_FLOODED = "rink_is_flooded";
        final String RINK_IS_RESURFACED = "rink_is_resurfaced";
        final String RINK_CONDITION = "rink_condition";
        // `is_favorite` is not a DB column. SQLite alias of
        // `( f._id IS NOT NULL )`
        final String RINK_IS_FAVORITE = "rink_is_favorite";
        final String RINK_CREATED_AT = "rink_created_at";
    }

    public static interface FavoritesColumns {
        final String FAVORITE_ID = "rink_id";
        final String FAVORITE_RINK_ID = "rink_id";
        // TODO Cleanup, goes into Provider or Favorites
        final String FAVORITE_IS_FAVORITE_MAPPED = "( favorites.rink_id IS NOT NULL )";
    }

    public static final String CONTENT_AUTHORITY = "ca.mudar.patinoires";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_BOROUGHS = "boroughs";
    private static final String PATH_PARKS = "parks";
    private static final String PATH_RINKS = "rinks";
    private static final String PATH_FAVORITES = "favorites";
    // TODO Verify this duplicate!
    private static final String PATH_RINKS_FAVORITES = "favorites";
    private static final String PATH_RINKS_SKATING = "skating";
    private static final String PATH_RINKS_HOCKEY = "hockey";
    private static final String PATH_RINKS_ALL = "all";

    public static class Boroughs implements BoroughsColumns, SyncColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_BOROUGHS).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.patinoires.borough";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.patinoires.borough";

        public static final String DEFAULT_SORT = BoroughsColumns.BOROUGH_NAME + " ASC ";

        public static Uri buildBoroughUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getBoroughId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class Parks implements ParksColumns, SyncColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PARKS).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.patinoires.park";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.patinoires.park";

        public static final String DEFAULT_SORT = ParksColumns.PARK_NAME + " ASC ";

        public static final String GROUP_BY_JOIN_TABLE = " GROUP BY " + ParksColumns.PARK_ID;

        public static Uri buildParkUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getParkId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildRinksUri(String parkId) {
            return CONTENT_URI.buildUpon().appendPath(parkId).appendPath(PATH_RINKS).build();
        }
    }

    public static class Rinks implements RinksColumns, SyncColumns, BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RINKS).build();

        public static final Uri CONTENT_FAVORITES_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RINKS).appendPath(PATH_RINKS_FAVORITES).build();
        public static final Uri CONTENT_SKATING_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RINKS).appendPath(PATH_RINKS_SKATING).build();
        public static final Uri CONTENT_HOCKEY_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RINKS).appendPath(PATH_RINKS_HOCKEY).build();
        public static final Uri CONTENT_ALL_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RINKS).appendPath(PATH_RINKS_ALL).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.patinoires.rink";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.patinoires.rink";

        public static final String DEFAULT_SORT = RinksColumns.RINK_NAME + " ASC ";

        public static Uri buildRinkUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getRinkId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class Favorites implements FavoritesColumns, SyncColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.patinoires.favorite";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.patinoires.favorite";

        public static final String DEFAULT_SORT = FavoritesColumns.FAVORITE_RINK_ID + " ASC ";

        public static Uri buildFavoriteUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getFavoriteId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    private RinksContract() {
    }
}
