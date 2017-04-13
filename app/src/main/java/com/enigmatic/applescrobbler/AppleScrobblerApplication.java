package com.enigmatic.applescrobbler;

import android.app.Application;

import com.last.fm.api.Lfm;

public class AppleScrobblerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Lfm.initializeWithSecret(this);
    }
}
