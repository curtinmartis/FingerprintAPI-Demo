package com.ticketmaster.fingerprintdemo;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ticketmaster.fingerprintdemo.MainActivityModel.ScanState;
import com.ticketmaster.servos.util.otto.BusProvider;

import java.lang.ref.WeakReference;

public class MainActivityView {

    private WeakReference<Activity> activityRef;

    private TextView statusText;
    private Button scanButton;
    private Button encryptButton;

    public MainActivityView(Activity activity) {
        activityRef = new WeakReference<>(activity);
        setupWidgets(activity);
    }

    private void setupWidgets(Activity activity) {
        statusText = (TextView) activity.findViewById(R.id.txt_status);
        scanButton = (Button) activity.findViewById(R.id.btn_scan);
        scanButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.post(new ScanButtonTappedEvent());
            }
        });
        encryptButton = (Button) activity.findViewById(R.id.btn_encrypt);
        encryptButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.post(new EncryptButtonTappedEvent());
            }
        });
    }

    public void updateScanButton(ScanState state) {
        switch (state) {
            case UNSUPPORTED:
            case CRYPTO_FAILED:
                scanButton.setEnabled(false);
                break;
            case SCANNING:
            case PRINT_NOT_FOUND:
            case NEED_HELP:
                scanButton.setEnabled(true);
                scanButton.setText(R.string.btn_stop);
                break;
            case WAITING:
            case PRINT_FOUND:
            case SCAN_ERROR:
            default:
                scanButton.setEnabled(true);
                scanButton.setText(R.string.btn_scan);
                break;
        }
    }

    public void updateStateText(ScanState state, CharSequence message) {
        switch (state) {
            case UNSUPPORTED:
                statusText.setText(R.string.txt_state_unsupported);
                break;
            case WAITING:
                statusText.setText(R.string.txt_state_waiting);
                break;
            case SCANNING:
                statusText.setText(R.string.txt_state_scanning);
                break;
            case PRINT_FOUND:
                statusText.setText(R.string.txt_state_found_print);
                break;
            case PRINT_NOT_FOUND:
                statusText.setText(R.string.txt_state_no_print);
                break;
            case SCAN_ERROR:
            case NEED_HELP:
                statusText.setText(message);
                break;
        }
    }

    private boolean hasActivity() {
        return activityRef != null && activityRef.get() != null;
    }

    public void showCryptoGeneratedSnackbar() {
        if (!hasActivity()) return;

        Snackbar.make(scanButton, R.string.toast_crypto_generated, Snackbar.LENGTH_SHORT).show();
    }

    public void showKeyNotGeneratedSnackbar() {
        if (!hasActivity()) return;

        Snackbar.make(scanButton, R.string.toast_key_not_generated, Snackbar.LENGTH_SHORT).show();
    }

    public void showCipherNotInitializedSnackbar() {
        if (!hasActivity()) return;

        Snackbar.make(scanButton, R.string.toast_cipher_not_initialized, Snackbar.LENGTH_SHORT).show();
    }

    public void showCryptoUnlockedSnackbar() {
        if (!hasActivity()) return;

        Snackbar.make(scanButton, R.string.toast_crypto_unlocked, Snackbar.LENGTH_SHORT).show();
    }

    public void showCryptoLockedSnackbar() {
        if (!hasActivity()) return;

        Snackbar.make(scanButton, R.string.toast_crypto_locked, Snackbar.LENGTH_SHORT).show();
    }

    public void showEncryptionSuccessfulSnackbar() {
        if (!hasActivity()) return;

        Snackbar.make(scanButton, R.string.toast_encryption_successful, Snackbar.LENGTH_SHORT).show();
    }

    public static class ScanButtonTappedEvent { }
    public static class EncryptButtonTappedEvent { }

}
