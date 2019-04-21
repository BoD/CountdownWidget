/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2013-present Benoit 'BoD' Lubek (BoD@JRAF.org)
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
package org.jraf.android.countdownwidget.app.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Rect
import android.graphics.Shader.TileMode
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import org.jraf.android.countdownwidget.R
import org.jraf.android.countdownwidget.app.settings.SettingsActivity
import org.jraf.android.countdownwidget.app.settings.getReleaseDateZone
import org.jraf.android.countdownwidget.util.getCountDownToRelease
import org.jraf.android.countdownwidget.util.getFormattedCountdown
import org.jraf.android.util.log.Log

class AppWidgetProvider : android.appwidget.AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        updateWidgets(context, appWidgetManager, appWidgetIds)
    }

    companion object {
        fun updateWidgets(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
            val bitmap = drawLogo(context)
            val remoteViews = RemoteViews(context.packageName, R.layout.appwidget).apply {
                setImageViewBitmap(R.id.imgLogo, bitmap)
            }
            val intent = Intent(context, SettingsActivity::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_CONFIGURE
            }
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            remoteViews.setOnClickPendingIntent(R.id.imgLogo, pendingIntent)
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
        }

        fun drawLogo(context: Context): Bitmap {
            val logoWidth = context.resources.getDimensionPixelSize(R.dimen.logoWidth)
            val logoHeight = context.resources.getDimensionPixelSize(R.dimen.logoHeight)
            val padding = context.resources.getDimensionPixelSize(R.dimen.padding)
            var textSize = context.resources.getDimensionPixelSize(R.dimen.textSize)

            val releaseDateZone = getReleaseDateZone(context)
            val nbDays = getCountDownToRelease(releaseDateZone)
            Log.d("nbDays=%s", nbDays)

            val text = getFormattedCountdown(context, nbDays).toString()

            val paint = Paint().apply {
                textAlign = Align.CENTER
                this.textSize = textSize.toFloat()
                isAntiAlias = true
                isDither = true
                isFilterBitmap = true
                typeface = Typeface.createFromAsset(context.assets, context.getString(R.string.widget_font))
            }

            // Measure text
            val textBounds = Rect()
            paint.getTextBounds(text, 0, text.length, textBounds)

            // Reduce text size if it's too wide
            while (textBounds.width() >= logoWidth) {
                textSize -= 5
                paint.textSize = textSize.toFloat()
                paint.getTextBounds(text, 0, text.length, textBounds)
            }

            val bitmapHeight = logoHeight + padding + textBounds.height()
            val bitmap = Bitmap.createBitmap(logoWidth, bitmapHeight, Config.ARGB_8888)

            // Draw logo
            val canvas = Canvas(bitmap)
            val logoBitmap = (ContextCompat.getDrawable(context, R.drawable.widget_logo) as BitmapDrawable).bitmap
            canvas.drawBitmap(logoBitmap, 0f, 0f, null)

            // Draw text
            val color0 = ContextCompat.getColor(context, R.color.text0)
            val color1 = ContextCompat.getColor(context, R.color.text1)
            paint.shader = LinearGradient(0f, (logoHeight + padding).toFloat(), 0f, bitmapHeight.toFloat(), color0, color1, TileMode.CLAMP)
            canvas.drawText(text, logoWidth / 2f, (logoHeight + padding + -textBounds.top).toFloat(), paint)
            return bitmap
        }
    }
}
