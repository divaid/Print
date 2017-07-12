package com.weixing.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
//        BaseActivity activity = (BaseActivity) context;
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager mConnectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = mConnectivity.getActiveNetworkInfo();
            //手机无网络
            if (activeNetworkInfo == null) {
//                activity.onDisConnect();
            } else {
//                activity.onConnect();
            }
        }
    }
}
