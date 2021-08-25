package com.jaltrack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import com.database.Schedule;
import com.database.ScheduleDatabaseHelper;
import com.project.shweta.jaltrack.R;

import java.util.List;

import static com.helper.CommonFunctions.formatTime;

public class ScheduleAdapter
        extends RecyclerView.Adapter<ScheduleAdapter.MyView> implements View.OnClickListener {

    private Context context;
    private List<Schedule> scheduleList;
    private RecyclerView recyclerView;
    private String[] days = {"", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private ScheduleDatabaseHelper db;

    private TimePicker timePickerUpdate;
    private LinearLayout frameLayoutUpdate;
    private Button cancelUpdate, doneUpdate;
    private int adapterPos;

    public class MyView
            extends RecyclerView.ViewHolder
            implements View.OnClickListener, ToggleButton.OnCheckedChangeListener {

        private TextView scheduleTime, deleteSchedule;
        private Switch scheduleSwitch;
        private ImageView imageDownArrow;
        private LinearLayout layoutScheduleDays, layoutLayer3;
        private ToggleButton sunday, monday, tuesday, wednesday, thursday, friday, saturday;


        public MyView(View v) {
            super(v);

            scheduleTime = v.findViewById(R.id.schedule_time);
            scheduleTime.setOnClickListener(this);
            deleteSchedule = v.findViewById(R.id.delete_schedule);
            deleteSchedule.setOnClickListener(this);
            imageDownArrow = v.findViewById(R.id.image_down);
            imageDownArrow.setOnClickListener(this);
            layoutScheduleDays = v.findViewById(R.id.schedule_days);
            layoutLayer3 = v.findViewById(R.id.layer_3);

            scheduleSwitch = v.findViewById(R.id.schedule_switch);
            scheduleSwitch.setOnCheckedChangeListener(this);
            sunday = v.findViewById(R.id.sunday);
            sunday.setOnCheckedChangeListener(this);
            monday = v.findViewById(R.id.monday);
            monday.setOnCheckedChangeListener(this);
            tuesday = v.findViewById(R.id.tuesday);
            tuesday.setOnCheckedChangeListener(this);
            wednesday = v.findViewById(R.id.wednesday);
            wednesday.setOnCheckedChangeListener(this);
            thursday = v.findViewById(R.id.thursday);
            thursday.setOnCheckedChangeListener(this);
            friday = v.findViewById(R.id.friday);
            friday.setOnCheckedChangeListener(this);
            saturday = v.findViewById(R.id.saturday);
            saturday.setOnCheckedChangeListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view == scheduleTime) {
                if (frameLayoutUpdate.getVisibility() == View.GONE) {
                    frameLayoutUpdate.setVisibility(View.VISIBLE);
                }
                adapterPos = getAdapterPosition();
                String scheduleTime = scheduleList.get(adapterPos).getTime();
                int schedule_HH = Integer.parseInt(scheduleTime.substring(0, 2));
                int schedule_MM = Integer.parseInt(scheduleTime.substring(3, 5));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePickerUpdate.setHour(schedule_HH);
                    timePickerUpdate.setMinute(schedule_MM);
                }

            } else if (view == imageDownArrow) {
                showDays(view, view.getRotation() == 0);
            } else if (view == deleteSchedule) {
                TextView text = (TextView) view;
                if (text.getText().toString().equalsIgnoreCase("delete")) {
                    Schedule schedule = scheduleList.get(getAdapterPosition());
                    showDays(imageDownArrow, view.getRotation() == 0);
//                    recyclerView.getChildViewHolder(view).itemView.findViewById(R.id.image_down).performClick();
                    db.deleteSchedule(schedule);
                    scheduleList.remove(schedule);
                    notifyDataSetChanged();
                }
            }
        }

        private void showDays(final View view, boolean show) {
            if (show) {
                layoutScheduleDays.setVisibility(View.VISIBLE);
                view.animate().rotation(180).setDuration(500);
//                layoutLayer3.animate().translationY(layoutScheduleDays.getHeight()).setDuration(250)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        layoutLayer3.setY();
//                    }
//                });
                layoutScheduleDays.animate()
                        .alpha(1.0f)
                        .setDuration(1000)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                layoutScheduleDays.setVisibility(View.VISIBLE);
                            }

//                            @Override
//                            public void onAnimationStart(Animator animation) {
//                                super.onAnimationStart(animation);
//
//                            }
                        });
                deleteSchedule.setText("Delete");
                deleteSchedule.setTextColor(context.getResources().getColor(R.color.colorRed));
            } else {
                view.animate().rotation(0).setDuration(500);
                layoutScheduleDays.animate()
                        .alpha(0.0f)
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                layoutScheduleDays.setVisibility(View.GONE);
                            }
//                            @Override
//                            public void onAnimationStart(Animator animation) {
//                                super.onAnimationStart(animation);
//                                layoutLayer3.animate().translationY(-10).setDuration(250);
//                            }
                        });
                deleteSchedule.setText(getselectedDays(scheduleList.get(getAdapterPosition())));
                deleteSchedule.setTextColor(context.getResources().getColor(R.color.colorBlack));
            }
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (compoundButton == scheduleSwitch) {
                scheduleList.get(getAdapterPosition()).setOn(b);
                db.updateScheduleSwitch(scheduleList.get(getAdapterPosition()));
            } else if (compoundButton == sunday) {
                updateDayInSchedule(sunday, getAdapterPosition(), 1, b);
            } else if (compoundButton == monday) {
                updateDayInSchedule(monday, getAdapterPosition(), 2, b);
            } else if (compoundButton == tuesday) {
                updateDayInSchedule(tuesday, getAdapterPosition(), 3, b);
            } else if (compoundButton == wednesday) {
                updateDayInSchedule(wednesday, getAdapterPosition(), 4, b);
            } else if (compoundButton == thursday) {
                updateDayInSchedule(thursday, getAdapterPosition(), 5, b);
            } else if (compoundButton == friday) {
                updateDayInSchedule(friday, getAdapterPosition(), 6, b);
            } else if (compoundButton == saturday) {
                updateDayInSchedule(saturday, getAdapterPosition(), 7, b);
            }
        }
    }

    private void updateDayInSchedule(ToggleButton button,
                                     int adapterPosition, int dayIndex, boolean add) {
        if (add) {
            if (!scheduleList.get(adapterPosition).getDays().contains(dayIndex)) {
                scheduleList.get(adapterPosition).addDay(dayIndex);
                button.setTextColor(context.getResources().getColor(R.color.colorWhite));
            }
        } else {
            if (scheduleList.get(adapterPosition).getDays().contains(dayIndex)) {
                scheduleList.get(adapterPosition).removeDay(dayIndex);
                button.setTextColor(context.getResources().getColor(R.color.colorBlack));
            }
        }
        db.updateScheduleDays(scheduleList.get(adapterPosition));
    }


    public ScheduleAdapter(Context context, List<Schedule> scheduleList) {
        this.context = context;
        this.scheduleList = scheduleList;
        this.timePickerUpdate = ((Activity) context).findViewById(R.id.timepicker_update);
        this.frameLayoutUpdate = ((Activity) context).findViewById(R.id.frame_background_update);
        this.cancelUpdate = ((Activity) context).findViewById(R.id.btn_cancel_update);
        this.cancelUpdate.setOnClickListener(this);
        this.doneUpdate = ((Activity) context).findViewById(R.id.btn_done_update);
        this.doneUpdate.setOnClickListener(this);

        db = new ScheduleDatabaseHelper(context);
    }

    @Override
    public void onClick(View view) {
        if (view == doneUpdate) {
            changeTimeInDB();
        } else if (view == cancelUpdate) {
            frameLayoutUpdate.setVisibility(View.GONE);
        }
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView
                = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule,
                parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {
        Schedule schedule = scheduleList.get(position);
        holder.scheduleTime.setText(formatDate(schedule.getTime()));
        holder.scheduleSwitch.setChecked(schedule.isOn());
        holder.deleteSchedule.setText(getselectedDays(schedule));

        changeViewIfChecked(holder.sunday, schedule, 1);
        changeViewIfChecked(holder.monday, schedule, 2);
        changeViewIfChecked(holder.tuesday, schedule, 3);
        changeViewIfChecked(holder.wednesday, schedule, 4);
        changeViewIfChecked(holder.thursday, schedule, 5);
        changeViewIfChecked(holder.friday, schedule, 6);
        changeViewIfChecked(holder.saturday, schedule, 7);
    }

    private String formatDate(String dateStr) {
        try {
            String time;
            String minute = dateStr.substring(3, 5);
            int hh = Integer.parseInt(dateStr.substring(0, 2));
            if (hh > 12) {
                if (hh - 12 > 9) {
                    time = (hh - 12) + ":" + minute + " PM";
                } else {
                    time = "0" + (hh - 12) + ":" + minute + " PM";
                }
            } else {
                if (hh > 9) {
                    time = hh + ":" + minute + " AM";
                } else {
                    time = "0" + hh + ":" + minute + " AM";
                }
            }
            return time;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    private void changeTimeInDB() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int hourOfDay = timePickerUpdate.getHour();

            int minute = timePickerUpdate.getMinute();
            System.out.println("TIME OF REMINDER: " + formatTime(hourOfDay, minute));

            String formattedTime = formatTime(hourOfDay, minute);
            for (Schedule schedule : db.getAllSchedule()) {
                if (formattedTime.equals(schedule.getTime())) {
                    Toast.makeText(context,
                            "You already have a reminder set for this time."
                            , Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            scheduleList.get(adapterPos).setTime(formatTime(hourOfDay, minute));
            db.updateScheduleTime(scheduleList.get(adapterPos));
            notifyDataSetChanged();
            frameLayoutUpdate.setVisibility(View.GONE);
        }


    }

    private void changeViewIfChecked(ToggleButton button, Schedule schedule, int index) {
        button.setChecked(schedule.hasDay(index));
        if (schedule.hasDay(index)) {
            button.setTextColor(context.getResources().getColor(R.color.colorWhite));
        }
    }

    private String getselectedDays(Schedule schedule) {
        StringBuilder selectedDays = new StringBuilder();
        for (int i = 0; i < schedule.getDays().size(); i++) {
            int index = (int) schedule.getDays().get(i);
            if (i == 0) {
                selectedDays.append(days[index]);
            } else {
                selectedDays.append(", ").append(days[index]);
            }
        }
        return String.valueOf(selectedDays);
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

}
