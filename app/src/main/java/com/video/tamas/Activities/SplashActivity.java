package com.video.tamas.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.video.tamas.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    private long Delay = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Timer RunSplash = new Timer();
        TimerTask ShowSplash = new TimerTask() {
            @Override
            public void run() {
                SplashActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Intent myIntent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(myIntent);
                        overridePendingTransition(0, 0);
                        finish();

                    }
                });
            }
        };

        // Start the timer
        RunSplash.schedule(ShowSplash, Delay);
    }
    }

