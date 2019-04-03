package com.openkey.okcsdk;

import android.annotation.SuppressLint;
import android.app.Application;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanner;
import com.openkey.okcsdk.callbacks.OkcManagerCallback;
import com.openkey.okcsdk.config.OpenKeyConfig;
import com.openkey.okcsdk.helper.ToHexUtil;
import com.openkey.okcsdk.model.FetchKeyResponse;
import com.openkey.okcsdk.model.Status;
import com.openkey.okcsdk.service.Services;

import org.apache.commons.codec.binary.Base64;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OKCManager {
    @SuppressLint("StaticFieldLeak")
    private static volatile OKCManager instance;
    private Application mApplication;
    private OkcManagerCallback mOkcManagerCallback;

    /**
     * Access to this class can only be provided by this class
     * so object creation is limited to this only.
     *
     * @param application
     */
    public OKCManager(Application application) {
        mApplication = application;
    }

    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * This will return the instance of this class and provide a
     * context to this class.
     *
     * @param context {@link Context } of the host application
     * @return Instance of the {@link OKCManager} class
     */
    public static synchronized OKCManager getInstance(Application context)
            throws NullPointerException {
        if (instance == null) {
            if (context == null) {
                throw new NullPointerException("Null");
            }
            instance = new OKCManager(context);
        }
        return instance;
    }

    //register callback here for giving respective response to respective class
    public void registerOkcCallBack(@NonNull final OkcManagerCallback okcManagerCallback) {
        mOkcManagerCallback = okcManagerCallback;

    }

    //initilization
    public void OkcInit() {
        Log.e("Init OKCBleManager", "called");
        BleManager.getInstance().init(mApplication);
        if (mOkcManagerCallback != null)
            mOkcManagerCallback.initilizationSuccess();
    }

    //fetch room list from server
    public void fetchKeys(String okcToken) {
        // Get the retrofit instance
        Utilities.getInstance().saveValue(Constants.OKC_TOKEN, okcToken, mApplication);
        Services services = Utilities.getInstance().getRetrofit(mApplication).create(Services.class);
        services.fetchKeys("Token " + okcToken).enqueue(new Callback<FetchKeyResponse>() {
            @Override
            public void onResponse(Call<FetchKeyResponse> call, Response<FetchKeyResponse>
                    response) {
                if (response.isSuccessful()) {
                    if (mOkcManagerCallback != null)
                        mOkcManagerCallback.fetchKeySuccess(response.body());
                    Utilities.getInstance().saveValue(Constants.OKC_MAC, response.body().getData().getPropertyLocks().get(0).getMac(), mApplication);
                    Utilities.getInstance().saveValue(Constants.ROOM_ID, response.body().getData().getPropertyLocks().get(0).getId(), mApplication);
                } else {
                    if (mOkcManagerCallback != null)
                        mOkcManagerCallback.fetchKeyFailure(Utilities.getInstance().handleApiError(response.errorBody(), mApplication));
                    // get the error message from the response and return it to the callback
                }
            }

            @Override
            public void onFailure(Call<FetchKeyResponse> call, Throwable t) {
                if (mOkcManagerCallback != null)
                    mOkcManagerCallback.fetchKeyFailure("Internel Server Error or Timeout");
            }
        });
    }

    //get key for perticular lock from server
    public void sendKeyForOpenDoor(String okcToken, int roomId) {
        // Get the retrofit instance
        Services services = Utilities.getInstance().getRetrofit(mApplication).create(Services.class);
        services.sendOpenDoorData("Token " + okcToken, roomId).enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status>
                    response) {
                if (response.isSuccessful()) {
                    openOkcDoor(response.body().getData().getKey());
                } else {
                    if (mOkcManagerCallback != null)
                        mOkcManagerCallback.openDoorFailure(Utilities.getInstance().handleApiError(response.errorBody(), mApplication));
                    // get the error message from the response and return it to the callback
                }
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {
                if (mOkcManagerCallback != null)
                    mOkcManagerCallback.openDoorFailure("Internal server error or Timeout.");
            }
        });
    }

    /**
     * starts door open process after got the key
     *
     * @param trueKey
     */
    public void openOkcDoor(String trueKey) {
        byte[] byteArray = Base64.decodeBase64(trueKey.getBytes());
        String uuidService = Constants.UUID_SERVICE;
        String uuidChararacteristic = Constants.UUID_CHARACTERSTICK;
        BleDevice configDevice = (BleDevice) OpenKeyConfig.getIns().getDevice();

        BleManager.getInstance().write(
                configDevice,
                uuidService,
                uuidChararacteristic,
                byteArray,
                true,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {

                        Observable.just(0).delay(Constants.NOTIFY_MSG_HANDLE_TIME, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(o -> {
                                    BleManager.getInstance().disconnectAllDevice();
                                });

                        if (mOkcManagerCallback != null && total - current == 0)
                            mOkcManagerCallback.openDoorSuccess("Door Opened");
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        if (mOkcManagerCallback != null)
                            mOkcManagerCallback.openDoorFailure(exception.getDescription());
                        BleManager.getInstance().disconnectAllDevice();
                    }
                });
    }

    //starts scanning
    public void scanMyDevice(String okcMac) {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                if (mOkcManagerCallback != null)
                    mOkcManagerCallback.scanResult("Scanning started");
            }

            @Override
            public void onScanning(BleDevice result) {

                String scanRecord = ToHexUtil.byte2hex(result.getScanRecord());
                if (scanRecord == null) {
                    return;
                }
                if (scanRecord.contains(okcMac)) {

                    Observable.just(result)
                            .delay(100, TimeUnit.MILLISECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(device -> {
                                OpenKeyConfig.getIns().setDevice(device);
                                Log.e("Get Device", ":" + device);
                                connectMyDevice((BleDevice) OpenKeyConfig.getIns().getDevice());
                            });

                    BleScanner.getInstance().stopLeScan();
                }
                if (mOkcManagerCallback != null)
                    mOkcManagerCallback.scanResult("Scanning in progress");
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                Log.e("Scan Result", "onScanFinished");
                if (mOkcManagerCallback != null)
                    mOkcManagerCallback.scanResult("Scanning finished");
            }
        });
    }

    //connect particular scanned device for open
    public void connectMyDevice(BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {

            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Constants.IS_BLE_READY = true;
                String okcToken = Utilities.getInstance().getValue(Constants.OKC_TOKEN, "", mApplication);
                int roomId = Utilities.getInstance().getValue(Constants.ROOM_ID, 0, mApplication);
                Log.e("okcToken connect", okcToken + "");

                sendKeyForOpenDoor(okcToken, roomId);
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {

            }
        });
    }
}
