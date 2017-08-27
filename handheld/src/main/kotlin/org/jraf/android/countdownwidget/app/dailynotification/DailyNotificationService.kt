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
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
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
        private const val NOTIFICATION_ID = 0
    }

    override fun onHandleIntent(intent: Intent) {
        Log.d("intent=%s", StringUtil.toString(intent))
        if (!MainPrefs.get(this).dailyNotification) {
            // We got triggered, but the setting is off so please don't do anything
            Log.d("Setting is off")
            return
        }
        val releaseDateZone = SettingsUtil.getReleaseDateZone(this)
        val nbDays = DateTimeUtil.getCountDownToRelease(releaseDateZone)
        Log.d("nbDays=%s", nbDays)

        showNotification(nbDays)
    }

    private fun getPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, DailyNotificationService::class.java)
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun showNotification(nbDays: Int) {
        Log.d()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel()
        val mainNotifBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_MAIN)

        // No date please
        mainNotifBuilder.setShowWhen(false)

        // Small icon
        mainNotifBuilder.setSmallIcon(R.drawable.ic_notif)

        // Big icon
        mainNotifBuilder.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_launcher))

        // Title
        mainNotifBuilder.setContentTitle(CountdownStringUtil.getFormattedCountdown(this, nbDays))

        // Text
        val text = getString(R.string.notif_text)
        mainNotifBuilder.setContentText(text)

        // Content intent
        val mainActivityIntent = Intent()
        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        val mainActivityPendingIntent = PendingIntent.getActivity(this, 0, mainActivityIntent, 0)
        mainNotifBuilder.setContentIntent(mainActivityPendingIntent)

        // Auto cancel
        mainNotifBuilder.setAutoCancel(true)


        // Wear specifics
        val wearableExtender = NotificationCompat.WearableExtender()

        // No icon
        wearableExtender.hintHideIcon = true

        // Backgtound
        wearableExtender.background = BitmapFactory.decodeResource(resources, R.drawable.ic_notif_logo)

        mainNotifBuilder.extend(wearableExtender)


        val notification = mainNotifBuilder.build()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val name = getString(R.string.notif_channel_main_name)
        val description = getString(R.string.notif_channel_main_description)
        // Let's face it
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_MAIN, name, importance)
        channel.description = description
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
