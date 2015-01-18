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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.utils.ConnectionHelper;

public class MapErrorFragment extends Fragment implements
        View.OnClickListener {

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

        final int errorCode = getArguments().getInt(Const.BundleExtras.ERROR_CODE);
        final int resource = (errorCode == Const.StartupStatus.ERROR_PLAYSERVICES) ?
                R.layout.fragment_error_playservices : R.layout.fragment_error_connection;

        final View view = inflater.inflate(resource, container, false);

        final View btnRetry = view.findViewById(R.id.btn_retry_connection);
        if (btnRetry != null) {
            btnRetry.setOnClickListener(this);
            ViewCompat.setElevation(btnRetry,
                    getResources().getDimensionPixelSize(R.dimen.elevation_high));
        }

        return view;
    }

    /**
     * Callback action for the Retry button on the initial noConnection screen.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
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
