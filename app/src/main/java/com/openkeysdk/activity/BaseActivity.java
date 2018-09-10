package com.openkeysdk.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.openkey.sdk.api.response.session.SessionResponse;
import com.openkey.sdk.interfaces.OpenKeyCallBack;

public abstract class BaseActivity extends AppCompatActivity implements OpenKeyCallBack {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public void session(SessionResponse sessionResponse) {

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
