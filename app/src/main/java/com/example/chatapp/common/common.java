package com.example.chatapp.common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;

import androidx.core.app.NotificationCompat;

import com.example.chatapp.R;
import com.example.chatapp.model.userModel;
import com.example.chatapp.services.MyFirebaseService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;

public class common {
    public static final String CHAT_LIST_REF ="ChatList" ;
    public static final String CHAT_REF = "Chat";
    public static final String CHAT_DETAIL_REF ="Detail" ;
    public static final String NOTI_TITLE ="title" ;
    public static final String NOTI_CONTENT = "content";
    public static final String NOTI_SENDER ="sender" ;
    public static final String NOTI_ROOM_ID = "room_id";
    public static userModel currentUser=new userModel();
    public static final String USER_REF ="People" ;
    public static userModel chatUser=new userModel();
    public static String roomSelected="";

    public static String GenerateChatRoomId(String a, String b) {
        if(a.compareTo(b)>0){
            return new StringBuilder(a).append(b).toString();
        }

        else if(a.compareTo(b)<0){
            return new StringBuilder(b).append(a).toString();
        }

        else {
            return new StringBuilder("Chat_Yourself_Error").append(new Random().nextInt()).toString();
        }
    }

    public static String getName(userModel chatUser) {
        return new StringBuilder(chatUser.getFirstName()).append(" ")
                .append(chatUser.getLastName()).toString();
    }

    public static String gteFileName(ContentResolver contentResolver, Uri fileUri) {
        String res=null;

        if(fileUri.getScheme().equals("content")) {
            final String[] projection = new String[]{"*"};
            Cursor cursor = contentResolver.query(fileUri, projection, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    res = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if(res==null){
            res=fileUri.getPath();
            int cut=res.lastIndexOf("/");
            if(cut!=-1){
                res=res.substring(cut+1);
            }

        }
        return res;
    }

    public static void showNotification(Context context, int id, String title, String content, String sender, String roomId, Intent intent) {

        PendingIntent pendingIntent = null;
        //A PendingIntent is a token that you give to a foreign application
        // (e.g. NotificationManager, AlarmManager, Home Screen AppWidgetManager,
        // or other 3rd party applications),
        /** which allows the foreign application to use your application's permissions to execute a predefined piece of code.**/

        if (intent != null) {
            pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
            String NOTIFICATION_CHANNEL_ID="com.example.chatapp";
            NotificationManager notificationManager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                NotificationChannel notificationChannel=new NotificationChannel(NOTIFICATION_CHANNEL_ID,"new chat app",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription("new chat app");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
            builder.setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_baseline_image_24));
            if(pendingIntent!=null){
                builder.setContentIntent(pendingIntent);
            }
        Notification notification=builder.build();

            if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(sender)&& !common.roomSelected.equals(roomId))
                notificationManager.notify(id,notification);


    }
}
