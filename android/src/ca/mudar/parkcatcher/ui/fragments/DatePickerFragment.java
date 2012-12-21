
package ca.mudar.parkcatcher.ui.fragments;

import ca.mudar.parkcatcher.ParkingApp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    protected OnParkingCalendarChangedListener mListener;

    public interface OnParkingCalendarChangedListener {
        // Parent activity is required to provide getter/setter for the parking
        // time.
        public GregorianCalendar getParkingCalendar();

        public void setParkingDate(int year, int month, int day);
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
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        mListener.setParkingDate(year, month, day);
    }
}
