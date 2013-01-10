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
import ca.mudar.parkcatcher.provider.ParkingContract.Posts;
import ca.mudar.parkcatcher.provider.ParkingContract.PostsColumns;

import com.actionbarsherlock.app.SherlockListFragment;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

public class FavoritesFragment extends SherlockListFragment implements LoaderCallbacks<Cursor> {

    protected Cursor cursor = null;
    protected SimpleCursorAdapter mAdapter;

    // @Override
    // public View onCreateView(LayoutInflater inflater, ViewGroup container,
    // Bundle savedInstanceState) {
    // return inflater.inflate(R.layout.fragment_favorites, container, false);
    // }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setListAdapter(null);

        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.fragment_list_item_favorites,
                cursor,
                new String[] {
                        PostsColumns.LAT, PostsColumns.LNG, PostsColumns.GEO_DISTANCE,
                        PostsColumns.ID_POST
                },
                new int[] {
                        R.id.favorite_name, R.id.favorite_desc, R.id.favorite_distance
                },
                0);

        setListAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        return new CursorLoader(getSherlockActivity().getApplicationContext(),
                Posts.CONTENT_STARRED_URI, FavoritesQuery.FAVORITES_SUMMARY_PROJECTION, null, null,
                Posts.DISTANCE_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // TODO Auto-generated method stub
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);
    }

    public static interface FavoritesQuery {
        // int _TOKEN = 0x10;

        final int _ID = 0;
        final int ID_POST = 1;
        final int LAT = 2;
        final int LNG = 3;
        final int GEO_DISTANCE = 4;

        final String[] FAVORITES_SUMMARY_PROJECTION = new String[] {
                BaseColumns._ID, PostsColumns.ID_POST, PostsColumns.LAT, PostsColumns.LNG,
                PostsColumns.GEO_DISTANCE
        };
    }
}
