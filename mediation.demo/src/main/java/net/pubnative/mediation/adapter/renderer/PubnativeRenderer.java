package net.pubnative.mediation.adapter.renderer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import net.pubnative.mediation.request.model.PubnativeAdModel;

public interface PubnativeRenderer<T extends PubnativeAdModel> {
    View createView(Context context, ViewGroup parent);
    void renderView(View view, T adModel);
}
