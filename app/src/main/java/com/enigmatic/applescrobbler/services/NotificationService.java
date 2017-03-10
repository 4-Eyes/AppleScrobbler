package com.enigmatic.applescrobbler.services;

import android.os.Parcel;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.enigmatic.applescrobbler.lastfm.NotificationScrobbleHandler;
import com.enigmatic.applescrobbler.lastfm.TrackData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;


public class NotificationService extends NotificationListenerService {

    private NotificationScrobbleHandler handler = new NotificationScrobbleHandler();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("NotificationStuff", "Notification listener created");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        // Filter it so that we only extract information from apple music notifications
        if (!statusBarNotification.getPackageName().equals("com.apple.android.music")) return;

        RemoteViews views = statusBarNotification.getNotification().bigContentView;
        if (views == null) views = statusBarNotification.getNotification().contentView;
        if (views == null) return;

        TrackData data = new TrackData();
        int dataCount = 0;
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
                if (methodName == null) {
                    continue;
                }
                else if (methodName.equals("setText")) {
                    parcel.readInt();

                    String t = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim();
                    switch (dataCount) {
                        case 0:
                            data.setTitle(t);
                            break;
                        case 1:
                            data.setAlbum(t);
                            break;
                        case 2:
                            data.setArtist(t);
                            break;
                    }
                    dataCount++;
                }
                else if (methodName.equals("setTime")) {
                    parcel.readInt();

                    Date time = new Date(parcel.readLong());

                }
            }
        } catch (Exception e) {
            Log.e("NotificationStuff", e.getMessage());
            e.printStackTrace();
        }

        handler.push(data);

    }
}
