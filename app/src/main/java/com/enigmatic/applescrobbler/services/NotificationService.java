package com.enigmatic.applescrobbler.services;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.LocalBroadcastManager;
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
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.i("NotificationStuff", "Notification listener created");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        // Filter it so that we only extract information from apple music notifications
        if (!statusBarNotification.getPackageName().equals("com.apple.android.music")) return;

        RemoteViews views = statusBarNotification.getNotification().bigContentView;
//        if (views == null) views = statusBarNotification.getNotification().contentView;
        if (views == null) return;

        Log.i("NotificationStuff", "New notification being processed");
        TrackData data = new TrackData();
        int dataCount = 0;
        try {
            Field field = views.getClass().getDeclaredField("mActions");
            field.setAccessible(true);

            ArrayList<Parcelable> actions = (ArrayList<Parcelable>) field.get(views);

//            Log.i("NotificationStuff", "Number of actions: " + actions.size());

            for (Parcelable p : actions) {
                Parcel parcel = Parcel.obtain();
                p.writeToParcel(parcel, 0);
                parcel.setDataPosition(0);

                int tag = parcel.readInt();
                if (tag != 2 &&  tag != 12) continue; // 2 is ReflectionAction, 12 is BitmapReflectionAction

                parcel.readInt();

                String methodName = parcel.readString();
//                Log.i("NotificationStuff", "Method name: " + methodName);
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
                else if (methodName.equals("setContentDescription")) {
                    parcel.readInt();

                    String t = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim();
                    Log.i("NotificationStuff", "Content Description: " + t);
                    data.setConetentType(t);
                }
                else if (methodName.equals("setTime")) {
                    parcel.readInt();

                    Date time = new Date(parcel.readLong());

//                    Log.i("NotificationStuff", "Time Stamp: " + time.toString());
                    data.setScrobbleTime(time);
                }
                else if (methodName.equals("setImageResource")) {
                    parcel.readInt();

                    int imageResource = parcel.readInt();
//                    Log.i("NotificationStuff", "Image Resource: " + imageResource);
                }
                else if (methodName.equals("setEnabled")) {
                    parcel.readInt();

                    boolean enabled = parcel.readByte() != 0;
//                    Log.i("NotificationStuff", "Enabled: " + enabled);
                }
                else if (methodName.equals("setImageBitmap")){
//                    parcel.readInt();
//
//
//                    Bitmap bitmap = Bitmap.CREATOR.createFromParcel(parcel);
//                    Intent intent = new Intent("Msg");
//                    intent.putExtra("bitmap", bitmap);
//                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        } catch (Exception e) {
            Log.e("NotificationStuff", e.getMessage());
            e.printStackTrace();
        }

        handler.push(data);

    }
}
