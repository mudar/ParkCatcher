
package ca.mudar.parkcatcher.ui.fragments;

import ca.mudar.parkcatcher.ParkingApp;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    protected OnParkingCalendarChangedListener mListener;

    public interface OnParkingCalendarChangedListener {
        // Parent activity is required to provide getter/setter for the parking
        // time.
        public GregorianCalendar getParkingCalendar();

        public void setParkingTime(int hourOfDay, int minute);
    }

    /**
     * Attach a listener.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnParkingCalendarChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnParkingCalendarChangedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ((ParkingApp) getActivity().getApplicationContext()).updateUiLanguage();

        final GregorianCalendar c = mListener.getParkingCalendar();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        mListener.setParkingTime(hourOfDay, minute);
    }
}
