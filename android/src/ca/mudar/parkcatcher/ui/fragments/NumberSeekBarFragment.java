
package ca.mudar.parkcatcher.ui.fragments;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class NumberSeekBarFragment extends DialogFragment {

    protected OnParkingCalendarChangedListener mListener;

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
        final View view = factory.inflate(R.layout.fragment_number_seekbar, null);
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
