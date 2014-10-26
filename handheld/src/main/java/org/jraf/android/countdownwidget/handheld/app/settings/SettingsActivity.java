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

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.jraf.android.countdownwidget.BuildConfig;
import org.jraf.android.countdownwidget.R;
import org.jraf.android.countdownwidget.handheld.Constants;
import org.jraf.android.countdownwidget.handheld.app.androidwear.AndroidWearService;
import org.jraf.android.countdownwidget.handheld.util.ScheduleUtil;
import org.jraf.android.util.about.AboutActivityIntentBuilder;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        addPreferencesFromResource(R.xml.settings);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);

        Preference tutorialPreference = findPreference(Constants.PREF_TUTORIAL);
        tutorialPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                onTutorialClicked();
                return true;
            }
        });

        Preference aboutPreference = findPreference(Constants.PREF_ABOUT);
        aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                onAboutClicked();
                return true;
            }
        });

        // We need this because this Activity is used as the configure Activity for the AppWidget.
        if (AppWidgetManager.ACTION_APPWIDGET_CONFIGURE.equals(getIntent().getAction())) {
            int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
            Bundle extras = getIntent().getExtras();
            if (extras != null) appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Intent resultValue = new Intent();
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);

            // In that case we don't show the tutorial (because obviously the user knows how to add a widget)
            getPreferenceScreen().removePreference(tutorialPreference);

            // But we DO need to warn the user they MUST press back, otherwise the widget won't be created
            Toast.makeText(this, R.string.preference_toast, Toast.LENGTH_LONG).show();
        }
//        DateTimeUtil.listAllDates();

        // Don't show Android Wear stuff for old devices that don't support it
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getPreferenceScreen().removePreference(findPreference(Constants.PREF_ANDROID_WEAR));
        }
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
            setResult(RESULT_OK);
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
        }
    };

    private void onAboutClicked() {
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
        startActivity(builder.build(this));
    }

    private void onTutorialClicked() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://jraf.org/episodeVII/tutorial.html")));
    }
}
