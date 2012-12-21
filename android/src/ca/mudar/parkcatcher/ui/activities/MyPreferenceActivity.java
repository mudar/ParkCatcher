
package ca.mudar.parkcatcher.ui.activities;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.Const.PrefsNames;
import ca.mudar.parkcatcher.Const.PrefsValues;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;

import java.util.Locale;

public class MyPreferenceActivity extends SherlockPreferenceActivity implements
        OnSharedPreferenceChangeListener {
    protected static final String TAG = "MyPreferenceActivity";

    protected ParkingApp mParkingApp;
    protected SharedPreferences mSharedPrefs;

    ListPreference tUnits;
    ListPreference tLanguage;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mParkingApp = (ParkingApp) getApplicationContext();
        mParkingApp.updateUiLanguage();
        
        ActionBar ab = getSupportActionBar(); 
        
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.activity_preferences);

        getPreferenceManager().setSharedPreferencesName(Const.APP_PREFS_NAME);
        addPreferencesFromResource(R.xml.preferences);
        mSharedPrefs = getSharedPreferences(Const.APP_PREFS_NAME, MODE_PRIVATE);

        tUnits = (ListPreference) findPreference(PrefsNames.UNITS_SYSTEM);
        tLanguage = (ListPreference) findPreference(PrefsNames.LANGUAGE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * Default units system is ISO
         */
        tUnits.setSummary(getSummaryByValue(mSharedPrefs.getString(PrefsNames.UNITS_SYSTEM,
                PrefsValues.UNITS_ISO)));

        /**
         * The app's Default language is the phone's language. If not supported,
         * we default to English.
         */
        String lg = mSharedPrefs.getString(PrefsNames.LANGUAGE, Locale.getDefault().getLanguage());
        if (!lg.equals(PrefsValues.LANG_EN) && !lg.equals(PrefsValues.LANG_FR)) {
            lg = PrefsValues.LANG_EN;
        }
        tLanguage.setSummary(getSummaryByValue(mSharedPrefs.getString(PrefsNames.LANGUAGE, lg)));
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
    protected void onPause() {
        if (mSharedPrefs != null) {
            mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
        }

        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /**
         * onChanged, new preferences values are sent to the AppHelper.
         */
        if (key.equals(PrefsNames.UNITS_SYSTEM)) {
            String units = sharedPreferences.getString(key, PrefsValues.UNITS_ISO);
            tUnits.setSummary(getSummaryByValue(units));
            mParkingApp.setUnits(units);
        }
        else if (key.equals(PrefsNames.LANGUAGE)) {
            String lg = sharedPreferences.getString(key, Locale.getDefault().getLanguage());
            tLanguage.setSummary(getSummaryByValue(lg));
            onConfigurationChanged(lg);
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

    /**
     * Update the interface language, independently from the phone's UI
     * language. This does not override the parent function because the Manifest
     * does not include configChanges.
     */
    private void onConfigurationChanged(String lg) {

        mParkingApp.setLanguage(lg);
        mParkingApp.updateUiLanguage();

        finish();
        Intent intent = new Intent(getApplicationContext(), MyPreferenceActivity.class);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent);
    }
}
