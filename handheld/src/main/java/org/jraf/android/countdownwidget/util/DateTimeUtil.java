/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 * 
 * Copyright (C) 2014 Benoit 'BoD' Lubek (BoD@JRAF.org)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jraf.android.countdownwidget.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.text.format.DateUtils;
import android.text.format.Time;

import org.jraf.android.countdownwidget.BuildConfig;
import org.jraf.android.util.log.Log;

public class DateTimeUtil {
    public static Calendar getCalendar(int year, int month, int day) {
        Calendar res = Calendar.getInstance();
        res.set(Calendar.YEAR, year);
        res.set(Calendar.MONTH, month);
        res.set(Calendar.DAY_OF_MONTH, day);
        stripTime(res);
        return res;
    }

    private static int getJulianDay(Calendar cal) {
        Time time = new Time();
        time.set(cal.getTimeInMillis());
        return Time.getJulianDay(cal.getTimeInMillis(), time.gmtoff);
    }

    public static int getNbDaysToDate(Calendar from, int toYear, int toMonth, int toDay) {
        stripTime(from);
        int todayJulianDay = getJulianDay(from);
        Calendar eventCal = getCalendar(toYear, toMonth, toDay);
        stripTime(eventCal);
        int eventJulianDay = getJulianDay(eventCal);

        return eventJulianDay - todayJulianDay;
    }

    public static void stripTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Get the date of tomorrow at 8:00, as a timestamp.
     */
    public static long getTomorrowAtEight() {
        Calendar calendar = Calendar.getInstance();
        // At eight
        stripTime(calendar);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        // Tomorrow
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime().getTime();
    }

    /**
     * Get the date of tomorrow at 8:00, as a delay (number of milliseconds until this moment, starting from now).
     */
    public static long getTomorrowAtEightAsDelay() {
        long tomorrowAtEight = getTomorrowAtEight();
        return tomorrowAtEight - System.currentTimeMillis();
    }


    public static long getInXSeconds(int seconds) {
        return System.currentTimeMillis() + seconds * 1000;
    }

    public static int getCountDownToRelease(int releaseDateZone) {
        Calendar releaseDate = BuildConfig.MOVIE.getReleaseDates()[releaseDateZone];
        return getNbDaysToDate(Calendar.getInstance(), releaseDate.get(Calendar.YEAR), releaseDate.get(Calendar.MONTH), releaseDate.get(Calendar.DAY_OF_MONTH));
    }

    public static CharSequence getCountDownToReleaseAsText(Context context, int releaseDateZone) {
        int nbDays = getCountDownToRelease(releaseDateZone);
        return StringUtil.getFormattedCountdownFull(context, nbDays);
    }

    public static void listAllDates() {
        Calendar starWarsRelease = BuildConfig.MOVIE.getReleaseDates()[3];
        Calendar now = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
        while (getJulianDay(now) <= getJulianDay(starWarsRelease)) {
            Log.d(sdf.format(now.getTime()) + " ⇒ " + getNbDaysToDate(now, 2015, Calendar.DECEMBER, 18) + " days ");
            now.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    public static String getFormattedReleaseDate(Context context, int releaseDateZone) {
        Calendar releaseDate = BuildConfig.MOVIE.getReleaseDates()[releaseDateZone];
        return DateUtils.formatDateTime(context, releaseDate.getTime().getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);
    }
}
