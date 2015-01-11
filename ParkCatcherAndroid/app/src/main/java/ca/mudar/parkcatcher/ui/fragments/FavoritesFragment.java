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
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.GregorianCalendar;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.model.Queries;
import ca.mudar.parkcatcher.provider.ParkingContract.Posts;
import ca.mudar.parkcatcher.ui.activities.DetailsActivity;
import ca.mudar.parkcatcher.ui.adapters.PostsCursorAdapter;
import ca.mudar.parkcatcher.utils.ParkingTimeHelper;

public class FavoritesFragment extends Fragment implements
        LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener,
        CalendarFilterFragment.CalendarFilterUpdatedListener {
    private static final String TAG = "FavoritesFragment";

    private View mView;
    private PostsCursorAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_list_favorites, container, false);

        mView.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.slider_collapsed_height));

        ListView mListView = (ListView) mView.findViewById(android.R.id.list);

        mListView.setAdapter(null);

        mAdapter = new PostsCursorAdapter(getActivity(),
                R.layout.fragment_list_item_favorites,
                null,
                new String[] {
                        ca.mudar.parkcatcher.provider.ParkingContract.Favorites.LABEL, Posts.GEO_DISTANCE
                },
                new int[] {
                        R.id.favorite_name, R.id.favorite_distance
                },
                0);

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ParkingApp parkingApp = (ParkingApp) getActivity().getApplicationContext();

        final Bundle args = new Bundle();
        args.putStringArray(Const.KEY_BUNDLE_CURSOR_SELECTION,
                getSelectionArgs(parkingApp.getParkingCalendar(), parkingApp.getParkingDuration()));

        getLoaderManager().initLoader(Queries.Favorites._TOKEN, args, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final Cursor c = mAdapter.getCursor();

        if ((position < 0) || (position == c.getCount())) {
            return;
        }

        c.moveToPosition(position);
        int idPost = c.getInt(Queries.Favorites.ID_POST);

        final Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(Const.INTENT_EXTRA_POST_ID, idPost);
        getActivity().startActivity(intent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] selectionArgs = null;
        if (id == Queries.Favorites._TOKEN && args.containsKey(Const.KEY_BUNDLE_CURSOR_SELECTION)) {
            selectionArgs = args.getStringArray(Const.KEY_BUNDLE_CURSOR_SELECTION);
        }

        return new CursorLoader(getActivity().getApplicationContext(),
                Posts.CONTENT_STARRED_URI,
                Queries.Favorites.FAVORITES_SUMMARY_PROJECTION,
                null,
                selectionArgs,
                Posts.FORBIDDEN_DISTANCE_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        if ((data == null) || (data.getCount() == 0)) {
            mView.findViewById(android.R.id.empty).setVisibility(View.GONE);
            mView.findViewById(R.id.favorites_empty_list).setVisibility(View.VISIBLE);
        }
        else {
            mView.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.favorites_empty_list).setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onCalendarFilterChanged(GregorianCalendar calendar, int duration) {
        refreshList(calendar, duration);
    }

    private void refreshList(GregorianCalendar calendar, int duration) {
        final Bundle args = new Bundle();
        args.putStringArray(Const.KEY_BUNDLE_CURSOR_SELECTION,
                getSelectionArgs(calendar,duration));

        getLoaderManager().restartLoader(Queries.Favorites._TOKEN, args, this);
    }

    private String[] getSelectionArgs(GregorianCalendar calendar, int duration) {
        final double hourOfWeek = ParkingTimeHelper.getHourOfWeek(calendar);
        final int dayOfYear = ParkingTimeHelper.getIsoDayOfYear(calendar);

        return new String[] {
                Double.toString(hourOfWeek),
                Integer.toString(duration),
                Integer.toString(dayOfYear)
        };
    }

}
