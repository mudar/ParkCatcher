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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.ui.activities.base.ToolbarActivity;
import ca.mudar.parkcatcher.ui.fragments.SettingsFragment;

public class SettingsActivity extends ToolbarActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "SettingsActivity";

    private SharedPreferences mSharedPrefs;

    public static Intent newIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.activity_preferences);
        setContentView(R.layout.activity_toolbar_header);
        getActionBarToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ParkingApp mParkingApp = (ParkingApp) getApplicationContext();
        mParkingApp.updateUiLanguage();

        mSharedPrefs = getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);

        if (savedInstanceState == null) {
            final SettingsFragment fragment = new SettingsFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        /**
         * Remove the listener onPause
         */
        if (mSharedPrefs != null) {
            mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    /**
     * Update the interface language, independently from the phone's UI language.
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Const.PrefsNames.LANGUAGE.equals(key)) {
            // Recreate activity using a crossfade
            final Intent intent = SettingsActivity.newIntent(this);
            this.finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
        }
    }

}
