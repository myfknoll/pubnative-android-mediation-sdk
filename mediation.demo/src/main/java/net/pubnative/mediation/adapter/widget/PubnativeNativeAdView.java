package net.pubnative.mediation.adapter.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.squareup.picasso.Picasso;

import net.pubnative.mediation.adapter.model.AdMobNativeAppInstallAdModel;
import net.pubnative.mediation.demo.R;
import net.pubnative.mediation.request.model.PubnativeAdModel;

import java.util.List;

public class PubnativeNativeAdView extends RelativeLayout {

    private static final String TAG = PubnativeNativeAdView.class.getSimpleName();
    protected Context                mContext;
    protected PubnativeAdModel       mAdModel;
    // Behaviour
    protected NativeAppInstallAdView mAdMobContainer;
    // Ad info
    protected ViewGroup              mAdDisclosure;
    protected TextView               mDescription;
    protected TextView               mTitle;
    protected RatingBar              mRating;
    protected ImageView              mIcon;
    protected ImageView              mBanner;
    protected Button                 mCallToAction;
    protected TextView               mPrice;
    protected TextView               mStore;

    public PubnativeNativeAdView(Context context) {

        super(context);
        initLayout(context);
    }

    public PubnativeNativeAdView(Context context, AttributeSet attrs) {

        super(context, attrs);
        initLayout(context);
    }

    public PubnativeNativeAdView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        Log.v(TAG, "onInterceptTouchEvent");
        onTouchEvent(ev);
        return false;
    }

    //==============================================================================================
    // PubnativeNativeAdView methods
    //==============================================================================================
    // Initializing
    //----------------------------------------------------------------------------------------------

    protected void initLayout(Context context) {

        Log.v(TAG, "initLayout");
        mContext = context;
    }

    // Population data
    //----------------------------------------------------------------------------------------------

    /**
     * Populate data in view
     *
     * @param model Native Ad model
     */
    public void setModel(PubnativeAdModel model) {

        Log.v(TAG, "setModel");
        mAdModel = model;

        populateAdView();

        if (model instanceof AdMobNativeAppInstallAdModel) {
            mAdMobContainer = new NativeAppInstallAdView(mContext);
            addView(mAdMobContainer);
            populateAppInstallAdView();

        } else {
            removeView(mAdMobContainer);
        }
    }

    protected void populateAppInstallAdView() {

        Log.v(TAG, "populateAppInstallAdView");

        mAdMobContainer.setHeadlineView(mTitle);
        mAdMobContainer.setImageView(mBanner);
        mAdMobContainer.setBodyView(mDescription);
        mAdMobContainer.setCallToActionView(mCallToAction);
        mAdMobContainer.setIconView(mIcon);
        mAdMobContainer.setPriceView(mPrice);
        mAdMobContainer.setStarRatingView(mRating);
        mAdMobContainer.setStoreView(mStore);
        // Assign native ad object to the native view.
        mAdMobContainer.setNativeAd((NativeAd) mAdModel.getNativeAd());
    }

    protected void populateAdView() {

        Log.v(TAG, "populateAdView");
        mTitle.setText(mAdModel.getTitle());
        mDescription.setText(mAdModel.getDescription());
        mCallToAction.setText(mAdModel.getCallToAction());
        mRating.setRating(mAdModel.getStarRating());
        Picasso.with(getContext()).load(mAdModel.getIconUrl()).into(mIcon);
        Picasso.with(getContext()).load(mAdModel.getBannerUrl()).into(mBanner);

        View sponsorView = mAdModel.getAdvertisingDisclosureView(mContext);
        if (sponsorView != null) {
            mAdDisclosure.addView(sponsorView);
        }

    }

    // Fields
    //----------------------------------------------------------------------------------------------

    public void setBodyView(TextView view) {

        mDescription = view;
    }

    public void setHeadlineView(TextView view) {

        mTitle = view;
    }

    public void setStarRatingView(RatingBar view) {

        mRating = view;
    }

    public void setIconView(ImageView view) {

        mIcon = view;
    }

    public void setImageView(ImageView view) {

        mBanner = view;
    }

    public void setCallToActionView(Button view) {

        mCallToAction = view;
    }

    public void setPriceView(TextView view) {

        mPrice = view;
    }

    public void setStoreView(TextView view) {

        mStore = view;
    }

    public void setAdDisclosure(ViewGroup view) {

        mAdDisclosure = view;
    }
}
