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

import android.content.Context

import org.jraf.android.countdownwidget.R
import org.jraf.android.countdownwidget.prefs.MainPrefs

fun getReleaseDateZone(context: Context): Int {
    val countryValueIndex = getCountryValueIndex(context)
    // Get the corresponding release zone for the country index
    return context.resources.getIntArray(R.array.releaseDateZonesByCountry)[countryValueIndex]
}

fun getCountryValueIndex(context: Context): Int {
    val prefCountryValue = MainPrefs(context).country
    val countryValues = context.resources.getStringArray(R.array.country_values)
    // Find the index of the user's preferred country
    var countryValueIndex = 0
    while (countryValueIndex < countryValues.size) {
        if (prefCountryValue == countryValues[countryValueIndex]) break
        countryValueIndex++
    }
    return countryValueIndex
}
