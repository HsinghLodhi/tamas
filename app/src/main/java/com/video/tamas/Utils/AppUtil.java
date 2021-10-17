package com.video.tamas.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.LruCache;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


/**
 * Created by PRATHAMAESH on 30-Apr-18.
 */

public class AppUtil {
    private static DeviceResourceManager resourceManager = null;
    private static AppUtil mAppSingletonInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mContext;


    private AppUtil(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();


        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized AppUtil getInstance(Context context) {
        if (mAppSingletonInstance == null) {
            mAppSingletonInstance = new AppUtil(context);
        }
        return mAppSingletonInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one mlm.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setRetryPolicy(new DefaultRetryPolicy(
                3000000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueue().add(req);
    }


    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static void doMlmLogout(Activity activity) {
        if (activity != null) {
            resourceManager = new DeviceResourceManager(activity);
        } else {
            return;
        }

        activity.finish();
    }

    public static void forceUpdate(Activity activity) {
        PackageManager packageManager = activity.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(activity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = null;
        if (packageInfo != null) {
            currentVersion = packageInfo.versionName;
        }
        //new VersionChecker(currentVersion, activity).execute();
    }

    public static void shareVideo(String title, String path) {

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("video/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        String msg = "https://play.google.com/store/apps/details?id=" + mContext.getPackageName() + "\nफिल्म इन्डस्ट्री में शुरूवात करें\n" +
                "घर बैठे अपना टैलेंट (एक्टिंग,डांसिंग,सिंगिंग आदि ) Tamas App>Talent\n" +
                "कैटेगरी में ऑडिशन विडियो अपलोड करके प्रोडूसर, डायरेक्टर, आदि को दिखाएं और फिल्मों में काम पाऐं .\n" +
                "घर बैठे ही Tamas App>Entertainment कैटेगरी में शार्ट वीडियो अपलोड करें  और अपने फॉलोवर्स बढ़ाऐं और मज़े करते हुए पैसे भी बनाऐं\n" +
                "\n" +
                "तो अब आपको बिना स्ट्रगल किये फिल्मों में काम भी मिलेगा, फेमस भी होंगे और पैसे भी बनाएँगे\n" +
                "\uD83D\uDC47\uD83C\uDFFBनीचे दिए गए लिंक से ‘Tamas App’ अभी डाउनलोड करें\n";
        shareIntent.putExtra(
                Intent.EXTRA_TEXT, msg +
                        "https://play.google.com/store/apps/details?id=" + mContext.getPackageName());
        shareIntent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        mContext.startActivity(Intent.createChooser(shareIntent, "Share"));
    }
}

