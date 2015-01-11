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

package ca.mudar.parkcatcher.service;

import android.app.IntentService;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.Const.PrefsNames;
import ca.mudar.parkcatcher.provider.ParkingContract;
import ca.mudar.parkcatcher.provider.ParkingContract.Posts;
import ca.mudar.parkcatcher.provider.ParkingContract.PostsColumns;
import ca.mudar.parkcatcher.utils.Lists;

public class DistanceUpdateService extends IntentService {
    private static final String TAG = "DistanceUpdateService";

    private ContentResolver contentResolver;
    private SharedPreferences prefs;

    public DistanceUpdateService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        contentResolver = getContentResolver();

        prefs = getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Calculate distance and save it in the database. This way distance is
     * calculated at write time which is less often than number of reads (or
     * bindView). This also allows for updates in the listView by the
     * CursorLoader.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        final long startLocal = System.currentTimeMillis();

        Double latitude = intent.getDoubleExtra(Const.INTENT_EXTRA_GEO_LAT, Double.NaN);
        Double longitude = intent.getDoubleExtra(Const.INTENT_EXTRA_GEO_LNG, Double.NaN);

        /**
         * Check to see if this is a forced update. Currently not in use in the
         * UI.
         */
        boolean doUpdate = intent.getBooleanExtra(Const.INTENT_EXTRA_FORCE_UPDATE, false);

        if (latitude.equals(Double.NaN) || longitude.equals(Double.NaN)) {

            /**
             * Intent extras are empty, force update if we have lat/lng in
             * Prefs.
             */

            Float lastLat = prefs.getFloat(PrefsNames.LAST_UPDATE_LAT, Float.NaN);
            Float lastLng = prefs.getFloat(PrefsNames.LAST_UPDATE_LNG, Float.NaN);

            if (lastLat.equals(Float.NaN) || lastLng.equals(Float.NaN)) {
                return;
            }

            doUpdate = true;
            latitude = lastLat.doubleValue();
            longitude = lastLng.doubleValue();
        }

        /**
         * If it's not a forced update then check to see if we've moved far
         * enough, or there's been a long enough delay since the last update and
         * if so, enforce a new update.
         */
        if (!doUpdate) {
            Location newLocation = new Location(Const.LOCATION_PROVIDER_SERVICE);
            newLocation.setLatitude(latitude);
            newLocation.setLongitude(longitude);

            /**
             * Retrieve the last update time and place.
             */
            long lastTime = prefs.getLong(PrefsNames.LAST_UPDATE_TIME_GEO, Long.MIN_VALUE);
            Float lastLat = prefs.getFloat(PrefsNames.LAST_UPDATE_LAT, Float.NaN);
            Float lastLng = prefs.getFloat(PrefsNames.LAST_UPDATE_LNG, Float.NaN);

            if (lastLat.equals(Float.NaN) || lastLng.equals(Float.NaN)) {
                doUpdate = true;
            }
            else {
                Location lastLocation = new Location(Const.LOCATION_PROVIDER_SERVICE);
                lastLocation.setLatitude(lastLat.doubleValue());
                lastLocation.setLongitude(lastLng.doubleValue());

                /**
                 * If update time and distance bounds have been passed, do an
                 * update.
                 */
                if ((lastTime < System.currentTimeMillis() - Const.MAX_TIME)
                        || (lastLocation.distanceTo(newLocation) > Const.MAX_DISTANCE)) {
                    doUpdate = true;
                }
            }
        }
        
        if (doUpdate) {
            try {
                contentResolver.applyBatch(ParkingContract.CONTENT_AUTHORITY,
                        updateDistance(Posts.CONTENT_STARRED_URI, latitude, longitude));
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }

            /**
             * Save the last update time and place to the Shared Preferences.
             */
            final Editor prefsEditor = prefs.edit();
            prefsEditor.putFloat(PrefsNames.LAST_UPDATE_LAT, latitude.floatValue());
            prefsEditor.putFloat(PrefsNames.LAST_UPDATE_LNG, longitude.floatValue());
            prefsEditor.putLong(PrefsNames.LAST_UPDATE_TIME_GEO, System.currentTimeMillis());
            prefsEditor.apply();
        }
        Log.v(TAG, "Distance calculation took " + (System.currentTimeMillis()
                - startLocal) + " ms");
    }

    private ArrayList<ContentProviderOperation> updateDistance(Uri contentUri,
            double startLatitude, double startLongitude) {
        final ArrayList<ContentProviderOperation> batch = Lists.newArrayList();

        Cursor queuedPosts = contentResolver.query(contentUri,
                DistanceQuery.POSTS_SUMMARY_PROJECTION,
                null, null, Posts.DISTANCE_SORT);
        try {
            final String selection = BaseColumns._ID + " = ? ";

            while (queuedPosts.moveToNext()) {
                String[] queuedId = new String[] {
                        queuedPosts.getString(DistanceQuery._ID)
                };
                double endLat = queuedPosts.getDouble(DistanceQuery.LAT);
                double endLng = queuedPosts.getDouble(DistanceQuery.LNG);

                if ((endLat == 0.0) || (endLng == 0.0)) {
                    continue;
                }

                int oldDistance = queuedPosts.getInt(DistanceQuery.GEO_DISTANCE);

                /**
                 * Calculate the new distance.
                 */
                float[] results = new float[1];
                Location.distanceBetween(startLatitude, startLongitude, endLat, endLng, results);
                int distance = (int) results[0];

                /**
                 * Compare the new distance to the old one, to avoid the db
                 * write operation if not necessary.
                 */
                if ((oldDistance == 0)
                        || (Math.abs(oldDistance - distance) > Const.DB_MAX_DISTANCE)) {
                    final ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(contentUri);
                    builder.withValue(PostsColumns.GEO_DISTANCE, distance);
                    builder.withSelection(selection, queuedId);

                    batch.add(builder.build());
                }
            }

        } finally {
            queuedPosts.close();
        }

        return batch;
    }

    /**
     * The cursor columns projection.
     */
    private static interface DistanceQuery {
        final String[] POSTS_SUMMARY_PROJECTION = new String[] {
                Posts._ID,
                PostsColumns.LAT,
                PostsColumns.LNG,
                PostsColumns.GEO_DISTANCE
        };

        final int _ID = 0;
        final int LAT = 1;
        final int LNG = 2;
        final int GEO_DISTANCE = 3;
    }
}
