package net.pubnative.mediation.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import net.pubnative.mediation.config.model.PubnativeConfigAPIResponseModel;
import net.pubnative.mediation.config.model.PubnativeConfigRequestModel;
import net.pubnative.mediation.config.model.PubnativeConfigModel;
import net.pubnative.mediation.config.model.PubnativePlacementModel;
import net.pubnative.mediation.insights.model.PubnativeInsightsAPIResponseModel;
import net.pubnative.mediation.task.PubnativeHttpTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by davidmartin on 28/07/15.
 */
public class PubnativeConfigManager
{
    protected static final String SHARED_PREFERENCES_CONFIG = "net.pubnative.mediation";
    protected static final String CONFIG_STRING_KEY         = "config";
    protected static final String APP_TOKEN_STRING_KEY      = "appToken";
    protected static final String TIMESTAMP_LONG_KEY        = "config.timestamp";
    protected static final String REFRESH_LONG_KEY          = "refresh";

    protected static final String CONFIG_DOWNLOAD_BASE_URL  = "http://ml.pubnative.net/ml/v1/config";
    protected static final String APP_TOKEN_KEY             = "?app_token=";

    protected static List<PubnativeConfigRequestModel> queue = null;
    protected static boolean                           idle  = true;

    private PubnativeConfigManager()
    {
        // do some initialization here may be.
    }

    /**
     * Gets a config asynchronously with listener callback, downloading a new one when outdated
     *
     * @param context  valid context object
     * @param appToken unique identification key provided by Pubnative for mediation sdk
     * @param listener listener to be used for tracking the config loaded callback
     */
    public synchronized static void getConfig(Context context, String appToken, PubnativeConfigRequestListener listener)
    {
        if (context != null && !TextUtils.isEmpty(appToken))
        {
            if (listener != null)
            {
                PubnativeConfigRequestModel item = new PubnativeConfigRequestModel();
                item.context = context;
                item.appToken = appToken;
                item.listener = listener;
                PubnativeConfigManager.enqueueRequest(item);
                PubnativeConfigManager.doNextConfigRequest();
            }
        }
        else
        {
            // ensuring null config is returned
            PubnativeConfigManager.invokeLoaded(listener, null);
        }
    }

    private static void doNextConfigRequest()
    {
        if (PubnativeConfigManager.idle)
        {
            PubnativeConfigRequestModel item = PubnativeConfigManager.dequeueRequest();
            if (item != null)
            {
                PubnativeConfigManager.idle = false;
                PubnativeConfigManager.getNextConfig(item.context, item.appToken, item.listener);
            }
        }
    }

    protected static void enqueueRequest(PubnativeConfigRequestModel item)
    {
        if (item != null)
        {
            if (PubnativeConfigManager.queue == null)
            {
                PubnativeConfigManager.queue = new ArrayList<PubnativeConfigRequestModel>();
            }

            PubnativeConfigManager.queue.add(item);
        }
    }

    protected static PubnativeConfigRequestModel dequeueRequest()
    {
        PubnativeConfigRequestModel result = null;
        if (PubnativeConfigManager.queue != null && PubnativeConfigManager.queue.size() > 0)
        {
            result = PubnativeConfigManager.queue.remove(0);
        }
        return result;
    }

    private static void getNextConfig(Context context, String appToken, PubnativeConfigRequestListener listener)
    {
        if (context != null && !TextUtils.isEmpty(appToken))
        {
            // Downloads if needed
            if (PubnativeConfigManager.configNeedsUpdate(context, appToken))
            {
                PubnativeConfigManager.downloadConfig(context, listener, appToken);
            }
            else
            {
                PubnativeConfigManager.serveStoredConfig(context, listener);
            }
        }
        else
        {
            // ensuring null config is returned
            PubnativeConfigManager.invokeLoaded(listener, null);
        }
    }

    protected static void serveStoredConfig(Context context, PubnativeConfigRequestListener listener)
    {
        PubnativeConfigModel configModel = PubnativeConfigManager.getStoredConfig(context);
        PubnativeConfigManager.invokeLoaded(listener, configModel);
    }

    /**
     * Returns the stored config object if exists in SharedPreferences. Else returns null.
     *
     * @param context context object used to get SharedPreferences instance
     * @return PubnativeConfigModel object if found in SharedPreferences. Else null.
     */
    public static PubnativeConfigModel getStoredConfig(Context context)
    {
        PubnativeConfigModel currentConfig = null;
        String configString = PubnativeConfigManager.getStoredConfigString(context);
        if (!TextUtils.isEmpty(configString))
        {
            try
            {
                currentConfig = new Gson().fromJson(configString, PubnativeConfigModel.class);
            }
            catch (Exception e)
            {
                System.out.println("PubnativeConfigManager.getConfig - Error:" + e);
            }
        }

        // Ensure not returning an invalid getConfig
        if (currentConfig != null && currentConfig.isNullOrEmpty())
        {
            currentConfig = null;
        }

        return currentConfig;
    }

    protected static void invokeLoaded(PubnativeConfigRequestListener listener, PubnativeConfigModel configModel)
    {
        if (listener != null)
        {
            listener.onConfigLoaded(configModel);
        }
        PubnativeConfigManager.idle = true;
        PubnativeConfigManager.doNextConfigRequest();
    }

    protected static void updateConfig(Context context, String appToken, PubnativeConfigModel configModel)
    {
        if (context != null && !TextUtils.isEmpty(appToken))
        {
            if (configModel != null && !configModel.isNullOrEmpty())
            {
                PubnativeConfigManager.setStoredConfig(context, configModel);
                PubnativeConfigManager.setStoredAppToken(context, appToken);
                PubnativeConfigManager.setStoredTimestamp(context, System.currentTimeMillis());

                if (configModel.globals.containsKey(PubnativeConfigModel.ConfigContract.REFRESH))
                {
                    Double refresh = (Double) configModel.globals.get(PubnativeConfigModel.ConfigContract.REFRESH);
                    PubnativeConfigManager.setStoredRefresh(context, refresh.longValue());
                }
            }
            else
            {
                PubnativeConfigManager.clean(context);
            }
        }
        else
        {
            PubnativeConfigManager.clean(context);
        }
    }

    protected synchronized static void downloadConfig(final Context context, final PubnativeConfigRequestListener listener, final String appToken)
    {
        if (context != null && !TextUtils.isEmpty(appToken))
        {
            String downloadURLString = PubnativeConfigManager.getConfigDownloadBaseUrl(context) + APP_TOKEN_KEY + appToken;

            PubnativeHttpTask http = new PubnativeHttpTask(context);
            http.setListener(new PubnativeHttpTask.Listener()
            {
                @Override
                public void onHttpTaskFinished(PubnativeHttpTask task, String result)
                {
                    PubnativeConfigManager.processConfigDownloadResponse(context, appToken, result);
                    PubnativeConfigManager.serveStoredConfig(context, listener);
                }

                @Override
                public void onHttpTaskFailed(PubnativeHttpTask task, String errorMessage)
                {
                    PubnativeConfigManager.serveStoredConfig(context, listener);
                }
            });
            http.execute(downloadURLString);
        }
        else
        {
            PubnativeConfigManager.serveStoredConfig(context, listener);
        }
    }

    protected static void processConfigDownloadResponse(Context context, String appToken, String result)
    {
        if (!TextUtils.isEmpty(result))
        {
            try
            {
                PubnativeConfigAPIResponseModel response = new Gson().fromJson(result, PubnativeConfigAPIResponseModel.class);

                if (PubnativeInsightsAPIResponseModel.Status.OK.equals(response.status))
                {
                    if (response.config != null && !response.config.isNullOrEmpty())
                    {
                        // Update delivery manager's tracking data
                        PubnativeConfigManager.updateDeliveryManagerCache(context, response.config);
                        // Saving config string
                        PubnativeConfigManager.updateConfig(context, appToken, response.config);
                    }
                }
                else
                {
                    System.out.println("PubnativeConfigManager.downloadConfig - Error:" + response.error_message);
                }
            }
            catch (Exception e)
            {
                System.out.println("PubnativeConfigManager.downloadConfig - Error:" + e);
            }
        }
    }

    protected static boolean configNeedsUpdate(Context context, String appToken)
    {
        boolean result = false;

        if (context != null)
        {
            String storedConfigString = PubnativeConfigManager.getStoredConfigString(context);
            if (TextUtils.isEmpty(storedConfigString))
            {
                // No stored config found - need update
                result = true;
            }
            else
            {
                String storedAppToken = PubnativeConfigManager.getStoredAppToken(context);
                if (TextUtils.isEmpty(storedAppToken) || TextUtils.isEmpty(appToken) || !storedAppToken.equals(appToken))
                {
                    // different (stored token != new token) OR invalid (stored/new token is empty/null) app_tokens found - Need update
                    result = true;
                }
                else
                {
                    Long refresh = PubnativeConfigManager.getStoredRefresh(context);
                    Long storedTimestamp = PubnativeConfigManager.getStoredTimestamp(context);

                    if (refresh == null || storedTimestamp == null)
                    {
                        // Invalid/unset refresh or timestamp value - Need update
                        result = true;
                    }
                    else
                    {
                        Long currentTimestamp = System.currentTimeMillis();
                        Long elapsed = TimeUnit.MILLISECONDS.toMinutes(currentTimestamp - storedTimestamp);
                        if (elapsed >= refresh)
                        {
                            // Elapsed refresh time - Need update
                            result = true;
                        }
                    }
                }
            }
        }

        return result;
    }

    private static void updateDeliveryManagerCache(Context context, PubnativeConfigModel downloadedConfig)
    {
        if (downloadedConfig != null)
        {
            PubnativeConfigModel storedConfig = PubnativeConfigManager.getStoredConfig(context);
            if (storedConfig != null)
            {
                Set<String> storePlacementIds = storedConfig.placements.keySet();
                for (String placementId : storePlacementIds)
                {
                    // check if new config contains that placement.
                    PubnativePlacementModel newPlacement = downloadedConfig.placements.get(placementId);
                    if (newPlacement != null)
                    {
                        // compare the delivery rule of the new placement with the stored one.
                        PubnativePlacementModel storedPlacement = storedConfig.placements.get(placementId);
                        if (storedPlacement != null)
                        {
                            if (storedPlacement.delivery_rule != null && newPlacement.delivery_rule != null)
                            {
                                // check if impression cap (hour) changed
                                if (storedPlacement.delivery_rule.imp_cap_hour != newPlacement.delivery_rule.imp_cap_hour)
                                {
                                    PubnativeDeliveryManager.resetHourlyImpressionCount(context, placementId);
                                }

                                // check if impression cap (day) changed
                                if (storedPlacement.delivery_rule.imp_cap_day != newPlacement.delivery_rule.imp_cap_day)
                                {
                                    PubnativeDeliveryManager.resetDailyImpressionCount(context, placementId);
                                }

                                // check if pacing cap changed
                                if (storedPlacement.delivery_rule.pacing_cap_minute != newPlacement.delivery_rule.pacing_cap_minute
                                        || storedPlacement.delivery_rule.pacing_cap_hour != newPlacement.delivery_rule.pacing_cap_hour)
                                {
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
    protected static String getConfigDownloadBaseUrl(Context context)
    {
        String configDownloadBaseUrl = CONFIG_DOWNLOAD_BASE_URL;
        PubnativeConfigModel storedConfig = PubnativeConfigManager.getStoredConfig(context);
        if (storedConfig != null && !storedConfig.isNullOrEmpty())
        {
            String configUrl = (String) storedConfig.globals.get(PubnativeConfigModel.ConfigContract.CONFIG_URL);
            if (!TextUtils.isEmpty(configUrl))
            {
                configDownloadBaseUrl = configUrl;
            }
        }
        return configDownloadBaseUrl;
    }

    // CONFIG
    protected synchronized static String getStoredConfigString(Context context)
    {
        return PubnativeConfigManager.getStringSharedPreference(context, CONFIG_STRING_KEY);
    }

    protected synchronized static void setStoredConfig(Context context, PubnativeConfigModel config)
    {
        // ensuring the string "null" is not getting saved.
        String configString = (config != null) ? new Gson().toJson(config) : null;
        PubnativeConfigManager.setStringSharedPreference(context, CONFIG_STRING_KEY, configString);
    }

    protected static void clean(Context context)
    {
        PubnativeConfigManager.setStoredAppToken(context, null);
        PubnativeConfigManager.setStoredTimestamp(context, null);
        PubnativeConfigManager.setStoredRefresh(context, null);
        PubnativeConfigManager.setStoredConfig(context, null);
    }

    // APP TOKEN
    protected static String getStoredAppToken(Context context)
    {
        return PubnativeConfigManager.getStringSharedPreference(context, APP_TOKEN_STRING_KEY);
    }

    protected static void setStoredAppToken(Context context, String appToken)
    {
        PubnativeConfigManager.setStringSharedPreference(context, APP_TOKEN_STRING_KEY, appToken);
    }

    // TIMESTAMP
    protected static Long getStoredTimestamp(Context context)
    {
        return PubnativeConfigManager.getLongSharedPreference(context, TIMESTAMP_LONG_KEY);
    }

    protected static void setStoredTimestamp(Context context, Long timestamp)
    {
        PubnativeConfigManager.setLongSharedPreference(context, TIMESTAMP_LONG_KEY, timestamp);
    }

    // REFRESH
    protected static Long getStoredRefresh(Context context)
    {
        return PubnativeConfigManager.getLongSharedPreference(context, REFRESH_LONG_KEY);
    }

    protected static void setStoredRefresh(Context context, Long refresh)
    {
        PubnativeConfigManager.setLongSharedPreference(context, REFRESH_LONG_KEY, refresh);
    }

    // STRING
    protected static String getStringSharedPreference(Context context, String key)
    {
        String result = null;
        if (context != null && !TextUtils.isEmpty(key))
        {
            SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
            if (preferences != null && preferences.contains((key)))
            {
                result = preferences.getString(key, null);
            }
        }
        return result;
    }

    protected static void setStringSharedPreference(Context context, String key, String value)
    {
        if (context != null && !TextUtils.isEmpty(key))
        {
            SharedPreferences.Editor editor = PubnativeConfigManager.getSharedPreferencesEditor(context);
            if (editor != null)
            {
                if (TextUtils.isEmpty(value))
                {
                    editor.remove(key);
                }
                else
                {
                    editor.putString(key, value);
                }
                editor.apply();
            }
        }
    }

    // LONG
    protected static Long getLongSharedPreference(Context context, String key)
    {
        Long result = null;
        SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
        if (context != null && preferences.contains(key))
        {
            Long value = preferences.getLong(key, 0);
            if (value > 0)
            {
                result = value;
            }
        }
        return result;
    }

    protected static void setLongSharedPreference(Context context, String key, Long value)
    {
        if (context != null && !TextUtils.isEmpty(key))
        {
            SharedPreferences.Editor editor = PubnativeConfigManager.getSharedPreferencesEditor(context);
            if (editor != null)
            {
                if (value == null)
                {
                    editor.remove(key);
                }
                else
                {
                    editor.putLong(key, value);
                }
                editor.apply();
            }
        }
    }

    // SHARED PREFERENCES
    protected static SharedPreferences getSharedPreferences(Context context)
    {
        SharedPreferences result = null;
        if (context != null)
        {
            result = context.getSharedPreferences(SHARED_PREFERENCES_CONFIG, Context.MODE_PRIVATE);
        }
        return result;
    }

    protected static SharedPreferences.Editor getSharedPreferencesEditor(Context context)
    {
        SharedPreferences.Editor result = null;
        if (context != null)
        {
            SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
            if (preferences != null)
            {
                result = preferences.edit();
            }
        }
        return result;
    }
}
