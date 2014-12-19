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

package ca.mudar.parkcatcher.ui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.ui.fragments.HelpFragment;

public class HelpPagerAdapter extends FragmentPagerAdapter {
    protected static final String TAG = "HelpPagerAdapter";

    private final String[] titles;

    public HelpPagerAdapter(FragmentManager fm, Context context) {
        super(fm);

        this.titles = context.getResources().getStringArray(R.array.help_tabs_array);
    }

    @Override
    public Fragment getItem(int position) {
        return HelpFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return Const.HelpTabs._COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    // TODO verify need for this
//    @Override
//    public void onPageSelected(int position) {
//        if (position == Const.HelpPages.ARROW) {
//            HelpFragment arrowFragment = (HelpFragment) mAdapter.getItem(HelpPages.ARROW);
//            View root = findViewById(R.id.root_help_arrow);
//            if (root != null) {
//                arrowFragment.startLoadingImages(root, getResources());
//            }
//        }
//    }
}
