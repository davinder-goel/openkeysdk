package com.openkeysdk;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.openkey.sdk.OpenKeyManager;
import com.openkey.sdk.api.response.session.SessionResponse;
import com.openkey.sdk.enums.EnvironmentType;

import java.util.ArrayList;

public class KeyActiveFragment extends BaseFragment implements View.OnClickListener {

    private Button mBtnAuthenciate;
    private Button mBtnIntialize;
    private Button mBtnGetKey;
    private Button mBtnScan;
    private TextView mTextStatus;
    private EditText mEdtTextToken;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean isScanning;
    private Handler handler;
    private static final int MY_PERMISSIONS_REQUEST = 999;
    private int MOBILE_KEY_STATUES = 0;

    private ArrayList<String> okcRoomNumbers;
    private String mOkCSelectedRoom;


    private Runnable stopper = new Runnable() {
        @Override
        public void run() {
            if (isScanning) {
                stopScanning();
            }
        }
    };

    //It should not be null
    private String mToken = "2hqeouhbvnyxiz45qqesbkhhhmqgq3yq3b7gzngm2v3xvn3zua53udgxr6lj3fl5";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dashboard, container, false);
        init(view);
        listners();
//        requestPermission();
        return view.getRootView();
    }


    private void requestPermission() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Activity activity = getActivity();
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


    public void init(View view) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBtnAuthenciate = view.findViewById(R.id.buttonAuthenciate);
        mBtnIntialize = view.findViewById(R.id.buttonIntialize);
        mBtnGetKey = view.findViewById(R.id.buttonGetKey);
        mBtnScan = view.findViewById(R.id.buttonOpenDoor);
        mEdtTextToken = view.findViewById(R.id.editTextSdkToken);
        mTextStatus = view.findViewById(R.id.textViewStatus);
    }

    public void listners() {
        mBtnAuthenciate.setOnClickListener(this);
        mBtnIntialize.setOnClickListener(this);
        mBtnGetKey.setOnClickListener(this);
        mBtnScan.setOnClickListener(this);
    }

    /**
     * Start lock opening process
     */
    private void startScanning() {
        isScanning = true;
        handler = new Handler();
        handler.postDelayed(stopper, 10000);
        OpenKeyManager.getInstance().startScanning(this, mOkCSelectedRoom);
    }

    /**
     * Stop lock opening process
     */
    private void stopScanning() {
        hideMessage();
        isScanning = false;
        if (handler != null) {
            handler.removeCallbacks(stopper);
        }
    }

    public void showMessage(String message) {
        mTextStatus.setVisibility(View.VISIBLE);
        mTextStatus.setText(message);
    }

    public void hideMessage() {
        mTextStatus.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        hasBTPermissions();
        //OpenKeyManager.getInstance(getActivity()).getSession(mToken,this);
    }


    private boolean hasBTPermissions() {
        boolean hasPermission = ContextCompat.checkSelfPermission(
                getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
        if (hasPermission == false) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    4101
            );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
            )
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                            getActivity(),
                            Manifest.permission.BLUETOOTH_SCAN
                    )
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                            getActivity(),
                            Manifest.permission.BLUETOOTH_CONNECT
                    )
                            != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                            getActivity(),
                            Manifest.permission.BLUETOOTH_ADVERTISE
                    )
                            != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_ADVERTISE
                        },
                        4011
                );
                return false;
            } else {
                return true;
            }
        }
        return true;
    }


    public void keyStatus() {

//    PENDING_KEY_SERVER_REQUEST=1;
//    KEY_SERVER_REQUESTED=2;
//    KEY_DELIVERED=3;
        switch (MOBILE_KEY_STATUES) {
            case 3:
                //If the key status is delivered and device have key then it will not call initialize
                if (!OpenKeyManager.getInstance().isKeyAvailable(this))
                    OpenKeyManager.getInstance().initialize(this);
                break;

            default:
                OpenKeyManager.getInstance().initialize(this);
                break;
        }
    }

    @Override
    public void sessionResponse(SessionResponse sessionResponse) {
        if (sessionResponse.getData() != null) {
            MOBILE_KEY_STATUES = sessionResponse.getData().getMobileKeyStatusId();
            keyStatus();
        }
    }

    @Override
    public void sessionFailure(String errorDescription, String errorCode) {
        Log.e("onFailure", ":");
        hideMessage();
        showToast("Booking not found");
    }

    @Override
    public void initializationSuccess() {
        hideMessage();
        showToast("initialization success");
        OpenKeyManager.getInstance().getKey(this);
    }

    @Override
    public void initializationFailure(String errorDescription) {
        hideMessage();
        showToast("initializationFailure");
    }

    @Override
    public void stopScan(boolean isLockOpened, String description) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isLockOpened) {
                    showMessage("Door opened Successfully.");
                }
                hideMessage();
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy:", "onDestroy:");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("onDestroyView:", "onDestroyView:");

    }

    @Override
    public void isKeyAvailable(boolean haveKey, String description) {
        Log.e("isKeyAvailable app", "Called");
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showMessage("");
                    showToast("DONE");
                }
            });
    }

    @Override
    public void getOKCandOkModuleMobileKeysResponse(ArrayList<String> availableRooms, Boolean isDeviceFound) {
        Log.e("Fetch rooms", availableRooms + " called");
        if (okcRoomNumbers == null) {
            okcRoomNumbers = new ArrayList<>();
        } else {
            okcRoomNumbers.clear();
        }

        if (availableRooms != null && availableRooms.size() > 0) {
            for (int i = 0; i < availableRooms.size(); i++) {
                okcRoomNumbers.add(availableRooms.get(i));
            }
            mOkCSelectedRoom = okcRoomNumbers.get(0); // for example

        }
    }

    @Override
    public void fetchDrkSubModules(ArrayList<String> availableRooms) {

    }

    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }


    public void openDoor() {
        // Required for SDK
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
            return;
        }

        if (OpenKeyManager.getInstance().isKeyAvailable
                (this)) {
            if (mBluetoothAdapter.enable()) {
                showMessage("Scanning..");
                startScanning();
            } else {
                startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            }
        } else {
            showToast("No key found");
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAuthenciate:
                mEdtTextToken.setText(mToken);
                String token = mEdtTextToken.getText().toString().trim();
                if (token.length() > 0) {
                    OpenKeyManager.getInstance().authenticate(token, this, EnvironmentType.DEV);
                } else {
                    showMessage("Please enter sdk token first.");
                }
                break;

            case R.id.buttonIntialize:
                showMessage("Initializing...");
                OpenKeyManager.getInstance().initialize(this);
                break;

            case R.id.buttonGetKey:
                showMessage("Fetching key...");
                OpenKeyManager.getInstance().getKey(this);
                break;

            case R.id.buttonOpenDoor:
                openDoor();
                break;
        }
    }
}
