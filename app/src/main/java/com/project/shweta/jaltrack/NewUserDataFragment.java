package com.project.shweta.jaltrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TimePicker;

import androidx.fragment.app.Fragment;

public class NewUserDataFragment extends Fragment implements View.OnClickListener, TextWatcher {

    public static final String KEY_WEIGHT = "WEIGHT";
    public static final String KEY_AGE = "AGE";
    public static final String KEY_WAKEUPHOUR = "WAKEUPHOUR";
    public static final String KEY_WAKEUPMIN = "WAKEUPMIN";
    public static final String KEY_ISWAKEUPAM = "ISWAKEUPAM";
    public static final String KEY_BEDTIMEHOUR = "BEDTIMEHOUR";
    public static final String KEY_BEDTIMEMIN = "BEDTIMEMIN";
    public static final String KEY_ISBEDTIMEAM = "ISBEDTIMEAM";
    public static final String KEY_VOLUMETODRINK = "VOLUMETODRINK";
    public static final String PREFERENCE_NAME = "JALTRACK_DATA";

    public static SharedPreferences SHARED_PREFERENCES;

    private int page = 1;
    private int age = 1;
    private int weight;
    private int wakeUpHH;
    private int wakeUpMM;
    private int bedTimeHH;
    private int bedTimeMM;
    private boolean isWakeUpAM, isBedTimeAM;

    private View view, lineBetweenButton1, lineBetweenButton2;
    private NumberPicker agePicker;
    private EditText editTextWeight;
    private RadioButton radioButtonPageOne, radioButtonPageTwo, radioButtonPageThree;
    private HorizontalScrollView horizontalScrollView;
    private Button buttonPrev, buttonNext;
    private TimePicker pickerWakeUpTime, pickerBedTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_new_user_data, container, false);

        lineBetweenButton1 = view.findViewById(R.id.lineBetweenButton1);
        lineBetweenButton2 = view.findViewById(R.id.lineBetweenButton2);
        agePicker = view.findViewById(R.id.agePicker);
        editTextWeight = view.findViewById(R.id.weight);
        radioButtonPageOne = view.findViewById(R.id.radioButtonPageOne);
        radioButtonPageTwo = view.findViewById(R.id.radioButtonPageTwo);
        radioButtonPageThree = view.findViewById(R.id.radioButtonPageThree);
        buttonPrev = view.findViewById(R.id.buttonPrev);
        buttonNext = view.findViewById(R.id.buttonNext);
        horizontalScrollView = view.findViewById(R.id.horizontalScrollView);
        pickerBedTime = view.findViewById(R.id.pickerBedTime);
        pickerWakeUpTime = view.findViewById(R.id.pickerWakeUpTime);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            wakeUpHH = pickerWakeUpTime.getHour();
            wakeUpMM = pickerWakeUpTime.getMinute();
            isWakeUpAM = wakeUpHH < 13;
            bedTimeHH = pickerBedTime.getHour();
            bedTimeMM = pickerBedTime.getMinute();
            isBedTimeAM = bedTimeHH < 13;

        }

        setUpAgePicker();

        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        pickerWakeUpTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                wakeUpHH = i;
                wakeUpMM = i1;
                isWakeUpAM = wakeUpHH < 13;
                System.out.println("Wake Up Time:- " + wakeUpHH + ":" + wakeUpMM + " " + isWakeUpAM);
            }
        });
        pickerBedTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                bedTimeHH = i;
                bedTimeMM = i1;
                isBedTimeAM = bedTimeHH < 13;
                System.out.println("Bed Time:- " + bedTimeHH + ":" + bedTimeMM + " " + isBedTimeAM);
            }
        });
        radioButtonPageOne.setOnClickListener(this);
        radioButtonPageTwo.setOnClickListener(this);
        radioButtonPageThree.setOnClickListener(this);
        buttonPrev.setOnClickListener(this);
        buttonNext.setOnClickListener(this);
        editTextWeight.addTextChangedListener(this);

        return view;
    }

    public void setUpAgePicker() {
        agePicker.setMinValue(1);
        agePicker.setMaxValue(150);
        agePicker.setWrapSelectorWheel(true);
        agePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                age = newVal;
                System.out.println(age + " " + weight);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (radioButtonPageOne.equals(view)) {

            page = 1;
            lineBetweenButton1.setBackgroundColor(Color.parseColor("#1a1a1a"));
            lineBetweenButton2.setBackgroundColor(Color.parseColor("#1a1a1a"));
            radioButtonPageTwo.setChecked(false);
            radioButtonPageThree.setChecked(false);
            horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);
            buttonPrev.setVisibility(View.GONE);
            buttonNext.setText("NEXT");

        } else if (radioButtonPageTwo.equals(view)) {

            page = 2;
            lineBetweenButton1.setBackgroundColor(Color.parseColor("#D81B60"));
            lineBetweenButton2.setBackgroundColor(Color.parseColor("#1a1a1a"));
            radioButtonPageThree.setChecked(false);
            horizontalScrollView.scrollTo(horizontalScrollView.getWidth(), 0);
            buttonPrev.setVisibility(View.VISIBLE);
            buttonNext.setText("NEXT");

        } else if (radioButtonPageThree.equals(view)) {

            page = 3;
            lineBetweenButton1.setBackgroundColor(Color.parseColor("#D81B60"));
            lineBetweenButton2.setBackgroundColor(Color.parseColor("#D81B60"));
            horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            buttonPrev.setVisibility(View.VISIBLE);
            buttonNext.setText("FINISH");

        } else if (buttonPrev.equals(view)) {

            if (page == 2) {
                radioButtonPageOne.performClick();
            } else if (page == 3) {
                radioButtonPageTwo.performClick();
            }

        } else if (buttonNext.equals(view)) {

            if (buttonNext.getText().toString().equalsIgnoreCase("NEXT")) {
                if (page == 1) {
                    radioButtonPageTwo.performClick();
                } else if (page == 2) {
                    radioButtonPageThree.performClick();
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                    double volumeToDrink;

                    if (age < 30) {
                        volumeToDrink = (weight / 2.2) * 40;
                    } else if (age < 55) {
                        volumeToDrink = (weight / 2.2) * 35;
                    } else {
                        volumeToDrink = (weight / 2.2) * 30;
                    }
                    volumeToDrink = volumeToDrink / 28.3;

                    if (getContext() != null) {
                        SHARED_PREFERENCES = getContext().getApplicationContext()
                                .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = SHARED_PREFERENCES.edit();
                        editor.putInt(KEY_WEIGHT, weight);
                        editor.putInt(KEY_AGE, age);
                        editor.putInt(KEY_WAKEUPHOUR, wakeUpHH);
                        editor.putInt(KEY_WAKEUPMIN, wakeUpMM);
                        editor.putBoolean(KEY_ISWAKEUPAM, isWakeUpAM);
                        editor.putInt(KEY_BEDTIMEHOUR, bedTimeHH);
                        editor.putInt(KEY_BEDTIMEMIN, bedTimeMM);
                        editor.putBoolean(KEY_ISBEDTIMEAM, isBedTimeAM);
                        editor.putInt(KEY_VOLUMETODRINK, (int) volumeToDrink);

                        System.out.println(weight + " " + age + " " + wakeUpHH + " " + wakeUpMM + " " +
                                isWakeUpAM + " " + bedTimeHH + " " + bedTimeMM + " " + isBedTimeAM + " " + volumeToDrink);
                        editor.apply();
                        if (getActivity() != null) {
                            ((MainActivity) getActivity())
                                    .changeFragment(((MainActivity) getActivity()).fragmentHome);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.length() > 0) {
            weight = Integer.parseInt(charSequence.toString());
            System.out.println(weight);
            buttonNext.setEnabled(weight > 0);
        } else {
            buttonNext.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
