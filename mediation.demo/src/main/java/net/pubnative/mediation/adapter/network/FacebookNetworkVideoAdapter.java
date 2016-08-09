package net.pubnative.mediation.adapter.network;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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

/**
 * Created by alvarlega on 08/08/16.
 */
public class FacebookNetworkVideoAdapter extends PubnativeLibraryNetworkVideoAdapter
        implements AdListener,
                   ImpressionListener {

    private static final String TAG = FacebookNetworkVideoAdapter.class.getSimpleName();

    private NativeAd mNative;
    private WindowManager mWindowManager;
    private Context mContext;
    private View mContainer;
    private LinearLayout mParentView;
    private FacebookRenderer mRenderer;

    /**
     * Creates a new instance of PubnativeLibraryNetworkVideoAdapter.
     *
     * @param data server configured data for the current adapter network.
     */
    public FacebookNetworkVideoAdapter(Map data) {

        super(data);
    }

    @Override
    public void load(Context context) {

        Log.v(TAG, "load");
        if (context != null && mData != null) {
            mContext = context;
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            mRenderer = new FacebookRenderer();
            mContainer = mRenderer.createView(context, prepareFullscreenView());
            mParentView.setGravity(Gravity.CENTER);
            //AdSettings.addTestDevice("e356e5959d9062f96086af6250f3ead8");
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

        return true;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void destroy() {
        mNative.unregisterView();
        mWindowManager.removeView(mParentView);
        super.destroy();
    }

    private RelativeLayout prepareFullscreenView() {

        mParentView = new LinearLayout(mContext) {
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

        mParentView.setContentDescription("Parent View");

        mWindowManager.addView(mParentView, windowParams);

        RelativeLayout container = new RelativeLayout(mContext) {

            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

                int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
                int originalHeight = MeasureSpec.getSize(heightMeasureSpec);
                int calculatedHeight = originalWidth * 9 / 16;
                int finalWidth, finalHeight;

                if (calculatedHeight > originalHeight)
                {
                    finalWidth = originalHeight * 16 / 9;
                    finalHeight = originalHeight;
                }
                else
                {
                    finalWidth = originalWidth;
                    finalHeight = calculatedHeight;
                }

                super.onMeasure(
                        MeasureSpec.makeMeasureSpec(finalWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
            }
        };
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        container.setLayoutParams(params);
        container.setContentDescription("Container View");
        mParentView.addView(container);

        return container;
    }

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
            mNative.registerViewForInteraction(mParentView);
            //mParentView.setGravity(Gravity.CENTER);
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
