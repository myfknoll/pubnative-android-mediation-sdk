package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAppInstallAd;

import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.model.AdMobNativeAppInstallAdModel;

import java.util.Map;

/**
 * Created by davidmartin on 03/06/16.
 */
public class AdMobNetworkAdapter extends PubnativeNetworkAdapter
        implements NativeAppInstallAd.OnAppInstallAdLoadedListener {

    public static final    String TAG           = AdMobNetworkAdapter.class.getSimpleName();
    protected static final String ADMOB_UNIT_ID = "unit_id";

    /**
     * Creates a new instance of PubnativeNetworkAdapter
     *
     * @param data server configured data for the current adapter network.
     */
    public AdMobNetworkAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // PubnativeNetworkAdapter methods
    //==============================================================================================
    @Override
    public void request(Context context) {

        Log.v(TAG, "request");
        if (context == null || mData == null) {
            invokeFailed(new Exception(TAG+" - error: invalid arguments"));
        } else {
            String unitID = (String) mData.get(ADMOB_UNIT_ID);
            if (TextUtils.isEmpty(unitID)) {
                invokeFailed(new Exception(TAG+" - error: missing argument unit_id"));
            } else {
                createRequest(context, unitID );
            }
        }
    }
    //==============================================================================================
    // AdMobNetworkAdapter methods
    //==============================================================================================
    protected void createRequest(Context context, String unitId) {

        Log.v(TAG, "createRequest");
        AdLoader adLoader = new AdLoader.Builder(context, unitId)
                .forAppInstallAd(this)
                .withAdListener(new NativeAdListener())
                .build();
        adLoader.loadAd(getAdRequest());
    }

    protected AdRequest getAdRequest() {

        AdRequest.Builder builder = new AdRequest.Builder();
        builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        return builder.build();
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // AdListener
    //----------------------------------------------------------------------------------------------
    protected class NativeAdListener extends com.google.android.gms.ads.AdListener {

        @Override
        public void onAdFailedToLoad(int var1) {

            Log.v(TAG, "onAdFailedToLoad");
            invokeFailed(new Exception(TAG+" - error loading the ad with code:" + String.valueOf(var1)));
        }
    }
    // NativeAppInstallAd.OnAppInstallAdLoadedListener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onAppInstallAdLoaded(NativeAppInstallAd nativeAppInstallAd) {

        Log.v(TAG, "onAppInstallAdLoaded");
        AdMobNativeAppInstallAdModel wrapper = new AdMobNativeAppInstallAdModel(nativeAppInstallAd);
        invokeLoaded(wrapper);
    }
}
