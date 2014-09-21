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

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.jraf.android.util.log.wrapper.Log;

/**
 * Helper singleton class to communicate with wearables.<br/>
 * Note: {@link #connect(android.content.Context)} must be called prior to calling all the other methods.<br/>
 * Note: a connection to a {@link com.google.android.gms.common.api.GoogleApiClient} is maintained by this class, which may or may not be a performance problem.
 */
public class WearCommHelper {
    private static final WearCommHelper INSTANCE = new WearCommHelper();

    private GoogleApiClient mGoogleApiClient;

    private WearCommHelper() {}

    public static WearCommHelper get() {
        return INSTANCE;
    }

    public void connect(Context context) {
        Log.d();
        if (mGoogleApiClient != null) return;
        mGoogleApiClient = new GoogleApiClient.Builder(context).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle connectionHint) {
                Log.d("connectionHint=" + connectionHint);
            }

            @Override
            public void onConnectionSuspended(int cause) {
                Log.d("cause=" + cause);
                // TODO reconnect
            }
        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult result) {
                Log.w("result=" + result);
                // TODO handle failures
            }
        }).addApi(Wearable.API).build();
        mGoogleApiClient.connect();
    }

    public void disconnect() {
        Log.d();
        if (mGoogleApiClient != null) mGoogleApiClient.disconnect();
        mGoogleApiClient = null;
    }


    /*
     * Days.
     */

    public void updateDays(int days) {
        Log.d("days=" + days );
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(CommConstants.PATH_DAYS);

        DataMap dataMap = putDataMapRequest.getDataMap();
        dataMap.putInt(CommConstants.EXTRA_DAYS, days);

        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, request);
    }
}