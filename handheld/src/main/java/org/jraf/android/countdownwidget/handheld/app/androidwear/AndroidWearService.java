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

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import org.jraf.android.countdownwidget.common.wear.WearCommHelper;
import org.jraf.android.countdownwidget.handheld.Constants;
import org.jraf.android.countdownwidget.handheld.util.DateTimeUtil;
import org.jraf.android.util.log.wrapper.Log;

public class AndroidWearService extends IntentService {
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String ACTION_REMOVE_AND_UPDATE = "ACTION_REMOVE_AND_UPDATE";

    public AndroidWearService() {
        super("AndroidWearService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        WearCommHelper.get().connect(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferenceManager.getBoolean(Constants.PREF_ANDROID_WEAR, Constants.PREF_ANDROID_WEAR_DEFAULT)) {
            // We got triggered, but the setting is off so please don't do anything
            return;
        }

        int nbDays = DateTimeUtil.getCountDownToEpisodeVII();
        Log.d("nbDays=" + nbDays);
        WearCommHelper wearCommHelper = WearCommHelper.get();
        if (ACTION_REMOVE_AND_UPDATE.equals(intent.getAction())) {
            wearCommHelper.removeDays();
        }
        wearCommHelper.updateDays(nbDays);
    }

    @Override
    public void onDestroy() {
        WearCommHelper.get().disconnect();
        super.onDestroy();
    }

    public static PendingIntent getPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, AndroidWearService.class);
        intent.setAction(action);
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void backgroundRemoveAndUpdateDays(final Context context) {
        final WearCommHelper wearCommHelper = WearCommHelper.get();
        wearCommHelper.connect(context);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                synchronized (wearCommHelper) {
                    int nbDays = DateTimeUtil.getCountDownToEpisodeVII();
                    Log.d("nbDays=" + nbDays);
                    wearCommHelper.removeDays();
                    wearCommHelper.updateDays(nbDays);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                wearCommHelper.disconnect();
            }
        }.execute();
    }
}
