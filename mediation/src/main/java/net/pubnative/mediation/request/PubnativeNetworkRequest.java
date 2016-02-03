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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class PubnativeNetworkRequest implements PubnativeNetworkAdapterListener,
                                                PubnativeConfigRequestListener {

    private static final String TRACKING_PARAMETER_APP_TOKEN  = "app_token";
    private static final String TRACKING_PARAMETER_REQUEST_ID = "reqid";

    protected Context                         context;
    protected PubnativeNetworkRequestListener listener;
    protected PubnativeConfigModel            config;
    protected PubnativePlacementModel         placement;
    protected PubnativeAdModel                ad;
    protected PubnativeInsightDataModel       trackingModel;
    protected String                          appToken;
    protected String                          placementID;
    protected PubnativePriorityRuleModel      currentPriorityRule;
    protected int                             currentNetworkIndex;
    protected long                            adapterRequestStart;

    public PubnativeNetworkRequest() {

        this.trackingModel = new PubnativeInsightDataModel();
    }

    // TRACKING INFO
    public void setAge(int age) {

        this.trackingModel.age = age;
    }

    public void setEducation(String education) {

        this.trackingModel.education = education;
    }

    public void addInterest(String interest) {

        this.trackingModel.addInterest(interest);
    }

    public enum Gender {
        MALE,
        FEMALE
    }

    public void setGender(Gender gender) {

        this.trackingModel.gender = gender.name().toLowerCase();
    }

    public void setInAppPurchasesEnabled(boolean iap) {

        this.trackingModel.iap = iap;
    }

    public void setInAppPurchasesTotal(float iapTotal) {

        this.trackingModel.iap_total = iapTotal;
    }

    // REQUEST

    /**
     * Starts a new ad request.
     *
     * @param context     valid Context object.
     * @param appToken    valid appToken provided by Pubnative.
     * @param placementID valid placementId provided by Pubnative.
     * @param listener    valid listener to keep track of request callbacks.
     */
    public void start(Context context, String appToken, String placementID, PubnativeNetworkRequestListener listener) {

        this.appToken = appToken;
        this.placementID = placementID;
        this.context = context;
        this.currentNetworkIndex = 0;

        if (listener == null) {

            // Just drop the call
            System.out.println("PubnativeNetworkRequest.start - listener not specified, dropping the call");
            return;
        }

        this.listener = listener;

        this.invokeStart();

        if (this.context == null || TextUtils.isEmpty(appToken) || TextUtils.isEmpty(placementID)) {

            this.invokeFail(new IllegalArgumentException("PubnativeNetworkRequest.start - invalid start parameters"));

        } else {

            this.getConfig(context, appToken, this);
        }
    }

    protected void getConfig(Context context, String appToken, PubnativeConfigRequestListener listener) {
        // This method getConfig() here gets the stored/downloaded config and
        // continues to startRequest() in it's callback "onConfigLoaded()".
        PubnativeConfigManager.getConfig(context, appToken, listener);
    }

    @Override
    public void onConfigLoaded(PubnativeConfigModel configModel) {

        this.startRequest(configModel);
    }

    private void startRequest(PubnativeConfigModel configModel) {

        this.config = configModel;

        if (this.config == null || this.config.isNullOrEmpty()) {

            this.invokeFail(new NetworkErrorException("PubnativeNetworkRequest.start - invalid config retrieved"));

        } else {

            if (this.config.placements.containsKey(placementID)) {

                this.placement = this.config.placements.get(placementID);

                if (this.placement != null && this.placement.delivery_rule != null) {

                    if (this.placement.delivery_rule.isActive()) {

                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... voids) {

                                PubnativeNetworkRequest.this.trackingModel.reset();
                                PubnativeNetworkRequest.this.trackingModel.fillDefaults(
                                        PubnativeNetworkRequest.this.context);
                                PubnativeNetworkRequest.this.trackingModel.placement_name = placementID;
                                PubnativeNetworkRequest.this.trackingModel.delivery_segment_ids = PubnativeNetworkRequest.this.placement.delivery_rule.segment_ids;

                                String adFormatCode = PubnativeNetworkRequest.this.placement.ad_format_code;
                                PubnativeNetworkRequest.this.trackingModel.ad_format_code = adFormatCode;
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

                } else {

                    this.invokeFail(new Exception("PubnativeNetworkRequest.start - config error"));
                }

            } else {

                this.invokeFail(new IllegalArgumentException("PubnativeNetworkRequest.start - placement_id not found"));
            }
        }
    }

    protected void startRequest() {

        PubnativeDeliveryRuleModel deliveryRuleModel = this.placement.delivery_rule;

        if (deliveryRuleModel.isFrequencyCapReached(context, this.placementID)) {

            this.invokeFail(new Exception("Pubnative - start error: (frequecy_cap) too many ads"));

        } else {

            Calendar overdueCalendar = deliveryRuleModel.getPacingOverdueCalendar();
            Calendar pacingCalendar = PubnativeDeliveryManager.getPacingCalendar(this.placementID);

            String requestID = UUID.randomUUID().toString();
            if (overdueCalendar == null || pacingCalendar == null || pacingCalendar.before(overdueCalendar)) {

                // Pacing cap reset or deactivated or not reached
                this.doNextNetworkRequest(requestID);

            } else {

                // Pacing cap active and limit reached
                // return the same ad during the pacing cap amount of time
                if (this.ad == null) {

                    this.invokeFail(new Exception("Pubnative - start error: (pacing_cap) too many ads"));

                } else {

                    this.invokeLoad(this.ad, requestID);
                }
            }
        }
    }

    protected void doNextNetworkRequest(String requestID) {

        if (this.placement.priority_rules != null && this.placement.priority_rules.size() > this.currentNetworkIndex) {

            this.currentPriorityRule = this.placement.priority_rules.get(this.currentNetworkIndex);
            this.currentNetworkIndex++;
            String currentNetworkID = currentPriorityRule.network_code;

            if (!TextUtils.isEmpty(currentNetworkID) && this.config.networks.containsKey(currentNetworkID)) {

                PubnativeNetworkModel networkModel = this.config.networks.get(currentNetworkID);
                PubnativeNetworkAdapter adapter = PubnativeNetworkAdapterFactory.createAdapter(networkModel);

                if (adapter == null) {

                    this.trackingModel.addAttemptedNetwork(this.currentPriorityRule.network_code);
                    this.trackNetwork(this.currentPriorityRule, 0, PubnativeInsightCrashModel.ERROR_CONFIG, "creation error");
                    this.doNextNetworkRequest(requestID);

                } else {

                    // Add ML extras for adapter
                    Map extras = new HashMap();
                    extras.put(TRACKING_PARAMETER_REQUEST_ID, requestID.toString());
                    adapter.doRequest(this.context, networkModel.timeout, extras, this);
                }

            } else {

                this.trackingModel.addAttemptedNetwork(this.currentPriorityRule.network_code);
                this.trackNetwork(this.currentPriorityRule, 0, PubnativeInsightCrashModel.ERROR_CONFIG, "id not found");
                this.doNextNetworkRequest(requestID);
            }

        } else {

            this.trackRequestInsight(requestID);
            this.invokeFail(new Exception("Pubnative - no fill"));
        }
    }

    // HELPERS
    protected void trackNetwork(PubnativePriorityRuleModel priorityRuleModel, long responsetime, String error, String details) {

        PubnativeInsightCrashModel crashModel = null;
        PubnativeNetworkModel networkModel = this.config.networks.get(priorityRuleModel.network_code);

        if (networkModel.crash_report) {

            crashModel = new PubnativeInsightCrashModel();
            crashModel.error = error;
            crashModel.details = details;
        }

        this.trackingModel.addNetwork(priorityRuleModel, responsetime, crashModel);
    }

    protected void invokeStart() {

        if (this.listener != null) {

            this.listener.onRequestStarted(this);
        }
    }

    protected void invokeLoad(final PubnativeAdModel ad, String requestID) {

        this.trackRequestInsight(requestID);

        if (this.listener != null) {

            this.listener.onRequestLoaded(this, ad);
        }
    }

    protected void invokeFail(final Exception exception) {

        if (this.listener != null) {

            this.listener.onRequestFailed(this, exception);
        }
    }

    protected void trackRequestInsight(String requestID) {

        if (this.config != null && ((this.trackingModel.attempted_networks != null && this.trackingModel.attempted_networks.size() > 0) || this.trackingModel.network != null)) {

            String requestURL = (String) this.config.globals.get(PubnativeConfigModel.ConfigContract.REQUEST_BEACON);

            if (!TextUtils.isEmpty(requestURL)) {

                PubnativeInsightsManager.trackData(this.context, requestURL, getTrackingParameters(requestID), this.trackingModel);
            }
        }
    }

    protected Map<String, String> getTrackingParameters(String requestID) {

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(TRACKING_PARAMETER_APP_TOKEN, this.appToken);
        parameters.put(TRACKING_PARAMETER_REQUEST_ID, requestID);
        return parameters;
    }

    // CALLBACKS
    // PubnativeNetworkAdapterListener

    @Override
    public void onAdapterRequestStarted(PubnativeNetworkAdapter adapter) {

        this.adapterRequestStart = System.currentTimeMillis();
    }

    @Override
    public void onAdapterRequestLoaded(PubnativeNetworkAdapter adapter, PubnativeAdModel ad) {

        long responseTime = System.currentTimeMillis() - this.adapterRequestStart;
        Map<String, String> extras = adapter.getExtras();
        String requestID = extras.get(TRACKING_PARAMETER_REQUEST_ID);

        if (ad == null) {

            this.trackingModel.addAttemptedNetwork(this.currentPriorityRule.network_code);
            this.trackNetwork(this.currentPriorityRule, responseTime, PubnativeInsightCrashModel.ERROR_NO_FILL, "");
            this.doNextNetworkRequest(requestID);

        } else {

            this.ad = ad;

            this.trackingModel.network = this.currentPriorityRule.network_code;
            this.trackingModel.addNetwork(this.currentPriorityRule, responseTime, null);

            // Default tracking data
            this.ad.setTrackingInfo(this.trackingModel);

            // Impression tracking data
            String impressionURL = null;
            if (this.config.globals.containsKey(PubnativeConfigModel.ConfigContract.IMPRESSION_BEACON)) {

                impressionURL = (String) this.config.globals.get(PubnativeConfigModel.ConfigContract.IMPRESSION_BEACON);
            }
            this.ad.setTrackingImpressionData(impressionURL, getTrackingParameters(requestID));

            // Click tracking data
            String clickURL = null;
            if (this.config.globals.containsKey(PubnativeConfigModel.ConfigContract.CLICK_BEACON)) {

                clickURL = (String) this.config.globals.get(PubnativeConfigModel.ConfigContract.CLICK_BEACON);
            }
            this.ad.setTrackingClickData(clickURL, getTrackingParameters(requestID));

            PubnativeDeliveryManager.updatePacingCalendar(this.trackingModel.placement_name);
            this.invokeLoad(ad, requestID);
        }
    }

    @Override
    public void onAdapterRequestFailed(PubnativeNetworkAdapter adapter, Exception exception) {

        System.out.println("Pubnative - adapter error: " + exception);
        // Waterfall to the next network



        PubnativeInsightCrashModel crashModel = new PubnativeInsightCrashModel();
        crashModel.details = exception.toString();
        crashModel.error = PubnativeInsightCrashModel.ERROR_ADAPTER;

        if (IllegalArgumentException.class.isAssignableFrom(exception.getClass())) {

            crashModel.error = PubnativeInsightCrashModel.ERROR_CONFIG;

        } else if (TimeoutException.class.isAssignableFrom(exception.getClass())) {

            crashModel.error = PubnativeInsightCrashModel.ERROR_TIMEOUT;
        }

        long responseTime = System.currentTimeMillis() - this.adapterRequestStart;
        this.trackingModel.addAttemptedNetwork(this.currentPriorityRule.network_code);
        this.trackingModel.addNetwork(this.currentPriorityRule, responseTime, crashModel);

        Map<String, String> extras = adapter.getExtras();
        String requestID = extras.get(TRACKING_PARAMETER_REQUEST_ID);
        this.doNextNetworkRequest(requestID);
    }
}
