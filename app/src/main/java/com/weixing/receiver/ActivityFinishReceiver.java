package com.weixing.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by wb-lrs192703 on 2016/11/18.
 */
public class ActivityFinishReceiver extends BroadcastReceiver {

    public static final String ACTION_FINISH = "finish";

    @Override
    public void onReceive(Context context, Intent intent) {
        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;
            if (intent.getAction().equals(ACTION_FINISH)) {
                activity.finish();
            }
        }

    }


}
