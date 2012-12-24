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
import ca.mudar.parkcatcher.ui.fragments.DatePickerFragment;
import ca.mudar.parkcatcher.ui.fragments.FavoritesFragment;
import ca.mudar.parkcatcher.ui.fragments.MapFragment;
import ca.mudar.parkcatcher.ui.fragments.NumberPickerFragment;
import ca.mudar.parkcatcher.ui.fragments.NumberSeekBarFragment;
import ca.mudar.parkcatcher.ui.fragments.TimePickerFragment;
import ca.mudar.parkcatcher.utils.ActivityHelper;
import ca.mudar.parkcatcher.utils.ConnectionHelper;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SlidingDrawer;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends LocationFragmentActivity implements ActionBar.TabListener,
        DatePickerFragment.OnParkingCalendarChangedListener,
        TimePickerFragment.OnParkingCalendarChangedListener,
        NumberSeekBarFragment.OnParkingCalendarChangedListener,
        NumberPickerFragment.OnParkingCalendarChangedListener,
        MapFragment.OnMyLocationChangedListener {
    protected static final String TAG = "MainActivity";

    TabHost mTabHost;
    // TabManager mTabManager;

    MapFragment mMapFragment;
    FavoritesFragment mFavoritesFragment;
    private Location initLocation;
    private boolean isCenterOnMyLocation;
    private boolean isPlayservicesOutdated;

    ActivityHelper activityHelper;
    ParkingApp parkingApp;

    @SuppressWarnings("deprecation")
    SlidingDrawer mDrawer;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        // requestWindowFeature(Window.FEATURE_PROGRESS);

        // Log.v(TAG, "onCreate");

        activityHelper = ActivityHelper.createInstance(this);

        parkingApp = (ParkingApp) getApplicationContext();
        parkingApp.updateUiLanguage();

        isPlayservicesOutdated = (GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getApplicationContext()) != ConnectionResult.SUCCESS);

        if (isPlayservicesOutdated) {
            disableLocationUpdates();
            isCenterOnMyLocation = false;
            setContentView(R.layout.activity_playservices_update);
            return;
        }
        else {
            setContentView(R.layout.activity_main);
        }

        // Set the layout containing the two fragments

        setSupportProgressBarIndeterminateVisibility(Boolean.FALSE);
        //

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

        // Initialize the displayed values
        parkingApp.resetParkingCalendar();

        updateParkingTimeTitle();
        updateParkingDateButton();
        updateParkingTimeButton();
        updateParkingDurationButton();

        mDrawer = (SlidingDrawer) findViewById(R.id.drawer_time);
        mDrawer.animateOpen();

        // Toggle the zoom controller on drawer open/close
        // mDrawer.setOnDrawerOpenListener(new
        // SlidingDrawer.OnDrawerOpenListener() {
        // @Override
        // public void onDrawerOpened() {
        // mMapFragment.toggleZoomControlDisplay(false);
        // }
        // });
        // mDrawer.setOnDrawerCloseListener(new
        // SlidingDrawer.OnDrawerCloseListener() {
        // @Override
        // public void onDrawerClosed() {
        // // mMapFragment.toggleZoomControlDisplay(true);
        // }
        // });

        initLocation = null;

        Integer latitude = getIntent().getIntExtra(Const.INTENT_EXTRA_GEO_LAT, Integer.MIN_VALUE);
        Integer longitude = getIntent().getIntExtra(Const.INTENT_EXTRA_GEO_LNG, Integer.MIN_VALUE);

        if (!latitude.equals(Integer.MIN_VALUE) && !longitude.equals(Integer.MIN_VALUE)) {
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
        isCenterOnMyLocation = true;
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

        if (initLocation != null || isCenterOnMyLocation) {
            mMapFragment.setMapCenter(initLocation);
        }
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

        if (keyCode == KeyEvent.KEYCODE_MENU) {
            mMapFragment.searchToggle(false);
        }
        else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            mMapFragment.searchToggle(true);
        }

        return super.onKeyUp(keyCode, event);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
        // Log.v(TAG, "onTab Selected");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (tab.getTag().equals(Const.TAG_TABS_MAP)) {
            ft.show(mMapFragment);
            ft.commit();
        }
        else if (tab.getTag().equals(Const.TAG_TABS_FAVORITES)) {
            ft.show(mFavoritesFragment);
            ft.commit();
        }

        if (mDrawer != null && mDrawer.isOpened()) {
            mDrawer.animateClose();
        }
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
        // Log.v(TAG, "onTab Unselected");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (tab.getTag().equals(Const.TAG_TABS_MAP)) {
            ft.hide(mMapFragment);
            ft.commit();
        }
        else if (tab.getTag().equals(Const.TAG_TABS_FAVORITES)) {
            ft.hide(mFavoritesFragment);
            ft.commit();
        }
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {
        // Log.v(TAG, "onTab Reselected");

        if (tab.getTag().equals(Const.TAG_TABS_MAP)) {
            isCenterOnMyLocation = true;

            mMapFragment.resetMapCenter();
            parkingApp.resetParkingCalendar();

            updateParkingTimeTitle();
            updateParkingDateButton();
            updateParkingTimeButton();
            updateParkingDurationButton();

            mMapFragment.downloadOverlaysForced();
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

        mMapFragment.downloadOverlaysForced();
    }

    @Override
    public void setParkingTime(int hourOfDay, int minute) {
        parkingApp.setParkingTime(hourOfDay, minute);

        updateParkingTimeButton();

        updateParkingTimeTitle();

        mMapFragment.downloadOverlaysForced();
    }

    @Override
    public void setParkingDuration(int duration) {
        parkingApp.setParkingDuration(duration);

        updateParkingDurationButton();

        updateParkingTimeTitle();

        mMapFragment.downloadOverlaysForced();
    }

    @Override
    public void OnMyLocationChanged(final Location location) {
        Log.v(TAG, "OnMyLocationChanged");
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
        // TODO Auto-generated method stub
        if (mDrawer.isOpened()) {
            mDrawer.animateClose();
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

        GregorianCalendar c = parkingApp.getParkingCalendar();
        int duration = parkingApp.getParkingDuration();

        Date date = c.getTime();

        SimpleDateFormat df = new SimpleDateFormat(getResources().getString(
                R.string.drawer_title_day), Locale.getDefault());

        String day = df.format(date);
        // Required for French: capitalize first character
        day = day.substring(0, 1).toUpperCase() + day.substring(1);

        df = new SimpleDateFormat(getResources().getString(
                R.string.drawer_button_time), Locale.getDefault());
        String time = df.format(c.getTime());

        String sTimeTitle;
        if (duration == 1) {
            sTimeTitle = String.format(getResources().getString(R.string.drawer_time_title), day,
                    time);
        }
        else {
            sTimeTitle = String.format(getResources().getString(R.string.drawer_time_title_plural),
                    day, time, duration);
        }

        TextView vTimeTitle = (TextView) findViewById(R.id.drawer_time_title);
        if (vTimeTitle != null)
            vTimeTitle.setText(sTimeTitle);
    }

    private void updateParkingDateButton() {
        GregorianCalendar c = parkingApp.getParkingCalendar();

        SimpleDateFormat df = new SimpleDateFormat(getResources().getString(
                R.string.drawer_button_date), Locale.getDefault());
        String date = df.format(c.getTime());
        // Required for French: capitalize first character
        date = date.substring(0, 1).toUpperCase() + date.substring(1);

        ((Button) findViewById(R.id.btn_day)).setText(date);
    }

    private void updateParkingTimeButton() {
        GregorianCalendar c = parkingApp.getParkingCalendar();

        SimpleDateFormat df = new SimpleDateFormat(getResources().getString(
                R.string.drawer_button_time), Locale.getDefault());
        String time = df.format(c.getTime());
        ((Button) findViewById(R.id.btn_start)).setText(time);
    }

    private void updateParkingDurationButton() {
        int duration = parkingApp.getParkingDuration();

        if (duration == 1) {
            ((Button) findViewById(R.id.btn_duration)).setText(R.string.drawer_button_duration);
        }
        else {
            ((Button) findViewById(R.id.btn_duration)).setText(String.format(getResources()
                    .getString(R.string.drawer_button_duration_plural), duration));
        }

    }

}
