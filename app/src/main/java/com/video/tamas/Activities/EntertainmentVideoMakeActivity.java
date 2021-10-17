package com.video.tamas.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.video.tamas.R;


public class EntertainmentVideoMakeActivity extends EntertainmentCameraActivity {

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, EntertainmentVideoMakeActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portrate);
        onCreateActivity();
        videoWidth = 720;
        videoHeight = 1280;
        cameraWidth = 1280;
        cameraHeight = 720;
    }
}
