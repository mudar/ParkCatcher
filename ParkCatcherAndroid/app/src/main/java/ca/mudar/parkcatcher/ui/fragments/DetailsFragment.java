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

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.model.AddressFormatted;
import ca.mudar.parkcatcher.model.Queries;
import ca.mudar.parkcatcher.provider.ParkingContract.Favorites;
import ca.mudar.parkcatcher.provider.ParkingContract.Posts;
import ca.mudar.parkcatcher.ui.activities.DetailsActivity;
import ca.mudar.parkcatcher.ui.activities.MainActivity;
import ca.mudar.parkcatcher.utils.AnimHelper;
import ca.mudar.parkcatcher.utils.ConnectionHelper;
import ca.mudar.parkcatcher.utils.GeoHelper;
import ca.mudar.parkcatcher.utils.NotifyingAsyncQueryHandler;
import ca.mudar.parkcatcher.utils.ParkingTimeHelper;
import ca.mudar.parkcatcher.utils.SearchMessageHandler;

public class DetailsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SearchMessageHandler.SearchHandlerCallbacks,
        NotifyingAsyncQueryHandler.AsyncQueryListener,
        View.OnClickListener {
    private static final String TAG = "DetailsFragment";
    final private Handler handler = new SearchMessageHandler(this);
    private int mIdPost;
    private View mView;
    private boolean mIsStarred = false;
    private double mGeoLat = Double.MIN_VALUE;
    private double mGeoLng = Double.MIN_VALUE;
    private String mShareDesc = "";
    private String mFavoriteLabel = "";
    private NotifyingAsyncQueryHandler mHandler;
    private ParkingApp parkingApp;

    public static DetailsFragment newInstance(int idPost) {
        final DetailsFragment fragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Const.BundleExtras.ID_POST, idPost);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_details, container, false);

        mIdPost = getArguments().getInt(Const.BundleExtras.ID_POST, Const.UNKNOWN);

        setOnClickListeners(mView);

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        parkingApp = (ParkingApp) getActivity().getApplicationContext();
        getLoaderManager().initLoader(Queries.PostDetails._TOKEN, null, this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        mHandler = new NotifyingAsyncQueryHandler(getActivity().getContentResolver(), this);

        updateParkingTime();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_details, menu);

        menu.findItem(R.id.action_favorites_toggle).setIcon(mIsStarred ?
                R.drawable.ic_action_star_on : R.drawable.ic_action_star_off);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favorites_toggle) {
            toggleFavorite(mIsStarred);

            mIsStarred = !mIsStarred; // Toggle value
            getActivity().supportInvalidateOptionsMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handle buttons click
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.details_address_row) {
            launchMap();
        } else if (id == R.id.btn_directions) {
            launchNavDirections();
        } else if (id == R.id.btn_streetview) {
            launchStreetview();
        } else if (id == R.id.btn_share) {
            launchShare();
        }
    }

    /**
     * Implementation of LoaderCallbacks
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        final GregorianCalendar parkingCalendar = parkingApp.getParkingCalendar();
        final int duration = parkingApp.getParkingDuration();
        final double hourOfWeek = ParkingTimeHelper.getHourOfWeek(parkingCalendar);
        final int dayOfYear = ParkingTimeHelper.getIsoDayOfYear(parkingCalendar);

        final Uri uriPost = Posts.buildPostTimedUri(String.valueOf(mIdPost),
                String.valueOf(hourOfWeek),
                String.valueOf(hourOfWeek + duration),
                String.valueOf(dayOfYear));

        final String[] selectionArgs = ParkingTimeHelper.getCursorLoaderSelectionArgs(
                parkingApp.getParkingCalendar(), parkingApp.getParkingDuration());
        return new CursorLoader(getActivity().getApplicationContext(),
                uriPost,
                Queries.PostDetails.PROJECTION,
                null,
                selectionArgs,
                null);
    }

    /**
     * Implementation of LoaderCallbacks
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            fillDetails(data);
        }
    }

    /**
     * Implementation of LoaderCallbacks
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /**
     * Implementation of NotifyingAsyncQueryHandler
     */
    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
    }

    /**
     * Implementation of NotifyingAsyncQueryHandler
     */
    @Override
    public void onDeleteComplete(int token, Object cookie, int result) {
       parkingApp.showToastText(R.string.toast_favorites_removed, Toast.LENGTH_SHORT);
    }

    /**
     * Implementation of NotifyingAsyncQueryHandler
     */
    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {
        parkingApp.showToastText(R.string.toast_favorites_added, Toast.LENGTH_SHORT);
    }

    /**
     * Implementation of SearchMessageHandler.OnMessageHandledListener
     */
    @Override
    public void onSearchResults(Message msg) {
        final Bundle b = msg.getData();

        if (b.getInt(Const.BundleExtras.REVERSE_GEOCODER) == Const.BundleExtrasValues.SEARCH_ADDRESS_SUCCESS) {
            mView.findViewById(R.id.details_address_error).setVisibility(View.GONE);

            final TextView viewAddressPrimary = (TextView) mView.findViewById(R.id.details_address_primary);
            final TextView viewAddressSecondary = (TextView) mView.findViewById(R.id.details_address_secondary);
            final String addressPrimary = b.getString(Const.BundleExtras.ADDRESS_PRIMARY);
            final String addressSecondary = b.getString(Const.BundleExtras.ADDRESS_SECONDARY);
            viewAddressSecondary.setText(addressSecondary);

            AnimHelper.updateTextView(viewAddressPrimary, addressPrimary);
            AnimHelper.crossfade(mView.findViewById(R.id.progressbar), viewAddressSecondary);

            mFavoriteLabel = addressPrimary;
        } else {
            mView.findViewById(R.id.progressbar).setVisibility(View.GONE);
            AnimHelper.crossfade(mView.findViewById(R.id.details_address_primary),
                    mView.findViewById(R.id.details_address_error));
        }
    }

    private void setOnClickListeners(View view) {
        view.findViewById(R.id.details_address_row).setOnClickListener(this);
        view.findViewById(R.id.btn_directions).setOnClickListener(this);
        view.findViewById(R.id.btn_streetview).setOnClickListener(this);
        view.findViewById(R.id.btn_share).setOnClickListener(this);
    }

    /**
     * Fill details from cursor data.
     *
     * @param data
     */
    private void fillDetails(Cursor data) {
        mIsStarred = (data.getInt(Queries.PostDetails.IS_STARRED) == 1);
        mGeoLat = data.getDouble(Queries.PostDetails.LAT);
        mGeoLng = data.getDouble(Queries.PostDetails.LNG);
        final boolean isForbidden = (data.getInt(Queries.PostDetails.IS_FORBIDDEN) == 1);

        setThemeByParkingStatus(isForbidden);
        getActivity().supportInvalidateOptionsMenu();
        runAddressSearch(mGeoLat, mGeoLng);

        ((TextView) mView.findViewById(R.id.details_title)).setText(
                isForbidden ? R.string.details_title_forbidden : R.string.details_title_allowed);

        createParkingPanels((LinearLayout) mView.findViewById(R.id.details_panels_wrapper),
                data);

        mView.findViewById(R.id.root_view).setVisibility(View.VISIBLE);
    }

    /**
     * Add all panels to the wrapping parent layout
     *
     * @param parent
     * @param data
     */
    private void createParkingPanels(LinearLayout parent, Cursor data) {
        final LayoutParams params = getPanelLayoutParams();
        final int textColor = getResources().getColor(R.color.details_panel_text);
        do {
            final String desc = data.getString(Queries.PostDetails.DESCRIPTION);
            parent.addView(formatPanel(desc, params, textColor));

            // Add panel details to the ShareIntent description
            mShareDesc += Const.LINE_SEPARATOR + desc;
        } while (data.moveToNext());
    }

    /**
     *
     * @param desc
     * @param params
     * @param textColor
     * @return
     */
    private TextView formatPanel(String desc, LayoutParams params, int textColor) {
        desc = desc.trim().toUpperCase();
        final TextView panelUi = new TextView(getActivity());

        panelUi.setGravity(Gravity.CENTER_HORIZONTAL);
        panelUi.setLayoutParams(params);
        panelUi.setTextColor(textColor);

        String prefix = (String) desc.subSequence(0, 2);
        prefix = prefix.trim();
        try {
            // Start with a figure: authorized duration
            final int tryIntValue = Integer.valueOf(prefix.trim());
            panelUi.setBackgroundResource(R.drawable.bg_panel_parking);
        } catch (NumberFormatException e) {
            if (desc.subSequence(0, 1).equals("P")) {
                // Starts with "P " (trimmed): authorized
                panelUi.setBackgroundResource(R.drawable.bg_panel_parking);
                desc = desc.substring(1);
            } else if (desc.subSequence(0, 2).equals("\\A")) {
                panelUi.setBackgroundResource(R.drawable.bg_panel_no_stopping);
                desc = desc.substring(2);
            } else if (desc.subSequence(0, 2).equals("\\P")) {
                panelUi.setBackgroundResource(R.drawable.bg_panel_no_parking);
                desc = desc.substring(2);
            } else {
                panelUi.setBackgroundResource(R.drawable.bg_panel_no_parking);
            }
        }

        panelUi.setText(desc.trim());

        return panelUi;
    }

    /**
     * Get the panel's layout params: width, height and bottom margin.
     *
     * @return
     */
    private LayoutParams getPanelLayoutParams() {
        final LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        final int marginBottom = getResources().getDimensionPixelSize(R.dimen.details_panel_margin_bottom);
        params.setMargins(0, 0, 0, marginBottom);

        return params;
    }


    /**
     * Handle toggling of starred location
     */
    private void toggleFavorite(boolean wasFavorite) {
        if (wasFavorite) {
            /**
             * Remove from favorites.
             */
            mHandler.startDelete(0,
                    null,
                    Posts.CONTENT_STARRED_URI,
                    Favorites.ID_POST + "=?",
                    new String[]{Integer.toString(mIdPost)});
        } else {
            /**
             * Add to favorites
             */
            final ContentValues values = new ContentValues();
            values.put(Favorites.ID_POST, mIdPost);
            if ((mFavoriteLabel != null) && (mFavoriteLabel.length() > 0)) {
                values.put(Favorites.LABEL, mFavoriteLabel);
            } else {
                final String timestamp = DateFormat
                        .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        .format(new Date());

                values.put(Favorites.LABEL, timestamp);
            }

            mHandler.startInsert(Posts.CONTENT_STARRED_URI, values);
        }
    }

    private void launchMap() {
        final Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        intent.putExtra(Const.BundleExtras.GEO_LAT, mGeoLat);
        intent.putExtra(Const.BundleExtras.GEO_LNG, mGeoLng);
        intent.putExtra(Const.BundleExtras.ID_POST, mIdPost);

        startActivity(intent);
    }

    private void launchStreetview() {

        if ((Double.compare(mGeoLat, Double.MIN_VALUE) != 0)
                && (Double.compare(mGeoLng, Double.MIN_VALUE) != 0)) {

            try {
                final Uri uriStreetView = Uri.parse(String.format(Const.URI_INTENT_STREETVIEW,
                        mGeoLat, mGeoLng));
                final Intent intent = new Intent(Intent.ACTION_VIEW, uriStreetView);
                startActivity(intent);
            } catch (NullPointerException e) {
                e.printStackTrace();

            } catch (Exception e) {
                parkingApp.showToastText(R.string.toast_streetview_error, Toast.LENGTH_LONG);
                e.printStackTrace();

                final Uri uriInstallStreetView = Uri.parse(Const.URI_INSTALL_STREETVIEW);
                final Intent intent = new Intent(Intent.ACTION_VIEW, uriInstallStreetView);
                startActivity(intent);
            }
        }
    }

    private void launchNavDirections() {

        if ((Double.compare(mGeoLat, Double.MIN_VALUE) != 0)
                && (Double.compare(mGeoLng, Double.MIN_VALUE) != 0)) {
            /**
             * Get directions using Intents.
             */

            try {
                final Uri uriNavigation = Uri.parse(String.format(Const.URI_INTENT_NAVIGATION,
                        mGeoLat, mGeoLng));
                final Intent intent = new Intent(Intent.ACTION_VIEW, uriNavigation);
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();

                String sAddr = "";
                Location userLocation = parkingApp.getLocation();
                if (userLocation != null) {
                    sAddr = Double.toString(userLocation.getLatitude()) + ","
                            + Double.toString(userLocation.getLongitude());
                }

                final String urlGmaps = String.format(Const.URL_GMAPS_DIRECTIONS, sAddr,
                        mGeoLat + "," + mGeoLng);

                final Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(urlGmaps));
                startActivity(intent);
            }
        }
    }

    private void launchShare() {
        final Resources resources = getResources();
        final GregorianCalendar parkingCalendar = parkingApp.getParkingCalendar();

        final int dayOfWeek = ParkingTimeHelper.getIsoDayOfWeek(parkingCalendar);
        final double parkingHour = ParkingTimeHelper.getHourRounded(parkingCalendar);

        final String url = String.format(resources.getString(R.string.url_share_post_id),
                mIdPost,
                dayOfWeek,
                parkingHour,
                parkingApp.getParkingDuration());
        final String subject = resources.getString(R.string.details_share_title);
        final String message = String.format(
                resources.getString(R.string.details_share_subtitle),
                url,
                mShareDesc);

        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(intent);
    }

    private void setThemeByParkingStatus(boolean isForbidden) {
        // Toggle address icon color
        ((ImageView) mView.findViewById(R.id.details_address_icon))
                .setColorFilter(isForbidden ?
                        getResources().getColor(R.color.text_primary_light) :
                        getResources().getColor(R.color.theme_primary));

        // Toggle Toolbar color
        ((DetailsActivity) getActivity()).toggleToobarColor(isForbidden);
    }

    private void updateParkingTime() {
        ((TextView) mView.findViewById(R.id.details_time_title)).setText(
                ParkingTimeHelper.getTitle(getActivity(),
                        parkingApp.getParkingCalendar(),
                        parkingApp.getParkingDuration()));
    }

    /**
     * Start the reverse geocode address search thread.
     * Results are sent to the handler.
     *
     * @param latitude
     * @param longitude
     */
    private void runAddressSearch(final double latitude, final double longitude) {
        if (ConnectionHelper.hasConnection(getActivity())) {
            /**
             * Background thread
             */
            final Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    AddressFormatted address = null;
                    try {
                        // Reverse Geocode search
                        address = GeoHelper.findAddressFromLocation(getActivity(), latitude, longitude);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    final Message msg = handler.obtainMessage();
                    final Bundle b = new Bundle();

                    if (address == null) {
                        /**
                         * Send error message to handler.
                         */
                        b.putInt(Const.BundleExtras.REVERSE_GEOCODER, Const.BundleExtrasValues.SEARCH_ADDRESS_ERROR);
                    } else {
                        /**
                         * Send success message to handler with the result geo-coordinates.
                         */
                        b.putInt(Const.BundleExtras.REVERSE_GEOCODER, Const.BundleExtrasValues.SEARCH_ADDRESS_SUCCESS);
                        b.putString(Const.BundleExtras.ADDRESS_PRIMARY, address.getPrimaryAddress());
                        b.putString(Const.BundleExtras.ADDRESS_SECONDARY, address.getSecondaryAddress());
                    }
                    msg.setData(b);

                    handler.sendMessage(msg);
                }
            });
            thread.start();
        } else {
            onSearchResults(new Message());
        }
    }
}
