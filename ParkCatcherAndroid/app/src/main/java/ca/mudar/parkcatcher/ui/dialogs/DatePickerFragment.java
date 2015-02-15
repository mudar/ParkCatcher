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
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private OnParkingCalendarChangedListener mListener;

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ((ParkingApp) getActivity().getApplicationContext()).updateUiLanguage();

        final GregorianCalendar c = mListener.getParkingCalendar();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if (Const.SUPPORTS_LOLLIPOP) {
            return new DatePickerDialog(getActivity(), R.style.DialogTheme, this,
                    year,
                    month,
                    day);
        } else {
            // Skip theming pre-lollipop
            return new DatePickerDialog(getActivity(), this,
                    year,
                    month,
                    day);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        mListener.setParkingDate(year, month, day);
    }

    public interface OnParkingCalendarChangedListener {
        // Target Fragment or parent Activity are required to provide getter/setter for the parking
        // time.
        public GregorianCalendar getParkingCalendar();

        public void setParkingDate(int year, int month, int day);
    }
}
