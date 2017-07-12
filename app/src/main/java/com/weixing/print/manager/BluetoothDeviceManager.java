package com.weixing.print.manager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import com.weixing.application.PrintApplication;
import com.weixing.print.printer.JQPrinter;
import com.weixing.print.printer.Printer_define;
import com.weixing.print.utils.PrintUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wb-cuiweixing on 2015/10/13.
 * 手持设备蓝牙管理
 */
public class BluetoothDeviceManager {

    private BluetoothAdapter mbtAdapter = null;
    private boolean mBtOpenSilent = true; //是否静默连接
    private Context mContext;
    public static final int PRINTINGEQUIPMENT = 1536;//打印机蓝牙
    private Activity mActivity;

    public BluetoothDeviceManager(Context context) {
        this.mContext = context;
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        }
    }

//    /**
//     * 开启手持设备蓝牙
//     */
//    public BluetoothAdapter openBlueTooth() {
//        mbtAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (mbtAdapter == null) {
//            ToastUtil.showLong(mContext, "本机没有找到蓝牙硬件或驱动！");
//            return null;
//        }
//        PrintApplication.getInstance().btAdapter = mbtAdapter;
//        // 如果本地蓝牙没有开启，则开启
//        if (!PrintApplication.getInstance().btAdapter.isEnabled()) {
//            PrintUtils.log("正在开启蓝牙");
//            if (!mBtOpenSilent) {
//                // 我们通过startActivityForResult()方法发起的Intent将会在onActivityResult()回调方法中获取用户的选择，比如用户单击了Yes开启，
//                // 那么将会收到RESULT_OK的结果，
//                // 如果RESULT_CANCELED则代表用户不愿意开启蓝牙
////                Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
////                startActivityForResult(mIntent,REQUEST_BT_ENABLE );
//            } else {
//                //静默开启
//                PrintApplication.getInstance().btAdapter.enable();
//                while (PrintApplication.getInstance().btAdapter.getState() != BluetoothAdapter.STATE_ON) ;
//                PrintUtils.log("本地蓝牙已打开");
//            }
//        } else {
//            PrintUtils.log("蓝牙已经开启");
//        }
//
//        return PrintApplication.getInstance().btAdapter;
//    }

    /**
     * 连接蓝牙设备
     */

    public boolean connectOtherBlueTooth(String btDeviceInfo) {
        if (PrintApplication.getInstance().btAdapter.isDiscovering()) {
            PrintApplication.getInstance().btAdapter.cancelDiscovery();
        }

        if (btDeviceInfo == null) {
            return false;
        }
//        String btName = btDeviceInfo.split("\n")[0];
        String btAddress = btDeviceInfo.substring(btDeviceInfo.length() - 17);

        // TODO: 2016/11/2 为什么使用硬编码设置打印机类型？？？
        if (PrintApplication.getInstance().printer == null) {//打包#2
            PrintApplication.getInstance().printer = new JQPrinter(Printer_define.PRINTER_MODEL.JLP351_IC);
        }
        if (PrintApplication.getInstance().printer != null) {
            if (PrintApplication.getInstance().printer.isOpen && !PrintApplication.getInstance().printer.close()) {
                return false;
            }
            if (!TextUtils.isEmpty(btAddress) && !PrintApplication.getInstance().printer.open(btAddress) && mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HandlerUtils.handle(mActivity, PrintExceptionState.PORTNOOPEN);
                    }
                });
                return false;
            }
            if (!PrintApplication.getInstance().printer.wakeUp() && mActivity != null) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HandlerUtils.handle(mActivity, PrintExceptionState.NOWARKUP);
                    }
                });
                return false;
            }
        }

        PrintApplication.getInstance().currentPrinterAddress = btAddress;
        PrintApplication.getInstance().currentPrinterInfo = btDeviceInfo;
        if (mActivity != null) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    HandlerUtils.handle(mActivity, PrintExceptionState.ENABLEPRINT);
                }
            });
        }
        return true;
    }

    /**
     * 获取已绑定的打印设备
     */
    public Set<BluetoothDevice> getPairedPrinterDevices() {
        Set<BluetoothDevice> mPairedDevices = new HashSet<BluetoothDevice>();
        if (PrintApplication.getInstance().btAdapter.enable()) {
            Set<BluetoothDevice> pairedDevices = PrintApplication.getInstance().btAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if ((device.getBluetoothClass().getMajorDeviceClass() == PRINTINGEQUIPMENT)) {
                        mPairedDevices.add(device);
                    }
                }
                return mPairedDevices;
            }
        }
        return null;
    }

    /**
     * 取消所有已绑定的设备
     */
    public void cancelAllPairedDevices() {
        if (PrintApplication.getInstance().btAdapter != null && PrintApplication.getInstance().btAdapter.isEnabled()) {
            Set<BluetoothDevice> btAdapterBondedDevices = PrintApplication.getInstance().btAdapter.getBondedDevices();
            if (btAdapterBondedDevices.size() > 0) {
                for (BluetoothDevice btAdapterBondedDevice : btAdapterBondedDevices) {
                    cancelPaired(btAdapterBondedDevice.getAddress());
                }
            }
        }
        return;
    }
    /**
     * 取消除当前绑定的所有已绑定的设备
     */
   /* public void cancelOtherPairedDevices(){
        RFCacheUtils.myLog("BDM:cancelOtherPairedDevices()");
        if(PrintApplication.getInstance().btAdapter != null && PrintApplication.getInstance().btAdapter.enable()){
            Set<BluetoothDevice> btAdapterBondedDevices = PrintApplication.getInstance().btAdapter.getBondedDevices();
            if(btAdapterBondedDevices.size()>0){
                for (BluetoothDevice btAdapterBondedDevice : btAdapterBondedDevices) {
                    if(btAdapterBondedDevice.getAddress().equals(PrintApplication.getInstance().currentPrinterAddress)){
                        continue;
                    }
                    cancelPaired(btAdapterBondedDevice.getAddress());
                }
            }
        }
        return ;
    }*/

    /**
     * 取消匹配
     */
    public void cancelPaired(String btAddress) {
        try {
            BluetoothDevice device = PrintApplication.getInstance().btAdapter.getRemoteDevice(btAddress);
            Method CancelPairedMethod = device.getClass().getMethod("removeBond");
            CancelPairedMethod.invoke(device);
            while (device.getBondState() == BluetoothDevice.BOND_BONDED) ;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取可见设备但未绑定过
     */

    public void startDiscory() {
//        if (!PrintApplication.getInstance().btAdapter.isEnabled()) {
//            PrintApplication.getInstance().btAdapter.enable();
//            while (PrintApplication.getInstance().btAdapter.getState() != BluetoothAdapter.STATE_ON) ;
//        }

        //搜索设备
        if (PrintApplication.getInstance().btAdapter.isDiscovering()) {
            PrintApplication.getInstance().btAdapter.cancelDiscovery();
        }
        PrintApplication.getInstance().btAdapter.startDiscovery();
        return;
    }


    /**
     * 关闭手持设备蓝牙
     */
    public boolean closeBlueTooth() {
        PrintApplication.getInstance().currentPrinterAddress = null;
        PrintApplication.getInstance().currentPrinterInfo = null;
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (PrintApplication.getInstance().printer != null) {
            if (!PrintApplication.getInstance().printer.close()) {

                PrintUtils.log("打印机关闭失败");
            } else {

                PrintUtils.log("打印机关闭成功");
            }
        }
        //清空配对记录
        cancelAllPairedDevices();

        if (btAdapter != null) {
            if (btAdapter.isEnabled()) {
                btAdapter.disable();
            }
        }
        PrintApplication.getInstance().btManager = null;
        return false;

    }
}


