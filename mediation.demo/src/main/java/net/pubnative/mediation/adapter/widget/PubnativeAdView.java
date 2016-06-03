package net.pubnative.mediation.adapter.widget;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;

import net.pubnative.mediation.adapter.model.AdMobNativeAppInstallAdModel;
import net.pubnative.mediation.request.model.PubnativeAdModel;

public class PubnativeAdView extends RelativeLayout {

    private static final String TAG = PubnativeAdView.class.getSimpleName();
    // Behaviour
    protected NativeAppInstallAdView mAdMobContainer;
    // Ad info
    protected View                   mDescription;
    protected View                   mTitle;
    protected View                   mRating;
    protected View                   mIcon;
    protected View                   mBanner;
    protected View                   mCallToAction;

    public PubnativeAdView(Context context) {

        super(context);
    }

    //==============================================================================================
    // ViewGroup
    //==============================================================================================
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        Log.v(TAG, "onInterceptTouchEvent");
        onTouchEvent(ev);
        return false;
    }
    //==============================================================================================
    // PubnativeNativeAdView
    //==============================================================================================
    // Public
    //----------------------------------------------------------------------------------------------

    /**
     * Sets the model into the view
     *
     * @param model Native Ad model
     */
    public void setModel(Context context, PubnativeAdModel model) {

        if (model instanceof AdMobNativeAppInstallAdModel) {
            mAdMobContainer = new NativeAppInstallAdView(context);
            mAdMobContainer.setHeadlineView(mTitle);
            mAdMobContainer.setImageView(mBanner);
            mAdMobContainer.setBodyView(mDescription);
            mAdMobContainer.setCallToActionView(mCallToAction);
            mAdMobContainer.setIconView(mIcon);
            mAdMobContainer.setStarRatingView(mRating);
            // Assign native ad object to the native view.
            mAdMobContainer.setNativeAd((NativeAd) model.getNativeAd());
            addView(mAdMobContainer);
        } else {
            removeView(mAdMobContainer);
        }
    }

    /**
     * Sets a description view to track
     *
     * @param view view to track
     *
     * @return this item
     */
    public PubnativeAdView withDescription(View view) {

        mDescription = view;
        return this;
    }

    /**
     * Sets a title view to track
     *
     * @param view view to track
     *
     * @return this item
     */
    public PubnativeAdView withTitle(View view) {

        mTitle = view;
        return this;
    }

    /**
     * Sets a rating view to track
     *
     * @param view view to track
     *
     * @return this item
     */
    public PubnativeAdView withRating(View view) {

        mRating = view;
        return this;
    }

    /**
     * Sets an icon view to track
     *
     * @param view view to track
     *
     * @return this item
     */
    public PubnativeAdView withIcon(View view) {

        mIcon = view;
        return this;
    }

    /**
     * Sets a banner view to track
     *
     * @param view view to track
     *
     * @return this item
     */
    public PubnativeAdView withBanner(View view) {

        mBanner = view;
        return this;
    }

    /**
     * Sets a call to action view to track
     *
     * @param view view to track
     *
     * @return this item
     */
    public PubnativeAdView withCallToAction(Button view) {

        mCallToAction = view;
        return this;
    }
}
