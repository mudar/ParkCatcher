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

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.utils.ConnectionHelper;

public class MapErrorFragment extends Fragment implements
        View.OnClickListener {

    private int mErrorCode;

    public static MapErrorFragment newInstance(int errorCode) {
        final MapErrorFragment fragment = new MapErrorFragment();
        final Bundle bundle = new Bundle();
        bundle.putInt(Const.BundleExtras.ERROR_CODE, errorCode);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mErrorCode = getArguments().getInt(Const.BundleExtras.ERROR_CODE);
        final int resource = (mErrorCode == Const.StartupStatus.ERROR_PLAYSERVICES) ?
                R.layout.fragment_error_playservices : R.layout.fragment_error_connection;

        final View view = inflater.inflate(resource, container, false);

        final View btnResolve = view.findViewById(R.id.btn_resolve_error);
        btnResolve.setOnClickListener(this);
        ViewCompat.setElevation(btnResolve,
                getResources().getDimensionPixelSize(R.dimen.elevation_high));

        if (mErrorCode == Const.StartupStatus.ERROR_PLAYSERVICES) {
            displayPlayservicesErrorMessage(view);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().setTitle(R.string.app_name);
    }

    /**
     * Callback action for the Retry button on the initial noConnection screen.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (mErrorCode == Const.StartupStatus.ERROR_PLAYSERVICES) {
            try {
                final int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

                if (GooglePlayServicesUtil.isUserRecoverableError(statusCode)) {
                    final PendingIntent intent = GooglePlayServicesUtil
                            .getErrorPendingIntent(statusCode, getActivity(), 0);
                    try {
                        /**
                         * Resolve the error.
                         * The activity's onActivityResult method will be invoked
                         */
                        new ConnectionResult(statusCode, intent)
                                .startResolutionForResult(
                                        getActivity(),
                                        Const.RequestCodes.PLAYSERVICES);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Error cannot be resolved, show human-readable string of the error code
                    final String errorString = GooglePlayServicesUtil.getErrorString(statusCode);
                    ((ParkingApp) getActivity().getApplicationContext())
                            .showToastText(errorString, Toast.LENGTH_LONG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (ConnectionHelper.hasConnection(getActivity())) {
                ((ParkingApp) getActivity().getApplicationContext())
                        .showToastText(R.string.map_no_connection_restart, Toast.LENGTH_LONG);
                final Intent intent = getActivity().getIntent();
                getActivity().finish();
                startActivity(intent);
            } else {
                ConnectionHelper.showDialogNoConnection(getActivity());
            }
        }
    }

    private void displayPlayservicesErrorMessage(View view) {
        final int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

        int message;
        switch (statusCode) {
            case ConnectionResult.SUCCESS:
                // Should not happen!
                view.findViewById(R.id.playservices_message_container).setVisibility(View.GONE);
                return;
            case ConnectionResult.SERVICE_MISSING:
                message = isTablet() ?
                        R.string.common_google_play_services_install_text_tablet :
                        R.string.common_google_play_services_install_text_phone;
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                message = R.string.common_google_play_services_update_text;
                break;
            case ConnectionResult.SERVICE_DISABLED:
                message = R.string.common_google_play_services_needs_enabling_title;
                break;
//            case ConnectionResult.SERVICE_INVALID:
            default:
                message = R.string.common_google_play_services_unknown_issue;
                break;
        }

        ((TextView) view.findViewById(R.id.playservices_message)).setText(message);
        view.findViewById(R.id.playservices_message_container).setOnClickListener(this);
    }

    /**
     * Verify if device is a phone or tablet, to display the right Playservices message.
     *
     * @return True if device is a tablet
     */
    private boolean isTablet() {
        return (getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
