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

import ca.mudar.parkcatcher.Const.PrefsNames;
import ca.mudar.parkcatcher.Const.PrefsValues;
import ca.mudar.parkcatcher.service.DistanceUpdateService;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.location.Location;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ParkingApp extends Application {
    protected static final String TAG = "ParkingApp";

    private SharedPreferences prefs;

    private String mUnits;
    private String mLanguage;
    private Toast mToast;
    private boolean mHasLoadedData;
    private boolean mHasViewedTutorial;

    private GregorianCalendar mParkingCalendar;
    private int mParkingDuration;

    private Location mLocation;

    // TODO: verify possible memory leakage of the following code
    private static ParkingApp instance = null;

    public static ParkingApp getInstance() {
        checkInstance();
        return instance;
    }

    private static void checkInstance() {
        if (instance == null)
            throw new IllegalStateException("Application not created yet!");
    }

    @SuppressLint("ShowToast")
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);

        /**
         * Initialize UI settings based on preferences.
         */
        mUnits = prefs.getString(PrefsNames.UNITS_SYSTEM, PrefsValues.UNITS_ISO);

        mLanguage = prefs.getString(PrefsNames.LANGUAGE, Locale.getDefault().getLanguage());
        if (!mLanguage.equals(PrefsValues.LANG_EN) && !mLanguage.equals(PrefsValues.LANG_FR)) {
            mLanguage = PrefsValues.LANG_EN;
        }

        mHasLoadedData = prefs.getBoolean(PrefsNames.HAS_LOADED_DATA, Const.HAS_OFFLINE);

        resetParkingCalendar();

        /**
         * Having a single Toast instance allows overriding (replacing) the
         * messages and avoiding Toast stack delays.
         */
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        updateUiLanguage();
    }

    public void showToastText(int res, int duration) {
        mToast.setText(res);
        mToast.setDuration(duration);
        mToast.show();
    }

    public void showToastText(String msg, int duration) {
        mToast.setText(msg);
        mToast.setDuration(duration);
        mToast.show();
    }

    /**
     * Used to force distance calculations. Mainly on first launch where an
     * empty or partial DB cursor receives the location update, ends up doing
     * partial distance updates.
     */
    public void initializeLocation() {
        // TODO replace this by a listener or synch tasks
        mLocation = null;

        Editor prefsEditor = prefs.edit();

        prefsEditor.putFloat(PrefsNames.LAST_UPDATE_LAT, Float.NaN);
        prefsEditor.putFloat(PrefsNames.LAST_UPDATE_LNG, Float.NaN);
        prefsEditor.putLong(PrefsNames.LAST_UPDATE_TIME_GEO, System.currentTimeMillis());
        prefsEditor.commit();
    }

    public Location getLocation() {
        /**
         * Background services save a passively set location in the Preferences.
         */
        Float lastLat = prefs.getFloat(PrefsNames.LAST_UPDATE_LAT, Float.NaN);
        Float lastLng = prefs.getFloat(PrefsNames.LAST_UPDATE_LNG, Float.NaN);

        if (lastLat.equals(Float.NaN) || lastLng.equals(Float.NaN)) {
            return mLocation;
        }

        mLocation = new Location(Const.LOCATION_PROVIDER_PREFS);
        mLocation.setLatitude(lastLat.doubleValue());
        mLocation.setLongitude(lastLng.doubleValue());

        return mLocation;
    }

    public void setLocation(Location location) {
        // Log.v(TAG, "setLocation");
        if (location == null) {
            return;
        }
        else {
            // Log.v(TAG, "new location = " + location.getLatitude() + "," +
            // location.getLongitude());
            if ((mLocation == null) || (this.mLocation.distanceTo(location) > Const.MAX_DISTANCE)) {
                Intent intent = new Intent(this.getApplicationContext(),
                        DistanceUpdateService.class);
                intent.putExtra(Const.INTENT_EXTRA_GEO_LAT, location.getLatitude());
                intent.putExtra(Const.INTENT_EXTRA_GEO_LNG, location.getLongitude());
                startService(intent);
            }
            /**
             * No need to save location in Preferences because it's done in the
             * background services.
             */

            mLocation = location;
        }
    }

    public String getUnits() {
        return mUnits;
    }

    public void setUnits(String units) {
        this.mUnits = units;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String lang) {
        this.mLanguage = lang;
        updateUiLanguage();
    }

    public boolean hasLoadedData() {
        return mHasLoadedData;
    }

    public void setHasLoadedData(boolean hasLoadedData) {
        this.mHasLoadedData = hasLoadedData;
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(PrefsNames.HAS_LOADED_DATA, mHasLoadedData).commit();
    }
    

    public boolean hasViewedTutorial() {
        return mHasViewedTutorial;
    }
    
    public void setHasViewedTutorial(boolean hasViewedTutorial) {
        this.mHasViewedTutorial = hasViewedTutorial;
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean(PrefsNames.HAS_VIEWED_TUTORIAL, mHasViewedTutorial).commit();
    }

    public GregorianCalendar getParkingCalendar() {
        return mParkingCalendar;
    }

    public void setParkingCalendar(GregorianCalendar calendar) {
        mParkingCalendar = calendar;
    }

    public void resetParkingCalendar() {
        mParkingCalendar = new GregorianCalendar();
        mParkingDuration = Const.DURATION_DEFAULT;
    }

    public void setParkingDate(int year, int month, int day) {
        int hourOfDay = mParkingCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mParkingCalendar.get(Calendar.MINUTE);
        mParkingCalendar.set(year, month, day, hourOfDay, minute);
    }

    public void setParkingTime(int hourOfDay, int minute) {
        int year = mParkingCalendar.get(Calendar.YEAR);
        int month = mParkingCalendar.get(Calendar.MONTH);
        int day = mParkingCalendar.get(Calendar.DAY_OF_MONTH);

        mParkingCalendar.set(year, month, day, hourOfDay, minute);
    }

    public int getParkingDuration() {
        return mParkingDuration;
    }

    public void setParkingDuration(int duration) {
        mParkingDuration = duration;
    }

    /**
     * Force the configuration change to a locale different that the phone's.
     */
    public void updateUiLanguage() {
        Locale locale = new Locale(mLanguage);
        Configuration config = new Configuration();
        config.locale = locale;
        Locale.setDefault(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

}
