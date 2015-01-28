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

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.ui.activities.base.NavdrawerActivity;
import ca.mudar.parkcatcher.ui.fragments.AboutFragment;
import ca.mudar.parkcatcher.utils.IntentHelper;

public class AboutActivity extends NavdrawerActivity {
    private static final String TAG = "AboutActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.activity_about);
        setContentView(R.layout.activity_about);

        getActionBarToolbar().setNavigationIcon(R.drawable.ic_action_arrow_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Remove elevation, to be seamless with title/subtitle
        ViewCompat.setElevation(getActionBarToolbar(), 0);
        ViewCompat.setElevation(findViewById(R.id.about_header),
                getResources().getDimensionPixelSize(R.dimen.headerbar_elevation));

        if (savedInstanceState == null) {
            final AboutFragment fragment = new AboutFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, fragment)
                    .commit();
        }
    }

    @Override
    protected int getDefaultNavDrawerItem() {
        return Const.NavdrawerSection.ABOUT;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_about, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_rate) {
            IntentHelper.sendPlaystoreIntent(this, R.string.package_parkcatcher);
            return true;
        } else if (item.getItemId() == R.id.action_share) {
            //  Native sharing
            IntentHelper.sendShareIntent(this, R.string.share_intent_title, R.string.share_intent_text);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
