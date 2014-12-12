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

import ca.mudar.parkcatcher.R;

import com.actionbarsherlock.app.SherlockFragment;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends SherlockFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_about, container, false);

        /**
         * Display version number in the About header.
         */
        ((TextView) root.findViewById(R.id.about_project_version))
                .setText(String.format(getResources().getString(R.string.about_project_version),
                        getResources().getString(R.string.app_version)));

        /**
         * Handle web links.
         */
        MovementMethod method = LinkMovementMethod.getInstance();
        ((TextView) root.findViewById(R.id.about_credits_1)).setMovementMethod(method);
        ((TextView) root.findViewById(R.id.about_credits_2)).setMovementMethod(method);
        ((TextView) root.findViewById(R.id.about_credits_3)).setMovementMethod(method);
        ((TextView) root.findViewById(R.id.about_open_data)).setMovementMethod(method);
        ((TextView) root.findViewById(R.id.about_project_url)).setMovementMethod(method);

        return root;
    }
}
