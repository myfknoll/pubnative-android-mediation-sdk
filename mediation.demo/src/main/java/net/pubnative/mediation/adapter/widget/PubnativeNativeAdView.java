package net.pubnative.mediation.adapter.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.squareup.picasso.Picasso;
import net.pubnative.mediation.adapter.model.AdMobNativeAppInstallAdModel;
import net.pubnative.mediation.demo.R;
import net.pubnative.mediation.request.model.PubnativeAdModel;

public class PubnativeNativeAdView extends RelativeLayout{

    // Behaviour
    protected ProgressBar    mAdLoading;
    protected RelativeLayout mAdContainer;
    protected NativeAppInstallAdView    mAdMobContainer;
    // Ad info
    protected ViewGroup      mAdDisclosure;
    protected TextView       mDescription;
    protected TextView       mTitle;
    protected RatingBar      mRating;
    protected ImageView      mIcon;
    protected ImageView      mBanner;
    protected Button         mCallToAction;
    protected TextView       mPrice;
    protected TextView       mStore;

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

    private void initLayout(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.pubnative_ad_view, this);

        mAdLoading = (ProgressBar) findViewById(R.id.ad_spinner);
        mAdContainer = (RelativeLayout) findViewById(R.id.ad_clickable);
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
            prepareAdMobView(mAdMobContainer, model);
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
     * @param model data model
     */
    private void prepareAdMobView(NativeAppInstallAdView adView, PubnativeAdModel model) {

        adView.setHeadlineView(mTitle);
        adView.setImageView(mBanner);
        adView.setBodyView(mDescription);
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(mIcon);
        adView.setStarRatingView(mRating);
        adView.setPriceView(mPrice);
        adView.setStoreView(mStore);
        adView.setCallToActionView(mCallToAction);

    }

    public void cleanAdView() {

        mAdLoading.setVisibility(GONE);
        mAdContainer.removeAllViews();
        mAdDisclosure.removeAllViews();
        mTitle.setText("");
        mDescription.setText("");
        mRating.setRating(0f);
        mRating.setVisibility(GONE);
        mIcon.setImageDrawable(null);
        mBanner.setImageDrawable(null);
    }
}
