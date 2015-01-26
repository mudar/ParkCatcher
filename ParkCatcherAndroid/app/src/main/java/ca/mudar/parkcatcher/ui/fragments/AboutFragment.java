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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.mudar.parkcatcher.R;

public class AboutFragment extends Fragment implements
        View.OnClickListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_about, container, false);

        view.findViewById(R.id.about_open_data).setOnClickListener(this);
        view.findViewById(R.id.about_source_code).setOnClickListener(this);
        view.findViewById(R.id.about_credits_dev).setOnClickListener(this);
        view.findViewById(R.id.about_credits_design).setOnClickListener(this);
        view.findViewById(R.id.about_montreal_ouvert).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        if (id == R.id.about_open_data) {
            openWebPage(R.string.url_about_open_data);
        } else if (id == R.id.about_source_code) {
            openWebPage(R.string.url_about_source_code);
        } else if (id == R.id.about_credits_dev) {
            openWebPage(R.string.url_about_credits_dev);
        } else if (id == R.id.about_credits_design) {
            openWebPage(R.string.url_about_credits_design);
        } else if (id == R.id.about_montreal_ouvert) {
            openWebPage(R.string.url_about_montreal_ouvert);
        }
    }

    private void openWebPage(int res) {
        final String url = getActivity().getResources().getString(res);
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
