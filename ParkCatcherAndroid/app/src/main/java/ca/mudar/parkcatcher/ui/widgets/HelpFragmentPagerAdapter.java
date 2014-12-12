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

package ca.mudar.parkcatcher.ui.widgets;

import ca.mudar.parkcatcher.Const.HelpPages;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.ui.fragments.HelpFragment;

import com.viewpagerindicator.IconPagerAdapter;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class HelpFragmentPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
    protected static final String TAG = "HelpFragmentPagerAdapter";

    private Resources resources;

    public HelpFragmentPagerAdapter(FragmentManager fm, Resources res) {
        super(fm);

        resources = res;
    }

    @Override
    public int getIconResId(int index) {
        return 0;
    }

    @Override
    public Fragment getItem(int position) {
        return HelpFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return HelpPages.COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case HelpPages.APP:
                return resources.getString(R.string.help_title_app);
            case HelpPages.RULES:
                return resources.getString(R.string.help_title_rules);
            case HelpPages.STOPPING:
                return resources.getString(R.string.help_title_stopping);
            case HelpPages.PARKING:
                return resources.getString(R.string.help_title_parking);
            case HelpPages.RESTRICTED:
                return resources.getString(R.string.help_title_restricted);
            case HelpPages.SRRR:
                return resources.getString(R.string.help_title_srrr);
            case HelpPages.CELL:
                return resources.getString(R.string.help_title_cell);
            case HelpPages.ARROW:
                return resources.getString(R.string.help_title_arrow);
            case HelpPages.PRIORITY:
                return resources.getString(R.string.help_title_priority);
        }

        return String.format(resources.getString(R.string.help_title_default), position + 1);
    }
}
