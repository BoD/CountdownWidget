/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2013 Benoit 'BoD' Lubek (BoD@JRAF.org)
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
package org.jraf.android.countdownwidget.app.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import org.jraf.android.countdownwidget.R;
import org.jraf.android.countdownwidget.app.settings.SettingsActivity;
import org.jraf.android.countdownwidget.app.settings.SettingsUtil;
import org.jraf.android.countdownwidget.util.DateTimeUtil;
import org.jraf.android.countdownwidget.util.StringUtil;
import org.jraf.android.util.log.Log;

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateWidgets(context, appWidgetManager, appWidgetIds);
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Bitmap bitmap = drawLogo(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget);
        remoteViews.setImageViewBitmap(R.id.imgLogo, bitmap);
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.imgLogo, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    public static Bitmap drawLogo(Context context) {
        int logoWidth = context.getResources().getDimensionPixelSize(R.dimen.logoWidth);
        int logoHeight = context.getResources().getDimensionPixelSize(R.dimen.logoHeight);
        int padding = context.getResources().getDimensionPixelSize(R.dimen.padding);
        int textSize = context.getResources().getDimensionPixelSize(R.dimen.textSize);

        int releaseDateZone = SettingsUtil.getReleaseDateZone(context);
        int nbDays = DateTimeUtil.getCountDownToRelease(releaseDateZone);
        Log.d("nbDays=%s", nbDays);

        String text = StringUtil.getFormattedCountdown(context, nbDays).toString();

        Paint paint = new Paint();
        paint.setTextAlign(Align.CENTER);
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.widget_font)));

        // Measure text
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);

        // Reduce text size if it's too wide
        while (textBounds.width() >= logoWidth) {
            textSize -= 5;
            paint.setTextSize(textSize);
            paint.getTextBounds(text, 0, text.length(), textBounds);
        }

        int bitmapHeight = logoHeight + padding + textBounds.height();
        Bitmap bitmap = Bitmap.createBitmap(logoWidth, bitmapHeight, Config.ARGB_8888);

        // Draw logo
        Canvas canvas = new Canvas(bitmap);
        Bitmap logoBitmap = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.widget_logo)).getBitmap();
        canvas.drawBitmap(logoBitmap, 0, 0, null);

        // Draw text
        int color0 = ContextCompat.getColor(context, R.color.text0);
        int color1 = ContextCompat.getColor(context, R.color.text1);
        paint.setShader(new LinearGradient(0, logoHeight + padding, 0, bitmapHeight, color0, color1, TileMode.CLAMP));
        canvas.drawText(text, logoWidth / 2F, logoHeight + padding + -textBounds.top, paint);
        return bitmap;
    }
}
