package com.weixing.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by wb-lrs192703 on 2016/10/27.
 */
public class ScreenStateReceiver extends BroadcastReceiver {
    private onScreenStateChangeListener mOnScreenStateChangedLsitener;

    public void registerScreenStateChangeListener(onScreenStateChangeListener listener) {
        mOnScreenStateChangedLsitener = listener;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            if (mOnScreenStateChangedLsitener != null) {
                mOnScreenStateChangedLsitener.onScreenOn();
            }
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            if (mOnScreenStateChangedLsitener != null) {
                mOnScreenStateChangedLsitener.onScerrnOff();
            }
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
            if (mOnScreenStateChangedLsitener != null) {
                mOnScreenStateChangedLsitener.onUserPresent();
            }
        }

    }

    public interface onScreenStateChangeListener {
        void onScreenOn(); //屏幕变量

        void onScerrnOff();//屏幕变暗，加锁。

        void onUserPresent();//解锁
    }

}
