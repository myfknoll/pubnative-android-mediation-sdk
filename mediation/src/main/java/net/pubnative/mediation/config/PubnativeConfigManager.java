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

package net.pubnative.mediation.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import net.pubnative.mediation.config.model.PubnativeConfigAPIResponseModel;
import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.config.model.PubnativeConfigRequestModel;
import net.pubnative.mediation.config.model.PubnativePlacementModel;
import net.pubnative.mediation.insights.model.PubnativeInsightsAPIResponseModel;
import net.pubnative.mediation.network.PubnativeHttpRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PubnativeConfigManager {

    private static         String                            TAG                       = PubnativeConfigManager.class.getSimpleName();
    protected static final String                            SHARED_PREFERENCES_CONFIG = "net.pubnative.mediation";
    protected static final String                            CONFIG_STRING_KEY         = "config";
    protected static final String                            APP_TOKEN_STRING_KEY      = "appToken";
    protected static final String                            TIMESTAMP_LONG_KEY        = "config.timestamp";
    protected static final String                            REFRESH_LONG_KEY          = "refresh";
    protected static final String                            CONFIG_DOWNLOAD_BASE_URL  = "http://ml.pubnative.net/ml/v1/config";
    protected static final String                            APP_TOKEN_KEY             = "app_token";
    protected static       List<PubnativeConfigRequestModel> sQueue                    = null;
    protected static       boolean                           sIdle                     = true;
    //==============================================================================================
    // Listener
    //==============================================================================================

    /**
     * Interface for callbacks when the requested config gets downloaded
     */
    public interface Listener {

        /**
         * Invoked when config manager returns a config.
         *
         * @param configModel PubnativeConfigModel object when cached/download config is available, else null.
         */
        void onConfigLoaded(PubnativeConfigModel configModel);
    }
    //==============================================================================================
    // PubnativeConfigManager
    //==============================================================================================
    // Singleton
    //----------------------------------------------------------------------------------------------

    private PubnativeConfigManager() {
        // do some initialization here may be.
    }
    //----------------------------------------------------------------------------------------------
    // Public
    //----------------------------------------------------------------------------------------------

    /**
     * Gets a config asynchronously with listener callback, downloading a new one when outdated
     *
     * @param context  valid context object
     * @param appToken unique identification key provided by Pubnative for mediation sdk
     * @param extras   valid extras map with parameters for the request
     * @param listener listener to be used for tracking the config loaded callback
     */
    public synchronized static void getConfig(Context context, String appToken, Map extras, PubnativeConfigManager.Listener listener) {

        Log.v(TAG, "getConfig: " + appToken);
        if (listener == null) {
            Log.e(TAG, "getConfig - Error: listener is null, dropping this call");
        } else if (context == null) {
            // ensuring null config is returned
            Log.e(TAG, "getConfig - Error: context is null");
            invokeLoaded(null, listener);
        } else if (TextUtils.isEmpty(appToken)) {
            Log.e(TAG, "getConfig - Error: app token is null");
            invokeLoaded(null, listener);
        } else {
            PubnativeConfigRequestModel item = new PubnativeConfigRequestModel();
            item.context = context;
            item.appToken = appToken;
            item.extras = extras;
            item.listener = listener;
            enqueueRequest(item);
            doNextConfigRequest();
        }
    }

    /**
     * Completely resets all stored config data
     *
     * @param context valid context object
     */
    public static void clean(Context context) {

        Log.v(TAG, "clean");
        setStoredAppToken(context, null);
        setStoredTimestamp(context, null);
        setStoredRefresh(context, null);
        setStoredConfig(context, null);
    }
    //----------------------------------------------------------------------------------------------
    // Private
    //----------------------------------------------------------------------------------------------

    protected static void doNextConfigRequest() {

        Log.v(TAG, "doNextConfigRequest");
        PubnativeConfigRequestModel item = dequeueRequest();
        if (sIdle && item != null) {
            sIdle = false;
            getNextConfig(item);
        }
    }

    private static void getNextConfig(PubnativeConfigRequestModel requestModel) {

        Log.v(TAG, "getNextConfig: " + requestModel.appToken);
        if (configNeedsUpdate(requestModel)) {
            downloadConfig(requestModel);
        } else {
            serveStoredConfig(requestModel);
        }
    }

    protected static void serveStoredConfig(PubnativeConfigRequestModel request) {

        Log.v(TAG, "serveStoredConfig");
        invokeLoaded(getStoredConfig(request.context), request.listener);
    }

    public static PubnativeConfigModel getStoredConfig(Context context) {

        Log.v(TAG, "getStoredConfig");
        PubnativeConfigModel currentConfig = null;
        String configString = getStoredConfigString(context);
        if (!TextUtils.isEmpty(configString)) {
            try {
                currentConfig = new Gson().fromJson(configString, PubnativeConfigModel.class);
            } catch (Exception e) {
                Log.e(TAG, "getStoredConfig - Error: " + e);
            }
        }
        // Ensure not returning an invalid getConfig
        if (currentConfig == null || currentConfig.isEmpty()) {
            currentConfig = null;
        }
        return currentConfig;
    }

    protected static void updateConfig(Context context, String appToken, PubnativeConfigModel configModel) {

        Log.v(TAG, "updateConfig");
        if (context != null) {
            if (TextUtils.isEmpty(appToken) || configModel == null || configModel.isEmpty()) {
                clean(context);
            } else {
                setStoredConfig(context, configModel);
                setStoredAppToken(context, appToken);
                setStoredTimestamp(context, System.currentTimeMillis());
                if (configModel.globals.containsKey(PubnativeConfigModel.GLOBAL.REFRESH)) {
                    Double refresh = (Double) configModel.globals.get(PubnativeConfigModel.GLOBAL.REFRESH);
                    setStoredRefresh(context, refresh.longValue());
                }
            }
        }
    }

    protected synchronized static void downloadConfig(final PubnativeConfigRequestModel requestModel) {

        Log.v(TAG, "downloadConfig");
        PubnativeHttpRequest http = new PubnativeHttpRequest();
        http.start(requestModel.context, getConfigDownloadUrl(requestModel), new PubnativeHttpRequest.Listener() {

            @Override
            public void onPubnativeHttpRequestStart(PubnativeHttpRequest request) {

                Log.v(TAG, "onPubnativeHttpRequestStart");
            }

            @Override
            public void onPubnativeHttpRequestFinish(PubnativeHttpRequest request, String result) {

                Log.v(TAG, "onPubnativeHttpRequestFinish");
                processConfigDownloadResponse(requestModel, result);
                serveStoredConfig(requestModel);
            }

            @Override
            public void onPubnativeHttpRequestFail(PubnativeHttpRequest request, Exception exception) {

                Log.v(TAG, "onPubnativeHttpRequestFail: " + exception.toString());
                serveStoredConfig(requestModel);
            }
        });
    }

    protected static void processConfigDownloadResponse(PubnativeConfigRequestModel request, String result) {

        Log.v(TAG, "processConfigDownloadResponse");
        if (TextUtils.isEmpty(result)) {
            // In case of server error problem, we serve the stored config
            Log.e(TAG, "downloadConfig - Error, empty response");
            serveStoredConfig(request);
        } else {
            try {
                PubnativeConfigAPIResponseModel response = new Gson().fromJson(result, PubnativeConfigAPIResponseModel.class);
                if (PubnativeInsightsAPIResponseModel.Status.OK.equals(response.status)) {
                    // Update delivery manager's tracking data
                    updateDeliveryManagerCache(request.context, response.config);
                    // Saving config string
                    updateConfig(request.context, request.appToken, response.config);
                } else {
                    Log.e(TAG, "downloadConfig - Error: " + response.error_message);
                    serveStoredConfig(request);
                }
            } catch (Exception e) {
                Log.e(TAG, "downloadConfig - Error: " + e);
                serveStoredConfig(request);
            }
        }
    }

    protected static boolean configNeedsUpdate(PubnativeConfigRequestModel request) {

        Log.v(TAG, "configNeedsUpdate");
        boolean result = false;
        String storedConfigString = getStoredConfigString(request.context);
        String storedAppToken = getStoredAppToken(request.context);
        Long refresh = getStoredRefresh(request.context);
        Long storedTimestamp = getStoredTimestamp(request.context);
        Long currentTimestamp = System.currentTimeMillis();
        if (TextUtils.isEmpty(storedConfigString)) {
            // There is no stored config
            result = true;
        } else if (!storedAppToken.equals(request.appToken)) {
            // Stored config is different than the requested app token
            result = true;
        } else if (refresh == null ||
                   storedTimestamp == null) {
            // There is no previous refresh or timestamp stored
            result = true;
        } else if (TimeUnit.MILLISECONDS.toMinutes(currentTimestamp - storedTimestamp) >= refresh) {
            // refresh time was elapsed
            result = true;
        }
        return result;
    }

    private static void updateDeliveryManagerCache(Context context, PubnativeConfigModel downloadedConfig) {

        Log.v(TAG, "updateDeliveryManagerCache");
        PubnativeConfigModel storedConfig = getStoredConfig(context);
        if (storedConfig != null) {
            Set<String> storePlacementIds = storedConfig.placements.keySet();
            for (String placementId : storePlacementIds) {
                // check if new config contains that placement.
                PubnativePlacementModel newPlacement = downloadedConfig.placements.get(placementId);
                PubnativePlacementModel storedPlacement = storedConfig.placements.get(placementId);
                if (newPlacement == null) {
                    PubnativeDeliveryManager.resetHourlyImpressionCount(context, placementId);
                    PubnativeDeliveryManager.resetDailyImpressionCount(context, placementId);
                    PubnativeDeliveryManager.resetPacingCalendar(placementId);
                } else {
                    // Check if impression cap (hour) changed
                    if (storedPlacement.delivery_rule.imp_cap_hour != newPlacement.delivery_rule.imp_cap_hour) {
                        PubnativeDeliveryManager.resetHourlyImpressionCount(context, placementId);
                    }
                    // check if impression cap (day) changed
                    if (storedPlacement.delivery_rule.imp_cap_day != newPlacement.delivery_rule.imp_cap_day) {
                        PubnativeDeliveryManager.resetDailyImpressionCount(context, placementId);
                    }
                    // check if pacing cap changed
                    if (storedPlacement.delivery_rule.pacing_cap_minute != newPlacement.delivery_rule.pacing_cap_minute
                        || storedPlacement.delivery_rule.pacing_cap_hour != newPlacement.delivery_rule.pacing_cap_hour) {
                        PubnativeDeliveryManager.resetPacingCalendar(placementId);
                    }
                }
            }
        }
    }

    //==============================================================================================
    // Callback helpers
    //==============================================================================================
    protected static void invokeLoaded(PubnativeConfigModel configModel, PubnativeConfigManager.Listener listener) {

        Log.v(TAG, "invokeLoaded");
        if (listener != null) {
            listener.onConfigLoaded(configModel);
        }
        sIdle = true;
        doNextConfigRequest();
    }
    //==============================================================================================
    // QUEUE
    //==============================================================================================

    protected static void enqueueRequest(PubnativeConfigRequestModel item) {

        Log.v(TAG, "enqueueRequest");
        if (item != null) {
            if (sQueue == null) {
                sQueue = new ArrayList<PubnativeConfigRequestModel>();
            }
            sQueue.add(item);
        }
    }

    protected static PubnativeConfigRequestModel dequeueRequest() {

        Log.v(TAG, "dequeueRequest");
        PubnativeConfigRequestModel result = null;
        if (sQueue != null && sQueue.size() > 0) {
            result = sQueue.remove(0);
        }
        return result;
    }
    //==============================================================================================
    // SHARED PREFERENCES
    //==============================================================================================
    // CONFIG URL
    //----------------------------------------------------------------------------------------------

    protected static String getConfigDownloadBaseUrl(Context context) {

        Log.v(TAG, "getConfigDownloadBaseUrl");
        String configDownloadBaseUrl = CONFIG_DOWNLOAD_BASE_URL;
        PubnativeConfigModel storedConfig = getStoredConfig(context);
        if (storedConfig != null && !storedConfig.isEmpty()) {
            String configUrl = (String) storedConfig.globals.get(PubnativeConfigModel.GLOBAL.CONFIG_URL);
            if (!TextUtils.isEmpty(configUrl)) {
                configDownloadBaseUrl = configUrl;
            }
        }
        return configDownloadBaseUrl;
    }

    protected static String getConfigDownloadUrl(PubnativeConfigRequestModel request) {

        Uri.Builder uriBuilder = Uri.parse(getConfigDownloadBaseUrl(request.context)).buildUpon();
        uriBuilder.appendQueryParameter(APP_TOKEN_KEY, request.appToken);
        if (request.extras != null) {
            for (String key : request.extras.keySet()) {
                String value = request.extras.get(key);
                uriBuilder.appendQueryParameter(key, value);
            }
        }
        return uriBuilder.build().toString();
    }
    //----------------------------------------------------------------------------------------------
    // CONFIG
    //----------------------------------------------------------------------------------------------

    protected synchronized static String getStoredConfigString(Context context) {

        Log.v(TAG, "getStoredConfigString");
        return getStringSharedPreference(context, CONFIG_STRING_KEY);
    }

    protected synchronized static void setStoredConfig(Context context, PubnativeConfigModel config) {

        Log.v(TAG, "setStoredConfig");
        // ensuring the string "null" is not getting saved.
        String configString = (config != null) ? new Gson().toJson(config) : null;
        configString = "{\"globals\":{\"refresh\":10.0,\"impression_timeout\":1800.0,\"config_url\":\"http://ml.pubnative.net/ml/v1/config\",\"impression_beacon\":\"http://ml.pubnative.net/ml/v1/imp\",\"click_beacon\":\"http://ml.pubnative.net/ml/v1/clk\",\"request_beacon\":\"http://ml.pubnative.net/ml/v1/req\"},\"networks\":{\"facebook#985985301423089_988176314537321\":{\"adapter\":\"FacebookNetworkAdapter\",\"crash_report\":true,\"params\":{\"placement_id\":\"985985301423089_988176314537321\"},\"timeout\":4000},\"pubnative\":{\"adapter\":\"PubnativeLibraryNetworkAdapter\",\"crash_report\":true,\"params\":{\"apptoken\":\"7c26af3aa5f6c0a4ab9f4414787215f3bdd004f80b1b358e72c3137c94f5033c\"},\"timeout\":8000},\"pubnative#1\":{\"adapter\":\"PubnativeLibraryNetworkAdapter\",\"crash_report\":true,\"params\":{\"apptoken\":\"7c26af3aa5f6c0a4ab9f4414787215f3bdd004f80b1b358e72c3137c94f5033c\",\"zoneid\":\"1\"},\"timeout\":8000},\"yahoo#Native_Mediation\":{\"adapter\":\"YahooNetworkAdapter\",\"crash_report\":true,\"params\":{\"ad_space_name\":\"Native_Mediation\",\"api_key\":\"Y4CJH99JHK6ZS35KFCFY\"},\"timeout\":4000},\"yahoo#mediation_interstitial\":{\"adapter\":\"YahooNetworkAdapter\",\"crash_report\":true,\"params\":{\"ad_space_name\":\"mediation_interstitial\",\"api_key\":\"Y4CJH99JHK6ZS35KFCFY\"},\"timeout\":4000},\"inlocomedia#native\":{\"adapter\":\"InLocoMediaNetworkAdapter\",\"crash_report\":true,\"params\":{\"app_id\":\"48278de5099d4832f2f6e048e1b5981675c0c758e17f585b0aec30a11a1126c3\",\"ad_unit_id\":\"322021666fad75102a9d2b75bf25a41a550812c7328115cf8ae4b7ea9a5a6b6e\"},\"timeout\":4000},\"inlocomedia#interstitial\":{\"adapter\":\"InLocoMediaNetworkAdapter\",\"crash_report\":true,\"params\":{\"app_id\":\"48278de5099d4832f2f6e048e1b5981675c0c758e17f585b0aec30a11a1126c3\",\"ad_unit_id\":\"b567ca24e4fb9052847a63d3e67061c90101d802e7e8cc31466f0c1011ab1a8e\"},\"timeout\":10000}},\"placements\":{\"disabled\":{\"ad_format_code\":\"icon\",\"delivery_rule\":{\"imp_cap_day\":0,\"imp_cap_hour\":0,\"no_ads\":true,\"pacing_cap_hour\":0,\"pacing_cap_minute\":0,\"segment_ids\":[]},\"priority_rules\":[]},\"facebook_only\":{\"ad_format_code\":\"icon\",\"delivery_rule\":{\"imp_cap_day\":0,\"imp_cap_hour\":0,\"no_ads\":false,\"pacing_cap_hour\":0,\"pacing_cap_minute\":0,\"segment_ids\":[]},\"priority_rules\":[{\"id\":42,\"network_code\":\"facebook#985985301423089_988176314537321\",\"segment_ids\":[]}]},\"imp_day_cap_10\":{\"ad_format_code\":\"icon\",\"delivery_rule\":{\"imp_cap_day\":10,\"imp_cap_hour\":0,\"no_ads\":false,\"pacing_cap_hour\":0,\"pacing_cap_minute\":0,\"segment_ids\":[]},\"priority_rules\":[{\"id\":39,\"network_code\":\"pubnative\",\"segment_ids\":[]}]},\"imp_hour_cap_10\":{\"ad_format_code\":\"icon\",\"delivery_rule\":{\"imp_cap_day\":0,\"imp_cap_hour\":10,\"no_ads\":false,\"pacing_cap_hour\":0,\"pacing_cap_minute\":0,\"segment_ids\":[]},\"priority_rules\":[{\"id\":37,\"network_code\":\"pubnative\",\"segment_ids\":[]}]},\"pacing_cap_hour_1\":{\"ad_format_code\":\"icon\",\"delivery_rule\":{\"imp_cap_day\":0,\"imp_cap_hour\":0,\"no_ads\":false,\"pacing_cap_hour\":1,\"pacing_cap_minute\":0,\"segment_ids\":[]},\"priority_rules\":[{\"id\":38,\"network_code\":\"pubnative\",\"segment_ids\":[]}]},\"pacing_cap_min_1\":{\"ad_format_code\":\"icon\",\"delivery_rule\":{\"imp_cap_day\":0,\"imp_cap_hour\":0,\"no_ads\":false,\"pacing_cap_hour\":0,\"pacing_cap_minute\":1,\"segment_ids\":[]},\"priority_rules\":[{\"id\":40,\"network_code\":\"pubnative\",\"segment_ids\":[]}]},\"pubnative_only\":{\"ad_format_code\":\"icon\",\"delivery_rule\":{\"imp_cap_day\":0,\"imp_cap_hour\":0,\"no_ads\":false,\"pacing_cap_hour\":0,\"pacing_cap_minute\":0,\"segment_ids\":[]},\"priority_rules\":[{\"id\":33,\"network_code\":\"pubnative\",\"segment_ids\":[]}]},\"waterfall\":{\"ad_format_code\":\"icon\",\"delivery_rule\":{\"imp_cap_day\":0,\"imp_cap_hour\":0,\"no_ads\":false,\"pacing_cap_hour\":0,\"pacing_cap_minute\":0,\"segment_ids\":[]},\"priority_rules\":[{\"id\":43,\"network_code\":\"facebook#985985301423089_988176314537321\",\"segment_ids\":[]},{\"id\":35,\"network_code\":\"yahoo#Native_Mediation\",\"segment_ids\":[]},{\"id\":36,\"network_code\":\"pubnative\",\"segment_ids\":[]}]},\"yahoo_interstitial\":{\"ad_format_code\":\"banner\",\"delivery_rule\":{\"imp_cap_day\":0,\"imp_cap_hour\":0,\"no_ads\":false,\"pacing_cap_hour\":0,\"pacing_cap_minute\":0,\"segment_ids\":[]},\"priority_rules\":[{\"id\":322,\"network_code\":\"yahoo#mediation_interstitial\",\"segment_ids\":[]}]},\"yahoo_only\":{\"ad_format_code\":\"icon\",\"delivery_rule\":{\"imp_cap_day\":0,\"imp_cap_hour\":0,\"no_ads\":false,\"pacing_cap_hour\":0,\"pacing_cap_minute\":0,\"segment_ids\":[]},\"priority_rules\":[{\"id\":21,\"network_code\":\"yahoo#Native_Mediation\",\"segment_ids\":[]}]},\"inlocomedia_only\":{\"ad_format_code\":\"icon\",\"delivery_rule\":{\"imp_cap_day\":0,\"imp_cap_hour\":0,\"no_ads\":false,\"pacing_cap_hour\":0,\"pacing_cap_minute\":0,\"segment_ids\":[]},\"priority_rules\":[{\"id\":21,\"network_code\":\"inlocomedia#native\",\"segment_ids\":[]}]},\"inlocomedia_interstitial\":{\"ad_format_code\":\"icon\",\"delivery_rule\":{\"imp_cap_day\":0,\"imp_cap_hour\":0,\"no_ads\":false,\"pacing_cap_hour\":0,\"pacing_cap_minute\":0,\"segment_ids\":[]},\"priority_rules\":[{\"id\":21,\"network_code\":\"inlocomedia#interstitial\",\"segment_ids\":[]}]}},\"request_params\":{}}";
        setStringSharedPreference(context, CONFIG_STRING_KEY, configString);
    }
    //----------------------------------------------------------------------------------------------
    // APP_TOKEN
    //----------------------------------------------------------------------------------------------

    protected static String getStoredAppToken(Context context) {

        Log.v(TAG, "getStoredAppToken");
        return getStringSharedPreference(context, APP_TOKEN_STRING_KEY);
    }

    protected static void setStoredAppToken(Context context, String appToken) {

        Log.v(TAG, "getStoredAppToken");
        setStringSharedPreference(context, APP_TOKEN_STRING_KEY, appToken);
    }
    //----------------------------------------------------------------------------------------------
    // TIMESTAMP
    //----------------------------------------------------------------------------------------------

    protected static Long getStoredTimestamp(Context context) {

        Log.v(TAG, "getStoredTimestamp");
        return getLongSharedPreference(context, TIMESTAMP_LONG_KEY);
    }

    protected static void setStoredTimestamp(Context context, Long timestamp) {

        Log.v(TAG, "getStoredTimestamp");
        setLongSharedPreference(context, TIMESTAMP_LONG_KEY, timestamp);
    }
    //----------------------------------------------------------------------------------------------
    // REFRESH
    //----------------------------------------------------------------------------------------------

    protected static Long getStoredRefresh(Context context) {

        Log.v(TAG, "getStoredRefresh");
        return getLongSharedPreference(context, REFRESH_LONG_KEY);
    }

    protected static void setStoredRefresh(Context context, Long refresh) {

        Log.v(TAG, "setStoredRefresh");
        setLongSharedPreference(context, REFRESH_LONG_KEY, refresh);
    }
    //----------------------------------------------------------------------------------------------
    // String
    //----------------------------------------------------------------------------------------------

    protected static String getStringSharedPreference(Context context, String key) {

        Log.v(TAG, "getStringSharedPreference");
        String result = null;
        if (context != null && !TextUtils.isEmpty(key)) {
            SharedPreferences preferences = getSharedPreferences(context);
            if (preferences != null && preferences.contains((key))) {
                result = preferences.getString(key, null);
            }
        }
        return result;
    }

    protected static void setStringSharedPreference(Context context, String key, String value) {

        Log.v(TAG, "setStringSharedPreference");
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        if (context != null && !TextUtils.isEmpty(key) && editor != null) {
            if (TextUtils.isEmpty(value)) {
                editor.remove(key);
            } else {
                editor.putString(key, value);
            }
            editor.apply();
        }
    }
    //----------------------------------------------------------------------------------------------
    // Long
    //----------------------------------------------------------------------------------------------

    protected static Long getLongSharedPreference(Context context, String key) {

        Log.v(TAG, "getLongSharedPreference");
        Long result = null;
        SharedPreferences preferences = getSharedPreferences(context);
        if (context != null && preferences.contains(key)) {
            Long value = preferences.getLong(key, 0);
            if (value > 0) {
                result = value;
            }
        }
        return result;
    }

    protected static void setLongSharedPreference(Context context, String key, Long value) {

        Log.v(TAG, "setLongSharedPreference");
        if (context != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = getSharedPreferences(context).edit();
            if (editor != null) {
                if (value == null) {
                    editor.remove(key);
                } else {
                    editor.putLong(key, value);
                }
                editor.apply();
            }
        }
    }
    //----------------------------------------------------------------------------------------------
    // BASE SharedPreferences item
    //----------------------------------------------------------------------------------------------

    protected static SharedPreferences getSharedPreferences(Context context) {

        Log.v(TAG, "getSharedPreferences");
        SharedPreferences result = null;
        if (context != null) {
            result = context.getSharedPreferences(SHARED_PREFERENCES_CONFIG, Context.MODE_PRIVATE);
        }
        return result;
    }
}
