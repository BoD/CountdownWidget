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
package org.jraf.android.countdownwidget.handheld.app.settings;

import java.io.File;
import java.io.FileOutputStream;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.WorkerThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.jraf.android.countdownwidget.BuildConfig;
import org.jraf.android.countdownwidget.R;
import org.jraf.android.countdownwidget.handheld.Constants;
import org.jraf.android.countdownwidget.handheld.app.androidwear.AndroidWearService;
import org.jraf.android.countdownwidget.handheld.app.appwidget.AppWidgetProvider;
import org.jraf.android.countdownwidget.handheld.util.DateTimeUtil;
import org.jraf.android.countdownwidget.handheld.util.ScheduleUtil;
import org.jraf.android.countdownwidget.handheld.util.ViewUtil;
import org.jraf.android.util.about.AboutActivityIntentBuilder;
import org.jraf.android.util.io.IoUtil;
import org.jraf.android.util.log.wrapper.Log;

public class SettingsActivity extends AppCompatActivity {

    public static final String SHARE_DIRECTORY_NAME = "EpisodeVII";
    public static final String SHARE_FILE_NAME = "shared.png";
    public static final int SHARE_IMAGE_WIDTH_PX = 480;
    public static final int SHARE_IMAGE_HEIGHT_PX = 248;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
        super.onDestroy();
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new SharedPreferences
            .OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (Constants.PREF_ANDROID_WEAR.equals(key)) {
                if (sharedPreferences.getBoolean(Constants.PREF_ANDROID_WEAR, Constants.PREF_ANDROID_WEAR_DEFAULT)) {
                    // Schedule an alarm
                    ScheduleUtil.scheduleRepeatingAlarm(SettingsActivity.this);

                    // Also send the value now
                    AndroidWearService.backgroundRemoveAndUpdateDays(SettingsActivity.this);

                    // Also send the value in a minute (this allows the Wearable app to finish installing)
                    ScheduleUtil.scheduleOnceAlarm(SettingsActivity.this);
                } else {
                    // Unschedule the alarm
                    ScheduleUtil.unscheduleRepeatingAlarm(SettingsActivity.this);
                }
            } else if (Constants.PREF_COUNTRY.equals(key)) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(SettingsActivity.this);
                ComponentName provider = new ComponentName(SettingsActivity.this, AppWidgetProvider.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
                AppWidgetProvider.updateWidgets(SettingsActivity.this, appWidgetManager, appWidgetIds);

                SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
                settingsFragment.updateCountrySummary();
            }
        }
    };

    void onAboutClicked() {
        AboutActivityIntentBuilder builder = new AboutActivityIntentBuilder();
        builder.setAppName(getString(R.string.app_name));
        builder.setBuildDate(BuildConfig.BUILD_DATE);
        builder.setGitSha1(BuildConfig.GIT_SHA1);
        builder.setAuthorCopyright(getString(R.string.about_authorCopyright));
        builder.setLicense(getString(R.string.about_License));
        builder.setShareTextSubject(getString(R.string.about_shareText_subject));
        builder.setShareTextBody(getString(R.string.about_shareText_body));
        builder.setBackgroundResId(R.drawable.about_bg);
        builder.addLink(getString(R.string.about_email_uri), getString(R.string.about_email_text));
        builder.addLink(getString(R.string.about_web_uri), getString(R.string.about_web_text));
        builder.addLink(getString(R.string.about_sources_uri), getString(R.string.about_sources_text));
        builder.setIsLightIcons(true);
        startActivity(builder.build(this));
    }

    void onTutorialClicked() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://jraf.org/widget/tutorial.html")));
    }


    void onShareClicked() {
        View view = getLayoutInflater().inflate(R.layout.appwidget, null, false);
        Bitmap logoBitmap = AppWidgetProvider.drawLogo(this);
        ImageView imgLogo = (ImageView) view.findViewById(R.id.imgLogo);
        imgLogo.setImageBitmap(logoBitmap);
        final Bitmap viewBitmap = ViewUtil.renderViewToBitmap(view, SHARE_IMAGE_WIDTH_PX, SHARE_IMAGE_HEIGHT_PX);

        new AsyncTask<Void, Void, Uri>() {
            @Override
            protected Uri doInBackground(Void... params) {
                try {
                    return saveAndInsertImage(viewBitmap);
                } catch (Exception e) {
                    Log.w("Could not save image", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Uri uri) {
                if (uri == null) {
                    Toast.makeText(SettingsActivity.this, R.string.settings_share_problemToast, Toast.LENGTH_LONG).show();
                } else {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/png");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.settings_share_subject));
                    shareIntent.putExtra("sms_body", getString(R.string.settings_share_subject));
                    int releaseDateZone = SettingsUtil.getReleaseDateZone(SettingsActivity.this);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, DateTimeUtil.getCountDownToEpisodeVIIAsText(SettingsActivity.this, releaseDateZone));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.settings_share_chooser)));
                }
            }

        }.execute();
    }

    @WorkerThread
    private Uri saveAndInsertImage(Bitmap image) throws Exception {
        File picturesPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File path = new File(picturesPath, SHARE_DIRECTORY_NAME);
        path.mkdirs();
        String fileName = SHARE_FILE_NAME;
        File file = new File(path, fileName);
        FileOutputStream outputStream = new FileOutputStream(file);
        image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        IoUtil.closeSilently(outputStream);
        return Uri.fromFile(file);
    }
}
