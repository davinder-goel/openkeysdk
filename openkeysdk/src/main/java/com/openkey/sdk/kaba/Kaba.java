package com.openkey.sdk.kaba;

import static android.content.Context.MODE_PRIVATE;
import static com.openkey.sdk.kaba.util.Settings.mobileAppId;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.legic.mobile.sdk.api.LegicMobileSdkManager;
import com.legic.mobile.sdk.api.exception.SdkException;
import com.legic.mobile.sdk.api.listener.BackendEventListener;
import com.legic.mobile.sdk.api.listener.LcMessageEventListener;
import com.legic.mobile.sdk.api.listener.NeonFileEventListener;
import com.legic.mobile.sdk.api.listener.ReaderEventListener;
import com.legic.mobile.sdk.api.listener.SdkEventListener;
import com.legic.mobile.sdk.api.types.AddressingMode;
import com.legic.mobile.sdk.api.types.LcConfirmationMethod;
import com.legic.mobile.sdk.api.types.LcMessageMode;
import com.legic.mobile.sdk.api.types.NeonFile;
import com.legic.mobile.sdk.api.types.NeonFileState;
import com.legic.mobile.sdk.api.types.NeonSubFile;
import com.legic.mobile.sdk.api.types.ReaderFoundReport;
import com.legic.mobile.sdk.api.types.RfInterface;
import com.legic.mobile.sdk.api.types.RfInterfaceState;
import com.legic.mobile.sdk.api.types.SdkErrorReason;
import com.legic.mobile.sdk.api.types.SdkStatus;
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

import java.util.List;
import java.util.UUID;

import io.sentry.Sentry;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Kaba implements BackendEventListener, ReaderEventListener, SdkEventListener, LcMessageEventListener, NeonFileEventListener {

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

            if (mManager != null) {
                mManager = null;
            }

            mManager = Utils.getSdkManager(mContext);
            registerListeners();
            initSdk();
            startKabaProcessing();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    private void initSdk() {
        try {
            if (!mManager.isStarted()) {
                try {
                    Log.e("initSdk", "Start mobile SDK manager not started");
                    mManager.start(Settings.mobileAppId, Settings.mobileAppTechUser,
                            Settings.mobileAppTechPassword, Settings.serverUrl);
                } catch (SdkException e) {
                    e.printStackTrace();
                }
            }
            if (mManager.isStarted()) {
                Log.e("initSdk", "Start mobile SDK manager started");
                Log.e("initSdk", mManager.isRegisteredToBackend() + "");

                if (mManager.isRegisteredToBackend()) {

                    try {
                        if (mManager.isRfInterfaceHardwareSupported(RfInterface.BLE_PERIPHERAL)) {
                            if (!mManager.isRfInterfaceActive(RfInterface.BLE_PERIPHERAL)) {
                                mManager.setRfInterfaceActive(RfInterface.BLE_PERIPHERAL, true);
                            }
                        }
                        if (mManager.isRfInterfaceHardwareSupported(RfInterface.NFC_HCE)) {
                            if (!mManager.isRfInterfaceActive(RfInterface.NFC_HCE)) {
                                mManager.setRfInterfaceActive(RfInterface.NFC_HCE, true);
                            }
                        }
                    } catch (SdkException e) {
                        Log.e("init sdk 142", "Could not activate interface ", e);
                    }
                }
            }
        } catch (SdkException e) {
            e.printStackTrace();
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
            mManager.registerForSdkEvents(this);
            mManager.registerForReaderEvents(this);
            mManager.registerForBackendEvents(this);
            mManager.registerForLcMessageEvents(this);
            mManager.registerForNeonFileEvents(this);
        } catch (SdkException e) {
            Log.e("Kaba", "Could not register listener: " + e.getLocalizedMessage());
        }
    }

    /**
     * Prepare application startup for KABA,Before execetuing any operation
     * on, KABA the successful application startup is required.
     */
    private void startKabaProcessing() {
        kabaRegistrationToken = Utilities.getInstance().getValue(Constants.KABA_REGISTRATION_TOKEN, "", mContext);
        Log.e("Kaba Token", kabaRegistrationToken + "");
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
            Log.e("startKABA", mManager.isRegisteredToBackend() + "::Called");
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
        } catch (SdkException e) {
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
        Log.e("Device ID at the time Register", deviceId + "");
        mManager.initiateRegistrationWithBackend(
                deviceId,
                LcConfirmationMethod.SMS
        );
    }

    //-----------------------------------------------------------------------------------------------------------------|

    //-----------------------------------------------------------------------------------------------------------------|
    private void completeRegister(String token) {
        Log.e("completeRegister", token + "");

        mManager.registerWithBackend(token);
    }

    //-----------------------------------------------------------------------------------------------------------------|


    //-----------------------------------------------------------------------------------------------------------------|
    public void synchronise() {
        Log.e("kaba synchronise", "Called");
        // isLoginActionFired = true;
        isSynchronizationStarted = true;
        mManager.synchronizeWithBackend();
    }

    @Override
    public void backendRegistrationInitializedEvent(SdkStatus status) {
        Log.e("backendRegistrationInitializedEvent", "" + status);

        if (status.isSuccess()) {
            if (kabaRegistrationToken != null && kabaRegistrationToken.length() > 0) {
                completeRegister(kabaRegistrationToken);
            } else {
//                mOpenKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
                mOpenKeyCallBack.initializationFailure(status.toString());
            }
            Log.e("Kaba", "Registration Step 1 done with status " + status);
        } else {
//            mOpenKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
            mOpenKeyCallBack.initializationFailure(status.toString());
        }
    }

    @Override
    public void backendRegistrationFinishedEvent(SdkStatus status) {
        Log.e("backendRegistrationFinishedEvent", "" + status);

        if (status.isSuccess()) {
            Api.setPeronalizationComplete(mContext, mOpenKeyCallBack);
            Log.e("Kaba", "Registration Step 2 done with status " + status);
        } else {
            mOpenKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
        }
    }

    @Override
    public void backendUnregisteredEvent(SdkStatus status) {
        Log.e("backendUnregisteredEvent", "" + status);

        if (status.isSuccess()) {
            Log.e("Kaba", "Unregister done with status " + status);
        } else {
            handleSdkErrors(status);
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    @Override
    public void backendSynchronizeStartEvent() {
        Log.e("backendSynchronizeStartEvent", "Synchronize started");
    }

    @Override
    public void backendSynchronizeDoneEvent(SdkStatus status) {
        Log.e("backendSynchronizeDoneEvent", "" + status);

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

    //-----------------------------------------------------------------------------------------------------------------|


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
//        deactivateAllFiles();
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
            List<NeonFile> files = mManager.getAllNeonFiles();
            Log.e("LegicNeonFile", ":" + files.size());

            if (files.size() > 0) {
                SessionResponse sessionResponse = GetBooking.getInstance().getBooking();

                for (NeonFile f : files) {
                    String fileInfos = f.toString();

                    byte[] fileId = f.getFileId();
                    if (fileId.length > 0) {
                        fileInfos += " and File Id: " + Utils.dataToByteString(f.getFileId());
                    }
                    Log.e("Neon Files", fileInfos + "");


                    String reservationnumber = "" + f.getMetaData().get("ReservationNumber").getStringValue();
                    String roomNumber = "" + f.getMetaData().get("RoomNumber").getStringValue();
//                    logs(f);
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
        } catch (Exception e) {
            Log.e("Kaba null files", e.getLocalizedMessage());
        }
        return false;
    }

    //-----------------------------------------------------------------------------------------------------------------|
    private void logs(NeonFile legicNeonFile) {
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

//        Log.e("Kaba", "Activate file index ");

//            initSdk();
        try {
            List<NeonFile> files = mManager.getAllNeonFilesWithState(NeonFileState.DEPLOYED);
            int updated = 0;

            if (files != null && files.size() > 0) {

                for (NeonFile f : files) {

                    String reservationnumber = "" + f.getMetaData().get("ReservationNumber").getStringValue();
                    String roomNumber = "" + f.getMetaData().get("RoomNumber").getStringValue();
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
                                Log.e("legicNeonFile", ":" + f);
                                mManager.setNeonFileActive(f, true);
                                updated++;
                            } catch (SdkException e) {
                                Log.e("Kaba", e.getLocalizedMessage());
                            }
                            break;
                        } else {
                            deactivateAllFiles();
                        }
                    } else {
                        deactivateAllFiles();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Kaba null activate file", e.getLocalizedMessage());
            mOpenKeyCallBack.stopScan(false, "Kaba Exception::" + e.getLocalizedMessage() + "");

        }
    }

    //-----------------------------------------------------------------------------------------------------------------|
    private void deactivateAllFiles() {
        Log.e("Kaba", "Deactivate all files");

        try {
            if (mManager.isRfInterfaceActive(RfInterface.BLE_CENTRAL)) {
                Log.e("", "Deactivate BLE Central");
                mManager.setRfInterfaceActive(RfInterface.BLE_CENTRAL, false);
            } else {
                Log.e("", "Activate BLE Central");
            }
        } catch (SdkException e) {
            e.printStackTrace();
        }

    }

    //-----------------------------------------------------------------------------------------------------------------|

    //-----------------------------------------------------------------------------------------------------------------|
//    @Override
//    public void backendRequestAddFileDoneEvent(LegicMobileSdkStatus legicSdkStatus) {
//        if (legicSdkStatus.isSuccess()) {
//            Log.e("Kaba", "Backend Request add file done with status " + legicSdkStatus);
//        } else {
//            handleSdkErrors(legicSdkStatus);
//        }
//    }

    //-----------------------------------------------------------------------------------------------------------------|
//    @Override
//    public void backendRequestRemoveFileDoneEvent(LegicMobileSdkStatus status) {
//        if (status.isSuccess()) {
//            Log.e("Kaba", "Backend Request remove file done with status " + status);
//        } else {
//            handleSdkErrors(status);
//        }
//    }

    //-----------------------------------------------------------------------------------------------------------------|
    public void sendMessageToBackend() {
        Log.e("sendMessageToBackend", "Send Message to backend (Destination Mobile App)");

        try {
            String message = "LC Message from LEGIC Mobile SDK Quickstart App!";
            byte[] msgData = message.getBytes();

            mManager.sendLcMessageToBackendMobileApp(msgData);

            int counter = mManager.getNumberOfLcMessagesToBackend();

            String logText = "Number of LC Messages for backend: " + counter;
            Log.e("sendMessageToBackend", logText + "");

        } catch (SdkException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------------------------------------|

    //-----------------------------------------------------------------------------------------------------------------|
    @Override
    public void readerLcMessageEvent(byte[] data, LcMessageMode lcMessageMode,
                                     RfInterface rfInterface) {
//        Toast.makeText(mContext, "readerLcMessageEvent 523", Toast.LENGTH_SHORT).show();
//        deactivateAllFiles();
        if (!Constants.IS_SCANNING_STOPPED) {
            Constants.IS_SCANNING_STOPPED = true;
            final BLEDataHandler dataHandler = new BLEDataHandler(data);
            if (dataHandler.isAccessGranted()) {
                mOpenKeyCallBack.stopScan(true, com.openkey.sdk.Utilities.Response.LOCK_OPENED_SUCCESSFULLY);
                if (isLoginActionFired) {
                    Sentry.configureScope(scope -> {
                        scope.setTag("openingStatus", "KABA Lock opening success");
                        Sentry.captureMessage("openingStatus->KABA Lock opening success");
                    });
                    isLoginActionFired = false;
                    Api.logSDK(mContext, 1);
                }
            } else {
                Sentry.configureScope(scope -> {
                    scope.setTag("openingStatus", "KABA Lock opening failure");
                    Sentry.captureMessage("openingStatus->KABA Lock opening failure");

                });
//                Toast.makeText(mContext, dataHandler.getMessageString() + "::LINE-520", Toast.LENGTH_SHORT).show();
//                mOpenKeyCallBack.stopScan(false, com.openkey.sdk.Utilities.Response.LOCK_OPENING_FAILURE);
                mOpenKeyCallBack.stopScan(false, dataHandler.toString() + "");
//            Api.logSDK(mContext, 0);
            }
        }
        Log.e("Kaba", "LC message event data: " + Utils.dataToByteString(data) + " mode: " + lcMessageMode
                + " on interface " + rfInterface);
    }

    //-----------------------------------------------------------------------------------------------------------------|
    @Override
    public void readerLcMessagePollingEvent(LcMessageMode lcMessageMode, RfInterface rfInterface) {
        Log.e("Kaba", "LC message polling event, mode: " + lcMessageMode + " on interface " + rfInterface);
//        Toast.makeText(mContext, "Polling Event 558", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void readerAddedLcMessageEvent(int i, RfInterface rfInterface) {
//        Toast.makeText(mContext, "Added Event 563", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void readerPasswordRequestEvent(byte[] bytes, RfInterface rfInterface) {
//        Toast.makeText(mContext, "Pasword Event 569", Toast.LENGTH_SHORT).show();

    }

    //-----------------------------------------------------------------------------------------------------------------|

    //-----------------------------------------------------------------------------------------------------------------|


    //-----------------------------------------------------------------------------------------------------------------|
    private void handleSdkErrors(SdkStatus status) {
        // this method logs only when status is not "OK"
        if (!status.isSuccess()) {

            // LegicMobileSdkErrorReason gives more insight about the cause
            SdkErrorReason reason = status.getReason();

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


    @Override
    public void backendNeonFileChangedEvent(NeonFile neonFile) {
        Log.e("backendNeonFileChangedEvent", "" + neonFile);

    }

    @Override
    public void backendRequestAddNeonFileDoneEvent(SdkStatus sdkStatus) {
        Log.e("backendRequestAddNeonFileDoneEvent", "" + sdkStatus);

    }

    @Override
    public void backendRequestRemoveNeonFileDoneEvent(SdkStatus sdkStatus) {
        Log.e("backendRequestRemoveNeonFileDoneEvent", "" + sdkStatus);

        if (sdkStatus.isSuccess()) {
            Log.e("Remove neon", "Backend Request remove file done with status " + sdkStatus);
        } else {
            handleSdkErrors(sdkStatus);
        }
    }

    @Override
    public void readerReadNeonFileEvent(NeonFile neonFile, RfInterface rfInterface) {
//        Toast.makeText(mContext, "Read Neon Event 644", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void readerWriteNeonFileEvent(NeonFile neonFile, RfInterface rfInterface) {
//        Toast.makeText(mContext, "Write neon Event 649", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void readerReadNeonSubFileEvent(NeonSubFile neonSubFile, NeonFile neonFile, RfInterface rfInterface) {
//        Toast.makeText(mContext, "Read neon sub Event 655", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void readerWriteNeonSubFileEvent(NeonSubFile neonSubFile, NeonFile neonFile, RfInterface rfInterface) {
//        Toast.makeText(mContext, "Write Neon sub Event 660", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void readerConnectEvent(long l, AddressingMode addressingMode, int i, UUID uuid, RfInterface rfInterface) {
//        Toast.makeText(mContext, "Connect Event 667", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void readerConnectFailedEvent(SdkStatus sdkStatus, UUID uuid, RfInterface rfInterface) {
//        Toast.makeText(mContext, "Connect Failed Event 673", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void readerDisconnectEvent(UUID uuid, RfInterface rfInterface) {
//        Toast.makeText(mContext, "Disconnect Event 679", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void readerReceivedReaderFoundReportEvent(ReaderFoundReport readerFoundReport) {
//        Toast.makeText(mContext, "Found report Event 685", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void rfInterfaceActivatedEvent(long l, AddressingMode addressingMode, RfInterface rfInterface) {
        Log.e("rfInterfaceActivatedEvent", "" + rfInterface);

    }

    @Override
    public void rfInterfaceDeactivatedEvent(long l, AddressingMode addressingMode, RfInterface rfInterface) {
        Log.e("rfInterfaceDeactivatedEvent", "" + rfInterface);

    }

    @Override
    public void rfInterfaceChangeEvent(RfInterface rfInterface, RfInterfaceState rfInterfaceState) {
        Log.e("rfInterfaceChangeEvent", "" + rfInterface);

    }
    //-----------------------------------------------------------------------------------------------------------------|

}