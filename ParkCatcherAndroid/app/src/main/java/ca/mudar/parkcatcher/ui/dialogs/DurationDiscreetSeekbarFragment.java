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

package ca.mudar.parkcatcher.ui.dialogs;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;

public class DurationDiscreetSeekbarFragment extends DialogFragment {

    private OnParkingCalendarChangedListener mListener;

    public DurationDiscreetSeekbarFragment() {

    }

    /**
     * Attach a listener.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            final Fragment targetFragment = getTargetFragment();
            if (targetFragment != null && (targetFragment instanceof OnParkingCalendarChangedListener)) {
                mListener = (OnParkingCalendarChangedListener) targetFragment;
            } else {
                mListener = (OnParkingCalendarChangedListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnParkingCalendarChangedListener");
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ((ParkingApp) getActivity().getApplicationContext()).updateUiLanguage();

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View view = inflater.inflate(R.layout.dialog_duration_discreet_seekbar, null);
        final View vTitle = inflater.inflate(R.layout.dialog_duration_discreet_seekbar_title, (android.view.ViewGroup) view, false);
        final TextView vTitleDuration = (TextView) vTitle.findViewById(R.id.dialog_title);
        final DiscreteSeekBar seekbar = (DiscreteSeekBar) view.findViewById(R.id.seekbar_duration);

        int currentDuration = mListener.getParkingDuration();
        vTitleDuration.setText(String.valueOf(currentDuration));
        seekbar.setProgress(currentDuration);

        seekbar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
//                getDialog().setTitle(getDurationTitle(value));
                vTitleDuration.setText(String.valueOf(value));
            }
        });

        AlertDialog.Builder builder;
        if (Const.SUPPORTS_LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }

        return builder
                .setCustomTitle(vTitle)
//                .setTitle(getDurationTitle(currentDuration))
                .setView(view)
                .setPositiveButton(R.string.dialog_set,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                onDurationSet(seekbar);
                            }
                        }
                )
                .setNegativeButton(R.string.dialog_cancel, null)
                .create();
    }

    /**
     * Update the app's duration value
     *
     * @param seekbar
     */
    private void onDurationSet(DiscreteSeekBar seekbar) {
        int duration = seekbar.getProgress();
        mListener.setParkingDuration(duration);
    }

    /**
     * Update the Dialog title.
     *
     * @param progress
     */
    private String getDurationTitle(int progress) {
        return getResources().getQuantityString(
                R.plurals.dialog_value_duration,
                progress,
                progress);
    }

    public interface OnParkingCalendarChangedListener {
        // Parent activity is required to provide getter/setter for the parking
        // duration.
        public int getParkingDuration();

        public void setParkingDuration(int duration);
    }

}
