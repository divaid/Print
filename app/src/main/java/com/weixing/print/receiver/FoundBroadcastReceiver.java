package com.weixing.print.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.weixing.print.inter.OnScanFinishListener;
import com.weixing.print.inter.OnScanListener;

import java.util.HashSet;

/**
 * Created by wb-cuiweixing on 2015/11/13.
 */
public class FoundBroadcastReceiver extends BroadcastReceiver {
    public static final int PRINTINGEQUIPMENT = 1536;//打印机蓝牙
    HashSet<BluetoothDevice> mAvailableDevices = new HashSet<BluetoothDevice>();
    OnScanListener mOnScanListener;
    OnScanFinishListener mOnScanFinishListener;
    public void setOnScanListener(OnScanListener listener) {
        this.mOnScanListener = listener;
    }

    public void setOnScanFinishListener(OnScanFinishListener mOnScanFinishListener) {
        this.mOnScanFinishListener = mOnScanFinishListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if(device.getBluetoothClass().getMajorDeviceClass() == PRINTINGEQUIPMENT){
                mAvailableDevices.add(device);
                if (mOnScanListener != null) {
                    mOnScanListener.onScan(mAvailableDevices);
                }
                Log.e("susei", "susei蓝牙可见设备：" + device.getName() + device.getAddress());
            }

        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            if(mOnScanFinishListener != null){
                mOnScanFinishListener.onScanFinish();
            }

            if (mAvailableDevices.size() == 0) {
                Log.e("susei", "susei蓝牙可见设备：0个");
            }
            mAvailableDevices.clear();
        }
    }
}
