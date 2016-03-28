package com.ticketmaster.fingerprintdemo;

import android.Manifest;
import android.Manifest.permission;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.v4.content.ContextCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat.AuthenticationCallback;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat.AuthenticationResult;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat.CryptoObject;
import android.support.v4.os.CancellationSignal;

import com.ticketmaster.fingerprintdemo.MainActivityModel.ScanState;
import com.ticketmaster.servos.util.otto.BusProvider;

import java.lang.ref.WeakReference;

public class FingerprintHelper extends AuthenticationCallback {

    public static final int REQUEST_FINGERPRINT_PERMISSION = 0;

    private WeakReference<Activity> activityRef;

    private FingerprintManagerCompat fingerprintManager;
    private CancellationSignal cancellationSignal;
    private CryptoObject cryptoObject;

    public FingerprintHelper(Activity activity) {
        activityRef = new WeakReference<>(activity);
        fingerprintManager = FingerprintManagerCompat.from(activity);
    }

    /* AUTHENTICATIONCALLBACK */

    @Override
    public void onAuthenticationFailed() {
        BusProvider.post(new AuthenticationCallbackEvent(ScanState.PRINT_NOT_FOUND));
    }

    @Override
    public void onAuthenticationSucceeded(AuthenticationResult result) {
        BusProvider.post(new AuthenticationCallbackEvent(ScanState.PRINT_FOUND));
    }

    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        BusProvider.post(new AuthenticationCallbackEvent(ScanState.NEED_HELP, helpString));
    }

    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {
        BusProvider.post(new AuthenticationCallbackEvent(ScanState.SCAN_ERROR, errString));
    }

    /* PERMISSIONS */

    public boolean hasFingerprintPermission() {
        return hasActivity() && (VERSION.SDK_INT < VERSION_CODES.M
                || ContextCompat.checkSelfPermission(getActivity(), permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED);

    }

    @TargetApi(VERSION_CODES.M)
    public void requestFingerprintPermission() {
        if (!hasActivity()) return;

        getActivity().requestPermissions(new String[]{Manifest.permission.USE_FINGERPRINT}, REQUEST_FINGERPRINT_PERMISSION);
    }

    public boolean canReadFingerprints() {
        return fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints();
    }

    /* SCANNING OPERATION */

    public void setCryptoObject(CryptoObject cryptoObject) {
        this.cryptoObject = cryptoObject;
    }

    public void startScan() {
        cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, 0, cancellationSignal, this, null);
    }

    public void stopScan() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            cancellationSignal = null;
        }
    }

    /* ACTIVITY HELPERS */

    private boolean hasActivity() {
        return activityRef != null && activityRef.get() != null;
    }

    private Activity getActivity() {
        return activityRef.get();
    }

    public class AuthenticationCallbackEvent {

        private ScanState newState;
        private CharSequence message;

        public AuthenticationCallbackEvent(ScanState newState) {
            this(newState, null);
        }

        public AuthenticationCallbackEvent(ScanState newState, CharSequence message) {
            this.newState = newState;
            this.message = message;
        }

        public ScanState getNewState() {
            return newState;
        }

        public CharSequence getMessage() {
            return message;
        }
    }

}
