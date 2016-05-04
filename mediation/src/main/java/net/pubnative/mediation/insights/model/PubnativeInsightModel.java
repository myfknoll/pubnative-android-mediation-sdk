package net.pubnative.mediation.insights.model;

import android.content.Context;
import android.util.Log;

import net.pubnative.mediation.config.PubnativeDeliveryManager;
import net.pubnative.mediation.config.model.PubnativePriorityRuleModel;
import net.pubnative.mediation.insights.PubnativeInsightsManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by davidmartin on 04/05/16.
 */
public class PubnativeInsightModel {

    private static final String TAG = PubnativeInsightModel.class.getSimpleName();
    protected String                    mRequestInsightURL;
    protected String                    mImpressionInsightURL;
    protected String                    mClickInsightURL;
    protected Context                   mContext;
    protected PubnativeInsightDataModel mData;
    protected Map<String, String>       mExtras;

    public interface Listener {

        void onInsightLoaded();
    }

    public PubnativeInsightModel(Context context) {

        mContext = context;
        mData = new PubnativeInsightDataModel();
        mData.fillDefaults(mContext);
    }

    /**
     * Sets the placement name for this insight
     *
     * @param placement valid placement name
     */
    public void setPlacement(String placement) {

        mData.placement_name = placement;
    }

    /**
     * Sets the segments list
     *
     * @param deliverySegments valid segments list
     */
    public void setSegments(List<Integer> deliverySegments) {

        mData.delivery_segment_ids = deliverySegments;
    }

    public void setAdFormatCode(String adFormatCode) {

        mData.ad_format_code = adFormatCode;
    }

    public void setUserId(String uid) {

        mData.user_uid = uid;
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
        mData.age = age;
    }

    /**
     * Sets education for the ad request
     *
     * @param education education of the target as string
     */
    public void setEducation(String education) {

        Log.v(TAG, "setEducation: " + education);
        mData.education = education;
    }

    /**
     * Adds an interest keyword for the request
     *
     * @param interest interest keyword of the target
     */
    public void addInterest(String interest) {

        Log.v(TAG, "addInterest: " + interest);
        mData.addInterest(interest);
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
        mData.gender = gender.name().toLowerCase();
    }

    /**
     * Sets a value for the request to tell if the inapp purchased are enabled
     *
     * @param iap true if in app purchased are enabled, false if not
     */
    public void setInAppPurchasesEnabled(boolean iap) {

        Log.v(TAG, "setInAppPurchasesEnabled: " + iap);
        mData.iap = iap;
    }

    /**
     * Sets the total amount spent by this client in in app purchased
     *
     * @param iapTotal total amount spent as float
     */
    public void setInAppPurchasesTotal(float iapTotal) {

        Log.v(TAG, "setInAppPurchasesTotal: " + iapTotal);
        mData.iap_total = iapTotal;
    }

    /**
     * Gets the ad format code
     *
     * @return valid ad format code string
     */
    public String getAdFormat() {

        return mData.ad_format_code;
    }

    /**
     * Sets the creative url of the data
     *
     * @param url valid url string for the creative
     */
    public void setCreativeUrl(String url) {

        Log.v(TAG, "setCreativeUrl");
        mData.creative_url = url;
    }

    /**
     * Adds extra fields to be added in the insight query string
     *
     * @param key   key string
     * @param value value string
     */
    public void addExtra(String key, String value) {

        Log.v(TAG, "addExtra");
        if (mExtras == null) {
            mExtras = new HashMap<String, String>();
        }
        mExtras.put(key, value);
    }

    /**
     * This will set up the insight urls to use in the track process
     *
     * @param requestUrl    valid request url string
     * @param impressionUrl valid impression url string
     * @param clickUrl      valid click url string
     */
    public void setInsightURLs(String requestUrl, String impressionUrl, String clickUrl) {

        mRequestInsightURL = requestUrl;
        mImpressionInsightURL = impressionUrl;
        mClickInsightURL = clickUrl;
    }
    //==============================================================================================
    // Tracking
    //==============================================================================================

    /**
     * Sets the current network as unreachable due to the passed exception
     *
     * @param exception exception with the details of the unreachability
     */
    public void trackUnreachableNetwork(PubnativePriorityRuleModel priorityRuleModel, long responseTime, Exception exception) {

        Log.v(TAG, "trackUnreachableNetwork", exception);
        PubnativeInsightCrashModel crashModel = new PubnativeInsightCrashModel();
        crashModel.error = exception.getMessage();
        crashModel.details = exception.toString();
        mData.addUnreachableNetwork(priorityRuleModel.network_code);
        mData.addNetwork(priorityRuleModel, responseTime, crashModel);
    }

    /**
     * Sets the current network as attempted but failed
     *
     * @param exception exception with the details
     */
    public void trackAttemptedNetwork(PubnativePriorityRuleModel priorityRuleModel, long responseTime, Exception exception) {

        Log.v(TAG, "trackAttemptedNetwork", exception);
        PubnativeInsightCrashModel crashModel = new PubnativeInsightCrashModel();
        crashModel.error = exception.getMessage();
        crashModel.details = exception.toString();
        mData.addAttemptedNetwork(priorityRuleModel.network_code);
        mData.addNetwork(priorityRuleModel, responseTime, crashModel);
    }

    /**
     * Sets the current network as succeded
     */
    public void trackSuccededNetwork(PubnativePriorityRuleModel priorityRuleModel, long responseTime) {

        Log.v(TAG, "trackSuccededNetwork");
        mData.network = priorityRuleModel.network_code;
        mData.addNetwork(priorityRuleModel, responseTime, null);
        PubnativeDeliveryManager.updatePacingCalendar(mData.placement_name);
    }

    /**
     * Sends request insight data
     */
    public void sendRequestInsight() {

        Log.v(TAG, "sendRequestInsight");
        PubnativeInsightsManager.trackData(mContext, mRequestInsightURL, mExtras, mData);
    }

    /**
     * Sends impression insight data
     */
    public void sendImpressionInsight() {

        Log.v(TAG, "sendImpressionInsight");
        PubnativeDeliveryManager.logImpression(mContext, mData.placement_name);
        PubnativeInsightsManager.trackData(mContext, mImpressionInsightURL, mExtras, mData);
    }

    /**
     * Sends a request insight data
     */
    public void sendClickInsight() {

        Log.v(TAG, "sendClickInsight");
        PubnativeInsightsManager.trackData(mContext, mClickInsightURL, mExtras, mData);
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    public void invokeOnLoaded(Listener listener) {

        Log.v(TAG, "invokeOnLoaded");
        if (listener == null) {
            listener.onInsightLoaded();
        }
    }
}
