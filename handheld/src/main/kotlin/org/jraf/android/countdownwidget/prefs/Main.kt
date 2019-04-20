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

import android.content.Context
import org.jraf.android.kprefs.Key
import org.jraf.android.kprefs.Prefs

class MainPrefs(context: Context) {
    private val prefs = Prefs(context)

    /**
     * Country.
     */
    var country by prefs.String("USA", Key(KEY_COUNTRY))

    /**
     * Daily notification.
     */
    var dailyNotification by prefs.Boolean(false)

    companion object {
        const val KEY_TUTORIAL = "tutorial"
        const val KEY_ABOUT = "about"
        const val KEY_SHARE = "share"
        const val KEY_COUNTRY = "country"
        const val KEY_DAILY_NOTIFICATION = "dailyNotification"
    }
}