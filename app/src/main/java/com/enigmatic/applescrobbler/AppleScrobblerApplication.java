package com.enigmatic.applescrobbler;

import android.app.Application;

import com.ag.lfm.Lfm;

public class AppleScrobblerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Lfm.initializeWithSecret(this);
    }
}
