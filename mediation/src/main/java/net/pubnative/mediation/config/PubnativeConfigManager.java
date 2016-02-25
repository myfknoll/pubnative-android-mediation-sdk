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
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import net.pubnative.mediation.config.model.PubnativeConfigAPIResponseModel;
import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.config.model.PubnativeConfigRequestModel;
import net.pubnative.mediation.config.model.PubnativePlacementModel;
import net.pubnative.mediation.insights.model.PubnativeInsightsAPIResponseModel;
import net.pubnative.mediation.task.PubnativeHttpTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PubnativeConfigManager {

    private static String TAG = PubnativeConfigManager.class.getSimpleName();

    protected static final String SHARED_PREFERENCES_CONFIG = "net.pubnative.mediation";
    protected static final String CONFIG_STRING_KEY         = "config";
    protected static final String APP_TOKEN_STRING_KEY      = "appToken";
    protected static final String TIMESTAMP_LONG_KEY        = "config.timestamp";
    protected static final String REFRESH_LONG_KEY          = "refresh";

    protected static final String CONFIG_DOWNLOAD_BASE_URL = "http://ml.pubnative.net/ml/v1/config";
    protected static final String APP_TOKEN_KEY            = "?app_token=";

    protected static List<PubnativeConfigRequestModel> sQueue = null;
    protected static boolean                           sIdle  = true;

    private PubnativeConfigManager() {
        // do some initialization here may be.
    }

    /**
     * Gets a config asynchronously with listener callback, downloading a new one when outdated
     *
     * @param context  valid context object
     * @param appToken unique identification key provided by Pubnative for mediation sdk
     * @param listener listener to be used for tracking the config loaded callback
     */
    public synchronized static void getConfig(Context context, String appToken, PubnativeConfigRequestListener listener) {

        Log.v(TAG, "getConfig(Context context, String appToken = " + appToken + ", PubnativeConfigRequestListener listener)");

        if (context != null && !TextUtils.isEmpty(appToken)) {
            if (listener != null) {
                PubnativeConfigRequestModel item = new PubnativeConfigRequestModel();
                item.context = context;
                item.appToken = appToken;
                item.listener = listener;
                PubnativeConfigManager.enqueueRequest(item);
                PubnativeConfigManager.doNextConfigRequest();
            }
        } else {
            // ensuring null config is returned
            PubnativeConfigManager.invokeLoaded(listener, null);
        }
    }

    private static void doNextConfigRequest() {

        Log.v(TAG, "doNextConfigRequest()");

        if (PubnativeConfigManager.sIdle) {
            PubnativeConfigRequestModel item = PubnativeConfigManager.dequeueRequest();
            if (item != null) {
                PubnativeConfigManager.sIdle = false;
                PubnativeConfigManager.getNextConfig(item.context, item.appToken, item.listener);
            }
        }
    }

    protected static void enqueueRequest(PubnativeConfigRequestModel item) {

        Log.v(TAG, "enqueueRequest(PubnativeConfigRequestModel item)");

        if (item != null) {
            if (PubnativeConfigManager.sQueue == null) {
                PubnativeConfigManager.sQueue = new ArrayList<PubnativeConfigRequestModel>();
            }

            PubnativeConfigManager.sQueue.add(item);
        }
    }

    protected static PubnativeConfigRequestModel dequeueRequest() {

        Log.v(TAG, "dequeueRequest()");

        PubnativeConfigRequestModel result = null;
        if (PubnativeConfigManager.sQueue != null && PubnativeConfigManager.sQueue.size() > 0) {
            result = PubnativeConfigManager.sQueue.remove(0);
        }
        return result;
    }

    private static void getNextConfig(Context context, String appToken, PubnativeConfigRequestListener listener) {

        Log.v(TAG, "getNextConfig(Context context, String appToken = " + appToken + ", PubnativeConfigRequestListener listener)");

        if (context != null && !TextUtils.isEmpty(appToken)) {
            // Downloads if needed
            if (PubnativeConfigManager.configNeedsUpdate(context, appToken)) {
                PubnativeConfigManager.downloadConfig(context, listener, appToken);
            } else {
                PubnativeConfigManager.serveStoredConfig(context, listener);
            }
        } else {
            // ensuring null config is returned
            PubnativeConfigManager.invokeLoaded(listener, null);
        }
    }

    protected static void serveStoredConfig(Context context, PubnativeConfigRequestListener listener) {
        PubnativeConfigModel configModel = PubnativeConfigManager.getStoredConfig(context);
        PubnativeConfigManager.invokeLoaded(listener, configModel);
    }

    /**
     * Returns the stored config object if exists in SharedPreferences. Else returns null.
     *
     * @param context context object used to get SharedPreferences instance
     * @return PubnativeConfigModel object if found in SharedPreferences. Else null.
     */
    public static PubnativeConfigModel getStoredConfig(Context context) {
        PubnativeConfigModel currentConfig = null;
        String               configString  = PubnativeConfigManager.getStoredConfigString(context);
        if (!TextUtils.isEmpty(configString)) {
            try {
                currentConfig = new Gson().fromJson(configString, PubnativeConfigModel.class);
            } catch (Exception e) {
                Log.e(TAG, "getStoredConfig - Error: " + e);
            }
        }

        // Ensure not returning an invalid getConfig
        if (currentConfig != null && currentConfig.isNullOrEmpty()) {
            currentConfig = null;
        }

        return currentConfig;
    }

    protected static void invokeLoaded(PubnativeConfigRequestListener listener, PubnativeConfigModel configModel) {
        if (listener != null) {
            listener.onConfigLoaded(configModel);
        }
        PubnativeConfigManager.sIdle = true;
        PubnativeConfigManager.doNextConfigRequest();
    }

    protected static void updateConfig(Context context, String appToken, PubnativeConfigModel configModel) {

        Log.v(TAG, "updateConfig(Context context, String appToken = " + appToken + ", PubnativeConfigModel configModel)");

        if (context != null && !TextUtils.isEmpty(appToken)) {
            if (configModel != null && !configModel.isNullOrEmpty()) {
                PubnativeConfigManager.setStoredConfig(context, configModel);
                PubnativeConfigManager.setStoredAppToken(context, appToken);
                PubnativeConfigManager.setStoredTimestamp(context, System.currentTimeMillis());

                if (configModel.globals.containsKey(PubnativeConfigModel.ConfigContract.REFRESH)) {
                    Double refresh = (Double) configModel.globals.get(PubnativeConfigModel.ConfigContract.REFRESH);
                    PubnativeConfigManager.setStoredRefresh(context, refresh.longValue());
                }
            } else {
                PubnativeConfigManager.clean(context);
            }
        } else {
            PubnativeConfigManager.clean(context);
        }
    }

    protected synchronized static void downloadConfig(final Context context, final PubnativeConfigRequestListener listener, final String appToken) {

        Log.v(TAG, "downloadConfig(final Context context, final PubnativeConfigRequestListener listener, final String appToken = " + appToken + ")");

        if (context != null && !TextUtils.isEmpty(appToken)) {
            String downloadURLString = PubnativeConfigManager.getConfigDownloadBaseUrl(context) + APP_TOKEN_KEY + appToken;

            PubnativeHttpTask http = new PubnativeHttpTask(context);
            http.setListener(new PubnativeHttpTask.Listener() {
                @Override
                public void onHttpTaskFinished(PubnativeHttpTask task, String result) {
                    PubnativeConfigManager.processConfigDownloadResponse(context, appToken, result);
                    PubnativeConfigManager.serveStoredConfig(context, listener);
                }

                @Override
                public void onHttpTaskFailed(PubnativeHttpTask task, String errorMessage) {
                    PubnativeConfigManager.serveStoredConfig(context, listener);
                }
            });
            http.execute(downloadURLString);
        } else {
            PubnativeConfigManager.serveStoredConfig(context, listener);
        }
    }

    protected static void processConfigDownloadResponse(Context context, String appToken, String result) {

        Log.v(TAG, "processConfigDownloadResponse(Context context, String appToken = " + appToken + ", String result = " + result + ")");

        if (!TextUtils.isEmpty(result)) {
            try {
                PubnativeConfigAPIResponseModel response = new Gson().fromJson(result, PubnativeConfigAPIResponseModel.class);

                if (PubnativeInsightsAPIResponseModel.Status.OK.equals(response.status)) {
                    if (response.config != null && !response.config.isNullOrEmpty()) {
                        // Update delivery manager's tracking data
                        PubnativeConfigManager.updateDeliveryManagerCache(context, response.config);
                        // Saving config string
                        PubnativeConfigManager.updateConfig(context, appToken, response.config);
                    }
                } else {
                    Log.e(TAG, "downloadConfig - Error: " + response.error_message);
                }
            } catch (Exception e) {
                Log.e(TAG, "downloadConfig - Error: " + e);
            }
        }
    }

    protected static boolean configNeedsUpdate(Context context, String appToken) {

        Log.v(TAG, "configNeedsUpdate(Context context, String appToken = " + appToken + ")");

        boolean result = false;

        if (context != null) {
            String storedConfigString = PubnativeConfigManager.getStoredConfigString(context);
            if (TextUtils.isEmpty(storedConfigString)) {
                // No stored config found - need update
                result = true;
            } else {
                String storedAppToken = PubnativeConfigManager.getStoredAppToken(context);
                if (TextUtils.isEmpty(storedAppToken) || TextUtils.isEmpty(appToken) || !storedAppToken.equals(appToken)) {
                    // different (stored token != new token) OR invalid (stored/new token is empty/null) app_tokens found - Need update
                    result = true;
                } else {
                    Long refresh = PubnativeConfigManager.getStoredRefresh(context);
                    Long storedTimestamp = PubnativeConfigManager.getStoredTimestamp(context);

                    if (refresh == null || storedTimestamp == null) {
                        // Invalid/unset refresh or timestamp value - Need update
                        result = true;
                    } else {
                        Long currentTimestamp = System.currentTimeMillis();
                        Long elapsed = TimeUnit.MILLISECONDS.toMinutes(currentTimestamp - storedTimestamp);
                        if (elapsed >= refresh) {
                            // Elapsed refresh time - Need update
                            result = true;
                        }
                    }
                }
            }
        }

        return result;
    }

    private static void updateDeliveryManagerCache(Context context, PubnativeConfigModel downloadedConfig) {

        Log.v(TAG, "updateDeliveryManagerCache(Context context, PubnativeConfigModel downloadedConfig)");

        if (downloadedConfig != null) {
            PubnativeConfigModel storedConfig = PubnativeConfigManager.getStoredConfig(context);

            if (storedConfig != null) {
                Set<String> storePlacementIds = storedConfig.placements.keySet();

                for (String placementId : storePlacementIds) {
                    // check if new config contains that placement.
                    PubnativePlacementModel newPlacement = downloadedConfig.placements.get(placementId);

                    if (newPlacement != null) {
                        // compare the delivery rule of the new placement with the stored one.
                        PubnativePlacementModel storedPlacement = storedConfig.placements.get(placementId);

                        if (storedPlacement != null) {
                            if (storedPlacement.delivery_rule != null && newPlacement.delivery_rule != null) {
                                // check if impression cap (hour) changed
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
            }
        }
    }

    // CONFIG URL
    protected static String getConfigDownloadBaseUrl(Context context) {
        String               configDownloadBaseUrl = CONFIG_DOWNLOAD_BASE_URL;
        PubnativeConfigModel storedConfig          = PubnativeConfigManager.getStoredConfig(context);
        if (storedConfig != null && !storedConfig.isNullOrEmpty()) {
            String configUrl = (String) storedConfig.globals.get(PubnativeConfigModel.ConfigContract.CONFIG_URL);
            if (!TextUtils.isEmpty(configUrl)) {
                configDownloadBaseUrl = configUrl;
            }
        }
        return configDownloadBaseUrl;
    }

    // CONFIG
    protected synchronized static String getStoredConfigString(Context context) {
        return PubnativeConfigManager.getStringSharedPreference(context, CONFIG_STRING_KEY);
    }

    protected synchronized static void setStoredConfig(Context context, PubnativeConfigModel config) {
        // ensuring the string "null" is not getting saved.
        String configString = (config != null) ? new Gson().toJson(config) : null;
        PubnativeConfigManager.setStringSharedPreference(context, CONFIG_STRING_KEY, configString);
    }

    public static void clean(Context context) {
        PubnativeConfigManager.setStoredAppToken(context, null);
        PubnativeConfigManager.setStoredTimestamp(context, null);
        PubnativeConfigManager.setStoredRefresh(context, null);
        PubnativeConfigManager.setStoredConfig(context, null);
    }

    // APP TOKEN
    protected static String getStoredAppToken(Context context) {
        return PubnativeConfigManager.getStringSharedPreference(context, APP_TOKEN_STRING_KEY);
    }

    protected static void setStoredAppToken(Context context, String appToken) {
        PubnativeConfigManager.setStringSharedPreference(context, APP_TOKEN_STRING_KEY, appToken);
    }

    // TIMESTAMP
    protected static Long getStoredTimestamp(Context context) {
        return PubnativeConfigManager.getLongSharedPreference(context, TIMESTAMP_LONG_KEY);
    }

    protected static void setStoredTimestamp(Context context, Long timestamp) {
        PubnativeConfigManager.setLongSharedPreference(context, TIMESTAMP_LONG_KEY, timestamp);
    }

    // REFRESH
    protected static Long getStoredRefresh(Context context) {
        return PubnativeConfigManager.getLongSharedPreference(context, REFRESH_LONG_KEY);
    }

    protected static void setStoredRefresh(Context context, Long refresh) {
        PubnativeConfigManager.setLongSharedPreference(context, REFRESH_LONG_KEY, refresh);
    }

    // STRING
    protected static String getStringSharedPreference(Context context, String key) {
        String result = null;
        if (context != null && !TextUtils.isEmpty(key)) {
            SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
            if (preferences != null && preferences.contains((key))) {
                result = preferences.getString(key, null);
            }
        }
        return result;
    }

    protected static void setStringSharedPreference(Context context, String key, String value) {
        if (context != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = PubnativeConfigManager.getSharedPreferencesEditor(context);
            if (editor != null) {
                if (TextUtils.isEmpty(value)) {
                    editor.remove(key);
                } else {
                    editor.putString(key, value);
                }
                editor.apply();
            }
        }
    }

    // LONG
    protected static Long getLongSharedPreference(Context context, String key) {
        Long              result      = null;
        SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
        if (context != null && preferences.contains(key)) {
            Long value = preferences.getLong(key, 0);
            if (value > 0) {
                result = value;
            }
        }
        return result;
    }

    protected static void setLongSharedPreference(Context context, String key, Long value) {
        if (context != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = PubnativeConfigManager.getSharedPreferencesEditor(context);
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

    // SHARED PREFERENCES
    protected static SharedPreferences getSharedPreferences(Context context) {
        SharedPreferences result = null;
        if (context != null) {
            result = context.getSharedPreferences(SHARED_PREFERENCES_CONFIG, Context.MODE_PRIVATE);
        }
        return result;
    }

    protected static SharedPreferences.Editor getSharedPreferencesEditor(Context context) {
        SharedPreferences.Editor result = null;
        if (context != null) {
            SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
            if (preferences != null) {
                result = preferences.edit();
            }
        }
        return result;
    }
}
