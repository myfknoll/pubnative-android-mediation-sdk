package net.pubnative.mediation.adapter.widget;

import android.content.Context;
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
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.squareup.picasso.Picasso;

import net.pubnative.mediation.adapter.model.AdMobNativeAppInstallAdModel;
import net.pubnative.mediation.demo.R;
import net.pubnative.mediation.request.model.PubnativeAdModel;

public class PubnativeNativeAdView extends RelativeLayout{

    private static final String TAG = PubnativeNativeAdView.class.getSimpleName();

    // Behaviour
    private NativeAppInstallAdView    mAdMobContainer;
    // Ad info
    private ViewGroup      mAdDisclosure;
    private TextView       mDescription;
    private TextView       mTitle;
    private RatingBar      mRating;
    private ImageView      mIcon;
    private ImageView      mBanner;
    private Button         mCallToAction;
    private TextView       mPrice;
    private TextView       mStore;

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

    /**
     * Initialize layout
     * @param context context of activity
     */
    private void initLayout(Context context) {

        Log.v(TAG, "initLayout");

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.pubnative_ad_view, this);

        mAdMobContainer = (NativeAppInstallAdView) findViewById(R.id.ad_admob_container);
        mAdDisclosure = (ViewGroup) findViewById(R.id.ad_disclosure);
        mTitle = (TextView) findViewById(R.id.ad_title);
        mDescription = (TextView) findViewById(R.id.ad_description);
        mRating = (RatingBar) findViewById(R.id.ad_rating);
        mIcon = (ImageView) findViewById(R.id.ad_icon);
        mBanner = (ImageView) findViewById(R.id.ad_banner);
        mPrice = (TextView) findViewById(R.id.ad_price);
        mStore = (TextView) findViewById(R.id.ad_store);
        mCallToAction = (Button) findViewById(R.id.ad_call_to_action);

    }

    /**
     * Populate data in view
     * @param model Native Ad model
     */
    public void updateAdView(PubnativeAdModel model) {

        Log.v(TAG, "updateAdView");
        if (model instanceof AdMobNativeAppInstallAdModel) {
            prepareAdMobView(mAdMobContainer);
        } else {
            // Ad content
            mTitle.setText(model.getTitle());
            mDescription.setText(model.getDescription());
            mRating.setRating(model.getStarRating());
            mRating.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(model.getIconUrl()).into(mIcon);
            Picasso.with(getContext()).load(model.getBannerUrl()).into(mBanner);
            View sponsorView = model.getAdvertisingDisclosureView(getContext());
            if (sponsorView != null) {
                mAdDisclosure.addView(sponsorView);
            }
        }
    }

    /**
     * Method prepare view for AdMob depends from model type.
     *
     * @param adView container for AdMob Ads
     */
    private void prepareAdMobView(NativeAppInstallAdView adView) {

        Log.v(TAG, "prepareAdMobView");
        adView.setHeadlineView(mTitle);
        adView.setImageView(mBanner);
        adView.setBodyView(mDescription);
        adView.setIconView(mIcon);
        adView.setStarRatingView(mRating);
        adView.setPriceView(mPrice);
        adView.setStoreView(mStore);
        adView.setCallToActionView(mCallToAction);

    }

    /**
     * Removed all old views and recreate view from scratch.
     * @param context activity context
     */
    public void cleanAdView(Context context) {

        Log.v(TAG, "cleanAdView");
        this.removeAllViews();
        initLayout(context);
    }

    //==============================================================================================
    // AdMob Helpers
    //==============================================================================================

    public View getHeadlineView() {

        Log.v(TAG, "getHeadlineView");
        return mAdMobContainer.getHeadlineView();
    }

    public View getBodyView() {

        Log.v(TAG, "getBodyView");
        return mAdMobContainer.getBodyView();
    }

    public View getCallToActionView(){

        Log.v(TAG, "getCallToActionView");
        return mAdMobContainer.getCallToActionView();
    }

    public View getIconView() {

        Log.v(TAG, "getIconView");
        return mAdMobContainer.getIconView();
    }

    public View getImageView() {

        Log.v(TAG, "getImageView");
        return mAdMobContainer.getImageView();
    }

    public View getPriceView() {

        Log.v(TAG, "getPriceView");
        return mAdMobContainer.getPriceView();
    }

    public View getStoreView() {

        Log.v(TAG, "getStoreView");
        return mAdMobContainer.getStoreView();
    }

    public View getStarRatingView() {

        Log.v(TAG, "getStarRatingView");
        return mAdMobContainer.getStarRatingView();
    }

    public void setNativeAd(NativeAd nativeAd) {

        Log.v(TAG, "setNativeAd");
        mAdMobContainer.setNativeAd(nativeAd);
    }
}
