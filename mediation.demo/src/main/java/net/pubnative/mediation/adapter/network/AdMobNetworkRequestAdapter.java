package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;

import net.pubnative.mediation.adapter.model.AdMobNativeAdModel;
import net.pubnative.mediation.adapter.model.AdMobNativeContentAdModel;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.Map;

public class AdMobNetworkRequestAdapter extends PubnativeNetworkRequestAdapter {

    public static final String    TAG       = AdMobNetworkRequestAdapter.class.getSimpleName();
    protected static final String ADMOB_UNIT_ID   = "unit_id";
    protected static final String ADMOB_APP_ID   = "app_id";

    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-2479858864815484/9847796057";
    private static final String ADMOB_AD_APP_ID = "ca-app-pub-2479858864815484~8371062858";

    public AdMobNetworkRequestAdapter(Map data) {

        super(data);
    }

    @Override
    protected void request(Context context) {

        Log.v(TAG, "request");
        if (context != null && mData != null) {
            String placementId = (String) mData.get(ADMOB_UNIT_ID);
            String appId = (String) mData.get(ADMOB_APP_ID);
            if (TextUtils.isEmpty(placementId)) {
                invokeFailed(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
            } else {
                createRequest(context, placementId, appId);
            }
        }
    }

    private void createRequest(Context context, String unitId, String appId) {

        Log.d(TAG, "createRequest() called with: " + "unitId = [" + unitId + "], appId = [" + appId + "]");

        MobileAds.initialize(context, ADMOB_AD_APP_ID);

        //AdLoader.Builder builder = new AdLoader.Builder(context, unitId);
        AdLoader adLoader = new AdLoader.Builder(context, ADMOB_AD_UNIT_ID)
                .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {

                    @Override
                    public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {

                        Log.d(TAG, "onAppInstallAdLoaded() called with: " + "nativeAppInstallAd = [" + nativeAppInstallAd + "]");
                        AdMobNativeAdModel wrapper = new AdMobNativeAdModel(nativeAppInstallAd);
                        invokeLoaded(wrapper);
                    }
                })
                .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {

                    @Override
                    public void onContentAdLoaded(NativeContentAd nativeContentAd) {

                        Log.d(TAG, "onContentAdLoaded() called with: " + "nativeContentAd = [" + nativeContentAd + "]");
                        AdMobNativeContentAdModel wrapper = new AdMobNativeContentAdModel(nativeContentAd);
                        invokeLoaded(wrapper);
                    }
                })
                .withAdListener(new AdListener() {

                    @Override
                    public void onAdFailedToLoad(int i) {

                        Log.e(TAG, "onAdFailedToLoad with code " + String.valueOf(i));
                        super.onAdFailedToLoad(i);
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        .setReturnUrlsForImageAssets(true)
                        .build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().addTestDevice("16F5F25826CB21FCB488335014973DA7").build());
    }
}
