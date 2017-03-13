package com.enigmatic.applescrobbler.services;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.RemoteViews;

import com.enigmatic.applescrobbler.lastfm.NotificationScrobbleHandler;
import com.enigmatic.applescrobbler.lastfm.TrackData;

import java.util.Date;

import jp.yokomark.remoteview.reader.RemoteViewsInfo;
import jp.yokomark.remoteview.reader.RemoteViewsReader;
import jp.yokomark.remoteview.reader.action.ReflectionAction;
import jp.yokomark.remoteview.reader.action.RemoteViewsAction;


public class NotificationService extends NotificationListenerService {

    private NotificationScrobbleHandler handler = new NotificationScrobbleHandler();
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.i("NotificationStuff", "Notification listener created");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        super.onNotificationPosted(statusBarNotification);
        RemoteViews remoteViews = statusBarNotification.getNotification().bigContentView;
        RemoteViewsInfo info = RemoteViewsReader.read(this, remoteViews);

        TrackData data = new TrackData();
        int dataCount = 0;
        for (RemoteViewsAction action : info.getActions()) {
//            if (action instanceof BitmapReflectionAction) {
//                Bitmap bitmap = ((BitmapReflectionAction)action).getBitmap();
//                Intent intent = new Intent("Msg");
//                intent.putExtra("bitmap", bitmap);
//                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
//            }
            if (action instanceof ReflectionAction) {
                ReflectionAction reflectionAction = (ReflectionAction) action;
                String methodName = reflectionAction.getMethodName();

                if (methodName.equals("setText")) {
                    String t = (String)reflectionAction.getValue();
                    t = t.trim();
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
                } else if (methodName.equals("setContentDescription")) {
                    String t = (String)reflectionAction.getValue();
                    t = t.trim();
                    data.setContentType(t);
                    Log.i("NotificationDetails", "Content Description: " + t);
                }
            }
        }
        data.setStartTime(new Date(System.currentTimeMillis()));
        handler.push(data);
    }
}
