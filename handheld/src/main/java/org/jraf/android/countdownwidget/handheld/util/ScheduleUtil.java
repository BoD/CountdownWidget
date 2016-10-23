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
package org.jraf.android.countdownwidget.handheld.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;

import org.jraf.android.countdownwidget.handheld.app.androidwear.AndroidWearService;

public class ScheduleUtil {
    public static void scheduleRepeatingAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = AndroidWearService.getPendingIntent(context, AndroidWearService.ACTION_UPDATE);
        alarmManager.setInexactRepeating(AlarmManager.RTC, DateTimeUtil.getTomorrowAtEight(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    public static void unscheduleRepeatingAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = AndroidWearService.getPendingIntent(context, AndroidWearService.ACTION_UPDATE);
        alarmManager.cancel(pendingIntent);
    }

    public static void scheduleOnceAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = AndroidWearService.getPendingIntent(context, AndroidWearService.ACTION_REMOVE_AND_UPDATE);
        alarmManager.set(AlarmManager.RTC, DateTimeUtil.getInXSeconds(15), pendingIntent);
    }
}
