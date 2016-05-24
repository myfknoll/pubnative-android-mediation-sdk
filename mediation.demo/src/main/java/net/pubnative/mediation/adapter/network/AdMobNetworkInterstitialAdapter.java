package net.pubnative.mediation.adapter.network;

import android.content.Context;

import java.util.Map;

public class AdMobNetworkInterstitialAdapter extends PubnativeNetworkInterstitialAdapter {

    public AdMobNetworkInterstitialAdapter(Map data) {
        super(data);
    }

    @Override
    public void load(Context context) {

    }

    @Override
    public boolean isReady() {

        return false;
    }

    @Override
    public void show() {

    }

    @Override
    public void destroy() {

    }
}
