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
import ca.mudar.parkcatcher.provider.ParkingContract.PanelsCodes;
import ca.mudar.parkcatcher.provider.ParkingContract.Posts;
import ca.mudar.parkcatcher.ui.activities.MainActivity;
import ca.mudar.parkcatcher.utils.ActivityHelper;
import ca.mudar.parkcatcher.utils.ConnectionHelper;
import ca.mudar.parkcatcher.utils.GeoHelper;
import ca.mudar.parkcatcher.utils.NotifyingAsyncQueryHandler;
import ca.mudar.parkcatcher.utils.ParkingTimeHelper;
import ca.mudar.parkcatcher.utils.SearchMessageHandler;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DetailsFragment extends SherlockFragment implements LoaderCallbacks<Cursor>,
        SearchMessageHandler.OnMessageHandledListener,
        NotifyingAsyncQueryHandler.AsyncQueryListener,
        Runnable {
    private static final String TAG = "DetailsFragment";

    private int mIdPost = -1;
    // private Uri mUriPost = null;

    private View mView;
    private boolean mIsStarred = false;
    private double mGeoLat = Double.MIN_VALUE;
    private double mGeoLng = Double.MIN_VALUE;
    private String mShareDesc = "";
    private String mFavoriteLabel = "";
    private NotifyingAsyncQueryHandler mHandler;
    final private Handler handler = new SearchMessageHandler(this);

    ActivityHelper activityHelper;
    private ParkingApp parkingApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityHelper = ActivityHelper.createInstance(getActivity());
        parkingApp = (ParkingApp) getActivity().getApplicationContext();

        Intent intent = getSherlockActivity().getIntent();

        // TODO Optimize this using savedInstanceState to avoid reload of
        // identical data onResume
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            mIdPost = getIdFromUri(intent.getData());
        }
        // else if ((savedInstanceState != null)
        // && savedInstanceState.containsKey(Const.KEY_INSTANCE_RINK_ID)) {
        // mRinkId = savedInstanceState.getInt(Const.KEY_INSTANCE_RINK_ID);
        // }
        else {
            mIdPost = intent.getIntExtra(Const.INTENT_EXTRA_POST_ID, -1);
        }

        // mUriPost = Posts.buildPostUri(Integer.toString(mIdPost));

        getLoaderManager().initLoader(PostDetailsQuery._TOKEN, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_details, container, false);

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        mHandler = new NotifyingAsyncQueryHandler(getActivity().getContentResolver(), this);

        Intent intent = getSherlockActivity().getIntent();

        // TODO Optimize this using savedInstanceState to avoid reload of
        // identical data onResume
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            mIdPost = getIdFromUri(intent.getData());
        }
        // else if ((savedInstanceState != null)
        // && savedInstanceState.containsKey(Const.KEY_INSTANCE_RINK_ID)) {
        // mRinkId = savedInstanceState.getInt(Const.KEY_INSTANCE_RINK_ID);
        // }
        else {
            mIdPost = intent.getIntExtra(Const.INTENT_EXTRA_POST_ID, -1);
        }

        // mUriPost = Posts.buildPostUri(Integer.toString(mIdPost));

        final GregorianCalendar calendar = parkingApp.getParkingCalendar();
        final int duration = parkingApp.getParkingDuration();

        ((TextView) mView.findViewById(R.id.details_time_title)).setText(ParkingTimeHelper
                .getTitle(this.getActivity(), calendar, duration));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_details, menu);

        int resIcon = (mIsStarred ? R.drawable.ic_action_favorite_on
                : R.drawable.ic_action_favorite_off);

        // MenuItem actionItem = menu.findItem(R.id.menu_share);
        // ShareActionProvider actionProvider = (ShareActionProvider)
        // actionItem.getActionProvider();
        // actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        // actionProvider.setShareIntent(createShareIntent());

        menu.findItem(R.id.menu_favorites_toggle).setIcon(resIcon);
    }

    // private Intent createShareIntent() {
    // final GregorianCalendar parkingCalendar =
    // parkingApp.getParkingCalendar();
    //
    // final int dayOfWeek = (parkingCalendar.get(Calendar.DAY_OF_WEEK) ==
    // Calendar.SUNDAY ? 7
    // : parkingCalendar.get(Calendar.DAY_OF_WEEK) - 1);
    // final double parkingHour = parkingCalendar.get(Calendar.HOUR_OF_DAY)
    // + Math.round(parkingCalendar.get(Calendar.MINUTE) / 0.6) / 100.00d;
    //
    // // final int duration = parkingApp.getParkingDuration();
    //
    // final String url =
    // String.format(res.getString(R.string.url_share_post_id),
    // mIdPost,
    // dayOfWeek,
    // parkingHour,
    // parkingApp.getParkingDuration());
    // final String subject =
    // String.format(res.getString(R.string.details_share_title), url);
    // final String desc =
    // String.format(res.getString(R.string.details_share_subtitle),
    // mShareDesc);
    //
    // final Intent intent = new Intent(Intent.ACTION_SEND);
    // intent.setType("text/plain");
    // // EXTRA_SUBJECT is not used to allow sharing with SMS instead of
    // // MMS
    // // intent.putExtra(Intent.EXTRA_SUBJECT, subject);
    // intent.putExtra(Intent.EXTRA_TEXT, subject + Const.LINE_SEPARATOR +
    // desc);
    //
    // return intent;
    // }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Resources res = getResources();

        if (item.getItemId() == R.id.menu_favorites_toggle) {
            onCheckedChanged(mIsStarred);

            mIsStarred = (mIsStarred ? false : true); // Toggle value
            getSherlockActivity().invalidateOptionsMenu();
            return true;
        }
        else if (item.getItemId() == R.id.menu_map) {
            final Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(Const.INTENT_EXTRA_GEO_LAT, mGeoLat);
            intent.putExtra(Const.INTENT_EXTRA_GEO_LNG, mGeoLng);
            intent.putExtra(Const.INTENT_EXTRA_POST_ID, mIdPost);

            startActivity(intent);

            return true;
        }
        else if (item.getItemId() == R.id.menu_reminder) {
            parkingApp.showToastText(R.string.toast_todo_reminder, Toast.LENGTH_LONG);

            return true;
        }
        else if (item.getItemId() == R.id.menu_directions) {

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
            return true;
        }
        else if (item.getItemId() == R.id.menu_streetview) {

            if ((Double.compare(mGeoLat, Double.MIN_VALUE) != 0)
                    && (Double.compare(mGeoLng, Double.MIN_VALUE) != 0)) {

                try {
                    final Uri uriStreetView = Uri.parse(String.format(Const.URI_INTENT_STREETVIEW,
                            mGeoLat, mGeoLng));
                    final Intent intent = new Intent(Intent.ACTION_VIEW, uriStreetView);
                    startActivity(intent);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    return false;
                } catch (Exception e) {
                    parkingApp.showToastText(R.string.toast_streetview_error, Toast.LENGTH_LONG);
                    e.printStackTrace();

                    final Uri uriInstallStreetView = Uri.parse(Const.URI_INSTALL_STREETVIEW);
                    final Intent intent = new Intent(Intent.ACTION_VIEW, uriInstallStreetView);
                    startActivity(intent);

                    return false;
                }
            }
            return true;
        }

        else if (item.getItemId() == R.id.menu_share) {

            final GregorianCalendar parkingCalendar = parkingApp.getParkingCalendar();

            final int dayOfWeek = (parkingCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 7
                    : parkingCalendar.get(Calendar.DAY_OF_WEEK) - 1);
            final double parkingHour = parkingCalendar.get(Calendar.HOUR_OF_DAY)
                    + Math.round(parkingCalendar.get(Calendar.MINUTE) / 0.6) / 100.00d;

            // final int duration = parkingApp.getParkingDuration();

            final String url = String.format(res.getString(R.string.url_share_post_id),
                    mIdPost,
                    dayOfWeek,
                    parkingHour,
                    parkingApp.getParkingDuration());
            final String subject = String.format(res.getString(R.string.details_share_title), url);
            final String desc = String.format(res.getString(R.string.details_share_subtitle),
                    mShareDesc);

            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            // EXTRA_SUBJECT is not used to allow sharing with SMS instead of
            // MMS
            // intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, subject + Const.LINE_SEPARATOR + desc);

            startActivity(intent);
            return true;
        }

        return (activityHelper.onOptionsItemSelected(item) || super.onOptionsItemSelected(item));
    }

    /**
     * Implementation of LoaderCallbacks
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle arg1) {

        final GregorianCalendar parkingCalendar = parkingApp.getParkingCalendar();

        final int dayOfWeek = (parkingCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 7
                : parkingCalendar.get(Calendar.DAY_OF_WEEK) - 1);
        final double parkingHour = parkingCalendar.get(Calendar.HOUR_OF_DAY)
                + Math.round(parkingCalendar.get(Calendar.MINUTE) / 0.6) / 100.00d;

        final double hourOfWeek = parkingHour + (dayOfWeek - 1) * 24;

        // API uses values 0-365 (or 364)
        final int dayOfYear = parkingCalendar.get(Calendar.DAY_OF_YEAR) - 1;

        final int duration = parkingApp.getParkingDuration();

        final Uri uriPost = Posts.buildPostTimedUri(String.valueOf(mIdPost),
                String.valueOf(hourOfWeek),
                String.valueOf(hourOfWeek + duration),
                String.valueOf(dayOfYear));

        return new CursorLoader(getSherlockActivity().getApplicationContext(),
                uriPost,
                PostDetailsQuery.PROJECTION,
                null,
                new String[] {
                        Double.toString(hourOfWeek),
                        Integer.toString(duration),
                        Integer.toString(dayOfYear)
                },
                null);
    }

    /**
     * Implementation of LoaderCallbacks
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            initDetails(data);
        }
    }

    /**
     * Implementation of LoaderCallbacks
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // TODO Auto-generated method stub
    }

    /**
     * Implementation of NotifyingAsyncQueryHandler
     */
    @Override
    public void onQueryComplete(int token, Object cookie, Cursor cursor) {
        // TODO Auto-generated method stub
    }

    /**
     * Implementation of Runnable. This runnable thread gets the k by reverse
     * Geocode in the background. Results are sent to the handler.
     */
    @Override
    public void run() {
        String address = null;
        try {
            /**
             * Reverse Geocode search.
             */
            address = GeoHelper.findAddressFromLocation(getActivity(), mGeoLat, mGeoLng);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Message msg = handler.obtainMessage();
        final Bundle b = new Bundle();

        if (address == null) {
            /**
             * Send error message to handler.
             */
            b.putInt(Const.KEY_BUNDLE_REVERSE_GEOCODER, Const.BUNDLE_SEARCH_ADDRESS_ERROR);
        }
        else {
            /**
             * Send success message to handler with the found geocoordinates.
             */
            b.putInt(Const.KEY_BUNDLE_REVERSE_GEOCODER, Const.BUNDLE_SEARCH_ADDRESS_SUCCESS);
            b.putString(Const.KEY_BUNDLE_ADDRESS_DESC, address);
        }
        msg.setData(b);

        handler.sendMessage(msg);
    }

    /**
     * Implementation of SearchMessageHandler.OnMessageHandledListener
     */
    @Override
    public void OnMessageHandled(Message msg) {
        final Bundle b = msg.getData();

        mView.findViewById(R.id.details_progress_address).setVisibility(View.GONE);

        if (b.getInt(Const.KEY_BUNDLE_REVERSE_GEOCODER) == Const.BUNDLE_SEARCH_ADDRESS_SUCCESS) {
            TextView addressUi = (TextView) mView.findViewById(R.id.details_address);
            final String desc = b.getString(Const.KEY_BUNDLE_ADDRESS_DESC);
            addressUi.setText(desc);
            addressUi.setVisibility(View.VISIBLE);

            final int indexOfSeparator = desc.indexOf(Const.LINE_SEPARATOR);
            if (indexOfSeparator > 0) {
                mFavoriteLabel = desc.substring(0, indexOfSeparator);
            }
            else {
                mFavoriteLabel = desc;
            }

        } else {
            /**
             * Address not found! Display error message.
             */
            try {
                ((ParkingApp) getActivity().getApplicationContext()).showToastText(
                        R.string.toast_address_error, Toast.LENGTH_SHORT);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }

    }

    private void initDetails(Cursor data) {
        mIsStarred = (data.getInt(PostDetailsQuery.IS_STARRED) == 1);
        mGeoLat = data.getDouble(PostDetailsQuery.LAT);
        mGeoLng = data.getDouble(PostDetailsQuery.LNG);

        // Start the reverse geocode address search
        if (ConnectionHelper.hasConnection(getActivity())) {
            mView.findViewById(R.id.details_progress_address).setVisibility(View.VISIBLE);
            final Thread thread = new Thread(this);
            thread.start();
        }
        else {
            mView.findViewById(R.id.details_progress_address).setVisibility(View.GONE);
        }

        final boolean isForbidden = (data.getInt(PostDetailsQuery.IS_FORBIDDEN) == 1);

        final LinearLayout panelsWrapper = (LinearLayout) mView
                .findViewById(R.id.details_panels_wrapper);
        panelsWrapper.removeAllViews();

        // Panels LayoutParams and Color
        final LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);

        final float SCALE = getActivity().getResources().getDisplayMetrics().density;
        // Convert dips to pixels
        float valueDips = 16.0f;
        int valuePixels = (int) (valueDips * SCALE);

        params.setMargins(0, 0, 0, valuePixels);

        final int textColor = getResources().getColor(R.color.details_panel_text);
        do {
            final String desc = data.getString(PostDetailsQuery.DESCRIPTION);
            mShareDesc += Const.LINE_SEPARATOR + desc;

            panelsWrapper.addView(getPanel(desc, params, textColor));
        } while (data.moveToNext());

        final TextView titleUi = (TextView) mView.findViewById(R.id.details_title);

        if (isForbidden) {
            titleUi.setText(R.string.details_title_forbidden);
        }
        else {
            titleUi.setText(R.string.details_title_allowed);
        }

        getSherlockActivity().invalidateOptionsMenu();
    }

    private TextView getPanel(String desc, LayoutParams params, int textColor) {
        desc = desc.trim().toUpperCase();
        final TextView panelUi = new TextView(getSherlockActivity());

        panelUi.setGravity(Gravity.CENTER_HORIZONTAL);
        panelUi.setLayoutParams(params);
        panelUi.setTextColor(textColor);

        String prefix = (String) desc.subSequence(0, 2);
        prefix = prefix.trim();
        try {
            // Start with a figure: authorized duration
            Integer.valueOf(prefix.trim());
            panelUi.setBackgroundResource(R.drawable.bg_panel_parking);
        } catch (NumberFormatException e) {
            if (desc.subSequence(0, 1).equals("P")) {
                // Starts with "P " (trimmed): authorized
                panelUi.setBackgroundResource(R.drawable.bg_panel_parking);
                desc = desc.substring(1);
            }
            else if (desc.subSequence(0, 2).equals("\\A")) {
                panelUi.setBackgroundResource(R.drawable.bg_panel_no_stopping);
                desc = desc.substring(2);
            }
            else if (desc.subSequence(0, 2).equals("\\P")) {
                panelUi.setBackgroundResource(R.drawable.bg_panel_no_parking);
                desc = desc.substring(2);
            }
            else {
                panelUi.setBackgroundResource(R.drawable.bg_panel_no_parking);
            }
        }

        panelUi.setText(desc.trim());

        return panelUi;
    }

    private int getIdFromUri(Uri uri) {
        int postId = -1;

        List<String> pathSegments = uri.getPathSegments();

        if ((pathSegments.size() == 5)
                && (pathSegments.get(0).equals(Const.INTENT_EXTRA_URL_PATH_POST_ID))) {

            try {
                postId = Integer.parseInt(pathSegments.get(1));
                final int day = Integer.valueOf(pathSegments.get(2));
                final double time = Double.valueOf(pathSegments.get(3));
                final int duration = Integer.valueOf(pathSegments.get(4));

                final int hourOfDay = (int) time;
                final int minute = (int) ((time - hourOfDay) * 60);

                GregorianCalendar calendar = new GregorianCalendar();

                calendar.set(Calendar.DAY_OF_WEEK, day == 7 ? Calendar.SUNDAY : day + 1);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                parkingApp.setParkingCalendar(calendar);
                // parkingApp.setParkingTime(hourOfDay, minute);
                parkingApp.setParkingDuration(duration);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return postId;
    }

    /**
     * Handle toggling of starred checkbox.
     */
    private void onCheckedChanged(boolean wasFavorite) {
        int message = R.string.toast_favorites_added;

        if (wasFavorite) {
            /**
             * Remove from favorites.
             */
            String[] args = new String[] {
                    Integer.toString(mIdPost)
            };
            mHandler.startDelete(FavoritesToggleQuery._TOKEN, null, Posts.CONTENT_STARRED_URI,
                    Favorites.ID_POST + "=?", args);
            message = R.string.toast_favorites_removed;
        }
        else {
            /**
             * Add to favorites
             */
            final ContentValues values = new ContentValues();
            values.put(Favorites.ID_POST, mIdPost);
            if ((mFavoriteLabel != null) && (mFavoriteLabel.length() > 0)) {
                values.put(Favorites.LABEL, mFavoriteLabel);
            }
            else {
                String timestamp = DateFormat
                        .getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        .format(new Date());

                values.put(Favorites.LABEL, timestamp);
            }

            mHandler.startInsert(Posts.CONTENT_STARRED_URI, values);
        }

        parkingApp.showToastText(message, Toast.LENGTH_LONG);
    }

    private static interface PostDetailsQuery {
        int _TOKEN = 0x30;

        final String[] PROJECTION = new String[] {
                PanelsCodes.CODE,
                PanelsCodes.DESCRIPTION,
                PanelsCodes.TYPE_DESC,
                Posts.IS_STARRED,
                Posts.LAT,
                Posts.LNG,
                Posts.IS_FORBIDDEN,
        };

        // TODO: Fix the provider's hard-coded projection
        final int CODE = 0;
        final int DESCRIPTION = 1;
        final int TYPE_DESC = 2;
        final int IS_STARRED = 3;
        final int LAT = 4;
        final int LNG = 5;
        final int IS_FORBIDDEN = 6;
    }

    private static interface FavoritesToggleQuery {
        int _TOKEN = 0x40;
    }

}
