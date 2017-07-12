package com.weixing.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.weixing.application.PrintApplication;
import com.weixing.print.R;
import com.weixing.print.adapter.AvailableAdapter;
import com.weixing.print.inter.OnScanFinishListener;
import com.weixing.print.inter.OnScanListener;
import com.weixing.print.manager.BluetoothDeviceManager;
import com.weixing.print.printer.JQPrinter;
import com.weixing.print.printer.Printer_define;
import com.weixing.print.utils.PrintUtils;
import com.weixing.utils.ToastUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by wb-cuiweixing on 2016/12/8.
 */
public class PrintSetDialog {
    public static final int REQUEST_CODE_BLUETOOTH_ON = 2016;
    private static final int HIDEHINTINFO = 1;
    private AvailableAdapter mAvailableAdapter;
    private LinearLayout mSettingDialogView;
    private TextView mHintInfoTv;
    private AlertDialog mSettingDialog;
    private String mBtDeviceInfo;
    private String mWantConnBtAddress;
    private ListView mAvailableLV;
    private ArrayList mAvaibleList;
    private Handler mHandler;
    private Button mDialogSearchBt;
    public static final int PRINTINGEQUIPMENT = 1536;//打印机蓝牙
    private LinearLayout mDialogLl;

    private Activity mActivity;
    private int itemId;
    private OnPrinterConnectListener mListener;//打印机设置里的打印机连接成功回调；

    public void setOnPrinterConnectListener(OnPrinterConnectListener mListener) {
       this.mListener = mListener;
    }

    public PrintSetDialog(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void init() {
        // TODO: 2016/11/2 打印机类型硬编码？？？
        if (PrintApplication.getInstance().printer == null)
            PrintApplication.getInstance().printer = new JQPrinter(Printer_define.PRINTER_MODEL.JLP351_IC);

        if (PrintApplication.getInstance().btManager == null)
            PrintApplication.getInstance().btManager = new BluetoothDeviceManager(mActivity);

        if (PrintApplication.getInstance().btAdapter == null)
            PrintApplication.getInstance().btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (PrintApplication.getInstance().btAdapter == null) {
            PrintUtils.log("蓝牙打开失败，或者不支持蓝牙");
            return;
        }

    }

    /**
     * 申请打开蓝牙
     */
    public void requestBlueTooth() {
        // 请求打开 Bluetooth
        Intent requestBluetoothOn = new Intent(
                BluetoothAdapter.ACTION_REQUEST_ENABLE);
        // 请求开启 Bluetooth
        mActivity.startActivityForResult(requestBluetoothOn, REQUEST_CODE_BLUETOOTH_ON);
    }

    /**
     * 设置蓝牙
     */
    public void settingBTPrinter() {
        if (!PrintApplication.getInstance().btAdapter.isEnabled()) {
            requestBlueTooth();
        } else {
            onUserAccess();
        }
    }

    public void onUserAccess() {
        initHanler();
        alertSettingDialog();
    }

    private void initHanler() {
        mHandler = new Handler(PrintApplication.getInstance().getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HIDEHINTINFO:
                        mHintInfoTv.setVisibility(View.GONE);
                        break;
                }
            }
        };
    }

    /**
     * 蓝牙弹窗
     */
    private void alertSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        mSettingDialogView = (LinearLayout) mActivity.
                getLayoutInflater().inflate(R.layout.dialog_list_print_setting, null);
        mDialogLl = (LinearLayout) mSettingDialogView.findViewById(R.id.ll_dialog);
        builder.setView(mSettingDialogView);
        mDialogSearchBt = (Button) mSettingDialogView.findViewById(R.id.dialog_search);


        // 可见设备
        mAvailableLV = (ListView) mSettingDialogView.findViewById(R.id.dialog_listview_avaible);
        mAvaibleList = new ArrayList<String>();
        mAvailableAdapter = new AvailableAdapter(mAvaibleList, mActivity);
        mAvailableLV.setAdapter(mAvailableAdapter);

        //注册搜索广播
        registerFoundBrocastReceiver();

        //以防周边并无2台以上设备，无法触发FoundBroadcastReceiver  当前连接设备也能正常显示
        showAvailbaleDevices(null);

        mAvailableLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemId = view.getId();
                mAvailableLV.setEnabled(false);
                mDialogSearchBt.setEnabled(false);

                TextView pairedDeviceName = (TextView) view.findViewById(R.id.tv_pairedDevice);
                mHintInfoTv = (TextView) view.findViewById(R.id.tv_hintInfo);
                mHintInfoTv.setVisibility(View.VISIBLE);
                mBtDeviceInfo = pairedDeviceName.getText().toString();
                mWantConnBtAddress = mBtDeviceInfo.substring(mBtDeviceInfo.length() - 17);

                if (PrintApplication.getInstance().btAdapter.isDiscovering()) {
                    PrintApplication.getInstance().btAdapter.cancelDiscovery();
                }

                if (PrintApplication.getInstance().currentPrinterAddress != null && PrintApplication.getInstance().currentPrinterAddress.equals(mWantConnBtAddress)) {
                    ToastUtil.showLong(PrintApplication.getInstance(), "连接的就是当前设备");
                    mSettingDialog.dismiss();
                    return;
                }

                new Thread(new Runnable() {
                    Message msg = mHandler.obtainMessage();

                    @Override
                    public void run() {
                        //清空配对的记录
                        PrintApplication.getInstance().btManager.cancelAllPairedDevices();
                        PrintApplication.getInstance().currentPrinterAddress = null;
                        PrintApplication.getInstance().currentPrinterInfo = null;
                        boolean conn = PrintApplication.getInstance().btManager.connectOtherBlueTooth(mBtDeviceInfo);
                        if (conn && mListener != null) {
                            mListener.onConnect();
                        }
                        msg.what = HIDEHINTINFO;
                        mHandler.sendMessage(msg);
                        mSettingDialog.dismiss();
                    }
                }).start();
            }
        });

        //捕获实体返回键
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (PrintApplication.getInstance().btAdapter.isDiscovering()) {
                        PrintApplication.getInstance().btAdapter.cancelDiscovery();
                    }
                }
                return false;
            }
        });

        mDialogSearchBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog 搜索可见设备
                mDialogSearchBt.setClickable(false);
                mDialogSearchBt.setText("搜索中...");
                registerFoundBrocastReceiver();
            }
        });
        mDialogSearchBt.setClickable(false);
//        mSettingDialog.setCanceledOnTouchOutside(false);
        mSettingDialog = builder.show();


    }

    private void registerFoundBrocastReceiver() {
        if(PrintApplication.getInstance().foundBroadcastReceiver == null){
            PrintApplication.getInstance().initBluetooth();
        }
        PrintApplication.getInstance().foundBroadcastReceiver.setOnScanListener(new OnScanListener() {
            @Override
            public void onScan(HashSet<BluetoothDevice> devices) {
                showAvailbaleDevices(devices);
            }
        });

        PrintApplication.getInstance().foundBroadcastReceiver.setOnScanFinishListener(new OnScanFinishListener() {
            @Override
            public void onScanFinish() {
                if (!mDialogSearchBt.isClickable()) {
                    mDialogSearchBt.setClickable(true);
                }
                mDialogSearchBt.setText("搜索");
                mDialogSearchBt.setBackground(PrintApplication.getInstance().getResources().getDrawable(R.drawable.seletor_bottom_new));
                PrintUtils.log("搜索完成.。。。。。");
            }
        });
        PrintApplication.getInstance().btManager.startDiscory();
        PrintUtils.log("start discovery");
    }


    /**
     * 展示当前连接和可见打印设备
     *
     * @param mAvailableDevices
     */
    private void showAvailbaleDevices(HashSet<BluetoothDevice> mAvailableDevices) {
        if (mAvaibleList == null) {
            mAvaibleList = new ArrayList();
        }
        mAvaibleList.clear();
        //将当前配对的设备显示到第一条
        if (PrintApplication.getInstance() == null || PrintApplication.getInstance().btManager == null) { //打包#4
            return;
        }
        Set<BluetoothDevice> pairedPrinterDevices = PrintApplication.getInstance().btManager.getPairedPrinterDevices();
        if (pairedPrinterDevices != null && pairedPrinterDevices.size() > 0) {
            for (BluetoothDevice pairedPrinterDevice : pairedPrinterDevices) {
//                if (pairedPrinterDevice.getBluetoothClass().getMajorDeviceClass() == PRINTINGEQUIPMENT) {
                mAvaibleList.add(0, pairedPrinterDevice.getName() + "\n" + pairedPrinterDevice.getAddress());
                PrintUtils.log("getavailabledevices:paired device:" + pairedPrinterDevice.getAddress());
//                }
            }
        }
        if (mAvailableDevices != null && mAvailableDevices.size() > 0) {
            for (BluetoothDevice device : mAvailableDevices) {
                mAvaibleList.add(device.getName() + "\n" + device.getAddress());
//                mAvaibleList.add(mAvaibleList.size(),device.getName() + "\n" + device.getAddress());
                PrintUtils.log("getavailabledevices:unpaired device:" + device.getAddress());
            }
        }
        if (mAvailableAdapter == null) {
            mAvailableAdapter = new AvailableAdapter(mAvaibleList, mActivity);
        }
        mAvailableAdapter.notifyDataSetChanged();
    }

    //当打印机连接成功时调用
    public interface OnPrinterConnectListener {
        void onConnect();
    }

}
