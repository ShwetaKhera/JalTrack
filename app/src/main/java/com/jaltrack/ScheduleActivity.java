package com.jaltrack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.database.Schedule;
import com.database.ScheduleDatabaseHelper;
import com.helper.CustomBroadcastReceiver;
import com.project.shweta.jaltrack.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.helper.CommonFunctions.formatTime;

public class ScheduleActivity extends AppCompatActivity implements View.OnClickListener {

    private ScheduleDatabaseHelper db;
    private ScheduleAdapter adapter;

    private RecyclerView recyclerView;
    private ImageView buttonAddSchedule, btnBack;
    private Button buttonDone, buttonCancel;
    private FrameLayout frameLayout;
    private TimePicker timePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        buttonAddSchedule = findViewById(R.id.btn_add_schedule);
        buttonAddSchedule.setOnClickListener(this);
        buttonCancel = findViewById(R.id.btn_cancel);
        buttonCancel.setOnClickListener(this);
        buttonDone = findViewById(R.id.btn_done);
        buttonDone.setOnClickListener(this);
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        frameLayout = findViewById(R.id.frame_background);
        timePicker = findViewById(R.id.timepicker);

        recyclerView = findViewById(R.id.recycler_view_schedule);
        initializeDbAndRecyclerView();
    }

    private void initializeDbAndRecyclerView() {
        db = new ScheduleDatabaseHelper(this);
        adapter = new ScheduleAdapter(this, db.getAllSchedule());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onClick(View view) {
        if (view == buttonAddSchedule) {
            showTimePicker();
        } else if (view == buttonCancel) {
            frameLayout.setVisibility(View.GONE);
        } else if (view == buttonDone) {
            addNewSchedule();
        } else if (view == btnBack) {
            finish();
        }
    }

    private void showTimePicker() {
        if (db.getScheduleCount() < 10) {
            if (frameLayout.getVisibility() == View.GONE) {
                frameLayout.setVisibility(View.VISIBLE);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (calendar.get(Calendar.AM_PM) == Calendar.AM) {
                    timePicker.setHour(calendar.get(Calendar.HOUR));
                } else {
                    timePicker.setHour(calendar.get(Calendar.HOUR) + 12);
                }
                timePicker.setMinute(calendar.get(Calendar.MINUTE));
            }
        } else {
            Toast.makeText(this,
                    "Only 10 reminders allowed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addNewSchedule() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            List<Integer> days = new ArrayList<>();
            for (int j = 1; j < 8; j++) {
                days.add(j);
            }

            int hh = timePicker.getHour();
            int mm = timePicker.getMinute();

            String formattedTime = formatTime(hh, mm);
            for (Schedule schedule : db.getAllSchedule()) {
                if (formattedTime.equals(schedule.getTime())) {
                    Toast.makeText(this,
                            "You already have a reminder set for this time."
                            , Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            db.insertSchedule(formattedTime, true, days);
            adapter = new ScheduleAdapter(this, db.getAllSchedule());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            frameLayout.setVisibility(View.GONE);

            addAlarmForNewSchedule(hh, mm);
        }
    }

    private void addAlarmForNewSchedule(int hh, int mm) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Calendar calendar = Calendar.getInstance();
            int calHH = calendar.get(Calendar.HOUR);
            int calMM = calendar.get(Calendar.MINUTE);
            if ((calHH == hh && calMM < mm) || calHH < hh) {
                Intent notifyIntent = new Intent(this, CustomBroadcastReceiver.class);
                notifyIntent.putExtra("NotiClick", true);
                AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

                if (hh > 11) {
                    if (hh != 12) hh -= 12;
                    calendar.set(Calendar.AM_PM, Calendar.PM);
                } else {
                    calendar.set(Calendar.AM_PM, Calendar.AM);
                }
                calendar.set(Calendar.HOUR, hh);
                calendar.set(Calendar.MINUTE, mm);
                calendar.set(Calendar.SECOND, 0);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this, 11, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT
                );
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTime().getTime(), pendingIntent);
            }
        }
    }
}