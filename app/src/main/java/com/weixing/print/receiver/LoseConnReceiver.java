package com.weixing.print.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.weixing.application.PrintApplication;
import com.weixing.print.manager.HandlerUtils;
import com.weixing.print.manager.PrintExceptionState;
import com.weixing.print.printer.JQPrinter;

/**
 * Created by wb-cuiweixing on 2015/11/13.
 */
public class LoseConnReceiver extends BroadcastReceiver {

    private Context mContext;
    public LoseConnReceiver(Context context){
        mContext = context;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
       /* String action = intent.getAction();
        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            JQPrinter printer = RFApplication.getInstance().printer;
            if (printer != null && printer.isOpen)
                HandlerUtils.handle(mContext, PrintExceptionState.DISCONNECT);
        }
*/
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            //TODO 优博讯设备在app中会概率性接收其他无关设备的断连干扰 暂时注释
            BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            String currentPrinterAddress = PrintApplication.getInstance().currentPrinterAddress;
            if(btDevice.getAddress().equals(currentPrinterAddress)){
                JQPrinter printer = PrintApplication.getInstance().printer;
                if (printer != null && printer.isOpen)
                    HandlerUtils.handle(mContext, PrintExceptionState.DISCONNECT);
            }else{
            }
        }
    }
}
