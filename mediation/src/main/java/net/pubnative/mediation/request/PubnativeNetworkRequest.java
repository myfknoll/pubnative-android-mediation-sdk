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

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.adapter.PubnativeNetworkAdapter;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterFactory;
import net.pubnative.mediation.adapter.PubnativeNetworkAdapterListener;
import net.pubnative.mediation.config.PubnativeConfigManager;
import net.pubnative.mediation.config.PubnativeConfigRequestListener;
import net.pubnative.mediation.config.PubnativeDeliveryManager;
import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.config.model.PubnativeDeliveryRuleModel;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.config.model.PubnativePlacementModel;
import net.pubnative.mediation.config.model.PubnativePriorityRuleModel;
import net.pubnative.mediation.insights.PubnativeInsightsManager;
import net.pubnative.mediation.insights.model.PubnativeInsightCrashModel;
import net.pubnative.mediation.insights.model.PubnativeInsightDataModel;
import net.pubnative.mediation.request.model.PubnativeAdModel;
import net.pubnative.mediation.utils.PubnativeDeviceUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class PubnativeNetworkRequest implements PubnativeNetworkAdapterListener,
                                                PubnativeConfigRequestListener {

    private static String TAG = PubnativeNetworkRequest.class.getSimpleName();

    private static final String TRACKING_PARAMETER_APP_TOKEN  = "app_token";
    private static final String TRACKING_PARAMETER_REQUEST_ID = "reqid";

    protected Context                         mContext;
    protected PubnativeNetworkRequestListener mListener;
    protected PubnativeConfigModel            mConfig;
    protected PubnativeAdModel                mAd;
    protected PubnativeInsightDataModel       mTrackingModel;
    protected String                          mAppToken;
    protected String                          mPlacementID;
    protected int                             mCurrentNetworkIndex;
    protected long                            mRequestStartTimestamp;
    protected boolean                         mIsRunning;
    protected Handler                         mHandler;
    //==============================================================================================
    // Pubic methods
    //==============================================================================================

    /**
     * Starts a new mAd request.
     *
     * @param context     valid Context object.
     * @param appToken    valid AppToken provided by Pubnative.
     * @param placementID valid placementId provided by Pubnative.
     * @param listener    valid Listener to keep track of request callbacks.
     */
    public void start(Context context, String appToken, String placementID, PubnativeNetworkRequestListener listener) {

        Log.v(TAG, "start(Context context, String appToken, String placementID, PubnativeNetworkRequestListener listener)");

        if (listener == null) {
            // Just drop the call
            Log.e(TAG, "start - listener not specified, dropping the call");
        } else {
            mHandler = new Handler();
            mListener = listener;
            if (mIsRunning) {
                Log.e(TAG, "start - Request already running, dropping the call");
            } else {
                if (context == null || TextUtils.isEmpty(appToken) || TextUtils.isEmpty(placementID)) {

                    invokeFail(new IllegalArgumentException("PubnativeNetworkRequest.start - invalid start parameters"));

                    Log.e(TAG, "start - invalid start parameters");
                } else {
                    mContext = context;
                    mTrackingModel = new PubnativeInsightDataModel();
                    mAppToken = appToken;
                    mPlacementID = placementID;
                    mCurrentNetworkIndex = -1;
                    mIsRunning = true;
                    invokeStart();
                    if (PubnativeDeviceUtils.isNetworkAvailable(mContext)) {
                        getConfig(appToken, this);
                    } else {
                        invokeFail(new Exception("PubnativeNetworkRequest.start - internet connection not available"));

                        Log.e(TAG, "start - internet connection not available");
                    }
                }
            }
        }
    }

    protected void getConfig(String appToken, PubnativeConfigRequestListener listener) {
        // This method getConfig() here gets the stored/downloaded mConfig and
        // continues to startRequest() in it's callback "onConfigLoaded()".
        PubnativeConfigManager.getConfig(mContext, appToken, listener);
    }

    private void startRequest(PubnativeConfigModel configModel) {

        Log.v(TAG, "startRequest(PubnativeConfigModel configModel)");

        mConfig = configModel;
        if (mConfig == null || mConfig.isNullOrEmpty()) {
            invokeFail(new NetworkErrorException("PubnativeNetworkRequest.start - null or invalid config retrieved"));

            Log.e(TAG, "startRequest - null or invalid config retrieved");
        } else {
            PubnativePlacementModel placement = mConfig.getPlacement(mPlacementID);
            if (placement == null) {
                invokeFail(new IllegalArgumentException("PubnativeNetworkRequest.start - placement_id \'" + mPlacementID + "\' not found"));

                Log.e(TAG, "startRequest - placement_id '\" + mPlacementID + \"' not found");
            } else if (placement.delivery_rule == null) {
                invokeFail(new Exception("PubnativeNetworkRequest.start - config error, delivery rule not found"));

                Log.e(TAG, "startRequest - config error, delivery rule not found");
            } else if (placement.delivery_rule.isActive()) {
                startTracking();
            } else {
                invokeFail(new Exception("PubnativeNetworkRequest.start - placement_id \'" + mPlacementID + "\' not active"));

                Log.e(TAG, "startRequest - placement_id '\" + mPlacementID + \"' not active");
            }
        }
    }

    protected void startTracking() {

        Log.v(TAG, "startTracking()");

        new Thread(new Runnable() {

            @Override
            public void run() {

                // Prepare looper for further handlers
                Looper.prepare();
                // Reset tracking
                mTrackingModel.reset();
                mTrackingModel.fillDefaults(mContext);
                mTrackingModel.placement_name = mPlacementID;
                PubnativePlacementModel placement = mConfig.getPlacement(mPlacementID);
                if (placement != null) {
                    mTrackingModel.delivery_segment_ids = placement.delivery_rule.segment_ids;
                    mTrackingModel.ad_format_code = placement.ad_format_code;
                }
                startRequest();
            }
        }).start();
    }

    protected void startRequest() {

        Log.v(TAG, "startRequest()");

        PubnativeDeliveryRuleModel deliveryRuleModel = mConfig.getPlacement(mPlacementID).delivery_rule;
        if (deliveryRuleModel.isFrequencyCapReached(mContext, mPlacementID)) {
            invokeFail(new Exception("Pubnative - start error: (frequecy_cap) too many ads"));

            Log.e(TAG, "startRequest - (frequecy_cap) too many ads");
        } else {
            Calendar overdueCalendar = deliveryRuleModel.getPacingOverdueCalendar();
            Calendar pacingCalendar = PubnativeDeliveryManager.getPacingCalendar(mPlacementID);

            if (overdueCalendar == null || pacingCalendar == null || pacingCalendar.before(overdueCalendar)) {

                // Pacing cap reset or deactivated or not reached, start adapter request with new request ID
                String requestID = UUID.randomUUID().toString();
                doNextNetworkRequest(requestID);
            } else {
                // Pacing cap active and limit reached
                // return the same mAd during the pacing cap amount of time
                if (mAd == null) {
                    invokeFail(new Exception("Pubnative - start error: (pacing_cap) too many ads"));

                    Log.e(TAG, "startRequest - (frequecy_cap) too many ads");
                } else {
                    invokeLoad(mAd);
                }
            }
        }
    }

    protected void doNextNetworkRequest(String requestID) {

        Log.v(TAG, "doNextNetworkRequest(String requestID = " + requestID + ")");

        mCurrentNetworkIndex++;
        PubnativePriorityRuleModel currentPriorityRule = mConfig.getPriorityRule(mPlacementID, mCurrentNetworkIndex);
        if (currentPriorityRule == null) {
            trackRequestInsight(requestID);
            invokeFail(new Exception("Pubnative - no fill"));

            Log.e(TAG, "doNextNetworkRequest - no fill");
        } else {
            PubnativeNetworkModel networkModel = mConfig.getNetwork(currentPriorityRule.network_code);
            if (networkModel == null) {
                trackUnreachableNetwork(PubnativeInsightCrashModel.ERROR_CONFIG, "Network id not found");
                doNextNetworkRequest(requestID);
            } else {
                PubnativeNetworkAdapter adapter = PubnativeNetworkAdapterFactory.createAdapter(networkModel);
                if (adapter == null) {
                    trackUnreachableNetwork(PubnativeInsightCrashModel.ERROR_CONFIG, "Adapter creation error");
                    doNextNetworkRequest(requestID);
                } else {
                    // Add ML extras for adapter
                    Map extras = new HashMap();
                    extras.put(TRACKING_PARAMETER_REQUEST_ID, requestID);
                    adapter.doRequest(mContext, networkModel.timeout, extras, this);
                }
            }
        }
    }

    protected void invokeStart() {
        // Ensure returning callbacks on same thread than where we started the call
        mHandler.post(new Runnable() {

            @Override
            public void run() {

                if (mListener != null) {
                    mListener.onRequestStarted(PubnativeNetworkRequest.this);
                }
            }
        });
    }

    protected void invokeLoad(final PubnativeAdModel ad) {

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                mIsRunning = false;
                if (mListener != null) {
                    mListener.onRequestLoaded(PubnativeNetworkRequest.this, ad);
                }
            }
        });
    }

    protected void invokeFail(final Exception exception) {

        mHandler.post(new Runnable() {

            @Override
            public void run() {

                mIsRunning = false;
                if (mListener != null) {
                    mListener.onRequestFailed(PubnativeNetworkRequest.this, exception);
                }
            }
        });
    }
    //==============================================================================================
    // TRACKING
    //==============================================================================================

    protected void trackRequestInsight(String requestID) {

        String requestURL = (String) mConfig.getGlobal(PubnativeConfigModel.ConfigContract.REQUEST_BEACON);
        if (TextUtils.isEmpty(requestURL)) {
            Log.e(TAG, "trackRequestInsight - Error: Tracking request aborted, requestURL not found");
        } else {
            PubnativeInsightsManager.trackData(mContext, requestURL, getTrackingParameters(requestID), mTrackingModel);
        }
    }

    private void trackUnreachableNetwork(String error, String details) {

        PubnativePriorityRuleModel priorityRuleModel = mConfig.getPriorityRule(mPlacementID, mCurrentNetworkIndex);
        if (priorityRuleModel == null) {
            Log.e(TAG, "trackUnreachableNetwork - Error: Tracking unreachable network, priorityRuleModel not found");
        } else {
            long responseTime = System.currentTimeMillis() - mRequestStartTimestamp;
            PubnativeInsightCrashModel crashModel = new PubnativeInsightCrashModel();
            crashModel.error = error;
            crashModel.details = details;
            mTrackingModel.addUnreachableNetwork(priorityRuleModel.network_code);
            mTrackingModel.addNetwork(priorityRuleModel, responseTime, crashModel);
        }
    }

    private void trackAttemptedNetwork(String error, String details) {

        PubnativePriorityRuleModel priorityRuleModel = mConfig.getPriorityRule(mPlacementID, mCurrentNetworkIndex);
        if (priorityRuleModel == null) {
            Log.e(TAG, "trackAttemptedNetwork - Error: Tracking attempted network, priorityRuleModel not found");
        } else {
            long responseTime = System.currentTimeMillis() - mRequestStartTimestamp;
            PubnativeInsightCrashModel crashModel = new PubnativeInsightCrashModel();
            crashModel.error = error;
            crashModel.details = details;
            mTrackingModel.addAttemptedNetwork(priorityRuleModel.network_code);
            mTrackingModel.addNetwork(priorityRuleModel, responseTime, crashModel);
        }
    }

    protected Map<String, String> getTrackingParameters(String requestID) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(TRACKING_PARAMETER_APP_TOKEN, mAppToken);
        parameters.put(TRACKING_PARAMETER_REQUEST_ID, requestID);
        return parameters;
    }

    public void setAge(int age) {

        mTrackingModel.age = age;
    }

    public void setEducation(String education) {

        mTrackingModel.education = education;
    }

    public void addInterest(String interest) {

        mTrackingModel.addInterest(interest);
    }

    public enum Gender {
        MALE,
        FEMALE
    }

    public void setGender(Gender gender) {

        mTrackingModel.gender = gender.name().toLowerCase();
    }

    public void setInAppPurchasesEnabled(boolean iap) {

        mTrackingModel.iap = iap;
    }

    public void setInAppPurchasesTotal(float iapTotal) {

        mTrackingModel.iap_total = iapTotal;
    }
    //==============================================================================================
    // Callbacks
    //==============================================================================================

    // PubnativeConfigRequestListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onConfigLoaded(PubnativeConfigModel configModel) {

        startRequest(configModel);
    }

    // PubnativeNetworkAdapterListener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onAdapterRequestStarted(PubnativeNetworkAdapter adapter) {

        mRequestStartTimestamp = System.currentTimeMillis();
    }

    @Override
    public void onAdapterRequestLoaded(PubnativeNetworkAdapter adapter, PubnativeAdModel ad) {

        long responseTime = System.currentTimeMillis() - mRequestStartTimestamp;
        Map<String, String> extras = adapter.getExtras();
        String requestID = extras.get(TRACKING_PARAMETER_REQUEST_ID);
        if (ad == null) {
            trackAttemptedNetwork(PubnativeInsightCrashModel.ERROR_NO_FILL, "");
            doNextNetworkRequest(requestID);
        } else {
            mAd = ad;
            // Track succeded network
            PubnativePriorityRuleModel priorityRuleModel = mConfig.getPriorityRule(mPlacementID, mCurrentNetworkIndex);
            mTrackingModel.network = priorityRuleModel.network_code;
            mTrackingModel.addNetwork(priorityRuleModel, responseTime, null);
            // Send tracking
            trackRequestInsight(requestID);
            // Default tracking data
            mAd.setTrackingInfo(mTrackingModel);
            // Impression tracking data
            String impressionURL = (String) mConfig.getGlobal(PubnativeConfigModel.ConfigContract.IMPRESSION_BEACON);
            mAd.setTrackingImpressionData(impressionURL, getTrackingParameters(requestID));
            // click tracking data
            String clickURL = (String) mConfig.getGlobal(PubnativeConfigModel.ConfigContract.CLICK_BEACON);
            mAd.setTrackingClickData(clickURL, getTrackingParameters(requestID));
            // Update pacing
            PubnativeDeliveryManager.updatePacingCalendar(mTrackingModel.placement_name);
            // Finish the request
            invokeLoad(ad);
        }
    }

    @Override
    public void onAdapterRequestFailed(PubnativeNetworkAdapter adapter, Exception exception) {

        Log.e(TAG, "onAdapterRequestFailed - adapter error: " + exception);
        // Waterfall to the next network
        if (IllegalArgumentException.class.isAssignableFrom(exception.getClass())) {
            trackUnreachableNetwork(PubnativeInsightCrashModel.ERROR_CONFIG, exception.toString());
        } else if (TimeoutException.class.isAssignableFrom(exception.getClass())) {
            trackUnreachableNetwork(PubnativeInsightCrashModel.ERROR_TIMEOUT, exception.toString());
        } else {
            trackUnreachableNetwork(PubnativeInsightCrashModel.ERROR_ADAPTER, exception.toString());
        }
        Map<String, String> extras = adapter.getExtras();
        String requestID = extras.get(TRACKING_PARAMETER_REQUEST_ID);
        doNextNetworkRequest(requestID);
    }
}
