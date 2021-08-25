package com.jaltrack;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.database.WaterLogDatabaseHelper;
import com.project.shweta.jaltrack.R;

import java.util.Objects;

import static com.helper.CommonFunctions.calculateIntakeFromWeightAndAge;
import static com.helper.Constants.AGE;
import static com.helper.Constants.BEDTIME;
import static com.helper.Constants.INTAKEGOAL;
import static com.helper.Constants.MyPREFERENCES;
import static com.helper.Constants.WAKEUPTIME;
import static com.helper.Constants.WEIGHT;

public class SettingsFragment
        extends Fragment
        implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private TextView textViewReminderSchedule, intakeGoal, textViewWeight, textViewWakeupTime,
            textViewBedTime, textViewResetData, newIntakeAmt, textViewAge, textNotWorking,
            textPrivacyPolicy;
    private LinearLayout layoutEditIntakeGoal, layoutEditAge, layoutEditWeight;
    private SeekBar seekBarIntakeGoal;
    private NumberPicker numberPickerAge, numberPickerWeight;
    private RelativeLayout confirmResetData;
    private Button btnCancel, btnDelete, btnCancelIntakeGoal, btnOkIntakeGoal, btnCancelAge,
            btnOkAge, btnCancelWeight, btnOkWeight, btnReset;

    private SharedPreferences sharedPreferences;
    private WaterLogDatabaseHelper waterLogDatabaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        textViewReminderSchedule = view.findViewById(R.id.reminder_schedule);
        textViewReminderSchedule.setOnClickListener(this);
        intakeGoal = view.findViewById(R.id.intake_goal);
        intakeGoal.setOnClickListener(this);
        textViewAge = view.findViewById(R.id.text_age);
        textViewAge.setOnClickListener(this);
        textViewWeight = view.findViewById(R.id.weight);
        textViewWeight.setOnClickListener(this);
        textViewWakeupTime = view.findViewById(R.id.wakeup_time);
        textViewBedTime = view.findViewById(R.id.bed_time);
        textViewResetData = view.findViewById(R.id.reset_data);
        textViewResetData.setOnClickListener(this);
        textNotWorking = view.findViewById(R.id.not_working);
        textNotWorking.setOnClickListener(this);
        textPrivacyPolicy = view.findViewById(R.id.privacy_policy);
        textPrivacyPolicy.setOnClickListener(this);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        btnDelete = view.findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(this);

        newIntakeAmt = view.findViewById(R.id.text_new_intake_amount);
        layoutEditIntakeGoal = view.findViewById(R.id.layout_edit_intake_goal);
        seekBarIntakeGoal = view.findViewById(R.id.seekBar_intake_goal);
        seekBarIntakeGoal.setOnSeekBarChangeListener(this);
        btnCancelIntakeGoal = view.findViewById(R.id.btn_cancel_intake_goal);
        btnCancelIntakeGoal.setOnClickListener(this);
        btnOkIntakeGoal = view.findViewById(R.id.btn_ok_intake_goal);
        btnOkIntakeGoal.setOnClickListener(this);
        btnReset = view.findViewById(R.id.btn_reset_intake_goal);
        btnReset.setOnClickListener(this);

        layoutEditAge = view.findViewById(R.id.layout_edit_age);
        numberPickerAge = view.findViewById(R.id.numberPicker_age);
        numberPickerAge.setMinValue(10);
        numberPickerAge.setMaxValue(100);
        btnCancelAge = view.findViewById(R.id.btn_cancel_age);
        btnCancelAge.setOnClickListener(this);
        btnOkAge = view.findViewById(R.id.btn_ok_age);
        btnOkAge.setOnClickListener(this);

        layoutEditWeight = view.findViewById(R.id.layout_edit_weight);
        numberPickerWeight = view.findViewById(R.id.numberPicker_weight);
        numberPickerWeight.setMinValue(70);
        numberPickerWeight.setMaxValue(350);
        btnCancelWeight = view.findViewById(R.id.btn_cancel_weight);
        btnCancelWeight.setOnClickListener(this);
        btnOkWeight = view.findViewById(R.id.btn_ok_weight);
        btnOkWeight.setOnClickListener(this);

        confirmResetData = view.findViewById(R.id.confirm_reset_data);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sharedPreferences = Objects.requireNonNull(
                    getActivity()).getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            waterLogDatabaseHelper = new WaterLogDatabaseHelper(Objects.requireNonNull(getActivity()));
        }

        setData();
        return view;
    }

    private void setData() {
        if (getActivity() != null) {
            int goalVal = sharedPreferences.getInt(INTAKEGOAL, 1470);
            String weightInString = sharedPreferences.getInt(WEIGHT, 132) + " lb";
            intakeGoal.setText(goalVal + " ml");
            textViewWeight.setText(weightInString);
            textViewWakeupTime.setText(sharedPreferences.getString(WAKEUPTIME, "07:00 AM"));
            textViewBedTime.setText(sharedPreferences.getString(BEDTIME, "11:00 PM"));

            newIntakeAmt.setText(goalVal + "");
            seekBarIntakeGoal.setProgress(goalVal);
            numberPickerAge.setValue(sharedPreferences.getInt(AGE, 23));
            numberPickerWeight.setValue(sharedPreferences.getInt(WEIGHT, 132));
        }
    }

    @Override
    public void onClick(View view) {
        if (view == textViewReminderSchedule) {
            startActivity(new Intent(getActivity(), ScheduleActivity.class));
        } else if (view == textViewResetData) {
            confirmResetData.setVisibility(View.VISIBLE);
        } else if (view == btnCancel) {
            confirmResetData.setVisibility(View.GONE);
        } else if (view == btnDelete) {
            waterLogDatabaseHelper.deleteAllLog();
            confirmResetData.setVisibility(View.GONE);
            Toast toast = Toast.makeText(
                    getActivity(),
                    "Your data has been reset successfully",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (view == intakeGoal) {
            layoutEditIntakeGoal.setVisibility(View.VISIBLE);
        } else if (view == btnCancelIntakeGoal) {
            layoutEditIntakeGoal.setVisibility(View.GONE);
        } else if (view == btnOkIntakeGoal) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(INTAKEGOAL, seekBarIntakeGoal.getProgress());
            editor.apply();
            setData();
            layoutEditIntakeGoal.setVisibility(View.GONE);
        } else if (view == textViewAge) {
            layoutEditAge.setVisibility(View.VISIBLE);
        } else if (view == btnCancelAge) {
            layoutEditAge.setVisibility(View.GONE);
        } else if (view == btnOkAge) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(AGE, numberPickerAge.getValue());
            editor.putInt(INTAKEGOAL, calculateIntakeFromWeightAndAge(
                    numberPickerAge.getValue(), sharedPreferences.getInt(WEIGHT, 132)));
            editor.apply();
            setData();
            layoutEditAge.setVisibility(View.GONE);
        } else if (view == textViewWeight) {
            layoutEditWeight.setVisibility(View.VISIBLE);
        } else if (view == btnCancelWeight) {
            layoutEditWeight.setVisibility(View.GONE);
        } else if (view == btnOkWeight) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(WEIGHT, numberPickerWeight.getValue());
            editor.putInt(INTAKEGOAL, calculateIntakeFromWeightAndAge(
                    sharedPreferences.getInt(AGE, 23), numberPickerWeight.getValue()));
            editor.apply();
            setData();
            layoutEditWeight.setVisibility(View.GONE);
        } else if (view == btnReset) {
            int age = sharedPreferences.getInt(AGE, 23);
            int weight = sharedPreferences.getInt(WEIGHT, 132);
            seekBarIntakeGoal.setProgress(calculateIntakeFromWeightAndAge(age, weight));
        } else if (view == textNotWorking) {
            startActivity(new Intent(getActivity(), FAQ.class));
        } else if (view == textPrivacyPolicy) {
            startActivity(new Intent(getActivity(), PrivacyPolicy.class));
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == seekBarIntakeGoal) {
            newIntakeAmt.setText(i + " ml");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
