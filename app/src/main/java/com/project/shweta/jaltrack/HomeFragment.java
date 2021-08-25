package com.project.shweta.jaltrack;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import me.itangqi.waveloadingview.WaveLoadingView;

import static com.project.shweta.jaltrack.NewUserDataFragment.KEY_VOLUMETODRINK;
import static com.project.shweta.jaltrack.NewUserDataFragment.PREFERENCE_NAME;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private View view;

    private WaveLoadingView waveLoadingView;
    private FloatingActionButton floatingActionButton;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //Inflate the layout for this fragment

        view = inflater.inflate(
                R.layout.fragment_home, container, false);

        waveLoadingView = view.findViewById(R.id.waveLoadingView);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(this);
        if (getContext() != null) {
            SharedPreferences sharedPreferences = getContext().getApplicationContext()
                    .getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            int volume = sharedPreferences.getInt(KEY_VOLUMETODRINK, 0);

            System.out.println(sharedPreferences.getInt(KEY_VOLUMETODRINK, 0));

            waveLoadingView.setProgressValue(0);
//            waveLoadingView.setAnimDuration(2000);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        if (floatingActionButton.equals(view)) {
            if (waveLoadingView.getProgressValue() < 90) {
                waveLoadingView.setProgressValue(waveLoadingView.getProgressValue() + 100 / 8);
            }
        }
    }
}
