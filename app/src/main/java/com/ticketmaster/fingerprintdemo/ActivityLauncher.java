package com.ticketmaster.fingerprintdemo;

import android.app.Activity;
import android.content.Intent;

public class ActivityLauncher {

    private Activity activity;

    public ActivityLauncher(Activity activity) {
        this.activity = activity;
    }

    public void launchActivityForResult(Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
    }

}
