package com.video.tamas.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.video.tamas.R;


public class TalentVideoMakeActivity extends TalentCameraActivity {

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, TalentVideoMakeActivity.class);
        activity.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talent_video_make);
        onCreateActivity();
        videoWidth = 720;
        videoHeight = 1280;
        cameraWidth = 1280;
        cameraHeight = 720;
    }
}
