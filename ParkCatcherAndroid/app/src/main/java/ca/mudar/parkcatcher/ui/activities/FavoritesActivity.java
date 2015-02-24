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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.ui.activities.base.NavdrawerActivity;
import ca.mudar.parkcatcher.ui.fragments.FavoritesFragment;
import ca.mudar.parkcatcher.ui.views.SlidingUpCalendar;

public class FavoritesActivity extends NavdrawerActivity implements
        SlidingUpCalendar.SlidingUpCalendarCallbacks {
    private static final String TAG = "FavoritesActivity";

    public static Intent newIntent(Context context) {
        final Intent intent = new Intent(context, FavoritesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        return intent;
    }

    private SlidingUpCalendar mSlidingUpCalendar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.activity_favorites);
        setContentView(R.layout.activity_favorites);

        getActionBarToolbar().setNavigationIcon(R.drawable.ic_action_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FavoritesFragment favoritesFragment;
        if (savedInstanceState == null) {
            favoritesFragment = new FavoritesFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, favoritesFragment, Const.FragmentTags.FAVORITES)
                    .commit();
        } else {
            favoritesFragment = (FavoritesFragment) getSupportFragmentManager()
                    .findFragmentByTag(Const.FragmentTags.FAVORITES);
        }

        mSlidingUpCalendar = (SlidingUpCalendar) findViewById(R.id.sliding_layout);

        // Enable interaction between the Map and sliding-up Calendar filter
        final Fragment calendarFilterFragment = getSupportFragmentManager()
                .findFragmentByTag(Const.FragmentTags.SLIDING_UP_CALENDAR);
        calendarFilterFragment.setTargetFragment(favoritesFragment, Const.RequestCodes.FAVORITES);
    }

    @Override
    protected int getDefaultNavDrawerItem() {
        return Const.NavdrawerSection.FAVORITES;
    }

    @Override
    protected void onNavdrawerStateChanged(int newState) {
        if (DrawerLayout.STATE_DRAGGING == newState || DrawerLayout.STATE_SETTLING == newState) {
            collapseSlidingUpCalendar();
        }
    }

    /**
     * Implements SlidingUpCalendarCallbacks
     */
    @Override
    public void showSlidingUpCalendar() {
        if (mSlidingUpCalendar != null) {
            mSlidingUpCalendar.showPanel();
        }
    }

    /**
     * Implements SlidingUpCalendarCallbacks
     */
    @Override
    public void hideSlidingUpCalendar() {
        if (mSlidingUpCalendar != null) {
            mSlidingUpCalendar.hidePanel();
        }
    }

    /**
     * Implements SlidingUpCalendarCallbacks
     */
    @Override
    public void collapseSlidingUpCalendar() {
        if (mSlidingUpCalendar != null) {
            mSlidingUpCalendar.collapsePanel();
        }
    }
}
