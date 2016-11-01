/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2016 Benoit 'BoD' Lubek (BoD@JRAF.org)
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

import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.TaskParams;

import org.jraf.android.countdownwidget.handheld.util.DateTimeUtil;
import org.jraf.android.util.log.Log;

public class UpdateWearNotificationTaskService extends GcmTaskService {
    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.d();
        startService(new Intent(this, UpdateWearNotificationService.class));
        // Reschedule, for tomorrow
        scheduleTask(this);
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    @Override
    public void onInitializeTasks() {
        // This is called when the app is re-installed, after all the tasks have been canceled.
        // Simply re-schedule the task.
        Log.d();
        scheduleTask(this);
    }

    /**
     * Schedule a one off task, set to tomorrow at 8:00.
     */
    public static void scheduleTask(Context context) {
        Log.d();
        String tag = UpdateWearNotificationTaskService.class.getSimpleName();
        long tomorrowAtEightAsDelaySeconds = DateTimeUtil.getTomorrowAtEightAsDelay() / 1000;
        OneoffTask periodicTask = new OneoffTask.Builder()
                .setTag(tag)
                .setService(UpdateWearNotificationTaskService.class)
                .setExecutionWindow(tomorrowAtEightAsDelaySeconds, tomorrowAtEightAsDelaySeconds + TimeUnit.HOURS.toSeconds(2))
                .setPersisted(true)
                .setUpdateCurrent(true)
                .build();
        GcmNetworkManager.getInstance(context).schedule(periodicTask);
    }

    public static void unscheduleTask(Context context) {
        Log.d();
        String tag = UpdateWearNotificationTaskService.class.getSimpleName();
        GcmNetworkManager.getInstance(context).cancelTask(tag, UpdateWearNotificationTaskService.class);
    }
}
