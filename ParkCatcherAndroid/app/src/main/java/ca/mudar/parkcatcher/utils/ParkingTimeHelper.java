
package ca.mudar.parkcatcher.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    /**
     * Get ISO dayOfWeek from Android Calendar
     *
     * @param calendar
     * @return
     */
    public static int getIsoDayOfWeek(GregorianCalendar calendar) {
        return (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY ?
                7 : calendar.get(Calendar.DAY_OF_WEEK) - 1);
    }

    public static double getHourRounded(GregorianCalendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY)
                + Math.round(calendar.get(Calendar.MINUTE) / 0.6) / 100.00d;
    }

    /**
     * Get Android dayOfWeek from ISO
     *
     * @param
     * @return
     */
    public static int getDayOfWeek(int isoDayOfWeek) {
        return (isoDayOfWeek == 7) ? Calendar.SUNDAY : (isoDayOfWeek + 1);
    }

    /**
     * Get Hour of week
     *
     * @param calendar
     * @return
     */
    public static double getHourOfWeek(GregorianCalendar calendar) {
        final int dayOfWeek = ParkingTimeHelper.getIsoDayOfWeek(calendar);
        final double parkingHour = ParkingTimeHelper.getHourRounded(calendar);

        return getHourOfWeek(dayOfWeek, parkingHour);
    }

    public static double getHourOfWeek(int dayOfWeek, double parkingHour) {
        return parkingHour + (dayOfWeek - 1) * 24;
    }

    /**
     * API uses ISO values 0-365 (or 364)
     *
     * @param calendar
     * @return
     */
    public static int getIsoDayOfYear(GregorianCalendar calendar) {
        return calendar.get(Calendar.DAY_OF_YEAR) - 1;
    }

    /**
     * Get hour values from clock. ex: 12.5 returns 12 h
     *
     * @param clockTime
     * @return
     */
    public static int getHoursFromClockTime(double clockTime) {
        return (int) Math.floor(clockTime);
    }

    /**
     * Get minutes values from clock. ex: 12.5 returns 30 m
     *
     * @param clockTime
     * @return
     */
    public static int getMintuesFromClockTime(double clockTime) {
        return (int) ((clockTime % 1.0d) * 60);
    }


    public static String[] getCursorLoaderSelectionArgs(GregorianCalendar calendar, int duration) {
        final double hourOfWeek = ParkingTimeHelper.getHourOfWeek(calendar);
        final int dayOfYear = ParkingTimeHelper.getIsoDayOfYear(calendar);

        return new String[]{
                Double.toString(hourOfWeek),
                Integer.toString(duration),
                Integer.toString(dayOfYear)
        };
    }
}
