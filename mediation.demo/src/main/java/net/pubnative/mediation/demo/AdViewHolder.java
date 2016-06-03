// The MIT License (MIT)
//
// Copyright (c) 2015 PubNative GmbH
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//

package net.pubnative.mediation.demo;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import net.pubnative.mediation.adapter.widget.PubnativeNativeAdView;
import net.pubnative.mediation.request.PubnativeNetworkRequest;
import net.pubnative.mediation.request.model.PubnativeAdModel;

/**
 * A class that holds the reference to all views in a cell.
 * This helps us to avoid redundant calls to "findViewById" each
 * time we initialize values into the cell.
 */
public class AdViewHolder implements PubnativeNetworkRequest.Listener,
                                     View.OnClickListener {

    private static final String TAG = AdViewHolder.class.getSimpleName();
    protected     Context               mContext;
    // Data
    protected     CellRequestModel      mCellRequestModel;
    // Behaviour
    protected     ProgressBar           mAdLoading;
    protected     PubnativeNativeAdView mAdContainer;
    protected     Button                mRequestButton;
    // Ad info
    protected     TextView              mPlacementID;
    protected     TextView              mAdapterName;

    protected     ViewGroup             mAdDisclosure;
    protected     TextView              mDescription;
    protected     TextView              mTitle;
    protected     RatingBar             mRating;
    protected     ImageView             mIcon;
    protected     ImageView             mBanner;
    protected     Button                mCallToAction;
    protected     TextView              mPrice;
    protected     TextView              mStore;

    public AdViewHolder(Context context, View convertView) {

        mContext = context;
        mAdLoading = (ProgressBar) convertView.findViewById(R.id.ad_spinner);
        mAdContainer = (PubnativeNativeAdView) convertView.findViewById(R.id.ad_view);
        mRequestButton = (Button) convertView.findViewById(R.id.request_button);
        mRequestButton.setOnClickListener(this);
        mAdapterName = (TextView) convertView.findViewById(R.id.ad_adapter_name_text);
        mPlacementID = (TextView) convertView.findViewById(R.id.placement_id_text);

        mAdDisclosure = (ViewGroup) convertView.findViewById(R.id.ad_disclosure);
        mTitle = (TextView) convertView.findViewById(R.id.ad_title);
        mDescription = (TextView) convertView.findViewById(R.id.ad_description);
        mRating = (RatingBar) convertView.findViewById(R.id.ad_rating);
        mIcon = (ImageView) convertView.findViewById(R.id.ad_icon);
        mBanner = (ImageView) convertView.findViewById(R.id.ad_banner);
        mPrice = (TextView) convertView.findViewById(R.id.ad_price);
        mStore = (TextView) convertView.findViewById(R.id.ad_store);
        mCallToAction = (Button) convertView.findViewById(R.id.ad_call_to_action);

    }

    public void setCellRequestModel(CellRequestModel cellRequestModel) {

        Log.v(TAG, "setCellRequestModel");
        if (mCellRequestModel != null && mCellRequestModel.adModel != null) {
            mCellRequestModel.adModel.stopTracking();
        }
        mCellRequestModel = cellRequestModel;
        cleanView();
        renderAd();
    }

    public void cleanView() {

        Log.v(TAG, "cleanView");
        mAdapterName.setText("");
        mAdLoading.setVisibility(View.GONE);

        mAdDisclosure.removeAllViews();
        mTitle.setText("");
        mDescription.setText("");
        mRating.setRating(0f);
        mRating.setVisibility(View.INVISIBLE);
        mIcon.setImageDrawable(null);
        mBanner.setImageDrawable(null);
        mPrice.setText("");
        mPrice.setVisibility(View.GONE);
        mStore.setText("");
        mStore.setVisibility(View.GONE);
        mCallToAction.setText("");
        mCallToAction.setVisibility(View.GONE);
    }

    public void renderAd() {

        Log.v(TAG, "renderAd");
        // Placement data
        mPlacementID.setText("Placement ID: " + mCellRequestModel.placementID);
        PubnativeAdModel model = mCellRequestModel.adModel;
        if (model != null) {
            // Privacy container
            String adapterNameText = model.getClass().getSimpleName();
            mAdapterName.setText(adapterNameText);
            //Initialize fields of view
            mAdContainer.setHeadlineView(mTitle);
            mAdContainer.setBodyView(mDescription);
            mAdContainer.setIconView(mIcon);
            mAdContainer.setImageView(mBanner);
            mAdContainer.setStarRatingView(mRating);
            mAdContainer.setPriceView(mPrice);
            mAdContainer.setStoreView(mStore);
            mAdContainer.setCallToActionView(mCallToAction);
            mAdContainer.setAdDisclosure(mAdDisclosure);
            // Populate data in view
            mAdContainer.setModel(model);
            // Tracking
            model.startTracking(mContext, mAdContainer);
        }
    }

    public void onRequestClick(View v) {

        Log.v(TAG, "onRequestClick");
        cleanView();
        mAdLoading.setVisibility(View.VISIBLE);
        mCellRequestModel.request.start(mContext, Settings.getAppToken(mContext), mCellRequestModel.placementID, this);
    }

    //==============================================================================================
    // CALLBACKS
    //==============================================================================================
    // PubnativeNetworkRequest.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeNetworkRequestLoaded(PubnativeNetworkRequest request, PubnativeAdModel ad) {

        Log.d(TAG, "onPubnativeNetworkRequestLoaded");
        mAdLoading.setVisibility(View.GONE);
        if (mCellRequestModel.adModel != null) {
            mCellRequestModel.adModel.stopTracking();
        }
        mCellRequestModel.adModel = ad;
        renderAd();
    }

    @Override
    public void onPubnativeNetworkRequestFailed(PubnativeNetworkRequest request, Exception exception) {

        Log.d(TAG, "onPubnativeNetworkRequestFailed: " + exception);
        Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_LONG).show();
        mAdLoading.setVisibility(View.GONE);
        mCellRequestModel.adModel = null;
        cleanView();
    }

    @Override
    public void onClick(View v) {

        onRequestClick(v);
    }
}
