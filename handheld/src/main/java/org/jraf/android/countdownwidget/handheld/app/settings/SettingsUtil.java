/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2015 Benoit 'BoD' Lubek (BoD@JRAF.org)
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
package org.jraf.android.countdownwidget.handheld.app.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.jraf.android.countdownwidget.R;
import org.jraf.android.countdownwidget.handheld.Constants;

public class SettingsUtil {
    public static int getReleaseDateZone(Context context) {
        SharedPreferences preferenceManager = PreferenceManager.getDefaultSharedPreferences(context);
        String prefCountryValue = preferenceManager.getString(Constants.PREF_COUNTRY, Constants.PREF_COUNTRY_DEFAULT);
        String[] countryValues = context.getResources().getStringArray(R.array.country_values);
        // Find the index of the user's preferred country
        int countryValueIndex;
        for (countryValueIndex = 0; countryValueIndex < countryValues.length; countryValueIndex++) {
            if (prefCountryValue.equals(countryValues[countryValueIndex])) break;
        }
        // Get the corresponding release zone for the country index
        return context.getResources().getIntArray(R.array.releaseDateZonesByCountry)[countryValueIndex];
    }
}
