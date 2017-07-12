package com.weixing.activity.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.weixing.activity.print.PrintActivity;
import com.weixing.print.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, PrintActivity.class));
            }
        },1000);
    }
}
