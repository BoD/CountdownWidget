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
package org.jraf.android.countdownwidget.util;

import android.content.Context;

import org.jraf.android.countdownwidget.R;

public class StringUtil {
    public static String getFormattedCountdown(Context context, int nbDays) {
        int resId;
        switch (nbDays) {
            case Integer.MIN_VALUE:
                return "";

            case -1:
                resId = R.string.countdown_minus_1;
                break;

            case 0:
                resId = R.string.countdown_zero;
                break;

            case 1:
                resId = R.string.countdown_one;
                break;

            default:
                if (nbDays < 0) {
                    resId = R.string.countdown_minus_other;
                    nbDays = -nbDays;
                } else {
                    resId = R.string.countdown_other;
                }
                break;
        }

        return context.getResources().getString(resId, nbDays);
    }

    public static String getFormattedCountdownFull(Context context, int nbDays) {
        int resId;
        switch (nbDays) {
            case Integer.MIN_VALUE:
                return "";

            case -1:
                resId = R.string.countdown_full_minus_1;
                break;

            case 0:
                resId = R.string.countdown_full_zero;
                break;

            case 1:
                resId = R.string.countdown_full_one;
                break;

            default:
                if (nbDays < 0) {
                    resId = R.string.countdown_full_minus_other;
                    nbDays = -nbDays;
                } else {
                    resId = R.string.countdown_full_other;
                }
                break;
        }

        return context.getResources().getString(resId, nbDays);
    }
}
