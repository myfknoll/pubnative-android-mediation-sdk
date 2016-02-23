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
import android.os.AsyncTask;
import android.text.TextUtils;

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
    //==============================================================================================
    // TRACKING
    //==============================================================================================

    protected void trackRequestInsight(String requestID) {

        if (mConfig != null && ((mTrackingModel.attempted_networks != null && mTrackingModel.attempted_networks.size() > 0) || mTrackingModel.network != null)) {
            String requestURL = (String) this.mConfig.globals.get(PubnativeConfigModel.ConfigContract.REQUEST_BEACON);
            if (!TextUtils.isEmpty(requestURL)) {
                PubnativeInsightsManager.trackData(this.mContext, requestURL, getTrackingParameters(requestID), this.mTrackingModel);
            }
        }
    }

    protected Map<String, String> getTrackingParameters(String requestID) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(TRACKING_PARAMETER_APP_TOKEN, this.mAppToken);
        parameters.put(TRACKING_PARAMETER_REQUEST_ID, requestID);
        return parameters;
    }

    public void setAge(int age) {

        mTrackingModel.age = age;
    }

    public void setEducation(String education) {

        this.mTrackingModel.education = education;
    }

    public void addInterest(String interest) {

        this.mTrackingModel.addInterest(interest);
    }

    public enum Gender {
        MALE,
        FEMALE
    }

    public void setGender(Gender gender) {

        this.mTrackingModel.gender = gender.name().toLowerCase();
    }

    public void setInAppPurchasesEnabled(boolean iap) {

        this.mTrackingModel.iap = iap;
    }

    public void setInAppPurchasesTotal(float iapTotal) {

        this.mTrackingModel.iap_total = iapTotal;
    }
    //==============================================================================================
    // Config operations
    //==============================================================================================

    private PubnativePlacementModel getCurrentPlacement() {

        PubnativePlacementModel result = null;
        if (mConfig != null && mConfig.placements != null) {
            result = mConfig.placements.get(mPlacementID);
        }
        return result;
    }

    private PubnativePriorityRuleModel getCurrentPriorityRule() {

        PubnativePriorityRuleModel result    = null;
        PubnativePlacementModel    placement = getCurrentPlacement();
        if (placement != null && placement.priority_rules != null && placement.priority_rules.size() > mCurrentNetworkIndex) {
            result = placement.priority_rules.get(mCurrentNetworkIndex);
        }
        return result;
    }
    //==============================================================================================
    // Pubic methods
    //==============================================================================================

    /**
     * Starts a new mAd request.
     *
     * @param context     valid Context object.
     * @param appToken    valid mAppToken provided by Pubnative.
     * @param placementID valid placementId provided by Pubnative.
     * @param listener    valid mListener to keep track of request callbacks.
     */
    public void start(Context context, String appToken, String placementID, PubnativeNetworkRequestListener listener) {

        this.mAppToken = appToken;
        this.mPlacementID = placementID;
        this.mContext = context;
        this.mCurrentNetworkIndex = 0;
        if (listener == null) {
            // Just drop the call
            System.out.println("PubnativeNetworkRequest.start - mListener not specified, dropping the call");
            return;
        }
        this.mListener = listener;
        if (this.mIsRunning) {
            System.out.println("PubnativeNetworkRequest.start - Request already running, dropping the call");
        } else {
            this.mIsRunning = true;
            this.invokeStart();
            if (this.mContext == null || TextUtils.isEmpty(appToken) || TextUtils.isEmpty(placementID)) {
                this.invokeFail(new IllegalArgumentException("PubnativeNetworkRequest.start - invalid start parameters"));
            } else {
                if (PubnativeDeviceUtils.isNetworkAvailable(context)) {
                    this.getConfig(context, appToken, this);
                } else {
                    this.invokeFail(new Exception("PubnativeNetworkRequest.start - internet connection not available"));
                }
            }
        }
    }

    protected void getConfig(Context context, String appToken, PubnativeConfigRequestListener listener) {
        // This method getConfig() here gets the stored/downloaded mConfig and
        // continues to startRequest() in it's callback "onConfigLoaded()".
        PubnativeConfigManager.getConfig(context, appToken, listener);
    }

    private void startRequest(PubnativeConfigModel configModel) {

        mConfig = configModel;
        if (mConfig == null || mConfig.isNullOrEmpty()) {
            invokeFail(new NetworkErrorException("PubnativeNetworkRequest.start - invalid mConfig retrieved"));
        } else {
            PubnativePlacementModel placement = mConfig.getPlacement(mPlacementID);
            if (placement == null) {
                this.invokeFail(new IllegalArgumentException("PubnativeNetworkRequest.start - placement_id not found"));
            } else if (placement.delivery_rule == null) {
                this.invokeFail(new Exception("PubnativeNetworkRequest.start - mConfig error"));
            } else if (placement.delivery_rule.isActive()) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {

                        mTrackingModel.reset();
                        mTrackingModel.fillDefaults(mContext);
                        mTrackingModel.placement_name = mPlacementID;
                        mTrackingModel.delivery_segment_ids = getCurrentPlacement().delivery_rule.segment_ids;
                        mTrackingModel.ad_format_code = getCurrentPlacement().ad_format_code;
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void obj) {

                        PubnativeNetworkRequest.this.startRequest();
                    }
                }.execute();
            } else {
                this.invokeFail(new Exception("PubnativeNetworkRequest.start - placement_id not active"));
            }
        }
    }

    protected void startRequest() {

        PubnativeDeliveryRuleModel deliveryRuleModel = getCurrentPlacement().delivery_rule;
        if (deliveryRuleModel.isFrequencyCapReached(mContext, this.mPlacementID)) {
            this.invokeFail(new Exception("Pubnative - start error: (frequecy_cap) too many ads"));
        } else {
            Calendar overdueCalendar = deliveryRuleModel.getPacingOverdueCalendar();
            Calendar pacingCalendar = PubnativeDeliveryManager.getPacingCalendar(this.mPlacementID);
            String requestID = UUID.randomUUID().toString();
            if (overdueCalendar == null || pacingCalendar == null || pacingCalendar.before(overdueCalendar)) {
                // Pacing cap reset or deactivated or not reached
                this.doNextNetworkRequest(requestID);
            } else {
                // Pacing cap active and limit reached
                // return the same mAd during the pacing cap amount of time
                if (this.mAd == null) {
                    this.invokeFail(new Exception("Pubnative - start error: (pacing_cap) too many ads"));
                } else {
                    this.invokeLoad(this.mAd, requestID);
                }
            }
        }
    }

    protected void doNextNetworkRequest(String requestID) {

        PubnativePriorityRuleModel currentPriorityRule = getCurrentPriorityRule();
        if (currentPriorityRule != null) {
            this.mCurrentNetworkIndex++;
            String currentNetworkID = currentPriorityRule.network_code;
            if (!TextUtils.isEmpty(currentNetworkID) && this.mConfig.networks.containsKey(currentNetworkID)) {
                PubnativeNetworkModel networkModel = this.mConfig.networks.get(currentNetworkID);
                PubnativeNetworkAdapter adapter = PubnativeNetworkAdapterFactory.createAdapter(networkModel);
                if (adapter == null) {
                    this.mTrackingModel.addUnreachableNetwork(currentNetworkID);
                    this.trackNetwork(currentPriorityRule, 0, PubnativeInsightCrashModel.ERROR_CONFIG, "creation error");
                    this.doNextNetworkRequest(requestID);
                } else {
                    // Add ML extras for adapter
                    Map extras = new HashMap();
                    extras.put(TRACKING_PARAMETER_REQUEST_ID, requestID.toString());
                    adapter.doRequest(this.mContext, networkModel.timeout, extras, this);
                }
            } else {
                this.mTrackingModel.addUnreachableNetwork(currentNetworkID);
                this.trackNetwork(currentPriorityRule, 0, PubnativeInsightCrashModel.ERROR_CONFIG, "id not found");
                this.doNextNetworkRequest(requestID);
            }
        } else {
            this.trackRequestInsight(requestID);
            this.invokeFail(new Exception("Pubnative - no fill"));
        }
    }

    // HELPERS
    protected void trackNetwork(PubnativePriorityRuleModel priorityRuleModel, long responsetime, String error, String details) {

        PubnativeInsightCrashModel crashModel   = null;
        PubnativeNetworkModel      networkModel = this.mConfig.networks.get(priorityRuleModel.network_code);
        if (networkModel.crash_report) {
            crashModel = new PubnativeInsightCrashModel();
            crashModel.error = error;
            crashModel.details = details;
        }
        this.mTrackingModel.addNetwork(priorityRuleModel, responsetime, crashModel);
    }

    protected void invokeStart() {

        if (this.mListener != null) {
            this.mListener.onRequestStarted(this);
        }
    }

    protected void invokeLoad(final PubnativeAdModel ad, String requestID) {

        this.mIsRunning = false;
        this.trackRequestInsight(requestID);
        if (this.mListener != null) {
            this.mListener.onRequestLoaded(this, ad);
        }
    }

    protected void invokeFail(final Exception exception) {

        this.mIsRunning = false;
        if (this.mListener != null) {
            this.mListener.onRequestFailed(this, exception);
        }
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

        this.mRequestStartTimestamp = System.currentTimeMillis();
    }

    @Override
    public void onAdapterRequestLoaded(PubnativeNetworkAdapter adapter, PubnativeAdModel ad) {

        long                responseTime = System.currentTimeMillis() - this.mRequestStartTimestamp;
        Map<String, String> extras       = adapter.getExtras();
        String              requestID    = extras.get(TRACKING_PARAMETER_REQUEST_ID);
        if (ad == null) {
            this.mTrackingModel.addAttemptedNetwork(this.mPriorityRule.network_code);
            this.trackNetwork(this.mPriorityRule, responseTime, PubnativeInsightCrashModel.ERROR_NO_FILL, "");
            this.doNextNetworkRequest(requestID);
        } else {
            this.mAd = ad;
            this.mTrackingModel.network = this.mPriorityRule.network_code;
            this.mTrackingModel.addNetwork(this.mPriorityRule, responseTime, null);
            // Default tracking data
            this.mAd.setTrackingInfo(this.mTrackingModel);
            // Impression tracking data
            String impressionURL = null;
            if (this.mConfig.globals.containsKey(PubnativeConfigModel.ConfigContract.IMPRESSION_BEACON)) {
                impressionURL = (String) this.mConfig.globals.get(PubnativeConfigModel.ConfigContract.IMPRESSION_BEACON);
            }
            this.mAd.setTrackingImpressionData(impressionURL, getTrackingParameters(requestID));
            // Click tracking data
            String clickURL = null;
            if (this.mConfig.globals.containsKey(PubnativeConfigModel.ConfigContract.CLICK_BEACON)) {
                clickURL = (String) this.mConfig.globals.get(PubnativeConfigModel.ConfigContract.CLICK_BEACON);
            }
            this.mAd.setTrackingClickData(clickURL, getTrackingParameters(requestID));
            PubnativeDeliveryManager.updatePacingCalendar(this.mTrackingModel.placement_name);
            this.invokeLoad(ad, requestID);
        }
    }

    @Override
    public void onAdapterRequestFailed(PubnativeNetworkAdapter adapter, Exception exception) {

        System.out.println("Pubnative - adapter error: " + exception);
        // Waterfall to the next network
        PubnativeInsightCrashModel crashModel = new PubnativeInsightCrashModel();
        crashModel.details = exception.toString();
        if (IllegalArgumentException.class.isAssignableFrom(exception.getClass())) {
            crashModel.error = PubnativeInsightCrashModel.ERROR_CONFIG;
            this.mTrackingModel.addUnreachableNetwork(this.mPriorityRule.network_code);
        } else if (TimeoutException.class.isAssignableFrom(exception.getClass())) {
            crashModel.error = PubnativeInsightCrashModel.ERROR_TIMEOUT;
            this.mTrackingModel.addUnreachableNetwork(this.mPriorityRule.network_code);
        } else {
            crashModel.error = PubnativeInsightCrashModel.ERROR_ADAPTER;
            this.mTrackingModel.addAttemptedNetwork(this.mPriorityRule.network_code);
        }
        long responseTime = System.currentTimeMillis() - this.mRequestStartTimestamp;
        this.mTrackingModel.addNetwork(this.mPriorityRule, responseTime, crashModel);
        Map<String, String> extras    = adapter.getExtras();
        String              requestID = extras.get(TRACKING_PARAMETER_REQUEST_ID);
        this.doNextNetworkRequest(requestID);
    }
}
