package com.video.tamas.Utils;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.video.tamas.R;

public class serv extends Service {

    MediaPlayer mp;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    public void onCreate()
    {   
        mp = MediaPlayer.create(this, R.raw.file);
        mp.setLooping(false);
    }
    public void onDestroy()
    {       
        mp.stop();
    }
    public void onStart(Intent intent,int startid){

        Log.d("serv", "On start");
        mp.start();
    }
}