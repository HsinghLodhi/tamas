package com.video.tamas.Activities;

import android.app.Application;
import android.os.Environment;

import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;

import java.io.File;

/**
 * Created by Dipendra Sharma  on 20-04-2019.
 */
public class MyApplication extends Application {
    //for exo player cache
    public LeastRecentlyUsedCacheEvictor cacheEvictor;
    public File storageDir;
    public static SimpleCache cache;

    @Override
    public void onCreate() {
        super.onCreate();
        //for exo Player Cache
        //cacheEvictor = new LeastRecentlyUsedCacheEvictor(1 * 1024 * 1024 * 1024); // My cache size will be 1MB and
        cacheEvictor = new LeastRecentlyUsedCacheEvictor(15L * 1024L * 1024L); // My cache size will be 1MB and
        storageDir = new File(String.valueOf(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)));
        cache = new SimpleCache(storageDir, cacheEvictor);

    }
}