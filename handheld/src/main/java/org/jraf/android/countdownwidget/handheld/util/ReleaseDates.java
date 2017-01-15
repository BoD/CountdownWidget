/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2015 Benoit 'BoD' Lubek (BoD@JRAF.org)
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
package org.jraf.android.countdownwidget.handheld.util;

import java.util.Calendar;

public class ReleaseDates {
    public static final Calendar[] RELEASE_DATES_EPISODE_VII = {
            DateTimeUtil.getCalendar(2015, Calendar.DECEMBER, 15),
            DateTimeUtil.getCalendar(2015, Calendar.DECEMBER, 16),
            DateTimeUtil.getCalendar(2015, Calendar.DECEMBER, 17),
            DateTimeUtil.getCalendar(2015, Calendar.DECEMBER, 18),
            DateTimeUtil.getCalendar(2015, Calendar.DECEMBER, 24),
            DateTimeUtil.getCalendar(2016, Calendar.JANUARY, 14),
            DateTimeUtil.getCalendar(2016, Calendar.JANUARY, 15),
            DateTimeUtil.getCalendar(2016, Calendar.JANUARY, 29),
            DateTimeUtil.getCalendar(2015, Calendar.DECEMBER, 14),
    };

    public static final Calendar[] RELEASE_DATES_ROGUE_ONE = {
            DateTimeUtil.getCalendar(2016, Calendar.DECEMBER, 14),
            DateTimeUtil.getCalendar(2016, Calendar.DECEMBER, 15),
            DateTimeUtil.getCalendar(2016, Calendar.DECEMBER, 16),
    };

    public static final Calendar[] RELEASE_DATES_EPISODE_VIII = {
            DateTimeUtil.getCalendar(2017, Calendar.DECEMBER, 14),
            DateTimeUtil.getCalendar(2017, Calendar.DECEMBER, 15),
    };

    public static final Calendar[] RELEASE_DATES_HAN_SOLO = {
            DateTimeUtil.getCalendar(2018, Calendar.MAY, 24),
            DateTimeUtil.getCalendar(2018, Calendar.MAY, 25),
    };
}
