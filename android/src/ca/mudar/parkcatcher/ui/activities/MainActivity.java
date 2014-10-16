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
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.service.SyncService;
import ca.mudar.parkcatcher.ui.fragments.DatePickerFragment;
import ca.mudar.parkcatcher.ui.fragments.FavoritesFragment;
import ca.mudar.parkcatcher.ui.fragments.MapFragment;
import ca.mudar.parkcatcher.ui.fragments.NumberPickerFragment;
import ca.mudar.parkcatcher.ui.fragments.NumberSeekBarFragment;
import ca.mudar.parkcatcher.ui.fragments.TimePickerFragment;
import ca.mudar.parkcatcher.utils.ActivityHelper;
import ca.mudar.parkcatcher.utils.ConnectionHelper;
import ca.mudar.parkcatcher.utils.EulaHelper;
import ca.mudar.parkcatcher.utils.ParkingTimeHelper;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.crittercism.app.Crittercism;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends LocationFragmentActivity implements ActionBar.TabListener,
        DatePickerFragment.OnParkingCalendarChangedListener,
        TimePickerFragment.OnParkingCalendarChangedListener,
        NumberSeekBarFragment.OnParkingCalendarChangedListener,
        NumberPickerFragment.OnParkingCalendarChangedListener,
        MapFragment.OnMyLocationChangedListener {
    protected static final String TAG = "MainActivity";

    MapFragment mMapFragment;
    FavoritesFragment mFavoritesFragment;
    private Location initLocation;
    private boolean isCenterOnMyLocation = true;
    private boolean isPlayservicesOutdated;
    private boolean hasLoadedData;

    ActivityHelper activityHelper;
    ParkingApp parkingApp;

    @SuppressWarnings("deprecation")
    SlidingDrawer mDrawer;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Const.IS_DEBUG) {
            Crittercism.init(getApplicationContext(), Const.CRITTERCISM_APP_ID);
        }

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        activityHelper = ActivityHelper.createInstance(this);

        parkingApp = (ParkingApp) getApplicationContext();
        parkingApp.updateUiLanguage();

        /**
         * Display the GPLv3 licence
         */
        if (!EulaHelper.hasAcceptedEula(this)) {

            if (ConnectionHelper.hasConnection(this)) {
                EulaHelper.showEula(false, this);
            }
            else {
                setContentView(R.layout.activity_no_connection);
                setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
                return;
            }
        }

        hasLoadedData = parkingApp.hasLoadedData();

        if (!hasLoadedData) {
            hasLoadedData = true;

            // The service runs in the background with no listener
            Intent intent = new Intent(Intent.ACTION_SYNC, null, getApplicationContext(),
                    SyncService.class);
            intent.putExtra(Const.INTENT_EXTRA_SERVICE_LOCAL, false);
            intent.putExtra(Const.INTENT_EXTRA_SERVICE_REMOTE, true);
            startService(intent);
        }

        isPlayservicesOutdated = (GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getApplicationContext()) != ConnectionResult.SUCCESS);

        if (isPlayservicesOutdated) {
            disableLocationUpdates();
            isCenterOnMyLocation = false;

            setContentView(R.layout.activity_playservices_update);
            setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);

            return;
        }
        else {
            setContentView(R.layout.activity_main);
            setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
        }

        // Set the layout containing the two fragments

        // Get the fragments
        FragmentManager fm = getSupportFragmentManager();
        mMapFragment = (MapFragment) fm.findFragmentByTag(Const.TAG_FRAGMENT_MAP);
        mFavoritesFragment = (FavoritesFragment) fm.findFragmentByTag(Const.TAG_FRAGMENT_FAVORITES);

        // Create the actionbar tabs
        final ActionBar ab = getSupportActionBar();

        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ab.addTab(ab.newTab().setText(R.string.tab_map).setTabListener(this)
                .setTag(Const.TAG_TABS_MAP));
        ab.addTab(ab.newTab().setText(R.string.tab_favorites).setTabListener(this)
                .setTag(Const.TAG_TABS_FAVORITES));

        initLocation = null;

        double latitude = getIntent().getDoubleExtra(Const.INTENT_EXTRA_GEO_LAT, Double.MIN_VALUE);
        double longitude = getIntent().getDoubleExtra(Const.INTENT_EXTRA_GEO_LNG, Double.MIN_VALUE);

        if (Double.compare(latitude, Double.MIN_VALUE) != 0
                && Double.compare(latitude, Double.MIN_VALUE) != 0) {
            initLocation = new Location(Const.LOCATION_PROVIDER_INTENT);

            initLocation.setLatitude(latitude);
            initLocation.setLongitude(longitude);

            isCenterOnMyLocation = false;
        }
        else {
            isCenterOnMyLocation = true;

            // Initialize the displayed values. This is not done when
            // MainActivity is called from Details activity, to keep the same
            // Calendar.
            parkingApp.resetParkingCalendar();
        }

        updateParkingTimeTitle();
        updateParkingDateButton();
        updateParkingTimeButton();
        updateParkingDurationButton();
        mFavoritesFragment.refreshList();

        mDrawer = (SlidingDrawer) findViewById(R.id.drawer_time);
        mDrawer.animateOpen();
    }

    @Override
    public void onNewIntent(Intent intent) {
        initLocation = null;

        Double latitude = intent.getDoubleExtra(Const.INTENT_EXTRA_GEO_LAT, Double.NaN);
        Double longitude = intent.getDoubleExtra(Const.INTENT_EXTRA_GEO_LNG, Double.NaN);

        if (!latitude.equals(Double.NaN) && !longitude.equals(Double.NaN)) {
            initLocation = new Location(Const.LOCATION_PROVIDER_INTENT);
            initLocation.setLatitude(latitude);
            initLocation.setLongitude(longitude);
            isCenterOnMyLocation = false;
        }
        else {
            isCenterOnMyLocation = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        try {
            outState.putString(Const.KEY_BUNDLE_SELECTED_TAB,
                    getSupportActionBar().getSelectedTab().getTag().toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        isCenterOnMyLocation = false;

        if (getIntent().hasExtra(Const.INTENT_EXTRA_POST_ID)) {
            try {
                final ActionBar ab = getSupportActionBar();
                if (ab.getSelectedTab().getPosition() != Const.TABS_INDEX_MAP) {
                    ab.setSelectedNavigationItem(Const.TABS_INDEX_MAP);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        else if ((savedInstanceState != null)
                && (savedInstanceState.containsKey(Const.KEY_BUNDLE_SELECTED_TAB))) {
            if (savedInstanceState.getString(Const.KEY_BUNDLE_SELECTED_TAB)
                    .equals(Const.TAG_TABS_FAVORITES)) {
                try {
                    final ActionBar ab = getSupportActionBar();
                    if (ab.getSelectedTab().getPosition() != Const.TABS_INDEX_FAVORITES) {
                        ab.setSelectedNavigationItem(Const.TABS_INDEX_FAVORITES);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Const.INTENT_REQ_CODE_EULA) {
            boolean hasAcceptedEula = EulaHelper.acceptEula(resultCode, this);
            if (!hasAcceptedEula) {
                this.finish();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check Playservices status
        if (isPlayservicesOutdated) {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()) != ConnectionResult.SUCCESS) {
                // Still out of date, interrupt onResume()
                disableLocationUpdates();
            }
            else {
                // Playservice updated, display message and restart activity
                parkingApp.showToastText(R.string.toast_playservices_restart, Toast.LENGTH_LONG);
                final Intent intent = getIntent();
                this.finish();
                startActivity(intent);
            }
            return;
        }

        if (!ConnectionHelper.hasConnection(this)) {
            ConnectionHelper.showDialogNoConnection(this);
        }

        // TODO Optimize this using savedInstanceState to avoid reload of
        // identical data onResume
        final Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            updateParkingTimeFromUri(intent.getData());

            updateParkingTimeTitle();
            updateParkingDateButton();
            updateParkingTimeButton();
            updateParkingDurationButton();
            mFavoritesFragment.refreshList();
        }

        // if
        // (getSupportActionBar().getSelectedTab().getTag().equals(Const.TAG_TABS_MAP)
        // || isCenterOnMyLocation) {
        if (initLocation != null || isCenterOnMyLocation) {
            try {
                mMapFragment.setMapCenter(initLocation);
                isCenterOnMyLocation = false;

                final ActionBar ab = getSupportActionBar();
                if (ab.getSelectedTab().getPosition() != Const.TABS_INDEX_MAP) {
                    ab.setSelectedNavigationItem(Const.TABS_INDEX_MAP);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        // }

    }

    @Override
    public void onPause() {
        super.onPause();
        initLocation = null;
        isCenterOnMyLocation = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return (activityHelper.onOptionsItemSelected(item) || super.onOptionsItemSelected(item));
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (mMapFragment != null) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                mMapFragment.searchToggle(false);
            }
            else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                mMapFragment.searchToggle(true);
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (tab.getTag().equals(Const.TAG_TABS_MAP)) {
            if (mDrawer != null) {
                mDrawer.setVisibility(View.VISIBLE);
            }

            // ft.show(mMapFragment);
            ft.hide(mFavoritesFragment);
            ft.commit();
        }
        else if (tab.getTag().equals(Const.TAG_TABS_FAVORITES)) {

            if (mDrawer != null) {
                mDrawer.setVisibility(View.GONE);
            }

            ft.show(mFavoritesFragment);
            // ft.hide(mMapFragment);
            ft.commit();
        }

        if (mDrawer != null && mDrawer.isOpened()) {
            mDrawer.animateClose();
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
        // FragmentTransaction ft =
        // getSupportFragmentManager().beginTransaction();
        //
        // if (tab.getTag().equals(Const.TAG_TABS_MAP)) {
        // ft.hide(mMapFragment);
        // ft.commit();
        // }
        // else if (tab.getTag().equals(Const.TAG_TABS_FAVORITES)) {
        // ft.hide(mFavoritesFragment);
        // ft.commit();
        // }
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
        if (tab.getTag().equals(Const.TAG_TABS_MAP)) {
            isCenterOnMyLocation = true;

            mMapFragment.resetMapCenter();
            parkingApp.resetParkingCalendar();

            updateParkingTimeTitle();
            updateParkingDateButton();
            updateParkingTimeButton();
            updateParkingDurationButton();
            mFavoritesFragment.refreshList();

            mMapFragment.updateOverlaysForced();
        }
        else if (tab.getTag().equals(Const.TAG_TABS_FAVORITES)) {
            // TODO handle reselection of tab
            Log.v(TAG, "TODO: scroll favorites to top and refresh?");
        }
    }

    @Override
    public GregorianCalendar getParkingCalendar() {
        return parkingApp.getParkingCalendar();
    }

    @Override
    public int getParkingDuration() {
        return parkingApp.getParkingDuration();
    }

    @Override
    public void setParkingDate(int year, int month, int day) {

        parkingApp.setParkingDate(year, month, day);

        updateParkingDateButton();

        updateParkingTimeTitle();
        mFavoritesFragment.refreshList();

        mMapFragment.updateOverlaysForced();
    }

    @Override
    public void setParkingTime(int hourOfDay, int minute) {
        parkingApp.setParkingTime(hourOfDay, minute);

        updateParkingTimeButton();

        updateParkingTimeTitle();
        mFavoritesFragment.refreshList();

        mMapFragment.updateOverlaysForced();
    }

    @Override
    public void setParkingDuration(int duration) {
        parkingApp.setParkingDuration(duration);

        updateParkingDurationButton();

        updateParkingTimeTitle();
        mFavoritesFragment.refreshList();

        mMapFragment.updateOverlaysForced();
    }

    @Override
    public void OnMyLocationChanged(final Location location) {
        /**
         * Following code allows the background listener to modify the UI's
         * menu.
         */
        runOnUiThread(new Runnable() {
            public void run() {
                // TODO: verify that new location is sent to service & favorites
                // fragment
                ((ParkingApp) getApplicationContext())
                        .setLocation(location);
                Log.v(TAG, "TODO: OnMyLocationChanged on UI");
                // invalidateOptionsMenu();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public void OnMyMapClickListener() {
        if (mDrawer.isOpened()) {
            mDrawer.animateClose();
        }
    }

    @Override
    public void OnSearchClickListener() {
        OnMyMapClickListener();
    }

    public void retryConnection(View v) {
        if (ConnectionHelper.hasConnection(this)) {
            parkingApp.showToastText(R.string.map_no_connection_restart, Toast.LENGTH_LONG);
            final Intent intent = getIntent();
            this.finish();
            startActivity(intent);
        }
        else {
            ConnectionHelper.showDialogNoConnection(this);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), Const.TAG_FRAGMENT_PICKER_DATE);
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), Const.TAG_FRAGMENT_PICKER_TIME);
    }

    // Build < v11
    public void showNumberSeekBarDialog(View v) {
        DialogFragment newFragment = new NumberSeekBarFragment();
        newFragment.show(getSupportFragmentManager(), Const.TAG_FRAGMENT_SEEKBAR_NUMBER);
    }

    // Build >= 11
    public void showNumberPickerDialog(View v) {
        DialogFragment newFragment = new NumberPickerFragment();
        newFragment.show(getSupportFragmentManager(), Const.TAG_FRAGMENT_PICKER_NUMBER);
    }

    @SuppressLint("DefaultLocale")
    private void updateParkingTimeTitle() {
        final GregorianCalendar c = parkingApp.getParkingCalendar();
        final int duration = parkingApp.getParkingDuration();

        ((TextView) findViewById(R.id.drawer_time_title)).setText(
                ParkingTimeHelper.getTitle(this, c, duration));
    }

    private void updateParkingDateButton() {
        final GregorianCalendar c = parkingApp.getParkingCalendar();

        ((Button) findViewById(R.id.btn_day)).setText(ParkingTimeHelper.getDate(this, c));
    }

    private void updateParkingTimeButton() {
        final GregorianCalendar c = parkingApp.getParkingCalendar();

        ((Button) findViewById(R.id.btn_start)).setText(ParkingTimeHelper.getTime(this, c));
    }

    private void updateParkingDurationButton() {
        final int duration = parkingApp.getParkingDuration();

        ((Button) findViewById(R.id.btn_duration)).setText(ParkingTimeHelper.getDuration(this,
                duration));
    }

    private void updateParkingTimeFromUri(Uri uri) {
        Log.v(TAG, "updateParkingTimeFromUri");
        List<String> pathSegments = uri.getPathSegments();

        // http://www.capteurdestationnement.com/map/search/2/15.5/12
        // http://www.capteurdestationnement.com/map/search/2/15.5/12/h2w2e7

        if ((pathSegments.size() >= 5)
                && (pathSegments.get(0).equals(Const.INTENT_EXTRA_URL_PATH_MAP))
                && (pathSegments.get(1).equals(Const.INTENT_EXTRA_URL_PATH_SEARCH))) {

            try {
                final int day = Integer.valueOf(pathSegments.get(2));
                final double time = Double.valueOf(pathSegments.get(3));
                final int duration = Integer.valueOf(pathSegments.get(4));

                final int hourOfDay = (int) time;
                final int minute = (int) ((time - hourOfDay) * 60);

                GregorianCalendar calendar = new GregorianCalendar();

                calendar.set(Calendar.DAY_OF_WEEK, day == 7 ? Calendar.SUNDAY : day + 1);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                parkingApp.setParkingCalendar(calendar);
                parkingApp.setParkingDuration(duration);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

}
