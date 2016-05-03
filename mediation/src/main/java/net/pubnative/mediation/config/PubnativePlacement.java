package net.pubnative.mediation.config;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.AdvertisingIdClient;
import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.config.model.PubnativeDeliveryRuleModel;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.config.model.PubnativePlacementModel;
import net.pubnative.mediation.config.model.PubnativePriorityRuleModel;
import net.pubnative.mediation.exceptions.PubnativeException;
import net.pubnative.mediation.insights.PubnativeInsightsManager;
import net.pubnative.mediation.insights.model.PubnativeInsightCrashModel;
import net.pubnative.mediation.insights.model.PubnativeInsightDataModel;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PubnativePlacement implements PubnativeConfigManager.Listener {

    private static final String TAG                           = PubnativePlacement.class.getSimpleName();
    private static final String TRACKING_PARAMETER_APP_TOKEN  = "app_token";
    private static final String TRACKING_PARAMETER_REQUEST_ID = "reqid";
    protected Context                   mContext;
    protected Listener                  mListener;
    protected String                    mAppToken;
    protected String                    mRequestID;
    protected String                    mPlacementName;
    protected PubnativePlacementModel   mPlacementModel;
    protected PubnativeConfigModel      mConfigModel;
    protected PubnativeInsightDataModel mInsightModel;
    protected Map<String, String>       mInsightParameters;
    protected int                       mCurrentNetworkIndex;

    /**
     * Interface for placement callbacks
     */
    public interface Listener {

        /**
         * Called when the placement was loaded
         *
         * @param placement placement that finished loading
         */
        void onPubnativePlacementReady(PubnativePlacement placement);

        /**
         * Called when the placement load failed
         *
         * @param placement placement that failed loading
         */
        void onPubnativePlacementLoadFail(PubnativePlacement placement, Exception exception);
    }

    /**
     * Loads the basic data for the current placement
     *
     * @param context       valid context
     * @param appToken      app token string
     * @param placementName placement name string
     * @param listener      valid listener to callback when the placement is ready
     */
    public void load(Context context, String appToken, String placementName, final Listener listener) {

        Log.v(TAG, "load");
        if (listener == null) {
            Log.e(TAG, "load", new IllegalArgumentException("listener cannot be null, dropping this call"));
        } else {
            mListener = listener;
            if (context == null ||
                TextUtils.isEmpty(appToken) ||
                TextUtils.isEmpty(placementName)) {
                invokeOnLoadFail(PubnativeException.PLACEMENT_PARAMETERS_INVALID);
            } else if (mConfigModel != null) {
                invokeOnLoadFail(new Exception("load - Error: placement is loaded"));
            } else {
                mContext = context;
                mAppToken = appToken;
                mPlacementName = placementName;
                mCurrentNetworkIndex = -1;
                mInsightModel = new PubnativeInsightDataModel();
                mRequestID = UUID.randomUUID().toString();
                PubnativeConfigManager.getConfig(mContext, mAppToken, this);
            }
        }
    }

    /**
     * Returns this placement tracking UUID, this will be unique
     * for each instance of PubnativePlacement objects
     *
     * @return String representation of the UUID
     */
    public String getTrackingUUID() {

        Log.v(TAG, "getUUID");
        return mRequestID;
    }

    /**
     * Checks if the current placement is disabled
     *
     * @return true if the placement is disabled, false if it's enabled
     */
    public boolean isDisabled() {

        Log.v(TAG, "isDisabled");
        boolean result = true;
        if (mPlacementModel != null) {
            PubnativeDeliveryRuleModel deliveryRuleModel = mPlacementModel.delivery_rule;
            result = deliveryRuleModel.isDisabled();
        }
        return result;
    }

    /**
     * Checks if the frequency cap of this placement is reached
     *
     * @return true if the frequency cap is active, false if it's not
     */
    public boolean isFrequencyCapActive() {

        Log.v(TAG, "isFrequencyCapEnabled");
        boolean result = false;
        if (mPlacementModel != null) {
            PubnativeDeliveryRuleModel deliveryRuleModel = mPlacementModel.delivery_rule;
            result = deliveryRuleModel.isFrequencyCapReached(mContext, mPlacementName);
        }
        return result;
    }

    /**
     * Checks if the pacing cap of this placement is reached
     *
     * @return true if the pacing cap is active, false if it's not
     */
    public boolean isPacingCapActive() {

        Log.v(TAG, "isPacingCapEnabled");
        boolean result = false;
        if (mPlacementModel != null) {
            PubnativeDeliveryRuleModel deliveryRuleModel = mPlacementModel.delivery_rule;
            Calendar overdueCalendar = deliveryRuleModel.getPacingOverdueCalendar();
            Calendar pacingCalendar = PubnativeDeliveryManager.getPacingCalendar(mPlacementName);
            if (overdueCalendar == null || pacingCalendar == null || pacingCalendar.before(overdueCalendar)) {
                // Pacing cap reset or deactivated or not reached
                result = false;
            } else {
                // Pacing cap active and limit reached
                result = true;
            }
        }
        return result;
    }

    /**
     * Gets this placement ad format code
     *
     * @return valid string if loaded, null if not
     */
    public String getAdFormatCode() {

        Log.v(TAG, "getAdFormatCode");
        String result = null;
        if (mPlacementModel != null) {
            result = mPlacementModel.ad_format_code;
        }
        return result;
    }

    /**
     * Gets the current delivery rule model
     *
     * @return valid PubnativeDeliveryRuleModel if loaded, null if not
     */
    public PubnativeDeliveryRuleModel getDeliveryRule() {

        Log.v(TAG, "getDeliveryRule");
        PubnativeDeliveryRuleModel result = null;
        if (mPlacementModel != null) {
            result = mPlacementModel.delivery_rule;
        }
        return result;
    }

    /**
     * Gest the loaded config model
     *
     * @return loaded PubnativeConfigModel object
     */
    public PubnativeConfigModel getConfig() {

        Log.v(TAG, "getConfig");
        return mConfigModel;
    }

    /**
     * Gets this placement name
     *
     * @return valid placement name string
     */
    public String getName() {

        Log.v(TAG, "getName");
        return mPlacementName;
    }

    /**
     * Gets tis placement app token
     *
     * @return valid app token string
     */
    public String getAppToken() {

        Log.v(TAG, "getAppToken");
        return mAppToken;
    }

    /**
     * Gets the current priority model
     *
     * @return valid PubnativePriorityRuleModel, null if there are no more
     */
    public PubnativePriorityRuleModel currentPriority() {

        Log.v(TAG, "currentPriority");
        PubnativePriorityRuleModel result = null;
        if (mPlacementName != null) {
            result = mPlacementModel.getPriorityRule(mCurrentNetworkIndex);
        }
        return result;
    }

    /**
     * Gets the current network model
     *
     * @return valid PubnativeNetworkModel, null if there are no more
     */
    public PubnativeNetworkModel currentNetwork() {

        Log.v(TAG, "currentNetwork");
        PubnativeNetworkModel result = null;
        PubnativePriorityRuleModel rule = currentPriority();
        if (rule != null) {
            result = mConfigModel.getNetwork(rule.network_code);
        }
        return result;
    }

    /**
     * Waterfalls to the next network
     */
    public void next() {

        Log.v(TAG, "next");
        mCurrentNetworkIndex++;
    }

    public void setCreativeURL(String creativeURL) {

        Log.v(TAG, "setCreativeURL");
        if (mInsightModel != null) {
            mInsightModel.creative_url = creativeURL;
        }
    }
    //==============================================================================================
    // Tracking
    //==============================================================================================

    /**
     * Sets the current network as unreachable due to the passed exception
     *
     * @param exception exception with the details of the unreachability
     */
    public void trackUnreachableNetwork(long responseTime, Exception exception) {

        Log.v(TAG, "trackUnreachableNetwork", exception);
        PubnativePriorityRuleModel priorityRuleModel = currentPriority();
        PubnativeInsightCrashModel crashModel = new PubnativeInsightCrashModel();
        crashModel.error = exception.getMessage();
        crashModel.details = exception.toString();
        mInsightModel.addUnreachableNetwork(priorityRuleModel.network_code);
        mInsightModel.addNetwork(priorityRuleModel, responseTime, crashModel);
    }

    /**
     * Sets the current network as attempted but failed
     *
     * @param exception exception with the details
     */
    public void trackAttemptedNetwork(long responseTime, Exception exception) {

        Log.v(TAG, "trackAttemptedNetwork", exception);
        PubnativePriorityRuleModel priorityRuleModel = currentPriority();
        PubnativeInsightCrashModel crashModel = new PubnativeInsightCrashModel();
        crashModel.error = exception.getMessage();
        crashModel.details = exception.toString();
        mInsightModel.addAttemptedNetwork(priorityRuleModel.network_code);
        mInsightModel.addNetwork(priorityRuleModel, responseTime, crashModel);
    }

    /**
     * Sets the current network as succeded
     */
    public void trackSuccededNetwork(long responseTime) {

        Log.v(TAG, "trackSuccededNetwork");
        PubnativePriorityRuleModel priorityRuleModel = currentPriority();
        mInsightModel.network = priorityRuleModel.network_code;
        mInsightModel.addNetwork(priorityRuleModel, responseTime, null);
        PubnativeDeliveryManager.updatePacingCalendar(mPlacementName);
    }

    /**
     * Sends request insight data
     */
    public void sendRequestInsight() {

        Log.v(TAG, "sendRequestInsight");
        String baseURL = (String) mConfigModel.getGlobal(PubnativeConfigModel.GLOBAL.REQUEST_BEACON);
        PubnativeInsightsManager.trackData(mContext, baseURL, mInsightParameters, mInsightModel);
    }

    /**
     * Sends impression insight data
     */
    public void sendImpressionInsight() {

        Log.v(TAG, "sendImpressionInsight");
        // TODO: Send impression insight
        PubnativeDeliveryManager.logImpression(mContext, mPlacementName);
        String baseURL = (String) mConfigModel.getGlobal(PubnativeConfigModel.GLOBAL.IMPRESSION_BEACON);
        PubnativeInsightsManager.trackData(mContext, baseURL, mInsightParameters, mInsightModel);
    }

    /**
     * Sends a request insight data
     */
    public void sendClickInsight() {

        Log.v(TAG, "sendClickInsight");
        // TODO: Send click insight
        String baseURL = (String) mConfigModel.getGlobal(PubnativeConfigModel.GLOBAL.CLICK_BEACON);
        PubnativeInsightsManager.trackData(mContext, baseURL, mInsightParameters, mInsightModel);
    }

    //==============================================================================================
    // Private methods
    //==============================================================================================\
    protected void configurePlacement() {

        PubnativePlacementModel placement = mConfigModel.getPlacement(mPlacementName);
        if (placement == null) {
            invokeOnLoadFail(PubnativeException.PLACEMENT_NOT_FOUND);
        } else if (placement.delivery_rule == null || placement.priority_rules == null) {
            invokeOnLoadFail(PubnativeException.PLACEMENT_EMPTY);
        } else if (placement.delivery_rule.isDisabled()) {
            invokeOnLoadFail(PubnativeException.PLACEMENT_DISABLED);
        } else {
            mPlacementModel = placement;
            startTracking();
        }
    }

    protected void startTracking() {

        mInsightModel = new PubnativeInsightDataModel();
        mInsightModel.reset();
        mInsightModel.fillDefaults(mContext);
        mInsightModel.placement_name = mPlacementName;
        mInsightModel.delivery_segment_ids = mPlacementModel.delivery_rule.segment_ids;
        mInsightModel.ad_format_code = mPlacementModel.ad_format_code;
        // Gather needed data
        Log.v(TAG, "getTrackingParameters");
        mInsightParameters = new HashMap<String, String>();
        mInsightParameters.put(TRACKING_PARAMETER_APP_TOKEN, mAppToken);
        mInsightParameters.put(TRACKING_PARAMETER_REQUEST_ID, mRequestID);
        if (mConfigModel.request_params != null) {
            for (String key : mConfigModel.request_params.keySet()) {
                String value = mConfigModel.request_params.get(key);
                mInsightParameters.put(key, value);
            }
        }
        AdvertisingIdClient.getAdvertisingId(mContext, new AdvertisingIdClient.Listener() {

            @Override
            public void onAdvertisingIdClientFinish(AdvertisingIdClient.AdInfo adInfo) {

                if (adInfo != null && !adInfo.isLimitAdTrackingEnabled()) {
                    mInsightModel.user_uid = adInfo.getId();
                }
                invokeOnReady();
            }

            @Override
            public void onAdvertisingIdClientFail(Exception exception) {

                invokeOnReady();
            }
        });
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeOnReady() {

        Log.v(TAG, "invokeOnReady");
        if (mListener != null) {
            mListener.onPubnativePlacementReady(this);
        }
    }

    protected void invokeOnLoadFail(Exception exception) {

        Log.v(TAG, "invokeOnLoadFail", exception);
        if (mListener != null) {
            mListener.onPubnativePlacementLoadFail(this, exception);
        }
    }

    //==============================================================================================
    // Callbacks
    //==============================================================================================
    // PubnativeConfigManager.Listener
    //----------------------------------------------------------------------------------------------
    @Override
    public void onConfigLoaded(PubnativeConfigModel config) {

        if (config == null ||
            config.isNullOrEmpty()) {
            invokeOnLoadFail(PubnativeException.PLACEMENT_CONFIG_INVALID);
        } else {
            mConfigModel = config;
            configurePlacement();
        }
    }
}
