package com.openkey.sdk.kaba;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.legic.mobile.sdk.api.LegicMobileSdkManager;
import com.legic.mobile.sdk.api.exception.LegicMobileSdkException;
import com.legic.mobile.sdk.api.listener.LegicMobileSdkEventListener;
import com.legic.mobile.sdk.api.listener.LegicMobileSdkPasswordEventListener;
import com.legic.mobile.sdk.api.listener.LegicMobileSdkRegistrationEventListener;
import com.legic.mobile.sdk.api.listener.LegicMobileSdkSynchronizeEventListener;
import com.legic.mobile.sdk.api.listener.LegicNeonFileEventListener;
import com.legic.mobile.sdk.api.listener.LegicReaderEventListener;
import com.legic.mobile.sdk.api.types.LcMessageMode;
import com.legic.mobile.sdk.api.types.LegicMobileSdkErrorReason;
import com.legic.mobile.sdk.api.types.LegicMobileSdkFileAddressingMode;
import com.legic.mobile.sdk.api.types.LegicMobileSdkPushType;
import com.legic.mobile.sdk.api.types.LegicMobileSdkStatus;
import com.legic.mobile.sdk.api.types.LegicNeonFile;
import com.legic.mobile.sdk.api.types.LegicNeonFileDefaultMode;
import com.legic.mobile.sdk.api.types.RfInterface;
import com.legic.mobile.sdk.api.types.RfInterfaceState;
import com.openkey.sdk.interfaces.OpenKeyCallBack;
import com.openkey.sdk.kaba.util.Settings;
import com.openkey.sdk.kaba.util.Utils;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class Kaba implements LegicMobileSdkSynchronizeEventListener,
        LegicMobileSdkRegistrationEventListener, LegicReaderEventListener,
        LegicNeonFileEventListener, LegicMobileSdkPasswordEventListener,
        LegicMobileSdkEventListener {
    private static final String LOG = "LEGIC-SDK-QUICKSTART";
    private LegicMobileSdkManager mManager;
    private Context mContext;
    private OpenKeyCallBack mOpenKeyCallBack;

    public Kaba(Context context, OpenKeyCallBack openKeyCallBack) {
        mOpenKeyCallBack = openKeyCallBack;
        mContext = context;

        setupKaba();
        register();
    }


    public void setupKaba() {
        try {
            initSdk();
            registerListeners();
            //unregisterListeners();
        } catch (LegicMobileSdkException e) {
            e.printStackTrace();
        }
    }


    //-----------------------------------------------------------------------------------------------------------------|
    private void initSdk() throws LegicMobileSdkException {
        mManager = Utils.getSdkManager(mContext);

        registerListeners();

        if (!mManager.isStarted()) {
            mManager.start(Settings.mobileAppId, Settings.mobileAppTechUser, Settings.mobileAppTechPassword,
                    Settings.serverUrl);
        }

        mManager.setLcProjectAddressingMode(true);

        if (mManager.isRegisteredToBackend()) {
            if (mManager.isRfInterfaceSupported(RfInterface.BLE) && !mManager.isRfInterfaceActive(RfInterface.BLE)) {
                mManager.activateRfInterface(RfInterface.BLE);
            }
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    protected void registerListeners() {
        if (mManager == null) {
            return;
        }
        try {
            mManager.registerForSynchronizeEvents(this);
            mManager.registerForRegistrationEvents(this);
            mManager.registerForReaderEvents(this);
            mManager.registerForFileEvents(this);
            mManager.registerForPasswordEvents(this);
            mManager.registerForSdkEvents(this);
        } catch (LegicMobileSdkException e) {
            Log.e("Kaba", "Could not register listener: " + e.getLocalizedMessage());
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    public void unregister() {
        mManager.unregister();
    }

    //-----------------------------------------------------------------------------------------------------------------|
    public void synchronise() {
        mManager.synchronizeWithBackend();
    }

    public void startScanning() {
        activateFile(1);
    }

    //-----------------------------------------------------------------------------------------------------------------|
    public void getAllFiles() {
        Log.e("Kaba", "Get all files from SDK");

        try {
            List<LegicNeonFile> files = mManager.getAllFiles();
            int fileIndex = 1;

            for (LegicNeonFile f : files) {
                String fileInfos = "Index: " + fileIndex++;
                fileInfos += "\nState: " + f.getFileState().toString();

                byte[] fileId = f.getFileId();
                if (fileId.length > 0) {
                    fileInfos += "\nFile Id: " + Utils.dataToByteString(f.getFileId());
                    for (String key : f.getMetaData().keySet()) {
                        fileInfos += "\n" + key + ": " + f.getMetaData().get(key).getStringValue();
                    }
                }
                Log.e("Kaba", fileInfos);
            }

        } catch (LegicMobileSdkException e) {
            Log.e("Kaba", "Error getting files from sdk: " + e.getLocalizedMessage());
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    public void activateFile(int index) {
        Log.e("Kaba", "Activate file index " + index);


        if (index > 0) {
            try {
                List<LegicNeonFile> files = mManager.getAllFiles();

                if (index <= files.size()) {
                    LegicNeonFile f = files.get(index - 1);

                    Log.e("Kaba", "file Id " + Utils.dataToByteString(f.getFileId()));

                    try {
                        mManager.activateFile(f);
                        mManager.setDefault(f, LegicNeonFileDefaultMode.LC_PROJECT_DEFAULT, true);
                    } catch (LegicMobileSdkException e) {
                        Log.e("Kaba", e.getLocalizedMessage());
                    }
                } else {
                    Log.e("Kaba", "File referenced by index does not exist");
                }

            } catch (LegicMobileSdkException e) {
                Log.e("Kaba", e.getLocalizedMessage());
            }
        } else {
            Log.e("Kaba", "Please use file Index > 0");
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    public boolean haveKey() {
        int index = 1;
        Log.e("Kaba", "Activate file index " + index);


        if (index > 0) {
            try {
                List<LegicNeonFile> files = mManager.getAllFiles();

                if (index <= files.size()) {
                    LegicNeonFile f = files.get(index - 1);

                    Log.e("Kaba", "file Id " + Utils.dataToByteString(f.getFileId()));

                    try {
                        mManager.activateFile(f);
                        mManager.setDefault(f, LegicNeonFileDefaultMode.LC_PROJECT_DEFAULT, true);
                        return true;
                    } catch (LegicMobileSdkException e) {
                        Log.e("Kaba", e.getLocalizedMessage());
                    }
                } else {
                    Log.e("Kaba", "File referenced by index does not exist");
                }

            } catch (LegicMobileSdkException e) {
                Log.e("Kaba", e.getLocalizedMessage());
            }
        } else {
            Log.e("Kaba", "Please use file Index > 0");
        }

        return false;
    }

    //-----------------------------------------------------------------------------------------------------------------|
    public void register() {


        //Get Device ID from EditText box
        String deviceId = "10008-919988333963";

        SharedPreferences.Editor editor = mContext.getSharedPreferences(Settings.MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("DeviceID", deviceId);
        editor.apply();

        List<RfInterface> interfaces = new ArrayList<>();
        try {

            boolean ble = mManager.isRfInterfaceSupported(RfInterface.BLE);

            if (ble) {
                interfaces.add(RfInterface.BLE);
            }
        } catch (LegicMobileSdkException e) {
            Log.e("Kaba", "Exception during registration: " + e.getLocalizedMessage());
        }


        mManager.initiateRegistration(
                deviceId,
                interfaces,
                Settings.lcConfirmationMethod,
                "",
                LegicMobileSdkPushType.GCM);
    }

    //-----------------------------------------------------------------------------------------------------------------|
    public void deactivateAllFiles() {
        Log.e("Kaba", "Deactivate all files");

        try {
            List<LegicNeonFile> files = mManager.getAllFiles();

            for (LegicNeonFile f : files) {
                try {
                    mManager.deactivateFile(f);
                } catch (LegicMobileSdkException e) {
                    Log.e("Kaba", e.getLocalizedMessage());
                }
            }
        } catch (LegicMobileSdkException e) {
            Log.e("Kaba", e.getLocalizedMessage());
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    @Override
    public void backendFileChangedEvent(LegicNeonFile legicFile) {
        Log.e("Kaba", "File changed -> file " + legicFile);
    }

    //-----------------------------------------------------------------------------------------------------------------| 
    @Override
    public void backendRequestAddFileDoneEvent(LegicMobileSdkStatus legicSdkStatus) {
        if (legicSdkStatus.isSuccess()) {
            Log.e("Kaba", "Backend Request add file done with status " + legicSdkStatus);
        } else {
            handleSdkErrors(legicSdkStatus);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------| 
    @Override
    public void backendRequestRemoveFileDoneEvent(LegicMobileSdkStatus status) {
        if (status.isSuccess()) {
            Log.e("Kaba", "Backend Request remove file done with status " + status);
        } else {
            handleSdkErrors(status);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------| 
    @Override
    public void readerReadFileEvent(LegicNeonFile legicFile, RfInterface rfInterface) {
        Log.e("Kaba", "Reader read Event on file  " + legicFile + " on interface " + rfInterface);

        byte[] messageData = new byte[]{0, 1, 1};
        try {
            mManager.sendLcMessage(messageData, LcMessageMode.ENCRYPTED_MACED_FILE_KEYS, rfInterface);
        } catch (LegicMobileSdkException e) {
        }
    }

    //-----------------------------------------------------------------------------------------------------------------| 
    @Override
    public void readerWriteFileEvent(LegicNeonFile legicFile, RfInterface rfInterface) {
        Log.e("Kaba", "Reader write Event on file  " + legicFile + " on interface " + rfInterface);
    }

    //-----------------------------------------------------------------------------------------------------------------| 
    @Override
    public void readerLcMessageEvent(byte[] data, LcMessageMode lcMessageMode, RfInterface rfInterface) {
        Log.e("Kaba", "LC message event data: " + Utils.dataToByteString(data) + " mode: " + lcMessageMode
                + " on interface " + rfInterface);
    }

    //-----------------------------------------------------------------------------------------------------------------| 
    @Override
    public void readerLcMessagePollingEvent(LcMessageMode lcMessageMode, RfInterface rfInterface) {
        Log.e("Kaba", "LC message polling event, mode: " + lcMessageMode + " on interface " + rfInterface);

    }

    //-----------------------------------------------------------------------------------------------------------------| 
    @Override
    public void readerConnectEvent(long Id, LegicMobileSdkFileAddressingMode mode, int readerType,
                                   RfInterface rfInterface) {
        Log.e("Kaba", "Reader connect event, id : " + Id + "/" + mode
                + " Reader Type: " + readerType + " interface:" + rfInterface);
    }

    //-----------------------------------------------------------------------------------------------------------------| 
    @Override
    public void backendRegistrationStartDoneEvent(LegicMobileSdkStatus status) {
        if (status.isSuccess()) {
            completeRegister("492660");
            Log.e("Kaba", "Registration Step 1 done with status " + status);
        } else {
            handleSdkErrors(status);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    public void completeRegister(String token) {
        mManager.register(token);
    }

    //-----------------------------------------------------------------------------------------------------------------|
    @Override
    public void backendRegistrationFinishedDoneEvent(LegicMobileSdkStatus status) {
        if (status.isSuccess()) {
            Log.e("Kaba", "Registration Step 2 done with status " + status);
        } else {
            handleSdkErrors(status);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------| 
    @Override
    public void backendUnregisterDoneEvent(LegicMobileSdkStatus status) {
        if (status.isSuccess()) {
            Log.e("Kaba", "Unregister done with status " + status);
        } else {
            handleSdkErrors(status);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    @Override
    public void backendSynchronizeStartEvent() {
        Log.e("Kaba", "Synchronize started");
    }

    //-----------------------------------------------------------------------------------------------------------------|
    @Override
    public void backendSynchronizeDoneEvent(LegicMobileSdkStatus status) {
        if (status.isSuccess()) {
            getAllFiles();
            Log.e("Kaba", "Synchronize done with status " + status);
        } else {
            handleSdkErrors(status);
        }
    }


    //-----------------------------------------------------------------------------------------------------------------| 
    @Override
    public void readerPasswordRequestEvent(byte[] data, RfInterface rfInterface) {
        Log.e("Kaba", "Password request with bytes " + Utils.dataToByteString(data) + " interface:" + rfInterface);
    }


    //-----------------------------------------------------------------------------------------------------------------| 
    @Override
    public void sdkActivatedEvent(long identifier, LegicMobileSdkFileAddressingMode mode,
                                  RfInterface rfInterface) {
        Log.e("Kaba", "Interface activated, Identifier: " + identifier + " mode: " + mode
                + " interface:" + rfInterface);
    }

    //-----------------------------------------------------------------------------------------------------------------| 
    @Override
    public void sdkDeactivatedEvent(long identifier, LegicMobileSdkFileAddressingMode mode,
                                    RfInterface rfInterface) {
        Log.e("Kaba", "Interface deactivated, Identifier: " + identifier + " mode: " + mode
                + " interface:" + rfInterface);
    }

    //-----------------------------------------------------------------------------------------------------------------| 
    @Override
    public void sdkRfInterfaceChangeEvent(RfInterface rfInterface,
                                          RfInterfaceState rfInterfaceState) {
        Log.e("Kaba", "Interface changed interface:" + rfInterface + " new state: " + rfInterfaceState);
    }

    //-----------------------------------------------------------------------------------------------------------------|
    private void checkPermissions() {

        String[] requiredPermissions = new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        List<String> missingPermissions = new ArrayList<>();

        boolean allPermissionsOk = true;
        for (String p : requiredPermissions) {
            Log.d(LOG, "checking Permission: " + p);
            if (ActivityCompat.checkSelfPermission(mContext, p) != PackageManager.PERMISSION_GRANTED) {
                Log.d(LOG, "missing Permission: " + p);
                allPermissionsOk = false;
                if (ActivityCompat.shouldShowRequestPermissionRationale((FragmentActivity) mContext, p)) {
                    Log.d(LOG, "User already denied permission once, he probably needs an explanation: " + p);
                }
                missingPermissions.add(p);
            }
        }

        if (!allPermissionsOk) {
            // request all missing permissions
            ActivityCompat.requestPermissions((FragmentActivity) mContext, missingPermissions.toArray(new String[0]),
                    /* unused because no callback*/ 0);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    private void handleSdkErrors(LegicMobileSdkStatus status) {
        // this method logs only when status is not "OK"
        if (!status.isSuccess()) {

            // LegicMobileSdkErrorReason gives more insight about the cause
            LegicMobileSdkErrorReason reason = status.getReason();

            Log.e("Kaba", "An action failed with the following error: " + status.getError().name());
            switch (reason.getReasonType()) {
                case SDK_ERROR:
                    Log.e("Kaba", "SDK internal error:\n" +
                            "You probably tried actions that are not allowed (unsupported interfaces, " +
                            "activation of non-deployed files, invalid data).");

                    Log.e("Kaba", "SDK error code: " + reason.getSdkErrorCode());
                    break;
                case BACKEND_ERROR:
                    Log.e("Kaba", "Backend error:\n" +
                            "This is usually caused by invalid configuration data (invalid mobileAppId), " +
                            "incorrect requests (wrong state, not registered) or by problems on the backend system.");

                    Log.e("Kaba", "Back-end error code (LEGIC Connect): " + reason.getErrorCode());
                    break;
                case HTTP_ERROR:
                    Log.e("Kaba", "HTTP error:\n" +
                            "This could be caused by connection or authentication problems, please check " +
                            "your configuration and/or your network settings.");

                    Log.e("Kaba", "HTTP Error code: " + reason.getErrorCode());
                    break;
                default:
                    Log.e("Kaba", "Unknown error reason: " + reason.toString());
            }
            Log.e("Kaba", "Full error description:\n" + reason);
        }
    }


    //-----------------------------------------------------------------------------------------------------------------|
    protected void unregisterListeners() {
        if (mManager == null) {
            return;
        }
        mManager.unregisterAnyEvents(this);
    }
}
