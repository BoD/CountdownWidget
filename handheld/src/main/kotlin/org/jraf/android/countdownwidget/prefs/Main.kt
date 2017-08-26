/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2017 Benoit 'BoD' Lubek (BoD@JRAF.org)
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
package org.jraf.android.countdownwidget.prefs

import org.jraf.android.prefs.DefaultBoolean
import org.jraf.android.prefs.DefaultString
import org.jraf.android.prefs.Name
import org.jraf.android.prefs.Prefs

@Prefs
class Main {
    companion object {
        const val PREF_TUTORIAL = "tutorial"
        const val PREF_ABOUT = "about"
        const val PREF_SHARE = "share"
        const val PREF_COUNTRY = "country"
        const val PREF_DAILY_NOTIFICATION = "dailyNotification"

        private const val PREF_COUNTRY_DEFAULT = "USA"
    }

    /**
     * Tutorial.
     */
    var tutorial: String? = null

    /**
     * About.
     */
    var about: String? = null

    /**
     * Share.
     */
    var share: String? = null

    /**
     * Country.
     */
    @DefaultString(PREF_COUNTRY_DEFAULT)
    @Name(PREF_COUNTRY)
    var country: String? = null

    /**
     * Daily notification.
     */
    @DefaultBoolean(true)
    var dailyNotification: Boolean? = null
}