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

package ca.mudar.parkcatcher.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import ca.mudar.parkcatcher.Const.DbValues;
import ca.mudar.parkcatcher.provider.ParkingDatabase.Tables;

/**
 * Contract class for interacting with {@link ParkingProvider}. Unless otherwise
 * noted, all time-based fields are milliseconds since epoch and can be compared
 * against {@link System#currentTimeMillis()}.
 * <p>
 * The backing {@link android.content.ContentProvider} assumes that {@link Uri}
 * are generated using stronger {@link String} identifiers, instead of
 * {@code int} {@link BaseColumns#_ID} values, which are prone to shuffle during
 * sync.
 */
public class ParkingContract {
    @SuppressWarnings("unused")
    private static final String TAG = "ParkingContract";
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

    public static interface PostsColumns {
        final String ID_POST = "id_post";
        final String LNG = "lng";
        final String LAT = "lat";
        final String GEO_DISTANCE = "post_geo_distance";
    }

    public static interface PanelsColumns {
        final String ID_PANEL = "id_panel";
        final String ID_POST = "id_post";
        final String ID_PANEL_CODE = "id_panel_code";
        final String ARROW = "arrow";
    }

    public static interface PanelsCodesColumns {
        final String CODE = "code";
        final String DESCRIPTION = "description";
        final String TYPE_DESC = "type_desc";
    }

    public static interface PanelsCodesRulesColumns {
        final String ID_PANEL_CODE = "id_panel_code";
        final String MINUTES_DURATION = "minutes_duration";
        final String HOUR_START = "hour_start";
        final String HOUR_END = "hour_end";
        final String HOUR_DURATION = "hour_duration";
        final String DAY_START = "day_start";
        final String DAY_END = "day_end";
    }

    public static interface FavoritesColumns {
        final String ID_POST = "id_post";
        final String LABEL = "label";
    }

    public static final String CONTENT_AUTHORITY = "ca.mudar.parkcatcher";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_POSTS = "posts";
    private static final String PATH_PANELS = "panels";
    private static final String PATH_PANELS_CODES = "panels_codes";
    private static final String PATH_PANELS_CODES_RULES = "panels_codes_rules";
    private static final String PATH_FAVORITES = "favorites";
    private static final String PATH_POSTS_ALLOWED = "allowed";
    private static final String PATH_POSTS_FORBIDDEN = "fobidden";
    private static final String PATH_POSTS_STARRED = "starred";
    // TODO: remove this
    private static final String PATH_POSTS_TIMED = "timed";

    public static class Posts implements PostsColumns, SyncColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_POSTS).build();

        public static final Uri CONTENT_ALLOWED_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_POSTS).appendPath(PATH_POSTS_ALLOWED).build();
        public static final Uri CONTENT_FORBIDDEN_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_POSTS).appendPath(PATH_POSTS_FORBIDDEN).build();
        public static final Uri CONTENT_STARRED_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_POSTS).appendPath(PATH_POSTS_STARRED).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.parkcatcher.post";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.parkcatcher.post";

        public static final String IS_FORBIDDEN = "is_forbidden";
        public static final String IS_STARRED = "is_starred";

        public static final String DEFAULT_SORT = BaseColumns._ID + " ASC ";
        public static final String DISTANCE_SORT = PostsColumns.GEO_DISTANCE + " ASC ";
        public static final String FORBIDDEN_DISTANCE_SORT = IS_FORBIDDEN + " ASC, "
                + PostsColumns.GEO_DISTANCE + " ASC ";

        public static Uri buildPostUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getPostId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildPostTimedUri(String id,
                String startHour, String endHour, String dayOfYear) {
            return CONTENT_URI.buildUpon()
                    .appendPath(id)
                    .appendPath(PATH_POSTS_TIMED)
                    .appendPath(startHour)
                    .appendPath(endHour)
                    .appendPath(dayOfYear)
                    .build();
        }

        public static String getPostTimedStartHour(Uri uri) {
            return uri.getPathSegments().get(3);
        }

        public static String getPostTimedEndHour(Uri uri) {
            return uri.getPathSegments().get(4);
        }

        public static String getPostTimedDayOfYear(Uri uri) {
            return uri.getPathSegments().get(5);
        }

        // public static Uri buildAllowedPostsUri(String id) {
        // return CONTENT_URI.buildUpon().appendPath(id).build();
        // }

        // public static Uri buildPanelsUri(String postId) {
        // return
        // CONTENT_URI.buildUpon().appendPath(postId).appendPath(PATH_PANELS).build();
        // }
    }

    public static class Panels implements PanelsColumns, SyncColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PANELS).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.parkcatcher.panel";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.parkcatcher.panel";

        public static final String DEFAULT_SORT = BaseColumns._ID + " ASC ";

        public static Uri buildPanelUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getPanelId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        // public static Uri buildPanelsCodesUri(String panelId) {
        // return
        // CONTENT_URI.buildUpon().appendPath(panelId).appendPath(PATH_PANELS_CODES).build();
        // }
    }

    public static class PanelsCodes implements PanelsCodesColumns, SyncColumns, BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PANELS_CODES).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.parkcatcher.code";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.parkcatcher.code";

        public static final String CONCAT_ID_PANEL_CODE = "GROUP_CONCAT("
                + Tables.PANELS_CODES + "." + BaseColumns._ID + ", '"
                + DbValues.CONCAT_SEPARATOR + "') AS " + PanelsColumns.ID_PANEL_CODE;
        public static final String CONCAT_DESCRIPTION = "GROUP_CONCAT("
                + PanelsCodesColumns.DESCRIPTION + ", '"
                + DbValues.CONCAT_SEPARATOR + "') AS " + PanelsCodesColumns.DESCRIPTION;
        public static final String CONCAT_TYPE_DESC = "GROUP_CONCAT("
                + PanelsCodesColumns.TYPE_DESC + ", '"
                + DbValues.CONCAT_SEPARATOR + "') AS " + PanelsCodesColumns.TYPE_DESC;

        public static final String DEFAULT_SORT = BaseColumns._ID + " ASC ";

        public static Uri buildPanelCodeUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getPanelCodeId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class PanelsCodesRules implements PanelsCodesRulesColumns, SyncColumns,
            BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_PANELS_CODES_RULES).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.parkcatcher.rule";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.parkcatcher.rule";

        public static final String DEFAULT_SORT = BaseColumns._ID + " ASC ";

        public static Uri buildPanelCodeRuleUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getPanelCodeRuleId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static class Favorites implements FavoritesColumns, SyncColumns, BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITES).build();

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.parkcatcher.favorite";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.parkcatcher.favorite";

        public static final String DEFAULT_SORT = PostsColumns.GEO_DISTANCE + " ASC ";

        public static Uri buildFavoriteUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getFavoriteId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    private ParkingContract() {
    }
}
