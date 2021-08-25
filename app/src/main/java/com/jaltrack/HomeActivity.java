package com.jaltrack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.database.Schedule;
import com.database.ScheduleDatabaseHelper;
import com.helper.CustomBroadcastReceiver;
import com.project.shweta.jaltrack.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.helper.CommonFunctions.formatTime;
import static com.helper.Constants.BEDTIME;
import static com.helper.Constants.MyPREFERENCES;
import static com.helper.Constants.WAKEUPTIME;
import static com.jaltrack.ClockFragment.addNewLog;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageHomeOutline, imageHomeColored;
    private ImageView imageGoalOutline, imageGoalColored;
    private ImageView imageAdviceOutline, imageAdviceColored;
    private ImageView imageSettingsOutline, imageSettingsColored;

    public ImageView imageAddGlass;

    private ScheduleDatabaseHelper scheduleDatabaseHelper;

    private static int tab = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        imageHomeOutline = findViewById(R.id.image_home_outlined);
        imageHomeColored = findViewById(R.id.image_home_colored);
        imageGoalOutline = findViewById(R.id.image_goal_outline);
        imageGoalColored = findViewById(R.id.image_goal_colored);
        imageAdviceOutline = findViewById(R.id.image_advice_outline);
        imageAdviceColored = findViewById(R.id.image_advice_colored);
        imageSettingsOutline = findViewById(R.id.image_settings_outlined);
        imageSettingsColored = findViewById(R.id.image_settings_colored);

        imageAddGlass = findViewById(R.id.image_add_glass);

        imageHomeOutline.setOnClickListener(this);
        imageHomeColored.setOnClickListener(this);
        imageGoalOutline.setOnClickListener(this);
        imageGoalColored.setOnClickListener(this);
        imageAdviceOutline.setOnClickListener(this);
        imageAdviceColored.setOnClickListener(this);
        imageSettingsOutline.setOnClickListener(this);
        imageSettingsColored.setOnClickListener(this);
        imageAddGlass.setOnClickListener(this);

        scheduleDatabaseHelper = new ScheduleDatabaseHelper(this);
        createAutomaticScheduleIfNotExisting();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null && extras.getBoolean("NotiClick")) {
                imageAddGlass.performClick();
            }

        }

        startNotificationTimer();
    }

    @Override
    public void onClick(View view) {
        if (view == imageHomeOutline || view == imageHomeColored) {
            if (tab != 0) {
                changeVisibility(true, false, false, false);
                loadFragment(new ClockFragment(), tab > 0);
                tab = 0;
            }
        }
        if (view == imageGoalOutline || view == imageGoalColored) {
            if (tab != 1) {
                changeVisibility(false, true, false, false);
                loadFragment(new HistoryFragment(), tab > 1);
                tab = 1;
            }
        }
        if (view == imageAdviceColored || view == imageAdviceOutline) {
            if (tab != 2) {
                changeVisibility(false, false, true, false);
                loadFragment(new TipsFragment(), tab > 2);
                tab = 2;
            }
        }
        if (view == imageSettingsColored || view == imageSettingsOutline) {
            if (tab != 3) {
                changeVisibility(false, false, false, true);
                loadFragment(new SettingsFragment(), tab > 3);
                tab = 3;
            }
        }
        if (view == imageAddGlass) {
            addNewLog();
            changeVisibility(true, false, false, false);
        }
    }

    private void loadFragment(Fragment fragment, boolean lToR) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (lToR) {
            fragmentTransaction.setCustomAnimations(
                    R.anim.enter_from_left,
                    R.anim.exit_to_right,
                    R.anim.enter_from_right,
                    R.anim.exit_to_left);
        } else {
            fragmentTransaction.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right);
        }
        fragmentTransaction.replace(R.id.fragment, fragment, fragment.getClass().getSimpleName());
        fragmentTransaction.commit(); // save the changes
    }

    private void changeVisibility(boolean isHome, boolean isGoal,
                                  boolean isAdvice, boolean isSettings) {

        imageHomeOutline.setVisibility(View.VISIBLE);
        imageHomeColored.setVisibility(View.INVISIBLE);
        imageGoalOutline.setVisibility(View.VISIBLE);
        imageGoalColored.setVisibility(View.INVISIBLE);
        imageAdviceOutline.setVisibility(View.VISIBLE);
        imageAdviceColored.setVisibility(View.INVISIBLE);
        imageSettingsOutline.setVisibility(View.VISIBLE);
        imageSettingsColored.setVisibility(View.INVISIBLE);

        if (isHome) {
            imageHomeColored.setVisibility(View.VISIBLE);
            imageHomeOutline.setVisibility(View.INVISIBLE);
        }
        if (isGoal) {
            imageGoalColored.setVisibility(View.VISIBLE);
            imageGoalOutline.setVisibility(View.INVISIBLE);
        }

        if (isAdvice) {
            imageAdviceColored.setVisibility(View.VISIBLE);
            imageAdviceOutline.setVisibility(View.INVISIBLE);
        }

        if (isSettings) {
            imageSettingsColored.setVisibility(View.VISIBLE);
            imageSettingsOutline.setVisibility(View.INVISIBLE);
        }
    }

    private void createAutomaticScheduleIfNotExisting() {
        if (scheduleDatabaseHelper.getScheduleCount() <= 0) {
            SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
            String startingTime = sharedPreferences.getString(WAKEUPTIME, "07:00");
            String endTime = sharedPreferences.getString(BEDTIME, "23:00");
            int numberOfReminders = 8;
            int starttime_HH = Integer.parseInt(startingTime.substring(0, 2));
            int starttime_MM = Integer.parseInt(startingTime.substring(3, 5));
            int endtime_HH = Integer.parseInt(endTime.substring(0, 2));
            int endtime_MM = Integer.parseInt(endTime.substring(3, 5));

            int slot = (endtime_HH - starttime_HH) * 60;
            slot = (slot - starttime_MM + endtime_MM) / numberOfReminders;

            int slotHour = (int) Math.floor(slot / 60);
            int slotMinute = slot - (slotHour * 60);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, starttime_HH);
            calendar.set(Calendar.MINUTE, starttime_MM);
            if (starttime_HH < 12) calendar.set(Calendar.AM_PM, Calendar.AM);
            else calendar.set(Calendar.AM_PM, Calendar.PM);

            for (int i = 0; i < numberOfReminders; i++) {
                List<Integer> days = new ArrayList<>();
                for (int j = 1; j < 8; j++) {
                    days.add(j);
                }

                int hh = calendar.get(Calendar.HOUR);
                if (calendar.get(Calendar.AM_PM) == Calendar.PM) hh += 12;

                int mm = calendar.get(Calendar.MINUTE);
                System.out.println("TIME OF REMINDER: " + formatTime(hh, mm));

                scheduleDatabaseHelper.insertSchedule(formatTime(hh, mm), true, days);
                calendar.add(Calendar.HOUR, slotHour);
                calendar.add(Calendar.MINUTE, slotMinute);
            }
        }
    }

    public void startNotificationTimer() {

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                setNewAlarm();
            }
        }, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)); // period: 1 day
    }

    public void setNewAlarm() {
        Intent notifyIntent = new Intent(this, CustomBroadcastReceiver.class);
        notifyIntent.putExtra("NotiClick", true);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        List<Schedule> scheduleList = scheduleDatabaseHelper.getAllSchedule();
        Calendar calendar = Calendar.getInstance();

        int count = 0;
        for (Schedule schedule : scheduleList) {

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, count++, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT
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

