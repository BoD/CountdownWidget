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
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.jraf.android.countdownwidget.util.getTomorrowAtEightAsDelay
import org.jraf.android.util.handler.HandlerUtil
import org.jraf.android.util.log.Log
import java.util.concurrent.TimeUnit

class DailyNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        Log.d()
        ContextCompat.startForegroundService(context, Intent(context, DailyNotificationService::class.java))
        // Reschedule, for tomorrow
        scheduleTask()
        return Result.success()
    }

    companion object {
        private val TAG = DailyNotificationWorker::class.java.simpleName


        /**
         * Schedule a one off task, set to tomorrow at 8:00.
         */
        fun scheduleTask() {
            Log.d()
            val workRequest = OneTimeWorkRequestBuilder<DailyNotificationWorker>()
                .setInitialDelay(getTomorrowAtEightAsDelay(), TimeUnit.MILLISECONDS)
                // Uncomment to debug...
                // .setInitialDelay(30, TimeUnit.SECONDS)
                .addTag(TAG)
                .build()

            val operation = WorkManager.getInstance().enqueue(workRequest)
            operation.state.observeAndLog()
        }

        fun unscheduleTask() {
            Log.d()
            val operation = WorkManager.getInstance().cancelAllWorkByTag(TAG)
            operation.state.observeAndLog()
        }

        private fun LiveData<Operation.State>.observeAndLog() {
            HandlerUtil.runOnUiThread {
                observeForever(object : Observer<Operation.State> {
                    override fun onChanged(t: Operation.State?) {
                        Log.d("State=$t")
                        if (t is Operation.State.SUCCESS || t is Operation.State.FAILURE) {
                            removeObserver(this)
                        }
                    }
                })
            }
        }
    }
}