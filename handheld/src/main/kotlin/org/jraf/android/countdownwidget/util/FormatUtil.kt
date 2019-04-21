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
package org.jraf.android.countdownwidget.util

import android.content.Context
import android.text.Html

import org.jraf.android.countdownwidget.R

fun getFormattedCountdown(context: Context, nbDays: Int): CharSequence {
    var nbDays = nbDays
    val resId: Int
    when (nbDays) {
        Integer.MIN_VALUE -> return ""

        -1 -> resId = R.string.countdown_minus_1

        0 -> resId = R.string.countdown_zero

        1 -> resId = R.string.countdown_one

        else -> if (nbDays < 0) {
            nbDays = -nbDays
            resId = R.string.countdown_minus_other
        } else {
            resId = R.string.countdown_other
        }
    }

    val htmlSource = context.resources.getString(resId, nbDays)
    return Html.fromHtml(htmlSource)
}

fun getFormattedCountdownFull(context: Context, nbDays: Int): CharSequence {
    var nbDays = nbDays
    val resId: Int
    when (nbDays) {
        Integer.MIN_VALUE -> return ""

        -1 -> resId = R.string.countdown_full_minus_1

        0 -> resId = R.string.countdown_full_zero

        1 -> resId = R.string.countdown_full_one

        else -> if (nbDays < 0) {
            resId = R.string.countdown_full_minus_other
            nbDays = -nbDays
        } else {
            resId = R.string.countdown_full_other
        }
    }

    val htmlSource = context.resources.getString(resId, nbDays)
    return Html.fromHtml(htmlSource)
}
