package net.pubnative.mediation.config;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.config.model.PubnativeDeliveryRuleModel;
import net.pubnative.mediation.config.model.PubnativeNetworkModel;
import net.pubnative.mediation.config.model.PubnativePlacementModel;
import net.pubnative.mediation.config.model.PubnativePriorityRuleModel;
import net.pubnative.mediation.exceptions.PubnativeException;

public class PubnativePlacement {

    private static final String TAG = PubnativePlacement.class.getSimpleName();
    protected Context                 mContext;
    protected String                  mAppToken;
    protected String                  mPlacementName;
    protected PubnativePlacementModel mPlacementModel;
    protected PubnativeConfigModel    mConfigModel;
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
            if (context == null) {
                invokeOnLoadFail(listener, PubnativeException.REQUEST_PARAMETERS_INVALID);
            } else if (TextUtils.isEmpty(appToken)) {
                invokeOnLoadFail(listener, PubnativeException.REQUEST_PARAMETERS_INVALID);
            } else if (TextUtils.isEmpty(placementName)) {
                invokeOnLoadFail(listener, PubnativeException.REQUEST_PARAMETERS_INVALID);
            } else if (mConfigModel != null) {
                invokeOnLoadFail(listener, new Exception("load - Error: placement is loaded"));
            } else {
                mContext = context;
                mAppToken = appToken;
                mPlacementName = placementName;
                mCurrentNetworkIndex = -1;
                PubnativeConfigManager.getConfig(mContext, mAppToken, new PubnativeConfigManager.Listener() {

                    @Override
                    public void onConfigLoaded(PubnativeConfigModel config) {

                        if (config == null) {
                            invokeOnLoadFail(listener, PubnativeException.REQUEST_CONFIG_INVALID);
                        } else if (config.isNullOrEmpty()) {
                            invokeOnLoadFail(listener, PubnativeException.REQUEST_CONFIG_INVALID);
                        } else {
                            mConfigModel = config;
                            PubnativePlacementModel placement = config.getPlacement(mPlacementName);
                            if (placement == null) {
                                invokeOnLoadFail(listener, PubnativeException.REQUEST_PLACEMENT_NOT_FOUND);
                            } else if (placement.delivery_rule == null || placement.priority_rules == null) {
                                invokeOnLoadFail(listener, PubnativeException.REQUEST_PLACEMENT_EMPTY);
                            } else if (placement.delivery_rule.isDisabled()) {
                                invokeOnLoadFail(listener, PubnativeException.REQUEST_PLACEMENT_DISABLED);
                            } else {
                                mPlacementModel = placement;
                                invokeOnReady(listener);
                            }
                        }
                    }
                });
            }
        }
    }

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
     * @return valid PubnativeDeliveryRuleModel
     */
    public PubnativeDeliveryRuleModel getDeliveryRule() {

        Log.v(TAG, "getDeliveryRule");
        PubnativeDeliveryRuleModel result = null;
        if (mPlacementModel != null) {
            result = mPlacementModel.delivery_rule;
        }
        return result;
    }

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
    public void waterfall() {

        Log.v(TAG, "waterfall");
        mCurrentNetworkIndex++;
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected void invokeOnReady(Listener listener) {

        Log.v(TAG, "invokeOnReady");
        if (listener != null) {
            listener.onPubnativePlacementReady(this);
        }
    }

    protected void invokeOnLoadFail(Listener listener, Exception exception) {

        Log.v(TAG, "invokeOnLoadFail", exception);
        if (listener != null) {
            listener.onPubnativePlacementLoadFail(this, exception);
        }
    }
}
