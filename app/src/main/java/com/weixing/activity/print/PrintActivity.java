package com.weixing.activity.print;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.weixing.activity.base.BaseActivity;
import com.weixing.application.PrintApplication;
import com.weixing.dialog.PrintSetDialog;
import com.weixing.print.R;
import com.weixing.print.manager.PrinterManager;
import com.weixing.utils.CreateCodeBitmap;
import com.weixing.utils.ToastUtil;

import java.io.IOException;
import java.util.regex.Pattern;

//TODO 添加设置条形码或者二维码的设置与直接打印文本与码的设置
public class PrintActivity extends BaseActivity implements View.OnClickListener {
    private PrintSetDialog mSetDialog;

    private EditText mEditText;
    private ImageView mContentBitmap;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("", "");
        }
    };
    private PrinterManager printerManager;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        initView();

        mHandler.sendEmptyMessageDelayed(0, 10000);
    }

    private void initView() {
        mEditText = (EditText) findViewById(R.id.print_ed);
        mContentBitmap = (ImageView) findViewById(R.id.print_content_img);
        findViewById(R.id.print_setting).setOnClickListener(this);
        findViewById(R.id.print).setOnClickListener(this);
        findViewById(R.id.printqr).setOnClickListener(this);

//        mEditText.addRightIcon(R.drawable.icon_scan, new LabelEditText.OnIconClickListener() {
//            @Override
//            public void onIconClick() {
//                ToastUtil.showLong("to scan");
//            }
//        });
//
//        mEditText.addRightIcon(R.drawable.icon_cancel, new LabelEditText.OnIconClickListener() {
//            @Override
//            public void onIconClick() {
//                mEditText.setText("");
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.print_setting:
                if (mSetDialog == null) {
                    mSetDialog = new PrintSetDialog(this);
                    mSetDialog.init();
                }
                mSetDialog.settingBTPrinter();
                break;
            case R.id.print:
                String contents = mEditText.getText().toString();
                if (TextUtils.isEmpty(contents)) {
                    ToastUtil.showLong("请输入打印内容");
                    return;
                }
                mContentBitmap.setImageBitmap(CreateCodeBitmap.creatBarcode(this, contents, 200, 50, false));
                if (PrintApplication.getInstance().currentPrinterAddress == null) {
                    alertSettingPrinterBeforeDialog();
                    return;
                }

                if (printBar(contents)) {
                    refreshView();
                }

                break;
            case R.id.printqr:
                String msg = mEditText.getText().toString();
                if (TextUtils.isEmpty(msg)) {
                    ToastUtil.showLong("请输入打印内容");
                    return;
                }

//                mContentBitmap.setImageBitmap(CreateCodeBitmap.createQRImage(this, msg, 200, 50, false));

                if (PrintApplication.getInstance().currentPrinterAddress == null) {
                    alertSettingPrinterBeforeDialog();
                    return;
                }

                if(printQr(msg)){
                    refreshView();
                }
                break;
        }
    }

    private void alertSettingPrinterBeforeDialog() {
        final AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflate = getLayoutInflater().inflate(R.layout.dialog_prompt_print, null);
        builder.setView(inflate);
        TextView msgTV = (TextView) inflate.findViewById(R.id.tv_msg);
        msgTV.setText("请先设置打印机");
        Button nativeBtn = (Button) inflate.findViewById(R.id.dialog_print_native_btn);
        dialog = builder.create();

        nativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (mSetDialog == null) {
                    mSetDialog = new PrintSetDialog(PrintActivity.this);
                    mSetDialog.init();
                }
                mSetDialog.settingBTPrinter();
            }
        });

        dialog.show();

    }

    private void refreshView() {
        String text = mEditText.getText().toString();
        if(TextUtils.isEmpty(text) || Pattern.compile("[a-zA-Z]").matcher(text).find()){
            return;
        }
        mEditText.setText("");
    }

    private boolean printBar(final String msg) {
        if(printerManager == null) {
            printerManager = new PrinterManager(this);
        }

        if (!printerManager.init()) {
            return false;
        }

//        final Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.icon_cancel);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    printerManager.printBarCode(PrintActivity.this, msg);
//                    printerManager.printImage(image);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
        return true;
    }

    private boolean printQr(final String msg){
        if(printerManager == null) {
            printerManager = new PrinterManager(this);
        }

        if (!printerManager.init()) {
            return false;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    printerManager.printQR(msg);
//                    printerManager.printImage(image);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
        return true;
    }

    @Override
    public void onBlueToothOpen() {
        super.onBlueToothOpen();
        mSetDialog.onUserAccess();
    }
}
