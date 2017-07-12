package com.weixing.print.inter;

import android.bluetooth.BluetoothDevice;

import java.util.HashSet;

public interface OnScanListener {
    void onScan(HashSet<BluetoothDevice> devices);
}
