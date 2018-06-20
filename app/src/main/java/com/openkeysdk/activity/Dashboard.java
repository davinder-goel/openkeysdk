package com.openkeysdk.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.openkey.sdk.interfaces.OpenKeyCallBack;
import com.openkeysdk.R;

public class Dashboard extends AppCompatActivity implements OpenKeyCallBack {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
    }

    @Override
    public void authenticated(boolean isAuthenticated, String description) {
    }

    @Override
    public void initializationSuccess() {
    }

    @Override
    public void initializationFailure(String errorDescription) {

    }

    @Override
    public void stopScan(boolean isLockOpened, String description) {

    }

    @Override
    public void isKeyAvailable(boolean haveKey, String description) {

    }
}
