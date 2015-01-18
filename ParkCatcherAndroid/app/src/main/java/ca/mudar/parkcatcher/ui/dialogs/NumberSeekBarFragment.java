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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;

public class NumberSeekBarFragment extends DialogFragment {

    private OnParkingCalendarChangedListener mListener;

    public NumberSeekBarFragment() {

    }

    public interface OnParkingCalendarChangedListener {
        // Parent activity is required to provide getter/setter for the parking
        // duration.
        public int getParkingDuration();

        public void setParkingDuration(int duration);
    }

    /**
     * Attach a listener.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            final Fragment targetFragment = getTargetFragment();
            if (targetFragment != null && (targetFragment instanceof  OnParkingCalendarChangedListener)) {
                mListener = (OnParkingCalendarChangedListener) targetFragment;
            } else {
                mListener = (OnParkingCalendarChangedListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnParkingCalendarChangedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ((ParkingApp) getActivity().getApplicationContext()).updateUiLanguage();

        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.dialog_number_seekbar, null);
        final SeekBar seekbar = (SeekBar) view.findViewById(R.id.seekbar_duration);
        final TextView vDurationValue = (TextView) view.findViewById(R.id.duration_value);

        seekbar.setMax(Const.DURATION_MAX - Const.DURATION_MIN);

        int currentDuration = mListener.getParkingDuration();
        seekbar.setProgress(currentDuration - Const.DURATION_MIN);
        setDurationText(vDurationValue, currentDuration - Const.DURATION_MIN);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setDurationText(vDurationValue, progress);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.dialog_title_duration))
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
    private void onDurationSet(SeekBar seekbar) {
        int duration = seekbar.getProgress() + Const.DURATION_MIN;
        mListener.setParkingDuration(duration);
    }

    /**
     * Display the seekbar selected value.
     * 
     * @param view
     * @param progress
     */
    private void setDurationText(TextView view, int progress) {
        String sDurationValue;
        if (progress + Const.DURATION_MIN == 1) {
            sDurationValue = getResources().getString(R.string.dialog_value_duration);
        }
        else {
            sDurationValue = String.format(
                    getResources().getString(R.string.dialog_value_duration_plural),
                    progress + Const.DURATION_MIN);
        }

        view.setText(sDurationValue);
    }

}
