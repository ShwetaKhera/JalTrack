package com.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.database.Schedule;
import com.database.ScheduleDatabaseHelper;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {

    public StartMyServiceAtBootReceiver() {
        super();
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setNewAlarm(context);
                }
            }, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)); // period: 1 day
        }
    }

    public void setNewAlarm(Context context) {
        Intent notifyIntent = new Intent(context, CustomBroadcastReceiver.class);
        notifyIntent.putExtra("NotiClick", true);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        ScheduleDatabaseHelper scheduleDatabaseHelper = new ScheduleDatabaseHelper(context);
        List<Schedule> scheduleList = scheduleDatabaseHelper.getAllSchedule();
        Calendar calendar = Calendar.getInstance();

        int count = 0;
        for (Schedule schedule : scheduleList) {

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context, count++, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT
            );

            int hour = Integer.parseInt(schedule.getTime().substring(0, 2));
            int min = Integer.parseInt(schedule.getTime().substring(3));

            int calHour = calendar.get(Calendar.HOUR);
            if (calendar.get(Calendar.AM_PM) == Calendar.PM) calHour += 12;

            int calMin = calendar.get(Calendar.MINUTE);

            if ((calHour == hour && calMin < min)
                    || hour > calHour) {
                if (schedule.isOn() && schedule.hasDay(calendar.get(Calendar.DAY_OF_WEEK))) {

                    if (hour > 11) {
                        if (hour != 12) hour -= 12;
                        calendar.set(Calendar.AM_PM, Calendar.PM);
                    } else {
                        calendar.set(Calendar.AM_PM, Calendar.AM);
                    }
                    calendar.set(Calendar.HOUR, hour);
                    calendar.set(Calendar.MINUTE, min);
                    calendar.set(Calendar.SECOND, 0);

                    System.out.println(hour + "  " + min + "  " + calendar.getTime());

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTime().getTime(), pendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTime().getTime(), pendingIntent);
                    }

                }
            }
        }
    }
}
