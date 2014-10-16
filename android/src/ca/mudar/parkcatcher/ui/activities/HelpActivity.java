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

import ca.mudar.parkcatcher.Const.HelpPages;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.ui.fragments.HelpFragment;
import ca.mudar.parkcatcher.ui.widgets.HelpFragmentPagerAdapter;
import ca.mudar.parkcatcher.utils.ActivityHelper;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TitlePageIndicator;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

public class HelpActivity extends SherlockFragmentActivity implements
        ViewPager.OnPageChangeListener {
    protected static final String TAG = "HelpActivity";

    private HelpFragmentPagerAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ParkingApp parkingApp = (ParkingApp) getApplicationContext();
        
        parkingApp.updateUiLanguage();
        parkingApp.setHasViewedTutorial(true);

        setContentView(R.layout.activity_help);

        mAdapter = new HelpFragmentPagerAdapter(
                getSupportFragmentManager(), getResources());

        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(mAdapter);
        pager.setCurrentItem(HelpPages.STOPPING);

        final TitlePageIndicator indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);

        indicator.setOnPageChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ActivityHelper activityHelper = ActivityHelper.createInstance(this);

        return (activityHelper.onOptionsItemSelected(item) || super.onOptionsItemSelected(item));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == HelpPages.ARROW) {
            HelpFragment arrowFragment = (HelpFragment) mAdapter.getItem(HelpPages.ARROW);
            View root = findViewById(R.id.root_help_arrow);
            if (root != null) {
                arrowFragment.startLoadingImages(root, getResources());
            }
        }
    }
}
