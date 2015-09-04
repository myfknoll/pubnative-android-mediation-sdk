package net.pubnative.library.predefined.interstitial;

import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import net.pubnative.library.Pubnative;
import net.pubnative.library.R;
import net.pubnative.library.model.NativeAdModel;
import net.pubnative.library.predefined.PubnativeActivity;
import net.pubnative.library.predefined.PubnativeView;
import net.pubnative.library.renderer.AdRenderer;
import net.pubnative.library.renderer.AdRendererListener;
import net.pubnative.library.renderer.NativeAdRenderer;

import java.util.ArrayList;

public class PubnativeInterstitialView extends PubnativeView implements
        AdRendererListener,
        OnClickListener
{
    private View     ctaView;
    private TextView descriptionView;

    public PubnativeInterstitialView(PubnativeActivity activity, ArrayList<NativeAdModel> ads)
    {
        super(activity, ads);
        this.getActivity().getLayoutInflater().inflate(R.layout.pubnative_interstitial, this, true);
        this.descriptionView = (TextView) this.findViewById(R.id.pn_interstitial_description);
        this.descriptionView.setVisibility(View.GONE);
        this.ctaView = this.findViewById(R.id.pn_interstitial_cta);
        this.ctaView.setOnClickListener(this);
        this.renderAd();
    }

    private void renderAd()
    {
        NativeAdRenderer renderer = new NativeAdRenderer(this.getContext());
        renderer.titleView = (TextView) this.findViewById(R.id.pn_interstitial_title);
        renderer.descriptionView = (TextView) this.findViewById(R.id.pn_interstitial_description);
        renderer.iconView = (ImageView) this.findViewById(R.id.pn_interstitial_icon);
        renderer.bannerView = (ImageView) this.findViewById(R.id.pn_interstitial_banner);
        renderer.ratingView = (RatingBar) this.findViewById(R.id.pn_interstitial_rating);
        renderer.downloadView = (TextView) this.findViewById(R.id.pn_interstitial_cta);
        renderer.render(this.ads.get(0), this);
    }

    // ADRendererListener<NativeAdModel>
    @Override
    public void onAdRenderStarted(AdRenderer renderer)
    {
        // Do nothing
        Log.v("PubnativeInterstitialActivity", "onAdRenderStarted");
    }

    @Override
    public void onAdRenderFailed(AdRenderer renderer, Exception e)
    {
        Log.v("PubnativeInterstitialActivity", "onAdRenderFailed: " + e);
        this.invokeMessage(PubnativeActivity.EVENT_LISTENER_FAILED, e);
    }

    @Override
    public void onAdRenderFinished(AdRenderer renderer)
    {
        Log.v("PubnativeInterstitialActivity", "onAdRenderFinished");
        this.ads.get(0).confirmImpressionAutomatically(this.getContext(), this, new NativeAdModel.Listener() {
            @Override
            public void onAdImpression(NativeAdModel model)
            {
                Log.v("PubnativeInterstitialActivity", "onAdImpression");
            }
        });
    }

    // OnClickListener
    @Override
    public void onClick(View v)
    {
        if (this.ctaView == v)
        {
            Pubnative.showInPlayStoreViaDialog(this.getActivity(), this.ads.get(0));
        }
    }

    @Override
    protected void onOrientationChanged(Configuration configuration)
    {
        this.descriptionView.setVisibility(View.GONE);
        if (Configuration.ORIENTATION_PORTRAIT == configuration.orientation && this.descriptionView.getText().length() > 0)
        {
            this.descriptionView.setVisibility(View.VISIBLE);
        }
    }
}
