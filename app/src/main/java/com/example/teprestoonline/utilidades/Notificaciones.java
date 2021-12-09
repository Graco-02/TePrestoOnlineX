package com.example.teprestoonline.utilidades;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.teprestoonline.MainActivity;
import com.example.teprestoonline.R;
import android.content.Context;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static androidx.core.content.ContextCompat.getSystemService;

public class Notificaciones {
    private static int noti_count = 0;

    public Notificaciones(){

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void show_Notification(String titulo, String cuerpo, AppCompatActivity act){
        // noti_count = atomicInteger.getAndIncrement();

        Intent intent=new Intent(act.getApplicationContext(), MainActivity.class);
        String CHANNEL_ID="MYCHANNEL";
        NotificationChannel notificationChannel=
                new NotificationChannel(CHANNEL_ID,"name", NotificationManager.IMPORTANCE_LOW);
        PendingIntent pendingIntent=PendingIntent.getActivity(act.getApplicationContext(),1,intent,0);
        @SuppressLint("ResourceType") Notification notification=new Notification.Builder(act.getApplicationContext(),CHANNEL_ID)
                .setContentText(cuerpo)
                .setContentTitle(titulo)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.sym_action_chat,"Title",pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(android.R.drawable.sym_action_chat)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build();

        NotificationManager notificationManager=(NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(noti_count++,notification);

    }
}
