package com.ticketmaster.fingerprintdemo;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat.CryptoObject;

public class MainActivityModel {

    public enum ScanState {
        UNSUPPORTED,
        WAITING,
        SCANNING,
        PRINT_FOUND,
        PRINT_NOT_FOUND,
        SCAN_ERROR,
        NEED_HELP,
        CRYPTO_FAILED
    }

    private FingerprintHelper fingerprintHelper;
    private CryptoHelper cryptoHelper;
    private ScanState scanState;

    public MainActivityModel(FingerprintHelper fingerprintHelper, CryptoHelper cryptoHelper) {
        this.fingerprintHelper = fingerprintHelper;
        this.cryptoHelper = cryptoHelper;
    }

    public boolean setState(ScanState state) {
        if (scanState == state) return false;

        scanState = state;
        return true;
    }

    public ScanState getState() {
        return scanState;
    }

    public boolean hasFingerprintPermission() {
        return fingerprintHelper.hasFingerprintPermission();
    }

    public void requestFingerprintPermission() {
        fingerprintHelper.requestFingerprintPermission();
    }

    public boolean canReadFingerprints() {
        return fingerprintHelper.canReadFingerprints();
    }

    public void startScan() {
        fingerprintHelper.setCryptoObject(new CryptoObject(cryptoHelper.getCipher()));
        fingerprintHelper.startScan();
    }

    public void stopScan() {
        fingerprintHelper.stopScan();
    }

    public boolean generateCryptoKey() {
        return cryptoHelper.createKey();
    }

    public boolean initCipher() {
        return cryptoHelper.initCipher();
    }

    public boolean encryptStuff() {
        return cryptoHelper.encryptStuff();
    }

}
