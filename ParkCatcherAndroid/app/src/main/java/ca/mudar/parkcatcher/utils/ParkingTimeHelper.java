
package ca.mudar.parkcatcher.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import ca.mudar.parkcatcher.R;

@SuppressLint("DefaultLocale")
public class ParkingTimeHelper {
    private static final String TAG = "ParkingTimeHelper";

    public static String getTitle(Context context, GregorianCalendar calendar, int duration) {
        final Resources res = context.getResources();
        final Date date = calendar.getTime();

        SimpleDateFormat df = new SimpleDateFormat(res.getString(
                R.string.drawer_title_day), Locale.getDefault());

        String day = df.format(date);
        // Required for French: capitalize first character
        day = day.substring(0, 1).toUpperCase() + day.substring(1);

        df = new SimpleDateFormat(res.getString(
                R.string.drawer_time_btn), Locale.getDefault());
        String time = df.format(date);

        if (duration == 1) {
            return String.format(res.getString(R.string.drawer_time_title), day,
                    time);
        }
        else {
            return String.format(res.getString(R.string.drawer_time_title_plural),
                    day, time, duration);
        }
    }

    public static String getDate(Context context, GregorianCalendar calendar) {
        SimpleDateFormat df = new SimpleDateFormat(context.getResources().getString(
                R.string.drawer_date_btn), Locale.getDefault());
        String date = df.format(calendar.getTime());
        // Required for French: capitalize first character
        date = date.substring(0, 1).toUpperCase() + date.substring(1);

        return date;
    }

    public static String getTime(Context context, GregorianCalendar calendar) {
        SimpleDateFormat df = new SimpleDateFormat(context.getResources().getString(
                R.string.drawer_time_btn), Locale.getDefault());
        String time = df.format(calendar.getTime());

        return time;
    }

    public static String getDuration(Context context, int duration) {
        if (duration == 1) {
            return context.getResources().getString(R.string.drawer_duration_btn);
        }
        else {
            return String.format(context.getResources()
                    .getString(R.string.drawer_duration_plural_btn), duration);
        }
    }
}
