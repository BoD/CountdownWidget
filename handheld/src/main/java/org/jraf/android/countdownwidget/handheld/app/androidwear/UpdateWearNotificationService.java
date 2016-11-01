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
package org.jraf.android.countdownwidget.handheld.app.androidwear;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.jraf.android.countdownwidget.common.wear.WearHelper;
import org.jraf.android.countdownwidget.handheld.Constants;
import org.jraf.android.countdownwidget.handheld.app.settings.SettingsUtil;
import org.jraf.android.countdownwidget.handheld.util.DateTimeUtil;
import org.jraf.android.util.log.Log;
import org.jraf.android.util.string.StringUtil;

public class UpdateWearNotificationService extends IntentService {
    public UpdateWearNotificationService() {
        super(UpdateWearNotificationService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("intent=%s", StringUtil.toString(intent));
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferenceManager.getBoolean(Constants.PREF_ANDROID_WEAR, Constants.PREF_ANDROID_WEAR_DEFAULT)) {
            // We got triggered, but the setting is off so please don't do anything
            Log.d("Setting is off");
            return;
        }
        int releaseDateZone = SettingsUtil.getReleaseDateZone(this);
        int nbDays = DateTimeUtil.getCountDownToRelease(releaseDateZone);
        Log.d("nbDays=%s", nbDays);
        WearHelper wearHelper = WearHelper.get();
        wearHelper.connect(this);
        wearHelper.removeDays();
        wearHelper.updateDays(nbDays);
        wearHelper.disconnect();
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, UpdateWearNotificationService.class);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void scheduleAlarmIn1Minute(Context context) {
        Log.d();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getPendingIntent(context);
        alarmManager.set(AlarmManager.RTC_WAKEUP, DateTimeUtil.getInXSeconds(60), pendingIntent);
    }
}
