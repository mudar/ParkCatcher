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

import ca.mudar.parkcatcher.R;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

import android.app.Activity;
import android.text.SpannableString;
import android.view.View;
import android.widget.TextView;

public class MyInfoWindowAdapter implements InfoWindowAdapter {

    private final View mView;

    public MyInfoWindowAdapter(Activity activity) {

        mView = activity.getLayoutInflater().inflate(R.layout.custom_info_window, null);
    }

    /**
     * Override to transform Snippet String into SpannableString, allowing the
     * use of line-separators.
     */
    @Override
    public View getInfoContents(Marker marker) {

        final String snippet = marker.getSnippet();
        final TextView snippetUi = ((TextView) mView.findViewById(R.id.snippet));
        if (snippet == null) {
            snippetUi.setVisibility(View.GONE);
        } else {
            final SpannableString snippetText = new SpannableString(snippet);
            snippetUi.setText(snippetText);
        }
        return mView;
    }

    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

}
