package com.openkeysdk;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.openkey.sdk.OpenKeyManager;

import java.util.ArrayList;
import java.util.List;

public class Fragmentactivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        OpenKeyManager.getInstance().init(getApplication(), "45144534-f181-4011-b142-5d53162a95c8");
        checkPermissions();
        requestPermission();
        getSupportFragmentManager().beginTransaction().add(R.id.action_container, new KeyActiveFragment(), "").commitAllowingStateLoss();
    }


    private void requestPermission() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Activity activity = Fragmentactivity.this;
                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH)) {
                    } else {
                        ActivityCompat.requestPermissions(activity, new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.BLUETOOTH,
                                        Manifest.permission.BLUETOOTH_ADMIN,
                                },
                                10);
                    }
                }
            }
        }, 500);
    }


    private void checkPermissions() {
        String[] requiredPermissions = new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        List<String> missingPermissions = new ArrayList<>();

        boolean allPermissionsOk = true;
        for (String p : requiredPermissions) {
            if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                Log.d("dfsdfdd", "missing Permission: " + p);
                allPermissionsOk = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, p)) {
                    Log.d("fsd", "User already denied permission once, he probably needs an explanation: " + p);
                }
                missingPermissions.add(p);
            }
        }

        if (!allPermissionsOk) {
            // request all missing permissions
            ActivityCompat.requestPermissions(this, missingPermissions.toArray(new String[0]),
                    /* unused because no callback*/ 0);
        }
    }
}