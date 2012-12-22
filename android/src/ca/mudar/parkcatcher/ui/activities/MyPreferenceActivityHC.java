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

package ca.mudar.parkcatcher.ui.activities;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.Const.PrefsNames;
import ca.mudar.parkcatcher.Const.PrefsValues;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.util.List;
import java.util.Locale;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MyPreferenceActivityHC extends SherlockPreferenceActivity {
    protected static final String TAG = "MyPreferenceActivityHC";

    protected ParkingApp mParkingApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParkingApp = (ParkingApp) getApplicationContext();
        mParkingApp.updateUiLanguage();

        ActionBar ab = getSupportActionBar();

        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.activity_preferences);

        showBreadCrumbs(getResources().getString(R.string.prefs_breadcrumb), null);

        // setParentTitle(getResources().getString(R.string.activity_preferences)
        // , null , null);

        // getFragmentManager().beginTransaction()
        // .replace(android.R.id.content, new MyPrefsFragment()).commit();
    }

    @Override
    public Intent getIntent() {
        // Override the original intent to remove headers and directly show
        // MyPrefsFragment
        final Intent intent = new Intent(super.getIntent());
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
                MyPrefsFragment.class.getName());
        intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
        return intent;
    }

    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Update the interface language, independently from the phone's UI
     * language. This does not override the parent function because the Manifest
     * does not include configChanges.
     */
    protected void onConfigurationChanged(String lg) {

        mParkingApp.setLanguage(lg);
        mParkingApp.updateUiLanguage();

        finish();
        Intent intent = new Intent(getApplicationContext(), MyPreferenceActivityHC.class);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent);
    }

    /**
     * This fragment shows the preferences for the first header.
     */
    public static class MyPrefsFragment extends PreferenceFragment implements
            OnSharedPreferenceChangeListener {

        protected SharedPreferences mSharedPrefs;

        ListPreference tUnits;
        ListPreference tLanguage;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PreferenceManager pm = this.getPreferenceManager();
            pm.setSharedPreferencesName(Const.APP_PREFS_NAME);
            pm.setSharedPreferencesMode(Context.MODE_PRIVATE);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            mSharedPrefs = pm.getSharedPreferences();

            tUnits = (ListPreference) findPreference(PrefsNames.UNITS_SYSTEM);
            tLanguage = (ListPreference) findPreference(PrefsNames.LANGUAGE);
        }

        @Override
        public void onResume() {
            super.onResume();

            /**
             * Default units system is ISO
             */
            tUnits.setSummary(getSummaryByValue(mSharedPrefs.getString(PrefsNames.UNITS_SYSTEM,
                    PrefsValues.UNITS_ISO)));

            /**
             * The app's Default language is the phone's language. If not
             * supported, we default to English.
             */
            String lg = mSharedPrefs.getString(PrefsNames.LANGUAGE, Locale.getDefault()
                    .getLanguage());
            if (!lg.equals(PrefsValues.LANG_EN) && !lg.equals(PrefsValues.LANG_FR)) {
                lg = PrefsValues.LANG_EN;
            }
            tLanguage
                    .setSummary(getSummaryByValue(mSharedPrefs.getString(PrefsNames.LANGUAGE, lg)));
            /**
             * This is required because language initially defaults to phone
             * language.
             */
            tLanguage.setValue(lg);

            /**
             * Set up a listener whenever a key changes
             */
            if (mSharedPrefs != null) {
                mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
            }
        }

        @Override
        public void onPause() {
            /**
             * Remove the listener onPause
             */
            if (mSharedPrefs != null) {
                mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
            }

            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            /**
             * onChanged, new preferences values are sent to the AppHelper.
             */
            if (key.equals(PrefsNames.UNITS_SYSTEM)) {
                String units = sharedPreferences.getString(key, PrefsValues.UNITS_ISO);
                tUnits.setSummary(getSummaryByValue(units));
                ((ParkingApp) getActivity().getApplicationContext()).setUnits(units);
            }
            else if (key.equals(PrefsNames.LANGUAGE)) {
                String lg = sharedPreferences.getString(key, Locale.getDefault().getLanguage());
                tLanguage.setSummary(getSummaryByValue(lg));
                ((MyPreferenceActivityHC) getActivity()).onConfigurationChanged(lg);
            }
        }

        /**
         * Get display name of selected preference value. Example: "English" for
         * "en", "Metric" for "iso", etc.
         * 
         * @param index Preference key
         * @return Display name of the value
         */
        private String getSummaryByValue(String index) {
            if (index == null) {
                return "";
            }
            else if (index.equals(PrefsValues.UNITS_ISO)) {
                return getResources().getString(R.string.prefs_units_iso);
            }
            else if (index.equals(PrefsValues.UNITS_IMP)) {
                return getResources().getString(R.string.prefs_units_imperial);
            }
            else if (index.equals(PrefsValues.LANG_FR)) {
                return getResources().getString(R.string.prefs_language_french);
            }
            else if (index.equals(PrefsValues.LANG_EN)) {
                return getResources().getString(R.string.prefs_language_english);
            }
            else {
                return "";
            }
        }
    }
}
