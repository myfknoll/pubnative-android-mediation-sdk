package net.pubnative.mediation.adapter.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

        onTouchEvent(ev);
        return false;
    }

    private void initLayout(Context context) {

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

    public void updateAdView(PubnativeAdModel model) {
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

        adView.setHeadlineView(mTitle);
        adView.setImageView(mBanner);
        adView.setBodyView(mDescription);
        adView.setIconView(mIcon);
        adView.setStarRatingView(mRating);
        adView.setPriceView(mPrice);
        adView.setStoreView(mStore);
        adView.setCallToActionView(mCallToAction);

    }

    public void cleanAdView(Context context) {

        this.removeAllViews();
        initLayout(context);
    }

    public View getHeadlineView() {
        return mAdMobContainer.getHeadlineView();
    }

    public View getBodyView() {
        return mAdMobContainer.getBodyView();
    }

    public View getCallToActionView(){
        return mAdMobContainer.getCallToActionView();
    }

    public View getIconView() {
        return mAdMobContainer.getIconView();
    }

    public View getImageView() {
        return mAdMobContainer.getImageView();
    }

    public View getPriceView() {
        return mAdMobContainer.getPriceView();
    }

    public View getStoreView() {
        return mAdMobContainer.getStoreView();
    }

    public View getStarRatingView() {
        return mAdMobContainer.getStarRatingView();
    }

    public void setNativeAd(NativeAd nativeAd) {
        mAdMobContainer.setNativeAd(nativeAd);
    }
}
