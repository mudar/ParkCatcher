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
 * - Replaced original content by Posts
 */

package ca.mudar.parkcatcher.provider;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import ca.mudar.parkcatcher.provider.ParkingContract.Favorites;
import ca.mudar.parkcatcher.provider.ParkingContract.Panels;
import ca.mudar.parkcatcher.provider.ParkingContract.PanelsCodes;
import ca.mudar.parkcatcher.provider.ParkingContract.PanelsCodesRules;
import ca.mudar.parkcatcher.provider.ParkingContract.Posts;
import ca.mudar.parkcatcher.provider.ParkingContract.PostsColumns;
import ca.mudar.parkcatcher.provider.ParkingDatabase.Tables;
import ca.mudar.parkcatcher.service.SyncService;
import ca.mudar.parkcatcher.utils.SelectionBuilder;

/**
 * Provider that stores {@link ParkingContract} data. Data is usually inserted
 * by {@link SyncService}, and queried by various {@link Activity} instances.
 */
public class ParkingProvider extends ContentProvider {
    private static final String TAG = "ParkingProvider ";

    private ParkingDatabase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int POSTS = 100;
    private static final int POSTS_ALLOWED = 101;
    private static final int POSTS_FORBIDDEN = 102;
    private static final int POSTS_STARRED = 103;
    private static final int POSTS_ID_TIMED = 104;
    private static final int POSTS_ID = 105;

    private static final int PANELS = 200;
    private static final int PANELS_ID = 201;

    private static final int PANELS_CODES = 300;
    private static final int PANELS_CODES_ID = 301;

    private static final int PANELS_CODES_RULES = 400;
    private static final int PANELS_CODES_RULES_ID = 401;

    private static final int FAVORITES = 500;
    private static final int FAVORITES_ID = 501;

    private static final int HOURS_PER_WEEK = 168; // 7 * 24

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ParkingContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "posts", POSTS);
        matcher.addURI(authority, "posts/allowed", POSTS_ALLOWED);
        matcher.addURI(authority, "posts/fobidden", POSTS_FORBIDDEN);
        matcher.addURI(authority, "posts/starred", POSTS_STARRED);
        matcher.addURI(authority, "posts/*", POSTS_ID);
        matcher.addURI(authority, "posts/*/timed/*/*/*", POSTS_ID_TIMED);

        matcher.addURI(authority, "panels", PANELS);
        matcher.addURI(authority, "panels/*", PANELS_ID);

        matcher.addURI(authority, "panels_codes", PANELS_CODES);
        matcher.addURI(authority, "panels_codes/*", PANELS_CODES_ID);

        matcher.addURI(authority, "panels_codes_rules", PANELS_CODES_RULES);
        matcher.addURI(authority, "panels_codes_rules/*", PANELS_CODES_RULES_ID);

        matcher.addURI(authority, "favorites", FAVORITES);
        matcher.addURI(authority, "favorites/*", FAVORITES_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        mOpenHelper = new ParkingDatabase(context);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {

            case POSTS:
                return Posts.CONTENT_TYPE;
            case POSTS_ALLOWED:
                return Posts.CONTENT_TYPE;
            case POSTS_FORBIDDEN:
                return Posts.CONTENT_TYPE;
            case POSTS_STARRED:
                return Posts.CONTENT_TYPE;
            case POSTS_ID_TIMED:
                return Posts.CONTENT_ITEM_TYPE;
            case POSTS_ID:
                return Posts.CONTENT_ITEM_TYPE;
            case PANELS:
                return Panels.CONTENT_TYPE;
            case PANELS_ID:
                return Panels.CONTENT_ITEM_TYPE;
            case PANELS_CODES:
                return PanelsCodes.CONTENT_ITEM_TYPE;
            case PANELS_CODES_ID:
                return PanelsCodes.CONTENT_TYPE;
            case PANELS_CODES_RULES:
                return PanelsCodesRules.CONTENT_TYPE;
            case PANELS_CODES_RULES_ID:
                return PanelsCodesRules.CONTENT_TYPE;
            case FAVORITES:
                return Favorites.CONTENT_TYPE;
            case FAVORITES_ID:
                return Favorites.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        final int match = sUriMatcher.match(uri);
        final SelectionBuilder builder = buildExpandedSelection(uri, match);

        switch (match) {
            case POSTS_ID:
            case POSTS_ID_TIMED: {
                final String id_post = Posts.getPostId(uri);

                String timeFilter = "1";
                try {

                    final double startHour = Double.parseDouble(selectionArgs[0]); // hourOfWeek
                    final int duration = Integer.parseInt(selectionArgs[1]);
                    final int dayOfYear = Integer.parseInt(selectionArgs[2]);

                    final double endHour = startHour + duration;
                    if (endHour > HOURS_PER_WEEK) {
                        // Handle week overlap
                        timeFilter = " ("
                                + getTimeFilterQuery(startHour, HOURS_PER_WEEK, dayOfYear) + " OR "
                                + getTimeFilterQuery(0, endHour - HOURS_PER_WEEK, dayOfYear) + ") ";
                    }
                    else {
                        timeFilter = getTimeFilterQuery(startHour, endHour, dayOfYear);
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return null;
                }

                final String queryTimeFilter = "(" + Subquery.PANELS_POST_ID_FORBIDDEN
                        + " WHERE " + Qualified.PANELS_ID_POST + " = " + id_post
                        + " AND " + timeFilter + ")";

                // TWEAK: SelectionBuilder uses cached mapping, which doesn't
                // work with our LEFT JOIN. Replaced by a manual query.
                // builder
                // .map(Posts.IS_FORBIDDEN, queryTimeFilter)
                // .map(Favorites._ID, Qualified.FAVORITE_ID)
                // .mapToTable(Posts.ID_POST, Tables.POSTS)
                // .map(Posts.IS_STARRED, Qualified.FAVORITE_ID +
                // " IS NOT NULL ");
                // Cursor c = builder
                // .query(db, projection, null, null, sortOrder, null);

                Cursor c = db.query(Tables.POSTS_JOIN_PANELS_PANELS_CODES_FAVORITES,
                        new String[] {
                                "code", "description", "type_desc",
                                "favorites._id IS NOT NULL  AS is_starred",
                                "lat", "lng",
                                queryTimeFilter + " AS is_forbidden "
                        },
                        builder.getSelection(),
                        builder.getSelectionArgs(),
                        null,
                        null,
                        null);

                c.setNotificationUri(getContext().getContentResolver(), uri);

                return c;
            }
            case POSTS_STARRED: {
                String queryTimeFilter = "1";

                if (selectionArgs != null) {
                    try {

                        final double startHour = Double.parseDouble(selectionArgs[0]); // hourOfWeek
                        final int duration = Integer.parseInt(selectionArgs[1]);
                        final int dayOfYear = Integer.parseInt(selectionArgs[2]);

                        final double endHour = startHour + duration;
                        if (endHour > HOURS_PER_WEEK) {
                            // Handle week overlap
                            queryTimeFilter = " ("
                                    + getTimeFilterQuery(startHour, HOURS_PER_WEEK, dayOfYear)
                                    + " OR "
                                    + getTimeFilterQuery(0, endHour - HOURS_PER_WEEK, dayOfYear)
                                    + ") ";
                        }
                        else {
                            queryTimeFilter = getTimeFilterQuery(startHour, endHour, dayOfYear);
                        }

                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return null;
                    }

                    queryTimeFilter = Qualified.POSTS_ID_POST + " IN ("
                            + Subquery.POSTS_FAVORITES_PANELS_FORBIDDEN
                            + " WHERE " + queryTimeFilter + ") ";

                    projection = new String[] {
                            Qualified.POSTS_ID,
                            Qualified.POSTS_ID_POST,
                            Favorites.LABEL,
                            Posts.GEO_DISTANCE,
                            queryTimeFilter + " AS " + Posts.IS_FORBIDDEN,
                    };
                }
                else {
                    projection = new String[] {
                            Qualified.POSTS_ID,
                            PostsColumns.LAT,
                            PostsColumns.LNG,
                            PostsColumns.GEO_DISTANCE
                    };
                }
                //

                // TWEAK: SelectionBuilder uses cached mapping, which doesn't
                // work with our LEFT JOIN. Replaced by a manual query.
                // Cursor c = builder
                // .map(Posts.IS_FORBIDDEN, queryTimeFilter)
                // .mapToTable(Posts._ID, Tables.POSTS)
                // .mapToTable(Posts.ID_POST, Tables.POSTS)
                // .query(db, projection, Qualified.POSTS_ID, null, sortOrder,
                // null);

                Cursor c = db.query(Tables.POSTS_JOIN_FAVORITES_PANELS_PANELS_CODES,
                        projection,
                        builder.getSelection(),
                        builder.getSelectionArgs(),
                        Qualified.POSTS_ID,
                        null,
                        sortOrder);

                c.setNotificationUri(getContext().getContentResolver(), uri);

                return c;
            }
            case POSTS_ALLOWED: {

                String[] selectionGeoArgs = new String[] {
                        // SW.latitude
                        selectionArgs[0],
                        // NE.latitude
                        selectionArgs[1],
                        // SW.longitude
                        selectionArgs[2],
                        // NE.longitude
                        selectionArgs[3]
                };

                String queryTimeFilter = "1";
                try {

                    final double startHour = Double.parseDouble(selectionArgs[4]); // hourOfWeek
                    final int duration = Integer.parseInt(selectionArgs[5]);
                    final int dayOfYear = Integer.parseInt(selectionArgs[6]);

                    final double endHour = startHour + duration;
                    if (endHour > HOURS_PER_WEEK) {
                        // Handle week overlap
                        queryTimeFilter = " ("
                                + getTimeFilterQuery(startHour, HOURS_PER_WEEK, dayOfYear) + " OR "
                                + getTimeFilterQuery(0, endHour - HOURS_PER_WEEK, dayOfYear) + ") ";
                    }
                    else {
                        queryTimeFilter = getTimeFilterQuery(startHour, endHour, dayOfYear);
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return null;
                }

                String groupBy = Qualified.POSTS_ID;
                Cursor c = builder
                        .map(Favorites._ID, Qualified.FAVORITE_ID)
                        .map(Posts.IS_STARRED, Qualified.FAVORITE_ID + " IS NOT NULL ")
                        .mapToTable(Posts.ID_POST, Tables.POSTS)
                        .where(selection, selectionGeoArgs)
                        .where(Qualified.POSTS_ID_POST + " NOT IN ("
                                + Subquery.POSTS_PANELS_FORBIDDEN
                                + " AND " + queryTimeFilter
                                + ")", selectionGeoArgs)
                        .query(db, projection, groupBy, null, sortOrder, null);
                c.setNotificationUri(getContext().getContentResolver(), uri);

                return c;
            }
            default: {
                Cursor c = builder.where(selection, selectionArgs).query(db, projection, sortOrder);
                c.setNotificationUri(getContext().getContentResolver(), uri);
                return c;
            }
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case POSTS: {
                db.insertOrThrow(Tables.POSTS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Posts.buildPostUri(values.getAsString(BaseColumns._ID));
            }
            case PANELS: {
                db.insertOrThrow(Tables.PANELS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Panels.buildPanelUri(values.getAsString(BaseColumns._ID));
            }
            case PANELS_CODES: {
                db.insertOrThrow(Tables.PANELS_CODES, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return PanelsCodes.buildPanelCodeUri(values.getAsString(BaseColumns._ID));
            }
            case PANELS_CODES_RULES: {
                db.insertOrThrow(Tables.PANELS_CODES_RULES, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return PanelsCodesRules.buildPanelCodeRuleUri(values.getAsString(BaseColumns._ID));
            }
            case POSTS_STARRED:
            case FAVORITES: {
                try {
                    db.insertOrThrow(Tables.FAVORITES, null, values);
                    getContext().getContentResolver().notifyChange(uri, null);
                } catch (SQLiteConstraintException e) {
                    Log.v(TAG, "Post is already a favorite");
                    // e.printStackTrace();
                }
                return Favorites.buildFavoriteUri(values.getAsString(BaseColumns._ID));
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case POSTS_STARRED: {
                final SelectionBuilder builder = new SelectionBuilder();
                int retVal = builder
                        .table(Tables.POSTS)
                        .where(selection, selectionArgs).update(db, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return retVal;
            }
            default: {
                final SelectionBuilder builder = buildExpandedSelection(uri, match);
                int retVal = builder.where(selection, selectionArgs).update(db, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return retVal;
            }
        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case POSTS: {
                return builder.table(Tables.POSTS);
            }
            case POSTS_ID: {
                final String id = Posts.getPostId(uri);
                return builder.table(Tables.POSTS).where(BaseColumns._ID + "=?", id);
            }
            case PANELS: {
                return builder.table(Tables.PANELS);
            }
            case PANELS_ID: {
                final String id = Panels.getPanelId(uri);
                return builder.table(Tables.PANELS).where(BaseColumns._ID + "=?", id);
            }
            case PANELS_CODES: {
                return builder.table(Tables.PANELS_CODES);
            }
            case PANELS_CODES_ID: {
                final String id = PanelsCodes.getPanelCodeId(uri);
                return builder.table(Tables.PANELS_CODES).where(BaseColumns._ID + "=?", id);
            }
            case PANELS_CODES_RULES: {
                return builder.table(Tables.PANELS_CODES_RULES);
            }
            case PANELS_CODES_RULES_ID: {
                final String id = PanelsCodesRules.getPanelCodeRuleId(uri);
                return builder.table(Tables.PANELS_CODES_RULES).where(BaseColumns._ID + "=?", id);
            }
            case POSTS_STARRED:
            case FAVORITES: {
                return builder.table(Tables.FAVORITES);
            }
            case FAVORITES_ID: {
                final String id = Favorites.getFavoriteId(uri);
                return builder.table(Tables.FAVORITES).where(BaseColumns._ID + "=?", id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case POSTS: {
                return builder.table(Tables.POSTS);
            }
            case POSTS_ALLOWED: {
                return builder.table(Tables.POSTS_JOIN_PANELS_PANELS_CODES_FAVORITES);
            }
            case POSTS_FORBIDDEN: {
                return builder.table(Tables.POSTS);
            }
            case POSTS_STARRED: {
                return builder.table(Tables.POSTS_JOIN_FAVORITES_PANELS_PANELS_CODES);
            }
            case POSTS_ID:
                // {
                // final String id = Posts.getPostId(uri);
                // return builder.table(Tables.POSTS).where(BaseColumns._ID +
                // "=?", id);
                // }
            case POSTS_ID_TIMED: {
                final String id_post = Posts.getPostId(uri);
                return builder
                        .table(Tables.POSTS_JOIN_PANELS_PANELS_CODES_FAVORITES)
                        .mapToTable(Posts.ID_POST, Tables.POSTS)
                        .where(Qualified.POSTS_ID_POST + "=?", id_post);
            }
            case PANELS: {
                return builder.table(Tables.PANELS);
            }
            case PANELS_ID: {
                final String id = Panels.getPanelId(uri);
                return builder.table(Tables.PANELS).where(BaseColumns._ID + "=?", id);
            }
            case PANELS_CODES: {
                return builder.table(Tables.PANELS_CODES);
            }
            case PANELS_CODES_ID: {
                final String id = PanelsCodes.getPanelCodeId(uri);
                return builder.table(Tables.PANELS_CODES).where(BaseColumns._ID + "=?", id);
            }
            case PANELS_CODES_RULES: {
                return builder.table(Tables.PANELS_CODES_RULES);
            }
            case PANELS_CODES_RULES_ID: {
                final String id = PanelsCodesRules.getPanelCodeRuleId(uri);
                return builder.table(Tables.PANELS_CODES_RULES).where(BaseColumns._ID + "=?", id);
            }
            case FAVORITES: {
                return builder.table(Tables.FAVORITES);
            }
            case FAVORITES_ID: {
                final String id = Favorites.getFavoriteId(uri);
                return builder.table(Tables.FAVORITES).where(BaseColumns._ID + "=?", id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

    }

    private interface Subquery {
        String POSTS_PANELS_FORBIDDEN = "SELECT " + Qualified.POSTS_ID_POST
                + " FROM "
                + Tables.POSTS_JOIN_PANELS_PANELS_CODES_RULES + " WHERE "
                + PostsColumns.LAT + " >= ? AND " +
                PostsColumns.LAT + " <= ? AND " +
                PostsColumns.LNG + " >= ? AND " +
                PostsColumns.LNG + " <= ?";

        String POSTS_FAVORITES_PANELS_FORBIDDEN = "SELECT " + Qualified.POSTS_ID_POST
                + " FROM "
                + Tables.POSTS_JOIN_FAVORITES_PANELS_PANELS_CODES_RULES;

        String PANELS_POST_ID_FORBIDDEN = "SELECT COUNT(" + Qualified.PANELS_ID + ") > 0 "
                + " FROM "
                + Tables.PANELS_JOIN_PANELS_CODES_RULES;
    }

    /**
     * Build the time filter subquery
     * 
     * @param startHour
     * @param endHour
     * @param dayOfYear
     * @return SQL string
     */
    private String getTimeFilterQuery(double startHour, double endHour, int dayOfYear) {

        final String sqlFilterDuration = " ( minutes_duration < " + ((endHour - startHour) * 60)
                + " ) ";
        final String sqlFilterHourSTart = " ( hour_start >= " + startHour + " AND hour_start < "
                + endHour + " ) ";
        final String sqlFilterHourEnd = " ( hour_end > " + startHour + " AND hour_end <= "
                + endHour + " ) ";
        final String sqlFilterHourWide = " ( hour_start <= " + startHour + " AND hour_end >= "
                + endHour + " ) ";

        final String sqlFilterHour = " ( " + sqlFilterHourSTart + " OR " + sqlFilterHourEnd
                + " OR " + sqlFilterHourWide + " ) ";

        final int startWeekDay = (int) Math.floor(startHour / 24);
        final int endWeekDay = (int) Math.floor(endHour / 24);

        String sqlFilterDayOfYear;
        if (startWeekDay != endWeekDay) {
            final int startDayOfYear = dayOfYear;
            final int endDayOfYear = dayOfYear + (endWeekDay - startWeekDay);

            final String sqlFilterDayOfYearStart = " ( day_start >= " + startDayOfYear
                    + " AND day_start <= " + endDayOfYear + " ) ";
            final String sqlFilterDayOfYearEnd = " ( day_end >= " + startDayOfYear
                    + " AND day_end <= " + endDayOfYear + " ) ";
            final String sqlFilterDayOfYearWide = " ( day_start <= " + startDayOfYear
                    + " AND day_end >= " + endDayOfYear + " ) ";

            sqlFilterDayOfYear = " ( " + sqlFilterDayOfYearStart + " OR " + sqlFilterDayOfYearEnd
                    + " OR " + sqlFilterDayOfYearWide + " ) ";
        }
        else {
            sqlFilterDayOfYear = " ( day_start <= " + dayOfYear + " AND day_end >= " + dayOfYear
                    + " ) ";
        }

        return " ( " + sqlFilterDuration + " AND " + sqlFilterDayOfYear + " AND " + sqlFilterHour
                + " ) ";
    }

    /**
     * {@link ParkingContract} fields that are fully qualified with a specific
     * parent {@link Tables}. Used when needed to work around SQL ambiguity.
     */
    private interface Qualified {
        String POSTS_ID = Tables.POSTS + "." + Posts._ID;
        String PANELS_ID = Tables.PANELS + "." + Panels._ID;
        String POSTS_ID_POST = Tables.POSTS + "." + Posts.ID_POST;
        // String PANELS_CODES_ID = Tables.PANELS_CODES + "." + Panels._ID;
        String PANELS_ID_POST = Tables.PANELS + "." + Posts.ID_POST;

        String FAVORITE_ID = Tables.FAVORITES + "." + BaseColumns._ID;
    }
}
