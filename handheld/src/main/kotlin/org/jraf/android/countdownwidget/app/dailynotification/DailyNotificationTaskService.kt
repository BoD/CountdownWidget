/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2016-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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
package org.jraf.android.countdownwidget.app.dailynotification

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.GcmTaskService
import com.google.android.gms.gcm.OneoffTask
import com.google.android.gms.gcm.TaskParams
import org.jraf.android.countdownwidget.util.getTomorrowAtEightAsDelay
import org.jraf.android.util.log.Log
import java.util.concurrent.TimeUnit

class DailyNotificationTaskService : GcmTaskService() {
    override fun onRunTask(taskParams: TaskParams): Int {
        Log.d()
        ContextCompat.startForegroundService(this, Intent(this, DailyNotificationService::class.java))
        // Reschedule, for tomorrow
        scheduleTask(this)
        return GcmNetworkManager.RESULT_SUCCESS
    }

    override fun onInitializeTasks() {
        // This is called when the app is re-installed, after all the tasks have been canceled.
        // Simply re-schedule the task.
        Log.d()
        scheduleTask(this)
    }

    companion object {
        /**
         * Schedule a one off task, set to tomorrow at 8:00.
         */
        fun scheduleTask(context: Context) {
            Log.d()
            val tag = DailyNotificationTaskService::class.java.simpleName
            val tomorrowAtEightAsDelaySeconds = getTomorrowAtEightAsDelay() / 1000
            val periodicTask = OneoffTask.Builder()
                .setTag(tag)
                .setService(DailyNotificationTaskService::class.java)
                .setExecutionWindow(tomorrowAtEightAsDelaySeconds, tomorrowAtEightAsDelaySeconds + TimeUnit.HOURS.toSeconds(2))
                .setPersisted(true)
                .setUpdateCurrent(true)
                .build()
            GcmNetworkManager.getInstance(context).schedule(periodicTask)
        }

        fun unscheduleTask(context: Context) {
            Log.d()
            val tag = DailyNotificationTaskService::class.java.simpleName
            GcmNetworkManager.getInstance(context).cancelTask(tag, DailyNotificationTaskService::class.java)
        }
    }
}
