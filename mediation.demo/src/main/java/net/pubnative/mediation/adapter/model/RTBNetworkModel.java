package net.pubnative.mediation.adapter.model;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.google.openrtb.OpenRtb;

import net.pubnative.mediation.request.model.PubnativeAdModel;

public class RTBNetworkModel extends PubnativeAdModel {

    public static final int RTBAssetTitleID        = 1;
    public static final int RTBAssetDescriptionlID = 2;
    public static final int RTBAssetIconID         = 3;
    public static final int RTBAssetBannerID       = 4;
    public static final int RTBAssetRatingID       = 5;
    public static final int RTBAssetCTAID          = 6;

    OpenRtb.BidResponse mResponse;

    public RTBNetworkModel(OpenRtb.BidResponse response) {

        mResponse = response;
    }

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
