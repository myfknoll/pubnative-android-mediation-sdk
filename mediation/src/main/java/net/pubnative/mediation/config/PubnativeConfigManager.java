package net.pubnative.mediation.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import net.pubnative.mediation.model.PubnativeConfigModel;

/**
 * Created by davidmartin on 28/07/15.
 */
public class PubnativeConfigManager
{
    protected static final String SHARED_PREFERENCES_CONFIG = "net.pubnative.mediation";
    protected static final String CONFIG_KEY                = "net.pubnative.mediation.getConfig";

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

    protected static void setConfigString(Context context, String config)
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
}
