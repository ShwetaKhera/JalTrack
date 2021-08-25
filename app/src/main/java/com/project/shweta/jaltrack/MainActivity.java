package com.project.shweta.jaltrack;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static com.project.shweta.jaltrack.NewUserDataFragment.PREFERENCE_NAME;

public class MainActivity extends AppCompatActivity {

    Fragment fragmentHome = new HomeFragment();
    Fragment fragmentNewUserData = new NewUserDataFragment();
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkFirstRun();
    }

    private void checkFirstRun() {

        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            System.out.println(getClass().getSimpleName() + " : Normal run");
            SharedPreferences sharedPreferences = this.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
            if (sharedPreferences != null) {
                changeFragment(fragmentHome);
            } else {
                changeFragment(fragmentNewUserData);
            }
        } else if (savedVersionCode == DOESNT_EXIST) {
            // TODO This is a new install (or the user cleared the shared preferences)

            System.out.println(getClass().getSimpleName() + " : New Install");
            changeFragment(fragmentNewUserData);

        } else if (currentVersionCode > savedVersionCode) {
            // TODO This is an upgrade

            System.out.println(getClass().getSimpleName() + " : Upgrade run");
            changeFragment(fragmentHome);
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    public void changeFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }
}
