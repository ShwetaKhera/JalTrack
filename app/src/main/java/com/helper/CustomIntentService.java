package com.helper;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.jaltrack.HomeActivity;
import com.project.shweta.jaltrack.R;

public class CustomIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 0;

    public CustomIntentService() {
        super("CustomIntentService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "notify_001");
        Intent ii = new Intent(getApplicationContext(), HomeActivity.class);
        ii.putExtra("NotiClick", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

        NotificationCompat.BigPictureStyle bpStyle = new NotificationCompat.BigPictureStyle();
        bpStyle.bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.tip_1)).build();
        bpStyle.setSummaryText("Sit Down to Drink Water Rather than Standing");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.water_drop).setColor(getResources().getColor(R.color.colorPrimary));
        mBuilder.setContentTitle("Gulg Gulg Gulg!");
        mBuilder.setContentText("I am here to remind you to drink water.");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bpStyle);
        mBuilder.setAutoCancel(true);
        mBuilder.setOngoing(true);

        mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "notify_001";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }
}
