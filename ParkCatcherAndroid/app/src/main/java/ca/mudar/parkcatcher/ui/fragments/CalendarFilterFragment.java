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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.GregorianCalendar;

import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.ui.dialogs.DatePickerFragment;
import ca.mudar.parkcatcher.ui.dialogs.NumberPickerFragment;
import ca.mudar.parkcatcher.ui.dialogs.NumberSeekBarFragment;
import ca.mudar.parkcatcher.ui.dialogs.TimePickerFragment;
import ca.mudar.parkcatcher.utils.ParkingTimeHelper;

public class CalendarFilterFragment extends Fragment implements
        DatePickerFragment.OnParkingCalendarChangedListener,
        TimePickerFragment.OnParkingCalendarChangedListener,
        NumberSeekBarFragment.OnParkingCalendarChangedListener,
        NumberPickerFragment.OnParkingCalendarChangedListener {
    private static final String TAG = "CalendarFilterFragment";

    private Context mContext;
    private ParkingApp parkingApp;
    private TextView vTimeTitle;
    private Button vBtnDay;
    private Button vBtnStart;
    private Button vBtnDuration;
    private CalendarFilterUpdatedListener mListener;

    /**
     * Attach a listener.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof CalendarFilterUpdatedListener) {
            mListener = (CalendarFilterUpdatedListener) activity;
        }
    }

    @Override
    public void setTargetFragment(Fragment fragment, int requestCode) {
        super.setTargetFragment(fragment, requestCode);
        if (mListener == null) {
            try {
                mListener = (CalendarFilterUpdatedListener) fragment;
            } catch (ClassCastException e) {
                throw new ClassCastException(fragment.toString()
                        + " must implement CalendarFilterUpdatedListener");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_calendar_filter, container, false);

        vTimeTitle = (TextView) view.findViewById(R.id.drawer_time_title);
        vBtnDay = (Button) view.findViewById(R.id.btn_day);
        vBtnStart = (Button) view.findViewById(R.id.btn_start);
        vBtnDuration = (Button) view.findViewById(R.id.btn_duration);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.mContext = getActivity();
        this.parkingApp = (ParkingApp) getActivity().getApplicationContext();
    }

    @Override
    public GregorianCalendar getParkingCalendar() {
        return parkingApp.getParkingCalendar();
    }

    @Override
    public int getParkingDuration() {
        return parkingApp.getParkingDuration();
    }

    @Override
    public void setParkingDuration(int duration) {
        parkingApp.setParkingDuration(duration);

        // Update UI values
        updateParkingDurationButton();
        updateParkingTimeTitle();

        // Update listener
        if (mListener != null) {
            mListener.onCalendarFilterChanged(parkingApp.getParkingCalendar(), duration);
        }
    }

    @Override
    public void setParkingDate(int year, int month, int day) {
        final GregorianCalendar calendar = parkingApp.setParkingDate(year, month, day);

        // Update UI values
        updateParkingDateButton();
        updateParkingTimeTitle();

        // Update listener
        if (mListener != null) {
            mListener.onCalendarFilterChanged(calendar, parkingApp.getParkingDuration());
        }
    }

    @Override
    public void setParkingTime(int hourOfDay, int minute) {
        final GregorianCalendar calendar = parkingApp.setParkingTime(hourOfDay, minute);

        // Update UI values
        updateParkingTimeButton();
        updateParkingTimeTitle();

        // Update listener
        if (mListener != null) {
            mListener.onCalendarFilterChanged(calendar, parkingApp.getParkingDuration());
        }
    }

    private void updateParkingTimeTitle() {
        final GregorianCalendar c = parkingApp.getParkingCalendar();
        final int duration = parkingApp.getParkingDuration();

        vTimeTitle.setText(ParkingTimeHelper.getTitle(mContext, c, duration));
    }

    private void updateParkingDateButton() {
        final GregorianCalendar c = parkingApp.getParkingCalendar();

        vBtnDay.setText(ParkingTimeHelper.getDate(mContext, c));
    }

    private void updateParkingTimeButton() {
        final GregorianCalendar c = parkingApp.getParkingCalendar();

        vBtnStart.setText(ParkingTimeHelper.getTime(mContext, c));
    }

    private void updateParkingDurationButton() {
        final int duration = parkingApp.getParkingDuration();

        vBtnDuration.setText(ParkingTimeHelper.getDuration(mContext, duration));
    }

    public interface CalendarFilterUpdatedListener {
        public void onCalendarFilterChanged(GregorianCalendar calendar, int duration);
    }

}
