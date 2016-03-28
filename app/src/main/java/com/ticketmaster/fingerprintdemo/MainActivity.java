package com.ticketmaster.fingerprintdemo;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ticketmaster.servos.util.otto.BusProvider;

public class MainActivity extends AppCompatActivity {

    private MainActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainActivityPresenter(
                new MainActivityModel(new FingerprintHelper(this), new CryptoHelper()),
                new MainActivityView(this)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.register(presenter);
        presenter.refreshUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.unregister(presenter);
        presenter.cleanup();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] state) {
        if (requestCode == FingerprintHelper.REQUEST_FINGERPRINT_PERMISSION) {
            if (state[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.permissionGranted();
            } else {
                presenter.permissionDenied();
            }
        }
    }

}
