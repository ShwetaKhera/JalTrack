package com.jaltrack;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.project.shweta.jaltrack.R;

public class TipsFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tips, container, false);

        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        interstitialAdLoader(view);
        nativeAdLoader(view);

        return view;
    }

    private void interstitialAdLoader(final View view) {
        final InterstitialAd interstitialAd = new InterstitialAd(view.getContext());
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                interstitialAd.show();
            }
        });
    }

    private void nativeAdLoader(final View view) {
        AdLoader adLoader = new AdLoader.Builder(view.getContext(),
                getResources().getString(R.string.native_advanced_ad_id))
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        NativeTemplateStyle nativeTemplateStyle = new
                                NativeTemplateStyle.Builder().build();

                        TemplateView templateView = view.findViewById(R.id.native_advanced_ad_view);
                        templateView.setStyles(nativeTemplateStyle);
                        templateView.setNativeAd(unifiedNativeAd);
                    }
                }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

}
