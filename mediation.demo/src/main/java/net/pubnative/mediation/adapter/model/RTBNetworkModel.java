package net.pubnative.mediation.adapter.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import net.pubnative.mediation.request.model.PubnativeAdModel;

public class RTBNetworkModel extends PubnativeAdModel {

    @Override
    public String getTitle() {

        return null;
    }

    @Override
    public String getDescription() {

        return null;
    }

    @Override
    public String getIconUrl() {

        return null;
    }

    @Override
    public String getBannerUrl() {

        return null;
    }

    @Override
    public String getCallToAction() {

        return null;
    }

    @Override
    public float getStarRating() {

        return 0;
    }

    @Override
    public View getAdvertisingDisclosureView(Context context) {

        return null;
    }

    @Override
    public void startTracking(Context context, ViewGroup adView) {

    }

    @Override
    public void stopTracking() {

    }
}
