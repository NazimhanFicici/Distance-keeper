package com.example.deneme;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;

public class Scanner_BTLE {

    private MainActivity ma;

    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private long scanPeriod;
    private int signalStrength;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public Scanner_BTLE(MainActivity mainActivity, long scanPeriod, int signalStrength) {
        ma = mainActivity;

        mHandler = new Handler();

        this.scanPeriod = scanPeriod;
        this.signalStrength = signalStrength;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) ma.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public boolean isScanning() {
        return mScanning;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void start() {
        if (!Utils.checkBluetooth(mBluetoothAdapter)) {
            Utils.requestUserBluetooth(ma);
            ma.stopScan();
        }
        else {
            scanLeDevice(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void stop() {
        scanLeDevice(false);
    }

    // This is called when the scan button is active
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void scanLeDevice(final boolean enable) {
        if (enable && !mScanning) {
            Utils.toast(ma.getApplicationContext(), "Starting BLE scan...");

            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
                @Override
                public void run() {
                    Utils.toast(ma.getApplicationContext(), "Stopping BLE scan...");

                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);

                    ma.stopScan();
                }
            }, scanPeriod);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
        else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    // Device scan callback.
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

                    final int new_rssi = rssi;
                    if (rssi > signalStrength) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ma.addDevice(device, new_rssi);
                            }
                        });
                    }
                }
            };
}