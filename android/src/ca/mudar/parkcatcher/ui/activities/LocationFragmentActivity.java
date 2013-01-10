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
 * - Copied from radioactiveyak.location_best_practices
 * - Renamed package
 * - Removed Checkins functions
 */

package ca.mudar.parkcatcher.ui.activities;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.receiver.LocationChangedReceiver;
import ca.mudar.parkcatcher.receiver.PassiveLocationChangedReceiver;
import ca.mudar.parkcatcher.utils.ActivityHelper;
import ca.mudar.parkcatcher.utils.PlatformSpecificImplementationFactory;
import ca.mudar.parkcatcher.utils.base.ILastLocationFinder;
import ca.mudar.parkcatcher.utils.base.LocationUpdateRequester;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class LocationFragmentActivity extends SherlockFragmentActivity {
    // TODO Refactor this into an abstract class

    private static final String TAG = "LocationFragmentActivity";

    protected int indexSection;

    protected ParkingApp mAppHelper;
    protected ActivityHelper mActivityHelper;
    protected SharedPreferences prefs;
    protected SharedPreferences.Editor prefsEditor;

    protected Criteria criteria;
    protected ILastLocationFinder lastLocationFinder;
    protected LocationUpdateRequester locationUpdateRequester;
    protected PendingIntent locationListenerPendingIntent;
    protected PendingIntent locationListenerPassivePendingIntent;

    protected LocationManager locationManager;

    protected boolean hasRegisteredSingleUpdateReceiver = false;
    protected boolean hasRegisteredLocProviderDisabledReceiver = false;
    protected boolean hasFollowLocationChanges = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mAppHelper = (ParkingApp) getApplicationContext();
        mActivityHelper = ActivityHelper.createInstance(this);

        prefs = getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /**
         * Instantiate a LastLocationFinder class. This will be used to find the
         * last known location when the application starts.
         */
        lastLocationFinder = PlatformSpecificImplementationFactory.getLastLocationFinder(this);
        lastLocationFinder.setChangedLocationListener(oneShotLastLocationUpdateListener);
        hasRegisteredSingleUpdateReceiver = true;

        /**
         * Set the last known location as user's current location.
         */
        mAppHelper.setLocation(lastLocationFinder.getLastBestLocation(Const.MAX_DISTANCE,
                Const.MAX_TIME));

        /**
         * Specify the Criteria to use when requesting location updates while
         * the application is Active.
         */
        criteria = new Criteria();
        if (Const.USE_GPS_WHEN_ACTIVITY_VISIBLE) {
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
        }
        else {
            criteria.setPowerRequirement(Criteria.POWER_LOW);
        }

        /**
         * Setup the location update Pending Intents.
         */
        Intent activeIntent = new Intent(this, LocationChangedReceiver.class);
        locationListenerPendingIntent = PendingIntent.getBroadcast(this, 0, activeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent passiveIntent = new Intent(this, PassiveLocationChangedReceiver.class);
        locationListenerPassivePendingIntent = PendingIntent.getBroadcast(this, 0, passiveIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        locationManager.removeUpdates(locationListenerPassivePendingIntent);

        /**
         * Instantiate a Location Update Requester class based on the available
         * platform version. This will be used to request location updates.
         */
        locationUpdateRequester = PlatformSpecificImplementationFactory
                .getLocationUpdateRequester(this.getApplicationContext(), locationManager);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        /**
         * Commit shared preference that says we're in the foreground.
         */
        prefsEditor.putBoolean(Const.EXTRA_KEY_IN_BACKGROUND, false);
        prefsEditor.commit();

        /**
         * Get the last known location (and optionally request location updates)
         * and update the place list.
         */
        hasFollowLocationChanges = prefs
                .getBoolean(Const.PrefsNames.FOLLOW_LOCATION_CHANGES, false);
        getLocationAndUpdateCursor(hasFollowLocationChanges);

        super.onResume();
    }

    @Override
    protected void onPause() {
        prefsEditor.putBoolean(Const.EXTRA_KEY_IN_BACKGROUND, true);
        prefsEditor.commit();

        /**
         * Stop listening for location updates when the Activity is inactive.
         */
        disableLocationUpdates();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (hasRegisteredSingleUpdateReceiver) {
            lastLocationFinder.cancel();
        }

        if (Const.DISABLE_PASSIVE_LOCATION_WHEN_USER_EXIT) {
            locationManager.removeUpdates(locationListenerPassivePendingIntent);
        }

        super.onDestroy();
    }

    /**
     * Find the last known location (using a {@link LastLocationFinder}) and
     * updates the place list accordingly.
     * 
     * @param updateWhenLocationChanges Request location updates
     */
    protected void getLocationAndUpdateCursor(boolean updateWhenLocationChanges) {
        /**
         * This isn't directly affecting the UI, so put it on a worker thread.
         */
        AsyncTask<Void, Void, Void> findLastLocationTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                /**
                 * Find the last known location, specifying a required accuracy
                 * of within the min distance between updates and a required
                 * latency of the minimum time required between updates.
                 */
                Location lastKnownLocation = lastLocationFinder.getLastBestLocation(
                        Const.MAX_DISTANCE,
                        System.currentTimeMillis() - Const.MAX_TIME);

                if (lastKnownLocation != null) {
                    mAppHelper.setLocation(lastKnownLocation);
                }

                return null;
            }
        };
        findLastLocationTask.execute();

        /**
         * If we have requested location updates, turn them on here.
         */
        toggleUpdatesWhenLocationChanges(updateWhenLocationChanges);
    }

    /**
     * Choose if we should receive location updates.
     * 
     * @param updateWhenLocationChanges Request location updates
     */
    protected void toggleUpdatesWhenLocationChanges(boolean updateWhenLocationChanges) {
        if (updateWhenLocationChanges) {
            requestLocationUpdates();
        }
        else {
            disableLocationUpdates();
        }
    }

    /**
     * Start listening for location updates.
     */
    protected void requestLocationUpdates() {
        hasRegisteredSingleUpdateReceiver = true;
        hasRegisteredLocProviderDisabledReceiver = true;

        /**
         * Normal updates while activity is visible.
         */
        locationUpdateRequester.requestLocationUpdates(Const.MAX_TIME,
                Const.MAX_DISTANCE, criteria, locationListenerPendingIntent);
        /**
         * Passive location updates from 3rd party apps when the Activity isn't
         * visible.
         */
        locationUpdateRequester.requestPassiveLocationUpdates(Const.PASSIVE_MAX_TIME,
                Const.PASSIVE_MAX_DISTANCE, locationListenerPassivePendingIntent);
        /**
         * Register a receiver that listens for when the provider I'm using has
         * been disabled.
         */
        IntentFilter intentFilter = new
                IntentFilter(Const.ACTIVE_LOCATION_UPDATE_PROVIDER_DISABLED);
        registerReceiver(locProviderDisabledReceiver, intentFilter);

        /**
         * Register a receiver that listens for when a better provider than I'm
         * using becomes available.
         */
        String bestProvider = locationManager.getBestProvider(criteria, false);
        String bestAvailableProvider = locationManager.getBestProvider(criteria,
                true);
        if (bestProvider != null && !bestProvider.equals(bestAvailableProvider))
        {
            locationManager.requestLocationUpdates(bestProvider, 0, 0,
                    bestInactiveLocationProviderListener, getMainLooper());
        }
    }

    /**
     * Stop listening for location updates
     */
    protected void disableLocationUpdates() {
        if (hasRegisteredLocProviderDisabledReceiver) {
            hasRegisteredLocProviderDisabledReceiver = false;
            try {
                unregisterReceiver(locProviderDisabledReceiver);
            } catch (IllegalArgumentException e) {
                Log.v(TAG, "Receiver already unregistered.");
                e.printStackTrace();
            }
        }

        locationManager.removeUpdates(locationListenerPendingIntent);
        locationManager.removeUpdates(bestInactiveLocationProviderListener);

        /**
         * When disabling active listeners, enable the passive listener
         * (according to user Preferences).
         */
        if (hasFollowLocationChanges) {
            locationUpdateRequester.requestPassiveLocationUpdates(Const.PASSIVE_MAX_TIME,
                    Const.PASSIVE_MAX_DISTANCE, locationListenerPassivePendingIntent);
        }
        else {
            locationManager.removeUpdates(locationListenerPassivePendingIntent);
        }
    }

    /**
     * One-off location listener that receives updates from the
     * {@link LastLocationFinder}. This is triggered where the last known
     * location is outside the bounds of our maximum distance and latency.
     */
    protected LocationListener oneShotLastLocationUpdateListener = new LocationListener() {
        public void onLocationChanged(Location l) {
            mAppHelper.setLocation(l);

            if (hasRegisteredSingleUpdateReceiver) {
                lastLocationFinder.cancel();
                hasRegisteredSingleUpdateReceiver = false;
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }
    };

    /**
     * If the best Location Provider (usually GPS) is not available when we
     * request location updates, this listener will be notified if / when it
     * becomes available. It calls requestLocationUpdates to re-register the
     * location listeners using the better Location Provider.
     */
    protected LocationListener bestInactiveLocationProviderListener = new
            LocationListener() {
                public void onLocationChanged(Location l) {
                }

                public void onProviderDisabled(String provider) {
                }

                public void onStatusChanged(String provider, int status, Bundle extras)
                {
                }

                public void onProviderEnabled(String provider) {
                    /**
                     * Re-register the location listeners using the better
                     * Location Provider.
                     */
                    requestLocationUpdates();
                }
            };

    /**
     * If the Location Provider we're using to receive location updates is
     * disabled while the app is running, this Receiver will be notified,
     * allowing us to re-register our Location Receivers using the best
     * available Location Provider is still available.
     */
    protected BroadcastReceiver locProviderDisabledReceiver = new
            BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean providerDisabled =
                            !intent.getBooleanExtra(LocationManager.KEY_PROVIDER_ENABLED, false);
                    /**
                     * Re-register the location listeners using the best
                     * available Location Provider.
                     */
                    if (providerDisabled)
                        requestLocationUpdates();
                }
            };
}
