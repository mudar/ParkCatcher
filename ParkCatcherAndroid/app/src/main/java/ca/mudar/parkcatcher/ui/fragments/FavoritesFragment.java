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

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.GregorianCalendar;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.model.Queries;
import ca.mudar.parkcatcher.provider.ParkingContract.Posts;
import ca.mudar.parkcatcher.ui.adapters.FavoritesAdapter;
import ca.mudar.parkcatcher.ui.views.SlidingUpCalendar;
import ca.mudar.parkcatcher.utils.ParkingTimeHelper;

public class FavoritesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        CalendarFilterFragment.CalendarFilterUpdatedListener,
        SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "FavoritesFragment";

    private View mView;
    private FavoritesAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private static String[] getSelectionArgs(GregorianCalendar calendar, int duration) {
        final double hourOfWeek = ParkingTimeHelper.getHourOfWeek(calendar);
        final int dayOfYear = ParkingTimeHelper.getIsoDayOfYear(calendar);

        return new String[]{
                Double.toString(hourOfWeek),
                Integer.toString(duration),
                Integer.toString(dayOfYear)
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.list_favorites, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh);
        setUpSwipeRefreshLayout(mSwipeRefreshLayout);

        final RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(null);

        mAdapter = new FavoritesAdapter(getActivity(),
                R.layout.list_item_favorites,
                null
        );
        mAdapter.setFooterLayout(R.layout.list_footer_favorites);

        recyclerView.setAdapter(mAdapter);

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
                Posts.DISTANCE_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        toggleProgressBar(false);
        mView.findViewById(R.id.favorites_loading).setVisibility(View.GONE);
        if ((data == null) || (data.getCount() == 0)) {
            mView.findViewById(R.id.swipe_refresh).setVisibility(View.GONE);
            mView.findViewById(R.id.favorites_empty).setVisibility(View.VISIBLE);
            toggleSlidingUpCalendar(false);
        } else {
            mView.findViewById(R.id.swipe_refresh).setVisibility(View.VISIBLE);
            mView.findViewById(R.id.favorites_empty).setVisibility(View.GONE);
            toggleSlidingUpCalendar(true);
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

    @Override
    public void onRefresh() {
        final ParkingApp parkingApp = (ParkingApp) getActivity().getApplicationContext();
        refreshList(parkingApp.getParkingCalendar(), parkingApp.getParkingDuration());
    }

    private void refreshList(GregorianCalendar calendar, int duration) {
        toggleProgressBar(true);

        final Bundle args = new Bundle();
        args.putStringArray(Const.KEY_BUNDLE_CURSOR_SELECTION,
                getSelectionArgs(calendar, duration));

        getLoaderManager().restartLoader(Queries.Favorites._TOKEN, args, this);
    }

    private void setUpSwipeRefreshLayout(SwipeRefreshLayout mSwipeRefreshLayout) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mSwipeRefreshLayout.setColorSchemeResources(
                    R.color.refresh_color_1,
                    R.color.refresh_color_2,
                    R.color.refresh_color_1,
                    R.color.refresh_color_2);
        }
    }

    private void toggleProgressBar(boolean isLoading) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(isLoading);
        }
    }

    private void toggleSlidingUpCalendar(boolean hasItems) {
        if (getActivity() instanceof SlidingUpCalendar.SlidingUpCalendarCallbacks) {
            SlidingUpCalendar.SlidingUpCalendarCallbacks listener =
                    (SlidingUpCalendar.SlidingUpCalendarCallbacks) getActivity();
            if (hasItems) {
                listener.showSlidingUpCalendar();
            } else {
                listener.hideSlidingUpCalendar();
            }
        }
    }
}
