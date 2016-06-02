package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;

import net.pubnative.mediation.adapter.model.AdMobNativeAppInstallAdModel;
import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.request.model.PubnativeAdTargetingModel;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class AdMobNetworkRequestAdapter extends PubnativeNetworkRequestAdapter {

    public static final String    TAG       = AdMobNetworkRequestAdapter.class.getSimpleName();
    protected static final String ADMOB_UNIT_ID   = "unit_id";

    public AdMobNetworkRequestAdapter(Map data) {

        super(data);
    }

    @Override
    protected void request(Context context) {

        Log.v(TAG, "request");
        if (context != null && mData != null) {
            String placementId = (String) mData.get(ADMOB_UNIT_ID);
            if (TextUtils.isEmpty(placementId)) {
                invokeFailed(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
            } else {
                createRequest(context, placementId);
            }
        }
    }

    private void createRequest(Context context, String unitId) {

        Log.v(TAG, "createRequest");

        AdLoader adLoader = new AdLoader.Builder(context, unitId)
                .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {

                    @Override
                    public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {

                        Log.v(TAG, "onAppInstallAdLoaded");
                        AdMobNativeAppInstallAdModel wrapper = new AdMobNativeAppInstallAdModel(nativeAppInstallAd);
                        invokeLoaded(wrapper);
                    }
                })
                .withAdListener(new AdListener() {

                    @Override
                    public void onAdFailedToLoad(int i) {

                        Log.v(TAG, "onAdFailedToLoad");
                        super.onAdFailedToLoad(i);
                        invokeFailed(PubnativeException.ADAPTER_UNKNOWN_ERROR);
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder()
                        .setReturnUrlsForImageAssets(true)
                        .build())
                .build();

        AdRequest request = prepareTargetingData(mTargeting);

        adLoader.loadAd(request);
    }

    private AdRequest prepareTargetingData(PubnativeAdTargetingModel targeting) {

        AdRequest.Builder builder = new AdRequest.Builder();
        int year = Calendar.getInstance().get(Calendar.YEAR);

        if (targeting == null) {
            return builder.addTestDevice("16F5F25826CB21FCB488335014973DA7").build();
        }

        if (targeting.age != null && targeting.age > 0) {
            builder.setBirthday(new GregorianCalendar(year - targeting.age, 1, 1).getTime());
        }

        if (!TextUtils.isEmpty(targeting.gender)){
            builder.setGender(targeting.gender.equalsIgnoreCase("male") ? AdRequest.GENDER_MALE : AdRequest.GENDER_FEMALE);
        }

        return builder.addTestDevice("16F5F25826CB21FCB488335014973DA7").build();
    }
}
