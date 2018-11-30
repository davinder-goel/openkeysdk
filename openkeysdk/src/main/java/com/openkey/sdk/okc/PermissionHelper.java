package com.openkey.sdk.okc;

import android.support.v4.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class PermissionHelper {


    private RxPermissions mRxPermissions;

    private PermissionCallBack callBack;

    public void requestPermission(FragmentActivity activity, PermissionCallBack callBack, String... perms) {
        this.callBack = callBack;
        if (perms == null || activity == null) {
            return;
        }

        if (mRxPermissions == null) {
            mRxPermissions = new RxPermissions(activity);
        }

        mRxPermissions.request(perms)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(granted -> {
                    if (granted) {
                        if (callBack != null) {
                            callBack.permissionGranted();
                        }
                    } else {
                        if (callBack != null) {
                            callBack.permissionRefused();
                        }
                    }
                });
    }

    public interface PermissionCallBack {
        void permissionGranted();

        void permissionRefused();
    }
}
