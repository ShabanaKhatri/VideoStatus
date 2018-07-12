package com.videoStatus.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.videoStatus.R;
import com.videoStatus.databinding.ActivitySplashBinding;
import com.videoStatus.retrofit.AndyUtils;
import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private Thread thread;
    private Intent mainIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        if(AndyUtils.isNetworkAvailable(this)){
            threadStart();
        }else {
            AndyUtils.DialogForNoInternetConnection(this);
        }
    }

    private void threadStart() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                } catch (InterruptedException e) {
                    finish();
                }
            }

        });
        thread.start();
    }
}
