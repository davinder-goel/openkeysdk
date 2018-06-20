package com.openkey.sdk.kaba;

import android.content.Context;
import android.util.Log;

import com.helixion.lokplatform.persistence.PersistentStoreException;
import com.helixion.secureelement.SeConnectionException;
import com.helixion.utilities.ByteArray;
import com.idconnect.core.api.IDConnectFactory;
import com.idconnect.core.api.IDConnectManager;
import com.idconnect.params.ConfirmationMethod;
import com.idconnect.params.ProfileIDs;
import com.idconnect.params.Property;
import com.idconnect.params.PropertyKeys;
import com.idconnect.params.PushTypes;
import com.idconnect.params.WalletApplication;
import com.idconnect.sdk.ble.BLEConfigParams;
import com.idconnect.sdk.ble.BLEFileSelectionModes;
import com.idconnect.sdk.enums.BLEPluginTypes;
import com.idconnect.sdk.enums.PluginMessageTypes;
import com.idconnect.sdk.exceptions.DataIncompatibleException;
import com.idconnect.sdk.exceptions.SETypeNotSupportedException;
import com.idconnect.sdk.exceptions.UserNotRegisteredException;
import com.idconnect.sdk.exceptions.WalletApplicationNotFoundException;
import com.idconnect.sdk.listeners.BLEListener;
import com.idconnect.sdk.listeners.RegistrationListener;
import com.idconnect.sdk.listeners.SeUIListener;
import com.idconnect.sdk.listeners.SynchroniseListener;
import com.idconnect.server.exceptions.ServerParamException;
import com.openkey.sdk.OpenKeyManager;
import com.openkey.sdk.Utilities.Constants;
import com.openkey.sdk.Utilities.Utilities;
import com.openkey.sdk.api.request.Api;
import com.openkey.sdk.api.service.Services;
import com.openkey.sdk.interfaces.OpenKeyCallBack;
import com.openkey.sdk.kaba.model.PrepareDirectWalletRegistrationRequest;
import com.openkey.sdk.kaba.model.Token;
import com.openkey.sdk.kaba.response.KabaTokenResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * @author OpenKey Inc.
 *         <p>
 *         This will handle all things about KABA lock manufacturer
 */

public class Kaba {

    //This walletId used to get the registration token from the kaba id-connect server
    private final String walletId = "10008";
    private OpenKeyCallBack openKeyCallBack;
    private Context mContext;
    private String kabaRegistrationToken;
    private String uniqueNumber;

    //KABA REQUIREMENTS
    private List<ProfileIDs> profileIDs = new ArrayList<>();
    private IDConnectManager manager = null;
    /**
     * Ble listener that listens the ble operation on Kaba Lock
     */
    private BLEListener bleListener = new BLEListener() {

        @Override
        public void success() {
            Log.e(TAG, "BLEListener: success");
        }

        @Override
        public void failure(SeConnectionException arg0) {
            Log.e(TAG, "BLEListener: failure");
        }
    };

    public Kaba(Context mContext, OpenKeyCallBack OpenKeyCallBack, String uniqueNumber) {
        this.openKeyCallBack = OpenKeyCallBack;
        this.mContext = mContext;
        this.uniqueNumber = uniqueNumber;
        setupKaba();
        startKabaProcessing();
    }

    /**
     * setup device for kaba lock
     */
    private void setupKaba() {
        // Add the type of SE Supported to the list of ProfileIds
        profileIDs.add(ProfileIDs.BLE);
        // Create the instance of IDConnectManager
        if (manager == null) {
            manager = IDConnectFactory.createIDConnectManager(mContext,
                    getSynchroniseListener(), walletId, "",
                    PushTypes.GCM, profileIDs, bleListener, getSeUIListener());
            // Once we have our instance of IDConnectManager then lets set slide_up the
            // server configuration. To run this application, please change the below values
            try {
                manager.getPersistentStore().saveServerURL(Constants.KABA_SERVER_URL);
            } catch (PersistentStoreException e) {
                e.printStackTrace();
            }
            manager.setUsername(Constants.KABA_SERVER_USER_NAME);
            manager.setPassword(Constants.KABA_SERVER_PASSWORD);
            try {
                manager.setConfigParam(BLEConfigParams.FILESELECTIONMODE.getParamKey(), BLEFileSelectionModes.PRESELECT_FILE.getMode());
            } catch (SETypeNotSupportedException e) {
                e.printStackTrace();
            }
        }
        // This simply prints to the log the current version of the OpenKeyCallBack
        Log.e(TAG, "onCreate: IDConnectManager version = " + manager.getSKDVersion());
    }


    /**
     * SeUiListener for Kaba OpenKeyCallBack
     *
     */
    private SeUIListener getSeUIListener() {
        return new SeUIListener() {

            @Override
            public void onReceiveMessageFromReader(int code, byte[] message) {
                Log.d(TAG, "BLE Message fromk reader - Code: " + code);
                if (message != null)
                    Log.d(TAG, "BLE Message from reader - Message: " + ByteArray.bytesToHexString(message));

                PluginMessageTypes type = PluginMessageTypes.getTypeFromState(code);
                switch (type) {
                    case BLUETOOTH_STATE:
                        int typeState=0;
                         if (message!=null&&message.length>0)
                         {
                             typeState =message[0];
                         }
                        BLEPluginTypes stateCode = BLEPluginTypes.getTypeFromState(typeState);
                        if (stateCode == BLEPluginTypes.PLUGIN_BLESTATE_ERROR ||
                                stateCode == BLEPluginTypes.PLUGIN_BLESTATE_NOT_ACTIVATED) {
                            //inform user BLE is disabled
                            Log.w(TAG, "BLE is disabled PLUGIN_BLESTATE_NOT_ACTIVATED");
                        } else if (stateCode == BLEPluginTypes.PLUGIN_BLESTATE_NOT_SUPPORTED) {
                            //inform user BLE is not supported
                            Log.w(TAG, "BLE is disabled PLUGIN_BLESTATE_NOT_SUPPORTED");
                        }
                        break;
                    case IDC_FILE_WAS_READ:
                        //inform user to do something
                        byte[] messageData = new byte[]{0, 1, 1};
                        try {
                            manager.sendMessageToBleReader(0, messageData);
                        } catch (SETypeNotSupportedException e) {
                            Log.w(TAG, "No BLE Support requested" + e);
                        }
                        break;
                    case IDC_MESSAGE:
                        final BLEDataHandler dataHandler = new BLEDataHandler(message);
                        if (dataHandler.isAccessGranted()) {
                            Utilities.getInstance().vibrate(mContext);
                            openKeyCallBack.stopScan(true, com.openkey.sdk.Utilities.Response.LOCK_OPENED_SUCCESSFULLY);
                        } else {
                            openKeyCallBack.stopScan(false, com.openkey.sdk.Utilities.Response.KEY_NOT_CORRECT);
                        }
                        stopScanning();
                        break;
                }
            }
        };
    }

    /**
     * stop scanning
     */
    private void stopScanning() {
        if (manager != null) {
            manager.deactivateAllCards();
        }
    }


    /**
     * Synchronize listener, for providing the response of synchronize , success or failure
     */
    private SynchroniseListener getSynchroniseListener() {
        return new SynchroniseListener() {

            @Override
            public void synchronisationStarted() {
                // This will be called when synchronisation has started. This is
                // returned when the
                // first synchronisation command completes and the next in sequence
                // begins
                Log.e(TAG, "SynchroniseListener: synchronisationStarted");

            }

            @Override
            public void synchronisationFailed(int code, String description) {
                Log.e(TAG, description + code);
                Log.e(TAG, "SynchroniseListener: synchronisationFailed");
                OpenKeyManager.getInstance(mContext).updateKeyStatus(false);
                openKeyCallBack.isKeyAvailable(false, com.openkey.sdk.Utilities.Response.FETCH_KEY_FAILED);
            }

            @Override
            public void synchronisationComplete() {
                OpenKeyManager.getInstance(mContext).updateKeyStatus(haveKey());
                // This will be called when.synchronisation has completed
                Log.e(TAG, "SynchroniseListener: synchronisationCompleted ");
                openKeyCallBack.isKeyAvailable(true, com.openkey.sdk.Utilities.Response.FETCH_KEY_SUCCESS);
            }

            @Override
            public void cardsUpdated() {
                // This will be called when new cards that have been downloaded
                // during synchronisation.
                // Cards downloaded can be retrieved through getAllCards()
                Log.e(TAG, "SynchroniseListener: cardsUpdated");
                //  openKeyCallBack.isKeyAvailable(true, com.openkey.sdk.Utilities.Response.FETCH_KEY_SUCCESS);
            }
        };
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
     * Get keys from the Kaba Server
     */
    public void getKabaKey() {

         /** Synchronise communicates with the server to
         * discover if there are any actions that the OpenKeyCallBack should perform.
         * One action can result in Wallet Apps being downloaded to the
         * persistent store. This would be reported through the SyncDevice
         * Listener passed into createIDConnectManager.*/
        try {
            manager.synchronize();
        } catch (ServerParamException e) {
            // This exception will be thrown if the server parameters were not
            // set
            // before a server operation takes place
            OpenKeyManager.getInstance(mContext).updateKeyStatus(false);
            openKeyCallBack.isKeyAvailable(false, com.openkey.sdk.Utilities.Response.FETCH_KEY_FAILED);
            Log.e(TAG, "Server configuration missing");
        } catch (UserNotRegisteredException e) {
            // This exception will be thrown if a getKabaKey command is
            // attempted
            // before the user is registered
            Log.e(TAG, "User not registered!");
            OpenKeyManager.getInstance(mContext).updateKeyStatus(false);
            openKeyCallBack.isKeyAvailable(false, com.openkey.sdk.Utilities.Response.FETCH_KEY_FAILED);
        }
    }

    /**
     * Start decrypting key
     */
    public void startScanning() {
        activateCards();
    }

    /**
     * This method is to activate the wallet cards for KABA, to open the lock.
     */
    private void activateCards() {

        try {
            List<WalletApplication> walletApps = manager.getAllCards();
            for (int i = 0; i < walletApps.size(); i++) {
                if (walletApps.get(i).getQualifier() > 0) {
                    Log.e(TAG, "Activating : " + walletApps.get(i).getWalletAppId() + "/" + walletApps.get(i).getQualifier());
                    manager.activateCard(walletApps.get(i).getWalletAppId(), walletApps.get(i).getQualifier());
                }
            }
        } catch (SeConnectionException e) {
            e.printStackTrace();
        } catch (WalletApplicationNotFoundException e) {
            e.printStackTrace();
        } catch (SETypeNotSupportedException e) {
            e.printStackTrace();
        } catch (PersistentStoreException e) {
            e.printStackTrace();
        } catch (DataIncompatibleException e) {
            e.printStackTrace();
        }
    }

    /**
     * start kaba functionality
     */
    private void startKABA() {
        try {
            // if user is not register
            if (!manager.getPersistentStore().isUserRegistered()) {
                registerToKaba();
            } else {
                /**
                 * Update the status on server that Registration Complete has been completed on Kaba server
                 */
                Api.setPeronalizationComplete(mContext,openKeyCallBack);
            }
        } catch (PersistentStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * Authenticate the device to Kaba Server.
     */
    private void registerToKaba() {

        final String publicSEId = getPublicSEId();

        Log.e(TAG, "registered  number to kaba : " + publicSEId);
        // Any required properties for registration can be passed in a List of
        // Properties
        List<Property> properties = new ArrayList<>();
        properties.add(new Property(PropertyKeys.PHONE_NUMBER.getName(), publicSEId));

        // Call registration with phone number and method for receiving kabaRegistrationToken
        try {
            manager.registerWallet(new RegistrationListener() {

                @Override
                public void fail(int code, String description) {
                    // If a failure occurred will performing the registerToKaba
                    // request the
                    // error will be reported here
                    Log.e(TAG, "register: failure");
                    Log.e("code : " + code, description);
                    openKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
                }

                @Override
                public void success(boolean b, String s) {

                    completeRegister();

                    Log.e(TAG, "register: success : " + s);
                }
            }, properties, publicSEId, ConfirmationMethod.NONE);
        } catch (ServerParamException e) {
            // This exception will be thrown if the server parameters were not
            // set
            // before a server operation takes place
            Log.e(TAG, "Server configuration missing");
            openKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
        }
    }




    /**
     * Check if the device have a valid key for Kaba Lock
     */
    public boolean haveKey() {
        try {

            if (manager == null) return false;
            // This will retrieve any cards that have been downloaded from the
            // server
            // and stored within the persistent store
            List<WalletApplication> walletApps = manager.getAllCards();
            // Once the list has been retrieved action on them how you see fit
            if (walletApps != null && !walletApps.isEmpty()) {
                for (int i = 0; i < walletApps.size(); i++) {
                    Log.e(TAG, walletApps.get(i).getWalletAppId() + "/" + walletApps.get(i).getQualifier());
                    if (walletApps.get(i).getQualifier() > 0) {
                        Log.e(TAG, "wallet has qualifier 1 : " + walletApps.get(i).getWalletAppId());
                        return true;
                    }
                }
                return false;
            } else {
                Log.e(TAG, "No Wallet");
            }
        } catch (PersistentStoreException e) {
            // If an issue occurred when loading to the persistent store
            openKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.UNKNOWN);
            Log.e(TAG, "PersistentStoreException");
        } catch (DataIncompatibleException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Complete the registration on Kaba Server, by given kabaRegistrationToken, which is returned by authentication
     * on Kaba server
     */
    private void completeRegister() {
        // Complete registration with provided kabaRegistrationToken
        try {
            manager.completeRegistration(new RegistrationListener() {
                @Override
                public void success(boolean b, String s) {
                    Log.e(TAG, "completeRegistration : Success : " + s);
                    try {
                        manager.getPersistentStore().saveUserRegistered(true);
                        // Update the status on server that Registration Complete has been completed on Kaba server
                        Api.setPeronalizationComplete(mContext,openKeyCallBack);
                    } catch (PersistentStoreException e) {
                        // If an issue occurred when saving to the persistent
                        // store
                        Log.e(TAG, "PersistentStoreException");
                        openKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
                    }
                }

                @Override
                public void fail(int code, String description) {
                    Log.e(TAG, "completeRegister: failure :: description ::" + description);
                    openKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);

                }
            }, kabaRegistrationToken);
        } catch (ServerParamException e) {
            // This exception will be thrown if the server parameters were not
            // set
            // before a server operation takes place
            Log.e(TAG, "Server configuration missing");
            openKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
        }
    }

    /**
     * Get registration token from the kaba id-connect server
     */
    private void getKabaRegistrationToken() {
        final String publicSEId = getPublicSEId();

        Log.e(TAG, "requesting token to kaba : " + publicSEId);
        PrepareDirectWalletRegistrationRequest request = new PrepareDirectWalletRegistrationRequest(publicSEId, walletId);
        Token token = new Token(request);
        Services services = Utilities.getInstance().getRetrofitForKaba().create(Services.class);
        services.getRegistrationToken(token).enqueue(new Callback<KabaTokenResponse>() {
            @Override
            public void onResponse(Call<KabaTokenResponse> call, Response<KabaTokenResponse> response) {
                if (response.isSuccessful()) {
                    KabaTokenResponse kabaTokenResponse=response.body();
                    if (kabaTokenResponse!=null&&kabaTokenResponse.getPrepareDirectWalletRegistrationResponse()
                            !=null&&kabaTokenResponse.getPrepareDirectWalletRegistrationResponse().getToken()!=null
                            &&kabaTokenResponse.getPrepareDirectWalletRegistrationResponse().getToken().length()>0)
                    {
                        Log.e(TAG, "Token From KABA : " + kabaTokenResponse.getPrepareDirectWalletRegistrationResponse().getToken());
                        kabaRegistrationToken = kabaTokenResponse.getPrepareDirectWalletRegistrationResponse().getToken();
                        // save token and start the process again
                        Utilities.getInstance().saveValue(Constants.KABA_REGISTRATION_TOKEN, kabaRegistrationToken, mContext);
                        startKabaProcessing();
                    }



                } else {
                    // Show the message  from the error body if response is not successful
                    Utilities.getInstance().handleApiError(response.errorBody(),mContext);
                    openKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
                }
            }

            @Override
            public void onFailure(Call<KabaTokenResponse> call, Throwable t) {
                openKeyCallBack.initializationFailure(com.openkey.sdk.Utilities.Response.INITIALIZATION_FAILED);
            }
        });
    }


    /**
     * Create publicSEId for KABA registration process
     */
    private String getPublicSEId() {
        String phoneNumber = uniqueNumber.replace("+", "");
        return "custom#" + walletId + "-" + phoneNumber;

    }
}
