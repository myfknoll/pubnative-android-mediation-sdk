package net.pubnative.mediation.adapter.network;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;

import net.pubnative.mediation.adapter.model.FacebookNativeAdModel;
import net.pubnative.mediation.adapter.renderer.FacebookRenderer;
import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import java.util.HashMap;
import java.util.Map;

public class FacebookNetworkVideoAdapter extends PubnativeLibraryNetworkVideoAdapter
        implements AdListener,
                   PubnativeAdModel.Listener {

    private static final String TAG = FacebookNetworkVideoAdapter.class.getSimpleName();
    //==============================================================================================
    // Properties
    //==============================================================================================
    private NativeAd mNative;
    private Context mContext;
    private View mContainer;
    private FacebookRenderer mRenderer;
    private Dialog mDialog;

    /**
     * Creates a new instance of PubnativeLibraryNetworkVideoAdapter.
     *
     * @param data server configured data for the current adapter network.
     */
    public FacebookNetworkVideoAdapter(Map data) {

        super(data);
    }

    //==============================================================================================
    // Public
    //==============================================================================================

    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        if (context != null && mData != null) {
            mContext = context;
            mRenderer = new FacebookRenderer();
            mContainer = mRenderer.createView(context, prepareDialogView());
            String placementId = (String) mData.get(FacebookNetworkRequestAdapter.KEY_PLACEMENT_ID);
            if (!TextUtils.isEmpty(placementId)) {
                mNative = new NativeAd(context, placementId);
                mNative.setAdListener(this);
                mNative.loadAd();
            } else {
                invokeLoadFail(PubnativeException.ADAPTER_ILLEGAL_ARGUMENTS);
            }
        } else {
            invokeLoadFail(PubnativeException.ADAPTER_MISSING_DATA);
        }
    }

    @Override
    public boolean isReady() {

        Log.v(TAG, "isReady");
        return true;
    }

    @Override
    public void show() {

        Log.v(TAG, "show");
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
        super.show();
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        mNative.unregisterView();
        if (mDialog != null) {
            mDialog.dismiss();
        }
        super.destroy();
    }

    //==============================================================================================
    // Helpers
    //==============================================================================================

    private RelativeLayout prepareDialogView(){

        mDialog = new Dialog(mContext, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        RelativeLayout layout = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams lParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(lParams);

        RelativeLayout container = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        container.setLayoutParams(params);
        mDialog.setContentView(layout);

        layout.addView(container);

        return container;

    }

    private void prepareCloseButton() {

        Log.v(TAG, "prepareCloseButton");
        ImageView closeBtn = new ImageView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        closeBtn.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.ic_notification_clear_all));
        closeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });

        ViewGroup parent = (ViewGroup) mContainer.getParent();
        int index = parent.indexOfChild(mContainer);
        parent.addView(closeBtn, index - 1, params);
    }

    //==============================================================================================
    // Callabacks
    //==============================================================================================
    // NativeAd.AdListener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onError(Ad ad, AdError adError) {

        Log.v(TAG, "onError: " + (adError != null ? (adError.getErrorCode() + " - " + adError.getErrorMessage()) : ""));
        if (adError == null) {
            invokeLoadFail(PubnativeException.ADAPTER_UNKNOWN_ERROR);
        } else {
            Map extras = new HashMap();
            extras.put("code", adError.getErrorCode());
            extras.put("message", adError.getErrorMessage());
            invokeLoadFail(PubnativeException.extraException(PubnativeException.ADAPTER_UNKNOWN_ERROR, extras));
        }
    }

    @Override
    public void onAdLoaded(Ad ad) {

        Log.v(TAG, "onAdLoaded");
        if (ad != mNative) {
            return;
        }
        if (mContainer != null) {
            prepareCloseButton();
            FacebookNativeAdModel adModel = new FacebookNativeAdModel(mNative);
            adModel.setListener(this);
            mRenderer.renderView(mContainer, adModel);
            mContainer.setClickable(true);
            mNative.registerViewForInteraction(mRenderer.getMediaView(mContainer));
        }
        invokeLoadFinish();
    }

    @Override
    public void onAdClicked(Ad ad) {
        Log.v(TAG, "onAdClicked");
        invokeClick();
    }

    // PubnativeAdModel.AdListener
    //----------------------------------------------------------------------------------------------

    @Override
    public void onAdImpressionConfirmed(PubnativeAdModel model) {
        Log.v(TAG, "onLoggingImpression");
        invokeImpressionConfirmed();
    }

    @Override
    public void onAdClick(PubnativeAdModel model) {
        Log.v(TAG, "onAdClicked");
        invokeClick();
    }
}
