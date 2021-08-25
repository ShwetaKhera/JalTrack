package com.jaltrack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.project.shweta.jaltrack.BuildConfig;
import com.project.shweta.jaltrack.R;

import static com.helper.Constants.MyPREFERENCES;

public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        checkFirstRun();
//        startActivity(new Intent(getApplicationContext(), NewDataActivity.class));

    }

    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        int currentVersionCode = BuildConfig.VERSION_CODE;

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        SharedPreferences sharedPreferences = this.getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        if (currentVersionCode == savedVersionCode) {

            System.out.println(getClass().getSimpleName() + " : Normal run");
            if (sharedPreferences.getAll().size() != 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }
                }, 1000);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), NewDataActivity.class));
                        finish();
                    }
                }, 1000);
            }
        } else if (savedVersionCode == DOESNT_EXIST) {

            System.out.println(getClass().getSimpleName() + " : New Install");
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), NewDataActivity.class));
                    finish();
                }
            }, 1000);

        } else if (currentVersionCode > savedVersionCode) {
            if (sharedPreferences.getAll().size() != 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }
                }, 1000);
                System.out.println(getClass().getSimpleName() + " : Upgrade run");
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), NewDataActivity.class));
                        finish();
                    }
                }, 1000);
            }
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        }
    }
}