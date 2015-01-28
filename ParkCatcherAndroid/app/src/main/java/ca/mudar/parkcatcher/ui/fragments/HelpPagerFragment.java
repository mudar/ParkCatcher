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

package ca.mudar.parkcatcher.ui.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.ui.adapters.HelpPagerAdapter;

public class HelpPagerFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_help_pager, container, false);
        ViewCompat.setElevation(view.findViewById(R.id.tabs),
                getResources().getDimensionPixelSize(R.dimen.headerbar_elevation));

        final ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
        final HelpPagerAdapter adapter = new HelpPagerAdapter(getFragmentManager(), getActivity());

        pager.setAdapter(adapter);
        pager.setCurrentItem(Const.HelpTabs.STOPPING);

        // Bind the tabs to the ViewPager
        final PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabs.setViewPager(pager);

        // Style the tabs
        tabs.setTextColorResource(R.color.text_tabs_help);
        tabs.setTextSize(getResources().getDimensionPixelSize(R.dimen.tabs_text_size));
        tabs.setTypeface(null, Typeface.NORMAL);

        return view;
    }
}
