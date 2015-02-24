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
import android.support.v4.view.ViewCompat;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.ui.activities.base.NavdrawerActivity;
import ca.mudar.parkcatcher.ui.fragments.HelpPagerFragment;

public class HelpActivity extends NavdrawerActivity {
    private static final String TAG = "HelpActivity";

    public static Intent newIntent(Context context) {
        final Intent intent = new Intent(context, HelpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.activity_help);
        setContentView(R.layout.activity_navdrawer);

        getActionBarToolbar().setNavigationIcon(R.drawable.ic_action_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Remove elevation, to be seamless with tabs
        ViewCompat.setElevation(getActionBarToolbar(), 0);

        if (savedInstanceState == null) {
            final HelpPagerFragment fragment = new HelpPagerFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        final ParkingApp parkingApp = (ParkingApp) getApplicationContext();
        parkingApp.setHasViewedTutorial(true);
    }

    @Override
    protected int getDefaultNavDrawerItem() {
        return Const.NavdrawerSection.HELP;
    }
}
