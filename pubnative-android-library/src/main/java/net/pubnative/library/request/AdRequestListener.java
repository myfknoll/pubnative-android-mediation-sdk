package net.pubnative.library.request;

import java.util.ArrayList;

import net.pubnative.library.model.NativeAdModel;

public interface AdRequestListener
{
    void onAdRequestStarted(AdRequest request);

    void onAdRequestFinished(AdRequest request, ArrayList<? extends NativeAdModel> ads);

    void onAdRequestFailed(AdRequest request, Exception ex);
}
