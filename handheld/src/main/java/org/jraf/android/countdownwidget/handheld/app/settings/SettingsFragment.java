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

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;

import org.jraf.android.countdownwidget.R;
import org.jraf.android.countdownwidget.handheld.Constants;

public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsActivity getCallbacks() {
        return (SettingsActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Preference tutorialPreference = findPreference(Constants.PREF_TUTORIAL);
        tutorialPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getCallbacks().onTutorialClicked();
                return true;
            }
        });

        Preference aboutPreference = findPreference(Constants.PREF_ABOUT);
        aboutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getCallbacks().onAboutClicked();
                return true;
            }
        });

        Preference sharePreference = findPreference(Constants.PREF_SHARE);
        sharePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getCallbacks().onShareClicked();
                return true;
            }
        });


        // We need this because this Activity is used as the configure Activity for the AppWidget.
        if (AppWidgetManager.ACTION_APPWIDGET_CONFIGURE.equals(getActivity().getIntent().getAction())) {
            int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null) appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            Intent resultValue = new Intent();
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            getActivity().setResult(Activity.RESULT_OK, resultValue);

            // In that case we don't show the tutorial (because obviously the user knows how to add a widget)
            getPreferenceScreen().removePreference(tutorialPreference);

            // But we DO need to warn the user they MUST press back, otherwise the widget won't be created
            Toast.makeText(getActivity(), R.string.preference_toast, Toast.LENGTH_LONG).show();
        }

        // Don't show Android Wear stuff for old devices that don't support it
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getPreferenceScreen().removePreference(findPreference(Constants.PREF_ANDROID_WEAR));
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {}
}
