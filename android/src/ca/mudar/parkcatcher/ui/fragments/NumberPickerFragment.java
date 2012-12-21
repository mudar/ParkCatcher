
package ca.mudar.parkcatcher.ui.fragments;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class NumberPickerFragment extends DialogFragment {

    protected OnParkingCalendarChangedListener mListener;

    public NumberPickerFragment() {

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
            mListener = (OnParkingCalendarChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnParkingCalendarChangedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ((ParkingApp) getActivity().getApplicationContext()).updateUiLanguage();

        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View view = factory.inflate(R.layout.fragment_number_picker, null);
        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.number_picker_duration);

        numberPicker.setMinValue(Const.DURATION_MIN);
        numberPicker.setMaxValue(Const.DURATION_MAX);
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        
        int currentDuration = mListener.getParkingDuration();
        numberPicker.setValue(currentDuration);

        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.dialog_title_duration))
                .setView(view)
                .setPositiveButton(R.string.dialog_done,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                onDurationSet(numberPicker);
                            }
                        }
                )
                .create();
    }

    /**
     * Update the app's duration value
     * 
     * @param seekbar
     */
    private void onDurationSet(NumberPicker numberPicker) {
        int duration = numberPicker.getValue();
        mListener.setParkingDuration(duration);
    }

}
