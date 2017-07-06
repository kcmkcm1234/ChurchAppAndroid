package com.tech.thrithvam.churchapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
     //   Log.d(TAG, "From: " + remoteMessage.getFrom());
      //  Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        String title;
        String description;
        String type;
        String linkID;
        if(remoteMessage.getData()!=null) {
            title = (remoteMessage.getData().get("title") == null || remoteMessage.getData().get("title").equals("")) ? "null" : remoteMessage.getData().get("title");
            description = (remoteMessage.getData().get("body") == null || remoteMessage.getData().get("body").equals("")) ? "null" : remoteMessage.getData().get("body");
            type = (remoteMessage.getData().get("type") == null || remoteMessage.getData().get("type").equals("")) ? "null" : remoteMessage.getData().get("type");
            linkID = (remoteMessage.getData().get("linkid") == null || remoteMessage.getData().get("linkid").equals(""))? "null" : remoteMessage.getData().get("linkid");
            DatabaseHandler db = DatabaseHandler.getInstance(this);
            db.InsertNotificationIDs(remoteMessage.getMessageId(),
                    title,
                    description,
                    type,
                    linkID,
                    Long.toString(java.util.Calendar.getInstance().getTimeInMillis()));

            //Notification----------------------
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyFirebaseMessagingService.this);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle(title);
            mBuilder.setContentText(description);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mBuilder.setSound(alarmSound);
            Intent resultIntent = new Intent(MyFirebaseMessagingService.this, Notifications.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(MyFirebaseMessagingService.this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setAutoCancel(true);
            mNotificationManager.notify((int)remoteMessage.getSentTime(), mBuilder.build());
        }
    }
}
