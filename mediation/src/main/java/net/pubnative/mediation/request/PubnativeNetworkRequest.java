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

package net.pubnative.mediation.request;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.adapter.PubnativeNetworkAdapterFactory;
import net.pubnative.mediation.adapter.PubnativeNetworkRequestAdapter;
import net.pubnative.mediation.config.PubnativePlacement;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.insights.model.PubnativeInsightDataModel;
import net.pubnative.mediation.request.model.PubnativeAdModel;
import net.pubnative.mediation.utils.PubnativeDeviceUtils;

import java.util.HashMap;
import java.util.Map;

public class PubnativeNetworkRequest implements PubnativeNetworkRequestAdapter.Listener {

    private static       String TAG                           = PubnativeNetworkRequest.class.getSimpleName();
    protected Context                          mContext;
    protected PubnativeNetworkRequest.Listener mListener;
    protected PubnativePlacement               mPlacement;
    protected PubnativeAdModel                 mAd;
    protected long                             mRequestStartTimestamp;
    protected boolean                          mIsRunning;
    protected Handler                          mHandler;
    //==============================================================================================
    // Listener
    //==============================================================================================

    /**
     * Interface for request callbacks that will inform about the request status
     */
    public interface Listener {

        /**
         * Invoked when ad request starts with valid params
         *
         * @param request Object used to make the ad request.
         */
        void onPubnativeNetworkRequestStarted(PubnativeNetworkRequest request);

        /**
         * Invoked when ad request returns valid ads.
         *
         * @param request Object used to make the ad request.
         * @param ad      Loaded ad model.
         */
        void onPubnativeNetworkRequestLoaded(PubnativeNetworkRequest request, PubnativeAdModel ad);

        /**
         * Invoked when ad request fails or when no ad is retrieved.
         *
         * @param request   Object used to make the ad request.
         * @param exception Exception with proper message of request failure.
         */
        void onPubnativeNetworkRequestFailed(PubnativeNetworkRequest request, Exception exception);
    }
    //==============================================================================================
    // Pubic methods
    //==============================================================================================

    /**
     * Starts a new mAd request.
     *
     * @param context       valid Context object.
     * @param appToken      valid AppToken provided by Pubnative.
     * @param placementName valid placementId provided by Pubnative.
     * @param listener      valid Listener to keep track of request callbacks.
     */
    public synchronized void start(Context context, String appToken, final String placementName, PubnativeNetworkRequest.Listener listener) {

        Log.v(TAG, "start: -placement: " + placementName + " -appToken:" + appToken);
        if (listener == null) {
            // Just drop the call
            Log.e(TAG, "start - listener not specified, dropping the call");
        } else if (mIsRunning) {
            Log.e(TAG, "start - request already running, dropping the call");
        } else {
            mIsRunning = true;
            mHandler = new Handler(Looper.getMainLooper());
            mListener = listener;
            startRequest(context, appToken, placementName);
        }
    }
    //==============================================================================================
    // Tracking data
    //==============================================================================================

    /**
     * Sets the age for the ad request
     *
     * @param age age of the target
     */
    public void setAge(int age) {

        Log.v(TAG, "setAge: " + age);
        mTrackingModel.age = age;
    }

    /**
     * Sets education for the ad request
     *
     * @param education education of the target as string
     */
    public void setEducation(String education) {

        Log.v(TAG, "setEducation: " + education);
        mTrackingModel.education = education;
    }

    /**
     * Adds an interest keyword for the request
     *
     * @param interest interest keyword of the target
     */
    public void addInterest(String interest) {

        Log.v(TAG, "addInterest: " + interest);
        mTrackingModel.addInterest(interest);
    }

    /**
     * Possible gender values
     */
    public enum Gender {
        MALE,
        FEMALE
    }

    /**
     * Sets the gender of the target
     *
     * @param gender gender of the garget as Enum value
     */
    public void setGender(Gender gender) {

        Log.v(TAG, "setGender: " + gender.name());
        mTrackingModel.gender = gender.name().toLowerCase();
    }

    /**
     * Sets a value for the request to tell if the inapp purchased are enabled
     *
     * @param iap true if in app purchased are enabled, false if not
     */
    public void setInAppPurchasesEnabled(boolean iap) {

        Log.v(TAG, "setInAppPurchasesEnabled: " + iap);
        mTrackingModel.iap = iap;
    }

    /**
     * Sets the total amount spent by this client in in app purchased
     *
     * @param iapTotal total amount spent as float
     */
    public void setInAppPurchasesTotal(float iapTotal) {

        Log.v(TAG, "setInAppPurchasesTotal: " + iapTotal);
        mTrackingModel.iap_total = iapTotal;
    }

    //==============================================================================================
    // Private methods
    //==============================================================================================
    protected synchronized void startRequest(Context context, String appToken, String placementName) {

        if (context == null || TextUtils.isEmpty(appToken) || TextUtils.isEmpty(placementName)) {
            invokeFail(PubnativeException.REQUEST_PARAMETERS_INVALID);
        } else if (PubnativeDeviceUtils.isNetworkAvailable(context)) {
            mContext = context;
            mPlacement = new PubnativePlacement();
            mPlacement.load(mContext, appToken, placementName, new PubnativePlacement.Listener() {

                @Override
                public void onPubnativePlacementReady(PubnativePlacement placement) {

                    checkDeliveryCaps();
                }

                @Override
                public void onPubnativePlacementLoadFail(PubnativePlacement placement, Exception exception) {

                    invokeFail(exception);
                }
            });
        } else {
            invokeFail(PubnativeException.REQUEST_NO_INTERNET);
        }
    }

    protected void checkDeliveryCaps() {

        Log.v(TAG, "checkDeliveryCaps");
        if (mPlacement.isDisabled()) {
            invokeFail(PubnativeException.PLACEMENT_DISABLED);
        } else if (mPlacement.isFrequencyCapActive()) {
            invokeFail(PubnativeException.PLACEMENT_FREQUENCY_CAP);
        } else if (mPlacement.isPacingCapActive()) {
            if (mAd == null) {
                invokeFail(PubnativeException.PLACEMENT_PACING_CAP);
            } else {
                invokeLoad(mAd);
            }
        } else {
            invokeStart();
            waterfall();
        }
    }

    protected void waterfall() {

        Log.v(TAG, "waterfall");
        mPlacement.next();
        PubnativeNetworkModel networkModel = mPlacement.currentNetwork();
        if (networkModel == null) {
            mPlacement.getInsightModel().sendRequestInsight();
            invokeFail(PubnativeException.REQUEST_NO_FILL);
        } else {
            PubnativeNetworkRequestAdapter adapter = PubnativeNetworkAdapterFactory.createNetworkAdapter(networkModel);
            if (adapter == null) {
                mPlacement.trackUnreachableNetwork(0, PubnativeException.REQUEST_ADAPTER_CREATION);
                waterfall();
            } else {
                // Add ML extras for adapter
                Map<String, String> extras = new HashMap<String, String>();
                extras.put(PubnativeNetworkRequestAdapter.EXTRA_REQID, mPlacement.getTrackingUUID());
                adapter.setExtras(extras);
                adapter.setListener(this);
                adapter.execute(mContext, networkModel.timeout);
            }
        }
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeStart() {

        Log.v(TAG, "invokeStart");
        // Ensure returning callbacks on same thread than where we started the call
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onPubnativeNetworkRequestStarted(PubnativeNetworkRequest.this);
                }
            }
        });
    }

    protected void invokeLoad(final PubnativeAdModel ad) {

        Log.v(TAG, "invokeLoad");
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                mIsRunning = false;
                if (mListener != null) {
                    mListener.onPubnativeNetworkRequestLoaded(PubnativeNetworkRequest.this, ad);
                }
                mListener = null;
            }
        });
    }

    protected void invokeFail(final Exception exception) {

        Log.v(TAG, "invokeFail: " + exception);
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                mIsRunning = false;
                if (mListener != null) {
                    mListener.onPubnativeNetworkRequestFailed(PubnativeNetworkRequest.this, exception);
                }
                mListener = null;
            }
        });
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeNetworkAdapterListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeNetworkAdapterRequestStarted(PubnativeNetworkRequestAdapter adapter) {

        Log.v(TAG, "onAdapterRequestStarted");
        mRequestStartTimestamp = System.currentTimeMillis();
    }

    @Override
    public void onPubnativeNetworkAdapterRequestLoaded(PubnativeNetworkRequestAdapter adapter, PubnativeAdModel ad) {

        Log.v(TAG, "onAdapterRequestLoaded");
        long responseTime = System.currentTimeMillis() - mRequestStartTimestamp;
        if (ad == null) {
            mPlacement.trackAttemptedNetwork(responseTime, PubnativeException.REQUEST_NO_FILL);
            waterfall();
        } else {
            mAd = ad;
            // Track succeded network
            mPlacement.trackSuccededNetwork(responseTime);
            // Default tracking data
            mAd.setInsightModel(mPlacement.getInsightModel());
            // Finish the request
            invokeLoad(ad);
        }
    }

    @Override
    public void onPubnativeNetworkAdapterRequestFailed(PubnativeNetworkRequestAdapter adapter, Exception exception) {

        Log.e(TAG, "onAdapterRequestFailed: " + exception);
        // Waterfall to the next network
        long responseTime = System.currentTimeMillis() - mRequestStartTimestamp;
        mPlacement.trackUnreachableNetwork(responseTime, exception);
        waterfall();
    }
}
