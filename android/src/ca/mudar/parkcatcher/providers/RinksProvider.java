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
 * - Replaced original content by Rinks
 */

package ca.mudar.parkcatcher.providers;



import ca.mudar.parkcatcher.Const.DbValues;
import ca.mudar.parkcatcher.providers.RinksContract.Boroughs;
import ca.mudar.parkcatcher.providers.RinksContract.Favorites;
import ca.mudar.parkcatcher.providers.RinksContract.Parks;
import ca.mudar.parkcatcher.providers.RinksContract.ParksColumns;
import ca.mudar.parkcatcher.providers.RinksContract.Rinks;
import ca.mudar.parkcatcher.providers.RinksDatabase.Tables;
import ca.mudar.parkcatcher.utils.SelectionBuilder;

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

/**
 * Provider that stores {@link RinksContract} data. Data is usually inserted by
 * {@link SyncService}, and queried by various {@link Activity} instances.
 */
public class RinksProvider extends ContentProvider {
    private static final String TAG = "RinksProvider ";

    private RinksDatabase mOpenHelper;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static final int BOROUGHS = 110;
    private static final int BOROUGHS_ID = 111;

    private static final int PARKS = 120;
    private static final int PARKS_ID = 121;
    private static final int PARKS_ID_RINKS = 122;

    private static final int RINKS = 130;
    private static final int RINKS_FAVORITES = 131;
    private static final int RINKS_SKATING = 132;
    private static final int RINKS_HOCKEY = 133;
    private static final int RINKS_ALL = 134;
    private static final int RINKS_ID = 135;

    private static final int FAVORITES = 140;
    private static final int FAVORITES_ID = 141;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RinksContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "boroughs", BOROUGHS);
        matcher.addURI(authority, "boroughs/*", BOROUGHS_ID);

        matcher.addURI(authority, "parks", PARKS);
        matcher.addURI(authority, "parks/*", PARKS_ID);
        matcher.addURI(authority, "parks/*/rinks", PARKS_ID_RINKS);

        matcher.addURI(authority, "rinks", RINKS);
        matcher.addURI(authority, "rinks/favorites", RINKS_FAVORITES);
        matcher.addURI(authority, "rinks/skating", RINKS_SKATING);
        matcher.addURI(authority, "rinks/hockey", RINKS_HOCKEY);
        matcher.addURI(authority, "rinks/all", RINKS_ALL);
        matcher.addURI(authority, "rinks/*", RINKS_ID);

        matcher.addURI(authority, "favorites", FAVORITES);
        matcher.addURI(authority, "favorites/*", FAVORITES_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        mOpenHelper = new RinksDatabase(context);
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOROUGHS:
                return Boroughs.CONTENT_TYPE;
            case BOROUGHS_ID:
                return Boroughs.CONTENT_ITEM_TYPE;
            case PARKS:
                return Parks.CONTENT_TYPE;
            case PARKS_ID:
                return Parks.CONTENT_ITEM_TYPE;
            case PARKS_ID_RINKS:
                return Rinks.CONTENT_ITEM_TYPE;
            case RINKS:
                return Rinks.CONTENT_TYPE;
            case RINKS_FAVORITES:
                return Rinks.CONTENT_TYPE;
            case RINKS_SKATING:
                return Rinks.CONTENT_TYPE;
            case RINKS_HOCKEY:
                return Rinks.CONTENT_TYPE;
            case RINKS_ALL:
                return Rinks.CONTENT_TYPE;
            case RINKS_ID:
                return Rinks.CONTENT_ITEM_TYPE;
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

        // TODO Replace this by Readable. Requires local JSON asset to initate
        // DB update.
        // final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        final int match = sUriMatcher.match(uri);
        final SelectionBuilder builder = buildExpandedSelection(uri, match);
        switch (match) {
            case PARKS: {
                String groupBy = ParksColumns.PARK_ID;
                Cursor c = builder.where(selection, selectionArgs).query(db, projection, groupBy,
                        null, sortOrder, null);
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
            case BOROUGHS: {
                db.insertOrThrow(Tables.BOROUGHS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Boroughs.buildBoroughUri(values.getAsString(BaseColumns._ID));
            }
            case PARKS: {
                db.insertOrThrow(Tables.PARKS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Parks.buildParkUri(values.getAsString(BaseColumns._ID));
            }
            case RINKS: {
                db.insertOrThrow(Tables.RINKS, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Rinks.buildRinkUri(values.getAsString(BaseColumns._ID));
            }
            case FAVORITES: {
                try {
                    db.insertOrThrow(Tables.FAVORITES, null, values);
                    getContext().getContentResolver().notifyChange(uri, null);
                } catch (SQLiteConstraintException e) {
                    Log.v(TAG, "Rink is already a favorite");
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
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
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
            case BOROUGHS: {
                return builder.table(Tables.BOROUGHS);
            }
            case BOROUGHS_ID: {
                final String boroughId = Boroughs.getBoroughId(uri);
                return builder.table(Tables.BOROUGHS).where(BaseColumns._ID + "=?", boroughId);
            }
            case PARKS: {
                return builder.table(Tables.PARKS);
            }
            case PARKS_ID: {
                final String parkId = Parks.getParkId(uri);
                return builder.table(Tables.PARKS).where(BaseColumns._ID + "=?", parkId);
            }
            case RINKS: {
                return builder.table(Tables.RINKS);
            }
            case RINKS_ID: {
                final String rinkId = Rinks.getRinkId(uri);
                return builder.table(Tables.RINKS).where(BaseColumns._ID + "=?", rinkId);
            }
            case FAVORITES: {
                return builder.table(Tables.FAVORITES);
            }
            case FAVORITES_ID: {
                final String favoriteId = Favorites.getFavoriteId(uri);
                return builder.table(Tables.FAVORITES).where(BaseColumns._ID + "=?", favoriteId);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case BOROUGHS: {
                return builder.table(Tables.BOROUGHS);
            }
            case BOROUGHS_ID: {
                final String boroughId = Boroughs.getBoroughId(uri);
                return builder.table(Tables.BOROUGHS).where(BaseColumns._ID + "=?", boroughId);
            }
            case PARKS: {
                return builder.table(Tables.PARKS_JOIN_RINKS_FAVORITES)
                        .mapToTable(Parks._ID, Tables.PARKS)
                        .map(Parks.PARK_TOTAL_RINKS, Parks.PARK_TOTAL_RINKS_MAPPED)
                        .map(Rinks.RINK_IS_FAVORITE, Favorites.FAVORITE_IS_FAVORITE_MAPPED);
            }
            case PARKS_ID: {
                final String parkId = Parks.getParkId(uri);
                return builder.table(Tables.PARKS).where(BaseColumns._ID + "=?", parkId);
            }
            case PARKS_ID_RINKS: {
                final String parkId = Parks.getParkId(uri);
                return builder.table(Tables.PARKS_JOIN_RINKS_FAVORITES)
                        .mapToTable(Parks._ID, Tables.PARKS)
                        .map(Rinks.RINK_IS_FAVORITE, Favorites.FAVORITE_IS_FAVORITE_MAPPED)
                        .where(Parks.PARK_ID + "=?", parkId);
            }
            case RINKS_ALL:
            case RINKS: {
                return builder.table(Tables.BOROUGHS_JOIN_PARKS_RINKS_FAVORITES)
                        .mapToTable(Rinks._ID, Tables.RINKS)
                        .mapToTable(Rinks.RINK_ID, Tables.RINKS)
                        .mapToTable(Favorites.FAVORITE_RINK_ID, Tables.FAVORITES)
                        .map(Rinks.RINK_IS_FAVORITE, Favorites.FAVORITE_IS_FAVORITE_MAPPED)
                        .where(Rinks.RINK_ID + " IS NOT NULL ");
            }
            case RINKS_FAVORITES: {
                return builder.table(Tables.BOROUGHS_JOIN_PARKS_RINKS_FAVORITES)
                        .mapToTable(Rinks._ID, Tables.RINKS)
                        .mapToTable(Rinks.RINK_ID, Tables.RINKS)
                        .mapToTable(Favorites.FAVORITE_RINK_ID, Tables.FAVORITES)
                        .map(Rinks.RINK_IS_FAVORITE, Favorites.FAVORITE_IS_FAVORITE_MAPPED)
                        .where(Rinks.RINK_IS_FAVORITE + "=1");
            }
            case RINKS_SKATING: {
                String[] args = new String[] {
                        Integer.toString(DbValues.KIND_PP), Integer.toString(DbValues.KIND_PPL),
                        Integer.toString(DbValues.KIND_C)
                };
                return builder
                        .table(Tables.BOROUGHS_JOIN_PARKS_RINKS_FAVORITES)
                        .mapToTable(Rinks._ID, Tables.RINKS)
                        .mapToTable(Rinks.RINK_ID, Tables.RINKS)
                        .mapToTable(Favorites.FAVORITE_RINK_ID, Tables.FAVORITES)
                        .map(Rinks.RINK_IS_FAVORITE, Favorites.FAVORITE_IS_FAVORITE_MAPPED)
                        .where(Rinks.RINK_KIND_ID + "=? OR " + Rinks.RINK_KIND_ID + "=? OR "
                                + Rinks.RINK_KIND_ID + "=?", args);
            }
            case RINKS_HOCKEY: {
                return builder.table(Tables.BOROUGHS_JOIN_PARKS_RINKS_FAVORITES)
                        .mapToTable(Rinks._ID, Tables.RINKS)
                        .mapToTable(Rinks.RINK_ID, Tables.RINKS)
                        .mapToTable(Favorites.FAVORITE_RINK_ID, Tables.FAVORITES)
                        .map(Rinks.RINK_IS_FAVORITE, Favorites.FAVORITE_IS_FAVORITE_MAPPED)
                        .where(Rinks.RINK_KIND_ID + "=?", Integer.toString(DbValues.KIND_PSE));
            }
            case RINKS_ID: {
                final String rinkId = Rinks.getRinkId(uri);
                return builder.table(Tables.BOROUGHS_JOIN_PARKS_RINKS_FAVORITES)
                        .mapToTable(Rinks._ID, Tables.RINKS)
                        .mapToTable(Rinks.RINK_ID, Tables.RINKS)
                        .mapToTable(Favorites.FAVORITE_RINK_ID, Tables.FAVORITES)
                        .map(Rinks.RINK_IS_FAVORITE, Favorites.FAVORITE_IS_FAVORITE_MAPPED)
                        .where(Qualified.RINKS_RINK_ID + "=?", rinkId);
            }
            case FAVORITES: {
                return builder.table(Tables.FAVORITES);
            }
            case FAVORITES_ID: {
                final String favoriteId = Favorites.getFavoriteId(uri);
                return builder.table(Tables.FAVORITES).where(BaseColumns._ID + "=?", favoriteId);
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

    /**
     * {@link ScheduleContract} fields that are fully qualified with a specific
     * parent {@link Tables}. Used when needed to work around SQL ambiguity.
     */
    private interface Qualified {
        String RINKS_RINK_ID = Tables.RINKS + "." + Rinks.RINK_ID;
        // String FAVORITE_RINK_ID = Tables.FAVORITES + "." +
        // Favorites.FAVORITE_RINK_ID;
        // String FAVORITE_ID = Tables.FAVORITES + "." + BaseColumns._ID;
    }
}
