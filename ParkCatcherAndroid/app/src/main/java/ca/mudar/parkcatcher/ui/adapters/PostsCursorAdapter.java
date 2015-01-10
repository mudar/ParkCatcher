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
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.TextView;

import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.model.Queries;
import ca.mudar.parkcatcher.utils.GeoHelper;

public class PostsCursorAdapter extends SimpleCursorAdapter {
    protected static final String TAG = "PostsCursorAdapter";

    final int colorAllowed;
    final int colorForbidden;

    public PostsCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to,
            int flags) {
        super(context, layout, c, from, to, flags);

        this.colorAllowed = context.getResources().getColor(R.color.listview_text_1);
        this.colorForbidden = context.getResources().getColor(R.color.listview_text_2);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        final int distance = cursor.getInt(Queries.Favorites.GEO_DISTANCE);
        final boolean isForbidden = cursor.getInt(Queries.Favorites.IS_FORBIDDEN) == 1;

        final String sDistance = (distance > 0 ? GeoHelper.getDistanceDisplay(context, distance) : "");
        ((TextView) view.findViewById(R.id.favorite_distance)).setText(sDistance);

        if (isForbidden) {
            view.findViewById(R.id.favorite_is_forbidden).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.favorite_name)).setTextColor(colorForbidden);
        }
        else {
            view.findViewById(R.id.favorite_is_forbidden).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.favorite_name)).setTextColor(colorAllowed);
        }
    }
}
