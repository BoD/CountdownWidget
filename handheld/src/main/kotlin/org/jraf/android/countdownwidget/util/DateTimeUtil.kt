/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 * 
 * Copyright (C) 2014-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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
package org.jraf.android.countdownwidget.util

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import android.text.format.Time
import org.jraf.android.countdownwidget.BuildConfig
import org.jraf.android.util.log.Log
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Get the date of tomorrow at 8:00, as a timestamp.
 */
fun getTomorrowAtEight(): Long {
    val calendar = Calendar.getInstance()
    stripTime(calendar)
    // At eight
    calendar.set(Calendar.HOUR_OF_DAY, 8)
    // Tomorrow
    calendar.add(Calendar.DAY_OF_MONTH, 1)
    return calendar.time.time
}

/**
 * Get the date of tomorrow at 8:00, as a delay (number of milliseconds until this moment, starting from now).
 */
fun getTomorrowAtEightAsDelay(): Long {
    val tomorrowAtEight = getTomorrowAtEight()
    return tomorrowAtEight - System.currentTimeMillis()
}

fun getCalendar(year: Int, month: Int, day: Int): Calendar {
    val res = Calendar.getInstance()
    res.set(Calendar.YEAR, year)
    res.set(Calendar.MONTH, month)
    res.set(Calendar.DAY_OF_MONTH, day)
    stripTime(res)
    return res
}

private fun getJulianDay(cal: Calendar): Int {
    val time = Time()
    time.set(cal.timeInMillis)
    return Time.getJulianDay(cal.timeInMillis, time.gmtoff)
}

fun getNbDaysToDate(from: Calendar, toYear: Int, toMonth: Int, toDay: Int): Int {
    stripTime(from)
    val todayJulianDay = getJulianDay(from)
    val eventCal = getCalendar(toYear, toMonth, toDay)
    stripTime(eventCal)
    val eventJulianDay = getJulianDay(eventCal)

    return eventJulianDay - todayJulianDay
}

fun stripTime(cal: Calendar) {
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
}


fun getInXSeconds(seconds: Int): Long {
    return System.currentTimeMillis() + seconds * 1000
}

fun getCountDownToRelease(releaseDateZone: Int): Int {
    val releaseDate = BuildConfig.MOVIE.releaseDates[releaseDateZone]
    return getNbDaysToDate(Calendar.getInstance(), releaseDate.get(Calendar.YEAR), releaseDate.get(Calendar.MONTH), releaseDate.get(Calendar.DAY_OF_MONTH))
}

fun getCountDownToReleaseAsText(context: Context, releaseDateZone: Int): CharSequence {
    val nbDays = getCountDownToRelease(releaseDateZone)
    return getFormattedCountdownFull(context, nbDays)
}

@SuppressLint("SimpleDateFormat")
fun listAllDates() {
    val starWarsRelease = BuildConfig.MOVIE.releaseDates[3]
    val now = Calendar.getInstance()
    val sdf = SimpleDateFormat("MMMM dd, yyyy")
    while (getJulianDay(now) <= getJulianDay(starWarsRelease)) {
        Log.d(sdf.format(now.time) + " ⇒ " + getNbDaysToDate(now, 2015, Calendar.DECEMBER, 18) + " days ")
        now.add(Calendar.DAY_OF_MONTH, 1)
    }
}

fun getFormattedReleaseDate(context: Context, releaseDateZone: Int): String {
    val releaseDate = BuildConfig.MOVIE.releaseDates[releaseDateZone]
    return DateUtils.formatDateTime(context, releaseDate.time.time, DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR)
}
