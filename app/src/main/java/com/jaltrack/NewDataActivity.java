package com.jaltrack;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.shweta.jaltrack.R;

import static com.helper.CommonFunctions.calculateIntakeFromWeightAndAge;
import static com.helper.CommonFunctions.convertDpToPixel;
import static com.helper.Constants.AGE;
import static com.helper.Constants.BEDTIME;
import static com.helper.Constants.ERROR_AGE_EMPTY;
import static com.helper.Constants.ERROR_AGE_ILLEGAL_ENTRY;
import static com.helper.Constants.ERROR_TIME_ILLEGAL_ENTRY;
import static com.helper.Constants.ERROR_WEIGHT_EMPTY;
import static com.helper.Constants.ERROR_WEIGHT_ILLEGAL_ENTRY;
import static com.helper.Constants.INTAKEGOAL;
import static com.helper.Constants.MyPREFERENCES;
import static com.helper.Constants.WAKEUPTIME;
import static com.helper.Constants.WEIGHT;

public class NewDataActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private LinearLayout layoutWeight, layoutAge, layoutWakeUpTime, layoutBedTime, layoutProgress;
    private EditText editTextWeight, editTextAge;
    private TimePicker timePickerWakeUpTime, timePickerBedTime;
    private TextView tab1, tab2, tab3, tab4, textViewError;
    private ImageView imageBtnPrev, imageBtnNext;

    private static int tabCount = 0;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_data);

        layoutProgress = findViewById(R.id.layoutProgress);
        layoutWeight = findViewById(R.id.layout_weight);
        layoutWeight.setOnTouchListener(new LayoutTouchListener(this));
        layoutAge = findViewById(R.id.layout_edit_age);
        layoutAge.setOnTouchListener(new LayoutTouchListener(this));
        layoutWakeUpTime = findViewById(R.id.layout_wake_up_time);
        layoutWakeUpTime.setOnTouchListener(new LayoutTouchListener(this));
        layoutBedTime = findViewById(R.id.layout_bed_time);
        layoutBedTime.setOnTouchListener(new LayoutTouchListener(this));

        imageBtnNext = findViewById(R.id.image_button_next);
        imageBtnNext.setOnClickListener(this);
        imageBtnPrev = findViewById(R.id.image_button_back);
        imageBtnPrev.setOnClickListener(this);

        editTextWeight = findViewById(R.id.editText_weight);
        editTextWeight.requestFocus();
        editTextWeight.setOnLongClickListener(this);
        editTextWeight.setLongClickable(false);

        editTextAge = findViewById(R.id.editText_age);
        editTextAge.setOnLongClickListener(this);
        editTextAge.setLongClickable(false);

        timePickerWakeUpTime = findViewById(R.id.timepicker_wakeup_time);
        timePickerBedTime = findViewById(R.id.timepicker_bed_time);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePickerWakeUpTime.setHour(7);
            timePickerWakeUpTime.setMinute(0);
            timePickerBedTime.setHour(23);
            timePickerBedTime.setMinute(0);
        }


        tab1 = findViewById(R.id.tab1);
        animateTabLineToActive(tab1);
        tab2 = findViewById(R.id.tab2);
        tab3 = findViewById(R.id.tab3);
        tab4 = findViewById(R.id.tab4);

        textViewError = findViewById(R.id.textView_error);

    }

    @Override
    public void onClick(View view) {
        if (view == imageBtnNext) {
            switchToNextTab();
        } else if (view == imageBtnPrev) {
            switchToPreviousTab();
        }
    }

    private boolean validateInput() {
        if (tabCount == 0) {
            if (editTextWeight.getText().length() > 0) {
                if (Integer.parseInt(editTextWeight.getText().toString()) >= 70
                        && Integer.parseInt(editTextWeight.getText().toString()) <= 350) {
                    return true;
                } else {
                    showErrorMessage(ERROR_WEIGHT_ILLEGAL_ENTRY);
                    editTextWeight.requestFocus();
                    return false;
                }
            } else {
                showErrorMessage(ERROR_WEIGHT_EMPTY);
                editTextWeight.requestFocus();
                return false;
            }
        } else if (tabCount == 1) {
            if (editTextAge.getText().length() > 0) {
                if (Integer.parseInt(editTextAge.getText().toString()) >= 10
                        && Integer.parseInt(editTextAge.getText().toString()) <= 100) {
                    return true;
                } else {
                    showErrorMessage(ERROR_AGE_ILLEGAL_ENTRY);
                    editTextAge.requestFocus();
                    return false;
                }
            } else {
                showErrorMessage(ERROR_AGE_EMPTY);
                editTextAge.requestFocus();
                return false;
            }
        } else if (tabCount == 3) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (timePickerBedTime.getHour() > timePickerWakeUpTime.getHour()) {
                    timePickerBedTime.setIs24HourView(true);
                    timePickerWakeUpTime.setIs24HourView(true);
                    return true;
                } else {
                    showErrorMessage(ERROR_TIME_ILLEGAL_ENTRY);
                    return false;
                }
            }
        }
        return true;
    }

    private void changeNextAndPrevBtnVisibility(int prev, int next) {
        imageBtnPrev.setVisibility(prev);
        imageBtnNext.setVisibility(next);
    }

    private void switchToNextTab() {
        switch (tabCount) {
            case 0:
                editTextAge.requestFocus();
                swipeToNext(layoutWeight, layoutAge);
                break;
            case 1:
                InputMethodManager imm = (InputMethodManager)
                        getBaseContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextAge.getWindowToken(), 0);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timePickerWakeUpTime.requestFocus();
                        swipeToNext(layoutAge, layoutWakeUpTime);
                    }
                }, 500);
                break;
            case 2:
                timePickerBedTime.requestFocus();
                timePickerBedTime.setVisibility(View.VISIBLE);
                swipeToNext(layoutWakeUpTime, layoutBedTime);
                break;
            case 3:
                if (!validateInput()) {
                    return;
                }
                layoutProgress.setVisibility(View.VISIBLE);
                changeNextAndPrevBtnVisibility(View.GONE, View.GONE);
                setDataToSharedPref();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }
                }, 2000);
        }
    }

    private void showErrorMessage(String message) {
        textViewError.setText(message);
        textViewError.animate()
                .translationY(textViewError.getHeight())
                .alpha(1)
                .setDuration(300)
                .start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                textViewError.animate()
                        .translationY(0)
                        .alpha(0)
                        .setDuration(300)
                        .start();
            }
        }, 2000);
    }

    private void setDataToSharedPref() {
        int age = Integer.parseInt(editTextAge.getText().toString());
        int weightInPounds = Integer.parseInt(editTextWeight.getText().toString());

        sharedPreferences = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(WEIGHT, weightInPounds);
        editor.putInt(AGE, age);
        editor.putInt(INTAKEGOAL, calculateIntakeFromWeightAndAge(age, weightInPounds));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            editor.putString(WAKEUPTIME,
                    formatTime(timePickerWakeUpTime.getHour(), timePickerWakeUpTime.getMinute()));

            editor.putString(BEDTIME,
                    formatTime(timePickerBedTime.getHour(), timePickerBedTime.getMinute()));
        }
        editor.apply();
    }

    private String formatTime(int hh, int mm) {
        String time;
        if (hh < 10) {
            if (mm < 10) {
                time = "0" + hh + ":" + "0" + mm;
            } else {
                time = "0" + hh + ":" + mm;
            }
        } else {
            if (mm < 10) {
                time = hh + ":" + "0" + mm;
            } else {
                time = hh + ":" + mm;
            }
        }
        return time;
    }


    private void changeActiveTabLine() {
        switch (tabCount) {
            case 0:
                animateTabLineToActive(tab1);
                animateTabLineToInactive(tab2);
                animateTabLineToInactive(tab3);
                animateTabLineToInactive(tab4);
                break;
            case 1:
                animateTabLineToActive(tab2);
                animateTabLineToInactive(tab1);
                animateTabLineToInactive(tab3);
                animateTabLineToInactive(tab4);
                break;
            case 2:
                animateTabLineToActive(tab3);
                animateTabLineToInactive(tab2);
                animateTabLineToInactive(tab1);
                animateTabLineToInactive(tab4);
                break;
            case 3:
                animateTabLineToActive(tab4);
                animateTabLineToInactive(tab2);
                animateTabLineToInactive(tab3);
                animateTabLineToInactive(tab1);
                break;
        }
    }

    private void animateTabLineToActive(TextView tab) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tab.setBackgroundTintList(getResources().getColorStateList(R.color.colorPrimaryDark));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    convertDpToPixel(this, 20), convertDpToPixel(this, 3));
            params.setMargins(
                    convertDpToPixel(this, 5),
                    0,
                    convertDpToPixel(this, 5),
                    0);
            tab.setLayoutParams(params);
        }
        tab.animate()
                .scaleY(1.5f)
                .scaleX(1.5f)
                .setDuration(100)
                .start();
    }


    private void animateTabLineToInactive(TextView tab) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tab.setBackgroundTintList(getResources().getColorStateList(R.color.colorGrey));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    convertDpToPixel(this, 20), convertDpToPixel(this, 1));
            params.setMargins(
                    convertDpToPixel(this, 5),
                    0,
                    convertDpToPixel(this, 5),
                    0);
            tab.setLayoutParams(params);
        }
        tab.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(0)
                .start();
    }

    private void swipeToNext(LinearLayout layoutToHide, LinearLayout layoutToShow) {
        if (!validateInput()) {
            return;
        }
        changeNextAndPrevBtnVisibility(View.VISIBLE, View.VISIBLE);
        tabCount++;
        changeActiveTabLine();
        layoutToHide.animate()
                .translationX(-layoutToHide.getWidth())
                .alpha(0)
                .setDuration(300)
                .start();
        layoutToHide.setVisibility(View.GONE);
        layoutToShow.animate()
                .translationX(0)
                .alpha(1)
                .setDuration(300)
                .start();
        layoutToShow.setVisibility(View.VISIBLE);
    }

    private void swipeToPrev(LinearLayout layoutToHide, LinearLayout layoutToShow) {
        tabCount--;
        changeActiveTabLine();
        layoutToHide.animate()
                .translationX(layoutToHide.getWidth())
                .alpha(0)
                .setDuration(300)
                .start();
        layoutToHide.setVisibility(View.GONE);
        layoutToShow.animate()
                .translationX(0)
                .alpha(1)
                .setDuration(300)
                .start();
        layoutToShow.setVisibility(View.VISIBLE);
    }

    private void switchToPreviousTab() {
        switch (tabCount) {
            case 1:
                layoutWeight.requestFocus();
                changeNextAndPrevBtnVisibility(View.GONE, View.VISIBLE);
                swipeToPrev(layoutAge, layoutWeight);
                break;
            case 2:
                layoutAge.requestFocus();
                changeNextAndPrevBtnVisibility(View.VISIBLE, View.VISIBLE);
                swipeToPrev(layoutWakeUpTime, layoutAge);
                break;
            case 3:
                timePickerBedTime.setVisibility(View.GONE);
                timePickerWakeUpTime.requestFocus();
                changeNextAndPrevBtnVisibility(View.VISIBLE, View.VISIBLE);
                swipeToPrev(layoutBedTime, layoutWakeUpTime);
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    class LayoutTouchListener implements View.OnTouchListener {

        private NewDataActivity activity;
        private float downX, downY, upX, upY;

        public LayoutTouchListener(NewDataActivity activity) {
            this.activity = activity;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            System.out.println("TOUCH EVENT: " + motionEvent.getAction());
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return swipeDownDetected(motionEvent);
                case MotionEvent.ACTION_UP:
                    return swipeUpDetected(motionEvent);
            }
            return false;
        }

        private boolean swipeDownDetected(MotionEvent event) {
            downX = event.getX();
            downY = event.getY();
            return true;
        }

        private boolean swipeUpDetected(MotionEvent event) {
            upX = event.getX();
            upY = event.getY();

            float deltaX = downX - upX;

            int MIN_DISTANCE = 100;
            if (Math.abs(deltaX) > MIN_DISTANCE) {
                if (deltaX < 0) {
                    this.onLeftToRightSwipe();
                    return true;
                }
                if (deltaX > 0) {
                    this.onRightToLeftSwipe();
                    return true;
                }
            }
            return false;
        }

        void onRightToLeftSwipe() {
            // activity.doSomething();
            switchToNextTab();
        }

        void onLeftToRightSwipe() {
            // activity.doSomething();
            switchToPreviousTab();
        }
    }
}
