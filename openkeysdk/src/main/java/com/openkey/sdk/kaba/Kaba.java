package com.openkey.sdk.kaba;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.legic.mobile.sdk.api.LegicMobileSdkManager;
import com.legic.mobile.sdk.api.exception.LegicMobileSdkException;
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
import com.openkey.sdk.OpenKeyManager;
import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.api.response.session.SessionResponse;
import com.openkey.sdk.interfaces.OpenKeyCallBack;
import com.openkey.sdk.kaba.response.invitationcode.KabaToken;
import com.openkey.sdk.kaba.util.BLEDataHandler;
import com.openkey.sdk.kaba.util.Settings;
import com.openkey.sdk.kaba.util.Utils;
import com.openkey.sdk.singleton.GetBooking;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.openkey.sdk.kaba.util.Settings.mobileAppId;

public class Kaba implements LegicMobileSdkSynchronizeEventListener,
        LegicMobileSdkRegistrationEventListener, LegicReaderEventListener,
        LegicNeonFileEventListener {

    private LegicMobileSdkManager mManager;
    private Application mContext;
    private OpenKeyCallBack mOpenKeyCallBack;
    private String kabaRegistrationToken;
    private boolean isSynchronizationStarted;
    private boolean isLoginActionFired;
    // tells how many time the whole operation fails in a single active time
    private int failedCounter;
    //-----------------------------------------------------------------------------------------------------------------|
    private Callback<KabaToken> kabaToken = new Callback<KabaToken>() {
        @Override
        public void onResponse(Call<KabaToken> call, Response<KabaToken> response) {
            if (response.isSuccessful()) {
                KabaToken kabaToken = response.body();
                if (kabaToken != null && kabaToken.getData() != null && kabaToken.getData().getCode() != null &&
                        kabaToken.getData().getCode().getPrepareCustomRegistrationResponse() != null &&
                        kabaToken.getData().getCode().getPrepareCustomRegistrationResponse().getToken() != null) {
                    String token = kabaToken.getData().getCode().getPrepareCustomRegistrationResponse().getToken();
                    Utilities.getInstance().saveValue(Constants.KABA_REGISTRATION_TOKEN, token, mContext);
                    startKabaProcessing();
                } else
                    mOpenKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);

            } else {
                // Show the message  from the error body if response is not successful
                Utilities.getInstance().handleApiError(response.errorBody(), mContext);
                mOpenKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
            }
        }

        @Override
        public void onFailure(Call<KabaToken> call, Throwable t) {
            mOpenKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);

        }
    };

    //-----------------------------------------------------------------------------------------------------------------|
    public Kaba(Application context, OpenKeyCallBack openKeyCallBack) {
        mOpenKeyCallBack = openKeyCallBack;
        mContext = context;
        setupKaba();
    }

    //-----------------------------------------------------------------------------------------------------------------|
    private void setupKaba() {
        try {

            if (mManager != null)
                mManager = null;

            mManager = Utils.getSdkManager(mContext);
            registerListeners();
            initSdk();
            startKabaProcessing();
        } catch (LegicMobileSdkException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    private void initSdk() throws LegicMobileSdkException {
        if (!mManager.isStarted()) {
            mManager.start(mobileAppId, Settings.mobileAppTechUser, Settings.mobileAppTechPassword,
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

    //-----------------------------------------------------------------------------------------------------------------|
    private void registerListeners() {
        unregisterListeners();

        if (mManager == null) {
            return;
        }
        try {
            mManager.registerForSynchronizeEvents(this);
            mManager.registerForRegistrationEvents(this);
            mManager.registerForReaderEvents(this);
            mManager.registerForFileEvents(this);
        } catch (LegicMobileSdkException e) {
            Log.e("Kaba", "Could not register listener: " + e.getLocalizedMessage());
        }
    }

    /**
     * Prepare application startup for KABA,Before execetuing any operation
     * on, KABA the successful application startup is required.
     */
    private void startKabaProcessing() {
        kabaRegistrationToken = Utilities.getInstance().getValue(Constants.KABA_REGISTRATION_TOKEN, "", mContext);
        if (kabaRegistrationToken.length() > 0) {
            startKABA();
        } else {
            getKabaRegistrationToken();
        }
    }

    /**
     * Get registration token from the kaba id-connect server
     */
    private void getKabaRegistrationToken() {
        Api.setInitializePersonalizationForKaba(mContext, kabaToken, mOpenKeyCallBack);
    }
    //-----------------------------------------------------------------------------------------------------------------|

    /**
     * start kaba functionality
     */
    private void startKABA() {
        // if user is not register
        try {
            if (!mManager.isRegisteredToBackend()) {
                register();
            } else {

                int mobileKeyStatusId = Utilities.getInstance().getValue(Constants.MOBILE_KEY_STATUS,
                        0, mContext);
                if (haveKey() && mobileKeyStatusId == 3) {

                    mOpenKeyCallBack.isKeyAvailable(true, com.openkey.sdk.Utilities.Response.FETCH_KEY_SUCCESS);
                } else {
                    if (mobileKeyStatusId == 1) {
                        /**
                         * Update the status on server that Registration Complete has been completed on Kaba server
                         */
                        Api.setPeronalizationComplete(mContext, mOpenKeyCallBack);
                    } else {
                        mOpenKeyCallBack.initializationSuccess();
                    }
                }
            }
        } catch (LegicMobileSdkException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    public void register() {

        //Get Device ID from EditText box
        String deviceId = mobileAppId + "-" + Utilities.getInstance().getValue(Constants.UNIQUE_NUMBER, "", mContext);
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
    @Override
    public void backendRegistrationStartDoneEvent(LegicMobileSdkStatus status) {
        if (status.isSuccess()) {
            if (kabaRegistrationToken != null && kabaRegistrationToken.length() > 0)
                completeRegister(kabaRegistrationToken);
            else
                mOpenKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);

            Log.e("Kaba", "Registration Step 1 done with status " + status);
        } else {
            mOpenKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    private void completeRegister(String token) {
        mManager.register(token);
    }

    //-----------------------------------------------------------------------------------------------------------------|
    @Override
    public void backendRegistrationFinishedDoneEvent(LegicMobileSdkStatus status) {
        if (status.isSuccess()) {
            Api.setPeronalizationComplete(mContext, mOpenKeyCallBack);
            Log.e("Kaba", "Registration Step 2 done with status " + status);
        } else {
            mOpenKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    public void synchronise() {
        Log.e("kaba synchronise", "Called");
        // isLoginActionFired = true;
        isSynchronizationStarted = true;
        mManager.synchronizeWithBackend();
    }

    //-----------------------------------------------------------------------------------------------------------------|
    @Override
    public void backendSynchronizeStartEvent() {
        Log.e("Kaba backend", "Synchronize started");
    }

    //-----------------------------------------------------------------------------------------------------------------|
    @Override
    public void backendSynchronizeDoneEvent(LegicMobileSdkStatus status) {
        if (isSynchronizationStarted) {
            isSynchronizationStarted = false;
            if (status.isSuccess()) {
                Log.e("SynchronizeStartEvent", "Synchronize done with status " + status);
                getAllFiles();
                Log.e("Kaba", " " + status);
            } else {
                Log.e("backendSynchronize", "failed " + status);
                synchProcess();
                //mOpenKeyCallBack.isKeyAvailable(false, com.openkey.sdk.Utilities.Response.FETCH_KEY_FAILED);

            }
        }
    }

    private void synchProcess() {
        Log.e("synchProcess", ":start");

        failedCounter++;
        if (failedCounter > 4) {
            Log.e("kaba synchronise", "if");
            Log.e("failedCounter", "::" + failedCounter);
            Log.e("SynchronizeStartEvent", ":error");
            mOpenKeyCallBack.isKeyAvailable(false, com.openkey.sdk.Utilities.Response.FETCH_KEY_FAILED);
        } else {
            Log.e("kaba synchronise", "else");
            // isLoginActionFired = true;
            isSynchronizationStarted = true;
            mManager.synchronizeWithBackend();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    private void getAllFiles() {
        deactivateAllFiles();
        if (haveKey()) {
            failedCounter = 0;
            OpenKeyManager.getInstance().updateKeyStatus(haveKey());
            mOpenKeyCallBack.isKeyAvailable(true, "FETCH_KEY_SUCCESS");
            Log.e("key", ": device has kaba key");
        } else {
            synchProcess();
            // mOpenKeyCallBack.isKeyAvailable(false, com.openkey.sdk.Utilities.Response.FETCH_KEY_FAILED);
            Log.e("Kaba", "Error getting files from sdk:");
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    public boolean haveKey() {
        Log.e("haveKey", "called");
        try {
            List<LegicNeonFile> files = mManager.getAllFiles();
            Log.e("LegicNeonFile", ":" + files.size());

            if (files.size() > 0) {
                SessionResponse sessionResponse = GetBooking.getInstance().getBooking();
                for (int i = 0; i < files.size(); i++) {
                    LegicNeonFile legicNeonFile = files.get(i);
                    String reservationnumber = "" + legicNeonFile.getMetaData().get("ReservationNumber").getStringValue();
                    String roomNumber = "" + legicNeonFile.getMetaData().get("RoomNumber").getStringValue();
                    logs(legicNeonFile);
                    Integer _bookingId = sessionResponse.getData().getParentSessionId() > 0
                            ? sessionResponse.getData().getParentSessionId()
                            : sessionResponse.getData().getId();
                    Log.e("reservationnumber", ":" + reservationnumber);
                    Log.e("roomNumber", ":" + roomNumber);
                    Log.e("_bookingId", ":" + _bookingId);
                    Log.e("Title", ":" + sessionResponse.getData()
                            .getHotelRoom().getTitle());

                    if (reservationnumber.trim().length() > 0
                            && roomNumber.trim().length() > 0) {
                        if (Integer.parseInt(reservationnumber)
                                == _bookingId && roomNumber.equals(sessionResponse.getData()
                                .getHotelRoom().getTitle())) {
                            return true;
                        }
                    }

                }
            }
        } catch (LegicMobileSdkException e) {
            Log.e("Kaba", e.getLocalizedMessage());
        }
        return false;
    }

    //-----------------------------------------------------------------------------------------------------------------|
    private void logs(LegicNeonFile legicNeonFile) {
        String fileInfos = "Index:";
        fileInfos += "\nState: " + legicNeonFile.getFileState().toString();
        byte[] fileId = legicNeonFile.getFileId();
        if (fileId.length > 0) {
            fileInfos += "\nFile Id: " + Utils.dataToByteString(legicNeonFile.getFileId());
            for (String key : legicNeonFile.getMetaData().keySet()) {
                fileInfos += "\n" + key + ": " + legicNeonFile.getMetaData().get(key).getStringValue();
            }
        }
        Log.e("GetAllFiles Kaba", fileInfos);
    }

    //-----------------------------------------------------------------------------------------------------------------|
    public void startScanning() {
        activateFile();
    }

    private void activateFile() {

        Log.e("Kaba", "Activate file index ");

        try {
            initSdk();
            try {
                List<LegicNeonFile> files = mManager.getAllFiles();

                if (files != null && files.size() > 0) {

                    for (int i = 0; i < files.size(); i++) {

                        LegicNeonFile legicNeonFile = files.get(i);
                        String reservationnumber = "" + legicNeonFile.getMetaData().get("ReservationNumber").getStringValue();
                        String roomNumber = "" + legicNeonFile.getMetaData().get("RoomNumber").getStringValue();
                        SessionResponse sessionResponse = GetBooking.getInstance().getBooking();
                        Integer _bookingId = sessionResponse.getData().getParentSessionId() > 0
                                ? sessionResponse.getData().getParentSessionId()
                                : sessionResponse.getData().getId();
                        if (reservationnumber.trim().length() > 0
                                && roomNumber.trim().length() > 0) {
                            if (Integer.parseInt(reservationnumber)
                                    == _bookingId && roomNumber.equals(sessionResponse.getData()
                                    .getHotelRoom().getTitle())) {
                                try {
                                    isLoginActionFired = true;
                                    Log.e("legicNeonFile", ":" + legicNeonFile);
                                    mManager.activateFile(legicNeonFile);
                                    mManager.setDefault(legicNeonFile, LegicNeonFileDefaultMode.LC_PROJECT_DEFAULT, true);
                                } catch (LegicMobileSdkException e) {
                                    Log.e("Kaba", e.getLocalizedMessage());
                                }
                                break;
                            } else
                                mManager.deactivateFile(legicNeonFile);
                        } else
                            mManager.deactivateFile(legicNeonFile);
                    }
                }
            } catch (LegicMobileSdkException e) {
                Log.e("Kaba", e.getLocalizedMessage());
            }

        } catch (LegicMobileSdkException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    private void deactivateAllFiles() {
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
    public void readerLcMessageEvent(byte[] data, LcMessageMode lcMessageMode,
                                     RfInterface rfInterface) {

        deactivateAllFiles();

        final BLEDataHandler dataHandler = new BLEDataHandler(data);
        if (dataHandler.isAccessGranted()) {
            mOpenKeyCallBack.stopScan(true, com.openkey.sdk.Utilities.Response.LOCK_OPENED_SUCCESSFULLY);
            if (isLoginActionFired) {
                isLoginActionFired = false;
                Api.logSDK(mContext, 1);
            }
        } else {
            mOpenKeyCallBack.stopScan(false, com.openkey.sdk.Utilities.Response.LOCK_OPENING_FAILURE);
//            Api.logSDK(mContext, 0);
        }

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
    public void backendUnregisterDoneEvent(LegicMobileSdkStatus status) {
        if (status.isSuccess()) {
            Log.e("Kaba", "Unregister done with status " + status);
        } else {
            handleSdkErrors(status);
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

    //-----------------------------------------------------------------------------------------------------------------|
    public void unregister() {
        if (mManager != null)
            mManager.unregister();
    }
    //-----------------------------------------------------------------------------------------------------------------|

}