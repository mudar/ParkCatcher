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

package ca.mudar.parkcatcher.ui.activities.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.ui.activities.AboutActivity;
import ca.mudar.parkcatcher.ui.activities.FavoritesActivity;
import ca.mudar.parkcatcher.ui.activities.HelpActivity;
import ca.mudar.parkcatcher.ui.activities.MainActivity;
import ca.mudar.parkcatcher.ui.activities.SettingsActivity;

public abstract class NavdrawerActivity extends ToolbarActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "NavdrawerActivity";

    private static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{
            R.string.navdrawer_item_map,
            R.string.navdrawer_item_favorites,
            R.string.navdrawer_item_help,
            R.string.navdrawer_item_about,
            R.string.navdrawer_item_settings
    };
    private static final int[] NAVDRAWER_ICON_RES_ID = new int[]{
            R.drawable.ic_nav_map,
            R.drawable.ic_nav_star,
            R.drawable.ic_nav_help,
            R.drawable.ic_nav_info,
            R.drawable.ic_nav_settings
    };
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<Integer> mNavDrawerItems = new ArrayList<Integer>();
    private View[] mNavDrawerItemViews = null;
    private int mLastCheckedItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLastCheckedItem = getDefaultNavDrawerItem();

        getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupNavDrawer();

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Update the interface language, independently from the phone's UI language.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Const.PrefsNames.LANGUAGE.equals(key)) {
            final String lg = sharedPreferences.getString(key, Locale.getDefault().getLanguage());

            final ParkingApp parkingApp = (ParkingApp) getApplicationContext();
            parkingApp.setLanguage(lg);
            parkingApp.updateUiLanguage();
            if (Const.SUPPORTS_HONEYCOMB) {
                recreate();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    protected abstract int getDefaultNavDrawerItem();

    private void setupNavDrawer() {
        final Toolbar toolbar = getActionBarToolbar();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,
                R.string.navdrawer_open,  /* "open drawer" description for accessibility */
                R.string.navdrawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                mDrawerToggle.syncState();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (DrawerLayout.STATE_DRAGGING == newState || DrawerLayout.STATE_SETTLING == newState) {
//                    mSlidingUpQueue.collapsePanel();
                }
            }

            @Override
            public void syncState() {
                super.syncState();

                final boolean isHomeAsUpEnabled = (getSupportActionBar().getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) != 0;

                mDrawerToggle.setDrawerIndicatorEnabled(!isHomeAsUpEnabled);
                if (isHomeAsUpEnabled) {
                    mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_action_arrow_back);
                    mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NavUtils.navigateUpFromSameTask(NavdrawerActivity.this);
                        }
                    });
                }
            }

            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                final FragmentManager fm = getSupportFragmentManager();
                if (item.getItemId() == android.R.id.home) {
                    if (fm.getBackStackEntryCount() > 0) {
                        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        mDrawerToggle.syncState();
                        return true;
                    } else if (!mDrawerToggle.isDrawerIndicatorEnabled()) {
                        finish();
                    }
                }

                return super.onOptionsItemSelected(item);
            }
        };

        // TODO verify need for this, for Fragments
//        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                mDrawerToggle.syncState();
//            }
//        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        populateNavDrawer();
    }

    private void populateNavDrawer() {
        mNavDrawerItems.clear();

        // Other items that are always in the nav drawer
        mNavDrawerItems.add(Const.NavdrawerSection.MAP);
        mNavDrawerItems.add(Const.NavdrawerSection.FAVORITES);
        mNavDrawerItems.add(Const.NavdrawerSection.HELP);
        mNavDrawerItems.add(Const.NavdrawerSection.ABOUT);
        mNavDrawerItems.add(Const.NavdrawerSection.SETTINGS);

        createNavDrawerItems();
    }

    private void createNavDrawerItems() {
        final ViewGroup mDrawerItemsListContainer = (ViewGroup) findViewById(R.id.navdrawer_items_list);
        if (mDrawerItemsListContainer == null) {
            return;
        }

        mNavDrawerItemViews = new View[mNavDrawerItems.size()];
        mDrawerItemsListContainer.removeAllViews();
        int i = 0;
        for (int itemId : mNavDrawerItems) {
            mNavDrawerItemViews[i] = makeNavDrawerItem(itemId, mDrawerItemsListContainer);
            mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
            ++i;
        }
    }

    private View makeNavDrawerItem(final int itemId, ViewGroup container) {
        final int layoutToInflate = R.layout.navdrawer_item;
        final View view = getLayoutInflater().inflate(layoutToInflate, container, false);

        if (itemId < 0 || itemId >= NAVDRAWER_ICON_RES_ID.length) {
            return view;
        }
        if (itemId == Const.NavdrawerSection.SETTINGS && !Const.SUPPORTS_ICS) {
            // Skip settings for non-ICS devices
            view.setVisibility(View.GONE);
            return view;
        }

        final ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        final TextView titleView = (TextView) view.findViewById(R.id.title);
        final int iconId = NAVDRAWER_ICON_RES_ID[itemId];
        final int titleId = NAVDRAWER_TITLE_RES_ID[itemId];

        final String title = getString(titleId);

        titleView.setText(title);
        iconView.setContentDescription(title);
        iconView.setImageResource(iconId);

        formatNavDrawerItem(view, mLastCheckedItem == itemId);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavDrawerItemClicked(itemId);
            }
        });

        return view;
    }

    private void formatNavDrawerItem(View view, boolean selected) {
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(selected ?
                getResources().getColor(R.color.drawer_active_text) :
                getResources().getColor(R.color.drawer_inactive_text));
        iconView.setColorFilter(selected ?
                getResources().getColor(R.color.navdrawer_icon_tint_selected) :
                getResources().getColor(R.color.navdrawer_icon_tint));
    }

    protected void setSelectedNavDrawerItem(int itemId) {
        if (mNavDrawerItemViews != null) {
            for (int i = 0; i < mNavDrawerItemViews.length; i++) {
                if (i < mNavDrawerItems.size()) {
                    int thisItemId = mNavDrawerItems.get(i);
                    formatNavDrawerItem(mNavDrawerItemViews[i], itemId == thisItemId);
                }
            }
        }
    }

    private void onNavDrawerItemClicked(int position) {
        if (position == mLastCheckedItem) {
            return;
        } else if (position == Const.NavdrawerSection.MAP) {
            showMapActivity();
        } else if (position == Const.NavdrawerSection.FAVORITES) {
            showFavoritesActivity();
        } else if (position == Const.NavdrawerSection.HELP) {
            showHelpActivity();
        } else if (position == Const.NavdrawerSection.ABOUT) {
            showAboutActivity();
        } else if (position == Const.NavdrawerSection.SETTINGS) {
            showSettingActivity();
        }
        mDrawerLayout.closeDrawer(Gravity.LEFT);
//        mLastCheckedItem = position;
//        setSelectedNavDrawerItem(mLastCheckedItem);
    }

    private void showMapActivity() {
        final Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void showFavoritesActivity() {
        final Intent intent = new Intent(this, FavoritesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void showHelpActivity() {
        final Intent intent = new Intent(this, HelpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void showAboutActivity() {
        final Intent intent = new Intent(this, AboutActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void showSettingActivity() {
        final Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
