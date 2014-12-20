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

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;

public abstract class ToolbarActivity extends ActionBarActivity {
    private static final String TAG = "ToolbarActivity";

    private Toolbar mActionBarToolbar = null;

    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                ViewCompat.setElevation(mActionBarToolbar,
                        getResources().getDimensionPixelSize(R.dimen.headerbar_elevation));
                setSupportActionBar(mActionBarToolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
        return mActionBarToolbar;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((ParkingApp) getApplicationContext()).updateUiLanguage();
        super.onCreate(savedInstanceState);
    }
}
