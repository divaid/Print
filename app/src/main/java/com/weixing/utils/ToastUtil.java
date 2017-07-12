package com.weixing.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;
import com.weixing.application.PrintApplication;

/**
 * Created by wb-wangkun.n on 2015/9/22.
 */
public class ToastUtil {

    private static Handler mHandler = new Handler();

    private static Toast mToast = null;

    private static Runnable r = new Runnable() {
        public void run() {
            mToast.cancel();
        }
    };

    public static void showToast(Context mContext, String text) {
        if (mContext == null) {
            return;
        }
        mHandler.removeCallbacks(r);
        if (mToast != null)
            mToast.setText(text);
        else
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public static void showLong(Context mContext, String msg) {
        showToast(mContext, msg);
    }

    public static void showLong(String msg) {
        showToast(PrintApplication.getInstance(), msg);
    }

}
