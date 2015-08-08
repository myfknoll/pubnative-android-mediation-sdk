package net.pubnative.mediation.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import net.pubnative.mediation.model.PubnativeConfigModel;

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

    private PubnativeConfigManager()
    {
    }

    public static synchronized PubnativeConfigModel getConfig(Context context, String appToken)
    {
        PubnativeConfigModel currentConfig = null;

        if (context != null && !TextUtils.isEmpty(appToken))
        {
            // Downloads if needed
            if (PubnativeConfigManager.configNeedsUpdate(context, appToken))
            {
                downloadConfig();
            }

            String configString = PubnativeConfigManager.getConfigString(context);
            if (!TextUtils.isEmpty(configString))
            {
                Gson gson = new Gson();
                try
                {
                    currentConfig = gson.fromJson(configString, PubnativeConfigModel.class);
                }
                catch (Exception e)
                {
                    System.out.println("PubnativeConfigManager.getConfig - Error:" + e);
                }
            }
        }

        // Ensure not returning an invalid getConfig
        if (currentConfig != null && currentConfig.isNullOrEmpty())
        {
            currentConfig = null;
        }

        return currentConfig;
    }

    // TODO: Hide this method with protected
    public static void updateConfigString(Context context, String appToken, String config)
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
                PubnativeConfigManager.setConfigString(context, config);
                PubnativeConfigManager.setAppToken(context, appToken);
                PubnativeConfigManager.setTimestamp(context, System.currentTimeMillis());

                if (configModel.config.containsKey(PubnativeConfigModel.ConfigContract.REFRESH))
                {
                    Double refresh = (double) configModel.config.get(PubnativeConfigModel.ConfigContract.REFRESH);
                    PubnativeConfigManager.setRefresh(context, refresh.longValue());
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


    protected static void downloadConfig()
    {
        // TODO: Download config
    }

    protected static boolean configNeedsUpdate(Context context, String appToken)
    {
        boolean result = false;
        String configString = PubnativeConfigManager.getConfigString(context);
        if (configString == null)
        {
            // Not assigned config
            result = true;
        }
        else
        {
            String storedAppToken = PubnativeConfigManager.getAppToken(context);
            if (TextUtils.isEmpty(storedAppToken) || TextUtils.isEmpty(appToken) || !storedAppToken.equals(appToken))
            {
                //Different or not assigned app token
                result = true;
            }
            else
            {
                Long refresh = PubnativeConfigManager.getRefresh(context);
                Long storedTimestamp = PubnativeConfigManager.getTimestamp(context);

                if (refresh == null || storedTimestamp == null)
                {
                    // Not assigned refresh or timestamp
                    result = true;
                }
                else
                {
                    Long currentTimestamp = System.currentTimeMillis();
                    Long elapsed = TimeUnit.MILLISECONDS.toMinutes(currentTimestamp - storedTimestamp);
                    if (elapsed >= refresh)
                    {
                        // Elapsed refresh time
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    // CONFIG
    protected static String getConfigString(Context context)
    {
        return PubnativeConfigManager.getStringSharedPreference(context, CONFIG_STRING_KEY);
    }

    protected static void setConfigString(Context context, String config)
    {
        PubnativeConfigManager.setStringSharedPreference(context, CONFIG_STRING_KEY, config);
    }

    protected static void clean(Context context)
    {
        PubnativeConfigManager.setAppToken(context, null);
        PubnativeConfigManager.setTimestamp(context, null);
        PubnativeConfigManager.setRefresh(context, null);
        PubnativeConfigManager.setConfigString(context, null);
    }

    // APP TOKEN
    protected static String getAppToken(Context context)
    {
        return PubnativeConfigManager.getStringSharedPreference(context, APP_TOKEN_STRING_KEY);
    }

    protected static void setAppToken(Context context, String appToken)
    {
        PubnativeConfigManager.setStringSharedPreference(context, APP_TOKEN_STRING_KEY, appToken);
    }

    // TIMESTAMP
    protected static Long getTimestamp(Context context)
    {
        return PubnativeConfigManager.getLongSharedPreference(context, TIMESTAMP_LONG_KEY);
    }

    protected static void setTimestamp(Context context, Long timestamp)
    {
        PubnativeConfigManager.setLongSharedPreference(context, TIMESTAMP_LONG_KEY, timestamp);
    }

    // REFRESH
    protected static Long getRefresh(Context context)
    {
        return PubnativeConfigManager.getLongSharedPreference(context, REFRESH_LONG_KEY);
    }

    protected static void setRefresh(Context context, Long refresh)
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
            if(preferences != null && preferences.contains((key)))
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
