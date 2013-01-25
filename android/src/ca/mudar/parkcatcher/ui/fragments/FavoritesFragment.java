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

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.provider.ParkingContract.Favorites;
import ca.mudar.parkcatcher.provider.ParkingContract.Posts;
import ca.mudar.parkcatcher.ui.activities.DetailsActivity;
import ca.mudar.parkcatcher.ui.widgets.PostsCursorAdapter;

import com.actionbarsherlock.app.SherlockListFragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class FavoritesFragment extends SherlockListFragment implements LoaderCallbacks<Cursor> {
    protected static final String TAG = "FavoritesFragment";

    ParkingApp parkingApp;

    private View rootView;
    protected Cursor cursor = null;
    protected PostsCursorAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(null);

        parkingApp = (ParkingApp) getActivity().getApplicationContext();

        mAdapter = new PostsCursorAdapter(getActivity(),
                R.layout.fragment_list_item_favorites,
                cursor,
                new String[] {
                        Favorites.LABEL, Posts.GEO_DISTANCE
                },
                new int[] {
                        R.id.favorite_name, R.id.favorite_distance
                },
                0);

        setListAdapter(mAdapter);

        Bundle args = new Bundle();
        args.putStringArray(Const.KEY_BUNDLE_CURSOR_SELECTION, getSelectionArgs());

        getLoaderManager().initLoader(FavoritesQuery._TOKEN, args, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_list_favorites, null);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor c = mAdapter.getCursor();

        if ((position < 0) || (position == c.getCount())) {
            return;
        }

        c.moveToPosition(position);
        int idPost = c.getInt(FavoritesQuery.ID_POST);

        Intent intent = new Intent(getSherlockActivity(), DetailsActivity.class);
        intent.putExtra(Const.INTENT_EXTRA_POST_ID, idPost);
        getSherlockActivity().startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] selectionArgs = null;
        if (id == FavoritesQuery._TOKEN && args.containsKey(Const.KEY_BUNDLE_CURSOR_SELECTION)) {
            selectionArgs = args.getStringArray(Const.KEY_BUNDLE_CURSOR_SELECTION);
        }

        return new CursorLoader(getSherlockActivity().getApplicationContext(),
                Posts.CONTENT_STARRED_URI,
                FavoritesQuery.FAVORITES_SUMMARY_PROJECTION,
                null,
                selectionArgs,
                Posts.FORBIDDEN_DISTANCE_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        if ((data == null) || (data.getCount() == 0)) {
            rootView.findViewById(android.R.id.empty).setVisibility(View.GONE);
            rootView.findViewById(R.id.favorites_empty_list).setVisibility(View.VISIBLE);
        }
        else {
            rootView.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.favorites_empty_list).setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void refreshList() {
        Bundle args = new Bundle();
        args.putStringArray(Const.KEY_BUNDLE_CURSOR_SELECTION, getSelectionArgs());

        getLoaderManager().restartLoader(FavoritesQuery._TOKEN, args, this);
    }

    private String[] getSelectionArgs() {

        final GregorianCalendar parkingCalendar = parkingApp.getParkingCalendar();

        final int dayOfWeek = (parkingCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 7
                : parkingCalendar.get(Calendar.DAY_OF_WEEK) - 1);
        final double parkingHour = parkingCalendar.get(Calendar.HOUR_OF_DAY)
                + Math.round(parkingCalendar.get(Calendar.MINUTE) / 0.6) / 100.00d;
        final double hourOfWeek = parkingHour + (dayOfWeek - 1) * 24;

        // API uses values 0-365 (or 364)
        final int dayOfYear = parkingCalendar.get(Calendar.DAY_OF_YEAR) - 1;

        final int duration = parkingApp.getParkingDuration();

        return new String[] {
                Double.toString(hourOfWeek),
                Integer.toString(duration),
                Integer.toString(dayOfYear)
        };
    }

    public static interface FavoritesQuery {
        int _TOKEN = 0x20;

        final String[] FAVORITES_SUMMARY_PROJECTION = new String[] {
                Posts._ID,
                Posts.ID_POST,
                Favorites.LABEL,
                Posts.GEO_DISTANCE,
                Posts.IS_FORBIDDEN,
        };
        final int _ID = 0;
        final int ID_POST = 1;
        final int LABEL = 2;
        final int GEO_DISTANCE = 3;
        final int IS_FORBIDDEN = 4;
    }
}
