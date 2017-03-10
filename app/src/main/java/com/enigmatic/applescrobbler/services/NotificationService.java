package com.enigmatic.applescrobbler.services;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class NotificationService extends NotificationListenerService {


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("NotificationStuff", "Notification listener created");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        RemoteViews views = statusBarNotification.getNotification().bigContentView;
        if (views == null) views = statusBarNotification.getNotification().contentView;
        if (views == null) return;

        List<String> text = new ArrayList<>();
        try {
            Field field = views.getClass().getDeclaredField("mActions");
            field.setAccessible(true);

            ArrayList<Parcelable> actions = (ArrayList<Parcelable>) field.get(views);

            for (Parcelable p : actions) {
                Parcel parcel = Parcel.obtain();
                p.writeToParcel(parcel, 0);
                parcel.setDataPosition(0);

                int tag = parcel.readInt();
                if (tag != 2) continue;

                parcel.readInt();

                String methodName = parcel.readString();
                if (methodName != null && methodName.equals("setText")) {
                    parcel.readInt();

                    String t = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim();
                    text.add(t);
                }
            }
        } catch (Exception e) {
            Log.e("NotificationStuff", e.getMessage());
            e.printStackTrace();
        }

        for (String t : text) {
            Log.i("NotificationStuff", t);
        }
    }
}
