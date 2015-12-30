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
package org.jraf.android.countdownwidget.handheld.app.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.jraf.android.countdownwidget.handheld.Constants;
import org.jraf.android.countdownwidget.handheld.app.androidwear.AndroidWearService;
import org.jraf.android.countdownwidget.handheld.util.ScheduleUtil;
import org.jraf.android.util.log.Log;
import org.jraf.android.util.string.StringUtil;

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("intent=%s", StringUtil.toString(intent));

        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferenceManager.getBoolean(Constants.PREF_ANDROID_WEAR, Constants.PREF_ANDROID_WEAR_DEFAULT)) {
            // Schedule an alarm
            ScheduleUtil.scheduleRepeatingAlarm(context);

            // Also send the value now
            AndroidWearService.backgroundRemoveAndUpdateDays(context);

            // Also send the value in a minute (this allows the phone to finish booting and the Wear connexion to be up)
            ScheduleUtil.scheduleOnceAlarm(context);
        }
    }
}
