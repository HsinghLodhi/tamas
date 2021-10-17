package com.video.tamas.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by CHETAN on 3/25/2017.
 */

public class DeviceResourceManager {

    @SuppressWarnings("unused")
    private static final String LOG_TAG = "DeviceResourceManager";
    private Context mContext = null;
    private SharedPreferences mPref = null;
    private Editor mEditor = null;

    public DeviceResourceManager(Context context) {
        mContext = context;
    }

    public String addToSharedPref(String prefName, String data) {
        mPref = mContext.getSharedPreferences(prefName, getSharedPreferenceMode());
        mEditor = mPref.edit();
        mEditor.putString(prefName, data);
        mEditor.commit();
        return prefName;
    }

    public void addToSharedPref(String prefName, int data) {
        mPref = mContext.getSharedPreferences(prefName, getSharedPreferenceMode());
        mEditor = mPref.edit();
        mEditor.putInt(prefName, data);
        mEditor.commit();
    }


    public void addToSharedPref(Context ctx, String prefName, int data) {
        mPref = ctx.getSharedPreferences(prefName, getSharedPreferenceMode());
        mEditor = mPref.edit();
        mEditor.putInt(prefName, data);
        mEditor.commit();
    }


    public void addToSharedPref(String prefName, boolean data) {
        mPref = mContext.getSharedPreferences(prefName, getSharedPreferenceMode());
        mEditor = mPref.edit();
        mEditor.putBoolean(prefName, data);
        mEditor.commit();
    }


    public void addToSharedPref(long data, String prefName) {
        mPref = mContext.getSharedPreferences(prefName, getSharedPreferenceMode());
        mEditor = mPref.edit();
        mEditor.putLong(prefName, data);
        mEditor.commit();
    }


    public void clearSharedPref(String prefName) {
        mPref = mContext.getSharedPreferences(prefName, getSharedPreferenceMode());
        mEditor = mPref.edit();
        mEditor.remove(prefName);
        mEditor.commit();
    }

    public void clearSharedPref(Context ctx, String prefName) {
        mPref = ctx.getSharedPreferences(prefName, getSharedPreferenceMode());
        mEditor = mPref.edit();
        mEditor.remove(prefName);
        mEditor.commit();
    }


    public String getDataFromSharedPref(String prefName) {
        mPref = mContext.getSharedPreferences(prefName, getSharedPreferenceMode());
        return mPref.getString(prefName, null);
    }


    public String getDataFromSharedPref(String prefName, String defaultValue) {
        mPref = mContext.getSharedPreferences(prefName, getSharedPreferenceMode());
        return mPref.getString(prefName, defaultValue);
    }


    public int getDataFromSharedPref(String prefName, int defaultValue) {
        mPref = mContext.getSharedPreferences(prefName, getSharedPreferenceMode());
        return mPref.getInt(prefName, defaultValue);
    }

    public int getDataFromSharedPref(Context ctx, String prefName, int defaultValue) {
        mPref = ctx.getSharedPreferences(prefName, getSharedPreferenceMode());
        return mPref.getInt(prefName, defaultValue);
    }

    public boolean getDataFromSharedPref(String prefName, boolean defaultValue) {
        mPref = mContext.getSharedPreferences(prefName, getSharedPreferenceMode());
        return mPref.getBoolean(prefName, defaultValue);
    }

    public long getDataFromSharedPref(long defaultValue, String prefName) {
        mPref = mContext.getSharedPreferences(prefName, getSharedPreferenceMode());
        return mPref.getLong(prefName, defaultValue);
    }

    private int getSharedPreferenceMode() {
        return Context.MODE_PRIVATE;
    }
}
