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
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.widget.TextView;

import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.model.Queries;
import ca.mudar.parkcatcher.utils.GeoHelper;

public class PostsAdapter extends ResourceCursorAdapter {
    private static final String TAG = "PostsAdapter";

    final int colorAllowed;
    final int colorForbidden;
    final Drawable drawableParkingAllowed;
    final Drawable drawableParkingForbidden;

    public PostsAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, layout, c, flags);

        final Resources resources = context.getResources();
        this.colorAllowed = resources.getColor(R.color.listview_text_1);
        this.colorForbidden = resources.getColor(R.color.listview_text_2);
        this.drawableParkingAllowed = resources.getDrawable(R.drawable.ic_parking_allowed);
        this.drawableParkingForbidden = resources.getDrawable(R.drawable.ic_parking_forbidden);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final int idPost = cursor.getInt(Queries.Favorites.ID_POST);
        final String label = cursor.getString(Queries.Favorites.LABEL);
        final int distance = cursor.getInt(Queries.Favorites.GEO_DISTANCE);
        final boolean isForbidden = cursor.getInt(Queries.Favorites.IS_FORBIDDEN) == 1;

        final String sDistance = (distance > 0 ? GeoHelper.getDistanceDisplay(context, distance) : "");
        final TextView uiFavoriteName = (TextView) view.findViewById(R.id.favorite_name);

        uiFavoriteName.setText(label);
        ((TextView) view.findViewById(R.id.favorite_distance)).setText(sDistance);

        if (isForbidden) {
            uiFavoriteName.setCompoundDrawablesWithIntrinsicBounds(drawableParkingForbidden, null, null, null);
        } else {
            uiFavoriteName.setCompoundDrawablesWithIntrinsicBounds(drawableParkingAllowed, null, null, null);
        }

        // Set POST_ID for onItemClick
        view.setTag(R.id.post_id_tag, idPost);
    }
}
