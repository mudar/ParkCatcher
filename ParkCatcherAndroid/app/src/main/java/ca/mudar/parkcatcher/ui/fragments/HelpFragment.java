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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.model.HelpCard;
import ca.mudar.parkcatcher.ui.adapters.HelpCardsAdapter;

public class HelpFragment extends Fragment {
    private static final String TAG = "HelpFragment";

    private int mIndex;

    public static HelpFragment newInstance(int index) {
        final HelpFragment fragment = new HelpFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Const.BundleExtras.HELP_PAGE, index);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIndex = getArguments().getInt(Const.BundleExtras.HELP_PAGE, Const.UNKNOWN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        int layout;
        int itemLayout;
        switch (mIndex) {
            case Const.HelpTabs.STOPPING:
            case Const.HelpTabs.PARKING:
            case Const.HelpTabs.RESTRICTED:
            case Const.HelpTabs.SRRR:
            case Const.HelpTabs.ARROW:
            case Const.HelpTabs.PRIORITY:
                layout = R.layout.fragment_help_page;
                itemLayout = R.layout.list_item_help;
                break;
            case Const.HelpTabs.CELL:
            case Const.HelpTabs.RULES:
                layout = R.layout.fragment_help_page;
                itemLayout = R.layout.list_item_help_text;
                break;
            // case HelpPages.APP:
            default:
                layout = R.layout.fragment_help_app;
                itemLayout = 0;
                break;
        }

        final View view = inflater.inflate(layout, container, false);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        if (recyclerView != null) {
            // App help doesn't use RecyclerView
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(layoutManager);

            final List<HelpCard> data = prepareData(mIndex);

            // specify an adapter (see also next example)
            final RecyclerView.Adapter mAdapter = new HelpCardsAdapter(getActivity(), itemLayout, data);
            recyclerView.setAdapter(mAdapter);
        }

        return view;
    }

    private List<HelpCard> prepareData(int index) {
        final List<HelpCard> data = new ArrayList<HelpCard>();

        switch (index) {
            case Const.HelpTabs.STOPPING:
                data.add(new HelpCard(
                        R.drawable.help_panel_stopping_1,
                        R.string.help_stopping_1)
                        .setImagePlaceholder(R.drawable.help_panel_placeholder_short));
                data.add(new HelpCard(
                        R.drawable.help_panel_stopping_2,
                        R.string.help_stopping_2));
                data.add(new HelpCard(
                        R.drawable.help_panel_stopping_3,
                        R.string.help_stopping_3));
                data.add(new HelpCard(
                        R.drawable.help_panel_stopping_4,
                        R.string.help_stopping_4));
                break;
            case Const.HelpTabs.PARKING:
                data.add(new HelpCard(
                        R.drawable.help_panel_parking_1,
                        R.string.help_parking_1)
                        .setImagePlaceholder(R.drawable.help_panel_placeholder_short));
                data.add(new HelpCard(
                        R.drawable.help_panel_parking_2,
                        R.string.help_parking_2));
                data.add(new HelpCard(
                        R.drawable.help_panel_parking_3,
                        R.string.help_parking_3));
                data.add(new HelpCard(
                        R.drawable.help_panel_parking_6,
                        R.string.help_parking_6)
                        .setImagePlaceholder(0)
                        .setImageBackground(R.drawable.help_panel_parking_6_bg));
                break;
            case Const.HelpTabs.RESTRICTED:
                data.add(new HelpCard(
                        R.drawable.help_panel_restricted_1,
                        R.string.help_restricted_1));
                data.add(new HelpCard(
                        R.drawable.help_panel_restricted_2,
                        R.string.help_restricted_2));
                break;
            case Const.HelpTabs.SRRR:
                data.add(new HelpCard(
                        R.drawable.help_panel_srrr,
                        R.string.help_srrr_1));
                data.add(new HelpCard(0,
                        R.string.help_srrr_2));
                break;
            case Const.HelpTabs.ARROW:
                // Text x2
                data.add(new HelpCard(
                        R.drawable.help_panel_arrow_1,
                        R.string.help_arrow_1)
                        .setImagePlaceholder(R.drawable.help_panel_placeholder_short));
                data.add(new HelpCard(0,
                        R.string.help_arrow_2));
                // Images x4
                data.add(new HelpCard(
                        R.drawable.help_arrow_sw_left, 0)
                        .setImagePlaceholder(R.drawable.help_arrow_empty)
                        .setImageBackground(R.color.transparent));
                data.add(new HelpCard(
                        R.drawable.help_arrow_ne_left, 0)
                        .setImagePlaceholder(R.drawable.help_arrow_empty)
                        .setImageBackground(R.color.transparent));
                data.add(new HelpCard(
                        R.drawable.help_arrow_se_right, 0)
                        .setImagePlaceholder(R.drawable.help_arrow_empty)
                        .setImageBackground(R.color.transparent));
                data.add(new HelpCard(
                        R.drawable.help_arrow_nw_right, 0)
                        .setImagePlaceholder(R.drawable.help_arrow_empty)
                        .setImageBackground(R.color.transparent));
                // Text x1
                data.add(new HelpCard(0,
                        R.string.help_arrow_3));
                // Images x4
                data.add(new HelpCard(
                        R.drawable.help_arrow_ne_right, 0)
                        .setImagePlaceholder(R.drawable.help_arrow_empty)
                        .setImageBackground(R.color.transparent));
                data.add(new HelpCard(
                        R.drawable.help_arrow_sw_right, 0)
                        .setImagePlaceholder(R.drawable.help_arrow_empty)
                        .setImageBackground(R.color.transparent));
                data.add(new HelpCard(
                        R.drawable.help_arrow_nw_left, 0)
                        .setImagePlaceholder(R.drawable.help_arrow_empty)
                        .setImageBackground(R.color.transparent));
                data.add(new HelpCard(
                        R.drawable.help_arrow_se_left, 0)
                        .setImagePlaceholder(R.drawable.help_arrow_empty)
                        .setImageBackground(R.color.transparent));
                // Text x2
                data.add(new HelpCard(
                        R.drawable.help_panel_parking_1,
                        R.string.help_arrow_4)
                        .setImagePlaceholder(R.drawable.help_panel_placeholder_short));
                data.add(new HelpCard(0,
                        R.string.help_arrow_7));
                break;
            case Const.HelpTabs.PRIORITY:
                data.add(new HelpCard(
                        R.drawable.help_panel_priority_1,
                        R.string.help_priority_1)
                        .setImagePlaceholder(R.drawable.help_panel_placeholder_short));
                data.add(new HelpCard(0,
                        R.string.help_priority_2));
                data.add(new HelpCard(
                        R.drawable.help_panel_priority_2,
                        R.string.help_priority_3)
                        .setImagePlaceholder(R.drawable.help_panel_placeholder_long));
                data.add(new HelpCard(
                        R.drawable.help_panel_priority_3,
                        R.string.help_priority_4)
                        .setImagePlaceholder(R.drawable.help_panel_placeholder_long));
                break;
            case Const.HelpTabs.RULES:
                data.add(new HelpCard(0,
                        R.string.help_rules)
                        .setTopDrawable(R.drawable.ic_big_forbidden));
                break;
            case Const.HelpTabs.CELL:
                data.add(new HelpCard(0,
                        R.string.help_cell_1)
                        .setTopDrawable(R.drawable.ic_big_smartphone));
                break;
        }


        return data;
    }
}
