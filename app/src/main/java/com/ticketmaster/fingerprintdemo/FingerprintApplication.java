package com.ticketmaster.fingerprintdemo;

import android.app.Application;

public class FingerprintApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CryptoHelper.clearKey();
    }
}
