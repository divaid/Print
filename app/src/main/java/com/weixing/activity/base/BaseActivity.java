package com.weixing.activity.base;

import android.app.Activity;
import android.content.Intent;
import com.weixing.dialog.PrintSetDialog;
import com.weixing.utils.ToastUtil;

/**
 * Created by wb-cuiweixing on 2016/12/20.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PrintSetDialog.REQUEST_CODE_BLUETOOTH_ON) {
            if (resultCode == Activity.RESULT_OK) {
                onBlueToothOpen();
            } else {
                onBlueToothOpenRefuse();
            }
        }
    }

    /**
     * 用户拒绝打开了蓝牙
     */
    public void onBlueToothOpenRefuse() {
        ToastUtil.showLong("请打开蓝牙后，才能连接打印机");
    }

    /**
     * 用户打开了蓝牙
     */
    public void onBlueToothOpen() {

    }
}
