package com.openkeysdk.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.openkeysdk.R;
import com.openkeysdk.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class Fragmentactivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getSupportFragmentManager().beginTransaction().add(R.id.action_container, new KeyActiveFragment(), "").commitAllowingStateLoss();
        //  checkPermissions();
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
