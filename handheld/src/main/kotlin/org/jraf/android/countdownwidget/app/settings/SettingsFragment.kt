/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2015-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.jraf.android.countdownwidget.R
import org.jraf.android.countdownwidget.prefs.MainPrefs
import org.jraf.android.countdownwidget.util.getFormattedReleaseDate

class SettingsFragment : PreferenceFragmentCompat() {

    private val callbacks: SettingsActivity
        get() = activity as SettingsActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)

        val tutorialPreference = findPreference(MainPrefs.KEY_TUTORIAL)
        tutorialPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            callbacks.onTutorialClicked()
            true
        }

        val aboutPreference = findPreference(MainPrefs.KEY_ABOUT)
        aboutPreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            callbacks.onAboutClicked()
            true
        }

        val sharePreference = findPreference(MainPrefs.KEY_SHARE)
        sharePreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            callbacks.onShareClicked()
            true
        }

        updateCountrySummary()

        // We need this because this Activity is used as the configure Activity for the AppWidget.
        if (activity!!.intent.action == AppWidgetManager.ACTION_APPWIDGET_CONFIGURE) {
            var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
            val extras = activity!!.intent.extras
            if (extras != null) appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            val resultValue = Intent()
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            activity!!.setResult(Activity.RESULT_OK, resultValue)

            // In that case we don't show the tutorial (because obviously the user knows how to add a widget)
            preferenceScreen.removePreference(tutorialPreference)

            // But we DO need to warn the user they MUST press back, otherwise the widget won't be created
            Toast.makeText(activity, R.string.preference_toast, Toast.LENGTH_LONG).show()
        }
    }

    fun updateCountrySummary() {
        val countryPreference = findPreference(MainPrefs.KEY_COUNTRY)
        val countryValueIndex = getCountryValueIndex(activity!!)
        val countryName = resources.getStringArray(R.array.country_labels)[countryValueIndex]
        val releaseDateZone = getReleaseDateZone(activity!!)
        val formattedDate = getFormattedReleaseDate(activity!!, releaseDateZone)
        countryPreference.summary = getString(R.string.preference_country_summary, formattedDate, countryName)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {}
}
