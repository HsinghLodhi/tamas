package com.video.tamas.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.video.tamas.R;
import com.video.tamas.Utils.Config;
import com.video.tamas.Utils.DeviceResourceManager;
import com.video.tamas.Utils.LocaleManager;


public class BaseActivity extends AppCompatActivity {
    String selectedLanguage;
    DeviceResourceManager resourceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resourceManager = new DeviceResourceManager(this);
        selectedLanguage = resourceManager.getDataFromSharedPref(Config.AppLanguage, "");
        selectedLanguage = LocaleManager.getLanguage(BaseActivity.this);
        Log.wtf("SelectedLanguage", selectedLanguage);
        LocaleManager.setLocale(BaseActivity.this, selectedLanguage);
        setContentView(R.layout.activity_base);
    }
}
