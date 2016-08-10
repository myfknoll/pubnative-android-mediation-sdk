package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.ImpressionListener;
import com.facebook.ads.NativeAd;

import net.pubnative.mediation.adapter.model.FacebookNativeAdModel;
import net.pubnative.mediation.adapter.renderer.FacebookRenderer;
import net.pubnative.mediation.exceptions.PubnativeException;

import java.util.HashMap;
import java.util.Map;

public class FacebookNetworkVideoAdapter extends PubnativeLibraryNetworkVideoAdapter
        implements AdListener,
                   ImpressionListener {

    private static final String TAG = FacebookNetworkVideoAdapter.class.getSimpleName();
    //==============================================================================================
    // Properties
    //==============================================================================================
    private NativeAd mNative;
    private WindowManager mWindowManager;
    private Context mContext;
    private View mContainer;
    private RelativeLayout mParentView;
    private FacebookRenderer mRenderer;

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
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            mRenderer = new FacebookRenderer();
            mContainer = mRenderer.createView(context, prepareFullscreenView());
            String placementId = (String) mData.get(FacebookNetworkRequestAdapter.KEY_PLACEMENT_ID);
            if (!TextUtils.isEmpty(placementId)) {
                mNative = new NativeAd(context, placementId);
                mNative.setAdListener(this);
                mNative.setImpressionListener(this);
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
        super.show();
    }

    @Override
    public void destroy() {

        Log.v(TAG, "destroy");
        mNative.unregisterView();
        mWindowManager.removeView(mParentView);
        super.destroy();
    }

    //==============================================================================================
    // Helpers
    //==============================================================================================

    private RelativeLayout prepareFullscreenView() {

        Log.v(TAG, "prepareFullscreenView");
        mParentView = new RelativeLayout(mContext) {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    mWindowManager.removeView(this);
                    return true;
                }
                return super.dispatchKeyEvent(event);
            }
        };

        mParentView.setBackgroundColor(Color.BLACK);

        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
        windowParams.gravity = Gravity.CENTER;
        windowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowParams.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        windowParams.format = PixelFormat.RGBA_8888;

        mWindowManager.addView(mParentView, windowParams);
        mParentView.setGravity(Gravity.CENTER);

        RelativeLayout container = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        container.setLayoutParams(params);
        mParentView.addView(container);

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
                destroy();
            }
        });

        ViewGroup parent = (ViewGroup) mContainer.getParent();
        int index = parent.indexOfChild(mContainer);
        parent.addView(closeBtn, index - 1, params);
    }

    //==============================================================================================
    // Callabacks
    //==============================================================================================
    // AdListener
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
            FacebookNativeAdModel adModel = new FacebookNativeAdModel(mNative);
            mRenderer.renderView(mContainer, adModel);
            prepareCloseButton();
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

    @Override
    public void onLoggingImpression(Ad ad) {

        Log.v(TAG, "onLoggingImpression");
        invokeImpressionConfirmed();
    }
}
