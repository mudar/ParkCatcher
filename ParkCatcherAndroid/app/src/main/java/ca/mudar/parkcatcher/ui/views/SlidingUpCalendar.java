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

package ca.mudar.parkcatcher.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ca.mudar.parkcatcher.Const;
import ca.mudar.parkcatcher.ParkingApp;
import ca.mudar.parkcatcher.R;
import ca.mudar.parkcatcher.ui.dialogs.DatePickerFragment;
import ca.mudar.parkcatcher.ui.dialogs.NumberPickerFragment;
import ca.mudar.parkcatcher.ui.dialogs.NumberSeekBarFragment;
import ca.mudar.parkcatcher.ui.dialogs.TimePickerFragment;
import ca.mudar.parkcatcher.utils.ParkingTimeHelper;

public class SlidingUpCalendar extends SlidingUpPanelLayout implements
        SlidingUpPanelLayout.PanelSlideListener {
    private static final String TAG = "SlidingUpCalendar";
    private static final int FADE_DURATION = 300;
    private static final int CHILDREN_COUNT = 2;
    private static final float ANCHOR_POINT = 0.7f;

    private final Context context;
    private final ParkingApp parkingApp;
    private final int layoutHeightMin;
    private final int layoutHeightMax;
    private int mLayoutLastHeight;
    private PanelSlideListener mSlidingUpCalendarListener;

    public SlidingUpCalendar(Context context) {
        this(context, null);
    }

    public SlidingUpCalendar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingUpCalendar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;
        this.parkingApp = (ParkingApp) context.getApplicationContext();
        this.layoutHeightMin = getResources().getDimensionPixelSize(R.dimen.slider_collapsed_height);
        this.layoutHeightMax = getResources().getDimensionPixelSize(R.dimen.slider_drag_height);

//        setAnchorPoint(ANCHOR_POINT);
        setPanelSlideListener(this);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(Const.SavedInstanceKeys.PARCELABLE, super.onSaveInstanceState());
        bundle.putInt(Const.SavedInstanceKeys.DIMENSION, mLayoutLastHeight);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            final int dimension = bundle.getInt(Const.SavedInstanceKeys.DIMENSION, -1);
            if (dimension != -1) {
                mLayoutLastHeight = dimension;
            }

            state = bundle.getParcelable(Const.SavedInstanceKeys.PARCELABLE);
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        setDragView(findViewById(R.id.drag_handle));
        updateParkingCalendar();

        setClickListeners();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final boolean intercept = super.onInterceptTouchEvent(ev);

        if (!intercept && isPanelAnchored()) {
            // collapse if shadow touched
            final int action = MotionEventCompat.getActionMasked(ev);
            if (action == MotionEvent.ACTION_DOWN) {
                if (isShadowUnder((int) ev.getY())) {
                    collapsePanel();
                    return true;
                }
            }
        }

        return intercept;
    }

    @Override
    public void showPanel() {
        if (getChildCount() == CHILDREN_COUNT) {
            getChildAt(0).setPadding(0, 0, 0, layoutHeightMin);
        }
        super.showPanel();
    }

    @Override
    public void hidePanel() {
        if (getChildCount() == CHILDREN_COUNT) {
            getChildAt(0).setPadding(0, 0, 0, 0);
        }
        super.hidePanel();
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        if (slideOffset < 0) {
            return;
        }

        if (mSlidingUpCalendarListener != null) {
            mSlidingUpCalendarListener.onPanelSlide(panel, slideOffset);
        }
    }

    @Override
    public void onPanelCollapsed(View panel) {
        mLayoutLastHeight = layoutHeightMin;

        if (mSlidingUpCalendarListener != null) {
            mSlidingUpCalendarListener.onPanelCollapsed(panel);
        }
    }

    @Override
    public void onPanelExpanded(View panel) {
        mLayoutLastHeight = layoutHeightMax;

        if (mSlidingUpCalendarListener != null) {
            mSlidingUpCalendarListener.onPanelExpanded(panel);
        }
    }

    @Override
    public void onPanelAnchored(View panel) {
        mLayoutLastHeight = layoutHeightMax;

        if (mSlidingUpCalendarListener != null) {
            mSlidingUpCalendarListener.onPanelAnchored(panel);
        }
    }

    @Override
    public void onPanelHidden(View view) {

    }

    /**
     * Determine if TouchEvent was on the shadow (ie: above a collapsed pane)
     *
     * @param y The pointer's AXIS_Y
     * @return Shadow Drawable is under the TouchEvent
     */
    private boolean isShadowUnder(int y) {
        View dragView = findViewById(R.id.drag_handle);
        if (dragView == null) return false;
        int[] viewLocation = new int[2];
        dragView.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);
        int screenY = parentLocation[1] + y;
        return screenY < viewLocation[1];
    }

    private void setClickListeners() {
        findViewById(R.id.btn_day).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });
        findViewById(R.id.btn_start).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });
        findViewById(R.id.btn_duration).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPickerDialog(v);
            }
        });
    }

    public void setSlidingUpQueueListener(PanelSlideListener listener) {
        mSlidingUpCalendarListener = listener;
    }

    public void updateParkingCalendar() {
        updateParkingTimeTitle();
        updateParkingDateButton();
        updateParkingTimeButton();
        updateParkingDurationButton();
    }

    @SuppressLint("DefaultLocale")
    public void updateParkingTimeTitle() {
        final GregorianCalendar c = parkingApp.getParkingCalendar();
        final int duration = parkingApp.getParkingDuration();

        ((TextView) findViewById(R.id.drawer_time_title)).setText(
                ParkingTimeHelper.getTitle(context, c, duration));
    }

    public void updateParkingDateButton() {
        final GregorianCalendar c = parkingApp.getParkingCalendar();

        ((Button) findViewById(R.id.btn_day)).setText(ParkingTimeHelper.getDate(context, c));
    }

    public void updateParkingTimeButton() {
        final GregorianCalendar c = parkingApp.getParkingCalendar();

        ((Button) findViewById(R.id.btn_start)).setText(ParkingTimeHelper.getTime(context, c));
    }

    public void updateParkingDurationButton() {
        final int duration = parkingApp.getParkingDuration();

        ((Button) findViewById(R.id.btn_duration)).setText(ParkingTimeHelper.getDuration(context,
                duration));
    }

    public void updateParkingTimeFromUri(Uri uri) {
        Log.v(TAG, "updateParkingTimeFromUri");
        List<String> pathSegments = uri.getPathSegments();

        // http://www.capteurdestationnement.com/map/search/2/15.5/12
        // http://www.capteurdestationnement.com/map/search/2/15.5/12/h2w2e7

        if ((pathSegments.size() >= 5)
                && (pathSegments.get(0).equals(Const.INTENT_EXTRA_URL_PATH_MAP))
                && (pathSegments.get(1).equals(Const.INTENT_EXTRA_URL_PATH_SEARCH))) {

            try {
                final int day = Integer.valueOf(pathSegments.get(2));
                final double time = Double.valueOf(pathSegments.get(3));
                final int duration = Integer.valueOf(pathSegments.get(4));

                final int hourOfDay = (int) time;
                final int minute = (int) ((time - hourOfDay) * 60);

                GregorianCalendar calendar = new GregorianCalendar();

                calendar.set(Calendar.DAY_OF_WEEK, day == 7 ? Calendar.SUNDAY : day + 1);
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                parkingApp.setParkingCalendar(calendar);
                parkingApp.setParkingDuration(duration);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public void showDatePickerDialog(View v) {
        final FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        final Fragment targetFragment = fm.findFragmentByTag(Const.FragmentTags.SLIDING_UP_CALENDAR);

        final DialogFragment dialog = new DatePickerFragment();
        dialog.setTargetFragment(targetFragment, Const.RequestCodes.DATE_PICKER);
        dialog.show(fm, Const.FragmentTags.PICKER_DATE);
    }

    public void showTimePickerDialog(View v) {
        final FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        final Fragment targetFragment = fm.findFragmentByTag(Const.FragmentTags.SLIDING_UP_CALENDAR);

        final DialogFragment dialog = new TimePickerFragment();
        dialog.setTargetFragment(targetFragment, Const.RequestCodes.TIME_PICKER);
        dialog.show(fm, Const.FragmentTags.PICKER_TIME);
    }

    // Build >= 11
    public void showNumberPickerDialog(View v) {
        final FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        final Fragment targetFragment = fm.findFragmentByTag(Const.FragmentTags.SLIDING_UP_CALENDAR);

        final DialogFragment dialog = new NumberPickerFragment();
        dialog.setTargetFragment(targetFragment, Const.RequestCodes.NUMBER_PICKER);
        dialog.show(fm, Const.FragmentTags.PICKER_NUMBER);
    }

    // Build < v11
    public void showNumberSeekBarDialog(View v) {
        final FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        final Fragment targetFragment = fm.findFragmentByTag(Const.FragmentTags.SLIDING_UP_CALENDAR);

        final DialogFragment dialog = new NumberSeekBarFragment();
        dialog.setTargetFragment(targetFragment, Const.RequestCodes.NUMBER_SEEKBAR);
        dialog.show(fm, Const.FragmentTags.SEEKBAR_NUMBER);
    }
}
