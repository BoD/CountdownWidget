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
package org.jraf.android.countdownwidget.common.wear;

import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.WorkerThread;

import org.jraf.android.util.log.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Helper singleton class to deal with the wear APIs.<br/>
 * Note: {@link #connect(Context)} must be called prior to calling all the other methods.<br/>
 */
public class WearHelper {
    private static final WearHelper INSTANCE = new WearHelper();

    private static final long AWAIT_TIME_S = 5;

    public static final String PATH_DAYS = "/days";

    /**
     * Number of days ({@code int}).
     */
    public static final String EXTRA_DAYS = "EXTRA_DAYS";

    private GoogleApiClient mGoogleApiClient;
    private int mUsers;

    private WearHelper() {}

    public static WearHelper get() {
        return INSTANCE;
    }

    @WorkerThread
    public synchronized void connect(Context context) {
        Log.d();
        mUsers++;
        if (mGoogleApiClient != null) {
            Log.d("Already connected, mUsers=%s", mUsers);
            return;
        }

        mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(Wearable.API).build();
        // Blocking
        ConnectionResult connectionResult = mGoogleApiClient.blockingConnect();
        if (!connectionResult.isSuccess()) {
            Log.d("Could not connect, errorCode=%s errorMessage='%s' mUsers=%s", connectionResult.getErrorCode(), connectionResult.getErrorMessage(), mUsers);
            // TODO handle failures
        } else {
            Log.d("Now connected, mUsers=%s", mUsers);
        }
    }

    public synchronized void disconnect() {
        mUsers--;
        Log.d("mUsers=%s", mUsers);
        if (mUsers == 0) {
            if (mGoogleApiClient != null) mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
    }


    /*
     * Days.
     */
    // region

    @WorkerThread
    public void updateDays(int days) {
        Log.d("days=%s", days);
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(PATH_DAYS);

        DataMap dataMap = putDataMapRequest.getDataMap();
        dataMap.putInt(EXTRA_DAYS, days);

        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, request).await(AWAIT_TIME_S, TimeUnit.SECONDS);
    }

    @WorkerThread
    public void removeDays() {
        Log.d();
        Wearable.DataApi.deleteDataItems(mGoogleApiClient, createUri(PATH_DAYS)).await(AWAIT_TIME_S, TimeUnit.SECONDS);
    }

    // endregion


    /*
     * Misc.
     */
    // region

    private static Uri createUri(String path) {
        return new Uri.Builder().scheme(PutDataRequest.WEAR_URI_SCHEME).path(path).build();
    }

    // endregion
}