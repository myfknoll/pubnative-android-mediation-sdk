package net.pubnative.mediation.renderer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import net.pubnative.mediation.request.model.PubnativeAdModel;

public interface PubnativeRenderer<T extends PubnativeAdModel> {

    /**
     * Method create ad view and return it
     * @param context application context
     * @param parent view container
     * @return View created view for displaying Ads
     */
    View createView(Context context, ViewGroup parent);

    /**
     * Method add data to ad view
     * @param view prepared view with fields for displaying Ads
     * @param adModel model of Ads
     */
    void renderView(View view, T adModel);
}
