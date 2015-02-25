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

package ca.mudar.parkcatcher.ui.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import java.util.Locale;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences mSharedPrefs;

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

        tUnits = (ListPreference) findPreference(Const.PrefsNames.UNITS_SYSTEM);
        tLanguage = (ListPreference) findPreference(Const.PrefsNames.LANGUAGE);
    }

    @Override
    public void onResume() {
        super.onResume();

        /**
         * Default units system is ISO
         */
        tUnits.setSummary(getSummaryByValue(mSharedPrefs.getString(Const.PrefsNames.UNITS_SYSTEM,
                Const.PrefsValues.UNITS_ISO)));

        /**
         * The app's Default language is the phone's language. If not
         * supported, we default to English.
         */
        String lg = mSharedPrefs.getString(Const.PrefsNames.LANGUAGE, Locale.getDefault()
                .getLanguage());
        if (!lg.equals(Const.PrefsValues.LANG_EN) && !lg.equals(Const.PrefsValues.LANG_FR)) {
            lg = Const.PrefsValues.LANG_EN;
        }
        tLanguage
                .setSummary(getSummaryByValue(mSharedPrefs.getString(Const.PrefsNames.LANGUAGE, lg)));
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
        super.onPause();

        /**
         * Remove the listener onPause
         */
        if (mSharedPrefs != null) {
            mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /**
         * onChanged, new preferences values are sent to the AppHelper.
         */
        if (Const.PrefsNames.UNITS_SYSTEM.equals(key)) {
            String units = sharedPreferences.getString(key, Const.PrefsValues.UNITS_ISO);
            tUnits.setSummary(getSummaryByValue(units));
            ((ParkingApp) getActivity().getApplicationContext()).setUnits(units);
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
        if (Const.PrefsValues.UNITS_ISO.equals(index)) {
            return getResources().getString(R.string.prefs_units_iso);
        } else if (Const.PrefsValues.UNITS_IMP.equals(index)) {
            return getResources().getString(R.string.prefs_units_imperial);
        } else if (Const.PrefsValues.LANG_FR.equals(index)) {
            return getResources().getString(R.string.prefs_language_french);
        } else if (Const.PrefsValues.LANG_EN.equals(index)) {
            return getResources().getString(R.string.prefs_language_english);
        } else {
            return "";
        }
    }
}