package com.ticketmaster.fingerprintdemo;

import com.squareup.otto.Subscribe;
import com.ticketmaster.fingerprintdemo.FingerprintHelper.AuthenticationCallbackEvent;
import com.ticketmaster.fingerprintdemo.MainActivityModel.ScanState;
import com.ticketmaster.fingerprintdemo.MainActivityView.EncryptButtonTappedEvent;
import com.ticketmaster.fingerprintdemo.MainActivityView.ScanButtonTappedEvent;

public class MainActivityPresenter {

    private MainActivityModel model;
    private MainActivityView view;

    public MainActivityPresenter(MainActivityModel model, MainActivityView view) {
        this.model = model;
        this.view = view;

        setInitialState();
    }

    @Subscribe
    public void onScanButtonTapped(ScanButtonTappedEvent event) {
        ScanState state = model.getState();
        if (state == ScanState.WAITING || state == ScanState.SCAN_ERROR || state == ScanState.PRINT_FOUND) {
            onStartScanTapped();
        } else {
            onStopScanTapped();
        }
    }

    @Subscribe
    public void onEncryptButtonTapped(EncryptButtonTappedEvent event) {
        if (model.encryptStuff()) {
            view.showEncryptionSuccessfulSnackbar();
        } else {
            view.showCryptoLockedSnackbar();
        }
    }

    @Subscribe
    public void onAuthenticationCallbackEvent(AuthenticationCallbackEvent event) {
        setState(event.getNewState(), event.getMessage());
    }

    private void setInitialState() {
        ScanState initialState;
        if (model.canReadFingerprints()) {
            initialState = ScanState.WAITING;
            initCrypto();
        } else {
            initialState = ScanState.UNSUPPORTED;
        }
        setState(initialState);
    }

    public void refreshUI() {
        view.updateScanButton(model.getState());
        view.updateStateText(model.getState(), null);
    }

    public void cleanup() {
        if (model.getState() == ScanState.SCANNING) model.stopScan();
    }

    private void onStartScanTapped() {
        if (!model.hasFingerprintPermission()) {
            model.requestFingerprintPermission();
        } else {
            setState(ScanState.SCANNING);
            model.startScan();
        }
    }

    private void onStopScanTapped() {
        model.stopScan();
    }

    private void setState(ScanState state) {
        setState(state, null);
    }

    private void setState(ScanState state, CharSequence message) {
        boolean changed = model.setState(state);
        if (changed) {
            view.updateScanButton(state);
            view.updateStateText(state, message);

            if (state == ScanState.PRINT_FOUND) {
                view.showCryptoUnlockedSnackbar();
            }
        }
    }

    public void permissionGranted() {
        setInitialState();
    }

    public void permissionDenied() {
        setInitialState();
    }

    private void initCrypto() {
        if (!model.generateCryptoKey()) {
            setState(ScanState.CRYPTO_FAILED);
            view.showKeyNotGeneratedSnackbar();
        } else if(!model.initCipher()) {
            setState(ScanState.CRYPTO_FAILED);
            view.showCipherNotInitializedSnackbar();
        } else {
            view.showCryptoGeneratedSnackbar();
        }
    }

}
