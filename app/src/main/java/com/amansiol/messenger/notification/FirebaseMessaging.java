package com.amansiol.messenger.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.amansiol.messenger.ChatActivity;
import com.amansiol.messenger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaging extends FirebaseMessagingService {
    private static final String CHANNEL_ID ="channel_id01" ;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
        String savedCurrentuser=sp.getString("CURRENT_USERID","None");
        String sent=remoteMessage.getData().get("sent");
        String user=remoteMessage.getData().get("user");
        FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
        if(fuser!=null){

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                    sendOAndAboveNotification(remoteMessage);
                }else {
                    sendNormalNotification(remoteMessage);
                }

        }
    }

    private void sendNormalNotification(RemoteMessage remoteMessage) {
        String user=""+remoteMessage.getData().get("user");
        String icon=""+remoteMessage.getData().get("icon");
        String title=""+remoteMessage.getData().get("title");
        String body=""+remoteMessage.getData().get("body");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("hisUID",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.appicon);
        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defsoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defsoundUri)
                .setContentIntent(pIntent)
                .setLargeIcon(bitmap)
                .setWhen(System.currentTimeMillis());
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if(i>0){
            j=i;

        }
        notificationManager.notify(j,builder.build());
    }

    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {
        CreateNotificationChannel();
        String user=""+remoteMessage.getData().get("user");
        String icon=""+remoteMessage.getData().get("icon");
        String title=""+remoteMessage.getData().get("title");
        String body=""+remoteMessage.getData().get("body");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("hisUID",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.appicon);
        PendingIntent pIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defsoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,CHANNEL_ID);
        builder.setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(Integer.parseInt(icon))
                .setSound(defsoundUri)
                .setContentIntent(pIntent)
                .setLargeIcon(bitmap)
                .setWhen(System.currentTimeMillis());


        int j=0;
        if(i>0){
            j=i;

        }
        NotificationManagerCompat notificationCompat=NotificationManagerCompat.from(this);
        notificationCompat.notify(j,builder.build());

    }

    private void CreateNotificationChannel() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            CharSequence title="ChatApp";
            String description="ChatApp Description";
            int importance=NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,title,importance);
            notificationChannel.setDescription( description);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            updateToken(s);
        }

    }

    private void updateToken(String s) {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token=new Token(s);
        ref.child(user.getUid()).setValue(token);
    }
}
