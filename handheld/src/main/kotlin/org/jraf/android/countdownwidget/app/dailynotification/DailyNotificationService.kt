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
package org.jraf.android.countdownwidget.app.dailynotification

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import org.jraf.android.countdownwidget.R
import org.jraf.android.countdownwidget.app.settings.SettingsUtil
import org.jraf.android.countdownwidget.prefs.MainPrefs
import org.jraf.android.countdownwidget.util.DateTimeUtil
import org.jraf.android.util.log.Log
import org.jraf.android.util.string.StringUtil
import org.jraf.android.countdownwidget.util.StringUtil as CountdownStringUtil


class DailyNotificationService : IntentService(DailyNotificationService::class.java.simpleName) {

    companion object {
        private const val NOTIFICATION_CHANNEL_MAIN = "NOTIFICATION_CHANNEL_MAIN"
        private const val NOTIFICATION_ID_FOREGROUND = 1
        private const val NOTIFICATION_ID_REGULAR = 2
    }

    private val numberOfDays: Int
        get() {
            val releaseDateZone = SettingsUtil.getReleaseDateZone(this)
            val nbDays = DateTimeUtil.getCountDownToRelease(releaseDateZone)
            Log.d("nbDays=%s", nbDays)
            return nbDays
        }

    override fun onCreate() {
        startForeground(NOTIFICATION_ID_FOREGROUND, createNotification())
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent) {
        Log.d("intent=%s", StringUtil.toString(intent))
        if (!MainPrefs(this).dailyNotification) {
            // We got triggered, but the setting is off so please don't do anything
            Log.d("Setting is off")
            return
        }
        showNotification()
    }

    private fun showNotification() {
        Log.d()
        val notification = createNotification()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_REGULAR, notification)
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel()
        val mainNotifBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_MAIN)

        // No date please
        mainNotifBuilder.setShowWhen(false)

        // Small icon
        mainNotifBuilder.setSmallIcon(R.drawable.ic_notif)

        // Big icon
        mainNotifBuilder.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher))

        // Title
        mainNotifBuilder.setContentTitle(CountdownStringUtil.getFormattedCountdown(this, numberOfDays))

        // Text
        val text = getString(R.string.notif_text)
        mainNotifBuilder.setContentText(text)

        // Color
        mainNotifBuilder.color = ContextCompat.getColor(this, R.color.text1)

        // Content intent
        val mainActivityIntent = Intent()
        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val mainActivityPendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0)
        mainNotifBuilder.setContentIntent(mainActivityPendingIntent)

        // Auto cancel
        mainNotifBuilder.setAutoCancel(true)

        // Let's face it
        mainNotifBuilder.priority = NotificationCompat.PRIORITY_MIN


        // Wear specifics
        val wearableExtender = NotificationCompat.WearableExtender()

        // No icon
        wearableExtender.hintHideIcon = true

        mainNotifBuilder.extend(wearableExtender)

        return mainNotifBuilder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = getString(R.string.notif_channel_main_name)
        val description = getString(R.string.notif_channel_main_description)
        // Let's face it
        val importance = NotificationManager.IMPORTANCE_MIN
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_MAIN, name, importance)
        channel.description = description
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
