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
        if (model != null) {
            populateAdView();
            if (model instanceof AdMobNativeAppInstallAdModel) {
                mAdMobContainer = new NativeAppInstallAdView(mContext);
                addView(mAdMobContainer);
                populateAppInstallAdView();
            } else {
                removeView(mAdMobContainer);
            }
        }
    }

    protected void populateAppInstallAdView() {

        Log.v(TAG, "populateAppInstallAdView");
        if (mTitle != null) {
            mAdMobContainer.setHeadlineView(mTitle);
        }
        if (mBanner != null) {
            mAdMobContainer.setImageView(mBanner);
        }
        if (mDescription != null) {
            mAdMobContainer.setBodyView(mDescription);
        }
        if (mCallToAction != null) {
            mAdMobContainer.setCallToActionView(mCallToAction);
        }
        if (mIcon != null) {
            mAdMobContainer.setIconView(mIcon);
        }
        if (mPrice != null) {
            mAdMobContainer.setPriceView(mPrice);
        }
        if (mRating != null) {
            mAdMobContainer.setStarRatingView(mRating);
        }
        if (mStore != null) {
            mAdMobContainer.setStoreView(mStore);
        }
        // Assign native ad object to the native view.
        if (mAdModel != null) {
            mAdMobContainer.setNativeAd((NativeAd) mAdModel.getNativeAd());
        }
    }

    protected void populateAdView() {

        Log.v(TAG, "populateAdView");
        if (mTitle != null) {
            mTitle.setText(mAdModel.getTitle());
        }
        if (mDescription != null) {
            mDescription.setText(mAdModel.getDescription());
        }
        if (mCallToAction != null) {
            mCallToAction.setText(mAdModel.getCallToAction());
        }
        if (mRating != null) {
            mRating.setRating(mAdModel.getStarRating());
        }
        if (mIcon != null) {
            Picasso.with(getContext()).load(mAdModel.getIconUrl()).into(mIcon);
        }
        if (mBanner != null) {
            Picasso.with(getContext()).load(mAdModel.getBannerUrl()).into(mBanner);
        }
        View sponsorView = mAdModel.getAdvertisingDisclosureView(mContext);
        if (sponsorView != null && mAdDisclosure != null) {
            mAdDisclosure.addView(sponsorView);
        }
    }
    // Fields
    //----------------------------------------------------------------------------------------------

    /**
     * Set view as a BodyView
     * @param view TextView where will Ad description will be shown
     */
    public void setBodyView(TextView view) {

        mDescription = view;
    }

    /**
     * Set view as a HeadlineView
     * @param view TextView where will Ad headline will be shown
     */
    public void setHeadlineView(TextView view) {

        mTitle = view;
    }

    /**
     * Set view as a StarRatingView
     * @param view RatingBar where will Ad rating will be shown
     */
    public void setStarRatingView(RatingBar view) {

        mRating = view;
    }

    /**
     * Set view as a IconView
     * @param view ImageView where will Ad icon will be shown
     */
    public void setIconView(ImageView view) {

        mIcon = view;
    }

    /**
     * Set view as a ImageView
     * @param view ImageView where will Ad banner will be shown
     */
    public void setImageView(ImageView view) {

        mBanner = view;
    }

    /**
     * Set view as a CallToActionView
     * @param view Button where will Ad Call-To-Action text will be shown
     */
    public void setCallToActionView(Button view) {

        mCallToAction = view;
    }

    /**
     * Set view as a PriceView
     * @param view TextView where will Ad price info will be shown
     */
    public void setPriceView(TextView view) {

        mPrice = view;
    }

    /**
     * Set view as a StoreView
     * @param view TextView where will Ad store info will be shown
     */
    public void setStoreView(TextView view) {

        mStore = view;
    }

    /**
     * Set view as a AdDisclosure
     * @param view ViewGroup where will Ad disclosure will be shown
     */
    public void setAdDisclosure(ViewGroup view) {

        mAdDisclosure = view;
    }
}
