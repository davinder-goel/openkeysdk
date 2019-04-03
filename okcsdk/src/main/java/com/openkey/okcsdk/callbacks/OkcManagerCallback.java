package com.openkey.okcsdk.callbacks;

import com.openkey.okcsdk.model.FetchKeyResponse;

public interface OkcManagerCallback {
    void scanResult(String msg);

    void initilizationSuccess();

    void initilizationFailure();

    void openDoorSuccess(String msg);

    void openDoorFailure(String msg);

    void fetchKeySuccess(FetchKeyResponse response);

    void fetchKeyFailure(String msg);
}
