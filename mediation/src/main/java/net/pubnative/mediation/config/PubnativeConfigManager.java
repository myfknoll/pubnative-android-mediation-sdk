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
    protected static final String CONFIG_KEY                = "net.pubnative.mediation.getConfig";
    private static final String APP_TOKEN_KEY               = "net.pubnative.mediation.app_token";
    private static final String TIMESTAMP_KEY               = "net.pubnative.mediation.timestamp";
    private static final String REFRESH_KEY                 = "net.pubnative.mediation.refresh_time";

    private PubnativeConfigManager()
    {
    }

    public static synchronized PubnativeConfigModel getConfig(Context context, String app_token)
    {
        PubnativeConfigModel currentConfig = null;

        // 2. Get stored getConfig
        String storedConfigString = PubnativeConfigManager.getConfigString(context);

        if (storedConfigString != null)
        {
            Gson gson = new Gson();
            try
            {
                currentConfig = gson.fromJson(storedConfigString, PubnativeConfigModel.class);
            }
            catch (Exception e)
            {
                System.out.println("PubnativeConfigManager.getConfig - Error:" + e);
            }
        }

        // TODO: if(currentModel == null || configStoredMinutes > currentModel.conf_refresh)
        // TODO: {
        // TODO:    String newConfigString = [DOWNLOAD A NEW CONFIG];
        // TODO:    PubnativeConfigModel newConfig = gson.fromJson(newConfigString, PubnativeConfigModel.class);
        // TODO:    if(newConfig != null)
        // TODO:    {
        // TODO:        SharedPreferences.Editor editor = prefs.edit();
        // TODO:        editor.putString(gson.toJson(newModel));
        // TODO:        editor.commit();
        // TODO:        currentModel = newModel;
        // TODO:    }
        // TODO: }

        // Ensure not returning an invalid getConfig
        if (currentConfig != null && currentConfig.isNullOrEmpty())
        {
            currentConfig = null;
        }

        return currentConfig;
    }

    protected static SharedPreferences getSharedPreferences(Context context)
    {
        SharedPreferences result = null;
        if(context != null)
        {
            result = context.getSharedPreferences(SHARED_PREFERENCES_CONFIG, Context.MODE_PRIVATE);
        }
        return result;
    }

    protected static void setConfigString(Context context, String config, String app_token)
    {
        if(context != null)
        {
            SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();

            if(TextUtils.isEmpty(config))
            {
                editor.remove(CONFIG_KEY);
            }
            else
            {
                editor.putString(CONFIG_KEY, config);
                try
                {
                    PubnativeConfigModel configModel = new Gson().fromJson(config, PubnativeConfigModel.class);
                    if (configModel != null)
                    {
                        if (configModel.config != null)
                        {
                            if (configModel.config.get("refresh") != null)
                            {
                                editor.putLong(REFRESH_KEY, (long) (double) configModel.config.get("refresh"));
                            }
                        }
                    }
                }
                catch (Exception e)
                {

                }
                editor.putLong(TIMESTAMP_KEY, System.currentTimeMillis());
            }
            if (TextUtils.isEmpty(app_token))
            {
                editor.remove(APP_TOKEN_KEY);
            }
            else
            {
                editor.putString(APP_TOKEN_KEY, app_token);
            }
            editor.apply();
        }
    }

    protected static String getConfigString(Context context)
    {
        String result = null;
        if(context != null)
        {
            SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
            result = preferences.getString(CONFIG_KEY, null);
        }
        return result;
    }

    protected static boolean hasConfig(Context context)
    {
        boolean result = false;
        if(context != null)
        {
            SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
            result = preferences.contains(CONFIG_KEY);
        }
        return result;
    }

    protected static String getAppToken(Context context)
    {
        String token = null;
        if (context != null)
        {
            SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
            token = preferences.getString(APP_TOKEN_KEY, null);
        }
        return token;
    }

    protected static Long getLastStoredTimestamp(Context context)
    {
        Long timeStamp = null;
        if (context != null)
        {
            SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
            timeStamp = preferences.getLong(TIMESTAMP_KEY, 0);

            if (timeStamp == 0)
            {
                return null;
            }
        }
        return timeStamp;
    }

    protected static boolean hasStoredAppToken(Context context, String app_token)
    {
        boolean result = false;
        if (context != null && !TextUtils.isEmpty(app_token))
        {
            SharedPreferences preferences = PubnativeConfigManager.getSharedPreferences(context);
            String storedAppToken = preferences.getString(APP_TOKEN_KEY, "");
            if (!TextUtils.isEmpty(storedAppToken))
            {
                result = storedAppToken.equals(app_token);
            }

        }
        return result;
    }

    protected static boolean hasTimeStampExpired(Context context, Long currentTimeInMillis)
    {
        Long storedTime = getLastStoredTimestamp(context);
        Long refreshPeriod = getRefreshTime(context);
        if (currentTimeInMillis != null && refreshPeriod != null && storedTime != null)
        {
            if (TimeUnit.MILLISECONDS.toMinutes(currentTimeInMillis - storedTime) >= refreshPeriod)
            {
                return true;
            }
        }
        return false;
    }

    protected static Long getRefreshTime(Context context) {
        Long refreshTime = null;
        if (context != null)
        {
            refreshTime = getSharedPreferences(context).getLong(REFRESH_KEY,0);
            if (refreshTime == 0)
            {
                refreshTime = null;
            }
        }
        return refreshTime;
    }
}
