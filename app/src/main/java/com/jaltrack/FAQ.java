package com.jaltrack;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.project.shweta.jaltrack.R;

public class FAQ extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageBack;
    private Button btnGoToSettings;
    private AdView adViewBanner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        imageBack = findViewById(R.id.btn_back);
        imageBack.setOnClickListener(this);
        btnGoToSettings = findViewById(R.id.btn_go_to_settings);
        btnGoToSettings.setOnClickListener(this);

        adViewBanner = findViewById(R.id.adView_banner);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewBanner.loadAd(adRequest);
    }

    @Override
    public void onClick(View view) {
        if (view == imageBack) {
            finish();
        } else if (view == btnGoToSettings) {
            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
        }
    }
}
