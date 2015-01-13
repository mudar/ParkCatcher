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
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.model.Queries;
import ca.mudar.parkcatcher.ui.activities.DetailsActivity;
import ca.mudar.parkcatcher.utils.GeoHelper;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {
    private static final String TAG = "FavoritesAdapter";

    private final Context context;
    private final int layout;
    private final Drawable drawableParkingAllowed;
    private final Drawable drawableParkingForbidden;
    private Cursor mCursor;

    public FavoritesAdapter(Context context, int layout, Cursor c) {
        this.context = context;
        this.layout = layout;
        this.mCursor = c;

        // Pre-load drawables
        final Resources resources = context.getResources();
        this.drawableParkingAllowed = resources.getDrawable(R.drawable.ic_parking_allowed);
        this.drawableParkingForbidden = resources.getDrawable(R.drawable.ic_parking_forbidden);
    }

    @Override
    public FavoritesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        return new FavoritesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FavoritesViewHolder holder, int position) {
        if (mCursor == null || !mCursor.moveToPosition(position)) {
            return;
        }

        final int idPost = mCursor.getInt(Queries.Favorites.ID_POST);
        final String label = mCursor.getString(Queries.Favorites.LABEL);
        final int distance = mCursor.getInt(Queries.Favorites.GEO_DISTANCE);
        final boolean isForbidden = mCursor.getInt(Queries.Favorites.IS_FORBIDDEN) == 1;

        final String sDistance = (distance > 0 ? GeoHelper.getDistanceDisplay(context, distance) : "");

        holder.idPost = idPost;
        holder.label.setText(label);
        holder.distance.setText(sDistance);

        if (isForbidden) {
            holder.label.setCompoundDrawablesWithIntrinsicBounds(drawableParkingForbidden, null, null, null);
        } else {
            holder.label.setCompoundDrawablesWithIntrinsicBounds(drawableParkingAllowed, null, null, null);
        }
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public static class FavoritesViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        private int idPost = -1;
        private TextView label;
        private TextView distance;

        public FavoritesViewHolder(View itemView) {
            super(itemView);

            label = (TextView) itemView.findViewById(R.id.favorite_label);
            distance = (TextView) itemView.findViewById(R.id.favorite_distance);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (idPost > 0) {
                final Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                intent.putExtra(Const.INTENT_EXTRA_POST_ID, idPost);
                v.getContext().startActivity(intent);
            }
        }
    }
}
