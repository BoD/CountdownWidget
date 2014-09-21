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
package org.jraf.android.countdownwidget.wearable.app.notif;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import org.jraf.android.countdownwidget.R;
import org.jraf.android.countdownwidget.common.util.StringUtil;
import org.jraf.android.countdownwidget.common.wear.CommConstants;
import org.jraf.android.util.log.wrapper.Log;

public class NotificationService extends WearableListenerService {
    private static final int NOTIFICATION_ID = 0;

    private int mDays;

    public NotificationService() {}

    @Override
    public void onPeerConnected(Node peer) {}

    @Override
    public void onPeerDisconnected(Node peer) {}

    @Override
    public void onMessageReceived(MessageEvent messageEvent) { }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("count=" + dataEvents.getCount());

        for (DataEvent dataEvent : dataEvents) {
            DataItem dataItem = dataEvent.getDataItem();
            Uri uri = dataItem.getUri();
            Log.d("uri=" + uri);
            String path = uri.getPath();
            Log.d("path=" + path);
            DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
            DataMap dataMap = dataMapItem.getDataMap();
            mDays = dataMap.getInt(CommConstants.EXTRA_DAYS);
            showNotification();
        }
    }

    private void showNotification() {
        Log.d();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = createNotification();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private Notification createNotification() {
        Notification.Builder mainNotifBuilder = new Notification.Builder(this);

        // A small icon is mandatory even if it will be hidden - without this the system refuses to show the notification...
        mainNotifBuilder.setSmallIcon(R.drawable.ic_launcher);

        // Title
        mainNotifBuilder.setContentTitle(getString(R.string.notification_title));

        // Text
        String text = StringUtil.getFormattedCountdown(this, mDays);
        mainNotifBuilder.setContentText(text);

        // Low priority (let's face it)
        mainNotifBuilder.setPriority(0);

        // Wear specifics
        Notification.WearableExtender wearableExtender = new Notification.WearableExtender();
        wearableExtender.setHintHideIcon(true);
        wearableExtender.setBackground(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notif_logo));

        Notification.Builder wearableNotifBuilder = wearableExtender.extend(mainNotifBuilder);
        Notification res = wearableNotifBuilder.build();
        return res;
    }
}
