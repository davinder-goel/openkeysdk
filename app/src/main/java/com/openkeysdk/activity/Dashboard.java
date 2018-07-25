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
import android.widget.Toast;

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
    // private String mToken = "ohpw6g45h67ajor4ejrtsi3u7naojiqmwcfbc43kwogmmeagi7ja2345ueet4ko2";

    //SALTO
    private String mToken = "gzqc2swylev6aqxiqzpe62leruin7vua6nqdplu6aovfraahbpgqb6mhrt462z43";

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
    protected void onResume() {
        super.onResume();
        OpenKeyManager.getInstance(Dashboard.this).getSession(mToken,
                this);
    }

    public void keyStatus() {

//    PENDING_KEY_SERVER_REQUEST=1;
//    KEY_SERVER_REQUESTED=2;
//    KEY_DELIVERED=3;

        switch (MOBILE_KEY_STATUES) {
            case 1:
                OpenKeyManager.getInstance(this).initialize(this);
                break;

            case 2:
                OpenKeyManager.getInstance(this).initialize(this);
                break;

            case 3:
                if (!OpenKeyManager.getInstance(this).isKeyAvailable(this)) {
                    OpenKeyManager.getInstance(this).initialize(this);
                }
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
    public void sessionFailure() {
        hideMessage();
        showToast("Booking not found");
    }

    @Override
    public void initializationSuccess() {
        hideMessage();
        OpenKeyManager.getInstance(this).getKey(this);
    }

    @Override
    public void initializationFailure(String errorDescription) {
        hideMessage();
        showToast("initializationFailure");
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
        showToast("DEVICE HAVE KEY " + haveKey);

    }

    public void showToast(String message) {
        Toast.makeText(Dashboard.this, message, Toast.LENGTH_LONG).show();
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
                mEdtTextToken.setText(mToken);
                mToken = mEdtTextToken.getText().toString().trim();
                if (mToken.length() > 0) {
                    OpenKeyManager.getInstance(Dashboard.this).getSession(mToken,
                            this);
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
