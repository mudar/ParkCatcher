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

package ca.mudar.parkcatcher.ui.listeners;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;

import ca.mudar.parkcatcher.R;

public class SearchViewQueryListener implements
        SearchView.OnQueryTextListener,
        View.OnFocusChangeListener {
    private static final String TAG = "SearchViewQueryListener";
    private static final int SEARCH_QUERY_MIN_LENGTH = 2;
    private final Context context;
    private SearchViewListener mListener;
    private MenuItem mSearchMenuItem;
    private String mSearchQuery;

    public SearchViewQueryListener(Context context, SearchViewListener listener) {
        this.context = context;
        this.mListener = listener;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        mListener.onSearchFocusChange(hasFocus);
        if (hasFocus) {
            if (mSearchQuery != null && mSearchQuery.length() > 0) {
                final SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
                searchView.setQuery(mSearchQuery, false);
            }
        } else {
            searchToggle(false);
        }
    }

    /**
     * Implementation of SearchView.OnQueryTextListener.
     * Handle the Search query
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
//        TODO
//        fix: getTextBeforeCursor on inactive InputConnection
        if ((query == null) || (query.length() < SEARCH_QUERY_MIN_LENGTH)) {
            return true;
        }

        searchToggle(false);
        mListener.onSearchSubmit(query);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if (query != null && query.length() >= 1) {
            mSearchQuery = query;
        }
        return false;
    }

    public void setSearchMenuItem(MenuItem item) {
        mSearchMenuItem = item;

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
        searchView.setQueryHint(context.getResources().getString(R.string.search_hint));

        searchView.setOnQueryTextListener(this);

        // Collapse when focus lost
        searchView.setOnQueryTextFocusChangeListener(this);
    }

    /**
     * Toggle collapse/expand the SearchView.
     *
     * @param isDisplayed
     */
    public void searchToggle(boolean isDisplayed) {
        if (mSearchMenuItem != null) {
            if (isDisplayed) {
                MenuItemCompat.expandActionView(mSearchMenuItem);

                final SearchView searchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
                searchView.onActionViewExpanded();
            } else {
                MenuItemCompat.collapseActionView(mSearchMenuItem);
            }
        }
    }

    public interface SearchViewListener {
        public void onSearchSubmit(String query);

        public void onSearchFocusChange(boolean hasFocus);
    }
}
