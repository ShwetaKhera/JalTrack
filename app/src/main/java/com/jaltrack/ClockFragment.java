package com.jaltrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.database.Log;
import com.database.WaterLogDatabaseHelper;
import com.project.shweta.jaltrack.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.helper.CommonFunctions.calculateIntakeFromWeightAndAge;
import static com.helper.Constants.AGE;
import static com.helper.Constants.MyPREFERENCES;
import static com.helper.Constants.WEIGHT;

public class ClockFragment
        extends Fragment
        implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private static List<Log> logList = new ArrayList<>();
    private static WaterLogDatabaseHelper db;
    private static LogAdapter mAdapter;
    private Date currentTime = Calendar.getInstance().getTime();
    private TextView secondHand, editTime, index, waterLevelAmount, mlCount, glassCount;
    private LinearLayout viewEditLog, layoutTimepicker;
    private ImageView deleteLog;
    private SeekBar waterLevelHandle;
    private Button cancelLogEdit, saveLogEdit, buttonDone, buttonCancel;
    private TimePicker timePicker;
    private RecyclerView recyclerViewLog;
    private RelativeLayout containerGlassImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_clock, container, false);

        mlCount = view.findViewById(R.id.text_ml_count);
        glassCount = view.findViewById(R.id.text_glass_count);
        secondHand = view.findViewById(R.id.second_hand);
        viewEditLog = view.findViewById(R.id.view_edit_log);
        viewEditLog.setOnClickListener(this);
        layoutTimepicker = view.findViewById(R.id.layout_timepicker);
        layoutTimepicker.setOnClickListener(this);
        editTime = view.findViewById(R.id.edit_time);
        editTime.setOnClickListener(this);
        deleteLog = view.findViewById(R.id.delete_log);
        deleteLog.setOnClickListener(this);
        cancelLogEdit = view.findViewById(R.id.cancel_log_edit);
        cancelLogEdit.setOnClickListener(this);
        saveLogEdit = view.findViewById(R.id.save_log_edit);
        saveLogEdit.setOnClickListener(this);
        timePicker = view.findViewById(R.id.timepicker);
        timePicker.setOnClickListener(this);
        waterLevelHandle = view.findViewById(R.id.water_level_handle);
        waterLevelHandle.setOnSeekBarChangeListener(this);
        buttonCancel = view.findViewById(R.id.btn_cancel_update);
        buttonCancel.setOnClickListener(this);
        buttonDone = view.findViewById(R.id.btn_done_update);
        buttonDone.setOnClickListener(this);


        waterLevelAmount = view.findViewById(R.id.water_level_amount);
        index = view.findViewById(R.id.index);

        recyclerViewLog = view.findViewById(R.id.recycler_view_log);
        containerGlassImage = view.findViewById(R.id.container_glass_image);

        initializeDbAndRecyclerView(view);
        startRotationOfHand(getStartingAngle());
        calculateAmountOfWater();
        createGlassView(view);

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                containerGlassImage.removeAllViews();
                createGlassView(view);
                calculateAmountOfWater();
            }
        });

        return view;
    }

    public static void addNewLog() {
        long id = db.insertLog(175);

        // get the newly inserted note from db
        Log n = db.getLog(id);

        if (n != null) {
            // adding new note to array list at 0 position
            logList.add(0, n);

            // refreshing the list
            mAdapter.notifyDataSetChanged();
        }
    }


    private void calculateAmountOfWater() {
        if (getActivity() != null) {
            SharedPreferences sharedPreferences =
                    getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            int age = sharedPreferences.getInt(AGE, 25);
            int weight = sharedPreferences.getInt(WEIGHT, 70);

            int totalMl = 0;
            for (Log log : logList) {
                totalMl += log.getAmount();
            }
            int volumeRemaining = calculateIntakeFromWeightAndAge(age, weight) - totalMl;
            int noOfGlasses;

            if (volumeRemaining < 1) {
                mlCount.setText("( " + 0 + " ml )");
                noOfGlasses = 0;
            } else {
                mlCount.setText("( " + volumeRemaining + " ml )");
                noOfGlasses = (volumeRemaining / 175) + 1;
            }

            if (noOfGlasses < 1) glassCount.setText(noOfGlasses + " glasses remaining");
            else glassCount.setText(noOfGlasses + " glasses remaining");
        }
    }

    private void initializeDbAndRecyclerView(View view) {
        db = new WaterLogDatabaseHelper(getActivity());
        if (logList.isEmpty()) {
            logList.addAll(db.getTodayLogs());
        }
        mAdapter = new LogAdapter(getActivity(), logList, view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity(), RecyclerView.HORIZONTAL, false);
        recyclerViewLog.setLayoutManager(linearLayoutManager);
        recyclerViewLog.setAdapter(mAdapter);

        System.out.println(db.getLogsCount());

    }

    // Returns starting angle of the seconds hand
    private int getStartingAngle() {
        int seconds = currentTime.getSeconds();
        int startingAngle = seconds * 6;
        if (seconds < 30) {
            return startingAngle + 180;
        } else if (seconds == 30) {
            return 0;
        }
        return startingAngle - 180;
    }

    // Starts rotation of seconds hand from the starting angle
    private void startRotationOfHand(int startingAngle) {
        System.out.println("Starting Angle: " + startingAngle);
        RotateAnimation rotate = new RotateAnimation(startingAngle, startingAngle + 360);
        rotate.setDuration(60000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());

        secondHand.startAnimation(rotate);
    }

    @Override
    public void onClick(View view) {
        if (view == editTime) {
            openTimepicker();
        } else if (view == buttonDone) {
            mAdapter.changeTimeInDB();
        } else if (view == buttonCancel) {
            layoutTimepicker.setVisibility(View.GONE);
        } else if (view == deleteLog) {
            deleteLogFromList();
        } else if (view == cancelLogEdit) {
            viewEditLog.setVisibility(View.GONE);
        } else if (view == saveLogEdit) {
            updateLogToList();
        }
    }

    private void openTimepicker() {
        int hh = Integer.parseInt(editTime.getText().toString().substring(0, 2));
        int mm = Integer.parseInt(editTime.getText().toString().substring(3));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(hh);
            timePicker.setMinute(mm);
        }
        layoutTimepicker.setVisibility(View.VISIBLE);
    }

    private void deleteLogFromList() {
        int position = Integer.parseInt(index.getText().toString());
        db.deleteLog(logList.get(position));

        // removing the note from the list
        logList.remove(position);

        viewEditLog.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
    }

    private void updateLogToList() {
        int position = Integer.parseInt(index.getText().toString());
        int amount = Integer.parseInt(waterLevelAmount.getText().toString());
//        Timestamp timestamp = new Timestamp(new Date().getTime());
//        String time = editTime.getText().toString();
//        timestamp.setHours(Integer.parseInt(time.substring(0,2)));
//        timestamp.setMinutes(Integer.parseInt(time.substring(3)));

        Log n = logList.get(position);
        // updating note text
        n.setAmount(amount);
//        n.setTimestamp(timestamp.toString());
//        System.out.println("UPDATED TIMESTAMP: " + timestamp.toString());

        // updating note in db
        db.updateLog(n);

        // refreshing the list
        logList.set(position, n);
        mAdapter.notifyDataSetChanged();
        viewEditLog.setVisibility(View.GONE);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int amt = (175 * seekBar.getProgress()) / 100;
        waterLevelAmount.setText(amt + "");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        int amt = (175 * seekBar.getProgress()) / 100;
        waterLevelAmount.setText(amt + "");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    private void createGlassView(View view) {
        for (int i = 0; i < logList.size(); i++) {
            Timestamp timestamp = Timestamp.valueOf(logList.get(i).getTimestamp());
//            timestamp.setTime(timestamp.getTime() + getOffset());
            int hour = timestamp.getHours();

            if (containerGlassImage.findViewById(hour) == null) {
                ImageView image = new ImageView(view.getContext());
                image.setId(hour);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(80, 80);
                image.setLayoutParams(params);

                float height = 327.5f;
                float width = 327.5f;

                System.out.println("HEIGHT OF GLASS VIEW CONTAINER(in dp): " + height);
                System.out.println("WIDTH OF GLASS VIEW CONTAINER(in dp): " + width);

                float centerX = width / 2;
                float centerY = height / 2;
                image.setX(centerX);
                image.setY(centerY);
                setPositionOnClock(hour, centerX, centerY, image);

                image.setPadding(20, 20, 20, 20);

                GradientDrawable border = new GradientDrawable();
                border.setColor(0xFFFFFFFF);
                border.setCornerRadius(100);
                border.setStroke(5, getResources().getColor(R.color.colorPrimaryDark));
                image.setImageDrawable(getResources().getDrawable(R.drawable.log_glass_empty));
                image.setBackground(border);

                // Adds the view to the layout
                containerGlassImage.addView(image);
            }
        }
        System.out.println(containerGlassImage.getChildCount());
    }

    private void setPositionOnClock(int hour, float centerX, float centerY, ImageView imageView) {
        float x = centerX;
        float y = centerY;

        float oddY = centerY * 3.4f / 4;
        float oddX = centerX * 2.15f / 4;

        float evenY = centerY * 1.95f / 4;
        float evenX = centerX * 3.55f / 4;

//        corners
        if (hour == 12 || hour == 0) {
            y = y - centerY;
        } else if (hour == 6 || hour == 18) {
            y = y + centerY;
        } else if (hour == 3 || hour == 15) {
            x = x + centerX;
        } else if (hour == 9 || hour == 21) {
            x = x - centerX;
        }
//        odd
        else if (hour == 1 || hour == 13) {
            y = y - oddY;
            x = x + oddX;
        } else if (hour == 5 || hour == 17) {
            y = y + oddY;
            x = x + oddX;
        } else if (hour == 7 || hour == 19) {
            y = y + oddY;
            x = x - oddX;
        } else if (hour == 11 || hour == 23) {
            y = y - oddY;
            x = x - oddX;
        }
//        even
        else if (hour == 2 || hour == 14) {
            y = y - evenY;
            x = x + evenX;
        } else if (hour == 4 || hour == 16) {
            y = y + evenY;
            x = x + evenX;
        } else if (hour == 8 || hour == 20) {
            y = y + evenY;
            x = x - evenX;
        } else if (hour == 10 || hour == 22) {
            y = y - evenY;
            x = x - evenX;
        }
        imageView.setX(x * 2.4f);
        imageView.setY(y * 2.4f);
    }
}

