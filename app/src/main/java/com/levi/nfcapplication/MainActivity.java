package com.levi.nfcapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import com.acs.bluetooth.Acr1255uj1Reader;
import com.acs.bluetooth.Acr3901us1Reader;
import com.acs.bluetooth.BluetoothReader;
import com.acs.bluetooth.BluetoothReaderGattCallback;
import com.acs.bluetooth.BluetoothReaderManager;

import java.io.UnsupportedEncodingException;

import static com.levi.nfcapplication.clsBluetoothAPI.EXTRAS_DEVICE_ADDRESS;
import static com.levi.nfcapplication.clsBluetoothAPI.EXTRAS_DEVICE_NAME;

public class MainActivity extends AppCompatActivity {
    ReceiverManager mReceiverManager;
    private static final String TAG = "MainActivity";
    /***
     * --------------------------------------------------------------------------------
     * NFC Bluetooth 관련 프로퍼티
     * --------------------------------------------------------------------------------
     */
    /* Detected reader. */


    clsBluetoothAPI mBluetoothAPI;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ACCESS_COARSE_LOCATION = 2;

    public static final int REQUEST_SCAN_BT = 1140;
    public static final int REQUEST_NOT_BT = 1141;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAPI = clsBluetoothAPI.createInstance(this);
        mReceiverManager = ReceiverManager.init(this);
        /*
         * Use this check to determine whether BLE is supported on the device.
         * Then you can selectively disable BLE-related features.
         */

        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
        }else{
            /*
             * Initializes a Bluetooth adapter. For API level 18 and above, get a
             * reference to BluetoothAdapter through BluetoothManager.
             */
            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAPI.mBluetoothAdapter = bluetoothManager.getAdapter();

            /* Checks if Bluetooth is supported on the device. */
            if (mBluetoothAPI.mBluetoothAdapter == null) {
                Toast.makeText(this, R.string.error_bluetooth_not_supported,
                        Toast.LENGTH_SHORT).show();
            }
        }

        /*
         * Ensures Bluetooth is enabled on the device. If Bluetooth is not
         * currently enabled, fire an intent to display a dialog asking the user
         * to grant permission to enable it.
         */
        if (!mBluetoothAPI.mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            /* Request access coarse location permission. */
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_ACCESS_COARSE_LOCATION);

            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }

        if(!mReceiverManager.isReceiverRegistered(mReceiver_Bluetooth)){
            final IntentFilter intentFilter = new IntentFilter();

            /* Start to monitor bond state change */
            intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mReceiverManager.registerReceiver(mReceiver_Bluetooth, intentFilter);

        }

        if (mBluetoothAPI.mBluetoothReader == null) {
            Intent intent = new Intent(this, BleScanActivity.class);
            startActivityForResult(intent,REQUEST_SCAN_BT);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mReceiverManager.isReceiverRegistered(mReceiver_Bluetooth)){
            mReceiverManager.unregisterReceiver(mReceiver_Bluetooth);
        }
        mBluetoothAPI.disconnectReader();
        mBluetoothAPI.updateConnectionState(BluetoothReader.STATE_DISCONNECTED);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(mBluetoothAPI.AUTHENTICATION_KEY, mBluetoothAPI.mMasterKey);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mBluetoothAPI.mMasterKey = savedInstanceState.getString(mBluetoothAPI.AUTHENTICATION_KEY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent =new Intent(this,BorrowBook.class);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (requestCode == RESULT_OK) {
                    //Constants.isScanning=true;

                }else{
                    //ants.isScanning=false;
                }
                break;
            case REQUEST_SCAN_BT:
                if (resultCode == REQUEST_NOT_BT) {
                    //Constants.isScanning = false;
                    mBluetoothAPI.mConnectState = BluetoothReader.STATE_DISCONNECTED;
                    return;
                }
                mBluetoothAPI.mDeviceName = data.getStringExtra(EXTRAS_DEVICE_NAME);
                mBluetoothAPI.mDeviceAddress = data.getStringExtra(EXTRAS_DEVICE_ADDRESS);


                SetBluetoothProp(null);


                mBluetoothAPI.mGattCallback = new BluetoothReaderGattCallback();

                mBluetoothAPI.mGattCallback
                        .setOnConnectionStateChangeListener(new BluetoothReaderGattCallback.OnConnectionStateChangeListener() {

                            @Override
                            public void onConnectionStateChange(
                                    final BluetoothGatt gatt, final int state,
                                    final int newState) {

                                Log.d(TAG, "onConnectionStateChange : " + mBluetoothAPI.mConnectState);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d(TAG, "onConnectionStateChange2 : " + mBluetoothAPI.mConnectState);


                                        if (state != BluetoothGatt.GATT_SUCCESS) {
                                            /*
                                             * Show the message on fail to
                                             * connect/disconnect.
                                             */
                                            mBluetoothAPI.mConnectState = BluetoothReader.STATE_DISCONNECTED;
                                            Log.d(TAG, "setOnConnectionStateChangeListener : STATE_DISCONNECTED");
                                            SetBluetoothProp(null);
                                            invalidateOptionsMenu();
                                            return;
                                        }

                                        mBluetoothAPI.updateConnectionState(newState);

                                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                                            /* Detect the connected reader. */
                                            if (mBluetoothAPI.mBluetoothReaderManager != null) {
                                                mBluetoothAPI.mBluetoothReaderManager.detectReader(
                                                        gatt, mBluetoothAPI.mGattCallback);
                                            }
                                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                                            mBluetoothAPI.mBluetoothReader = null;
                                            /*
                                             * Release resources occupied by Bluetooth
                                             * GATT client.
                                             */
                                            if (mBluetoothAPI.mBluetoothGatt != null) {
                                                mBluetoothAPI.mBluetoothGatt.close();
                                                mBluetoothAPI.mBluetoothGatt = null;
                                            }
                                        }
                                    }
                                });
                            }
                        });

                /* Initialize mBluetoothReaderManager. */
                mBluetoothAPI.mBluetoothReaderManager = new BluetoothReaderManager();

                /* Register BluetoothReaderManager's listeners */
                mBluetoothAPI.mBluetoothReaderManager
                        .setOnReaderDetectionListener(new BluetoothReaderManager.OnReaderDetectionListener() {

                            @Override
                            public void onReaderDetection(BluetoothReader reader) {
                                SetBluetoothProp(reader);
                                if (reader instanceof Acr3901us1Reader) {
                                    /* The connected reader is ACR3901U-S1 reader. */
                                    Log.v(TAG, "On Acr3901us1Reader Detected.");
                                } else if (reader instanceof Acr1255uj1Reader) {
                                    /* The connected reader is ACR1255U-J1 reader. */
                                    Log.v(TAG, "On Acr1255uj1Reader Detected.");



                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(MainActivity.this,
                                                    "The device is not supported!",
                                                    Toast.LENGTH_SHORT).show();

                                            /* Disconnect Bluetooth reader */
                                            Log.v(TAG, "Disconnect reader!!!");
                                            mBluetoothAPI.disconnectReader();
                                            mBluetoothAPI.updateConnectionState(BluetoothReader.STATE_DISCONNECTED);
                                        }
                                    });
                                    return;
                                }

                                mBluetoothAPI.mBluetoothReader = reader;
                                setListener(reader);
                                activateReader(reader);
                            }
                        });

                /* Connect the reader. */
                mBluetoothAPI.connectReader();
                break;
            case REQUEST_NOT_BT:
                mBluetoothAPI.mConnectState = BluetoothReader.STATE_DISCONNECTED;
                break;
        }
    }

    private final BroadcastReceiver mReceiver_Bluetooth = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            BluetoothAdapter bluetoothAdapter = null;
            BluetoothManager bluetoothManager = null;
            final String action = intent.getAction();

            if (!(mBluetoothAPI.mBluetoothReader instanceof Acr3901us1Reader)) {
                /* Only ACR3901U-S1 require bonding. */
                return;
            }

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                Log.i(TAG, "ACTION_BOND_STATE_CHANGED");

                /* Get bond (pairing) state */
                if (mBluetoothAPI.mBluetoothReaderManager == null) {
                    Log.w(TAG, "Unable to initialize BluetoothReaderManager.");
                    return;
                }

                bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                if (bluetoothManager == null) {
                    Log.w(TAG, "Unable to initialize BluetoothManager.");
                    return;
                }

                bluetoothAdapter = bluetoothManager.getAdapter();
                if (bluetoothAdapter == null) {
                    Log.w(TAG, "Unable to initialize BluetoothAdapter.");
                    return;
                }

                final BluetoothDevice device = bluetoothAdapter
                        .getRemoteDevice(mBluetoothAPI.mDeviceAddress);

                if (device == null) {
                    return;
                }

                final int bondState = device.getBondState();

                /* Enable notification */
                if (bondState == BluetoothDevice.BOND_BONDED) {
                    if (mBluetoothAPI.mBluetoothReader != null) {
                        mBluetoothAPI.mBluetoothReader.enableNotification(true);
                    }
                }


                /*
                 * Update bond status and show in the connection status field.
                 */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }

    };

    /*
     * Update listener
     */
    public void setListener(final BluetoothReader reader) {

        /* Update status change listener */
        if (reader instanceof Acr3901us1Reader) {
            ((Acr3901us1Reader) reader)
                    .setOnBatteryStatusChangeListener(new Acr3901us1Reader.OnBatteryStatusChangeListener() {

                        @Override
                        public void onBatteryStatusChange(
                                BluetoothReader bluetoothReader,
                                final int batteryStatus) {

                            Log.i(TAG, "mBatteryStatusListener data: "
                                    + batteryStatus);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }

                    });
        } else if (reader instanceof Acr1255uj1Reader) {
            ((Acr1255uj1Reader) reader)
                    .setOnBatteryLevelChangeListener(new Acr1255uj1Reader.OnBatteryLevelChangeListener() {

                        @Override
                        public void onBatteryLevelChange(
                                BluetoothReader bluetoothReader,
                                final int batteryLevel) {

                            Log.i(TAG, "mBatteryLevelListener data: "
                                    + batteryLevel);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                }
                            });
                        }

                    });
        }
        reader
                .setOnCardStatusChangeListener(new BluetoothReader.OnCardStatusChangeListener() {

                    @Override
                    public void onCardStatusChange(BluetoothReader bluetoothReader, final int sta) {
                        Log.i(TAG, "mCardStatusListener sta: " + sta);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (sta == BluetoothReader.CARD_STATUS_PRESENT  ){
                                    Log.i(TAG, "mCardStatusListener CARD_STATUS_PRESENT");
                                    byte apduCommand[] = Utils.getStringToHexBytes(mBluetoothAPI.DEFAULT_1255_APDU_COMMAND);
                                    if (apduCommand != null && apduCommand.length > 0) {
                                        /* Transmit APDU command. */
                                        if (!reader.transmitApdu(apduCommand)) {
                                            Toast.makeText(MainActivity.this,R.string.card_reader_not_ready, Toast.LENGTH_LONG).show();
                                        }else{
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this,"Character format error!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                    }

                });

        /* Wait for authentication completed. */
        reader
                .setOnAuthenticationCompleteListener(new BluetoothReader.OnAuthenticationCompleteListener() {

                    @Override
                    public void onAuthenticationComplete(
                            BluetoothReader bluetoothReader, final int errorCode) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (errorCode == BluetoothReader.ERROR_SUCCESS) {
                                    Log.d(TAG,"Authentication Success!");

                                    if (!reader.transmitEscapeCommand(mBluetoothAPI.AUTO_POLLING_START)) {
                                        Log.d(TAG, "polling failed");
                                    }else{
                                        //Constants.isScanning = false;
                                        Toast.makeText(MainActivity.this,"NFC (Bluetooth) Connected",Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Log.d(TAG,"Authentication Failed!");
                                }
                            }
                        });
                    }

                });

        /* Wait for receiving ATR string. */
        reader
                .setOnAtrAvailableListener(new BluetoothReader.OnAtrAvailableListener() {

                    @Override
                    public void onAtrAvailable(BluetoothReader bluetoothReader,
                                               final byte[] atr, final int errorCode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (atr == null) {
                                    Log.d(TAG,mBluetoothAPI.getErrorString(errorCode));
                                } else {
                                    Log.d(TAG,"setOnAtrAvailableListener");
                                    Log.d(TAG,Utils.toHexString(atr));
                                }
                            }
                        });
                    }

                });

        /* Wait for power off response. */
        reader
                .setOnCardPowerOffCompleteListener(new BluetoothReader.OnCardPowerOffCompleteListener() {

                    @Override
                    public void onCardPowerOffComplete(
                            BluetoothReader bluetoothReader, final int result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG,"setOnCardPowerOffCompleteListener");
                                Log.d(TAG,mBluetoothAPI.getErrorString(result));
                            }
                        });
                    }

                });
        final Intent intent =new Intent(this,BorrowBook.class);
        final Intent intent2 =getIntent();
        //태그값가져오기
        /* Wait for response APDU. */
        reader
                .setOnResponseApduAvailableListener(new BluetoothReader.OnResponseApduAvailableListener() {

                    @Override
                    public void onResponseApduAvailable(

                            BluetoothReader bluetoothReader, final byte[] apdu,
                            final int errorCode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG,"setOnResponseApduAvailableListener");
                                String strTagNum = getResponseString(apdu, errorCode);
                                //리더기 인식
                                Toast.makeText(getApplicationContext(),strTagNum,Toast.LENGTH_LONG).show();
                                Log.d(TAG,getResponseString(apdu, errorCode));
                                String userid = intent2.getStringExtra("id");
                                String bookname = ".";
                                String bookcode = ".";
                                intent.putExtra("name",bookname);
                                intent.putExtra("code",bookcode);
                                intent.putExtra("Tag_value",strTagNum);
                                intent.putExtra("id",userid);

                                startActivity(intent);

                            }

                        });

                    }

                });

        /* Wait for escape command response. */
        reader
                .setOnEscapeResponseAvailableListener(new BluetoothReader.OnEscapeResponseAvailableListener() {

                    @Override
                    public void onEscapeResponseAvailable(
                            BluetoothReader bluetoothReader,
                            final byte[] response, final int errorCode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG,"setOnEscapeResponseAvailableListener");
                                Log.d(TAG,getResponseString(response, errorCode));
                            }
                        });
                    }

                });

        /* Wait for device info available. */
        reader
                .setOnDeviceInfoAvailableListener(new BluetoothReader.OnDeviceInfoAvailableListener() {

                    @Override
                    public void onDeviceInfoAvailable(
                            BluetoothReader bluetoothReader, final int infoId,
                            final Object o, final int status) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (status != BluetoothGatt.GATT_SUCCESS) {
                                    Toast.makeText(MainActivity.this,
                                            "Failed to read device info!",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                switch (infoId) {
                                    case BluetoothReader.DEVICE_INFO_SYSTEM_ID: {
                                    }
                                    break;
                                    case BluetoothReader.DEVICE_INFO_MODEL_NUMBER_STRING:
                                        break;
                                    case BluetoothReader.DEVICE_INFO_SERIAL_NUMBER_STRING:
                                        break;
                                    case BluetoothReader.DEVICE_INFO_FIRMWARE_REVISION_STRING:
                                        break;
                                    case BluetoothReader.DEVICE_INFO_HARDWARE_REVISION_STRING:
                                        break;
                                    case BluetoothReader.DEVICE_INFO_MANUFACTURER_NAME_STRING:
                                        break;
                                    default:
                                        break;
                                }
                            }
                        });
                    }

                });

        /* Wait for battery level available. */
        if (reader instanceof Acr1255uj1Reader) {
            ((Acr1255uj1Reader) reader)
                    .setOnBatteryLevelAvailableListener(new Acr1255uj1Reader.OnBatteryLevelAvailableListener() {

                        @Override
                        public void onBatteryLevelAvailable(
                                BluetoothReader bluetoothReader,
                                final int batteryLevel, int status) {
                            Log.i(TAG, "mBatteryLevelListener data: "
                                    + batteryLevel);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG,mBluetoothAPI.getBatteryLevelString(batteryLevel));
                                }
                            });

                        }

                    });
        }

        /* Handle on battery status available. */
        if (reader instanceof Acr3901us1Reader) {
            ((Acr3901us1Reader) reader)
                    .setOnBatteryStatusAvailableListener(new Acr3901us1Reader.OnBatteryStatusAvailableListener() {

                        @Override
                        public void onBatteryStatusAvailable(
                                BluetoothReader bluetoothReader,
                                final int batteryStatus, int status) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG,mBluetoothAPI.getBatteryLevelString(batteryStatus));
                                }
                            });
                        }

                    });
        }

        /* Handle on slot status available. */
        reader
                .setOnCardStatusAvailableListener(new BluetoothReader.OnCardStatusAvailableListener() {

                    @Override
                    public void onCardStatusAvailable(
                            BluetoothReader bluetoothReader,
                            final int cardStatus, final int errorCode) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (errorCode != BluetoothReader.ERROR_SUCCESS) {
                                    Log.d(TAG,"setOnCardStatusAvailableListener : error");
                                    Log.d(TAG,mBluetoothAPI.getErrorString(errorCode));
                                } else {
                                    Log.d(TAG,mBluetoothAPI.getCardStatusString(cardStatus));
                                    Log.d(TAG,"setOnCardStatusAvailableListener");
                                }
                            }
                        });
                    }

                });

        reader
                .setOnEnableNotificationCompleteListener(new BluetoothReader.OnEnableNotificationCompleteListener() {

                    @Override
                    public void onEnableNotificationComplete(
                            final BluetoothReader bluetoothReader, final int result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result != BluetoothGatt.GATT_SUCCESS) {
                                    /* Fail */
                                    Log.d(TAG, "The device is unable to set notification!");
                                } else {
                                    Log.d(TAG, "The device is ready to use!");

                                    byte masterKey[] = Utils.getStringToHexBytes(mBluetoothAPI.mMasterKey);

                                    if (masterKey != null && masterKey.length > 0) {
                                        /* Start authentication. */
                                        if (!reader.authenticate(masterKey)) {
                                            mBluetoothAPI.disconnectReader();
                                        } else {
                                            Log.d(TAG, "Authenticating...");
                                        }
                                    }
                                }
                            }
                        });
                    }

                });
    }

    private void activateReader(BluetoothReader reader) {
        if (reader == null) {
            return;
        }

        if (reader instanceof Acr3901us1Reader) {
            /* Start pairing to the reader. */
            ((Acr3901us1Reader) reader).startBonding();
        } else if (reader instanceof Acr1255uj1Reader) {
            /* Enable notification. */
            reader.enableNotification(true);
        }
    }

    public void SetBluetoothProp(final BluetoothReader bluetoothReader) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (bluetoothReader instanceof Acr1255uj1Reader) {
                    /* The connected reader is ACR1255U-J1 reader. */
                    if (mBluetoothAPI.mMasterKey.length() == 0) {
                        try {
                            mBluetoothAPI.mMasterKey = Utils
                                    .toHexString(mBluetoothAPI.DEFAULT_1255_MASTER_KEY
                                            .getBytes("UTF-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    mBluetoothAPI.mMasterKey = "";
                }
            }
        });
    }

    private String getResponseString(byte[] response, int errorCode) {
        if (errorCode == BluetoothReader.ERROR_SUCCESS) {
            if (response != null && response.length > 0) {
                return Utils.toHexString(response);
            }
            return "";
        }
        return mBluetoothAPI.getErrorString(errorCode);
    }
}
