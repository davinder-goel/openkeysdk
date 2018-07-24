package com.openkeysdk.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.openkey.sdk.OpenKeyManager;
import com.openkey.sdk.api.response.session.SessionResponse;
import com.openkeysdk.R;

public class Dashboard extends BaseActivity implements View.OnClickListener {

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
    private String mToken = "gnaccifv5snt2nucvtcoalc3jzkpcix55y3j64v7xtgv6ekfrshkfy3v46sacefn";

    //SALTO
    //  private String mToken = "ds7v4gfvw24att7ykobnxyanvm6tgxr4g7ko2v3balxxsl7yoetnjlljqyc5t3sk";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        init();
        listners();
    }

    public void init() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBtnAuthenciate = findViewById(R.id.buttonAuthenciate);
        mBtnIntialize = findViewById(R.id.buttonIntialize);
        mBtnGetKey = findViewById(R.id.buttonGetKey);
        mBtnScan = findViewById(R.id.buttonOpenDoor);
        mEdtTextToken = findViewById(R.id.editTextSdkToken);
        mTextStatus = findViewById(R.id.textViewStatus);
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
        OpenKeyManager.getInstance(this).startScanning(this);
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
    public void authenticated(boolean isAuthenticated, String description) {
        mTextStatus.setVisibility(View.INVISIBLE);

//        if (isAuthenticated)
//        {
//            showMessage("Initializing...");
//            OpenKeyManager.getInstance(Dashboard.this).initialize(this);
//        }
    }

    @Override
    public void session(SessionResponse sessionResponse) {
        OpenKeyManager.getInstance(this).getSession(mToken, this);
    }


    @Override
    public void initializationSuccess() {
        hideMessage();
    }

    @Override
    public void initializationFailure(String errorDescription) {
        hideMessage();
    }

    @Override
    public void stopScan(boolean isLockOpened, String description) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideMessage();
            }
        });

    }

    @Override
    public void isKeyAvailable(boolean haveKey, String description) {
        hideMessage();

    }

    public void openDoor() {
        // Required for SDK
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
            return;
        }

        if (OpenKeyManager.getInstance(this).isKeyAvailable
                (this)) {
            if (mBluetoothAdapter.enable()) {
                showMessage("Scanning..");
                startScanning();
            } else {
                startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            }
        } else {
            //Utilities.getInstance().showToast(this, getString(R.string.no_key));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAuthenciate:
//                mToken = mEdtTextToken.getText().toString().trim();
//                mToken = "nbdcefbadslr7ezvlxp464rfkjrdrmkkpyh3767drd4a4hs3jaqmlv4cgxpl72tv";
                if (mToken.length() > 0) {
                    showMessage("Authenticating...");
                    OpenKeyManager.getInstance(Dashboard.this).authenticate(mToken,
                            this, false);
                }
                break;

            case R.id.buttonIntialize:
                showMessage("Initializing...");
                OpenKeyManager.getInstance(Dashboard.this).initialize(this);
                break;

            case R.id.buttonGetKey:
                showMessage("Fetching key...");
                OpenKeyManager.getInstance(Dashboard.this).getKey(this);
                break;

            case R.id.buttonOpenDoor:
                openDoor();
                break;
        }
    }
}
