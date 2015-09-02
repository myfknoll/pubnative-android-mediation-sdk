package net.pubnative.mediation.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import net.pubnative.mediation.model.PubnativeAPIResponseModel;
import net.pubnative.mediation.model.PubnativeConfigModel;
import net.pubnative.mediation.task.HttpTask;

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

    protected static final String CONFIG_DOWNLOAD_PARTIAL_URL = "http://ml.pubnative.net/ml/v1/config?app_token=";

    private PubnativeConfigManager()
    {
        // do some initialization here may be.
    }

    public synchronized static void getConfig(Context context, String appToken, PubnativeConfigManagerListener listener)
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

    protected static void serveStoredConfig(Context context, PubnativeConfigManagerListener listener)
    {
        PubnativeConfigModel configModel = PubnativeConfigManager.getStoredConfig(context);
        PubnativeConfigManager.invokeLoaded(listener, configModel);
    }

    protected static PubnativeConfigModel getStoredConfig(Context context)
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

    protected static void invokeLoaded(PubnativeConfigManagerListener listener, PubnativeConfigModel configModel)
    {
        if (listener != null)
        {
            listener.onConfigLoaded(configModel);
        }
    }

    protected static void updateConfigString(Context context, String appToken, String config)
    {
        if (context != null && !TextUtils.isEmpty(appToken) && !TextUtils.isEmpty(config))
        {
            Gson gson = new Gson();
            PubnativeConfigModel configModel = null;
            try
            {
                configModel = gson.fromJson(config, PubnativeConfigModel.class);
            }
            catch (Exception e)
            {
                System.out.println("PubnativeConfigManager.updateConfigString - Error: " + e);
            }

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


    protected static void downloadConfig(final Context context, final PubnativeConfigManagerListener listener, final String appToken)
    {
        if (context != null && !TextUtils.isEmpty(appToken))
        {
            String downloadURLString = CONFIG_DOWNLOAD_PARTIAL_URL + appToken;

            HttpTask http = new HttpTask(context);
            http.setListener(new HttpTask.HttpTaskListener()
            {
                @Override
                public void onHttpTaskFinished(HttpTask task, String result)
                {
                    if (!TextUtils.isEmpty(result))
                    {
                        try
                        {
                            PubnativeAPIResponseModel apiResponse = new Gson().fromJson(result, PubnativeAPIResponseModel.class);
                            if (apiResponse != null)
                            {
                                if (apiResponse.status.equals(PubnativeAPIResponseModel.Status.OK))
                                {
                                    // doing a double check.
                                    if (apiResponse.config != null && !apiResponse.config.isNullOrEmpty())
                                    {
                                        // saving config string.
                                        PubnativeConfigManager.setStoredConfig(context, apiResponse.config);
                                    }
                                }
                                else
                                {
                                    System.out.println("PubnativeConfigManager.downloadConfig - Error:" + apiResponse.error_message);
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            System.out.println("PubnativeConfigManager.downloadConfig - Error:" + e);
                        }
                    }

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
