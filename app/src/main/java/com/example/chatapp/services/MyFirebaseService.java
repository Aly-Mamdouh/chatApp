package com.example.chatapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.example.chatapp.common.common;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String> dataRec=remoteMessage.getData();
        if(dataRec!=null){
            common.showNotification(this,new Random().nextInt()
            ,dataRec.get(common.NOTI_TITLE)
             ,dataRec.get(common.NOTI_CONTENT)
              ,dataRec.get(common.NOTI_SENDER)
               ,dataRec.get(common.NOTI_ROOM_ID),null);
        }
    }
}