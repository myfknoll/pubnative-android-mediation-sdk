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

import net.pubnative.AdvertisingIdClient;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterFactory;
import net.pubnative.mediation.config.PubnativeDeliveryManager;
import net.pubnative.mediation.config.PubnativePlacement;
import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.config.model.PubnativeDeliveryRuleModel;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.config.model.PubnativePriorityRuleModel;
import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.insights.PubnativeInsightsManager;
import net.pubnative.mediation.insights.model.PubnativeInsightCrashModel;
import net.pubnative.mediation.insights.model.PubnativeInsightDataModel;
import net.pubnative.mediation.request.model.PubnativeAdModel;
import net.pubnative.mediation.utils.PubnativeDeviceUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PubnativeNetworkRequest implements PubnativeNetworkAdapter.Listener {

    private static       String TAG                           = PubnativeNetworkRequest.class.getSimpleName();
    private static final String TRACKING_PARAMETER_APP_TOKEN  = "app_token";
    private static final String TRACKING_PARAMETER_REQUEST_ID = "reqid";
    protected Context                          mContext;
    protected PubnativeNetworkRequest.Listener mListener;
    protected PubnativePlacement               mPlacement;
    protected PubnativeInsightDataModel        mTrackingModel;
    protected PubnativeAdModel                 mAd;
    protected long                             mRequestStartTimestamp;
    protected boolean                          mIsRunning;
    protected Handler                          mHandler;
    protected String                           mRequestID;
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
        } else {
            mHandler = new Handler(Looper.getMainLooper());
            mListener = listener;
            if (mIsRunning) {
                Log.e(TAG, "start - request already running, dropping the call");
            } else {
                if (context == null || TextUtils.isEmpty(appToken) || TextUtils.isEmpty(placementName)) {
                    invokeFail(PubnativeException.REQUEST_PARAMETERS_INVALID);
                } else {
                    mIsRunning = true;
                    mContext = context;
                    mTrackingModel = new PubnativeInsightDataModel();
                    mRequestID = UUID.randomUUID().toString();
                    invokeStart();
                    if (PubnativeDeviceUtils.isNetworkAvailable(mContext)) {
                        mPlacement = new PubnativePlacement();
                        mPlacement.load(mContext, appToken, placementName, new PubnativePlacement.Listener() {

                            @Override
                            public void onPubnativePlacementReady(PubnativePlacement placement) {

                                startTracking();
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
            }
        }
    }

    protected void startTracking() {

        Log.v(TAG, "startTracking");
        // Reset tracking
        mTrackingModel.reset();
        mTrackingModel.fillDefaults(mContext);
        mTrackingModel.placement_name = mPlacement.getName();
        mTrackingModel.delivery_segment_ids = mPlacement.getDeliveryRule().segment_ids;
        mTrackingModel.ad_format_code = mPlacement.getAdFormatCode();
        mTrackingModel.fillAdvertisingId(mContext, new AdvertisingIdClient.Listener() {

            @Override
            public void onAdvertisingIdClientFinish(AdvertisingIdClient.AdInfo adInfo) {

                startRequest();
            }

            @Override
            public void onAdvertisingIdClientFail(Exception exception) {

                startRequest();
            }
        });
    }

    protected void startRequest() {

        Log.v(TAG, "startRequest");
        PubnativeDeliveryRuleModel deliveryRuleModel = mPlacement.getDeliveryRule();
        if (deliveryRuleModel.isFrequencyCapReached(mContext, mPlacement.getName())) {
            invokeFail(PubnativeException.REQUEST_FREQUENCY_CAP);
        } else {
            Calendar overdueCalendar = deliveryRuleModel.getPacingOverdueCalendar();
            Calendar pacingCalendar = PubnativeDeliveryManager.getPacingCalendar(mPlacement.getName());
            if (overdueCalendar == null || pacingCalendar == null || pacingCalendar.before(overdueCalendar)) {
                // Pacing cap reset or deactivated or not reached, start adapter request with new request ID
                doNextNetworkRequest();
            } else {
                // Pacing cap active and limit reached
                // return the same mAd during the pacing cap amount of time
                if (mAd == null) {
                    invokeFail(PubnativeException.REQUEST_PACING_CAP);
                } else {
                    invokeLoad(mAd);
                }
            }
        }
    }

    protected void doNextNetworkRequest() {

        Log.v(TAG, "doNextNetworkRequest");
        mPlacement.waterfall();
        PubnativePriorityRuleModel currentPriorityRule = mPlacement.currentPriority();
        if (currentPriorityRule == null) {
            trackRequestInsight();
            invokeFail(PubnativeException.REQUEST_NO_FILL);
        } else {
            PubnativeNetworkModel networkModel = mPlacement.currentNetwork();
            if (networkModel == null) {
                trackUnreachableNetwork(PubnativeException.REQUEST_NETWORK_NOT_FOUND);
                doNextNetworkRequest();
            } else {
                PubnativeNetworkAdapter adapter = PubnativeNetworkAdapterFactory.createAdapter(networkModel);
                if (adapter == null) {
                    trackUnreachableNetwork(PubnativeException.REQUEST_ADAPTER_CREATION);
                    doNextNetworkRequest();
                } else {
                    // Add ML extras for adapter
                    Map<String, String> extras = new HashMap<String, String>();
                    extras.put(TRACKING_PARAMETER_REQUEST_ID, mRequestID);
                    adapter.setExtras(extras);
                    adapter.doRequest(mContext, networkModel.timeout, this);
                }
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
    // TRACKING
    //==============================================================================================

    protected void trackRequestInsight() {

        Log.v(TAG, "trackRequestInsight");
        String requestURL = (String) mPlacement.getConfig().getGlobal(PubnativeConfigModel.ConfigContract.REQUEST_BEACON);
        if (TextUtils.isEmpty(requestURL)) {
            Log.e(TAG, "trackRequestInsight - Error: Tracking request aborted, requestURL not found");
        } else {
            PubnativeInsightsManager.trackData(mContext, requestURL, getTrackingParameters(), mTrackingModel);
        }
    }

    private void trackUnreachableNetwork(Exception exception) {

        Log.v(TAG, "trackUnreachableNetwork");
        PubnativePriorityRuleModel priorityRuleModel = mPlacement.currentPriority();
        if (priorityRuleModel == null) {
            Log.e(TAG, "trackUnreachableNetwork - Error: Tracking unreachable network, priorityRuleModel not found");
        } else {
            long responseTime = System.currentTimeMillis() - mRequestStartTimestamp;
            PubnativeInsightCrashModel crashModel = new PubnativeInsightCrashModel();
            crashModel.error = exception.getMessage();
            crashModel.details = exception.toString();
            mTrackingModel.addUnreachableNetwork(priorityRuleModel.network_code);
            mTrackingModel.addNetwork(priorityRuleModel, responseTime, crashModel);
        }
    }

    private void trackAttemptedNetwork(Exception exception) {

        Log.v(TAG, "trackAttemptedNetwork");
        PubnativePriorityRuleModel priorityRuleModel = mPlacement.currentPriority();
        if (priorityRuleModel == null) {
            Log.e(TAG, "trackAttemptedNetwork - Error: Tracking attempted network, priorityRuleModel not found");
        } else {
            long responseTime = System.currentTimeMillis() - mRequestStartTimestamp;
            PubnativeInsightCrashModel crashModel = new PubnativeInsightCrashModel();
            crashModel.error = exception.getMessage();
            crashModel.details = exception.toString();
            mTrackingModel.addAttemptedNetwork(priorityRuleModel.network_code);
            mTrackingModel.addNetwork(priorityRuleModel, responseTime, crashModel);
        }
    }

    protected Map<String, String> getTrackingParameters() {

        Log.v(TAG, "getTrackingParameters");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(TRACKING_PARAMETER_APP_TOKEN, mPlacement.getAppToken());
        parameters.put(TRACKING_PARAMETER_REQUEST_ID, mRequestID);
        Map<String, String> requestParameters = mPlacement.getConfig().request_params;
        if (requestParameters != null) {
            for (String key : requestParameters.keySet()) {
                String value = requestParameters.get(key);
                parameters.put(key, value);
            }
        }
        return parameters;
    }

    //==============================================================================================
    // Tracking data
    //==============================================================================================
    public void setAge(int age) {

        Log.v(TAG, "setAge: " + age);
        mTrackingModel.age = age;
    }

    public void setEducation(String education) {

        Log.v(TAG, "setEducation: " + education);
        mTrackingModel.education = education;
    }

    public void addInterest(String interest) {

        Log.v(TAG, "addInterest: " + interest);
        mTrackingModel.addInterest(interest);
    }

    public enum Gender {
        MALE,
        FEMALE
    }

    public void setGender(Gender gender) {

        Log.v(TAG, "setGender: " + gender.name());
        mTrackingModel.gender = gender.name().toLowerCase();
    }

    public void setInAppPurchasesEnabled(boolean iap) {

        Log.v(TAG, "setInAppPurchasesEnabled: " + iap);
        mTrackingModel.iap = iap;
    }

    public void setInAppPurchasesTotal(float iapTotal) {

        Log.v(TAG, "setInAppPurchasesTotal: " + iapTotal);
        mTrackingModel.iap_total = iapTotal;
    }
    //==============================================================================================
    // Callbacks
    //==============================================================================================

    // PubnativeNetworkAdapterListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onPubnativeNetworkAdapterRequestStarted(PubnativeNetworkAdapter adapter) {

        Log.v(TAG, "onAdapterRequestStarted");
        mRequestStartTimestamp = System.currentTimeMillis();
    }

    @Override
    public void onPubnativeNetworkAdapterRequestLoaded(PubnativeNetworkAdapter adapter, PubnativeAdModel ad) {

        Log.v(TAG, "onAdapterRequestLoaded");
        long responseTime = System.currentTimeMillis() - mRequestStartTimestamp;
        if (ad == null) {
            trackAttemptedNetwork(PubnativeException.REQUEST_NO_FILL);
            doNextNetworkRequest();
        } else {
            mAd = ad;
            // Track succeded network
            PubnativePriorityRuleModel priorityRuleModel = mPlacement.currentPriority();
            mTrackingModel.network = priorityRuleModel.network_code;
            mTrackingModel.addNetwork(priorityRuleModel, responseTime, null);
            // Send tracking
            trackRequestInsight();
            // Default tracking data
            mAd.setTrackingInfo(mTrackingModel);
            // Impression tracking data
            String impressionURL = (String) mPlacement.getConfig().getGlobal(PubnativeConfigModel.ConfigContract.IMPRESSION_BEACON);
            mAd.setTrackingImpressionData(impressionURL, getTrackingParameters());
            // click tracking data
            String clickURL = (String) mPlacement.getConfig().getGlobal(PubnativeConfigModel.ConfigContract.CLICK_BEACON);
            mAd.setTrackingClickData(clickURL, getTrackingParameters());
            // Update pacing
            PubnativeDeliveryManager.updatePacingCalendar(mTrackingModel.placement_name);
            // Finish the request
            invokeLoad(ad);
        }
    }

    @Override
    public void onPubnativeNetworkAdapterRequestFailed(PubnativeNetworkAdapter adapter, Exception exception) {

        Log.e(TAG, "onAdapterRequestFailed: " + exception);
        // Waterfall to the next network
        trackUnreachableNetwork(exception);
        doNextNetworkRequest();
    }
}
