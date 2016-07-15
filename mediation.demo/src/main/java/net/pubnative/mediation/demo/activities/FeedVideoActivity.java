package net.pubnative.mediation.demo.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import net.pubnative.mediation.demo.R;
import net.pubnative.mediation.demo.Settings;
import net.pubnative.mediation.request.PubnativeNetworkFeedVideo;

public class FeedVideoActivity extends StandardAdUnitActivity implements PubnativeNetworkFeedVideo.Listener {

    private static final String TAG = FeedVideoActivity.class.getSimpleName();
    private RelativeLayout mFeedBannerView;
    private PubnativeNetworkFeedVideo mFeedVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLayoutInflater().inflate(R.layout.layout_infeed_banner, mBlankContainer);
        mFeedBannerView = (RelativeLayout) findViewById(R.id.infeed_banner_container);
    }

    public void onRequestClick(View v) {

        Log.v(TAG, "onRequestClick");
        mLoaderContainer.setVisibility(View.VISIBLE);
        mFeedVideo = new PubnativeNetworkFeedVideo();
        mFeedVideo.setListener(this);
        mFeedVideo.load(this, Settings.getAppToken(this), mPlacementSpinner.getSelectedItem().toString());
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeNetworkFeedVideo.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeNetworkFeedVideoLoadFinish(PubnativeNetworkFeedVideo feedVideo) {
        Log.v(TAG, "onPubnativeNetworkFeedVideoLoadFinish");
        mLoaderContainer.setVisibility(View.GONE);
        feedVideo.show(mFeedBannerView);
        showToast("Feed Video loaded");
    }

    @Override
    public void onPubnativeNetworkFeedVideoLoadFail(PubnativeNetworkFeedVideo feedVideo, Exception exception) {
        Log.v(TAG, "onPubnativeNetworkFeedVideoLoadFail", exception);
        mLoaderContainer.setVisibility(View.GONE);
        showToast(exception.getMessage());
        showToast("Feed Video loading failed");
    }

    @Override
    public void onPubnativeNetworkFeedVideoShow(PubnativeNetworkFeedVideo feedVideo) {
        Log.v(TAG, "onPubnativeNetworkFeedVideoShow");
        mLoaderContainer.setVisibility(View.GONE);
        showToast("Feed Video show");
    }

    @Override
    public void onPubnativeNetworkFeedVideoFinish(PubnativeNetworkFeedVideo feedVideo) {

    }

    @Override
    public void onPubnativeNetworkFeedVideoStart(PubnativeNetworkFeedVideo feedVideo) {

    }

    @Override
    public void onPubnativeNetworkFeedVideoClick(PubnativeNetworkFeedVideo feedVideo) {
        Log.v(TAG, "onPubnativeNetworkFeedVideoClick");
        showToast("Feed Video clicked");
        mFeedVideo.hide();
    }

    @Override
    public void onPubnativeNetworkFeedVideoHide(PubnativeNetworkFeedVideo feedVideo) {
        Log.v(TAG, "onPubnativeNetworkFeedVideoHide");
        showToast("Feed Banner hide");
    }
}
