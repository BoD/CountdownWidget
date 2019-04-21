/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 * 
 * Copyright (C) 2014-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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
package org.jraf.android.countdownwidget.app.settings

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import org.jraf.android.countdownwidget.BuildConfig
import org.jraf.android.countdownwidget.R
import org.jraf.android.countdownwidget.app.appwidget.AppWidgetProvider
import org.jraf.android.countdownwidget.app.dailynotification.DailyNotificationService
import org.jraf.android.countdownwidget.app.dailynotification.DailyNotificationTaskService
import org.jraf.android.countdownwidget.prefs.MainPrefs
import org.jraf.android.countdownwidget.util.getCountDownToReleaseAsText
import org.jraf.android.countdownwidget.util.toBitmap
import org.jraf.android.util.about.AboutActivityIntentBuilder
import org.jraf.android.util.async.Task
import org.jraf.android.util.async.TaskFragment
import org.jraf.android.util.log.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SettingsActivity : AppCompatActivity() {

    private val mainPrefs by lazy {
        MainPrefs(this@SettingsActivity)
    }

    private val onSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            MainPrefs.KEY_DAILY_NOTIFICATION -> if (mainPrefs.dailyNotification) {
                // Schedule an alarm
                DailyNotificationTaskService.scheduleTask(this@SettingsActivity)

                // Also show the notification now
                startService(Intent(this@SettingsActivity, DailyNotificationService::class.java))
            } else {
                // Unschedule the alarm
                DailyNotificationTaskService.unscheduleTask(this@SettingsActivity)
            }

            MainPrefs.KEY_COUNTRY -> {
                // Update the summary of the preference
                val settingsFragment = supportFragmentManager.findFragmentById(android.R.id.content) as SettingsFragment
                settingsFragment.updateCountrySummary()

                // Update the value on all the widgets
                val appWidgetManager = AppWidgetManager.getInstance(this@SettingsActivity)
                val provider = ComponentName(this@SettingsActivity, AppWidgetProvider::class.java)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(provider)
                AppWidgetProvider.updateWidgets(this@SettingsActivity, appWidgetManager, appWidgetIds)

                // Update the notification now if needed
                if (mainPrefs.dailyNotification) {
                    startService(Intent(this@SettingsActivity, DailyNotificationService::class.java))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PreferenceManager.setDefaultValues(this, R.xml.settings, false)
        supportFragmentManager.beginTransaction().replace(android.R.id.content, SettingsFragment()).commit()
        supportFragmentManager.executePendingTransactions()
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
        super.onDestroy()
    }

    fun onAboutClicked() {
        startActivity(
            AboutActivityIntentBuilder()
                .setAppName(getString(R.string.app_name))
                .setBuildDate(BuildConfig.BUILD_DATE)
                .setGitSha1(BuildConfig.GIT_SHA1)
                .setAuthorCopyright(getString(R.string.about_authorCopyright))
                .setLicense(getString(R.string.about_License))
                .setShareTextSubject(getString(R.string.about_shareText_subject))
                .setShareTextBody(getString(R.string.about_shareText_body))
                .setBackgroundResId(R.drawable.about_bg)
                .setShowOpenSourceLicencesLink(true)
                .addLink(getString(R.string.about_email_uri), getString(R.string.about_email_text))
                .addLink(getString(R.string.about_web_uri), getString(R.string.about_web_text))
                .addLink(getString(R.string.about_sources_uri), getString(R.string.about_sources_text))
                .setIsLightIcons(true)
                .build(this)
        )
    }

    fun onTutorialClicked() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://jraf.org/widget/tutorial.html")))
    }


    @SuppressLint("InflateParams")
    fun onShareClicked() {
        val view = layoutInflater.inflate(R.layout.appwidget, null, false)
        val logoBitmap = AppWidgetProvider.drawLogo(this)
        view.findViewById<ImageView>(R.id.imgLogo).setImageBitmap(logoBitmap)
        val viewBitmap = view.toBitmap(SHARE_IMAGE_WIDTH_PX, SHARE_IMAGE_HEIGHT_PX)

        TaskFragment(object : Task<SettingsActivity>() {
            private var savedImageUri: Uri? = null

            override fun doInBackground() {
                try {
                    savedImageUri = activity.saveAndInsertImage(viewBitmap)
                } catch (e: Exception) {
                    Log.w(e, "Could not save image")
                    throw e
                }
            }

            override fun onPostExecuteOk() {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_share_subject))
                    putExtra("sms_body", getString(R.string.settings_share_subject))
                    val releaseDateZone = getReleaseDateZone(activity)
                    putExtra(Intent.EXTRA_TEXT, getCountDownToReleaseAsText(this@SettingsActivity, releaseDateZone))
                    putExtra(Intent.EXTRA_STREAM, savedImageUri)
                }
                activity.startActivity(Intent.createChooser(shareIntent, getString(R.string.settings_share_chooser)))
            }

            override fun onPostExecuteFail() {
                Toast.makeText(activity, R.string.settings_share_problemToast, Toast.LENGTH_LONG).show()
            }
        }).execute(this)
    }

    @WorkerThread
    private fun saveAndInsertImage(image: Bitmap): Uri {
        val picturesPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val path = File(picturesPath, SHARE_DIRECTORY_NAME)
        val ok = path.mkdirs()
        if (!ok && !path.exists()) throw IOException("Could not create directories $path")
        val file = File(path, SHARE_FILE_NAME)
        FileOutputStream(file).use {
            image.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        return Uri.fromFile(file)
    }

    companion object {
        private const val SHARE_DIRECTORY_NAME = "EpisodeVII"
        private const val SHARE_FILE_NAME = "shared.png"
        private const val SHARE_IMAGE_WIDTH_PX = 480
        private const val SHARE_IMAGE_HEIGHT_PX = 248
    }
}