package com.openkeysdk;

import android.support.v4.app.Fragment;

import com.openkey.sdk.api.response.session.SessionResponse;
import com.openkey.sdk.interfaces.OpenKeyCallBack;

public class BaseFragment extends Fragment implements OpenKeyCallBack {


    @Override
    public void session(SessionResponse sessionResponse) {

    }

    @Override
    public void initializationSuccess() {

    }

    @Override
    public void sessionFailure(String errorDescription, String errorCode) {

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
