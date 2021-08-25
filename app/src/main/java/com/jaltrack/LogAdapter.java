package com.jaltrack;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.database.Log;
import com.project.shweta.jaltrack.R;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.helper.CommonFunctions.formatTime;
import static com.helper.CommonFunctions.getOffset;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.MyView> {

    private Context context;
    private List<Log> logList;
    private RecyclerView recyclerView;
    private final View.OnClickListener mOnClickListener = new MyOnClickListener();
    private LinearLayout viewEditLog, layoutTimepicker;
    private TextView editTime, waterLevelAmount, index;
    private SeekBar waterLevelHandle;
    private TimePicker timePicker;
    private int adapterPos;

    class MyView extends RecyclerView.ViewHolder {

        private TextView timeOfDrink, amountOfDrink;


        MyView(View v) {
            super(v);

            timeOfDrink = v.findViewById(R.id.time_of_drink);
            amountOfDrink = v.findViewById(R.id.amount_of_drink);
        }
    }

    LogAdapter(Context context, List<Log> logList, View view) {
        this.context = context;
        this.logList = logList;
        this.viewEditLog = view.findViewById(R.id.view_edit_log);
        this.layoutTimepicker = view.findViewById(R.id.layout_timepicker);
        this.timePicker = view.findViewById(R.id.timepicker);
        this.editTime = view.findViewById(R.id.edit_time);
        this.waterLevelAmount = view.findViewById(R.id.water_level_amount);
        this.waterLevelHandle = view.findViewById(R.id.water_level_handle);

        this.index = view.findViewById(R.id.index);
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView
                = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log,
                parent, false);
        recyclerView = (RecyclerView) parent;
        itemView.setOnClickListener(mOnClickListener);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {
        Log item = logList.get(position);
        Timestamp timestamp = Timestamp.valueOf(item.getTimestamp());
        holder.timeOfDrink.setText(formatTime(timestamp.getHours(), timestamp.getMinutes()));
        String displayAmount = "( " + item.getAmount() + "ml )";
        holder.amountOfDrink.setText(displayAmount);
        setAnimation(holder.itemView, holder.getAdapterPosition());
    }


    private void setAnimation(View viewToAnimate, int position) {
        if (position == 0) {
            Animation animation = AnimationUtils.loadAnimation(
                    viewToAnimate.getContext(),
                    android.R.anim.fade_in);
            animation.setDuration(1000);
            viewToAnimate.startAnimation(animation);
        } else {
            Animation animation = AnimationUtils.loadAnimation(
                    viewToAnimate.getContext(), R.anim.push_left_in);
            animation.setDuration(500);
            viewToAnimate.startAnimation(animation);
        }
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }


    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            adapterPos = recyclerView.getChildLayoutPosition(view);
            Log item = logList.get(adapterPos);
            openEditBox(item);
        }
    }

    private void openEditBox(Log item) {
        viewEditLog.setVisibility(View.VISIBLE);
        Timestamp timestamp = Timestamp.valueOf(item.getTimestamp());
        editTime.setText(formatTime(timestamp.getHours(), timestamp.getMinutes()));
        String displayAmount = String.valueOf(item.getAmount());
        waterLevelAmount.setText(displayAmount);
        String displayIndex = String.valueOf(logList.indexOf(item));
        index.setText(displayIndex);

        System.out.println("Index: " + logList.indexOf(item));

        int progress = (item.getAmount() * 100) / 175;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            waterLevelHandle.setProgress(progress, true);
        }
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            date.setTime(date.getTime() + getOffset());
            SimpleDateFormat fmtOut = new SimpleDateFormat("HH:mm");

            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

    public void changeTimeInDB() {
        layoutTimepicker.setVisibility(View.GONE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            NumberPicker hh = context.findViewById(Resources.getSystem().getIdentifier("hour",
//                    "id", "android"));
//            hh.setEnabled(true);
            int hourOfDay = timePicker.getHour();
            int minute = timePicker.getMinute();
            System.out.println("TIME OF REMINDER: " + formatTime(hourOfDay, minute));

            Calendar calendar = Calendar.getInstance();
            if (hourOfDay < 12) {
                calendar.set(Calendar.HOUR, hourOfDay);
                calendar.set(Calendar.AM_PM, Calendar.AM);
            } else {
                calendar.set(Calendar.HOUR, hourOfDay - 12);
                calendar.set(Calendar.AM_PM, Calendar.PM);
            }
            calendar.set(Calendar.MINUTE, minute);
            Timestamp timestamp = new Timestamp(calendar.getTime().getTime());
//            timestamp.setTime(timestamp.getTime() - getOffset());

            System.out.println("UDPATED TIME: " + timestamp.toString());
            logList.get(adapterPos).setTimestamp(timestamp.toString());
            editTime.setText(formatTime(hourOfDay, minute));
        }
    }

}
