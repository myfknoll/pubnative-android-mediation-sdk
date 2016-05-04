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
import net.pubnative.mediation.insights.model.PubnativeInsightModel;

import java.util.Calendar;
import java.util.UUID;

public class PubnativePlacement implements PubnativeConfigManager.Listener {

    private static final String TAG                           = PubnativePlacement.class.getSimpleName();
    private static final String TRACKING_PARAMETER_APP_TOKEN  = "app_token";
    private static final String TRACKING_PARAMETER_REQUEST_ID = "reqid";
    protected Context                 mContext;
    protected Listener                mListener;
    protected String                  mAppToken;
    protected String                  mRequestID;
    protected String                  mPlacementName;
    protected PubnativePlacementModel mPlacementModel;
    protected PubnativeConfigModel    mConfigModel;
    protected PubnativeInsightModel   mInsightModel;
    protected int                     mCurrentNetworkIndex;

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
                mRequestID = UUID.randomUUID().toString();
                PubnativeConfigManager.getConfig(mContext, mAppToken, this);
            }
        }
    }

    public PubnativeInsightModel getInsightModel() {

        return mInsightModel;
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
        mInsightModel.setAge(age);
    }

    /**
     * Sets education for the ad request
     *
     * @param education education of the target as string
     */
    public void setEducation(String education) {

        Log.v(TAG, "setEducation: " + education);
        mInsightModel.setEducation(education);
    }

    /**
     * Adds an interest keyword for the request
     *
     * @param interest interest keyword of the target
     */
    public void addInterest(String interest) {

        Log.v(TAG, "addInterest: " + interest);
        mInsightModel.addInterest(interest);
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
        mInsightModel.setGender(gender);mTrackingModel.gender = gender.name().toLowerCase();
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
            mInsightModel = new PubnativeInsightModel(mContext);
            mInsightModel.setPlacement(mPlacementName);
            mInsightModel.setSegments(mPlacementModel.delivery_rule.segment_ids);
            mInsightModel.setAdFormatCode(mPlacementModel.ad_format_code);
            AdvertisingIdClient.getAdvertisingId(mContext, new AdvertisingIdClient.Listener() {

                @Override
                public void onAdvertisingIdClientFinish(AdvertisingIdClient.AdInfo adInfo) {

                    if (adInfo != null && !adInfo.isLimitAdTrackingEnabled()) {
                        mInsightModel.setUserId(adInfo.getId());
                    }
                    startTracking();
                }

                @Override
                public void onAdvertisingIdClientFail(Exception exception) {

                    startTracking();
                }
            });
        }
    }

    protected void startTracking() {

        Log.v(TAG, "startTracking");
        String requestUrl = (String) mConfigModel.getGlobal(PubnativeConfigModel.GLOBAL.REQUEST_BEACON);
        String impressionUrl = (String) mConfigModel.getGlobal(PubnativeConfigModel.GLOBAL.IMPRESSION_BEACON);
        String clickUrl = (String) mConfigModel.getGlobal(PubnativeConfigModel.GLOBAL.CLICK_BEACON);
        mInsightModel.setInsightURLs(requestUrl, impressionUrl, clickUrl);
        mInsightModel.addExtra(TRACKING_PARAMETER_APP_TOKEN, mAppToken);
        mInsightModel.addExtra(TRACKING_PARAMETER_REQUEST_ID, mRequestID);
        if (mConfigModel.request_params != null) {
            for (String key : mConfigModel.request_params.keySet()) {
                String value = mConfigModel.request_params.get(key);
                mInsightModel.addExtra(key, value);
            }
        }
        invokeOnReady();
    }

    //==============================================================================================
    // Private methods
    //==============================================================================================
    /**
     * Sets the current network as unreachable due to the passed exception
     *
     * @param exception exception with the details of the unreachability
     */
    public void trackUnreachableNetwork(long responseTime, Exception exception) {

        Log.v(TAG, "trackUnreachableNetwork", exception);
        mInsightModel.trackUnreachableNetwork(currentPriority(), responseTime, exception);
    }

    /**
     * Sets the current network as attempted but failed
     *
     * @param exception exception with the details
     */
    public void trackAttemptedNetwork(long responseTime, Exception exception) {

        Log.v(TAG, "trackAttemptedNetwork", exception);
        mInsightModel.trackAttemptedNetwork(currentPriority(), responseTime, exception);
    }

    /**
     * Sets the current network as succeded
     */
    public void trackSuccededNetwork(long responseTime) {

        Log.v(TAG, "trackSuccededNetwork");
        mInsightModel.trackSuccededNetwork(currentPriority(), responseTime);
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
