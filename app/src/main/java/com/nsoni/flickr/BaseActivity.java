package com.nsoni.flickr;

import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

abstract class BaseActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    protected Snackbar mNetworkStatusSnackbar;

    @Override
    protected void onResume() {
        super.onResume();
        setupSnackbar();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            mNetworkStatusSnackbar.show();
        } else if (mNetworkStatusSnackbar.isShown()) {
            mNetworkStatusSnackbar.dismiss();
        }
    }

    @NonNull
    protected void setupSnackbar() {
        BaseActivity.this.registerReceiver(new ConnectivityReceiver(this), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        mNetworkStatusSnackbar = Snackbar.make(findViewById(R.id.coordinator), "Network unavailable, showing offline data...", Snackbar.LENGTH_INDEFINITE);
        mNetworkStatusSnackbar.setActionTextColor(getResources().getColor(android.R.color.white));
        mNetworkStatusSnackbar.getView().setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
    }
}
