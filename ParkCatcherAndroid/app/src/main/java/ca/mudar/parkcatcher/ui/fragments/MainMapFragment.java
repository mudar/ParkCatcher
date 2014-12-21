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
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.LocationSource.OnLocationChangedListener;
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
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
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
import ca.mudar.parkcatcher.ui.adapters.MyInfoWindowAdapter;
import ca.mudar.parkcatcher.utils.ConnectionHelper;
import ca.mudar.parkcatcher.utils.GeoHelper;
import ca.mudar.parkcatcher.utils.LongPressLocationSource;
import ca.mudar.parkcatcher.utils.SearchMessageHandler;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;

public class MainMapFragment extends SupportMapFragment implements
        CalendarFilterFragment.OnCalendarFilterChangedListener,
        SearchView.OnQueryTextListener,
        SearchMessageHandler.OnMessageHandledListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener,
        Runnable {
    protected static final String TAG = "MainMapFragment";

    protected OnMyLocationChangedListener mListener;

    protected static final int INDEX_OVERLAY_MY_LOCATION = 0x0;
    protected static final int INDEX_OVERLAY_PLACEMARKS = 0x1;

    // protected static final float ZOOM_DEFAULT = 12f;
    private static final float ZOOM_NEAR = 17f;
    private static final float ZOOM_MIN = 16f;
    private static final float HUE_MARKER = 94f;
    private static final float HUE_MARKER_STARRED = BitmapDescriptorFactory.HUE_YELLOW;
    private static final float DISTANCE_MARKER_HINT = 50f;

    private GoogleMap mMap;
    private LongPressLocationSource mLongPressLocationSource;

    protected LocationManager mLocationManager;
    private OnLocationChangedListener onLocationChangedListener;

    private Location initLocation = null;
    private Location mMapCenter = null;
    private LatLng screenCenter = null;
    private Marker clickedMarker = null;
    private Marker searchedMarker = null;
    private boolean hasHintMarker = true;

    ParkingApp parkingApp;

    private Handler handler;
    private JsonAsyncTask jsonAsyncTask = null;
    private DbAsyncTask dbAsyncTask = null;

    private MenuItem searchItem;
    private String postalCode;

    /**
     * Container Activity must implement this interface to receive the list item
     * clicks.
     */
    public interface OnMyLocationChangedListener {
        public void onSearchClickListener();

        public void onCameraChangeListener(boolean isLoading);
    }

    /**
     * Attach a listener.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnMyLocationChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMyLocationChangedListener");
        }
    }

    /**
     * Create map and initialize
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (handler == null) {
            handler = new SearchMessageHandler(this);
        }

        setRetainInstance(true);
    }

//    @Override
//    public View onCreateView(LayoutInflater inflater,
//                             ViewGroup view,
//                             Bundle savedInstance) {
//
//        View layout = super.onCreateView(inflater, view, savedInstance);
//        FrameLayout frameLayout = new FrameLayout(getActivity());
//        frameLayout.setBackgroundColor(
//                getResources().getColor(android.R.color.transparent));
//        ((ViewGroup) layout).addView(frameLayout,
//                new ViewGroup.LayoutParams(
//                        RelativeLayout.LayoutParams.MATCH_PARENT,
//                        RelativeLayout.LayoutParams.MATCH_PARENT
//                )
//        );
//        return layout;
//    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        super.onCreateView(inflater, container, savedInstanceState);
//        View view = inflater.inflate(R.layout.fragment_map, container, false);
//
//        //
//        activityHelper = ActivityHelper.createInstance(getActivity());
//        parkingApp = (ParkingApp) getActivity().getApplicationContext();
//        //
//        setRetainInstance(true);
//
//        // mMap.set
////        CameraUpdate tilted = new CameraUpdate();
////
////        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//        return view;
//    }

    /**
     * Initialize map and LocationManager
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        parkingApp = (ParkingApp) getActivity().getApplicationContext();

        setUpMapIfNeeded();

        // mMapView.setClickable(true);
        // mMapView.setMultiTouchControls(true);

        // mMapController = mMapView.getController();

        mLocationManager = (LocationManager) getActivity().getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        // CameraPosition cameraPos = mMap.getCameraPosition();
        //
        // final CameraPosition cameraPosFrom = new CameraPosition.Builder()
        // .target(cameraPos.target)
        // .zoom(cameraPos.zoom)
        // .bearing(cameraPos.bearing)
        // .tilt(45f)
        // .build();
        // final CameraPosition cameraPosTo = new CameraPosition.Builder()
        // .target(cameraPos.target)
        // .zoom(cameraPos.zoom)
        // .bearing(cameraPos.bearing)
        // .tilt(0)
        // .build();
        // Log.v(TAG, "onResume moveCamera animateCamera");
        // mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosFrom));
        // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosTo));

        initMap();
    }

    /**
     * Enable user location (GPS) updates on map display.
     */
    @Override
    public void onResume() {

        // mLocationOverlay.enableMyLocation();
        super.onResume();

        // TODO Optimize this using savedInstanceState to avoid reload of
        // identical data onResume
        if (ConnectionHelper.hasConnection(getActivity())) {
            final Intent intent = getActivity().getIntent();
            if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                final String query = getAddressFromUri(intent.getData());
                if (query != null) {
                    postalCode = query;
                    showSearchProcessing();
                }
            }

        }

        setUpMapIfNeeded();

        if (mLongPressLocationSource != null) {
            mLongPressLocationSource.onResume();
        }

        // if (mLocationSource != null) {
        // mLocationSource.onResume();
        // }

    }

    /**
     * Disable user location (GPS) updates on map hide.
     */
    @Override
    public void onPause() {
        // mLocationOverlay.disableMyLocation();
        super.onPause();

        if (mLongPressLocationSource != null) {
            mLongPressLocationSource.onPause();
        }
        // if (mLongPressLocationSource != null) {
        // mLocationSource.onPause();
        // }
    }

    /**
     * Create the fragment's Address Search MenuItem, from code.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        // TWEAK: FC when inflated from XML
        searchItem = menu.add(getResources().getString(R.string.menu_search));
        // TODO setIcon replace asset
        searchItem.setIcon(R.drawable.ic_action_search);
        MenuItemCompat.setShowAsAction(searchItem,
                MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        // Create the SearchView
        SearchView searchView = new SearchView(getActivity());
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(this);
        // Collapse when focus lost
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mListener.onSearchClickListener();
                } else {
                    MenuItemCompat.collapseActionView(searchItem);
                }
            }
        });

        // Assign the SearchView to the menuItem
        MenuItemCompat.setActionView(searchItem, searchView);
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
     * Implementation of GoogleMap.OnInfoWindowClickListener
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        final String title = marker.getTitle();

        try {
            final int idPost = Integer.valueOf(title);

            Intent intent = new Intent(getActivity(), DetailsActivity.class);
            intent.putExtra(Const.INTENT_EXTRA_POST_ID, idPost);
            getActivity().startActivity(intent);
        } catch (NumberFormatException e) {
            // Selected marker is not a post.
            // e.printStackTrace();
        }
    }

    /**
     * Implementation of SearchView.OnQueryTextListener. Handle the Address
     * Search query
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        postalCode = query;

        MenuItemCompat.collapseActionView(searchItem);
        if (ConnectionHelper.hasConnection(getActivity())) {
            showSearchProcessing();
        } else {
            parkingApp.showToastText(R.string.toast_search_network_connection_error,
                    Toast.LENGTH_LONG);
        }

        return true;
    }

    /**
     * SearchView.OnQueryTextListener
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        // TODO: use for address suggestions or auto-complete
        return false;
    }

    /**
     * Implementation of Runnable. This runnable thread gets the Geocode search
     * value in the background. Results are sent to the handler.
     */
    @Override
    public void run() {

        Address address = null;
        try {
            /**
             * Geocode search. Takes time and not very reliable!
             */
            address = GeoHelper.findAddressFromName(getActivity(), postalCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Message msg = handler.obtainMessage();
        final Bundle b = new Bundle();

        if (address == null) {
            /**
             * Send error message to handler.
             */
            b.putInt(Const.KEY_BUNDLE_SEARCH_ADDRESS, Const.BUNDLE_SEARCH_ADDRESS_ERROR);
        } else {
            /**
             * Send success message to handler with the found geocoordinates.
             */
            b.putInt(Const.KEY_BUNDLE_SEARCH_ADDRESS, Const.BUNDLE_SEARCH_ADDRESS_SUCCESS);
            b.putDouble(Const.KEY_BUNDLE_ADDRESS_LAT, address.getLatitude());
            b.putDouble(Const.KEY_BUNDLE_ADDRESS_LNG, address.getLongitude());
            b.putString(Const.KEY_BUNDLE_ADDRESS_DESC, address.getAddressLine(0));
        }
        msg.setData(b);

        handler.sendMessage(msg);
    }

    /**
     * Implementation of SearchMessageHandler.OnMessageHandledListener. Handle
     * the runnable thread results. This hides the indeterminate progress bar
     * then centers map on found location or displays error message.
     */
    @Override
    public void OnMessageHandled(Message msg) {
        if (getActivity() == null) {
            return;
        }

        setIndeterminateProgressVisibilty(false);

        final Bundle b = msg.getData();

        if (b.getInt(Const.KEY_BUNDLE_SEARCH_ADDRESS) == Const.BUNDLE_SEARCH_ADDRESS_SUCCESS) {
            /**
             * Address is found, center map on location.
             */
            final Location location = new Location(Const.LOCATION_PROVIDER_SEARCH);
            location.setLatitude(b.getDouble(Const.KEY_BUNDLE_ADDRESS_LAT));
            location.setLongitude(b.getDouble(Const.KEY_BUNDLE_ADDRESS_LNG));
            final String desc = b.getString(Const.KEY_BUNDLE_ADDRESS_DESC);

            setMapCenterZoomed(location);

            /**
             * Add marker for found location
             */

            searchedMarker = mMap.addMarker(new MarkerOptions()

                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title(desc)
                    .snippet(null)
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).visible(true));
            searchedMarker.showInfoWindow();

        } else {
            /**
             * Address not found! Display error message.
             */
            final String errorMsg = String.format(getResources().getString(
                            R.string.toast_search_error),
                    postalCode);
            ((ParkingApp) getActivity().getApplicationContext()).showToastText(
                    errorMsg, Toast.LENGTH_LONG);
        }
    }

    @Override
    public void updateCalendarFilter(GregorianCalendar calendar, int duration) {
        screenCenter = null;
        updateOverlays(calendar, duration);
    }

    // @Override
    // public void onLocationChanged(final Location location) {
    // Log.v(TAG, "onLocationChanged");
    // parkingApp.setLocation(location);
    // onLocationChangedListener.onLocationChanged(location);
    //
    // }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (!checkReady()) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = getMap();
            // Check if we were successful in obtaining the map.
            if (checkReady()) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        if (mLongPressLocationSource == null) {
            mLongPressLocationSource = new LongPressLocationSource(mMap);
            mMap.setOnMapLongClickListener(mLongPressLocationSource);
        }

        mMap.setMyLocationEnabled(true);
        mMap.setMapType(MAP_TYPE_NORMAL);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setAllGesturesEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                .bearing(-34)
                .zoom(10f)
                .target(new LatLng(45.5, -73.666667))
                .build()));

        mMap.setLocationSource(null);

        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(getActivity()));

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

    private boolean checkReady() {
        if (mMap == null) {
            parkingApp.showToastText(R.string.toast_map_not_ready, Toast.LENGTH_SHORT);
            return false;
        }
        return true;
    }

    private void setIndeterminateProgressVisibilty(boolean isLoading) {
        mListener.onCameraChangeListener(isLoading);
    }

    private class DbAsyncTask extends AsyncTask<Object, Void, Cursor> {

        // TODO: use WeakReference

        @Override
        protected void onPreExecute() {
            try {
                setIndeterminateProgressVisibilty(true);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Cursor doInBackground(Object... params) {

            final int dayOfWeek = (Integer) params[0];
            final double hourOfWeek = (Double) params[1] + (dayOfWeek - 1) * 24;
            final int duration = (Integer) params[2];

            final LatLng NE = (LatLng) params[3];
            final LatLng SW = (LatLng) params[4];

            final GregorianCalendar calendar = parkingApp.getParkingCalendar();
            // API uses values 0-365 (or 364)
            final int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR) - 1;

            try {
                Cursor cur = getActivity()
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
                                null);
                return cur;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onCancelled(Cursor cursor) {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            try {
                setIndeterminateProgressVisibilty(false);
            } catch (NullPointerException e) {
                e.printStackTrace();
                return;
            }

            if (cursor == null) {
                return;
            }

            final int totalMarkers = cursor.getCount();
            if (totalMarkers == 0 || isCancelled()) {
                cursor.close();
                return;
            }

            mMap.clear();

            // TODO: use same following code between DB and JSON
            if (searchedMarker != null) {
                searchedMarker = mMap.addMarker(new MarkerOptions()
                        .position(searchedMarker.getPosition())
                        .title(searchedMarker.getTitle())
                        .snippet(null)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).visible(true));
                searchedMarker.showInfoWindow();
                hasHintMarker = false;
            }

            if (screenCenter == null || clickedMarker != null) {
                hasHintMarker = false;
            }
            Location locationCenter = new Location(Const.LOCATION_PROVIDER_DEFAULT);
            if (hasHintMarker) {
                locationCenter.setLatitude(screenCenter.latitude);
                locationCenter.setLongitude(screenCenter.longitude);
            }

            final BitmapDescriptor defaultMarker = BitmapDescriptorFactory.defaultMarker(HUE_MARKER);
            final BitmapDescriptor starredMarker = BitmapDescriptorFactory.defaultMarker(HUE_MARKER_STARRED);

            cursor.moveToFirst();
            do {
                if (isCancelled()) {
                    cursor.close();
                    return;
                }

                final int idPost = cursor.getInt(Queries.PostsOverlays.ID_POST);
                final double lat = cursor.getDouble(Queries.PostsOverlays.LAT);
                final double lng = cursor.getDouble(Queries.PostsOverlays.LNG);
                final String desc = cursor.getString(Queries.PostsOverlays.CONCAT_DESCRIPTION)
                        .replace(DbValues.CONCAT_SEPARATOR, Const.LINE_SEPARATOR);
                final int isStarred = cursor.getInt(Queries.PostsOverlays.IS_STARRED);
                final Marker marker = mMap.addMarker(new MarkerOptions()
                        .title(String.valueOf(idPost))
                        .position(new LatLng(lat, lng))
                        .snippet(desc)
                        .icon(isStarred == 1 ? starredMarker : defaultMarker)
                        .visible(true));

                if (clickedMarker != null) {
                    if (clickedMarker.getPosition().equals(marker.getPosition())) {
                        marker.showInfoWindow();
                    }
                } else if (hasHintMarker) {
                    Location locationMarker = new Location(Const.LOCATION_PROVIDER_DEFAULT);
                    locationMarker.setLatitude(marker.getPosition().latitude);
                    locationMarker.setLongitude(marker.getPosition().longitude);

                    if (locationCenter.distanceTo(locationMarker) < DISTANCE_MARKER_HINT) {
                        marker.showInfoWindow();
                        hasHintMarker = false;
                        clickedMarker = marker;
                    }
                }

            } while (cursor.moveToNext());
            cursor.close();
        }

    }

    private class JsonAsyncTask extends AsyncTask<URL, Void, GeoJSON> {

        // TODO: use WeakReference

        @SuppressWarnings("unused")
        protected static final String TAG = "JsonAsyncTask";

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
                GeoJSON geoJson = gson.fromJson(rootElement, GeoJSON.class);
                return geoJson;
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

            // Log.v(TAG, "geoJson  = " + geoJson.toString());

            final int totalMarkers = geoJson.getFeatures().size();
            // Log.v(TAG, "totalMarkers = " + totalMarkers);

            if (isCancelled()) {
                return;
            }

            mMap.clear();

            // TODO: use same following code between DB and JSON
            if (searchedMarker != null) {
                searchedMarker = mMap.addMarker(new MarkerOptions()
                        .position(searchedMarker.getPosition())
                        .title(searchedMarker.getTitle())
                        .snippet(null)
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).visible(true));
                searchedMarker.showInfoWindow();
            }

            if (screenCenter == null || clickedMarker != null) {
                hasHintMarker = false;
            }
            Location locationCenter = new Location(Const.LOCATION_PROVIDER_DEFAULT);
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
                    Location locationMarker = new Location(Const.LOCATION_PROVIDER_DEFAULT);
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

    @Deprecated
    public void updateOverlaysForced() {
        Log.e(TAG, "updateOverlaysForced");
    }

    private void updateOverlays() {
        updateOverlays(parkingApp.getParkingCalendar(), parkingApp.getParkingDuration());
    }

    private void updateOverlays(GregorianCalendar calendar, int duration) {
        // Log.v(TAG, "current zoom = " + mMap.getCameraPosition().zoom);

        if (mMap.getCameraPosition().zoom < ZOOM_MIN) {
            parkingApp.showToastText(R.string.toast_map_zoom_to_update, Toast.LENGTH_LONG);
            return;
        }

        final Projection projection = mMap.getProjection();

        if (screenCenter == null) {
            screenCenter = mMap.getCameraPosition().target;
        } else {
            // TODO: get screen center without repeated use of Projection
            final LatLng cameraTarget = mMap.getCameraPosition().target;
            final Point cameraCenterPoint = projection.toScreenLocation(cameraTarget);
            final int screenWidth = cameraCenterPoint.x * 2;
            final int screenHeight = cameraCenterPoint.y * 2;

            final Point oldCenterPoint = projection.toScreenLocation(screenCenter);

            // Log.v(TAG, "cameraCenterPoint = " + cameraCenterPoint
            // + ". width = " + screenWidth
            // + ". height = " + screenHeight);
            if ((oldCenterPoint.x > screenWidth * 0.75)
                    || (oldCenterPoint.x < screenWidth * 0.25)
                    || (oldCenterPoint.y > screenHeight * 0.75)
                    || (oldCenterPoint.y < screenHeight * 0.25)) {
                // Log.e(TAG, "screenCenter redefined");
                screenCenter = cameraTarget;
            } else {
                // Log.v(TAG, "No refresh needed");
                return;
            }

        }

        final VisibleRegion region = projection.getVisibleRegion();
        final LatLngBounds bounds = region.latLngBounds;
        final LatLng SW = bounds.southwest;
        final LatLng NE = bounds.northeast;

        // Log.e(TAG, "new bounds = " + bounds.toString());

        // Get arguments for API call
        final int day = (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ? 7
                : calendar.get(Calendar.DAY_OF_WEEK) - 1);
        final double parkingHour = calendar.get(Calendar.HOUR_OF_DAY)
                + Math.round(calendar.get(Calendar.MINUTE) / 0.6) / 100.00d;

        if (Const.HAS_OFFLINE) {
            queryOverlays(day, parkingHour, duration, NE, SW);
        } else {
            downloadOverlays(day, parkingHour, duration, NE, SW);
        }

    }

    private void queryOverlays(int day, double parkingHour, int duration, LatLng NE, LatLng SW) {
        if (dbAsyncTask != null) {
            dbAsyncTask.cancel(true);
        }

        dbAsyncTask = new DbAsyncTask();
        dbAsyncTask.execute(day, parkingHour, duration, NE, SW);
    }

    private void downloadOverlays(int day, double parkingHour, int duration, LatLng NE, LatLng SW) {
        final String fetchUrl = String.format(Const.Api.POSTS_LIVE,
                day, parkingHour, duration,
                NE.latitude, SW.longitude, SW.latitude, NE.longitude);

        URL apiURL = null;
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
     * Initialize Map: centre and load posts
     */
    protected void initMap() {

        // mLocationOverlay = new
        // MyLocationOverlay(getActivity().getApplicationContext(), mMapView);
        // mLocationOverlay.enableCompass();
        // mLocationOverlay.enableMyLocation();
        // mMapView.getOverlays().add(INDEX_OVERLAY_MY_LOCATION,
        // mLocationOverlay);

        // ArrayList<MapMarker> mapMarkers = fetchMapMarkers();
        //
        // Drawable drawable =
        // this.getResources().getDrawable(R.drawable.ic_map_default_marker);
        // MyItemizedOverlay mItemizedOverlay = new MyItemizedOverlay(drawable,
        // mMapView);
        //
        // if (mapMarkers.size() > 0) {
        // for (MapMarker marker : mapMarkers) {
        //
        // MyOverlayItem overlayitem = new MyOverlayItem(marker.geoPoint,
        // marker.name,
        // marker.address, marker.id, marker.extra);
        // mItemizedOverlay.addOverlay(overlayitem);
        // }
        // mMapView.getOverlays().add(INDEX_OVERLAY_PLACEMARKS,
        // mItemizedOverlay);
        // }

    }

    /**
     * Set new map center.
     *
     * @param mapCenter
     */
    protected void animateToPoint(Location mapCenter) {
        Log.v(TAG, "animateToPoint "
                + String.format("mapCenter = %s", mapCenter));


        if (mMap == null) {
            return;
        }
        if (mapCenter != null) {
            // Log.v(TAG, "ZOOM_NEAR");
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                    mapCenter.getLatitude(), mapCenter.getLongitude()), ZOOM_NEAR));
        } else {
            // Log.v(TAG, "ZOOM_DEFAULT + initialAnimateToPoint");
            mMap.moveCamera(CameraUpdateFactory.zoomTo(ZOOM_NEAR));
            initialAnimateToPoint();
        }
    }

    /**
     * Initial map center animation on detected user location. If user is more
     * than minimum-distance from the city, center the map on Downtown. Also
     * defines the zoom.
     */
    protected void initialAnimateToPoint() {
        final List<String> enabledProviders = mLocationManager.getProviders(true);

        double coordinates[] = Const.MAPS_DEFAULT_COORDINATES;
        final double lat = coordinates[0];
        final double lng = coordinates[1];

        final Location userLocation = parkingApp.getLocation();
        if (userLocation != null) {
            /**
             * Center on app's user location.
             */
            // Log.v(TAG, "initialAnimateToPoint lat = " +
            // userLocation.getLatitude() + ". Lon = "
            // + userLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                    userLocation.getLatitude(), userLocation.getLongitude())));
        } else {
            /**
             * Center on Downtown.
             */
            // Log.v(TAG, "initialAnimateToPoint. Center on Downtown");
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
        }

        if ((mMapCenter == null) && enabledProviders.contains(LocationManager.NETWORK_PROVIDER)) {
            /**
             * Get user current location then display on map.
             */
            // mLocationOverlay.runOnFirstFix(new Runnable() {
            // public void run() {
            // GeoPoint userGeoPoint = mLocationOverlay.getMyLocation();
            //
            // if (mListener != null) {
            // mListener.onMyLocationChanged(userGeoPoint);
            // onMyLocationChanged(userGeoPoint);
            // }
            //
            // /**
            // * If user is very far from Montreal (> 25km) we center the
            // * map on Downtown.
            // */
            // final float[] resultDistance = new float[1];
            // android.location.Location.distanceBetween(lat, lng,
            // (userGeoPoint.getLatitudeE6() / 1E6),
            // (userGeoPoint.getLongitudeE6() / 1E6), resultDistance);
            //
            // if (resultDistance[0] > Const.MAPS_MIN_DISTANCE) {
            // userGeoPoint = new GeoPoint((int) (lat * 1E6), (int) (lng *
            // 1E6));
            // }
            //
            // mMapCenter = userGeoPoint;
            // if (userGeoPoint != null) {
            // mMapController.animateTo(userGeoPoint);
            // }
            // }
            // });
        } else if (mMapCenter != null) {
            /**
             * The AppHelper knows the user location from a previous query, so
             * use the saved value.
             */
            // Log.v(TAG, "initialAnimateToPoint. mMapCenter != null");
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                    mMapCenter.getLatitude(), mMapCenter.getLongitude())));
        }
    }

    /**
     * Setter for the MapCenter GeoPoint. Centers map on the new location and
     * displays the ViewBallooon.
     *
     * @param mapCenter The new location
     */
    public void setMapCenter(Location mapCenter) {
        Log.v(TAG, "setMapCenter "
                + String.format("mapCenter = %s", mapCenter));

        initLocation = mapCenter;
        animateToPoint(mapCenter);

        // if (mapCenter != null) {
        // Overlay overlayPlacemarks =
        // mMapView.getOverlays().get(INDEX_OVERLAY_PLACEMARKS);
        // overlayPlacemarks.onTap(mapCenter, mMapView);
        // }
    }

    @Deprecated
    private void onMyLocationChanged(final Location location) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                // TODO: verify that new location is sent to service & favorites
                // fragment
                parkingApp.setLocation(location);
                Log.v(TAG, "TODO: onMyLocationChanged on UI");
                // invalidateOptionsMenu();
            }
        });
    }

    /**
     * Sets the map center on the user real location with a near zoom. Used for
     * Tab re-selection.
     */
    public void resetMapCenter() {
        searchedMarker = null;
        if (!mMap.isMyLocationEnabled()) {
            mMap.setMyLocationEnabled(true);
        }
        // mMap.setLocationSource(mLocationSource);
        mMap.setLocationSource(null);
        setMapCenterZoomed(parkingApp.getLocation());
    }

    /**
     * Sets the map center on the location with a near zoom. Used for Address
     * Search and Tab re-selection.
     */
    private void setMapCenterZoomed(Location mapCenter) {
        // mMapController.setZoom(ZOOM_NEAR);
        setMapCenter(mapCenter);
    }

    /**
     * Called from the activity to handle onKeyUp
     */
    public void toggleSearchView(boolean isDisplayed) {
        if (isVisible() && isDisplayed) {
            MenuItemCompat.expandActionView(searchItem);
        } else {
            MenuItemCompat.collapseActionView(searchItem);
        }
    }

    /**
     * Show the Indeterminate ProgressBar and start the Geocode search thread.
     */
    protected void showSearchProcessing() {
        setIndeterminateProgressVisibilty(true);

        // TODO x
//        try {
//            final ActionBar ab = getActivity().getActionBar();
//            if (ab.getSelectedTab().getPosition() != Const.TABS_INDEX_MAP) {
//                ab.setSelectedNavigationItem(Const.TABS_INDEX_MAP);
//            }
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }

        final Thread thread = new Thread(this);
        thread.start();
    }

    private String getAddressFromUri(Uri uri) {
        String address = null;

        List<String> pathSegments = uri.getPathSegments();

        // http://www.capteurdestationnement.com/map/search/2/15.5/12/h2w2e7

        if ((pathSegments.size() == 6)
                && (pathSegments.get(0).equals(Const.INTENT_EXTRA_URL_PATH_MAP))
                && (pathSegments.get(1).equals(Const.INTENT_EXTRA_URL_PATH_SEARCH))) {

            try {
                address = pathSegments.get(5);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        return address;
    }

}
