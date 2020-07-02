package com.example.roadresq;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.roadresq.MainActivity;
import com.example.roadresq.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FCMService extends FirebaseMessagingService {

    private void sendNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, Help.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Default";
        long[] pattern = {500,500,500,500,500};


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.handshake)

                .setContentTitle(remoteMessage.getNotification().getTitle()+" needs your help!")
                .setContentText("Click here for more details")
                .setPriority(Notification.PRIORITY_MAX)
                .setVibrate(pattern)
//                .setLights(Color.BLUE,1,1)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Default Notification Channel");



            notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),channelId)
                    .setSmallIcon(R.drawable.handshake)
                    .setContentTitle(remoteMessage.getNotification().getTitle()+" needs your help!")
                    .setContentText("Click here for more details")
                    .setVibrate(pattern)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
            ;

            Log.i("abc", "in ifff "+remoteMessage.getNotification().getBody());
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification().getBody().toString().length()>0){



            ActivityManager am = (ActivityManager)getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            String cn = am.getRunningTasks(1).get(0).topActivity.flattenToString();

            Log.i("abc", "onMessageReceived: "+cn);

//            if(!cn.equals("com.mizaklabs.www.budd/com.mizaklabs.www.brew.chat.Chat_main")) {
//                sendNotification(remoteMessage);
//            }

            sendNotification(remoteMessage);
//            sendSnackbar(remoteMessage.getNotification().getBody());
        }
    }

}
