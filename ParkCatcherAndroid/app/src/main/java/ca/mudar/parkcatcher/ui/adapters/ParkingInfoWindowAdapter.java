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

import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import ca.mudar.parkcatcher.R;

public class ParkingInfoWindowAdapter implements InfoWindowAdapter {

    private final View view;

    public ParkingInfoWindowAdapter(View view) {

        this.view = view;
    }

    /**
     * Override to transform Snippet String into SpannableString, allowing the
     * use of line-separators.
     */
    @Override
    public View getInfoContents(Marker marker) {

        String title = marker.getTitle();
        final String snippet = marker.getSnippet();
        final TextView titleUi = (TextView) view.findViewById(R.id.title);
        final View subtitleUi = view.findViewById(R.id.subtitle);
        final TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));

        /**
         * TWEAK: to enable use of the OnInfoWindowClickListener, the title
         * actually holds the id_post. In our case, all infoWindows have the
         * same static title "Parking Allowed". So if the title can be converted
         * to an iteger, we hide it and display the static title. Otherwise, we
         * display the String title. Snippet is checked to verify that the
         * marker's title is not the user's address search string.
         */
        if (snippet != null) {
            try {
                int id = Integer.valueOf(title);
                title = null;
            } catch (NumberFormatException e) {
                // Nothing to do here, title is not an integer so it
            }
        }

        if (title != null) {
            titleUi.setText(title);
        }
        else {
            titleUi.setText(R.string.map_marker_snippet_title);
        }

        if (snippet == null) {
            subtitleUi.setVisibility(View.GONE);
            snippetUi.setVisibility(View.GONE);
        } else {
            subtitleUi.setVisibility(View.VISIBLE);
            snippetUi.setVisibility(View.VISIBLE);
            final SpannableString snippetText = new SpannableString(snippet);
            snippetUi.setText(snippetText);
        }
        return view;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

}
