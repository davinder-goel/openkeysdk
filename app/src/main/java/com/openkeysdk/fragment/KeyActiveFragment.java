package com.openkeysdk.fragment;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.openkey.sdk.OpenKeyManager;
import com.openkey.sdk.api.response.session.SessionResponse;
import com.openkeysdk.R;

public class KeyActiveFragment extends BaseFragment implements View.OnClickListener {


    private static final int MY_PERMISSIONS_REQUEST = 999;
    private Button mBtnAuthenciate;
    private Button mBtnIntialize;
    private Button mBtnGetKey;
    private Button mBtnScan;
    private TextView mTextStatus;
    private EditText mEdtTextToken;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean isScanning;
    private Handler handler;

    private int MOBILE_KEY_STATUES = 0;


    private Runnable stopper = new Runnable() {
        @Override
        public void run() {
            if (isScanning) {
                stopScanning();
            }
        }
    };

    //It should not be null

    //KABA
    //private String mToken = "nbdcefbadslr7ezvlxp464rfkjrdrmkkpyh3767drd4a4hs3jaqmlv4cgxpl72tv";

    //ASSA
    // private String mToken = "cqeevtgne7lchpcy24td22oc7m4qqvy4rsf3fjd7g5o6zkcvpcszhzax5wprcl72";

    //SALTO
    private String mToken = "cxfuihveoetedkah7vo2kc5ytczdtuxwt5ptlt7rqghwymou4cr2gyomwqychrbm";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dashboard, container, false);
        init(view);
        listners();
        return view.getRootView();
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
        OpenKeyManager.getInstance(getActivity()).startScanning(this);
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
        //OpenKeyManager.getInstance(getActivity()).getSession(mToken,this);
    }

    public void keyStatus() {

//    PENDING_KEY_SERVER_REQUEST=1;
//    KEY_SERVER_REQUESTED=2;
//    KEY_DELIVERED=3;

        switch (MOBILE_KEY_STATUES) {
            case 3:
                //If the key status is delivered and device have key then it will not call initialize
                if (!OpenKeyManager.getInstance(getActivity()).isKeyAvailable(this))
                    OpenKeyManager.getInstance(getActivity()).initialize(this);
                break;

            default:
                OpenKeyManager.getInstance(getActivity()).initialize(this);
                break;
        }
    }


    @Override
    public void session(SessionResponse sessionResponse) {
        hideMessage();
        if (sessionResponse != null && sessionResponse.getData() != null) {
            MOBILE_KEY_STATUES = sessionResponse.getData().getMobileKeyStatusId();
            keyStatus();
        }
    }


    @Override
    public void sessionFailure(String errorDescription) {
        hideMessage();
        showToast("Booking not found");
    }

    @Override
    public void initializationSuccess() {
        hideMessage();
        OpenKeyManager.getInstance(getActivity()).getKey(this);
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

        if (getActivity()!=null)
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showMessage("");
                showToast("DONE");
            }
        });
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

        if (OpenKeyManager.getInstance(getActivity()).isKeyAvailable
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
                mToken = mEdtTextToken.getText().toString().trim();
                if (mToken.length() > 0) {
                    OpenKeyManager.getInstance(getActivity()).getSession(mToken,
                            this);
                }
                break;

            case R.id.buttonIntialize:
                showMessage("Initializing...");
                OpenKeyManager.getInstance(getActivity()).initialize(this);
                break;

            case R.id.buttonGetKey:
                showMessage("Fetching key...");
                OpenKeyManager.getInstance(getActivity()).getKey(this);
                break;

            case R.id.buttonOpenDoor:
                openDoor();
                break;
        }
    }
}
