package com.video.tamas.firebase;

import android.app.PendingIntent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class KasinoBarFirebaseMessagingService extends FirebaseMessagingService {

    /*String url = "https://www.google.es/images/srpr/logo11w.png";*/
    private PendingIntent resultPendingIntent;
    private String wheelType;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {



        }
    }


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
       // sendDeviceToken(s);
    }
}

