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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.location.Address;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.Const.DbValues;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.model.GeoJSON;
import ca.mudar.parkcatcher.model.Post;
import ca.mudar.parkcatcher.model.Queries;
import ca.mudar.parkcatcher.provider.ParkingContract.Posts;
import ca.mudar.parkcatcher.ui.activities.DetailsActivity;
import ca.mudar.parkcatcher.ui.adapters.ParkingInfoWindowAdapter;
import ca.mudar.parkcatcher.ui.listeners.SearchViewQueryListener;
import ca.mudar.parkcatcher.utils.ConnectionHelper;
import ca.mudar.parkcatcher.utils.GeoHelper;
import ca.mudar.parkcatcher.utils.LocationHelper;
import ca.mudar.parkcatcher.utils.LongPressLocationSource;
import ca.mudar.parkcatcher.utils.ParkingTimeHelper;
import ca.mudar.parkcatcher.utils.SearchMessageHandler;
import ca.mudar.parkcatcher.utils.WebsiteUriHelper;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;

public class MainMapFragment extends SupportMapFragment implements
        CalendarFilterFragment.CalendarFilterUpdatedListener,
        SearchMessageHandler.SearchHandlerCallbacks,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        SearchViewQueryListener.SearchViewListener,
        GoogleMap.OnMyLocationChangeListener,
        GoogleMap.OnMapLoadedCallback,
        OnMapReadyCallback {
    private static final String TAG = "MainMapFragment";
    private static final float ZOOM_DEFAULT = 11f;
    private static final float ZOOM_NEAR = 17f;
    private static final float ZOOM_MIN = 16f;
    private static final float HUE_MARKER = 94.0f;
    private static final float HUE_MARKER_STARRED = BitmapDescriptorFactory.HUE_YELLOW;
    private static final float DISTANCE_MARKER_HINT = 50f;

    protected MapEventsListener mListener;
    private GoogleMap mMap;
    private LongPressLocationSource mLongPressLocationSource;

    private SearchViewQueryListener mSearchViewQueryListener;
    private View viewMarkerInfoWindow;

    private int mScreenWidth = 0;
    private int mScreenHeight = 0;
    private Location mMapCenter = null;
    private LatLng screenCenter = null;
    private Marker clickedMarker = null;
    private Marker searchedMarker = null;
    private boolean mIsLocationNearMontreal = false;
    private Location mInitialLocation;
    private boolean mIsMyLocationFound = false;
    private boolean mIsMapLoaded = false;
    private boolean mHasTriedInitialZoom = false;

    private ParkingApp parkingApp;

    private SearchMessageHandler searchMessageHandler;
    private JsonAsyncTask jsonAsyncTask = null;
    private DbAsyncTask dbAsyncTask = null;

    private MenuItem searchItem;

    /**
     * Attach a listener.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MapEventsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MapEventsListener");
        }
    }

    /**
     * Create map and initialize
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = super.onCreateView(inflater, container, savedInstanceState);

        if (Const.SUPPORTS_ICS) {
            view.setPadding(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.slider_collapsed_height));
        }

        viewMarkerInfoWindow = inflater.inflate(R.layout.custom_info_window, container, false);

        return view;
    }

    /**
     * Initialize map and LocationManager
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        parkingApp = (ParkingApp) getActivity().getApplicationContext();

        setUpMapIfNeeded();
        initializeSearchListeners();

        setHasOptionsMenu(true);
    }

    /**
     * Enable user location (GPS) updates on map display.
     */
    @Override
    public void onResume() {
        super.onResume();

        handleIntent();

        resetScreenDimensions();
        toggleLongPressLocationSource(true);
    }

    /**
     * Disable user location (GPS) updates on map hide.
     */
    @Override
    public void onPause() {
        super.onPause();

        toggleLongPressLocationSource(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_map, menu);

        searchItem = menu.findItem(R.id.action_search);
        mSearchViewQueryListener.setSearchMenuItem(searchItem);
    }

    /**
     * Implements OnMapReadyCallback
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkReady()) {
            setUpMap();
        }
    }

    /**
     * Implementation of GoogleMap.OnMapClickListener
     */
    @Override
    public void onMapClick(LatLng point) {
        clickedMarker = null;
    }

    /**
     * Implementation of GoogleMap.OnMarkerClickListener
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        clickedMarker = marker;
        return false;
    }

    /**
     * Implementation of GoogleMap.OnMyLocationChangeListener
     *
     * @param location
     */
    @Override
    public void onMyLocationChange(Location location) {
        mIsMyLocationFound = true;
        updateMyLocation(location);
        zoomToInitialLocationIfNeeded();
    }

    /**
     * Implementation of GoogleMap.OnMapLoadedCallback
     */
    @Override
    public void onMapLoaded() {
        mIsMapLoaded = true;
        zoomToInitialLocationIfNeeded();
    }

    /**
     * Implementation of GoogleMap.OnInfoWindowClickListener
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        final String title = marker.getTitle();

        try {
            final int idPost = Integer.valueOf(title);
            showSpotDetails(idPost);
        } catch (NumberFormatException e) {
            // Selected marker is not a post.
            // e.printStackTrace();
        }
    }

    /**
     * Implementation of SearchMessageHandler.SearchHandlerCallbacks
     *
     * @param query
     */
    @Override
    public void onSearchSubmit(String query) {
        startAddressSearch(query);
    }

    /**
     * Implementation of SearchMessageHandler.SearchHandlerCallbacks
     *
     * @param hasFocus
     */
    @Override
    public void onSearchFocusChange(boolean hasFocus) {
        if (hasFocus) {
            mListener.onMapSearchClick();
        }
    }

    /**
     * Implementation of SearchMessageHandler.OnMessageHandledListener.
     * Handle the runnable thread results. This hides the indeterminate progressbar
     * then centers map on found location or displays error message.
     *
     * @param msg
     */
    @Override
    public void onSearchResults(Message msg) {
        if (getActivity() == null) {
            return;
        }

        setIndeterminateProgressVisibilty(false);

        final Bundle b = msg.getData();
        final String desc = b.getString(Const.BundleExtras.ADDRESS_DESC);

        if (b.getInt(Const.BundleExtras.SEARCH_ADDRESS) == Const.BundleExtrasValues.SEARCH_ADDRESS_SUCCESS) {
            /**
             * Address is found, center map on location and add a marker
             */
            final Location location = LocationHelper.createSearchLocation(
                    b.getDouble(Const.BundleExtras.ADDRESS_LAT),
                    b.getDouble(Const.BundleExtras.ADDRESS_LNG));

            moveCameraToLocation(location, true);
            addSearchedLocationMarker(location, desc);
        } else {
            /**
             * Address not found! Display error message.
             */
            try {
                parkingApp.showToastText(String.format(
                        getResources().getString(R.string.toast_search_error),
                        desc), Toast.LENGTH_LONG);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Implementation of CalendarFilterFragment.CalendarFilterUpdatedListener
     *
     * @param calendar
     * @param duration
     */
    @Override
    public void onCalendarFilterChanged(GregorianCalendar calendar, int duration) {
        updateScreenCenter(null);
        updateOverlays(calendar, duration);
    }

    /**
     * Called from the activity to handle onKeyUp
     */
    public void toggleSearchView(boolean isDisplayed) {
        if (searchItem != null && isVisible()) {
            if (isDisplayed) {
                MenuItemCompat.expandActionView(searchItem);
            } else {
                MenuItemCompat.collapseActionView(searchItem);
            }
        }
    }

    /**
     * Call
     *
     * @param mapCenter
     */
    public void setInitialMapCenter(Location mapCenter) {
        mInitialLocation = mapCenter;
        skipInitialLocationZoom();
        moveCameraToLocation(mapCenter, false);
    }

    private void toggleLongPressLocationSource(boolean enabled) {
        if (mLongPressLocationSource != null) {
            mLongPressLocationSource.setEnabled(enabled);
        }
    }

    private void initializeSearchListeners() {
        if (searchMessageHandler == null) {
            searchMessageHandler = new SearchMessageHandler(this);
        }
        mSearchViewQueryListener = new SearchViewQueryListener(getActivity(), this);
    }

    private boolean checkReady() {
        if (mMap == null) {
            parkingApp.showToastText(R.string.toast_map_not_ready, Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (!checkReady()) {
            // Try to obtain the map from the SupportMapFragment.
            getMapAsync(this);
        }
    }

    /**
     * Set map background to transparent
     * Janky "fix" to prevent artefacts when embedding GoogleMaps in a sliding view.
     * By Greg Roodt: https://gist.github.com/groodt/5181980
     *
     * @param group
     */
    private void setMapTransparent(ViewGroup group) {
        int childCount = group.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = group.getChildAt(i);
            if (child instanceof ViewGroup) {
                setMapTransparent((ViewGroup) child);
            } else if (child instanceof SurfaceView) {
                child.setBackgroundColor(0x00000000);
            }
        }
    }

    private void setUpMap() {
        if (mLongPressLocationSource == null) {
            mLongPressLocationSource = new LongPressLocationSource(mMap);
            mMap.setOnMapLongClickListener(mLongPressLocationSource);
        }

        if (!Const.SUPPORTS_JELLY_BEAN_MR1) {
            setMapTransparent((ViewGroup) getView());
        }

        mMap.setMyLocationEnabled(true);
        mMap.setMapType(MAP_TYPE_NORMAL);

        final UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setAllGesturesEnabled(true);
        uiSettings.setMapToolbarEnabled(false);

        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .bearing(Const.MONTREAL_NATURAL_NORTH_ROTATION)
                .zoom(ZOOM_DEFAULT)
                .target(Const.MONTREAL_GEO_LAT_LNG)
                .build()));

        mMap.setLocationSource(null);

        mMap.setInfoWindowAdapter(new ParkingInfoWindowAdapter(viewMarkerInfoWindow));

        if (mInitialLocation == null) {
            mMap.setOnMapLoadedCallback(this);
        } else {
            moveCameraToLocation(mInitialLocation, false);
            mInitialLocation = null;
        }

        setUpMapListeners();
    }

    private void setUpMapListeners() {
        mMap.setOnMyLocationChangeListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                updateOverlays();
            }
        });
    }

    private void handleIntent() {
        // TODO Optimize this using savedInstanceState to avoid reload of
        // identical data onResume
        final Intent intent = getActivity().getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            final String query = WebsiteUriHelper.getAddressFromUri(intent.getData());
            if (query != null) {
                // To skip initial zoom to user location
                skipInitialLocationZoom();
            }
            startAddressSearch(query);
        }
    }

    private void setIndeterminateProgressVisibilty(boolean isLoading) {
        mListener.onMapDataProcessing(isLoading);
    }

    private void updateOverlays() {
        updateOverlays(parkingApp.getParkingCalendar(), parkingApp.getParkingDuration());
    }

    private void updateOverlays(GregorianCalendar calendar, int duration) {
        if (mMap.getCameraPosition().zoom < ZOOM_MIN) {
            if (mIsMapLoaded) {
                parkingApp.showToastText(R.string.toast_map_zoom_to_update, Toast.LENGTH_SHORT);
            }
            return;
        } else {
            parkingApp.hideToastText(R.string.toast_map_zoom_to_update);
        }

        final Projection projection = mMap.getProjection();

        final boolean updated = updateScreenCenter(projection);
        if (!updated) {
            // No refresh needed
            return;
        }

        // Get arguments for API call
        final LatLngBounds bounds = projection.getVisibleRegion().latLngBounds;
        final int day = ParkingTimeHelper.getIsoDayOfWeek(calendar);
        final double parkingHour = ParkingTimeHelper.getHourRounded(calendar);

        if (Const.HAS_OFFLINE) {
            queryOverlays(day, parkingHour, duration, bounds.northeast, bounds.southwest);
        } else {
            downloadOverlays(day, parkingHour, duration, bounds.northeast, bounds.southwest);
        }
    }

    private void queryOverlays(int day, double parkingHour, int duration, LatLng NE, LatLng SW) {
        if (dbAsyncTask != null) {
            dbAsyncTask.cancel(true);
        }

        dbAsyncTask = new DbAsyncTask();
        dbAsyncTask.execute(day, parkingHour, duration, NE, SW);
    }

    @Deprecated
    private void downloadOverlays(int day, double parkingHour, int duration, LatLng NE, LatLng SW) {
        final String fetchUrl = String.format(Const.Api.POSTS_LIVE,
                day, parkingHour, duration,
                NE.latitude, SW.longitude, SW.latitude, NE.longitude);

        URL apiURL;
        try {
            apiURL = new URL(fetchUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }

        if (jsonAsyncTask != null) {
            jsonAsyncTask.cancel(true);
        }
        jsonAsyncTask = new JsonAsyncTask();
        jsonAsyncTask.execute(apiURL);
    }

    /**
     * screenCenter is used to display the nearest hintMarker and to calculate when to trigger
     * data updates on far-enough camera movements.
     *
     * @param projection
     * @return boolean true if screenCenter was updated
     */
    private boolean updateScreenCenter(Projection projection) {
        if (projection == null) {
            screenCenter = null;
            return true;
        }

        if (screenCenter == null) {
            // We don't have a previous value for screenCenter
            screenCenter = mMap.getCameraPosition().target;
            return true;
        } else {
            // TODO: get screen center without repeated use of Projection
            final LatLng cameraTarget = mMap.getCameraPosition().target;
            final Point oldCenterPoint = projection.toScreenLocation(screenCenter);

            updateScreenDimensions(projection, cameraTarget);

            if ((oldCenterPoint.x > mScreenWidth * 0.75)
                    || (oldCenterPoint.x < mScreenWidth * 0.25)
                    || (oldCenterPoint.y > mScreenHeight * 0.75)
                    || (oldCenterPoint.y < mScreenHeight * 0.25)) {
                // Refresh is needed only on significant camera re-positioning
                screenCenter = cameraTarget;
                return true;
            }
        }
        return false;
    }

    /**
     * Screen width/height are used to trigger data updates on far-enough camera movements.
     *
     * @param projection
     * @param cameraTarget
     */
    private void updateScreenDimensions(Projection projection, LatLng cameraTarget) {
        if (projection == null) {
            mScreenWidth = 0;
            mScreenHeight = 0;
        } else if (mScreenWidth == 0 || mScreenHeight == 0) {
            // Reduce calls to the expensive toScreenLocation()
            final Point cameraCenterPoint = projection.toScreenLocation(cameraTarget);
            mScreenWidth = cameraCenterPoint.x * 2;
            mScreenHeight = cameraCenterPoint.y * 2;
        }
    }

    private void resetScreenDimensions() {
        updateScreenDimensions(null, null);
    }

    /**
     * Set new map center.
     *
     * @param mapCenter
     */
    private void moveCameraToLocation(Location mapCenter, boolean animated) {
        if (mMap == null || mapCenter == null) {
            return;
        }

        final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(
                mapCenter.getLatitude(), mapCenter.getLongitude()), ZOOM_NEAR);
        if (animated) {
            mMap.animateCamera(cameraUpdate);
        } else {
            mMap.moveCamera(cameraUpdate);
        }
    }

    /**
     * Check if user is near Montreal. We stop checking after first true value (default is false).
     *
     * @param myLocation
     */
    private void updateMyLocation(Location myLocation) {
        parkingApp.setLocation(myLocation);

        if (mIsMyLocationFound && !mIsLocationNearMontreal) {
            mIsLocationNearMontreal = LocationHelper.isLocationNearMontreal(myLocation);
            toggleMyLocationUiButton(mIsLocationNearMontreal);
        }
    }

    /**
     * Called by both onMyLocationChange() and onMapLoaded(), runs only if both have been called
     */
    private void zoomToInitialLocationIfNeeded() {
        if (mHasTriedInitialZoom) {
            return;
        }

        if (mIsMyLocationFound && mIsMapLoaded) {
            skipInitialLocationZoom();
            if (mIsLocationNearMontreal) {
                moveCameraToLocation(parkingApp.getLocation(), true);
            } else {
                moveCameraToLocation(LocationHelper.createDefaultLocation(), true);
            }
        } else if (mIsMapLoaded) {
            if (LocationHelper.isLocationNearMontreal(parkingApp.getLocation())) {
                // This is a first zoom to lastKnownLocation. Will move again once MyLocation is found
                moveCameraToLocation(parkingApp.getLocation(), true);
            }
        }
    }

    private void skipInitialLocationZoom() {
        mHasTriedInitialZoom = true;
    }

    /**
     * Enables the map's MyLocation button for Montreal users only.
     *
     * @param enabled
     */
    private void toggleMyLocationUiButton(boolean enabled) {
        if (mMap != null) {
            final UiSettings uiSettings = mMap.getUiSettings();
            if (uiSettings.isMyLocationButtonEnabled() != enabled) {
                uiSettings.setMyLocationButtonEnabled(enabled);
            }
        }
    }

    /**
     * Add marker for found location, using LatLng
     *
     * @param latLng
     * @param title
     */
    private void addSearchedLocationMarker(LatLng latLng, String title) {
        searchedMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(null)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).visible(true));
        searchedMarker.showInfoWindow();
    }

    /**
     * Add marker for found location, using Location
     *
     * @param location
     * @param title
     */
    private void addSearchedLocationMarker(Location location, String title) {
        addSearchedLocationMarker(new LatLng(location.getLatitude(), location.getLongitude()), title);
    }

    /**
     * Show the Indeterminate ProgressBar and start the Geocode search thread.
     */
    private void startAddressSearch(String query) {
        if (query != null) {
            if (ConnectionHelper.hasConnection(getActivity())) {
                setIndeterminateProgressVisibilty(true);
                // Start the background search
                new Thread(new AddressSearchRunnable(query)).start();
            } else {
                parkingApp.showToastText(R.string.toast_search_network_connection_error,
                        Toast.LENGTH_LONG);
            }
        }
    }

    private void showSpotDetails(int idPost) {
        getActivity().startActivity(DetailsActivity.newIntent(getActivity(), idPost));
    }


    /**
     * Container Activity must implement this interface to receive the list item
     * clicks.
     */
    public interface MapEventsListener {
        public void onMapSearchClick();

        public void onMapDataProcessing(boolean isProcessing);
    }

    /**
     * 3 background operation: address search, database load, json download
     */

    /**
     * Runnable tp get the Geocode search value in the background.
     * Results are sent to the handler.
     */
    private class AddressSearchRunnable implements Runnable {

        private final String query;

        private AddressSearchRunnable(String query) {
            this.query = query;
        }

        @Override
        public void run() {
            Address address = null;
            try {
                /**
                 * Geocode search. Takes time and not very reliable!
                 */
                address = GeoHelper.findAddressFromName(getActivity(), query);
            } catch (IOException e) {
                e.printStackTrace();
            }

            final Message msg = searchMessageHandler.obtainMessage();
            final Bundle b = new Bundle();

            if (address == null) {
                /**
                 * Send error message to handler.
                 */
                b.putInt(Const.BundleExtras.SEARCH_ADDRESS, Const.BundleExtrasValues.SEARCH_ADDRESS_ERROR);
                b.putString(Const.BundleExtras.ADDRESS_DESC, query);
            } else {
                /**
                 * Send success message to handler with the found geocoordinates.
                 */
                b.putInt(Const.BundleExtras.SEARCH_ADDRESS, Const.BundleExtrasValues.SEARCH_ADDRESS_SUCCESS);
                b.putDouble(Const.BundleExtras.ADDRESS_LAT, address.getLatitude());
                b.putDouble(Const.BundleExtras.ADDRESS_LNG, address.getLongitude());
                b.putString(Const.BundleExtras.ADDRESS_DESC, address.getAddressLine(0));
            }
            msg.setData(b);

            searchMessageHandler.sendMessage(msg);
        }
    }

    /**
     * Query database to load posts/markers then add them to the map.
     */
    private class DbAsyncTask extends AsyncTask<Object, Void, List<MarkerOptions>> {

        private boolean hasHintMarker;
        private int mIndexInfoWindow;
        private LatLng clickedLatLng;
        private WeakReference<Cursor> mCursor;

        @Override
        protected void onPreExecute() {
            try {
                hasHintMarker = true;
                mIndexInfoWindow = -1;
                if (clickedMarker != null) {
                    clickedLatLng = clickedMarker.getPosition();
                } else {
                    clickedLatLng = null;
                }
                setIndeterminateProgressVisibilty(true);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected List<MarkerOptions> doInBackground(Object... params) {
            final int dayOfWeek = (Integer) params[0];
            final double parkingHour = (Double) params[1];
            final double hourOfWeek = ParkingTimeHelper.getHourOfWeek(dayOfWeek, parkingHour);
            final int duration = (Integer) params[2];

            final LatLng NE = (LatLng) params[3];
            final LatLng SW = (LatLng) params[4];

            final GregorianCalendar calendar = parkingApp.getParkingCalendar();
            // API uses values 0-365 (or 364)
            final int dayOfYear = ParkingTimeHelper.getIsoDayOfYear(calendar);

            try {
                mCursor = new WeakReference<Cursor>(getActivity()
                        .getApplicationContext()
                        .getContentResolver()
                        .query(Posts.CONTENT_ALLOWED_URI,
                                Queries.PostsOverlays.PROJECTION,
                                Posts.LAT + " >= ? AND " +
                                        Posts.LAT + " <= ? AND " +
                                        Posts.LNG + " >= ? AND " +
                                        Posts.LNG + " <= ? ",

                                new String[]{
                                        Double.toString(SW.latitude),
                                        Double.toString(NE.latitude),
                                        Double.toString(SW.longitude),
                                        Double.toString(NE.longitude),
                                        Double.toString(hourOfWeek),
                                        Integer.toString(duration),
                                        Integer.toString(dayOfYear)
                                },
                                null));
                final List<MarkerOptions> markerOptions = buildMarkers();

                closeCursorIfNecessary();

                return markerOptions;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onCancelled(List<MarkerOptions> markerOptions) {
            closeCursorIfNecessary();
        }

        @Override
        protected void onPostExecute(List<MarkerOptions> markerOptions) {
            try {
                setIndeterminateProgressVisibilty(false);

                if (markerOptions == null) {
                    return;
                }

                final int totalMarkers = markerOptions.size();
                if (totalMarkers == 0 || isCancelled()) {
                    return;
                }

                mMap.clear();

                // TODO: use same following code between DB and JSON
                if (searchedMarker != null) {
                    addSearchedLocationMarker(searchedMarker.getPosition(), searchedMarker.getTitle());
                }


                int i = 0;
                for (MarkerOptions item : markerOptions) {
                    if (isCancelled()) {
                        return;
                    }
                    final Marker marker = mMap.addMarker(item);
                    if (i == mIndexInfoWindow) {
                        marker.showInfoWindow();
                        clickedMarker = marker;
                    }
                    markerOptions.set(i, null);
                    i++;
                }
                markerOptions.clear();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        private List<MarkerOptions> buildMarkers() {
            Cursor cursor = mCursor.get();
            if (cursor == null) {
                return null;
            }

            final int totalMarkers = cursor.getCount();
            if (totalMarkers == 0 || isCancelled()) {
                return null;
            }

            hasHintMarker = (screenCenter != null) && (searchedMarker == null) && (clickedMarker == null);

            final Location locationCenter = new Location(Const.LocationProviders.DEFAULT);
            if (hasHintMarker) {
                locationCenter.setLatitude(screenCenter.latitude);
                locationCenter.setLongitude(screenCenter.longitude);
            }

            final BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(HUE_MARKER);
            final BitmapDescriptor starredMarker = BitmapDescriptorFactory.defaultMarker(HUE_MARKER_STARRED);

            final List<MarkerOptions> markersArray = new ArrayList<MarkerOptions>();
            cursor.moveToFirst();
            do {
                if (isCancelled()) {
                    return markersArray;
                }

                final int idPost = cursor.getInt(Queries.PostsOverlays.ID_POST);
                final double lat = cursor.getDouble(Queries.PostsOverlays.LAT);
                final double lng = cursor.getDouble(Queries.PostsOverlays.LNG);
                final String desc = cursor.getString(Queries.PostsOverlays.CONCAT_DESCRIPTION)
                        .replace(DbValues.CONCAT_SEPARATOR, Const.LINE_SEPARATOR);
                final int isStarred = cursor.getInt(Queries.PostsOverlays.IS_STARRED);

                final LatLng latLng = new LatLng(lat, lng);
                markersArray.add(new MarkerOptions()
                        .title(String.valueOf(idPost))
                        .position(latLng)
                        .snippet(desc)
                        .icon(isStarred == 1 ? starredMarker : defaultMarker)
                        .visible(true));

                if (clickedLatLng != null) {
                    if (clickedLatLng.equals(latLng)) {
                        mIndexInfoWindow = markersArray.size() - 1;
                    }
                } else if (hasHintMarker) {
                    Location locationMarker = new Location(Const.LocationProviders.DEFAULT);
                    locationMarker.setLatitude(lat);
                    locationMarker.setLongitude(lng);

                    if (locationCenter.distanceTo(locationMarker) < DISTANCE_MARKER_HINT) {
                        mIndexInfoWindow = markersArray.size() - 1;
                        hasHintMarker = false;
                    }
                }

            } while (cursor.moveToNext());

            return markersArray;
        }

        private void closeCursorIfNecessary() {
            if (mCursor != null) {
                Cursor c = mCursor.get();
                if (c != null && !c.isClosed()) {
                    c.close();
                }
                mCursor.clear();
            }
        }

    }

    /**
     * Query GeoJSON API to load posts/markers then add them to the map.
     */
    @Deprecated
    private class JsonAsyncTask extends AsyncTask<URL, Void, GeoJSON> {

        @SuppressWarnings("unused")
        private static final String TAG = "JsonAsyncTask";

        // TODO: use WeakReference
        private boolean hasHintMarker = true;

        @Override
        protected void onPreExecute() {

            try {
                // Needed to avoid problems when main activity is sent to
                // background
                setIndeterminateProgressVisibilty(true);


            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected GeoJSON doInBackground(URL... params) {

            URL fetchUrl = params[0];

            // this.screenName is obtained as a user input
            URLConnection urlConnection;
            try {
                urlConnection = fetchUrl.openConnection();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }

            try {
                urlConnection.connect();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            JsonReader reader;
            try {
                reader = new JsonReader(
                        new InputStreamReader(urlConnection.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            JsonParser parser = new JsonParser();
            // TODO surround with try/catch
            JsonElement rootElement = parser.parse(reader);

            if (rootElement != null) {
                Gson gson = new Gson();
                return gson.fromJson(rootElement, GeoJSON.class);
            }

            return null;
        }

        @Override
        protected void onPostExecute(GeoJSON geoJson) {
            try {
                // Needed to avoid problems when main activity is sent to
                // background
                setIndeterminateProgressVisibilty(false);
            } catch (NullPointerException e) {
                e.printStackTrace();
                return;
            }

            if (geoJson == null || geoJson.getFeatures() == null) {
                return;
            }

            final int totalMarkers = geoJson.getFeatures().size();

            if (isCancelled()) {
                return;
            }

            mMap.clear();

            // TODO: use same following code between DB and JSON
            if (searchedMarker != null) {
                addSearchedLocationMarker(searchedMarker.getPosition(), searchedMarker.getTitle());
            }

            if (screenCenter == null || clickedMarker != null) {
                hasHintMarker = false;
            }
            Location locationCenter = new Location(Const.LocationProviders.DEFAULT);
            if (hasHintMarker) {
                locationCenter.setLatitude(screenCenter.latitude);
                locationCenter.setLongitude(screenCenter.longitude);
            }

            for (int i = 0; i < totalMarkers; i++) {

                if (isCancelled()) {
                    return;
                }

                Post post = geoJson.getFeatures().get(i);

                final Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(post.getLatLng())
                        .snippet(post.getDesc())
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(HUE_MARKER)).visible(true));

                if (clickedMarker != null) {
                    if (clickedMarker.getPosition().equals(marker.getPosition())) {
                        marker.showInfoWindow();
                    }
                } else if (hasHintMarker) {
                    Location locationMarker = new Location(Const.LocationProviders.DEFAULT);
                    locationMarker.setLatitude(marker.getPosition().latitude);
                    locationMarker.setLongitude(marker.getPosition().longitude);

                    if (locationCenter.distanceTo(locationMarker) < DISTANCE_MARKER_HINT) {
                        marker.showInfoWindow();
                        hasHintMarker = false;
                        clickedMarker = marker;
                    }
                }
            }
        }
    }

}
